var SRC_IP = '127.0.0.1';
var SRC_PORT = 8888;

var DEST_IP = '192.168.1.50';
var DEST_PORT = 8889;

// переносим документы начина€ с этого года и мес€ца
var START_YEAR = 2017;
var START_MONTH = 2; // 1 - январь

var objFSO = new ActiveXObject("Scripting.FileSystemObject");
var log;
if (objFSO.FileExists("copy_data.log"))
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
   Info(obj + ((filter == "") ? "" : " " + filter + " ") + "...");

   var srcObj = src.Get(obj, filter);
   if (!srcObj) {
      Info("No object");
   } else {
      Info("Get " + srcObj.Count + " objects");

      dest.New(obj);
      dest.Write(srcObj);
   }
}

function WriteObjects(objects, filter) {
   for (key in objects) {
      var obj = objects[key];
      WriteObject(obj, filter);
   }
}

function ToString(dig) {
   if (dig < 10)
      return "0" + dig.toString();
   return dig.toString();
}

function TimeString(date) {
   return PaddingZero(date.getHours()) + ':' + PaddingZero(date.getMinutes()) + ':' + PaddingZero(date.getSeconds());
}

function DateToString(sDate, sTime) {
   return 'ToDate("' + ToString(sDate.getDate()) + "/" + ToString(sDate.getMonth() + 1) + "/" + sDate.getYear().toString() + " " + sTime + '")';
}

function MakeWhere(keyField, sDate, eDate) {
   where = '"' + keyField + '">=' + DateToString(sDate, "00:00:00") + ' and "' + keyField + '" <=' + DateToString(eDate, "23:59:59");
   return where;
}

function MakeDTWhere(keyField, sDate, eDate) {
   where = '"' + keyField + '">=' + DateToString(sDate, TimeString(sDate)) + ' and "' + keyField + '" <=' + DateToString(eDate, TimeString(eDate));
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

function SetProps(dest, src, props) {
   while (true) {
      item = props.pop();
      if (!item)
         break;

      dest[item] = src[item];
   }
}

function PaddingZero(val) {
   return val < 10 ? "0" + val : "" + val;
}

function ToStringDate(date) {
   return date.getFullYear() + PaddingZero(date.getMonth() + 1) + PaddingZero(date.getDate()) + PaddingZero(date.getHours()) + PaddingZero(date.getMinutes()) + PaddingZero(date.getSeconds());
}

function CreateFolder(folder) {
   if (folder.length == 0)
      return;
   if (!objFSO.FolderExists(folder)) {
      CreateFolder(objFSO.GetParentFolderName(folder));
      objFSO.CreateFolder(folder);
   }
}

function WriteVisits(startDay, eDate, timeDelta) {
   obj = 'CopyVisit';
   objShell = new ActiveXObject("Wscript.Shell");

   keyField = 'date';

   var sDate = new Date();
   sDate.setTime(eDate.getTime() - timeDelta);
   //   sDate.setDate(sDate.getDate() - dayDelta);
   while (true) {
      var filter = MakeDTWhere(keyField, sDate, eDate);
      Info(obj + ((filter == "") ? "" : " " + filter + " ") + "...");

      var srcObj = src.Get(obj, filter);
      if (!srcObj) {
         Info("No object");
      } else {
         Info("Get " + srcObj.Count + " objects");

         dlist = dest.New(obj);
         for (i = 0; i < srcObj.Count; i++) {
            ddoc = dlist.New();
            sdoc = srcObj.Get(i);

            SetProps(ddoc, sdoc, ['id', 'date', 'userid', 'created', 'sended', 'timeZone', 'remark']);
            items = sdoc.items;
            for (j = 0; j < items.Count; j++) {
               si = items.Get(j);
               if (si.id.Size == 0)
                  continue;

               fileName = "uid" + sdoc.userid.replace(/[^a-z0-9]/gi, '_') + "\\oid" + sdoc.id.replace(/[^a-z0-9]/gi, '_');
               destFile = "agents\\" + fileName;
               //WScript.Echo(fileName);
               CreateFolder(destFile);

               fileName += "\\" + ToStringDate(new Date(sdoc.date)) + j + ".jpeg";
               destFile = "agents\\" + fileName;
               si.id.Write(destFile);
               di = ddoc.items.New();

               di.name = fileName;
            }
         }
         dest.Write(dlist);
      }

      if (sDate.getTime() < startDay.getTime())
         break;
      eDate.setTime(sDate.getTime() - 1000);
      sDate.setTime(eDate.getTime() - timeDelta);
      //eDate.setDate(eDate.getDate() - 1);
      //sDate.setTime(eDate.getTime());
      //sDate.setDate(sDate.getDate() - dayDelta);
   }
}



Info("Connecting source...");
var res = src.Connect(SRC_IP, SRC_PORT);
if (!res) {
   Info(src.ErrorMessage);
   WScript.Quit();
}
src.Timeout = 10 * 60 * 1000;

Info("Connecting dest...");
res = dest.Connect(DEST_IP, DEST_PORT);
if (!res) {
   Info(dest.ErrorMessage);
   WScript.Quit();
}
dest.Timeout = 10 * 60 * 1000;

Info("Copy data...");

var createdDocs = ["CopyIncass", "CopyOrder", "CopyOrgRemnants", 'CopyReturns'];
var dateDocs = ["CopyGPSPos", 'CopyUserLog'];
var objects = ["AgentMatrix", "ColorsTable", "Division", "DivisionManager", 'SyncInfo', 'UserInfo'];

//WriteObject('PotenzialOrg', 'not "userid" is null');
//WriteObject('ServerConfig', '"userid" is null or not "userid" is null');
//WriteObjects(objects, "");

//Info("Copy documents...");

var startDay = new Date(START_YEAR, START_MONTH - 1, 1);
var dayDelta = 30; // кол-во дней в запросе

//for (key in createdDocs)
//   WriteDocs(createdDocs[key], 'created', startDay, dayDelta);

//for (key in dateDocs)
//   WriteDocs(dateDocs[key], 'date', startDay, dayDelta);

endDate = new Date(2017, 6, 20, 16, 3, 13);
WriteVisits(startDay, endDate, 30 * 60 * 1000);

log.Close();