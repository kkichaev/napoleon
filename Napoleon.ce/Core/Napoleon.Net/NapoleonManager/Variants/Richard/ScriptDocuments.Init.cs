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
         docs.Add(new ScanLocationDoc());
         docs.Add(new WhReqDoc());
#if RETURN_DOC
         docs.Add(new ReturnDoc());
#else
         //(LeopardO || Ardis || ClassicSpb || Plombir || HappyLand || Metelica || Kovalchuk || Slavica || Gilyakov || Alecon || Gulliver || RPK || Athina || Ishim || TD12Months || MariMedSnab || Quad || Bella || Antonov || ClearLine || LukasN || Modus || Birsnek || Demetra || PolyGrand  || MyasoDel || Sapfire || ZooOpt  || Stantor || HappyLand || ASK || WallStreet || Beybars || Servolux || Alianta || Frolov  || Chuvakova || EasternEmpire || EcoLineGroup || Beniaminov)
#endif
         docs.Add(new SalesDoc());

      }
   }

   class WhReqDoc : ScriptDocument
   {
      internal WhReqDoc() :
         base(WhRequest.OBJ_NAME, "Заявка на склад", Resources.return_doc)
      {
      }
   }
}
