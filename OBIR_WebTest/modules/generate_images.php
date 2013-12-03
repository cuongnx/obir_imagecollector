<?php
include_once "include_files.php";
$ncol = 3;
$nrow = 4;

session_start();

$username = null;
if (isset($_POST['username'])) {
	$username = $_POST['username'];
	$_SESSION['username'] = $_POST['username'];
	$_SESSION['turn'] = -1;
	$_SESSION['corrected'] = 0;
	$_SESSION['displayed'] = [];
	$_SESSION['start'] = microtime(true);
} else {
	$username = $_SESSION['username'];
}

if (isset($_GET['selected'])) {
	$selected = $_GET['selected'];
	$turn = $_SESSION['turn'];

	if ($selected == $_SESSION['pivot']) {
		$_SESSION['corrected'] += 1;
	}

	$corrected = $_SESSION['corrected'];

	if ($turn >= 2) {
		if ($corrected >= 3) {
			echo json_encode(array("success" => 1));
		} else {
			echo json_encode(array("success" => 0));
		}

		exit;
	}

	$_SESSION['turn'] += 1;
}

$is = new ImageScore($username, $_SESSION['turn']);
if ($_SESSION['turn'] == - 1) {
	$_SESSION['turn'] = 0;
}

$keyimg = $is->getKeyImages();
if (!$keyimg) {
	header("Location: /result.php?success=0");
}
$dummy = $is->getDummyImages($ncol * $nrow);

array_push($_SESSION['displayed'], $keyimg);
foreach ($dummy as $k => $v) {
	array_push($_SESSION['displayed'], $v);
}

$correct_cell = mt_rand(0, 8);
$_SESSION['pivot'] = $correct_cell;

//error_log("Displayed: ".var_export($_SESSION['displayed'], true)."\n", 3, "../logs/debug.txt");
error_log("Correct cell: ".var_export($correct_cell, true)."  ".$keyimg."\n", 3, "../logs/debug.txt");
?>

<?php if ($is->isSuccess()) { ?>
<input type="submit" name="reload" value="Reload" onclick="mSelect(-2)" />
<table>
<?php
for ($i = 0; $i < $nrow; ++$i) {
	echo "<tr>";
	for ($j = 0; $j < $ncol; ++$j) {
		$cellnum = $i * $ncol + $j;
		$imgsrc = ($correct_cell == $cellnum) ? $keyimg : $dummy[$cellnum];
		echo "<td><a href=\"javascript:void(0)\" onclick=\"mSelect($cellnum)\"><img class=\"img_cell\" src=\"$imgsrc\" /></a></td>";
	}
	echo "</tr>";
}
?>
</table>
<?php } else {
echo "<h2>User not found!</h2>";
} ?>