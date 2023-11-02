var server = new ActiveXObject('GRSoft.Server');

WScript.Echo("Connecting...");

var res = server.Connect('109.106.134.114', 8888, '177', '177');
//var res = server.Connect('95.86.209.229', 8899, 'admin', 'admin');

if( !res )
{
	WScript.Echo(server.ErrorMessage);
	WScript.Quit();
}

WScript.Echo("Connected!");


//var docs = server.Get("Folder", "");
server.Timeout = 60000;
var docs = server.Get("VisitInfo", "date >= ToDate('14.06.2013') and date <ToDate('15.06.2013') and userid='$CURRENT_USERID'" );
//server.Delete("Agents",  "");

//var docs = server.Get("Folder",  '');
//var docs = server.Get("GPSPos", "date>ToDate('13.06.2013') and date <ToDate('15.06.2013') ");
if( !docs )
{
	WScript.Echo("Error " + server.ErrorMessage);
	WScript.Quit();
}	
/*
var fields = docs.Fields;
for( i = 0; i<fields.Count; i++ )
{
	var fld = fields.Get(i);
	WScript.Echo("Field " + fld.Name + " Type " + fld.Type);

	if( fld.Type == 3 )
	{
		WScript.Echo("        subObject");
		var cho = fld.ChildObject;
		for( j = 0; j < cho.Count; j++ )
		{
			var chf = cho.Get(j);
			WScript.Echo("        Field " + chf.Name + " Type " + chf.Type);
		}
	}

}
*/
WScript.Echo("Get object collection with " + docs.Count + " elements");
for( i=0; i<docs.Count; i++ )
{
	var doc = docs.Get(i);
	WScript.Echo("Id " + doc.date + " name " + doc.latitude + " ido " + doc.longitude);
}
WScript.Quit();
/*
var doc = docs.Get(0);
doc.name = "Это тест";
docs.Get(1).Delete();
docs.RemoveObject(1);
docs.Replace("101");

WScript.Quit();
*/
for( i=0; i<docs.Count; i++ )
{
	var doc = docs.Get(i);
	WScript.Echo("Date " + new Date(doc.date));

	var items = doc.items;
	for( j=0; j<items.Count; j++ )
	{
		var img = items.Get(j).id;
		if( img.Size > 0 )
		{
			var fileName = doc.id + "img" + j + ".jpeg";
			WScript.Echo("   write image file " + fileName);
			img.Write(fileName);
		}
	}
}