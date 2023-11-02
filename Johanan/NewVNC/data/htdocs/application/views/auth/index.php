<?php
defined('BASEPATH') OR exit('No direct script access allowed');
?>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">

	<title><?php echo lang('index_heading')?></title>
	
 	<link rel="stylesheet" type="text/css" href="/main.css">
 	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">

 	<link rel="stylesheet" href="/jquery/jquery-ui.min.css">

    <script src="/jquery/jquery.js"></script>
    <script src="/jquery/jquery-ui.min.js"></script>
    
    <script src="/jq_upload/js/vendor/jquery.ui.widget.js"></script>

	<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.10.19/css/jquery.dataTables.css">
	<script type="text/javascript" src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
</head>

<body>

<div class="container">

	<div class="row">
    	<div class="col-sm-10"><h1><?php echo lang('index_heading');?></h1></div>
    	<div class="col-sm-2">
    	<br/>
        	<a href="<?php echo site_url('auth/logout'); ?>"><?php echo lang('logout');?>
        	<span class="glyphicon glyphicon-log-out"></span></a>
		</div>
	</div>

<br/>
<div id="infoMessage"><?php echo $message;?></div>

<table id="users" class="stripe hover row-border" width="100%" >
	<thead>
    	<tr>
    		<th><?php echo lang('index_fname_th');?></th>
    		<th><?php echo lang('index_lname_th');?></th>
    		<th><?php echo lang('index_userid_th');?></th>
    		<th><?php echo lang('index_email_th');?></th>
    		<th><?php echo lang('index_groups_th');?></th>
    		<th><?php echo lang('index_status_th');?></th>
    		<th><?php echo lang('index_action_th');?></th>
    	</tr>
	</thead>
	<?php foreach ($users as $user):?>
		<tr>
            <td><?php echo htmlspecialchars($user->first_name,ENT_QUOTES,'UTF-8');?></td>
            <td><?php echo htmlspecialchars($user->last_name,ENT_QUOTES,'UTF-8');?></td>
            <td><?php echo htmlspecialchars($user->userid,ENT_QUOTES,'UTF-8');?></td>
            <td><?php echo htmlspecialchars($user->email,ENT_QUOTES,'UTF-8');?></td>
			<td>
				<?php foreach ($user->groups as $group):?>
					<?php echo htmlspecialchars($group->name,ENT_QUOTES,'UTF-8') ;?><br />
                <?php endforeach?>
			</td>
			<td><?php echo ($user->active) ? anchor("auth/deactivate/".$user->id, lang('index_active_link')) : anchor("auth/activate/". $user->id, lang('index_inactive_link'));?></td>
			<td><?php echo anchor("auth/edit_user/".$user->id, 'Edit') ;?></td>
		</tr>
	<?php endforeach;?>
</table>

<span class="btn btn-success">
<a href='<?php echo site_url('auth/create_user');?>'><?php echo lang('index_create_user_link');?></a>
</span>

</div> 


<script type="text/javascript">
$( document ).ready(function() {
	 var table = $('#users').DataTable();
});
</script>
</body>
</html>