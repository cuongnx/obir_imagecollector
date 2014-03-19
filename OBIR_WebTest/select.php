<?php
require_once 'modules/Mobile_Detect.php';

$detect = new Mobile_Detect();
$isMobile = $detect->isMobile();

session_start();
if (isset($_POST['username'])) {
	$_SESSION['siging_in'] = true;
} else {
	if (!isset($_SESSION['siging_in']) && $_SESSION['siging_in'] == false) {
		header("Location: /index.php");
	}
}
?>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=yes">
<title>OBIR WebTest</title>

<style type="text/css">
table {
	width: 100%;
}

td {
	width: 30%;
}

.img_container {
	width: 100%;
	position: relative;
	overflow: auto;
}

.img_container:before {
	content: "";
	display: block;
	padding-top: 100%;
}

img {
	position: absolute;
	top: 0;
	bottom: 0;
	left: 0;
	right: 0;
	width: 100%;
	height: 100%;
}


</style>

<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js">
</script>

<script type="text/javascript">

function mSelect(cellnum) {
	var xmlhttp;
	if (window.XMLHttpRequest) {
	  xmlhttp=new XMLHttpRequest();
	} else {
		xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}

	sessionStorage.setItem("start", (new Date()).getTime().toString());
	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState==4 && xmlhttp.status==200) {

			try {
				res = JSON.parse(xmlhttp.responseText);
				window.location = "/result.php?success="+res.success;
			} catch(e) {
			}
			
			image_table = document.getElementById("image_table");
			image_table.innerHTML = xmlhttp.response;

			end = (new Date()).getTime();
			diff = end - parseInt(sessionStorage.getItem("start"));
			diff += parseInt(sessionStorage.getItem("total_time"));
			sessionStorage.setItem("total_time", diff.toString());

			count = parseInt(sessionStorage.getItem("total_count"));
			count++;
			sessionStorage.setItem("total_count", count.toString());
		}
	}

	switch (cellnum) {
	case -1:
		xmlhttp.open("POST", "modules/generate_images.php", true);
		xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		xmlhttp.send("username=<?php echo isset($_POST['username']) ? $_POST['username'] : "" ?>");
		break;
	case -2:
		count = parseInt(sessionStorage.getItem("total_reload"));
		if (count >=3) {
			window.location = "/result.php?success=0";
		}
		count++;
		sessionStorage.setItem("total_reload", count.toString());
		xmlhttp.open("GET", "modules/generate_images.php", true);
		xmlhttp.send();
		break;
	default:
		sessionStorage.setItem("total_reload", "0");
		xmlhttp.open("GET", "modules/generate_images.php?selected=" + cellnum, true);
		xmlhttp.send();
		break;
	}
}

function initTable() {
<?php
if ($isMobile) {
	$output = '$("#image_table").css("width", window.innerWidth);';
} else {
	$output ='$("#image_table").css("width", window.innerWidth*0.8);';
}
echo $output;
?>
}

function initPage() {
	$(document).ready(function() {
		initTable();
	});
	mSelect(-1);
}

<?php
if (isset($_POST['username'])) {
	echo 'sessionStorage.setItem("total_time","0");';
	echo 'sessionStorage.setItem("total_count","0");';
	echo 'sessionStorage.setItem("total_reload","0");';
	echo "window.onload = initPage();";
} else {
	echo "window.onload = mSelect(-2);";
}
?>

</script>

</head>
<body>
	<div id="image_table"></div>
</body>
</html>