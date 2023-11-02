<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Welcome extends CI_Controller {

    public function __construct()
    {
        parent::__construct();
        $this->load->library('ion_auth');
        $this->lang->load('user');
    }
    
    public function index()
	{
	    if (!$this->ion_auth->logged_in())
	    {
	        // redirect them to the login page
	        redirect('auth/login', 'refresh');
	    } else if( $this->ion_auth->is_admin() ) {
	        redirect('admin', 'refresh');
	    } else {
	        redirect('user', 'refresh');
	    }
	}
}
