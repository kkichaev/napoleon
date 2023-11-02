<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Device extends CI_Controller {
    public function __construct() {
        parent::__construct();
        $this->load->model('gklserver');
    }
    
    public function view($id) {
        $this->load->view('view_device', array('id'=>$id));
    }


    public function get_screen($id) {
        $screen = $this->gklserver->get_screen($id);
        if($screen == NULL) {
            echo "no signal";
        } else {
            header('Content-type: image/jpeg');
            print $screen;            
        }
    }
    
    public function event($id, $event) {
        if($event == "click") {
            $X = func_get_arg(2);
            $Y = func_get_arg(3);
            
            $screen = $this->gklserver->mouse_click($id, $X, $Y);
            if($screen == NULL) {
                echo "no signal";
            } else {
                header('Content-type: image/jpeg');
                print $screen;
            }
//             echo $id . ' click ' . ' X ' . $X . ' Y ' . $Y;
        }
    }
}