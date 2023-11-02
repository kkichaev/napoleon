<?php

//error_reporting(E_ALL);
//ini_set("log_errors", TRUE);
//ini_set('error_log', './php_error.log'); 

if( $_SERVER['REQUEST_METHOD'] != 'POST' ) {
	exit('');
}

require_once 'proj_obj.php';
require_once 'db_data.php';

function writeLog($obj, $db) {
    $length = strlen($obj->log);
    
    $fh = fopen('data://text/plain,' . urlencode($obj->log), 'r');
    
    while(true) {
        $login = stream_get_line($fh, $length, '[');
        if($login === FALSE) {
            break;
        }

        $last = FALSE;
        while($last == FALSE) {
            $val = '';
            while(true) {
                $ch = fgetc($fh);
                if($ch === FALSE) {
                    return;
                }
                if($ch == ',' || $ch == ']') {
                    $last = ($ch == ']');
                    break;
                }
                $val .= $ch;
            }
            $date = hexdec($val);
            $logData = new LogData();
            
            $logData->login = $login;
            $logData->ip = $obj->ip;
            $logData->port = $obj->port;
            $logData->project = $obj->name;
            
            $logData->date = $date;
            $logData->success = 'true';
            $logData->sended = time();
            $logData->tz = $obj->tz;
            $logData->Write($db);
           //print_r($logData);
        }
    }
}

header('Content-type: application/json; charset=utf-8');

$data = file_get_contents("php://input");
$obj = json_decode($data);
$db = connectDB();
mysqli_set_charset($db, 'utf8');


if(strlen($obj->log) > 0) {
    writeLog($obj, $db);
}

$where = "WHERE `project`='" . $obj->name . "' or `project`='' ORDER BY `project` DESC";
$demos = BaseData::LoadData($where, 'DemoData', $db);
if(count($demos) > 1)
    $demos = array_slice($demos,0,1);

$where = "WHERE `project`='" . $obj->name . "'";
$license = BaseData::LoadData($where, 'LicenseData', $db);
foreach($license as $lc) {
	$lc->end = $lc->end * 1000;
	$lc->start = $lc->start * 1000;
}

$where = "WHERE `type` <> ''";
$licenseTypes = BaseData::LoadData($where, 'LicenseTypeData', $db);

mysqli_close($db);

$out = '[{"name":"DemoData",' . '"data":' . json_encode($demos) . "},";
$out .= '{"name":"LicenseTypeData",' . '"data":' . json_encode($licenseTypes) . "},";;
$out .= '{"name":"LicenseData",' . '"data":' . json_encode($license) . "}]";

echo $out;