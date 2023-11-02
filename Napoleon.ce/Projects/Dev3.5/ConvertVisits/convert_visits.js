var SRC_IP = '127.0.0.1';
var SRC_PORT = 8888;

// переносим документы начина€ с этого года и мес€ца
var START_YEAR = 2018;
var START_MONTH = 12; // 1 - январь

var endDate = new Date();
var startDay = new Date(START_YEAR, START_MONTH - 1, 1);

// ћес€ц идет с 0. 0 - январь
//var endDate = new Date(2018, 5, 21, 23, 0, 0); // end 2018-06-21 23:00:00
//var startDay = new Date(2018, 5, 20, 0, 0, 0); // end 2018-06-20 23:00:00


var objFSO = new ActiveXObject("Scripting.FileSystemObject");
var log;
if (objFSO.FileExists("copy_data.log"))
   log = objFSO.OpenTextFile("copy_data.log", 8);
else
   log = objFSO.CreateTextFile("copy_data.log");

var src = new ActiveXObject('GRSoft.Server');

function Info(message) {
   var curDate = new Date();
   log.Write(curDate.toLocaleTimeString() + "." + curDate.getMilliseconds() + " ");
   log.WriteLine(message);
   WScript.Echo(message);
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
   var where = '"' + keyField + '">=' + DateToString(sDate, "00:00:00") + ' and "' + keyField + '" <=' + DateToString(eDate, "23:59:59");
   return where;
}

function SetProps(dest, src, props) {
   while (true) {
      var item = props.pop();
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

function MakeDTWhere(keyField, sDate, eDate) {
   var where = '"' + keyField + '">=' + DateToString(sDate, TimeString(sDate)) + ' and "' + keyField + '" <=' + DateToString(eDate, TimeString(eDate));
   return where;
}

function ConverVisits(docwhere) {

   // mssql >= 2012
   //var filter = 'concat(convert(varchar, date), userid) in (';

   // mssql < 2012
   //var filter = '((convert(varchar, date) + userid) in (';

   // sqlite
   var filter = '(date || userid) in (';
   var start = 1;

   var el = docwhere.pop();
   while(el != undefined) {
      if (start) start = 0;
      else filter += ",";

      filter += "'" + el + "'";

      el = docwhere.pop();
   };
   filter += ")";

   var obj = 'VisitOld';
   var destObj = 'VisitUpd';

   var srcObj = src.Get(obj, filter);
   if (!srcObj) {
      //Info("No object");
   } else {
      var countDocs = srcObj.Count;
      var countPics = 0;
      var countBytes = 0;
      if (countDocs > 0)
         Info("Get " + countDocs + " docs");

      var wrVisits = new Object();

      for (var i = 0; i < srcObj.Count; i++) {
         var needWr = 0;
         var ddoc = null;
         var dlist = null;

         var sdoc = srcObj.Get(i);
         var uid = sdoc.userid;

         var items = sdoc.items;
         for (var j = 0; j < items.Count; j++) {
            si = items.Get(j);
            if (si.smallName.length > 0) {
               //Info('Skip exists image');
               continue;
            }

            var sbin = si.id;
            if (sbin.Size > 0) {
               countPics++;
               countBytes += sbin.Size;

               if (needWr == 0) {
                  if (dlist == null) {
                     var dlist = wrVisits[uid];
                     if (dlist == null) {
                        dlist = src.New(destObj);
                        wrVisits[uid] = dlist;
                     }
                  }

                  ddoc = dlist.New();
                  SetProps(ddoc, sdoc, ['id', 'date', 'userid', 'created', 'sended', 'timeZone', 'remark']);
                  needWr = 1;
               }
               var di = ddoc.items.New();
               var bin = di.id;
               bin.SetFrom(sbin);
            }
         }
      }

      for (var key in wrVisits) {
         wrVisits[key].Write(key);
      }
      if (countPics > 0)
         Info("Pics " + countPics + ", bytes " + countBytes)
   }
}

function WriteVisits(startDay, eDate, timeDelta) {
   keyField = 'date';

   var sDate = new Date();
   sDate.setTime(eDate.getTime() - timeDelta);
   //   sDate.setDate(sDate.getDate() - dayDelta);
   while (true) {
      var filter = MakeDTWhere(keyField, sDate, eDate);
      Info('VisitPreview' + ((filter == "") ? "" : " " + filter + " ") + "...");

      var testObjs = src.Get('VisitPreview', filter);

      if (testObjs) {

         var docwhere = new Array();
         for (var di = 0; di < testObjs.Count; di++) {
            var doc = testObjs.Get(di);
            var items = doc.items;
            for (var dj = 0; dj < items.Count; dj++) {
               item = items.Get(dj);
               if (item.smallSize.length == 0) {
                  var d = new Date(doc.date);
                  var ms = d.getTime() - d.getTimezoneOffset() * 60000;
                  var val = (ms + 11644473600000) * 10000;

                  var docwh = val + doc.userid;
                  docwhere.push(docwh);
                  break;
               }
            }

            if (docwhere.length >= 50) {
               ConverVisits(docwhere);
               docwhere = new Array();
            }

         }
         if (docwhere.length > 0) {
            ConverVisits(docwhere);
            docwhere = new Array();
         }
      }

      if (sDate.getTime() < startDay.getTime())
         break;
      eDate.setTime(sDate.getTime() - 1000);
      sDate.setTime(eDate.getTime() - timeDelta);
   }
}



Info("Connecting source...");
var res = src.Connect(SRC_IP, SRC_PORT);
if (!res) {
   Info(src.ErrorMessage);
   WScript.Quit();
}
src.Timeout = 10 * 60 * 1000;

Info("Copy data...");


WriteVisits(startDay, endDate, 10 * 60 * 1000);


log.Close();