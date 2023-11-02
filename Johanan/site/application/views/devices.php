<?php
defined('BASEPATH') OR exit('No direct script access allowed');
get_instance()->load->helper('url');
?><!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8">
	<title>Device list</title>
	<link href="<?php echo base_url('base.css')?>" rel="stylesheet">
</head>
<body>

<h1>Device list:</h1>
<table style='width: 80%' >
<tr>
<th>Status</th><th>ID</th><th>Last connect</th><th>Temp</th><th>Humidity</th>
</tr>
<?php foreach ($devices as $device): ?>
<tr>
<td><img alt="" src="<?php echo $device->isAlarm() ? base_url('pics/alarm.png') : base_url('pics/good.png')?>"></td>
<td><?php echo $device->id?></td>
<td><?php echo $device->getLastConnect() == NULL ? "none" : $device->getLastConnect()->format('Y-m-d H:i:s')?>
<td><?php echo $device->getTemp()?></td>
<td><?php echo $device->getHumidity()?></td>
<td><a  href='<?php echo site_url("device/view/".$device->id)?>'>Connect</a></td>
</tr>
<?php endforeach;?>
</table>
</body>
</html>