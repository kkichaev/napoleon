var SRC_IP = 'nap.servolux.by';
var SRC_PORT = 8850;
var LOGIN = "";
var PASWORD = "";

var USERID = 'Мн4'; // пустой userid - будет делать для всех

// в датах месяц идет с нуля, так что 10 - это ноябрь
var endDate = new Date(2020, 03, 13, 23, 0, 0); // end 2018-11-22 23:00:00
var startDate = new Date(2020, 03, 13, 0, 0, 0); // start 2018-11-22 0:00:00

var FILE_NAME = "log.txt";

// формат файла лога
// userid,дата время;действие;документ или описание;доп информация

var server = new ActiveXObject('GRSoft.Server');
var objFSO = new ActiveXObject("Scripting.FileSystemObject");
var log;
if (objFSO.FileExists(FILE_NAME))
   log = objFSO.OpenTextFile(FILE_NAME, 8);
else
   log = objFSO.CreateTextFile(FILE_NAME);


function Say(msg) {
   WScript.Echo(msg);
}

function PutLog(userid, date, action, docInfo, remark) {
   log.WriteLine(userid + ';' + date + ';' + action + ';' + docInfo + ';' + remark);
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

function DateToString(sDate, sTime) {
   return 'ToDate("' + DateString(sDate) + " " + sTime + '")';
}

function MakeDateWhere(keyField, sDate, eDate) {
   where = '"' + keyField + '">=' + DateToString(sDate, TimeString(sDate)) + ' and "' + keyField + '" <=' + DateToString(eDate, TimeString(eDate));
   return where;
}

function MakeWhere(uid, keyField, sDate, eDate) {
   where = MakeDateWhere(keyField, sDate, eDate);
   if (uid.length > 0)
      where += ' and "userid" in (' + "'" + uid + "')";
   return where;
}

function DTString(srvDate) {
   dateS = new Date(srvDate);
   return DateString(dateS) + ' ' + TimeString(dateS);
}

function CreateDocsLog(docName, title, where) {
   docs = server.Get(docName, where);
   //Say(docName + ' ' + where);
   if (!docs)
      return;
   for (i = 0; i < docs.Count; i++) {
      doc = docs.Get(i);
      PutLog(doc.userid, DTString(doc.created), 'Создание', title, DTString(doc.created) + " '" + doc.id + "'");
      PutLog(doc.userid, DTString(doc.sended), 'Отправка', title, DTString(doc.created));
      if(doc.editPostSend != null ) {
         d1 = new Date(doc.editPostSend);
         d2 = new Date(doc.created);
         if(d1.getTime() != d2.getTime())
            PutLog(doc.userid, DTString(doc.editPostSend), 'Изменение', title, DTString(doc.created));
      }
   }
}

function LoadWorkTime() {
   where = MakeWhere(USERID, 'start', startDate, endDate);

   docs = server.Get('WorkTime', where);
   if (!docs)
      return;
   for (i = 0; i < docs.Count; i++) {
      doc = docs.Get(i);
      PutLog(doc.userid, DTString(doc.start), 'Начало работы', doc.id, '');
      PutLog(doc.userid, DTString(doc.stop), 'Завершение работы', doc.id, '');
   }
}

function LoadGPSPos() {
   where = MakeWhere(USERID, 'date', startDate, endDate);

   docs = server.Get('GPSPos', where);
   if (!docs)
      return;
   for (i = 0; i < docs.Count; i++) {
      doc = docs.Get(i);
      var info = '';
      info = doc.isGSM > 0 ? "GSM" : "GPS";
      info += ",stl:" + doc.satellite;
      info += ",точн.:" + doc.accuracy;
      info += ",скор.:" + doc.speed;
      if (doc.isMock)
         info += ",Fake GPS";
      PutLog(doc.userid, DTString(doc.date), 'Координаты', doc.longitude + ';' + doc.latitude, info);
   }
}

function LogActions(action) {
   if (action == 1) return "GPS - Включен";
   if (action == 2) return "GPS - Выключен";
   if (action == 3) return "Время изменено";
   if (action == 4) return "КПК - Включен";
   if (action == 5) return "КПК - Выключен";
   if (action == 6) return "Сбой программы";
   if (action == 7) return "Наполеон - Запуск";
   if (action == 8) return "Наполеон - Выход";
   if (action == 9) return "КПК статус:";
   if (action == 10) return "Фоновая синхронизация";
   if (action == 11) return "Очистка базы";
   if (action == 12) return "Фоновая синхронизация - прайс";
   return "";
}

function LoadUserLog() {
   where = MakeWhere(USERID, 'date', startDate, endDate);

   docs = server.Get('UserLog', where);
   if (!docs)
      return;
   for (i = 0; i < docs.Count; i++) {
      doc = docs.Get(i);
      act = LogActions(doc.action);
      if (doc.action == 0)
         act = 'send:' + doc.objType
      PutLog(doc.userid, DTString(doc.date), act, doc.comments, '');
   }
}

CLEAR = 1;
GEN_DATA = 2;
DOCS = 4;
VISIT = 8;
INCASS = 16;
PRESENT = 32;
COST = 64;
DEBT = 128;
RESTORE = 256;

function SyncFlags(flags) {
   ret = "";


   if ((flags & CLEAR) != 0) ret += "очистка базы,";
   if ((flags & GEN_DATA) != 0) ret += "осн.данные,";
   if ((flags & DOCS) != 0) ret += "документы,";
   if ((flags & VISIT) != 0) ret += "посещения,";
   //if ((flags & INCASS) != 0) ret += "инкассации,";
   if ((flags & PRESENT) != 0) ret += "фото товара,";
   if ((flags & COST) != 0) ret += "цены,";
   if ((flags & DEBT) != 0) ret += "долги,";
   if ((flags & RESTORE) != 0) ret += "восстановление док.,";

   if (ret.length > 0)
      ret = ret.substr(0, ret.length - 1);

   return ret;
}

function LoadSyncData() {
   where = MakeWhere(USERID, 'created', startDate, endDate);

   docs = server.Get('SyncInfo', where);
   if (!docs)
      return;
   for (i = 0; i < docs.Count; i++) {
      doc = docs.Get(i);
      act = SyncFlags(doc.syncparam);
      PutLog(doc.userid, DTString(doc.created), act, doc.deviceID, '');
   }
}


Say("Connecting...")
var res = server.Connect(SRC_IP, SRC_PORT, LOGIN, PASWORD);
if (!res)
{
   Say(server.ErrorMessage);
   WScript.Quit();
}

where = MakeWhere(USERID, 'created', startDate, endDate);
Say("Load orders...")
CreateDocsLog('Order', 'Заявка', where);

Say("Load returns...")
CreateDocsLog('ReturnRequest', 'Заявка на возврат', where);

Say("Load remnants...")
CreateDocsLog('OrgRemnants', 'Съем остатков', where);

Say("Load scripts...")
CreateDocsLog('ScriptDoc', 'Сценарий', where);

Say("Load visits...")
where = MakeWhere(USERID, 'date', startDate, endDate);
CreateDocsLog('VisitInfo', 'Посещение', where);

Say("Load work time...")
LoadWorkTime();

Say("Load gps...")
LoadGPSPos();

Say("Load userlog...")
LoadUserLog();

Say("Load sync data...")
LoadSyncData();