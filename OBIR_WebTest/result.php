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

	printf("%d ms   (%d:%d:%.3f)", $diff*1000, $h, $m, $s);
} else {
	echo "unknown";
}
session_destroy();
$_SESSION['signing_in'] = false;
?>
</b>
<br/><br/>

Response time :

<b id="response_time">
</b>
<br/><br/>

Average per image request:

<b id="average_time">
</b>
<br/><br/>

<script type="text/javascript">
ms = parseInt(sessionStorage.getItem("total_time"));
s = ms / 1000;
h = Math.floor(s / 3600);
s -= h * 3600;
m = Math.floor(s / 60);
s -= m * 60;

count = parseInt(sessionStorage.getItem("total_count")) - 1;
avg = ms / count;

document.getElementById("response_time").innerHTML = ms + " ms   (" + h + ":" + m + ":" + s + ")";
document.getElementById("average_time").innerHTML = avg.toFixed(2) + " ms";
</script>

<input type="button" onClick="window.location='/index.php'" value="Retry" />
</body>
</html>