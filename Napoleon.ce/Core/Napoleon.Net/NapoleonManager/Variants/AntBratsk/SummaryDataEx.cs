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

      public override void Add(Order o)
      {
         orders++;
         //sum += o.Sum();

         AddOrg(o);
         AddOrder(o.id, o.userid, o.created);

         if (!hasMissedOrder)
            hasMissedOrder = FmDetailBase.OrderMissed(o, (DataSet<int, OrderCommitted>)DataModule.Get(OrderCommitted.OBJECT_NAME));
      }

      public override void Add(Incass doc)
      {
         pko++;
         sum += doc.Sum();
         AddOrg(doc);
      }
   }
}
