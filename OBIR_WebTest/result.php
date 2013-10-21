<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>OBIR WebTest</title>
</head>
<body>
<?php
$result = $_GET["success"];
?>
<h2><?php echo($result) ? "Authenticated!" : "Authentication failed!" ?></h2>
</body>
</html>