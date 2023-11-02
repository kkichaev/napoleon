var SRC_IP = 'nap.servolux.by';
var SRC_PORT = 8850;
var LOGIN = "";
var PASWORD = "";

//
// Параметры отчета 
// agents: список кодов агентов через запятую без пробелов. Пустая строка - по всем агентам
// start: строка с датой начала формат дд/мм/гггг 
// end: строка с датой окончания формат дд/мм/гггг
//
// Возвращает список 
// userid - код агента
// values - список параметров (id - код параметра, value - значение)
// changes - список измененный параметров  за интервал(date - дата и время изменения, id - код параметра, oldValue - старое значение, newValue - новое значение)

var AGENTS = "";
var start = new Date(2019, 4, 28, 0, 0, 0);
var finish = new Date();

var FILE_NAME = "agent_pda_settings.txt";

var server = new ActiveXObject('GRSoft.Server');
var objFSO = new ActiveXObject("Scripting.FileSystemObject");
var log;
if (objFSO.FileExists(FILE_NAME))
   log = objFSO.OpenTextFile(FILE_NAME, 8);
else
   log = objFSO.CreateTextFile(FILE_NAME);

WScript.Echo("Connecting...");

var res = server.Connect(SRC_IP, SRC_PORT, LOGIN, PASWORD);
if( !res )
{
	WScript.Echo(server.ErrorMessage);
	WScript.Quit();
}

WScript.Echo("Connected!");

server.Timeout = 60000;

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
   return DateString(sDate) + " " + TimeString(sDate);
}

param = { agents: agents, start: DateToString(start), finish: DateToString(finish) };
var res = server.Report("pda_settings", param);
if (!res)
{
	WScript.Echo("Error " + server.ErrorMessage);
	WScript.Quit();
}	

WScript.Echo("Get object collection with " + res.Count + " elements");
ctr = 0;
for( di=0; di<res.Count; di++ )
{
   var docs = res.Get(di);
   if (docs.Type == "Result") {
         for (i = 0; i < docs.Count; i++) {
         doc = docs.Get(i);

         log.WriteLine("'" + doc.userid + "' start");
         for (var j = 0; j < doc.values.Count; j++) {
            var vi = doc.values.Get(j);
            log.WriteLine(vi.id + " " + vi.value);
         }

         for( var j=0; j<doc.changes.Count; j++) {
            var ci = doc.changes.Get(j);
            log.WriteLine(ci.date + " " + ci.id + " " + ci.prevValue + " -> " + ci.newValue);
         }
         log.WriteLine("'" + doc.userid + "' end");
      }
   }
}
