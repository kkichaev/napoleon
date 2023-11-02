//var SRC_IP = '192.168.0.161';//'nap.servolux.by';
//var SRC_PORT = 8888; //8850;
var SRC_IP = 'nap.servolux.by';
var SRC_PORT = 8850;
var LOGIN = "";
var PASWORD = "";

var USERID = 'm1'; // задаем менеджера

// в датах месяц идет с нуля, так что 10 - это ноябрь
var endDate = new Date(2019, 04, 31, 23, 0, 0); // end 2019-05-31 23:00:00
var startDate = new Date(2019, 04, 30, 0, 0, 0); // start 2019-05-30 0:00:00

var FILE_NAME = "log_mgr.txt";

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

function PartName(partIndex) {
   if (partIndex == 0) return "ДопТП";
   if (partIndex == 1) return "Дисп";
   if (partIndex == 2) return "Мерч";
   if (partIndex == 3) return "МерчДоп";
   if (partIndex == 4) return "Цикл";
   if (partIndex == 5) return "Пн";
   if (partIndex == 6) return "Вт";
   if (partIndex == 7) return "Ср";
   if (partIndex == 8) return "Чт";
   if (partIndex == 9) return "Пт";
   if (partIndex == 10) return "Сб";
   if (partIndex == 11) return "Вс";
   return partIndex;
}

function ValueToString(partIndex, value) {
   if (partIndex == 4) {
      if (value == "0") return "";
      if (value == "1") return "Нечет.";
      if (value == "2") return "Чет.";
      if (value == "3") return "2";
      if (value == "5") return "4";
   }
   if (partIndex >= 5 && partIndex <= 11) {
      if (value == "0") return "";
      if (value == "1") return "В";
      if (value == "2") return "ВМ";
      if (value == "3") return "М";
      if (value == "4") return "Д";
      if (value == "5") return "ДМ";
      if (value == "6") return "m";
      if (value == "7") return "ВД";
      if (value == "8") return "З";
   }
   return value;
}

function SheduleValueToStr(oldV, newV) {
   var oldStr = "";
   var newStr = "";
   var oldP = oldV.split(",");
   var newP = newV.split(",");

   for (var ci = 0; ci < oldP.length; ci++) {
      if (oldP[ci] != newP[ci]) {
         var pn = PartName(ci);
         if (oldStr.length > 0) {
            oldStr += ",";
            newStr += ",";
         }
         oldStr += pn + ":" + ValueToString(ci, oldP[ci]);
         newStr += pn + ":" + ValueToString(ci, newP[ci]);
      }
   }


   return oldStr + ";" + newStr;
}

function CreateLog(where) {
   docs = server.Get("ManagerLog", where);
   //Say(docName + ' ' + where);
   if (!docs)
      return;

   log.WriteLine("Код менеджера;Дата изменения;Код Агента;Код контрагента/Тип продукции;Тип изменения;Старое значение;Новое значение;Код товара");
   for (var i = 0; i < docs.Count; i++) {
      doc = docs.Get(i);

      var type = "";


      var line = doc.userid + ";" + doc.created + ";" + doc.agentid + ";" + doc.id + ";";
      var values = doc.oldValue + ";" + doc.newValue;

      if (doc.type == "Shedule") {
         type = "Изменение маршрута";
         values = SheduleValueToStr(doc.oldValue, doc.newValue);
      } else if (doc.type == "ReturnLimit") {
         type = "Изменение лимита возврата";
      } else if (doc.type == "ChangeOrders") {
         type = "Подрезка заказов";
      }
      line += type + ";" + values + ";" + doc.item;


      log.WriteLine(line);
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
Say("Load log...");
CreateLog(where);
