<?php
defined('BASEPATH') OR exit('No direct script access allowed');
?>
<!DOCTYPE html>
<html >
<head>
	<meta charset="utf-8">
	<title>Login page</title>
 	<link rel="stylesheet" type="text/css" href="/main.css">
</head>
<body>

<div id="container">

<div id="body">

<h1><?php echo lang('edit_group_heading');?></h1>
<p><?php echo lang('edit_group_subheading');?></p>

<div id="infoMessage"><?php echo $message;?></div>

<?php echo form_open(current_url());?>

      <p>
            <?php echo lang('edit_group_name_label', 'group_name');?> <br />
            <?php echo form_input($group_name);?>
      </p>

      <p>
            <?php echo lang('edit_group_desc_label', 'description');?> <br />
            <?php echo form_input($group_description);?>
      </p>

      <p><?php echo form_submit('submit', lang('edit_group_submit_btn'));?></p>

<?php echo form_close();?>
</div>	
</div> 

</body>
</html>
