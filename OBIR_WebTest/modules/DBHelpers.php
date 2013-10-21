<?php

class DBHelpers {
	private static $host = DBConfig::HOST;
	private static $dbname = DBConfig::DBNAME;
	private static $user = DBConfig::USER;
	private static $password = DBConfig::PASSWORD;

	public static function connect() {
		$conn = null;

		try {
			$conn = new PDO("mysql:host=".self::$host.";dbname=".self::$dbname, self::$user, self::$password);
		} catch (PDOException $e) {
			error_log(var_export($e->getMessage(), true), 3, "logs/debug.txt");
		}

		return $conn;
	}
}
?>