<?php

class Device extends CI_Model {
    var $error;
    
    function __construct() {
        parent::__construct();
        $this->load->database();
        $this->db->db_debug = FALSE;
    }
    
    public function assignedDevices($farmId) {
        $query = $this->db->get_where('devices', array('farm_id'=>$farmId));
        return $query->result_array();
    }
    
    public function getDevice($deviceId) {
        $query = $this->db->get_where('devices', array('id'=>$deviceId));
        return $query->row();
    }

    public function getDeviceByNum($deviceNum, $userid) {
        $where = "number=$deviceNum and user_id=(select id from `users` where userid = $userid)";
        
        $query = $this->db->where($where)->get('devices');
        return $query->row();
    }
    
    public function freeDevices($userid) {
        $where = "`user_id`='$userid' and ((`farm_id` is null) or (not `farm_id`  in (select id from farms where `user_id` = '$userid')))";
        $query = $this->db->where($where, NULL, FALSE)->get('devices');
        return $query->result_array();
    }
    
    public function assignDevices($deviceList, $farmId) {
        $res = NULL;
        
        $this->db->set('farm_id', '')->where('farm_id', $farmId)->update('devices');
        foreach ($deviceList as $el) {
            if(!$this->db->set('farm_id', $farmId)->where('id', $el)->update('devices')) {
                $res = $this->db->error()->message;
                break;
            }
        }
        
        return $res;
    }
    
    public function getRows($userid) {
        $this->db->select('d.*, f.name as farm')->
            from("devices d")->join("farms f","d.farm_id = f.id", 'Left')->
            where('d.user_id', $userid);
        $query = $this->db->get();
        return $query->result_array();
    }
    
    function dataFromPost($post, $fields) {
        $data = array();
        foreach ($fields as $field) {
            if(array_key_exists($field, $post)) {
                $data[$field] = $post[$field];
            }
        }
        return $data;
    }
    
    public function getError() { return $this->error; }
    
    public function insert($post, $userid) {
        $data = $this->dataFromPost($post, array('number','name','description'));
        $data['user_id'] = $userid;
        if($this->db->insert('devices', $data))
            return $this->db->insert_id();
            $this->error = $this->db->error();
            return 0;
    }
    
    public function delete($post) {
        if(array_key_exists('id', $post)) {
            $this->db->where('id', $post['id']);
            $res = $this->db->delete('devices');
            if(! $res ) {
                $this->error = $this->db->error();
            }
            return $res;
        }
        $this->error = array('message'=>'No id in params');
        return false;
    }
    
    public function update($post) {
        if(array_key_exists('id', $post)) {
            $data = $this->dataFromPost($post, array('number','name','description'));
            
            $this->db->where('id', $post['id']);
            $res = $this->db->update('devices', $data);
            if(! $res ) {
                $this->error = $this->db->error();
            }
            return $res;
        }
        $this->error = array('message'=>'No id in params');
        return false;
    }
}