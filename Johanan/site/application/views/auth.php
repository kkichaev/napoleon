<?php
defined('BASEPATH') OR exit('No direct script access allowed');
get_instance()->load->helper('url');
?><!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8">
	<title>Auth page</title>
	<link href="<?php echo base_url('base.css')?>" rel="stylesheet">

	<style type="text/css">
	#infoMessage {
		margin: 5px 0 0 10px;
	}
	</style>
</head>
<body>

<div id="container">
<div id="infoMessage"><?php echo $message;?></div>

<div id="body">
<?php echo form_open("auth/login");?>
<p />
<table>
<tr><td><?php echo lang('login_identity_label', 'identity');?></td><td><?php echo form_input($identity);?></td></tr>
<tr><td><?php echo lang('login_password_label', 'password');?></td><td><?php echo form_input($password);?></td></tr>
<tr><td><?php echo lang('login_remember_label', 'remember');?></td><td><?php echo form_checkbox('remember', '1', FALSE, 'id="remember"');?></td></tr>
</table>
  <p><?php echo form_submit('submit', lang('login_submit_btn'));?></p>

<?php echo form_close();?>
</div>
</div>

</body>
</html>