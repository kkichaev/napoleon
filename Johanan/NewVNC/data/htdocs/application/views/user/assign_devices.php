<?php
defined('BASEPATH') OR exit('No direct script access allowed');
?><!DOCTYPE html>
<html lang="en">
<head>
	<title><?php echo lang('assign_dv_header');?></title>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
 	<link rel="stylesheet" href="/main.css">
	<script type="text/javascript" src="/main.js"></script>
 	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">

 	<link rel="stylesheet" href="/jquery/jquery-ui.min.css">

    <script src="/jquery/jquery.js"></script>
    <script src="/jquery/jquery-ui.min.js"></script>
    
    <script src="/jq_upload/js/vendor/jquery.ui.widget.js"></script>

	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/js/bootstrap.min.js"></script>

	<script src="https://cdnjs.cloudflare.com/ajax/libs/1000hz-bootstrap-validator/0.11.5/validator.min.js"></script>
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/js/toastr.min.js"></script>
	<link href="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/css/toastr.min.css" rel="stylesheet">
	
	<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.10.19/css/jquery.dataTables.css">
	<script type="text/javascript" src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>

	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/js/toastr.min.js"></script>
	<link href="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/css/toastr.min.css" rel="stylesheet">
</head>
<body>
<div class="container">

	<div class="row">
    	<div class="col-sm-10"><h1><?php echo lang('assign_dv_header') . ' ' . $farm->name;?></h1></div>
    	<div class="col-sm-2">
    	<br/>
        	<a href="<?php echo site_url('user/index'); ?>"><?php echo lang('home');?> 
        	<span class="glyphicon glyphicon-home"></span></a>
        	&nbsp;&nbsp;&nbsp;
        	<a href="<?php echo site_url('auth/logout'); ?>"><?php echo lang('logout');?>
        	<span class="glyphicon glyphicon-log-out"></span></a>
		</div>
	</div>

	<br/>
	<h4><?php echo lang('assign_dv_avail_dv');?></h4>
	<table id="availGW" class="stripe hover row-border" style="width:100%">
	<thead>
	    <tr>
		      <th><?php echo lang('mng_dv_id');?></th>
		      <th><?php echo lang('mng_dv_id');?></th>
		      <th><?php echo lang('mng_dv_name');?></th>
		      <th><?php echo lang('mng_dv_desc');?></th>
		      <th width="120px"><?php echo lang('mng_dv_action');?></th>
	    </tr>
	</thead>
	<tbody>
<?php foreach ($freeDV as $device):?>
    <tr>
    <td><?php echo $device['id']; ?> </td>
    <td><?php echo $device['number']; ?> </td>
    <td><?php echo $device['name']; ?> </td>
    <td><?php echo $device['description']; ?> </td>
    <td><button class="btn btn-success" id="addToFarm"><?php echo lang('assign_dv_add_to');?></button></td>
    </tr>
<?php endforeach;?>
	</tbody>
	</table>

	<br/>
	<div class="row">
		<div class="col-sm-2">
		<h4><?php echo lang('assign_dv_assign_dv');?></h4>
		</div>
    	<div class="col-sm-2"><button class="btn btn-success" id="saveData"><?php echo lang('save');?></button></div>	
	</div>
	
	<table id="assignedGW" class="stripe hover row-border" style="width:100%">
	<thead>
	    <tr>
		      <th><?php echo lang('mng_dv_id');?></th>
		      <th><?php echo lang('mng_dv_id');?></th>
		      <th><?php echo lang('mng_dv_name');?></th>
		      <th><?php echo lang('mng_dv_desc');?></th>
		      <th width="120px"><?php echo lang('mng_dv_action');?></th>
	    </tr>
	</thead>
	<tbody>
<?php foreach ($assignedDV as $device):?>
    <tr>
    <td><?php echo $device['id']; ?> </td>
    <td><?php echo $device['number']; ?> </td>
    <td><?php echo $device['name']; ?> </td>
    <td><?php echo $device['description']; ?> </td>
    <td><button class="btn btn-primary" id="rmvFromFarm"><?php echo lang('assign_dv_rmv_from');?></button></td>
    </tr>
<?php endforeach;?>
	</tbody>
	</table>
</div>

<script type="text/javascript">
$( document ).ready(function() {
	var tableAssign = $('#assignedGW').DataTable({
		"searching":false,
		"paging":false,
        "columns": [
            { "data": "id", "visible": false },
            { "data": "number" },
            { "data": "name" },
            { "data": "description" },
            { "defaultContent": 
	            '<button class="btn btn-primary" id="rmvFromFarm"><?php echo lang('assign_dv_rmv_from');?></button>'} 
 		]
	});

	var tableAvail = $('#availGW').DataTable( {
	        "columns": [
	            { "data": "id", "visible": false },
	            { "data": "number" },
	            { "data": "name" },
	            { "data": "description" },
	            { "defaultContent": 
		            '<button class="btn btn-success" id="addToFarm"><?php echo lang('assign_dv_add_to');?></button>'} 
	 		]
	});


	$('#availGW').on('click','#addToFarm',function(){
		var row = tableAvail.row( $(this).parents('tr') );
		var data = row.data();
		var dest = new Object();
		dest.id = data.id;
		dest.number = data.number;
		dest.name = data.name;
		dest.description = data.description;
		row.remove().draw();
		tableAssign.row.add(dest).draw(false);
	});

	$('#assignedGW').on('click','#rmvFromFarm',function(){
		var row = tableAssign.row( $(this).parents('tr') );
		var data = row.data();
		var dest = new Object();
		dest.id = data.id;
		dest.number = data.number;
		dest.name = data.name;
		dest.description = data.description;
		row.remove().draw();
		tableAvail.row.add(dest).draw(false);
	});

	$('#saveData').on('click',function(){
		var assigned = [];
		tableAssign.data().each(function(el){
			assigned.push(el.id);
		});

		var jsData = JSON.stringify(assigned);
		console.log(assigned);
		console.log(jsData);
		

        $.ajax({
            dataType: 'json',
            type:'POST',
            url: '<?php echo site_url('user/assignDevices/').$farm->id; ?>',
            data:{data:jsData}
        }).done(function(data){
            if(data.result) {
	            toastr.success('<?php echo lang('mng_dv_assigned');?>.', '<?php echo lang('success');?>', {timeOut: 5000});
            } else {
	            toastr.error(data.message, '<?php echo lang('error');?>', {timeOut: 5000});
        	}

        });
	});
});
</script>
</body>
</html>