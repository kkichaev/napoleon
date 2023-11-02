//
// Адрес сервера
//
var SERVER_ADDR = '188.68.213.171';

//
// Порт сервера
// 
var SERVER_PORT = 8877;

//
// Наименование сценария
//
var SCCRIPT_NAME = 'Только Пятёрочка !! Опрос по выкладке ЧЛ и ПХ. ';


//
// Дата и время интервала выбора документов
//
var DATE_START = "01/01/2020 10:00:00";
var DATE_END   = "01/02/2021 15:00:00";

//
// первод даты и времени в строку для запроса "31/12/2020 12:12:12"  - дата, месяц, год полный, часы, минуты, секунды. В строке должны быть ведущие нули: 01/01/2021 00:00:00
// 
function DateToString(date) {
   function Padding0(val) { return val < 10 ? '0' + val : '' + val; }

   return Padding0(date.getDate()) + "/" + Padding0(date.getMonth()+1) + "/" + date.getFullYear() + " " + Padding0(date.getHours()) + ":" + Padding0(date.getMinutes()) + ":" + Padding0(date.getSeconds());
}

//
// выводим документ и его фото
//
function printPhotoData(visit) {
   // created - дата и время создания документа
   WScript.Echo("Посещение от " + visit.created);

   // id - код точки
   WScript.Echo("Код точки " + visit.id);

   // userid - код агента
   WScript.Echo("Код агента " + visit.userid);

   // выводим фото
   var items = visit.items;
   for (var i = 0; i < items.Count; i++) {
      // путь до фото
      var photoSrc = items.Get(i).name;

      // добавляем его к адресу сервера
      var href = 'http://' + SERVER_ADDR + ':' + SERVER_PORT + '/' + photoSrc;

      WScript.Echo("Адрес фото");
      WScript.Echo(href);
   }
}

var server = new ActiveXObject('GRSoft.Server');

// Подключение к серверу
WScript.Echo("Connecting...");
var res = server.Connect(SERVER_ADDR, SERVER_PORT);
if (!res) {
   WScript.Echo(server.ErrorMessage);
   WScript.Quit();
}

WScript.Echo("Connected!");


// получаем ID сценария
var docs = server.Get("ScriptDef", "name='" + SCCRIPT_NAME + "'");
if (!docs || !docs.Count) {
   WScript.Echo("Сценарий <" + SCCRIPT_NAME + "> не найден на сервере");
   WScript.Quit();
}

// запрос документов сценария - подставляем код сценария, дату с и по
var where = "scriptId=" + docs.Get(0).id + " and created >= ToDate('" + DATE_START + "') and created <= ToDate('" + DATE_END + "')";
WScript.Echo("Идем документы сценария " + where);
var sdocs = server.Get("ScriptDoc", where);
if(!sdocs) {
   WScript.Echo(server.ErrorMessage);
   WScript.Quit();
}

// теперь для каждого документа сценария выбираем посещения
for (var i = 0; i < sdocs.Count; i++) {
   // Документ сценария
   var scriptDoc = sdocs.Get(i);

   // пункты мценария
   var items = scriptDoc.items;
   for (var j = 0; j < items.Count; j++) {

      var date = new Date(items.Get(j).date);
      where = "created = ToDate('" + DateToString (date) + "')";
      //WScript.Echo(where);

      // Ищем документы посещцения с фото. Документ VisitSrc возвращает ссылку на фото
      var visitDocs = server.Get("VisitSrc", where);
      if (visitDocs && visitDocs.Count) {
         var visit = visitDocs.Get(0);
         printPhotoData(visit);
      }
   }
}