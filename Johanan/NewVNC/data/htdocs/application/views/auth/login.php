<?php
defined('BASEPATH') OR exit('No direct script access allowed');
?>
<!DOCTYPE html>
<html >
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	
	<title>Login page</title>
 	<link rel="stylesheet" type="text/css" href="/main.css">
 	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">
</head>
<body>

<div class="container">

<h1><?php echo lang('login_heading');?></h1>
<p><?php echo lang('login_subheading');?></p>

<div id="infoMessage"><?php echo $message;?></div>

<?php echo form_open("auth/login");?>

  <div class="input-group">
    <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
    <input id="identity" type="text" class="form-control" name="identity" placeholder="Email">
  </div>
  <div class="input-group">
    <span class="input-group-addon"><i class="glyphicon glyphicon-lock"></i></span>
    <input id="password" type="password" class="form-control" name="password" placeholder="<?php echo lang('edit_user_validation_password_label'); ?>">
  </div>
  <br/>
	<button type="submit" class="btn btn-success"><?php echo lang('login_submit_btn'); ?></button>
<?php echo form_close();?>

<br/>
<a href="forgot_password"><?php echo lang('login_forgot_password');?></a>
</div>	

</body>
</html>