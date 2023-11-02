<?php
defined('BASEPATH') OR exit('No direct script access allowed');
?><!DOCTYPE html>
<html lang="en">
<head>
	<title><?php echo lang('user_page')?></title>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
 	<link rel="stylesheet" href="/main.css">
 	<link rel="stylesheet" href="/jquery//jquery-ui.css">
 	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">
 	
	<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.10.19/css/jquery.dataTables.css">
	
    <script src="/main.js"></script>
    <script src="/jquery/jquery.js"></script>
    <script src="/jquery/jquery-ui.min.js"></script>
    
    <script src="/jq_upload/js/vendor/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script> 	
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/js/bootstrap.min.js"></script>
</head>
<body>

<div class="container">	

	<div class="row">
    	<div class="col-sm-10"><h1><?php echo lang('welcome');?></h1></div>
    	<div class="col-sm-2">
    	<br/>
        	<a href="<?php echo site_url('user/index'); ?>"><?php echo lang('home');?> 
        	<span class="glyphicon glyphicon-home"></span></a>
        	&nbsp;&nbsp;&nbsp;
        	<a href="<?php echo site_url('auth/logout'); ?>"><?php echo lang('logout');?>
        	<span class="glyphicon glyphicon-log-out"></span></a>
		</div>
	</div>

	<p/>
	<h4><?php echo lang('devices_of_farm') . " " . $farm->name;?></h4>
	<br/>
	<table id="items" class="stripe hover row-border" style="width:100%">
	<thead>
	    <tr>
		      <th><?php echo lang('brws_dv_id');?></th>
		      <th><?php echo lang('brws_dv_name');?></th>
		      <th><?php echo lang('brws_dv_last_connect');?></th>
		      <th><?php echo lang('temp');?></th>
		      <th><?php echo lang('humidity');?></th>
		      <th><?php echo lang('alarm');?></th>
		      <th><?php echo lang('history');?></th>
	    </tr>
	</thead>
	<tbody>
	</tbody>
</table>
	
</div>

<script type="text/javascript">

function connectToDevice(devNumber, userid) {
	var strWindowFeatures = "location=yes,height=600,width=800,scrollbars=no,status=yes";

	var params = "device=" + devNumber;
	var URL = "<?php echo site_url('user/vncConnect');?>?" + params;
	console.log(URL);
	var win = window.open(URL, "_blank", strWindowFeatures);	        		
}

$( document ).ready(function() {
	var table = $('#items').DataTable({
        "ajax": "<?php echo site_url('user/farmDeviceBrowse') . "/" . $farm->id ?>",
		"paging":false,
		"columns": [
             { "render": function(data, type, row) {
            	return "<a '#' onclick='connectToDevice(" + row.number + ", <?php echo $userid?>);return false;'>" + row.number + "</a>"; 
             }}, 
            { "data": "name" },
             { "render": function(data, type, row) {
             	var date = new Date(row.time * 1000); 
            	return (!row.time) ? "<?php echo lang('not_connected');?>" : date.format('d-m-Y H:i:s'); 
             }}, 
            { "data": "temp" },
            { "data": "humidity" },
             { "render": function(data, type, row) {
            	return "<a '#' onclick='connectToDevice(" + row.number + ", <?php echo $userid?>);return false;'>" + 
             		"<img src='" + ((row.alarm != 0) ? "/images/alarm.png" : "/images/good.png") + "' alt='Alaram image'/></a>"; 
             }}, 
             { "render": function(data, type, row) {
             	return '<a href="<?php echo site_url('user/deviceHistory').'/'; ?>' + row.number + '/<?php echo $userid?>" class="btn btn-primary btn-lg">' + 
             		'<span class="glyphicon glyphicon-download-alt" /></a>'; 
              }}, 
		],
	});

	setInterval( function () { table.ajax.reload(null, false);}, 2000 );
});
</script>
</body>
</html>