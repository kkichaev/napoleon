var SRC_IP = '195.208.32.20';
var SRC_PORT = 8888;

var DEST_IP = '94.28.77.78';
//var DEST_IP = '127.0.0.1';
var DEST_PORT = 8888;

var serverSrc = new ActiveXObject('GRSoft.Server');
var serverDst = new ActiveXObject('GRSoft.Server');

function PaddingZero(val) {
   return val < 10 ? "0" + val : "" + val;
}

function ToStringDate(date) {
   return PaddingZero(date.getDate()) + "/" + PaddingZero(date.getMonth() + 1) + "/" + date.getFullYear();
}

function DocKey(doc) {
   return doc.userid + "|" + new Date(doc.created).getTime();
}

function DocKeyAdd(doc) {
   return doc.userid + "|" + (new Date(doc.created).getTime() + 1000);
}

function DocsToMap(docs) {
   var res = new Object();
   if(docs)
      for (i = 0; i < docs.Count; i++) {
         doc = docs.Get(i);
         var key = DocKey(doc);
         res[key] = true;
         //WScript.Echo("DKey " + key + " cr " + doc.created);

         key = DocKeyAdd(doc);
         res[key] = true;
         //WScript.Echo("DKey " + key + " cr " + doc.created);
      }

   return res;
}

function SetProps(dest, src, props) {
   while (true) {
      item = props.pop();
      if (!item)
         break;

      dest[item] = src[item];
   }
}

function SyncPODS(agents, serverDest, serverSrc) {
   for (ai = 0; ai < agents.Count; ai++) {
      agent = agents.Get(ai);
      if (agent.login == '')
         continue;

      where = '"userid"=' + "'" + agent.id + "'";
      srcPods = serverSrc.Get("SyncPOD", where);
      if (!srcPods)
         continue;

      WScript.Echo("Sync POD " + agent.name);

      destPods = serverDest.New("SyncPOD");
      for (i = 0; i < srcPods.Count; i++) {
         src = srcPods.Get(i);
         dest = destPods.New();

         SetProps(dest, src, ["created", "type", "remark"]);
      }
      serverSrc.Delete("SyncPOD", where);
      destPods.Write(agent.id);
   }
}

function GetAgent(agents, uid) {
   if(agents)
      for(i = 0 ; i< agents.Count; i++) {
         a = agents.Get(i);
         if (a.id == uid)
            return a.name;
      }
   return uid;
}

WScript.Echo("Connecting " + SRC_IP + ":" + SRC_PORT);
var res = serverSrc.Connect(SRC_IP, SRC_PORT);
if (!res) {
   WScript.Echo(server.ErrorMessage);
   WScript.Quit();
}

WScript.Echo("Connecting " + DEST_IP + ":" + DEST_PORT);
res = serverDst.Connect(DEST_IP, DEST_PORT);
if (!res) {
   WScript.Echo(serverDst.ErrorMessage);
   WScript.Quit();
}

WScript.Echo("Connected!");

var date = new Date();
var where = '"created" >= ToDate("' + ToStringDate(date) + '")';

var srcDocs = serverSrc.Get("Order", where);
var destDocs = serverDst.Get("Order", where);

var existsDocs = DocsToMap(destDocs);

var newDocs = new Object;
if (srcDocs) {
   WScript.Echo("Got " + srcDocs.Count + " docs" + " dest " + (destDocs ? destDocs.Count : "0"));

   for (i = 0; i < srcDocs.Count; i++) {
      var src = srcDocs.Get(i);
      //WScript.Echo("! " + src.userid + " date " + src.created + " id " + src.id);

      var key = DocKey(src);
      if (existsDocs[key] != null)
         continue;

      //WScript.Echo("Src Key " + key + " cr " + src.created);

      var userid = src.userid;
      var udocs = newDocs[userid];
      if (udocs == null) {
         udocs = serverDst.New("Order");
         newDocs[userid] = udocs;
      }
      var dst = udocs.New();
      SetProps(dst, src, ["id", "created", "date", "remark", "firmCode", "params", "prcType"]);

      for (j = 0; j < src.items.Count; j++) {
         var cst = src.items.Get(j);
         dcst = dst.items.New();
         SetProps(dcst, cst, ["isOurProduct", "qty", "id", "cost", "flags"]);
      }
   }
}

agents = serverSrc.Get("Agents", '');
for (uid in newDocs) {
   var docs = newDocs[uid];
   sdocs.Write(uid);
   WScript.Echo("Write agent's docs " + GetAgent(agents, uid) + " (" + docs.Count + ")");
}
WScript.Echo("Docs done");

SyncPODS(agents, serverSrc, serverDst);