<?php
defined('BASEPATH') OR exit('No direct script access allowed');
?><!DOCTYPE html>
<html lang="en">
<head>
	<title><?php echo lang('admin_page')?></title>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
 	<link rel="stylesheet" href="/main.css">
 	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">
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

	<p/>
	<div class="row">
    	<div class="col-sm-2">
    	<a class="btn btn-success btn-block" href="<?php echo site_url('admin/manageUsers'); ?>"><?php echo lang('manage_users'); ?></a>
    	</div>
	</div>
</div>
</body>
</html>