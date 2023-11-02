<?php
defined('BASEPATH') OR exit('No direct script access allowed');
$ctrl = get_instance();
$ctrl->load->helper('url');
?><!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>Device list</title>
<link href="<?php echo base_url('base.css')?>" rel="stylesheet">
<script type="text/javascript">
var img;
var imgRect;
function clickOnScreen(e) {
	e = e || window.event;
	X = e.clientX - imgRect.left;
	Y = e.clientY - imgRect.top;

	if(X < 0) X = 0;
	if(Y < 0) Y = 0;
	
	newSrc = '<?php echo site_url('device/event/'.$id.'/click/')?>' + X + '/' + Y
// 	console.log(newSrc)
	img.src = newSrc
}

function ready() {
    img = document.getElementById('imgID');
    img.addEventListener('click', clickOnScreen);
	imgRect = img.getBoundingClientRect();
}
document.addEventListener("DOMContentLoaded", ready);
</script>
</head>
<body>
<h1>Show device '<?php echo $id?>':</h1>
<div id='content'>
<img id='imgID' src='<?php echo site_url('device/get_screen/'.$id)?>' />
</div>
</body>
</html>