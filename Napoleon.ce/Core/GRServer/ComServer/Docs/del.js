var server = new ActiveXObject('GRSoft.Server');

WScript.Echo("Connecting...");

//var res = server.Connect('188.133.146.164', 8888, 'admin', 'admin');
var res = server.Connect('37.1.80.198', 9999, 'admin', 'admin');

if( !res )
{
	WScript.Echo(server.ErrorMessage);
	WScript.Quit();
}

WScript.Echo("Connected!");

server.Delete("DivisionManager", "division=0");
