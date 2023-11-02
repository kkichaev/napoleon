<?php  if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Gklserver extends CI_Model {
    
    private $error = '';
    
    public function  __construct() {
        parent::__construct();
        
        $this->load->config('gklserver', TRUE);
        $this->load->library(array('session'));
    }
    
    public function _exchange_device($cmd, $deviceID, $dataPtr=NULL) {
        $socket = socket_create(AF_INET, SOCK_STREAM,  SOL_TCP);
        $addr = $this->config->item('address', 'gklserver');
        $port = $this->config->item('port', 'gklserver');
        $connected = socket_connect($socket, $addr, $port);
        if(!$connected) {
            $this->error = 'Error connecting ' . socket_strerror(socket_last_error());
            return NULL;
        }
        
        $connData = new ConnectionHelper();
        $connData->command = $cmd;
        $connData->deviceID = $deviceID;
        if($dataPtr != NULL) {
            $connData->setData($dataPtr);
        }
        
        $data = NULL;
        if( $connData->send($socket) ) {
            if( $connData->receive($socket) ) {
                $data = $connData->data;
            } else {
                $this->error = 'Error receive';
            }
            
            $connData->command = ConnectionHelper::$BYE_CMD;
            $connData->clearData();
            $connData->send($socket);
        } else {
            $this->error = "Error sending";
        }
        socket_close($socket);
        return $data;
    }
    
    public function get_screen($deviceID) {
        $image = $this->_exchange_device(ConnectionHelper::$DRAW_DEVICE_CMD, $deviceID);
//         if (strlen($image) > 0){
//             header('Content-type: image/jpeg');
//             print $image;
//         }
        
        return $image;
    }
    
    public function mouse_click($deviceID, $x, $y) {
        $data = $x.';'.$y;
        $image = $this->_exchange_device(ConnectionHelper::$MOUSE_CLICK_CMD, $deviceID, $data);
        //         if (strlen($image) > 0){
        //             header('Content-type: image/jpeg');
        //             print $image;
        //         }
        
        return $image;
    }
    
    public function  get_device_list() {
        $devices = array();
        
        $deviceStr = $this->_exchange_device(ConnectionHelper::$DEVICE_LIST_CMD, '');
        foreach (explode('\n', $deviceStr) as $di) {
            $device = new DeviceObject();
            if( $device->load($di) ) {
                array_push($devices, $device);
            }
        }
        
        return $devices;
    }
}

class DeviceObject {
    public $id;
    public $values;
    
    function __construct() {
        $this->id = '';
        $this->values = array();
    }
    
    function load($str) {        
        if(strlen($str) == 0)
            return FALSE;
            
        $data = explode(":", $str);
        if(count($data) != 2)
            return FALSE;
                
        $this->id = $data[0];
        foreach (explode(';',$data[1]) as $kv) {
            $values = explode('=', $kv);
            if(count($values)== 2)
                $this->values[$values[0]] = $values[1];
        }
        
        return TRUE;
    }
    
    function getValue($key) {
        if(array_key_exists($key, $this->values))
            return $this->values[$key];
            
        return '';
    }
    
    function getLastConnect() {
        $ret = NULL;
        
        $lc = $this->getValue('lastConnect');
        if(strlen($lc) > 0 && strcmp($lc, 'none') != 0) {
            $year = intval(substr($lc, 0, 4));
            $month = intval(substr($lc, 4, 2));
            $day = intval(substr($lc, 6, 2));
            $hour = intval(substr($lc, 8, 2));
            $minute = intval(substr($lc, 10, 2));
            $second = intval(substr($lc, 12, 2));
            
            $ret = new DateTime();
            $ret->setDate($year, $month, $day);
            $ret->setTime($hour, $minute, $second);
        }
        
        return $ret;
    }
    
    function getTemp() { return $this->getValue('1090781205'); }
    function getHumidity() { return $this->getValue('2147745840'); }
    
    function isAlarm() {
        $res = FALSE;
        $val = $this->getValue('1073807363');
        if(strlen($val))
            $res = (intval($val, 10) > 0);
            return $res;
    }
}

class ConnectionHelper {
    static $DEVICE_ID_TAG = 'DEVICEID';
    static $SESSION_ID_TAG = 'SESSIONID';
    static $DATA_LEN_TAG = 'DATALEN';
    static $CMD_TAG = 'CMD';
    
    public static $DRAW_DEVICE_CMD = 'DRAW_DEVICE';
    public static $MOUSE_CLICK_CMD = 'MOUSE_CLICK';
    public static $DEVICE_LIST_CMD = 'DEVICE_LIST';
    public static $BYE_CMD = 'BYE';
    
    public $deviceID;
    public $command;
    public $sessionID;
    
    public $data;
    public $dataLength = 0;
    
    public $options = array();
    
    function __construct() {
        $this->sessionID = "";
        $this->deviceID = "";
    }
    
    public function receive($socket) {
        
        $timeout = array();
        $timeout['sec'] = 60;
        $timeout['usec'] = 0;
        socket_setopt($socket, SOL_SOCKET, SO_RCVTIMEO, $timeout);
        
        $hBuf = '00000000';
        $rcvd = socket_recv($socket, $hBuf, 8, MSG_WAITALL);
        if($rcvd != 8)
            return false;
            
            $hlen = hexdec($hBuf);
            $rcvd = socket_recv($socket, $hBuf, $hlen, MSG_WAITALL);
            if($rcvd != $hlen)
                return false;
                
                $opts = explode(';', $hBuf);
                foreach($opts as $val) {
                    $op = explode('=', $val);
                    if( count($op) == 2)
                        $this->setOption($op[0], $op[1]);
                }
                                
                if( $this->dataLength > 0) {
                    
                    $this->data = '';
                    $len = $this->dataLength;
                    
                    while($len > 0) {
                        $buf = '';
                        $rcvd = socket_recv($socket, $buf, $len, MSG_WAITALL);
                        $this->data .= $buf;
                        $len -= $rcvd;
                    }
                    
                    $rcvd = strlen($this->data);
                    
                    if($rcvd != $this->dataLength) {
                        return false;
                    }
                }
                
                return true;
    }
    
    public function clearData() {
        $this->dataLength = 0;
        $this->data = '';
    }
    
    public function setData($data) {
        $this->dataLength = strlen($data);
        $this->data = $data;
    }
    
    public function setOption($key, $value) {
        if(strcmp($key, ConnectionHelper::$DEVICE_ID_TAG) == 0) {
            $this->deviceID = $value;
        } elseif (strcmp($key, ConnectionHelper::$SESSION_ID_TAG) == 0) {
            $this->sessionID = $value;
        } elseif (strcmp($key, ConnectionHelper::$CMD_TAG) == 0) {
            $this->command = $value;
        } elseif (strcmp($key, ConnectionHelper::$DATA_LEN_TAG) == 0) {
            $this->dataLength = intval($value, 10);
        }else {
            $this->options[$key] = $value;
        }
    }
    
    public function send($socket) {
        $len = strlen($this->data);
        
        $pkt = ConnectionHelper::$CMD_TAG . '=' . $this->command . ';' . ConnectionHelper::$DEVICE_ID_TAG . '=' . $this->deviceID . ';';
        $pkt .= ConnectionHelper::$SESSION_ID_TAG . '=' . $this->sessionID . ';';
        $pkt .= ConnectionHelper::$DATA_LEN_TAG . '=' . strval($len). ';';
        
        foreach ($this->options as $key=>$val) {
            $pkt .= $key . '=' . $val . ';';
        }
        
        $pkt = sprintf("%08X", strlen($pkt)) . $pkt;
        socket_write($socket, $pkt, strlen($pkt));
        
        if($len > 0)
            socket_write($socket, $this->data, strlen($this->data));
            
            return true;
    }
}
    