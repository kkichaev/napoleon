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

	<title><?php echo lang('edit_user_heading')?></title>
	
 	<link rel="stylesheet" type="text/css" href="/main.css">
 	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">

</head>
<body>

<div class="container">

	<div class="row">
    	<div class="col-sm-10"><h1><?php echo lang('edit_user_heading');?></h1></div>
    	<div class="col-sm-2">
    	<br/>
        	<a href="<?php echo site_url('auth/logout'); ?>"><?php echo lang('logout');?>
        	<span class="glyphicon glyphicon-log-out"></span></a>
		</div>
	</div>

<br/>
<div id="infoMessage"><?php echo $message;?></div>

<?php echo form_open(uri_string());?>

      <div class="form-group">
            <?php echo lang('edit_user_fname_label', 'first_name');?> <br />
            <?php echo form_input($first_name,'','class="form-control"');?>
      </div>

      <div class="form-group">
            <?php echo lang('edit_user_lname_label', 'last_name');?> <br />
            <?php echo form_input($last_name,'','class="form-control"');?>
      </div>

      <div class="form-group">
            <?php echo lang('edit_user_id_label', 'uid');?> <br />
            <?php echo form_input($uid,'','class="form-control"');?>
      </div>

      <div class="form-group">
            <?php echo lang('edit_user_company_label', 'company');?> <br />
            <?php echo form_input($company,'','class="form-control"');?>
      </div>

      <div class="form-group">
            <?php echo lang('edit_user_phone_label', 'phone');?> <br />
            <?php echo form_input($phone,'','class="form-control"');?>
      </div>

      <div class="form-group">
            <?php echo lang('edit_user_password_label', 'password');?> <br />
            <?php echo form_input($password,'','class="form-control"');?>
      </div>

      <div class="form-group">
            <?php echo lang('edit_user_password_confirm_label', 'password_confirm');?><br />
            <?php echo form_input($password_confirm,'','class="form-control"');?>
      </div>

      <?php if ($this->ion_auth->is_admin()): ?>

          <h3><?php echo lang('edit_user_groups_heading');?></h3>
          <?php foreach ($groups as $group):?>
              <?php
                  $gID=$group['id'];
                  $checked = null;
                  $item = null;
                  foreach($currentGroups as $grp) {
                      if ($gID == $grp->id) {
                          $checked= ' checked="checked"';
                      break;
                      }
                  }
              ?>
              <input type="checkbox" class="form-check-input" name="groups[]" value="<?php echo $group['id'];?>"<?php echo $checked;?>>
              <label class="form-check-label">
              <?php echo htmlspecialchars($group['name'],ENT_QUOTES,'UTF-8');?>
              </label>
              &nbsp;&nbsp;
          <?php endforeach?>

      <?php endif ?>

      <?php echo form_hidden('id', $user->id);?>
      <?php echo form_hidden($csrf); ?>
	<br/>
	<br/>
	<button type="submit" class="btn btn-success"><?php echo lang('edit_user_submit_btn');?></button>

<?php echo form_close();?>
</div> 

</body>
</html>