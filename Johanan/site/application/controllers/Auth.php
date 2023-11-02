<?php

defined('BASEPATH') or exit('No direct script access allowed');

class Auth extends CI_Controller
{

    public function __construct()
    {
        parent::__construct();
        
        $this->load->library(array('form_validation','session','auth_lib'));
        $this->load->helper(array('url','language'));
        $this->lang->load('auth');
    }

    public function login()
    {
        $this->form_validation->set_rules('identity', str_replace(':', '', $this->lang->line('login_identity_label')), 'required');
        $this->form_validation->set_rules('password', str_replace(':', '', $this->lang->line('login_password_label')), 'required');
        
        if ($this->form_validation->run() == true) {
            // check to see if the user is logging in
            // check for "remember me"
            $remember = (bool) $this->input->post('remember');
            
            if ($this->auth_lib->login($this->input->post('identity'), $this->input->post('password'), $remember)) {
                // if the login is successful
                // redirect them back to the home page
                $this->session->set_flashdata('message', $this->auth_lib->messages());
                redirect('/', 'refresh');
            } else {
                // if the login was un-successful
                // redirect them back to the login page
                $this->session->set_flashdata('message', $this->auth_lib->errors());
                redirect('auth/login', 'refresh'); // use redirects instead of loading views for compatibility with MY_Controller libraries
            }
        } else {
            $data = array();
            
            // the user is not logging in so display the login page
            // set the flash data error message if there is one
            $data['message'] = (validation_errors()) ? validation_errors() : $this->session->flashdata('message');
            
            $data['identity'] = array(
                'name' => 'identity',
                'id' => 'identity',
                'type' => 'text',
                'value' => $this->form_validation->set_value('identity')
            );
            $data['password'] = array(
                'name' => 'password',
                'id' => 'password',
                'type' => 'password'
            );
            
            $this->load->view('auth', $data);
        }
    }
}