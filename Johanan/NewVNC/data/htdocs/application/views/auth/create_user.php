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

	<title><?php echo lang('create_user_heading')?></title>
	
 	<link rel="stylesheet" type="text/css" href="/main.css">
 	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">

</head>
<body>

<div class="container">

	<div class="row">
    	<div class="col-sm-10"><h1><?php echo lang('create_user_heading');?></h1></div>
    	<div class="col-sm-2">
    	<br/>
        	<a href="<?php echo site_url('auth/logout'); ?>"><?php echo lang('logout');?>
        	<span class="glyphicon glyphicon-log-out"></span></a>
		</div>
	</div>

<br/>
<div id="infoMessage"><?php echo $message;?></div>

<?php echo form_open("auth/create_user");?>

      <div class="form-group">
            <?php echo lang('create_user_fname_label', 'first_name');?> <br />
            <?php echo form_input($first_name,'','class="form-control"');?>
      </div>

      <div class="form-group">
            <?php echo lang('create_user_lname_label', 'last_name');?> <br />
            <?php echo form_input($last_name,'','class="form-control"');?>
      </div>
      
      <?php
      if($identity_column!=='email') {
          echo '<div class="form-group">';
          echo lang('create_user_identity_label', 'identity');
          echo '<br />';
          echo form_error('identity');
          echo form_input($identity,'','class="form-control"');
          echo '</div>';
      }
      ?>

      <div class="form-group">
            <?php echo lang('create_user_company_label', 'company');?> <br />
            <?php echo form_input($company,'','class="form-control"');?>
      </div>

      <div class="form-group">
            <?php echo lang('create_user_email_label', 'email');?> <br />
            <?php echo form_input($email,'','class="form-control"');?>
      </div>

      <div class="form-group">
            <?php echo lang('create_user_id_label', 'userid');?> <br />
            <?php echo form_input($userid,'','class="form-control"');?>
      </div>

      <div class="form-group">
            <?php echo lang('create_user_phone_label', 'phone');?> <br />
            <?php echo form_input($phone,'','class="form-control"');?>
      </div>

      <div class="form-group">
            <?php echo lang('create_user_password_label', 'password');?> <br />
            <?php echo form_input($password,'','class="form-control"');?>
      </div>

      <div class="form-group">
            <?php echo lang('create_user_password_confirm_label', 'password_confirm');?> <br />
            <?php echo form_input($password_confirm,'','class="form-control"');?>
      </div>


	<button type="submit" class="btn btn-success"><?php echo lang('create_user_submit_btn');?></button>

<?php echo form_close();?>
</div> 

</body>
</html>
