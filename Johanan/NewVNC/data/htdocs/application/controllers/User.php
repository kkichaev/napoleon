<?php 

class User extends CI_Controller
{
    public function __construct()
    {
        parent::__construct();
        $this->load->helper(['url', 'language']);
        $this->lang->load('user');

        $this->load->library('ion_auth');
        if (!$this->ion_auth->logged_in())
            redirect('auth/login', 'refresh');
    }
    
    public function index() {
        $uid = $this->session->userdata('unique_id');
                
        $userid = $this->ion_auth->get_user_id();
        $dedInterval = 3600; // in seconds
        
        $this->load->model('farm');
        $farms = $this->farm->browse($userid, $dedInterval);
        
        $params = array('unique_id'=>$uid, 'farms'=>$farms);
        $this->load->view('welcome_message', $params);
    }
    
    public function vncConnect() {
        if(array_key_exists('device', $_GET)) {
            $device = $_GET['device'];
            $uid = $this->session->userdata('unique_id');
            
            $this->config->load('gkl_server');
            $serverAdr = $this->config->item('server_address');
            $serverLocalAdr = $this->config->item('server_local_address');
            $serverPort = $this->config->item('server_port');
            $TUNNEL_ADDRESS = $this->config->item('tunnel');
            
            $params = array('device' => $device, 'serverAdr' => $serverAdr, 'uid' => $uid, 'serverLocalAdr' => $serverLocalAdr,
                'serverPort' => $serverPort, 'TUNNEL_ADDRESS' => $TUNNEL_ADDRESS);
            $this->load->view('user/vnc_connect', $params);
            
            return;
        }
    }
    
    public function farmDeviceBrowse($farmId) {
        $this->load->model('farm');
        $devices = $this->farm->browseFarm($farmId);
        $devAvail = array();
        foreach($devices as &$dev) {
            $devAvail[$dev['id']] = &$dev;
        }
        $devValues = $this->farm->farmDeviceValues($farmId);
        foreach ($devValues as $dv) {
            if($dv['param'] == 1090781205) { // AVR_TEMP
                $devAvail[$dv['id']]['temp'] = $dv['value'];
            } elseif( $dv['param'] == 2147745840) { // HUMIDITY_SENSOR1
                $devAvail[$dv['id']]['humidity'] = $dv['value'];
            } elseif( $dv['param'] == 1073807363) { // ALARM
                if($dv['value'] == 1)
                    $devAvail[$dv['id']]['alarm'] = 1;
            }
        
        }
        
        $res = array();
        $res['data'] = $devices;
        $this->output->set_content_type('application/json')->set_output(json_encode($res));
    }
    
    public function farmBrowse($farmId) {
        $uid = $this->session->userdata('unique_id');
        
        $this->load->model('farm');
        
        $farm = $this->farm->load($farmId);
        
        $dateFormat = $this->config->item('date_format');
        
        $params = array('farm'=>$farm, 'dateFormat'=>$dateFormat, 'userid'=>$uid);
        $this->load->view('user/farm_browse', $params);
    }
    
    
    
    /**
     * Farms
     */
    public function manageFarms() {
        $this->load->view('user/manage_farms');
    }
    
    public function listFarms() {
        $this->load->model('farm');
        
        $userid = $this->ion_auth->get_user_id();
        
        $res = array();
        $res['data'] = $this->farm->getRows($userid);
        $this->output->set_content_type('application/json')->set_output(json_encode($res));
    }

    public function removeFarm() {
        $this->load->model('farm');
        $res = array();
        
        if($this->farm->delete($_POST)) {
            $res['result'] = true;
        } else {
            $res['result'] = false;
            $res['error'] = $this->farm->getError();
        }
        $this->output->set_content_type('application/json')->set_output(json_encode($res));
    }
    
    public function insertFarm() {
        $res = array();
        
        $userid = $this->ion_auth->get_user_id();
        
        $this->load->model('farm');
        $id = $this->farm->insert($_POST, $userid);
        if($id == 0) {
            $res['result'] = false;
            $res['error'] = $this->farm->getError();
        } else {
            $res['result'] = true;
            $res['id'] = $id;
        }
        $this->output->set_content_type('application/json')->set_output(json_encode($res));
    }
        
    public function updateFarm() {
        $this->load->model('farm');
        $res = array();
        
        if($this->farm->update($_POST)) {
            $res['result'] = true;
        } else {
            $res['result'] = false;
            $res['error'] = $this->farm->getError();
        }
        $this->output->set_content_type('application/json')->set_output(json_encode($res));
    }
    
    public function assignDevices($id) {
        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            $res = array();
            if(array_key_exists('data', $_POST)) {
                $data = json_decode($_POST['data']);

                $res['result'] = true;
                
                $this->load->model('device');
                $msg = $this->device->assignDevices($data, $id);
                if($msg !== NULL) {
                    $res['result'] = false;
                    $res['message'] = $msg;
                }
            } else{
                $res['result'] = false;
                $res['message'] = 'No data in request';
            }
            $this->output->set_content_type('application/json')->set_output(json_encode($res));
            
        } else {
            $userid = $this->ion_auth->get_user_id();
            
            $this->load->model('farm');
            $this->load->model('device');
            
            $farm = $this->farm->load($id);
            $assigned = $this->device->assignedDevices($id);
            $free = $this->device->freeDevices($userid);
            
            $params = array('farm'=>$farm, 'assignedDV'=>$assigned, 'freeDV'=>$free);
            
            $this->load->view('user/assign_devices', $params);
        }
    }
    
    /**
     * Devices
     */
    
    
    public function manageDevices() {
        $this->load->view('user/manage_devices');
    }
    
    public function removeDevice() {
        $this->load->model('device');
        $res = array();
        
        if($this->device->delete($_POST)) {
            $res['result'] = true;
        } else {
            $res['result'] = false;
            $res['error'] = $this->device->getError();
        }
        $this->output->set_content_type('application/json')->set_output(json_encode($res));
    }
    
    public function updateDevice() {
        $this->load->model('device');
        $res = array();
        
        if($this->device->update($_POST)) {
            $res['result'] = true;
        } else {
            $res['result'] = false;
            $res['error'] = $this->device->getError();
        }
        $this->output->set_content_type('application/json')->set_output(json_encode($res));
    }
    
    public function insertDevice() {
        $userid = $this->ion_auth->get_user_id();
        
        $this->load->model('device');
        $res = array();
        
        $id = $this->device->insert($_POST, $userid);
        if($id == 0) {
            $res['result'] = false;
            $res['error'] = $this->device->getError();
        } else {
            $res['result'] = true;
            $res['id'] = $id;
        }
        $this->output->set_content_type('application/json')->set_output(json_encode($res));
    }
    
    public function listDevices() {
        $userid = $this->ion_auth->get_user_id();
        $res = array();
        $this->load->model('device');
        
        $res['data'] = $this->device->getRows($userid);
        $this->output->set_content_type('application/json')->set_output(json_encode($res));
    }
    
    
    public function deviceHistory($deviceNum, $userid) {
        $this->load->model('device');
        $device = $this->device->getDeviceByNum($deviceNum, $userid);
        $params = array('device' => $device);
        $this->load->view('user/history', $params);
    }

    public function downloadHistory($deviceId) {
        $this->load->model('history');
        $res = $this->history->getHistory($deviceId);
        if($res && $res->file_name != NULL) {
            if(!is_file($res->file_name)) {
                print "error";    
            } else {
                $this->load->helper('download');
                force_download($res->file_name, NULL);
            }
        }
    }
    
    public function requestHistory($deviceId) {
        
        $res = new stdClass();
        $res->error = true;
        if(array_key_exists('start', $_GET) && array_key_exists('end', $_GET)) {
            $this->load->model('history');
            if($this->history->putRequest($deviceId, $_GET['start'], $_GET['end'])) {
                $res->error = false;
            }
        }

        $this->output->set_content_type('application/json')->set_output(json_encode($res));
    }
    
    public function pollingHistory($deviceId) {
        $this->load->model('history');
        $res = $this->history->getHistory($deviceId);
        if(!$res) {
            $res = new stdClass();
            $res->error = true;
        } else {
            unset($res->file_name);
        }
        $this->output->set_content_type('application/json')->set_output(json_encode($res));
    }
}