<?php
defined('BASEPATH') OR exit('No direct script access allowed');
?>
<!DOCTYPE html>
<html >
<head>
	<meta charset="utf-8">

 	<link rel="stylesheet" type="text/css" href="/main.css">
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">

	<title><?php echo lang('reset_password_heading')?></title>
	
 	<link rel="stylesheet" type="text/css" href="/main.css">
 	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">
</head>
<body>

<div class="container">

<h1><?php echo lang('reset_password_heading');?></h1>

<div id="infoMessage"><?php echo $message;?></div>

<?php echo form_open('auth/reset_password/' . $code);?>

	<div class="form-group">
		<label for="new_password"><?php echo sprintf(lang('reset_password_new_password_label'), $min_password_length);?></label> <br />
		<?php echo form_input($new_password,'','class="form-control"');?>
	</div>

	<div class="form-group">
		<?php echo lang('reset_password_new_password_confirm_label', 'new_password_confirm');?> <br />
		<?php echo form_input($new_password_confirm,'','class="form-control"');?>
	</div>

	<?php echo form_input($user_id);?>
	<?php echo form_hidden($csrf); ?>

	<br/>
	<button type="submit" class="btn btn-success"><?php echo lang('reset_password_submit_btn');?></button>

<?php echo form_close();?>
</div> 

</body>
</html>