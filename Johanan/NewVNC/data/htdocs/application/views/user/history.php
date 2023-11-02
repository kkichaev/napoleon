<?php
defined('BASEPATH') OR exit('No direct script access allowed');
?><!DOCTYPE html>
<html lang="en">
<head>
	<title><?php echo lang('user_history_page')?></title>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
 	<link rel="stylesheet" href="/main.css">
 	<link rel="stylesheet" href="/jquery//jquery-ui.css">
 	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">
 	
	<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.10.19/css/jquery.dataTables.css">
	
    <script src="/main.js"></script>
    <script src="/jquery/jquery.js"></script>
    <script src="/jquery/jquery-ui.min.js"></script>
    
    <script src="/jq_upload/js/vendor/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script> 	
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/js/bootstrap.min.js"></script>
</head>
<body>

<div class="container">	
	<div class="row">
    	<div class="col-sm-10"><h1><?php echo lang('user_history_page');?></h1></div>
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
	<h4><?php echo('Device ID:' . $device->number . ' ' . $device->name);?></h4>
	<br/>

	<p>
	Start date: <input type="text" id="date_start" />&nbsp;&nbsp;End date: <input type="text" id="date_end"/>
	&nbsp;&nbsp;<button id="getHistory" type="button" class="btn btn-primary"><i class="glyphicon glyphicon-repeat"></i> <?php echo lang('user_get_history')?></button>
	</p>

	<div style="width:39%">
	<div class="progress" style="margin-bottom: 0px">
  		<div id="load_progress" class="progress-bar progress-bar-success progress-bar-striped active" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%">
    	<span id="current-progress"></span>
  		</div>
	</div>
	</div>
	<br/>
	<button id="loadHistory" type="button" class="btn btn-success"><i class="glyphicon glyphicon-download-alt"></i> <?php echo lang('user_dn_history')?></button>
</div>

<script type="text/javascript">
var pollingInterval;
var POLL_TIMEOUT = 3000;
var requesting = false;

function pollingHistory() {
	$.ajax({
		url: '<?php echo site_url('user/pollingHistory') . '/' . $device->id; ?>'			
	}).done(function(data) {
		if(data) {
// 			console.log(data);
			if(!data.error) {
				if(data.lines == 0 && data.finished) {
					//console.log('test');
					$("#load_progress")
					.css("width", "100%")
					.removeClass('progress-bar-success')
					.addClass('progress-bar-danger')
					.text("No history data");
					$('#loadHistory').prop('disabled', true);
					return;
				}
				if(data.lines > 0)
					updateProgress(data.cur_line / data.lines * 100);
				else
					updateProgress(0);
					
				$( "#date_start" ).datepicker().datepicker("setDate", new Date(data.hist_start * 1000));
				$( "#date_end" ).datepicker().datepicker("setDate", new Date(data.hist_end * 1000));
				if(requesting || data.cur_line < data.lines)
					pollingInterval = setTimeout(pollingHistory, POLL_TIMEOUT);
			}
		}
	});
}

function dateToString(date) {
	var mon = date.getMonth() + 1;
	var day = date.getDate();
	return date.getFullYear().toString() + ((mon < 10) ? "0" + mon : mon).toString() + ((day < 10) ? "0" + day : day); 
}

function requestHistory() {
	clearTimeout(pollingInterval);
	updateProgress(0);

	requesting = true;
	var params = '?start=' + dateToString($( "#date_start" ).datepicker("getDate")) + 
		'&end=' + dateToString($( "#date_end" ).datepicker("getDate"));
	console.log(params); 
	$.ajax({
		url: '<?php echo site_url('user/requestHistory') . '/' . $device->id; ?>' + params,			
	}).done(function(data) {
		if(data && !data.error) {
			pollingInterval = setTimeout(pollingHistory, POLL_TIMEOUT);
		}
	});
}

function updateProgress(current_progress) {
    $("#load_progress")
    .css("width", current_progress + "%")
    .attr("aria-valuenow", current_progress)
	.removeClass('progress-bar-danger')
	.addClass('progress-bar-success')
    .text(current_progress.toFixed(2) + "% Complete");
    if (current_progress >= 100) {
        $('#loadHistory').prop('disabled', false);
      	requesting = false;
	  	clearTimeout(pollingInterval);
    }
}

$( document ).ready(function() {
	var endDate = new Date(); 
	var startDate = new Date();
	startDate.setMonth(startDate.getMonth()-1);
	$( "#date_start" ).datepicker().datepicker("option", "dateFormat", "dd MM yy").datepicker("setDate", startDate);
	$( "#date_end" ).datepicker().datepicker("option", "dateFormat", "dd MM yy").datepicker("setDate", endDate);
	$( "#getHistory" ).on('click', function(event) {
		event.preventDefault();
		requestHistory();
	}); 
	$('#loadHistory').prop('disabled', true);
	$( "#loadHistory" ).on('click', function(event) {
		event.preventDefault();
		window.location.href = "<?php echo site_url('user/downloadHistory') . '/' . $device->id; ?>";
	}); 
	pollingInterval = setTimeout(pollingHistory);
});

</script>
</body>
</html>