var server = new ActiveXObject('GRSoft.Server');

WScript.Echo("Connecting...");

var res = server.Connect('188.133.146.164', 8888, 'admin', 'admin');
//var res = server.Connect('89.179.84.162', 8888, 'admin', 'admin');

if( !res )
{
	WScript.Echo(server.ErrorMessage);
	WScript.Quit();
}

WScript.Echo("Connected!");

var docs = server.Get("DivisionManager", "");
for( i=0; i<docs.Count; i++ )
{
	var doc = docs.Get(i);
	//if( doc.cheif != "" )
	{
		WScript.Echo("Manager " + doc.division + "|" + doc.login );
		//doc.cheif = "";
	}
}

//docs.Write();
/*
var docs = server.Get("DivisionManager", "");
for( i=0; i<docs.Count; i++ )
{
	var doc = docs.Get(i);
	WScript.Echo("Division " + doc.division + " Login " + doc.login);
}

var docs = server.Get("Agents", "");
for( i=0; i<docs.Count; i++ )
{
	var doc = docs.Get(i);
	WScript.Echo("Agent login " + doc.login);
}
*/
