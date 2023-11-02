using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class DivisionSummaryEx : DivisionSummary
   {
      protected DivisionSummaryEx(DataSet<int, CommonConfig> dsConfig):base(dsConfig)
      {
      }

      protected override void PostAddData()
      {
         base.PostAddData();

         IDataSet cdata = DataModule.Get(InvEqu.OBJECT_NAME);
         if (cdata != null)
             foreach (InvEqu order in cdata.Data)
                 this.Add(order);
      }

      private void Add(InvEqu contract)
      {
         if (contract.agent != null && ContainsKey(contract.userid))
         {
            SummaryData sd = this[contract.userid];
            sd.AddOrg(contract);
         }
      }
   }
}
