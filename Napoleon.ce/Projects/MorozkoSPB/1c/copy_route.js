var SRC_IP = '127.0.0.1';
var SRC_PORT = 8888;

var server = new ActiveXObject('GRSoft.Server');

function SetProps(dest, src, props) {
   while (true) {
      item = props.pop();
      if (!item)
         break;

      //WScript.Echo(item + ' ' + src[item]);
      dest[item] = src[item];
   }
}


WScript.Echo("Connecting...");

var res = server.Connect(SRC_IP, SRC_PORT);

if( !res )
{
	WScript.Echo(server.ErrorMessage);
	WScript.Quit();
}

WScript.Echo("Connected!");


var docs = server.Get("OrgFolder", 'not "userid" is null');
server.Timeout = 60000;
if( !docs )
{
	WScript.Echo("Error " + server.ErrorMessage);
	WScript.Quit();
}	

var destList = server.New('OrgRouteShedule');
var startDate = new Date(1970, 0, 2);

WScript.Echo("Get object collection with " + docs.Count + " elements");
for( i=0; i<docs.Count; i++ )
{
   var src = docs.Get(i);
   var dest = destList.New();

   //WScript.Echo(src.userid + ' ' + src.name + ' ' + src.items.Count);
   SetProps(dest, src, ["userid","name"]);
   dest.dateFrom = startDate.getVarDate();

   for (j = 0; j < src.items.Count; j++)
   {
      var isrc = src.items.Get(j);
      var idst = dest.items.New();
      SetProps(idst, isrc, ["name","pos"]);
   }
}

destList.WriteDirect();

WScript.Quit();
