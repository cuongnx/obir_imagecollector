<?php
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
<title>OBIR WebTest</title>
<style type="text/css">
img.img_cell {
	width: 250px;
	height: 250px;
}
</style>

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
		xmlhttp.open("GET", "modules/generate_images.php", true);
		xmlhttp.send();
		break;
	default:
		xmlhttp.open("GET", "modules/generate_images.php?selected=" + cellnum, true);
		xmlhttp.send();
		break;
	}
}

<?php
if (isset($_POST['username'])) {
	echo 'sessionStorage.setItem("total_time","0");';
	echo 'sessionStorage.setItem("total_count","0");';
	echo "window.onload = mSelect(-1);";
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