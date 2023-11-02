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

	<title><?php echo lang('forgot_password_heading')?></title>
	
 	<link rel="stylesheet" type="text/css" href="/main.css">
 	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">
</head>
<body>

<div class="container">

<h1><?php echo lang('forgot_password_heading');?></h1>
<p><?php echo sprintf(lang('forgot_password_subheading'), $identity_label);?></p>

<div id="infoMessage"><?php echo $message;?></div>

<?php echo form_open("auth/forgot_password");?>

      <p>
      	<label for="identity"><?php echo (($type=='email') ? sprintf(lang('forgot_password_email_label'), $identity_label) : sprintf(lang('forgot_password_identity_label'), $identity_label));?></label> <br />
      	<?php echo form_input($identity,'','class="form-control"');?>
      </p>
	
	<br/>
	<button type="submit" class="btn btn-success"><?php echo lang('forgot_password_submit_btn');?></button>

<?php echo form_close();?>
</div>
</body>
</html>