var SRC_IP = '127.0.0.1';
var SRC_PORT = 8888;

var DEST_IP = '127.0.0.1';
var DEST_PORT = 8889;


function ListToDict(srcObj) {
   var res = new Object();

   if (srcObj) {
      for (i = 0; i < srcObj.Count; i++) {
         var obj = srcObj.Get(i);
         res[obj.id] = obj;
      }
   }

   return res;
}

function SetProps(dest, src, props) {
   while (true) {
      item = props.pop();
      if (!item)
         break;

      //WScript.Echo(item);
      dest[item] = src[item];
   }
}

function SyncAgents(serverDst, serverSrc) {
   var agentsSrc = serverSrc.Get("Agents", '');
   var agentsDest = serverDst.Get("Agents", '');
   if (!agentsDest)
      agentsDest = serverDst.New("Agents");

   modified = false;

   WScript.Echo("Sync agents...");

   dicA = ListToDict(agentsDest);
   for (i = 0; i < agentsSrc.Count; i++) {
      var src = agentsSrc.Get(i);
      var dst = dicA[src.id];
      if (dst == null) {
         modified = true;
         dst = agentsDest.New();
         SetProps(dst, src, ["id", "name"]);
         dicA[dst.id] = dst;
      } else {
         if (dst.name.localeCompare(src.name) != 0) {
            modified = true;
            dst.name = src.name;
         }
      }
   }

   if (modified)
      agentsDest.WriteDirect();

   var cfgSrc = serverSrc.Get("Config", '');
   var cfgDest = serverDst.New("SyncConfig");
   if (cfgSrc)
      for (i = 0; i < cfgSrc.Count; i++) {
         var src = cfgSrc.Get(i);
         var dst = cfgDest.New();
         SetProps(dst, src, ["key", "value"]);
      }

   cfgDest.WriteDirect();

   return agentsSrc;
}

function SyncFolders(serverDst, serverSrc) {
   var listSrc = serverSrc.Get("ManagerFolder", '"userid" is null');
   var listDest = serverDst.New("ManagerFolder");

   WScript.Echo("Sync folders...");

   for (i = 0; i < listSrc.Count; i++) {
      var src = listSrc.Get(i);

      dst = listDest.New();
      SetProps(dst, src, ["id", "name", "level"]);
   }

   listDest.Replace('NULL');
}

function SyncPrice(serverDst, serverSrc, srcAgents)
{
   for (ac = 0; ac < srcAgents.Count; ac++) {
      agent = srcAgents.Get(ac);

      var listSrc = serverSrc.Get("ManagerPrice", '"userid"=' + "'" + agent.id + "'");
      var listDest = serverDst.New("ManagerPrice");

      if (!listSrc) {
         WScript.Echo("No price " + agent.name);
         continue;
      }
      WScript.Echo("Sync price " + agent.name);

      for (i = 0; i < listSrc.Count; i++) {
         var src = listSrc.Get(i);

         dst = listDest.New();
         SetProps(dst, src, ["id", "name", "fid", "qtyInPack", "qty", "weight"]);

         id = src.id;
         for (j = 0; j < src.cost.Count; j++) {
            var cst = src.cost.Get(j);
            dcst = dst.cost.New();
            dcst.id = id;
            dcst.cost = cst.cost;
         }
      }

      listDest.Replace(agent.id);
   }
}

function SyncOrgs(serverDst, serverSrc, srcAgents) {
   for (ac = 0; ac < srcAgents.Count; ac++) {
      agent = srcAgents.Get(ac);

      var listSrc = serverSrc.Get("Org", '"userid"=' + "'" + agent.id + "'");
      var listDest = serverDst.New("Org");

      if (!listSrc) {
         WScript.Echo("No org " + agent.name);
         continue;
      }
      WScript.Echo("Sync orgs " + agent.name);

      for (i = 0; i < listSrc.Count; i++) {
         var src = listSrc.Get(i);

         dst = listDest.New();
         SetProps(dst, src, ["id", "name", "address", "supplCode", "prcType", "costype"]);

         for (j = 0; j < src.contacts.Count; j++) {
            var cst = src.contacts.Get(j);
            dcst = dst.contacts.New();
            SetProps(dcst, cst, ["name", "phone", "id"]);
         }
      }

      listDest.Replace(agent.id);
   }
}

function SyncDebts(serverDst, serverSrc, srcAgents) {
   for (ac = 0; ac < srcAgents.Count; ac++) {
      agent = srcAgents.Get(ac);

      var listSrc = serverSrc.Get("Delivery", '"userid"=' + "'" + agent.id + "'");
      var listDest = serverDst.New("Delivery");

      if (!listSrc) {
         WScript.Echo("No delivery " + agent.name);
         continue;
      }
      WScript.Echo("Sync deliveries  " + agent.name);

      for (i = 0; i < listSrc.Count; i++) {
         var src = listSrc.Get(i);

         dst = listDest.New();
         SetProps(dst, src, ["id", "sumD", "number", "date", "payDate", "created"]);

         for (j = 0; j < src.items.Count; j++) {
            var cst = src.items.Get(j);
            dcst = dst.items.New();
            SetProps(dcst, cst, ["id", "qty", "sum"]);
         }
      }

      listDest.Replace(agent.id);
   }
}

function SyncPays(serverDst, serverSrc, srcAgents) {
   for (ac = 0; ac < srcAgents.Count; ac++) {
      agent = srcAgents.Get(ac);

      var listSrc = serverSrc.Get("SyncPay", '"userid"=' + "'" + agent.id + "'");
      var listDest = serverDst.New("SyncPay");

      if (!listSrc) {
         WScript.Echo("No payments " + agent.name);
         continue;
      }
      WScript.Echo("Sync payments  " + agent.name);

      for (i = 0; i < listSrc.Count; i++) {
         var src = listSrc.Get(i);

         dst = listDest.New();
         SetProps(dst, src, ["id", "sum", "number", "date"]);
      }

      listDest.Replace(agent.id);
   }
}

function SyncOrdCommitted(serverDst, serverSrc, srcAgents) {
   for (ac = 0; ac < srcAgents.Count; ac++) {
      agent = srcAgents.Get(ac);

      var listSrc = serverSrc.Get("OrderCommitted", '"userid"=' + "'" + agent.id + "'");
      var listDest = serverDst.New("OrderCommitted");

      if (!listSrc)
         continue;

      for (i = 0; i < listSrc.Count; i++) {
         var src = listSrc.Get(i);

         dst = listDest.New();
         SetProps(dst, src, ["created", "number", "userid"]);
      }

      listDest.Replace(agent.id);
   }
}

var serverSrc = new ActiveXObject('GRSoft.Server');
var serverDst = new ActiveXObject('GRSoft.Server');

WScript.Echo("Connecting " + SRC_IP+ ":" + SRC_PORT);
var res = serverSrc.Connect(SRC_IP, SRC_PORT);
if (!res) {
   WScript.Echo(server.ErrorMessage);
   WScript.Quit();
}

WScript.Echo("Connecting " + DEST_IP + ":" + DEST_PORT);
res = serverDst.Connect(DEST_IP, DEST_PORT);
if (!res) {
   WScript.Echo(server.ErrorMessage);
   WScript.Quit();
}

WScript.Echo("Connected!");

srcAgents = SyncAgents(serverDst, serverSrc);
SyncFolders(serverDst, serverSrc);
SyncPrice(serverDst, serverSrc, srcAgents);
SyncOrgs(serverDst, serverSrc, srcAgents);
SyncDebts(serverDst, serverSrc, srcAgents);
SyncPays(serverDst, serverSrc, srcAgents);
SyncOrdCommitted(serverDst, serverSrc, srcAgents);