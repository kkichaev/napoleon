<?php
class Admin extends CI_Controller
{
    public function __construct()
    {
        parent::__construct();
        $this->load->helper(['url', 'language']);
        $this->lang->load('admin');
    
        $this->load->library('ion_auth');
        if (!$this->ion_auth->logged_in())
            redirect('auth/login', 'refresh');
    }
    
    public function index() {
        $this->load->view('admin/main');
    }
    
    
    public function manageUsers() {
        redirect('auth/index');
    }
}