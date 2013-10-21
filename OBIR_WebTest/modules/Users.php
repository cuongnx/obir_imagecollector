<?php

class Users {

	public static function getPassword($username) {
		$password = null;

		$conn = DBHelpers::connect();

		if ($conn) {
			$st = $conn->prepare("SELECT * FROM `obir`.`users` WHERE `username`=?");

			if ($st->execute(array($username))) {
				$row = $st->fetch();
				$password = json_decode($row['password']);
			}

			$conn = null;
		}

		return $password;
	}

}
?>