var SRC_IP = '127.0.0.1';
var SRC_PORT = 8888;

var DEST_IP = '127.0.0.1';
var DEST_PORT = 8889;

// переносим документы начина€ с этого года и мес€ца
var START_YEAR = 2016;
var START_MONTH = 1; // 1 - январь

var objFSO = new ActiveXObject("Scripting.FileSystemObject");
var log;
if( objFSO.FileExists("copy_data.log") )
	log = objFSO.OpenTextFile("copy_data.log", 8);
else
	log = objFSO.CreateTextFile("copy_data.log");

var src = new ActiveXObject('GRSoft.Server');
var dest = new ActiveXObject('GRSoft.Server');

function Info(message) {
	curDate = new Date();
	log.Write(curDate.toLocaleTimeString() + "." + curDate.getMilliseconds() + " ");
	log.WriteLine(message);
	WScript.Echo(message);
}

function WriteObject(obj, filter) {
   Info(obj + ((filter == "") ? "" : " " + filter + " ") +  "...");

   var srcObj = src.Get(obj, filter);
   if( !srcObj ) {
      Info("No object");
   } else {
      Info("Get " + srcObj.Count + " objects");

      dest.New(obj);
      dest.Write(srcObj);
   }
}

function WriteObjects(objects, filter) {
	for( key in objects ) {
	   var obj = objects[key];
	   WriteObject(obj, filter);
	}
}

function ToString(dig) {
   if (dig < 10)
      return "0" + dig.toString();
   return dig.toString();
}

function DateToString(sDate, sTime) {
   return 'ToDate("' + ToString(sDate.getDate()) + "/" + ToString(sDate.getMonth() + 1) + "/" + sDate.getYear().toString() + " " + sTime + '")';
}

function MakeWhere(keyField, sDate, eDate) {
   where = '"' + keyField + '">=' + DateToString(sDate, "00:00:00") + ' and "' + keyField + '" <=' + DateToString(eDate, "23:59:59");
   return where;
}

function WriteDocs(objects, keyField, startDay, dayDelta) {
   var eDate = new Date();
   var sDate = new Date();
   sDate.setTime(eDate.getTime());
   sDate.setDate(sDate.getDate() - dayDelta);
   while (true) {
      var where = MakeWhere(keyField, sDate, eDate);
      WScript.Echo(where);

      WriteObject(objects, where);

      if (sDate.getTime() < startDay.getTime())
         break;
      eDate.setTime(sDate.getTime());
      eDate.setDate(eDate.getDate() - 1);
      sDate.setTime(eDate.getTime());
      sDate.setDate(sDate.getDate() - dayDelta);
   }
}


Info("Connecting source...");
var res = src.Connect(SRC_IP, SRC_PORT);
if( !res )
{
	Info(src.ErrorMessage);
	WScript.Quit();
}
src.Timeout = 10 * 60 * 1000;

Info("Connecting dest...");
res = dest.Connect(DEST_IP, DEST_PORT);
if( !res )
{
	Info(dest.ErrorMessage);
	WScript.Quit();
}
dest.Timeout = 10 * 60 * 1000;

Info("Copy data...");

var createdDocs = ["CopyIncass", "CopyOrder", "CopyOrgRemnants", 'CopyReturns', 'CopyProcuration', 'CopyCommonIncass'];
var dateDocs = ["CopyGPSPos", 'CopyUserLog'];
var objects = ["AgentMatrix", "ColorsTable", "Division", "DivisionManager", 'PotenzialOrg', 'SyncInfo', 'UserInfo','OrgFolder'];
var filterObjects = ['ServerConfig'];

var createdDocs = [];
var dateDocs = [];
var objects = [];
var filterObjects = ['OrgFolder'];

WriteObjects(filterObjects, '"userid" is null or not "userid" is null');

WriteObjects(objects, "");

Info("Copy documents...");

var startDay = new Date(START_YEAR, START_MONTH - 1, 1);
var dayDelta = 30; // кол-во дней в запросе

for (key in createdDocs)
   WriteDocs(createdDocs[key], 'created', startDay, dayDelta);

for (key in dateDocs)
   WriteDocs(dateDocs[key], 'date', startDay, dayDelta);

WriteDocs('CopyVisit', 'date', startDay, 5);

log.Close();