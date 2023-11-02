var server = new ActiveXObject('GRSoft.Server');

WScript.Echo("Connecting...");

var res = server.Connect('127.0.0.1', 8888, '2', '2');

if( !res )
{
	WScript.Echo(server.ErrorMessage);
	WScript.Quit();
}

WScript.Echo("Connected!");


server.Timeout = 60000;
var docs = server.Get("Sklads","");

if( !docs )
{
	WScript.Echo("Error " + server.ErrorMessage);
	WScript.Quit();
}	else
  WScript.Echo("records: " + docs.Count);
