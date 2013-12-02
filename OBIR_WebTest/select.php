<?php
include_once "include_files.php";
$ncol = 3;
$nrow = 4;

session_start();
if (isset($_GET['selected'])) {
	$selected = $_GET['selected'];
	$turn = $_SESSION['turn'];

	if ($selected == $_SESSION['pivot']) {
		$_SESSION['corrected'] += 1;
	}

	$corrected = $_SESSION['corrected'];

	if ($turn >= 2) {
		if ($corrected >= 3) {
			header("Location: /result.php?success=1");
		} else {
			header("Location: /result.php?success=0");
		}

		session_destroy();
		exit;
	}

	$_SESSION['turn'] += 1;

}

$username = null;
if (isset($_POST['username'])) {
	$username = $_POST['username'];
	$_SESSION['username'] = $_POST['username'];
	$_SESSION['turn'] = 0;
	$_SESSION['corrected'] = 0;
	$_SESSION['displayed'] = [];
} else {
	$username = $_SESSION['username'];
}

$is = new ImageScore($username, $_SESSION['turn']);

$keyimg = $is->getKeyImages();
if (!$keyimg) {
	session_destroy();
	header("Location: /result.php?success=0");
}
$dummy = $is->getDummyImages($ncol * $nrow);

array_push($_SESSION['displayed'], $keyimg);

$correct_cell = mt_rand(0, 8);
$_SESSION['pivot'] = $correct_cell;

error_log("Displayed: ".var_export($_SESSION['displayed'], true)."\n", 3, "logs/debug.txt");
error_log("Correct cell: ".var_export($correct_cell, true)."  ".$keyimg."\n", 3, "logs/debug.txt");
?>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>OBIR WebTest</title>
<style type="text/css">
img.img_cell {
	width: 250px;
	height: 250px;
}
</style>

</head>
<body>
	<?php if (!$is->isSuccess()) { ?>
		<h2>Username not exists</h2>
	<?php } else { ?>
	<table>
	<?php
	for ($i = 0; $i < $nrow; ++$i) {
		echo "<tr>";
		for ($j = 0; $j < $ncol; ++$j) {
			$cellnum = $i * $ncol + $j;
			$imgsrc = ($correct_cell == $cellnum) ? $keyimg : $dummy[$cellnum];
			echo "<td><a href=\"?selected=$cellnum\" onClick=\"select($cellnum)\"><img class=\"img_cell\" src=\"$imgsrc\" /></a></td>";
		}
		echo "</tr>";
	}
?>
	</table>
	<?php } ?>
	<input type="button" name="reload" value="Reload" onClick="window.location='/select.php'" />
</body>
</html>
