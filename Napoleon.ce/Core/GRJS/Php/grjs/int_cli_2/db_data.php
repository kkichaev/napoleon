<?php
define('MYSQL_DB', 'z79474_1');
define('MYSQL_HOST', 'mysqlserver');
define('MYSQL_LOGIN', 'z79474_1');
define('MYSQL_PWD', 'Base1B');

function connectDB() {
    $db = mysqli_connect(MYSQL_HOST, MYSQL_LOGIN, MYSQL_PWD, MYSQL_DB);
    if (!$db) {
        die('Ошибка соединения: ' . mysqli_error());
    }
    
    return $db;
}