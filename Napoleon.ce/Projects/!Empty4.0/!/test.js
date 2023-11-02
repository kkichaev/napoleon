var server = new ActiveXObject('GRSoft.Server');

WScript.Echo("Connecting...");

var res = server.Connect('127.0.0.1', 8888, '2', '2');//, '182', '1000');

if( !res )
{
	WScript.Echo(server.ErrorMessage);
	WScript.Quit();
}


WScript.Echo("Connected!");

server.Timeout = 4 * 60000;

var docs = server.Get("OrderBundle", '');

if(!docs) {
	WScript.Echo(server.ErrorMessage);
	WScript.Quit();
}

WScript.Echo("Get object collection with " + docs.Count + " elements");
ctr = 0;
for( i=0; i<docs.Count && i < 20 ; i++ )
{
   	var doc = docs.Get(i);
   	WScript.Echo(doc.created + " " + doc.id+ " ");
}

WScript.Quit();
