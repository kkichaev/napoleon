var server = new ActiveXObject('GRSoft.Server');

WScript.Echo("Connecting...");

var res = server.Connect('127.0.0.1', 8888);

if( !res )
{
	WScript.Echo(server.ErrorMessage);
	WScript.Quit();
}

WScript.Echo("Connected!");

var params = {cmd:'update_crc'};
server.Report("update_prezent", params);
