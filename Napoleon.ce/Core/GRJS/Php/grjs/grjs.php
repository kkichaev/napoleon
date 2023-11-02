<?php
ini_set("log_errors", TRUE);
ini_set('error_log', './php_error.log'); 

require_once '../int_cli_2/db_data.php';

header('Content-type: application/json; charset=utf-8');

/*
 * Handled commands
 * 
 * server=
 * register&login=&password=&project=
 * open&login=&password=
 */

function writeClientLog($db, $data) {
    foreach ($data as $el) {
        $stmt = "insert into grjs_client_log (`id`, `cid`, `action`, `date`, `duration`, `traficClient`, `traficServer`) " . 
        " values ($el->id, $el->cid, $el->action, $el->date, $el->duration, $el->traficClient, $el->traficServer)";
        
        mysqli_query($db, $stmt);
    }
}

function writeServerLog($db, $data) {
    foreach ($data as $el) {
        $stmt = "insert into grjs_server_log (`id`, `action`, `date`, `address`) " .
            " values ($el->id, $el->action, $el->date, '$el->address')";
        
        mysqli_query($db, $stmt);
    }
}

if( $_SERVER['REQUEST_METHOD'] == 'POST' ) {
    $obj = json_decode(file_get_contents("php://input"));
    if($obj) {
        $db = connectDB();
        mysqli_set_charset($db, 'utf8');
        
        if(property_exists($obj, "clients")) {
            writeClientLog($db, $obj->clients);
        }
        if(property_exists($obj, "servers")) {
            writeServerLog($db, $obj->servers);
        }
        mysqli_close($db);
    }
    print '{"res":1}';
    return;
}

function checkLogin($db, $login) {
    $sql = "SELECT login from grjs_servers where login = '$login'";
    $res = mysqli_query($db, $sql);
    if( mysqli_fetch_array($res) ) {
        return TRUE;
    }
    
    return FALSE;
}

function makeNewID($db) {
    $count = 0;
    $id = 0;
    $idVal = '';
    while($count < 100) {
        $val1 = random_int(0x80, 0xFF);
        $val2 = random_int(0, 0xFF);
        $val3 = random_int(0, 0xFF);
        $val4 = random_int(0, 0xFF);
        
        $id = ($val1 << 24) | ($val2 << 16) | ($val3 << 8) | $val4;
        
        $idVal = sprintf("%u", $id);
        $sql = "SELECT login from grjs_servers where `id` = $idVal";
        $res = mysqli_query($db, $sql);
        if( mysqli_fetch_array($res) ) {
            $count += 1;
            continue;
        } else {
            break;
        }
    }
    return $idVal;
}

define('CUR_ADDR', '192.168.0.161'); 
define('CUR_PORT', 9595);

function setServerAddress(&$obj, $id) {
    $obj->addr = CUR_ADDR;
    $obj->port = CUR_PORT;
}

$obj = new stdClass();
if(array_key_exists('server', $_GET)) {
    setServerAddress($obj, $_GET['server']);
} else {
    $login = $_GET['login'];
    $pwd = $_GET['password'];
    
    $db = connectDB();
    mysqli_set_charset($db, 'utf8');
    
    if(array_key_exists('register', $_GET)) {
        $proj = $_GET['project'];
        
        if(checkLogin($db, $login)) {
            $obj->error = "already_registred";
            $obj->res = 0;
        } else {
            $id = makeNewID($db);
            if($id == 0) {
                $obj->error = "no_id";
                $obj->res = 0;
            } else {
                $stmt = "insert into grjs_servers (`id`, `login`, `password`, `project`, `date`) VALUES ($id, '$login', '$pwd', '$proj', UNIX_TIMESTAMP())";
                mysqli_query($db, $stmt);
                
                $obj->id = $id;
                $obj->res = 1;
                setServerAddress($obj, $id);
            }
        }
    } elseif(array_key_exists('open', $_GET)) {
        $sql = "SELECT `id` from grjs_servers where `login` = '$login' and `password` = '$pwd'";
        $res = mysqli_query($db, $sql);
        if( ($row = mysqli_fetch_array($res))) {
            $obj->res = 1;
            $obj->id = $row['id'];
            setServerAddress($obj, $obj->id);
        } else {
            $obj->res = 0;
            $obj->error = "no_login_password";
        }
    }
    mysqli_close($db);
}

print json_encode($obj);
