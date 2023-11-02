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

         IDataSet cdata = DataModule.Get(Purchase.OBJECT_NAME);
         if (cdata != null)
            foreach (Purchase order in cdata.Data)
               this.Add(order);

         cdata = DataModule.Get(Selling.OBJECT_NAME);
         if (cdata != null)
            foreach (Selling order in cdata.Data)
               this.Add(order);

      }

      private void Add(Purchase p)
      {
         if (p.agent != null && ContainsKey(p.userid))
         {
            SummaryData sd = this[p.userid];
            sd.AddOrder(p);
         }
      }

      private void Add(Selling p)
      {
         if (p.agent != null && ContainsKey(p.userid))
         {
            SummaryData sd = this[p.userid];
            sd.AddOrg(p);

            if (!sd.hasMissedOrder)
            {
               sd.hasMissedOrder = FmDetailBase.OrderMissed(p, (DataSet<int, OrderCommitted>)DataModule.Get(OrderCommitted.OBJECT_NAME));
            }
         }
      }

   }
}
