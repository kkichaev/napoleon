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
         docs.Add(new OrderDoc());
         docs.Add(new IncassDoc());
         docs.Add(new VisitDoc());
         docs.Add(new RemnantsDoc());
         docs.Add(new TaskDoc());
         docs.Add(new FacingDoc());
         docs.Add(new InvEquDoc());
         docs.Add(new BarcodeDoc());
      }
   }

   internal class FacingDoc : ScriptDocument
   {
      internal FacingDoc()
         : base("Facing", "Фейсинг", Resources.merch)
      {
      }
   }

   internal class InvDoc : ScriptDocument
   {
      internal InvDoc()
         : base("InvFrg", "Инвентаризация", Resources.inv_doc)
      {
      }
   }

   internal class InvEquDoc : ScriptDocument
   {
      internal InvEquDoc()
         : base("InvEqu", "Контроль оборудования", Resources.inv_doc)
      {
      }
   }

   internal class BarcodeDoc : ScriptDocument
   {
      internal BarcodeDoc()
         : base("Barcode", "Штрихкод", Resources.barcode_doc)
      {
      }
   }
}
