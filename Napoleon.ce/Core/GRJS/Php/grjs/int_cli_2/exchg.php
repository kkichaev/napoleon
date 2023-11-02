<?php
//error_reporting(E_ALL);

header('Content-type: text/html; charset=ISO-8859-5');

require_once 'proj_obj.php';
require_once 'db_data.php';

function HexDump($str) {
	for ($i = 0; $i < strlen($str); $i++) {
		echo str_pad(dechex(ord($str[$i])), 2, '0', STR_PAD_LEFT);
		echo " ";
	}
	print "<br>";
}

function ToInt($chr) {
	if( $chr > 0x80 )
		return $chr - 256;
	return $chr;
}

class LoginInfoData {
	public $date;
	public $success;
	
	public function __construct($values) {
		$var = explode(":", $values);
		$this->success = "false";
		if( array_count_values($var) > 1 )
			$this->success = $var[1];
		$this->date = intval($var[0]);
	}
}

class ServerConnectLogData {
	public $login;
	public $date = array();
	
	public function  __construct($name, $dates) {
		
		$this->login = $name;
		$da = explode(',', $dates);
		foreach ($da as $val) {
			$cd = new LoginInfoData($val);
			array_push($this->date, $cd); 
		}
	}
}


class ServerConnectInfo {
	private static $ENCKEY = "456E4063244B6579474472233447377700000000000000000000000000000000";
	
	public $error = FALSE;
	public $project;
	public $ip;
	public $port;
	public $timeZone;
	public $log = array();
	
	public function ReadFrom($stream, $length) {
		$this->ip = $_SERVER['REMOTE_ADDR'];
		
		while(!feof($stream)) {
			$name = stream_get_line($stream, $length, ':');
			if( $name === FALSE )
				break;
			
			switch ($name) {
				case "Project":
					$this->project = stream_get_line($stream, $length, ';');
					break;
				case "Port":
					$this->port = intval(stream_get_line($stream, $length, ';'));
					break;
				case "TimeZone":
					$this->timeZone = intval(stream_get_line($stream, $length, ';'));
					break;
				case "Log":
					$this->ReadLog($stream, $length);
					break;
			}
		}
	}
	
	public function ReadLog($stream, $length) {
		while(!feof($stream)) {
			$pos = ftell($stream);
			$char = fgetc($stream);
			if( $char == ";")
				break;
			
			fseek($stream, $pos);
			$name = stream_get_line($stream, $length, '[');
			$values = stream_get_line($stream, $length, ']');
			if( $name === FALSE || $values === FALSE )
				break;
			
			$ld = new ServerConnectLogData($name, $values);
			array_push($this->log, $ld);			
		}
	}
	
	public function ParseData($data) {
		$key = pack("H*",ServerConnectInfo::$ENCKEY);
		$dec = mcrypt_decrypt(MCRYPT_RIJNDAEL_128, $key, $data, MCRYPT_MODE_ECB);
		
		$len = ToInt(ord($dec[strlen($dec) - 5]));
		$crcLen = strlen($dec) - 16 + (($len < 0) ? 0 : $len);
				
//  		HexDump(substr($dec, strlen($dec) - 4, strlen($dec)));

 		$crcVal = 0;
		$str = substr($dec, strlen($dec) - 4,  4);
		for($i=0;$i<4;$i++)
			$crcVal|=ord($str{$i})<<$i*8;
		$crcData = crc32(substr($dec, 0, $crcLen));
		
		if($crcData != $crcVal) {
			$this->error = TRUE;
			print dechex($crcVal) . "<br/>" . dechex($crcData) . "<br/>";
		} else {		
			$len = strlen($dec) - 16 + $len;
			$str = substr($dec, 0, $len);
			
			$fp = fopen('data:text/plain,'.urlencode($str), 'rb');
			$this->ReadFrom($fp, $len);
		}
	}
	
	public function EncodeData($data) {
		
		$dataLen = strlen($data);
		$data .= '00000';
		
		$rest = ($dataLen + 5) % 16;
		if( $rest != 0 )
			while($rest++ < 16)
				$data .= chr(0);
		
		$lastBlockLen = $dataLen + 16 - strlen($data);

		$crcStr = substr($data, 0, strlen($data) - 16 + (($lastBlockLen < 0) ? 0 : $lastBlockLen) );
		//print strlen($crcStr) . '<br/>';
		
		$crc = crc32($crcStr);
		
		$lenStr = chr($lastBlockLen < 0 ? 256 + $lastBlockLen : $lastBlockLen);
		$crcStr = chr($crc & 0xFF);
		$crcStr .= chr(($crc & 0xFF00)>>8);
		$crcStr .= chr(($crc & 0xFF0000)>>16);
		$crcStr .= chr(($crc & 0xFF000000)>>24);
		
// 		print HexDump($data);
		
		$i = strlen($data) - 5;
		$data[$i++] = chr($lastBlockLen < 0 ? 256 + $lastBlockLen : $lastBlockLen);
		$data[$i++] = chr($crc & 0xFF);
		$data[$i++] = chr(($crc & 0xFF00)>>8);
		$data[$i++] = chr(($crc & 0xFF0000)>>16);
		$data[$i++] = chr(($crc & 0xFF000000)>>24);
		
// 		/echo HexDump($data);

		$key = pack("H*",ServerConnectInfo::$ENCKEY);
		return mcrypt_encrypt(MCRYPT_RIJNDAEL_128, $key, $data, MCRYPT_MODE_ECB);
	}
	
	public function PutLog($db) {
		$writed = FALSE;
		
		foreach ($this->log as $vlog) {
			$logData = new LogData();
			
			$logData->login = $vlog->login;
			$logData->ip = $this->ip;
			$logData->port = $this->port;
			$logData->project = $this->project;
			$logData->tz = $this->timeZone;
				
			foreach ($vlog->date as $date) {
				$logData->date = $date->date;
				$logData->success = $date->success;
				$logData->Write($db);
				$writed = TRUE;
			}
		}
		
		if( !$writed ) {
		
			$logData = new LogData();
			
			$logData->login = "Server connect";
			$logData->ip = $this->ip;
			$logData->port = $this->port;
			$logData->project = $this->project;
			$logData->tz = $this->timeZone;
			
			$logData->date = time();
			$logData->success = 'true';
			$logData->Write($db);
		}
	}
}


$pd = new ServerConnectInfo();
$pd->ParseData($_GET['data']);


if(!$pd->error) {
	$db = connectDB();
	mysqli_set_charset($db, 'utf8');

	try {
		$pd->PutLog($db);
	} catch (Exception $e) {
		echo 'Exception: ',  $e->getMessage(), "\n";
	}
	
	$where = "WHERE `project`='" . $pd->project . "' or `project`='' ORDER BY `project` DESC";
	$demos = BaseData::LoadData($where, 'DemoData', $db);
	if(count($demos) > 1)
		$demos = array_slice($demos,0,1);

	$where = "WHERE `project`='" . $pd->project . "'";
	$license = BaseData::LoadData($where, 'LicenseData', $db);
	foreach($license as $lc) {
		$lc->end = $lc->end * 1000;
		$lc->start = $lc->start * 1000;
	}
	
	$ld = new LicenseData();
	$tableName = "`" . TABLE_PREFIX . $ld->TableName() . "`";

	//$where = "WHERE `type` in (SELECT `type` FROM  " . $tableName . " WHERE (`project` = '' or `project`='" . $pd->project . "'))";
	$where = "WHERE `type` <> ''";
	$licenseTypes = BaseData::LoadData($where, 'LicenseTypeData', $db);
	//print $where;
 	
	mysqli_close($db);
		
	$out = "DemoData" . json_encode($demos);
	$out .= "LicenseTypeData" . json_encode($licenseTypes);   
	$out .= "LicenseData" . json_encode($license);

//print_r($licenseTypes);
//echo '<br/>' . 'Test:' . json_encode($licenseTypes) . '<br/>';
//print $out;
	
	print $pd->EncodeData($out);
	//print $out;
}
// print_r($demos); echo  "<br/>";
// print_r($license); echo "<br/>";
// print_r($licenseTypes); echo "<br/>"; 
