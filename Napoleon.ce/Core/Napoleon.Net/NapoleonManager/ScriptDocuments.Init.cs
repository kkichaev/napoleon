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
#if VAND_PROJECT || Hendz
         docs.Add(new VandSelDoc());
         docs.Add(new VandAuditDoc());
         docs.Add(new VandReloadDoc());
#elif CottonClub
         docs.Add(new VisitDoc());
         docs.Add(new CommonAuditDoc());
         docs.Add(new PromoAuditDoc());
#else
         docs.Add(new OrderDoc());
#if (Ardis || Metelica || VladHleb || MichelK || Athina || RPK || Ishim || TD12Months || MariMedSnab || Bella || ClearLine || LukasN || Modus || Birsnek || Demetra || PolyGrand || MyasoDel || Sapfire || Stantor || WallStreet || Beybars || Servolux || Alianta || Frolov || Chuvakova || BeautyProfy || ROST || Vetli || EasternEmpire || EcoLineGroup || Beniaminov)
#else
         docs.Add(new IncassDoc());
#endif
         docs.Add(new VisitDoc());
         docs.Add(new RemnantsDoc());
         docs.Add(new ScanLocationDoc());
#if RETURN_DOC
         docs.Add(new ReturnDoc());
#else
         //(LeopardO || Ardis || ClassicSpb || Plombir || HappyLand || Metelica || Kovalchuk || Slavica || Gilyakov || Alecon || Gulliver || RPK || Athina || Ishim || TD12Months || MariMedSnab || Quad || Bella || Antonov || ClearLine || LukasN || Modus || Birsnek || Demetra || PolyGrand  || MyasoDel || Sapfire || ZooOpt  || Stantor || HappyLand || ASK || WallStreet || Beybars || Servolux || Alianta || Frolov  || Chuvakova || EasternEmpire || EcoLineGroup || Beniaminov)
#endif
#if (Ardis || Kovalchuk || Alecon || MariMedSnab || MasloDom || Quad || Bella || Sibtrade || Prodo || Birsnek || Odincov || KalinaTD || Discount)
         docs.Add(new MonitoringDoc());
#endif
#if(ClassicSpb)
         docs.Add(new TaskDoc());
#endif
#if(Plombir)
         docs.Add(new TaskDoc());
#endif
#if (PoultryNSib || MasloDom || Bella || Prodo || LukasN || ASK || TcarFood)
         docs.Add(new TaskDoc());
#endif
#if SALES_MODULE
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

#if WallStreet || EasternEmpire || EcoLineGroup
         docs.Add(new DistributionDoc());
#endif
#if Demetra
         docs.Add(new DistrScriptDoc());
#endif
#if (Prodo)
         docs.Add(new OrgDistributionDoc());
#endif
#if Incotek
         docs.Add(new StorcheckDoc());
#endif
#if (HappyLand)
         docs.Add(new SmartTaskStartDoc());
         docs.Add(new SmartTaskEndDoc());
#endif
#endif
#if Shweller
         docs.Add(new OrgDistributionDoc());
#endif
      }
   }
}
