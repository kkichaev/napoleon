using GRSoft.NapoleonManager.Properties;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   partial class ScriptDocuments
   {
      static void InitDocuments()
      {
         docs = new List<ScriptDocument>();
         docs.Add(new OrderDoc("OrderBundle"));
         docs.Add(new VisitDoc());
         docs.Add(new RemnantsDoc());
         docs.Add(new ReturnDoc("ReturnRequest"));
         //docs.Add(new DistribDoc());
         docs.Add(new RejectActDoc());
      }
   }

   internal class DistribDoc : ScriptDocument
   {
      internal DistribDoc()
         : base("DistribDoc", "Дистрибьюция", Resources.distrib_doc)
      {
      }
   }

   internal class RejectActDoc : ScriptDocument
   {
      internal RejectActDoc()
         : base("RejectAct", "Актирование", Resources.return_doc)
      {
      }
   }
}
