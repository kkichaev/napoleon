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
#if Avalon || Hendz
         docs.Add(new VandSelDoc());
         docs.Add(new VandAuditDoc());
         docs.Add(new VandReloadDoc());
#elif CottonClub
         docs.Add(new VisitDoc());
         docs.Add(new CommonAuditDoc());
         docs.Add(new PromoAuditDoc());
#else
         docs.Add(new OrderDoc());
#if (Ardis || Metelica || VladHleb || MichelK || Athina || RPK || Ishim || TD12Months || MariMedSnab || Bella || ClearLine || LukasN || Modus || Birsnek || Demetra || PolyGrand || MyasoDel || Sapfire || Stantor || WallStreet || Beybars || Servolux || Alianta || Frolov || Chuvakova || BeautyProfy || DymovMoscow)
#else
         docs.Add(new IncassDoc());
#endif
         docs.Add(new VisitDoc());
         docs.Add(new RemnantsDoc());
#if (LeopardO || Ardis || ClassicSpb || Plombir || HappyLand || Metelica || Kovalchuk || Slavica || Gilyakov || Alecon || Gulliver || RPK || Athina || Ishim || TD12Months || MariMedSnab || Quad || Bella || Antonov || ClearLine || LukasN || Modus || Birsnek || Demetra || PolyGrand  || MyasoDel || Sapfire || ZooOpt  || Stantor || HappyLand || ASK || WallStreet || Beybars || Servolux || Alianta || Frolov  || Chuvakova)
#else
         docs.Add(new ReturnDoc());
#endif
#if (Ardis || Kovalchuk || Alecon || VladHleb || MariMedSnab || MasloDom || Quad || Gwinner || Bella || Sibtrade || Prodo || Birsnek || Odincov)
         docs.Add(new MonitoringDoc());
#endif
#if(ClassicSpb)
         docs.Add(new SalesDoc());
         docs.Add(new TaskDoc());
#endif
#if(Plombir)
         docs.Add(new TaskDoc());
#endif
#if (PoultryNSib || MasloDom || Bella || Prodo || LukasN || ASK)
         docs.Add(new TaskDoc());
#endif
#if(Gulliver || Plombir)
         docs.Add(new SalesDoc());
#endif
#if(Alecon)
         docs.Add(new WSOrder());
#endif
#if(RPK)
         docs.Add(new DefectDoc());
#endif
#if (TDLider || Prodo || Sibtrade)
         docs.Add(new BonusDoc());
#endif

#if (Demetra || WallStreet)
         docs.Add(new DistributionDoc());
#endif
#if (Prodo)
         docs.Add(new OrgDistributionDoc());
#endif
#if (HappyLand)
         docs.Add(new SmartTaskStartDoc());
         docs.Add(new SmartTaskEndDoc());
#endif
#endif
      }
   }
}
