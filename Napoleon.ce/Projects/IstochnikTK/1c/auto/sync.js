var CONN_STR = 'File="C:\\Works\\1c\\ANew";Usr="Михаил";Pwd=""';
var server = new ActiveXObject('V83.COMConnector');

// var FILE_NAME = "log.txt";

// var objFSO = new ActiveXObject("Scripting.FileSystemObject");
// var log;
// if (objFSO.FileExists(FILE_NAME))
   // log = objFSO.OpenTextFile(FILE_NAME, 8);
// else
   // log = objFSO.CreateTextFile(FILE_NAME);

function Say(str) {
	// log.WriteLine(str);
	WScript.Echo(str);
}

var conn = server.Connect(CONN_STR);
if(!conn)
{
	Say('Fail');
	WScript.Quit();
}

var napoleon = conn.Справочники.ВнешниеОбработки.НайтиПоНаименованию("Выгрузка Наполеон");
if(napoleon.IsEmpty()) {
	Say('Нет выгрузки Наполеон во внешних обработках');
	WScript.Quit(1);
}

bdata = napoleon.ХранилищеВнешнейОбработки.Получить();
fn = conn.ПолучитьИмяВременногоФайла("epf");
bdata.Write(fn);
handler = conn.ВнешниеОбработки.Создать(fn, false);

handler.ЗагрузитьНастройки();
var param = WScript.Arguments(0);
if(param == "SyncData") {
	Say("Sync data");
	handler.ВыгрузкаОстатков(); 
} else if(param == "SyncDocs") {
	Say("Load docs");
	handler.ПриемДокументов(); 
} else if(param == "SyncDebet") {
	Say("Load debet");	
	handler.ВыгрузкаДолгов(); 
}
