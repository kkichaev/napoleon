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
        	<a href="<?php echo site_url('user/index'); ?>"><?php echo lang('home');?> 
        	<span class="glyphicon glyphicon-home"></span></a>
        	&nbsp;&nbsp;&nbsp;
        	<a href="<?php echo site_url('auth/logout'); ?>"><?php echo lang('logout');?>
        	<span class="glyphicon glyphicon-log-out"></span></a>
		</div>
	</div>
	<h3><?php echo lang('not_found_device') . " " . $device?></h3>
</div>
</body>
</html>