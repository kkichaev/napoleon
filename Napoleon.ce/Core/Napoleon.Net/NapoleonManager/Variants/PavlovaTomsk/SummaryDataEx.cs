using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class SummaryDataEx : SummaryData
   {

      public SummaryDataEx(Agent agent, DataSet<int, CommonConfig> dsConfig)
         : base(agent, dsConfig)
      {
      }

      public int PkoCnt { get { return pko; } }
      public double PkoSum { get; set; }

      public override void Add(Incass doc)
      {
         base.Add(doc);

         PkoSum += doc.Sum();
      }
   }

   class SummaryDivisionDataEx : SummaryDivisionData
   {
      public int PkoCnt { get; set; }
      public double PkoSum { get; set; }

      public SummaryDivisionDataEx(Division division)
         : base(division)
      { }

      public override void Add(SummaryData data)
      {
         base.Add(data);

         SummaryDataEx d = data as SummaryDataEx;

         if (d != null)
         {
            PkoCnt += d.PkoCnt;
            PkoSum += d.PkoSum;
         }
      }
   }

   
}
