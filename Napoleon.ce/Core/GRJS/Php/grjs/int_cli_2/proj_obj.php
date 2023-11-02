<?php

define('TABLE_PREFIX', "grd_");

abstract class BaseData {
	abstract public function TableName();
	
	protected function GetAssocDBFields() { return array(); }
	

	public function MakeSelectStmt($addStr) {
		$stmt = "SELECT ";
		
//		$props = call_user_func('get_object_vars', $this);
		$ro = new ReflectionObject($this);
		$props = $ro->getProperties(ReflectionProperty::IS_PUBLIC);
		$assoc = $this->GetAssocDBFields();
		
		foreach ($props as $prop) {
			$name = $prop->getName();
//			$name = $prop;    
			if(!array_key_exists($name, $assoc))
				$stmt .= "`" . $name . "`,";
			else
				$stmt .= "`" . $assoc[$name] . "` AS `" . $name . "`,";
		}
		
		$stmt = chop($stmt, ",") . " FROM `" . TABLE_PREFIX . $this->TableName() . "`";
		if( $addStr != "" )
			$stmt .= " " . $addStr;
		
		return $stmt;
		
	}

	public static function LoadData($addStr, $className, $db) {
		$ret = array();
		
		$var = new $className;
		$stmt = $var->MakeSelectStmt($addStr);	
		$fields = get_object_vars($var);
				
		if( $res = mysqli_query($db, $stmt) ) {
			while( $row = mysqli_fetch_array($res) ) {
				$item = new $className;
				foreach ($fields as $k => $v) {
					foreach ($row as $rk => $rv) {
						if( strtolower($k) == strtolower($rk)) {
							$item->$k = $rv;
							break;
						}
					}
				}
				array_push($ret, $item);
			}
		}
		
		return $ret;
	}
}

class LicenseTypeData extends BaseData {
	public $type;
	public $title;
	public $forAgents;

	protected function GetAssocDBFields() { return array('forAgents' => 'for_agents'); }
	
	public function TableName() { return "license_types"; } 
}

class LicenseData extends BaseData {
	public $id;
	public $type;
	public $project;
	public $count;
	public $start;
	public $end;
	
	public function TableName() { return "licenses"; } 
}

class DemoData extends BaseData {
	public $id;
	public $type;
	public $project;
	public $allowCount;
	public $timespan;

	protected function GetAssocDBFields() { return array('allowCount' => 'allow_count'); }
	
	public function TableName() { return "demos"; } 
}

class LogData extends BaseData {
	public $project;
	public $login;
	public $ip;
	public $port;
	public $date;
	public $success;
	public $tz;
	public $sended;

	public function TableName() { return "log"; } 
	
	public function Write($db) {
		$stmt = "INSERT INTO `" . TABLE_PREFIX . $this->TableName() . "` (";
		$values = " VALUES (";
		
		$this->sended = time();


		$ro = new ReflectionObject($this);		
		$props = $ro->getProperties(ReflectionProperty::IS_PUBLIC);
//		$props = call_user_func('get_object_vars', $this);
		$assoc = $this->GetAssocDBFields();
		
		foreach ($props as $prop) {
//			$name = $prop;
			$name = $prop->getName();
			$val = $this->$name;
			if(is_null($val))
				continue;
			
			if(is_string($val))
				$values .= "'" . $val . "'";
			else 
				$values .= (string)$val;
			$values .= ",";
			 
			if(!array_key_exists($name, $assoc)) {
				$stmt .= "`" . $name . "`,";
			} else {
				$stmt .= "`" . $assoc[$name] . "`,";
			}
		}
		
		$stmt = rtrim($stmt, ",") . " ) " . rtrim($values, ",") . ")";
		return mysqli_query($db, $stmt);
	}
}