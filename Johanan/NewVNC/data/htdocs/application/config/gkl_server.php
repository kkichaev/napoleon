<?php
defined('BASEPATH') OR exit('No direct script access allowed');

// $config['server_address'] = 'control.egg-count.com';
// $config['server_port'] = 7654;

if(strcmp($_SERVER['SERVER_NAME'], 'localhost') == 0) {
    $protocol = isset($_SERVER['HTTPS']) && $_SERVER['HTTPS'] != 'off' ? 'https://' : 'http://';
    $config['server_local_address'] = '192.168.0.161';
    $config['tunnel'] = $protocol . "212.232.41.126/gklconnect/gklconnect";
} else {
    $protocol = isset($_SERVER['HTTPS']) && $_SERVER['HTTPS'] != 'off' ? 'https://' : 'http://';
    $config['server_local_address'] = '127.0.0.1';
    $config['tunnel'] = $protocol . $_SERVER['HTTP_HOST'] ."/gklconnect/gklconnect";
}


$config['server_address'] = $_SERVER['SERVER_NAME'];
$config['server_port'] = 7654;
