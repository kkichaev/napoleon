<?php

class Farm extends CI_Model {
    var $error;
    
   function __construct() {
       parent::__construct();
       $this->load->database();
       $this->db->db_debug = FALSE;
   }
   
   public function countTotals() {
       return $this->db->count_all("farms");       
   }
   
   public function load($id) {
       $query = $this->db->where('id', $id)->get("farms");
       return $query->row();
   }
   
   public function getRows($userid) {
       $query = $this->db->where('user_id', $userid)->get("farms");
       return $query->result_array();
   }
   
   public function browse($userid, $dedInterval) {
       $this->db->select('count(dc.device_id) as devices, f.name as name, f.id as id, if(count(dc.device_id) < count(d.id), 1, ifnull(max(dc.alarm), 1)) as alarm ')->
            from('farms f, devices d')->
            join('(select device_id, alarm from device_connects dc where dc.time > unix_timestamp()-'.$dedInterval.') dc', 'dc.device_id = d.id', 'left')->
            where("d.farm_id = f.id and f.user_id = '" . $userid . "'")->
            group_by('f.id');
       //print $this->db->get_compiled_select();
       $query = $this->db->get();
       return $query->result_array();
   }
   
   public function browseFarm($farmId) {
       $this->db->select("d.id, d.name, d.number, ifnull(dc.time, 0) as time, ifnull(dc.alarm,1) as alarm,'' as humidity, '' as temp")->
           from('devices d')->
           join('device_connects dc', 'dc.device_id = d.id', 'left')->
           where("d.farm_id = " . $farmId );
//        print $this->db->get_compiled_select();
       $query = $this->db->get();
       return $query->result_array();
   }
   
   public function farmDeviceValues($farmId) {
       $this->db->select("d.id as id, dc.param as param, dc.value as value")->
       from('devices d')->
       join('device_connects_data dc', 'dc.device_id = d.id', 'left')->
       where("d.farm_id = " . $farmId );
       //        print $this->db->get_compiled_select();
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
       $data = $this->dataFromPost($post, array('name','description'));
       $data['user_id'] = $userid;
       if($this->db->insert('farms', $data))
           return $this->db->insert_id();
       $this->error = $this->db->error();
       return 0;
   }
   
   public function delete($post) {
       if(array_key_exists('id', $post)) {
           $this->db->where('id', $post['id']);
           $res = $this->db->delete('farms');
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
           $data = $this->dataFromPost($post, array('name','description'));
           
           $this->db->where('id', $post['id']);
           $res = $this->db->update('farms', $data);
           if(! $res ) {
               $this->error = $this->db->error();
           }
           return $res;
       }
       $this->error = array('message'=>'No id in params');
       return false;
   }
}