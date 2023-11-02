var SRC_IP = '192.168.0.161';//'nap.servolux.by';
var SRC_PORT = 8888; //8850;
var LOGIN = "";
var PASWORD = "";

var FILE_NAME = "agent_dist.txt";

var server = new ActiveXObject('GRSoft.Server');

WScript.Echo("Connecting...");

//
// Параметры отчета 
// agents: список кодов агентов через запятую без пробелов. Пустая строка - по всем агентам
// start: строка с датой начала формат дд/мм/гггг чч:мм:сс
// end: строка с датой окончания формат дд/мм/гггг чч:мм:сс
//
// Возвращает список 
// userid - код агента
// length - расстояние в м.

//var res = server.Connect('nap.servolux.by', 8850, '247' ,'5971');//, '123', '6287'); 

var AGENTS = "Mg3,Mg2";
var start = new Date(2018, 9, 31, 0, 0, 0 );
var finish = new Date(2018, 9, 31, 23, 59, 59 );

var res = server.Connect(SRC_IP, SRC_PORT, LOGIN, PASWORD);
if( !res )
{
	WScript.Echo(server.ErrorMessage);
	WScript.Quit();
}

WScript.Echo("Connected!");

var log;
var objFSO = new ActiveXObject("Scripting.FileSystemObject");
if (objFSO.FileExists(FILE_NAME))
   log = objFSO.OpenTextFile(FILE_NAME, 8);
else
   log = objFSO.CreateTextFile(FILE_NAME);

server.Timeout = 60000;

function ToString(dig) {
   if (dig < 10)
      return "0" + dig.toString();
   return dig.toString();
}

function PaddingZero(val) {
   return val < 10 ? "0" + val : "" + val;
}

function TimeString(date) {
   return PaddingZero(date.getHours()) + ':' + PaddingZero(date.getMinutes()) + ':' + PaddingZero(date.getSeconds());
}

function DateString(sDate) {
   return ToString(sDate.getDate()) + "/" + ToString(sDate.getMonth() + 1) + "/" + sDate.getYear().toString();
}

function DateToString(sDate) {
   return  DateString(sDate) + " " + TimeString(sDate);
}

var agents = AGENTS;
if (agents.length == 0) {
   var docA = server.Get("Agents", "");
   if (!docA) {
      WScript.Echo("Error " + server.ErrorMessage);
      WScript.Quit();
   } 

   for (i = 0; i < docA.Count; i++) {
      var ag = docA.Get(i);
      if (ag.login != "") {
         if (agents.length > 0)
            agents += ",";
         agents += ag.id;
      }
   }
}

param = { agents: agents, start: DateToString(start), finish: DateToString(finish) };


var res = server.Report("gpspos", param);
if( !res )
{
	WScript.Echo("Error " + server.ErrorMessage);
	WScript.Quit();
}	

ctr = 0;
for( di=0; di<res.Count; di++ )
{
   var docs = res.Get(di);
   if (docs.Type == "Result") {
      for (i = 0; i < docs.Count; i++) {
         doc = docs.Get(i);
         log.WriteLine(doc.userid + ";" + doc.length / 1000);
      }
   }
}
