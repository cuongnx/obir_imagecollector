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
</br>

Login time :

<b> 
<?php
session_start();

if (isset($_SESSION['start'])) {
	$diff = microtime(true) - $_SESSION['start'];
	$s = round($diff, 3);
	$h = floor($s / 3600);
	$s = $s - $h * 3600;
	$m = floor($s / 60);
	$s = $s - $m * 60;

	printf("%02d:%02d:%02.3f", $h, $m, $s);
} else {
	echo "unknown";
}
session_destroy();
?>
</b>
<br/><br/>

Response time :

<b id="response_time">
</b>
<br/><br/>

<script type="text/javascript">
ms = parseInt(sessionStorage.getItem("total_time"));
s = ms / 1000;
h = Math.floor(s / 3600);
s -= h * 3600;
m = Math.floor(s / 60);
s -= m * 60;

document.getElementById("response_time").innerHTML = h + " : " + m + " : " + s;
</script>

<input type="button" onClick="window.location='/index.php'" value="Retry" />
</body>
</html>