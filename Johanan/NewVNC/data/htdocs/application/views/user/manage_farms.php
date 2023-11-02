<?php
defined('BASEPATH') OR exit('No direct script access allowed');
?><!DOCTYPE html>
<html lang="en">
<head>
	<title><?php echo lang('manage_farms');?></title>
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
</head>
<body>

<div class="container">

	<div class="row">
    	<div class="col-sm-10"><h1><?php echo lang('manage_farms');?></h1></div>
    	<div class="col-sm-2">
    	<br/>
        	<a href="<?php echo site_url('user/index'); ?>"><?php echo lang('home');?> 
        	<span class="glyphicon glyphicon-home"></span></a>
        	&nbsp;&nbsp;&nbsp;
        	<a href="<?php echo site_url('auth/logout'); ?>"><?php echo lang('logout');?>
        	<span class="glyphicon glyphicon-log-out"></span></a>
		</div>
	</div>

    <p/>
	<p>
    <button type="button" class="btn btn-success" data-toggle="modal" data-target="#create-item"><?php echo lang('mng_fm_add_fm');?></button>
    </p>
    <br/>

	<table id="items" class="stripe hover row-border" style="width:100%">
	<thead>
	    <tr>
		      <th><?php echo lang('mng_fm_name');?></th>
		      <th><?php echo lang('mng_fm_desc');?></th>
		      <th><?php echo lang('mng_fm_devices');?></th>
		      <th width="200px"><?php echo lang('mng_fm_action');?></th>
	    </tr>
	</thead>
	<tbody>
	</tbody>
</table>


<!-- Create Item Modal -->

<div class="modal fade" id="create-item" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h4 class="modal-title" id="myModalLabel"><?php echo lang('mng_fm_create_fm');?></h4>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
      </div>

      <div class="modal-body">
            <form data-toggle="validator" action="items/store" method="POST">
                <div class="form-group">
                    <label class="control-label" for="name"><?php echo lang('mng_fm_name');?>:</label>
                    <input type="text" name="name" class="form-control" data-error="<?php echo lang('mng_fm_name_err');?>" required />
                    <div class="help-block with-errors"></div>
                </div>

                <div class="form-group">
                    <label class="control-label" for="description"><?php echo lang('mng_fm_desc');?>:</label>
                    <textarea name="description" class="form-control" ></textarea>
                    <div class="help-block with-errors"></div>
                </div>

                <div class="form-group">
                    <button type="submit" class="btn crud-submit btn-success"><?php echo lang('mng_fm_submit');?></button>
                </div>
            </form>
      </div>
    </div>
  </div>
</div>


<!-- Edit Item Modal -->
<div class="modal fade" id="edit-item" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h4 class="modal-title" id="myModalLabel"><?php echo lang('mng_fm_edit_fm');?></h4>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
      </div>


      <div class="modal-body">
            <form data-toggle="validator" action="items/store" method="POST">
            	<input type="hidden" name="id" class="edit-id">
            	
                <div class="form-group">
                    <label class="control-label" for="name"><?php echo lang('mng_fm_name');?>:</label>
                    <input type="text" name="name" class="form-control" data-error="<?php echo lang('mng_fm_name_err');?>" required />
                    <div class="help-block with-errors"></div>
                </div>

                <div class="form-group">
                    <label class="control-label" for="description"><?php echo lang('mng_fm_desc');?>:</label>
                    <textarea name="description" class="form-control" ></textarea>
                    <div class="help-block with-errors"></div>
                </div>

                <div class="form-group">
                    <button type="submit" class="btn crud-submit-edit btn-success"><?php echo lang('mng_fm_submit');?></button>
                </div>
            </form>
      </div>
    </div>
  </div>
</div>
</div>

<script type="text/javascript">
$( document ).ready(function() {

	 var table = $('#items').DataTable( {
	        "ajax": "<?php echo site_url('user/listFarms') ?>",
	        "columns": [
	            { "data": "name" },
	            { "data": "description" },
	            { "render": function(data, type, row) {
	            	return "<a href='<?php echo site_url('user/assignDevices');?>/"  + row.id + "'><?php echo lang('mng_fm_mng_devices');?> </a>"; 
	            }}, 
	            { "defaultContent": 
		            '<button class="btn btn-primary edit-item" data-toggle="modal" data-target="#edit-item"><?php echo lang('edit');?></button>&nbsp;&nbsp;&nbsp;&nbsp;' +
		            '<button class="btn btn-danger remove-item"><?php echo lang('delete');?></button>' } 
	        ]
	    } );

	/* Create new Item */
	$(".crud-submit").click(function(e){
	    e.preventDefault();
	    var parent = $("#create-item");
	    var fields = ['name', 'description'];
	    var itemData = loadItem(parent, fields);

	    if(itemData.name != '' && itemData.number != ''){
	        $.ajax({
	            dataType: 'json',
	            type:'POST',
	            url: '<?php echo site_url('user/insertFarm'); ?>',
	            data:itemData
	        }).done(function(data){
	            if(data.result) {
		            clearData(parent, fields);

		            $(".modal").modal('hide');

		            itemData.id = data.id;
		            table.row.add(itemData).draw(false);
		            toastr.success('<?php echo lang('mng_fm_created');?>.', '<?php echo lang('success');?>', {timeOut: 5000});
	            } else {
		            toastr.error(data.error.message, '<?php echo lang('error');?>', {timeOut: 5000});
	        	}

	        });
	    }else{
	        alert('<?php echo lang('mng_fm_empty_data');?>')
	    }
	});

	var updSubmit = $(".crud-submit-edit").click(function(e){
	    e.preventDefault();
	    var parent = $("#edit-item");
	    var fields = ['name', 'description'];
	    var itemData = loadItem(parent, fields);
	    itemData.id = parent.find(".edit-id").val();

	    if(itemData.number != '' && itemData.name != ''){
	        $.ajax({
	            dataType: 'json',
	            type:'POST',
	            url: '<?php echo site_url('user/updateFarm'); ?>',
	            data:itemData
	        }).done(function(data){
		        if(data.result) {
		        	updSubmit.data('row').data(itemData).draw();
		            $(".modal").modal('hide');
		            toastr.success('<?php echo lang('mng_fm_updated');?>.', '<?php echo lang('success');?>', {timeOut: 5000});
		        } else {
		            toastr.error(data.error.message, '<?php echo lang('error');?>', {timeOut: 5000});
		        }
	        });
	    }else{
	        alert('<?php echo lang('mng_fm_empty_data');?>')
	    }
	});
	
	/* Remove Item */
	$("body").on("click",".remove-item",function(){
		if(confirm('<?php echo lang('mng_fm_ask_remove');?>')) {
			var row = table.row( $(this).parents('tr') );
			var data = row.data();
    
    	    $.ajax({
    	        dataType: 'json',
    	        type:'POST',
    	        url: '<?php echo site_url('user/removeFarm'); ?>',
    	        data:{id:data.id}
    	    }).done(function(data){
        	    if(data.result) {
			        var _this = $(this);
			        row.remove().draw();
                    toastr.success('<?php echo lang('mng_fm_deleted');?>.', '<?php echo lang('success');?>', {timeOut: 5000});
        	    } else {
		            toastr.error(data.error.message, '<?php echo lang('error');?>', {timeOut: 5000});
        	    }
    	    });
		}
	});

	/* Edit Item */
	$("body").on("click",".edit-item",function(){
		var row = table.row( $(this).parents('tr') );
		var data = row.data();

		updSubmit.data('row', row);

		var parent = $("#edit-item");
		setDialogItem(parent, data, ['name', 'description']);		
	    parent.find(".edit-id").val(data.id);
	    parent.validator('update');
	});


	});
	</script>
</body>
</html>