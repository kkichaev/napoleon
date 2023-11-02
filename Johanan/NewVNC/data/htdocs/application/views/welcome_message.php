<?php
defined('BASEPATH') OR exit('No direct script access allowed');
?><!DOCTYPE html>
<html lang="en">
<head>
	<title><?php echo lang('user_page')?></title>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
 	<link rel="stylesheet" href="/main.css">
 	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">
 	
	<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.10.19/css/jquery.dataTables.css">
	
    <script src="/jquery/jquery.js"></script>
    <script src="/jquery/jquery-ui.min.js"></script>
    
    <script src="/jq_upload/js/vendor/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script> 	
</head>
<body>

<div class="container">	
	<div class="row">
    	<div class="col-sm-10"><h1><?php echo lang('welcome');?></h1></div>
    	<div class="col-sm-2">
    	<br/>
        	<a href="<?php echo site_url('auth/logout'); ?>"><?php echo lang('logout');?>
        	<span class="glyphicon glyphicon-log-out"></span></a>
		</div>
	</div>

	<h4><?php echo lang('user_id_is') . ' ' . $unique_id; ?></h4>
	<p/>
	<div class="row">
    	<div class="col-sm-2">
    	<a class="btn btn-success btn-block" href="<?php echo site_url('user/manageFarms'); ?>"><?php echo lang('manage_farms'); ?></a>
    	</div>
    	<div class="col-sm-2">
    	<a class="btn btn-success btn-block" href="<?php echo site_url('user/manageDevices'); ?>"><?php echo lang('manage_devices'); ?></a>
    	</div>
	</div>

	<p/>
	<h4><?php echo lang('browse_farms')?></h4>
	<br/>
	<table id="items" class="stripe hover row-border" style="width:100%">
	<thead>
	    <tr>
		      <th><?php echo lang('brws_fm_name');?></th>
		      <th><?php echo lang('brws_fm_devices');?></th>
		      <th><?php echo lang('alarm');?></th>
	    </tr>
	</thead>
	<tbody>
<?php foreach ($farms as $farm):?>
    <tr>
    <td><a href='<?php echo site_url('user/farmBrowse')."/".$farm['id'];?>'> <?php echo $farm['name'];?></a></td>
    <td><?php echo $farm['devices']; ?> </td>
    <td><a href='<?php echo site_url('user/farmBrowse')."/".$farm['id'];?>'>
    	<img src='<?php echo $farm['alarm'] > 0 ? "/images/alarm.png" : "/images/good.png" ;?>' alt="Alaram image"/></a></td>
    </tr>
<?php endforeach;?>
	
	</tbody>
</table>
	
</div>

<script type="text/javascript">
$( document ).ready(function() {

	var table = $('#items').DataTable();
});
</script>
</body>
</html>