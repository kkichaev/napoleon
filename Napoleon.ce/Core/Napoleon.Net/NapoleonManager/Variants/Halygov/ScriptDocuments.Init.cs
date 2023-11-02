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
#if Avalon
         docs.Add(new IncassDoc());
         docs.Add(new VandSelDoc());
         docs.Add(new VandAuditDoc());
         docs.Add(new VandReloadDoc());
#elif CottonClub
         docs.Add(new VisitDoc());
         docs.Add(new CommonAuditDoc());
         docs.Add(new PromoAuditDoc());
#else
         docs.Add(new OrderDoc());
#if (Ardis || Metelica || VladHleb || MichelK || Athina || RPK || Ishim || TD12Months || MariMedSnab || Bella || ClearLine || LukasN || Modus || Birsnek || Demetra || PolyGrand || MyasoDel || Sapfire || Stantor || WallStreet || Beybars || Servolux)
#else
         docs.Add(new IncassDoc());
#endif
         docs.Add(new VisitDoc());
         docs.Add(new RemnantsDoc());
#if (LeopardO || Ardis || ClassicSpb || Plombir || HappyLand || Metelica || Kovalchuk || Slavica || Gilyakov || Alecon || Gulliver || RPK || Athina || Ishim || TD12Months || MariMedSnab || Quad || Bella || Antonov || ClearLine || LukasN || Modus || Birsnek || Demetra || PolyGrand  || MyasoDel || Sapfire || ZooOpt  || Stantor || HappyLand || ASK || WallStreet || Beybars || Servolux)
#else
         docs.Add(new ReturnDoc());
#endif
         docs.Add(new MonitoringDoc());
#if(ClassicSpb)
         docs.Add(new SalesDoc());
         docs.Add(new TaskDoc());
#endif
         docs.Add(new TaskDoc());
#if(Gulliver || Plombir)
         docs.Add(new SalesDoc());
#endif
#if(Alecon)
         docs.Add(new WSOrder());
#endif
#if(RPK)
         docs.Add(new DefectDoc());
#endif
         docs.Add(new BonusDoc());

#if (Demetra || WallStreet)
         docs.Add(new DistributionDoc());
#endif
         docs.Add(new OrgDistributionDoc());
#if (HappyLand)
         docs.Add(new SmartTaskStartDoc());
         docs.Add(new SmartTaskEndDoc());
#endif
#endif
      }
   }
}
