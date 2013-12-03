<?php 
session_start();
session_destroy();
?>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>OBIR WebTest</title>
</head>
<body>
	<form action="select.php" method="post" >
		<!--<input type="text" width="100" id="username" name="username" />-->
		<select name="username" id="username">
			<?php
			require_once 'modules/dbconfig.php';
			require_once 'modules/DBHelpers.php';
			$dbConn = DBHelpers::connect();
			$st = $dbConn->prepare("SELECT (`username`) FROM `obir`.`users`");

			if ($st->execute()) {
				$users = $st->fetchAll(PDO::FETCH_COLUMN);
				if ($users) {
					foreach ($users as $id => $user) {
						echo "<option value=\"$user\">$user</option>";
					}
				}
			}
			?>
		</select>
		<input type="submit" value="Login" id="login" />
	</form>
</body>
</html>
