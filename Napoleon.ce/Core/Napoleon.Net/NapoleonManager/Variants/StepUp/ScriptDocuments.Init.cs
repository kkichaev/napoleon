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
         docs.Add(new VisitDoc());
      }
   }

   internal class ContractDoc : ScriptDocument
   {
      private static readonly string TITLE = "Контракт";

      internal ContractDoc(ContractDef c)
         : base("Contract", string.Format("{0}({1} '{2:dd/MM/yyyy} - '{3:dd/MM/yyyy})", TITLE, c.name, c.start, c.finish), Resources.qty2report, c.id)
      {
      }
   }
}
