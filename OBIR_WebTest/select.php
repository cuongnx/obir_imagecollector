<?php
include_once "include_files.php";
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
} else {
	$username = $_SESSION['username'];
}

$is = new ImageScore($username, $_SESSION['turn']);

$keyimg = $is->getKeyImages();
$dummy = $is->getDummyImages();
//error_log(var_export($keyimg, true)."\n", 3, "logs/debug.txt");
//error_log(var_export($dummy, true), 3, "logs/debug.txt");

$correct_cell = mt_rand(1, 9);
$_SESSION['pivot'] = $correct_cell;

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
	<tr>
	<td><a href="select.php?selected=1" onClick="select(1)"><img class="img_cell" src="<?php echo ($correct_cell==1)?$keyimg:$dummy[0]; ?>" /></a></td>
	<td><a href="select.php?selected=2" onClick="select(2)"><img class="img_cell" src="<?php echo ($correct_cell==2)?$keyimg:$dummy[1]; ?>" /></a></td>
	<td><a href="select.php?selected=3" onClick="select(3)"><img class="img_cell" src="<?php echo ($correct_cell==3)?$keyimg:$dummy[2]; ?>" /></a></td>
	</tr>
	<tr>
	<td><a href="?selected=4" onClick="select(4)"><img class="img_cell" src="<?php echo ($correct_cell==4)?$keyimg:$dummy[3]; ?>" /></a></td>
	<td><a href="?selected=5" onClick="select(5)"><img class="img_cell" src="<?php echo ($correct_cell==5)?$keyimg:$dummy[4]; ?>" /></a></td>
	<td><a href="?selected=6" onClick="select(6)"><img class="img_cell" src="<?php echo ($correct_cell==6)?$keyimg:$dummy[5]; ?>" /></a></td>
	</tr>
	<tr>
	<td><a href="?selected=7" onClick="select(7)"><img class="img_cell" src="<?php echo ($correct_cell==7)?$keyimg:$dummy[6]; ?>" /></a></td>
	<td><a href="?selected=8" onClick="select(8)"><img class="img_cell" src="<?php echo ($correct_cell==8)?$keyimg:$dummy[7]; ?>" /></a></td>
	<td><a href="?selected=9" onClick="select(9)"><img class="img_cell" src="<?php echo ($correct_cell==9)?$keyimg:$dummy[8]; ?>" /></a></td>
	</tr>
	</table>
	<?php } ?>
</body>
</html>
