<?php
class History extends CI_Model {
    function __construct() {
        parent::__construct();
        $this->load->database();
        $this->db->db_debug = FALSE;
    }
    
    public function getHistory($deviceId) {
        $query = $this->db->get_where('device_history_request', array('device_id'=>$deviceId));
        return $query->row();
    }
    
    function dateToTimestamp($date) {
        $dt = DateTime::createFromFormat("Ymd", $date);
        return $dt->getTimestamp();
    }
    
    public function putRequest($deviceId, $start, $end) {
        $data = [
            'device_id' => $deviceId,
            'hist_start' => $this->dateToTimestamp($start),
            'hist_end' => $this->dateToTimestamp($end),
            'started' => time(),
            'lines' => NULL,
            'finished' => NULL,
            'cur_line' => NULL,
            'file_name' => NULL,
            'remark' => NULL,
        ];
        return $this->db->replace('device_history_request', $data);
    }
}