<?php

class ImageScore {
	private $dbConn = null;

	private $keyImages = array();
	private $dummyImages = array();

	private static $key_size = 5;
	private static $dummy_size = 50;

	private static $maxima = 1;

	private $password = null;
	private $success = false;
	private $turn = null;

	private $password_tmp = array();

	public function __construct($username, $turn) {
		$this->password = Users::getPassword($username);

		if (!$this->password) {
			$this->success = false;
			return;
		} else {
			error_log(var_export($this->password, true)."\n", 3, "logs/debug.txt");
			$this->success = true;

			$len = count($this->password);
			$pass = array($this->password[$turn % $len]);
			$this->turn = $turn;

			$this->get_and_score($pass);
		}
	}

	private function get_and_score($password) {
		error_log("getting keywords...\n ", 3, "logs/debug.txt");
		try {

			$keywords = null;
			$pvector = null;

			// first get the keywords collection
			$this->dbConn = DBHelpers::connect();
			$st = $this->dbConn->prepare("SELECT (`word`) FROM `obir`.`keywords` ORDER BY `word` ASC");

			if ($st->execute()) {
				$keywords = $st->fetchAll(PDO::FETCH_COLUMN);

				if ($keywords) {

					//create password vector
					$pvector = array_fill(0, count($keywords), 0);
					foreach ($password as $p) {
						$pvector[$p - 1] = self::$maxima;
						array_push($this->password_tmp, $keywords[$p - 1]);
						error_log($keywords[$p - 1]." ", 3, "logs/debug.txt");
					}

					//get images and ranking based on password vector
					$this->ranking($pvector);
				}
			}

			$st = null;

		} catch (PDOException $e) {
			error_log(var_export($e->getMessage(), true), 3, "logs/debug.txt");
		}

	}

	private function ranking($q) {
		error_log("begin scoring...\n ", 3, "logs/debug.txt");
		$fullpass = array_fill(0, count($q), 0);
		foreach ($this->password as $p) {
			$fullpass[$p - 1] = self::$maxima;
		}

		try {

			$st = $this->dbConn->prepare("SELECT * FROM `obir`.`images` limit :limit offset :offset");

			$st->bindParam(':offset', $offset, PDO::PARAM_INT);
			$st->bindParam(':limit', $limit, PDO::PARAM_INT);
			$offset = mt_rand(1, 10000);
			$limit = 5000;

			if ($st->execute()) {

				//$rows = $st->fetchAll(PDO::FETCH_ASSOC);
				while ($row = $st->fetch(PDO::FETCH_ASSOC)) {
					//get image feature vector and calculate score

					$fv = json_decode($row['vector']);

					$R = $this->relevance($q, $fv);

					if ($R <= 0) {
						// if R=0 then make it dummy image
						if (self::dist($fullpass, $fv) <= 0) {
							if ($this->countObj($fv) > 2)
								if (count($this->dummyImages) < self::$dummy_size) {
									array_push($this->dummyImages, $row['location']);
								} else {
									$idx = mt_rand(0, self::$dummy_size + 30);
									if ($idx < self::$dummy_size) {
										$this->dummyImages = array_replace($this->dummyImages, array($idx => $row['location']));
									}
								}
						}

					} else {
						//else calculate score
						$N = $this->noise($R, $q, $fv);
						$S = $R * $N;

						//if S!=0 then determine if it should be put into correct image array
						if ($S != 0) {
							if (count($this->keyImages) < self::$key_size) {
								$this->keyImages = array_merge($this->keyImages, array($row['location'] => $S));
								arsort($this->keyImages);
							} else {
								$this->keyImages = self::rearrange($this->keyImages, array($row['location'] => $S));
							}
							//error_log($row['filename']." ".$R."*".$N."=".$S."\n", 3, "logs/debug.txt");
						}

					}

				}

				$offset += $st->rowCount();
				$limit = 1000;

				error_log("Done with ".$st->rowCount()." images\n ", 3, "logs/debug.txt");
				error_log(var_export($this->keyImages, true)."\n", 3, "logs/debug.txt");

			}

			error_log("Done scoring!\n ", 3, "logs/debug.txt");
		} catch (PDOException $e) {
			error_log(var_export($e->getMessage(), true), 3, "logs/debug.txt");
		}

		$st = null;
		$this->dbConn = null;
	}

	// distance between 2 vectors
	private static function dist($u, $v) {
		$d = 0;

		foreach ($u as $k => $val) {
			$d += $val * $v[$k];
		}

		return $d;
	}

	//relevance calculation
	private function relevance($q, $fv) {
		$d = self::dist($q, $fv);
		if ($d <= 0) {
			return 0;
		}

		//$d = pow($d, 1 / 2);
		//return (-M_E * $d * log($d));
		return $d;
	}

	//noise calculation
	private function noise($R, $q, $fv) {
		$count = 0;
		$N = 0;
		$d = self::dist($q, $fv);

		foreach ($fv as $i => $val) {
			if (($val > 0) && (!in_array($i + 1, $this->password))) {

				// create vector for keyword i
				$ki = array_fill(0, count($q), 0);
				$ki[$i] = self::$maxima;

				// calculate N
				//$delta = self::dist($fv, $ki) - $d;
				$delta = ($this->relevance($ki, $fv) - $R);

				$N += $delta;
				++$count;
			}
		}

		if (($N > 0) && ($count > 1)) {
			$N = (1 - $N / $count) * ($count - 1) / $count;
			//$N=$N*($count-1)/$count;
		} else {
			$N = 0;
		}

		return $N;
	}

	//count number of objects in image
	private function countObj($fv) {
		$c = 0;
		foreach ($fv as $idx => $v) {
			if ($v != 0) {
				++$c;
			}
		}
		return $c;
	}

	//rearrange sorted array
	private function rearrange($arr, $el) {
		$new = array_merge($arr, $el);
		arsort($new);
		return array_slice($new, 0, self::$key_size);
	}

	public function getKeyImages() {
		if ($this->turn === null) {
			return null;
		}
		$start = floor($this->turn / count($this->password));
		if ($start >= 1) {
			$this->keyImages = array_slice($this->keyImages, $start, count($this->keyImages));
		}
		return ($this->keyImages) ? key($this->keyImages) : null;
	}
	public function getDummyImages($num = 9) {
		if (!$this->dummyImages) {
			return null;
		}

		$keys = array_rand($this->dummyImages, $num);
		$dummy = array();
		foreach ($keys as $v) {
			array_push($dummy, $this->dummyImages[$v]);
		}
		return $dummy;
	}

	public function isSuccess() {
		return $this->success;
	}

	public function getPassword() {
		return $this->password_tmp;
	}
}
?>