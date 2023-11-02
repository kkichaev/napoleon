<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Welcome extends CI_Controller {
    
    public function __construct() {
        parent::__construct();
    
        $this->load->library(array('session', 'auth_lib'));
        $this->load->helper(array('url','language'));
        $this->load->model('gklserver');
    }

	/**
	 * Index Page for this controller.
	 *
	 * Maps to the following URL
	 * 		http://example.com/index.php/welcome
	 *	- or -
	 * 		http://example.com/index.php/welcome/index
	 *	- or -
	 * Since this controller is set as the default controller in
	 * config/routes.php, it's displayed at http://example.com/
	 *
	 * So any other public methods not prefixed with an underscore will
	 * map to /index.php/welcome/<method_name>
	 * @see https://codeigniter.com/user_guide/general/urls.html
	 */
	public function index()
	{
	    $uid = $this->session->userid;
	    
	    if($uid == NULL) {
	        if(!$this->auth_lib->login_remembered_user())
	           redirect('auth/login', 'refresh');
	    } else {
	        $devices = $this->gklserver->get_device_list();
	        $data = array(
	            'devices' => $devices
	        );
            $this->load->view('devices', $data);
	    }
	}
}
