<?php if (! defined('BASEPATH')) exit('No direct script access allowed');

class Auth_lib
{
    private $messages = '';
    private $errors = '';
    
    public function __construct() {
        $ctrl = get_instance();
        $ctrl->load->library(array('session'));
        $ctrl->load->helper(array('cookie', 'language'));
        $ctrl->lang->load('auth');
    }
    
    public function login($login, $password, $remember=FALSE) {
        if(strcmp($login, 'admin') == 0 && strcmp($password, 'admin') == 0) {
            $userid = 'admin';
            
            $ctrl = get_instance();
            $ctrl->session->set_userdata('userid', $userid);
            $ctrl->session->set_flashdata('message', '');
            
            if($remember) 
                $this->remember_user($userid, $login, $password);
            
            $this->set_messages('login_successful');
            return true;
        }
        $this->set_errors('login_unsuccessful');
        return FALSE;
    }
    
    public function logout() {
        
        if(get_cookie('login'))
            delete_cookie('login');
        
        if(get_cookie('password'))
            delete_cookie('password');

        $ctrl = get_instance();
        $ctrl->session->sess_regenerate(TRUE);        
        
        $this->set_message('logout_successful');
        
        return TRUE;
    }
    
    public function hash($str) {
        return sha1($str);
    }
    
    public function remember_user($userid, $login, $password) {
        $expire = (60*60*24*365*2);
        set_cookie(array(
            'name'   => 'login',
            'value'  => $login,
            'expire' => $expire
        ));
    
        set_cookie(array(
            'name'   => 'password',
            'value'  => $this->hash($password),
            'expire' => $expire
        ));
    }

    public function login_remembered_user() {
        $login = get_cookie('login');
        $password = get_cookie('password');
        
        if($login == NULL || $password == NULL)
            return FALSE;
        
            return $this->login($login, $pwd);
    }
    
    public function set_errors($message) {
        $this->errors = $message;
    }
    
    public function set_messages($message) {
        $this->messages = $message;
    }
    
    public function messages() {
        return get_instance()->lang->line($this->messages);
    }

    public function errors() {
        return get_instance()->lang->line($this->errors);
    }
}
