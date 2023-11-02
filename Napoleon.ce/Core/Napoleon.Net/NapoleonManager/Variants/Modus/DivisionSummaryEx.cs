using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class DivisionSummaryEx : DivisionSummary
   {
      protected DivisionSummaryEx(DataSet<int, CommonConfig> dsConfig) : base(dsConfig)
      {
         
      }

      protected override void PostAddData()
      {
         base.PostAddData();

         IDataSet cdata = DataModule.Get(Facing.OBJECT_NAME);
         if (cdata != null)
            foreach (Facing f in cdata.Data)
               this.Add(f);
      }

      internal void Add(Facing f)
      {
         if (f.agent != null && ContainsKey(f.AgentID))
         {
            SummaryData sd = this[f.AgentID];
            sd.AddOrg(f);
         }
      }
   }
}
