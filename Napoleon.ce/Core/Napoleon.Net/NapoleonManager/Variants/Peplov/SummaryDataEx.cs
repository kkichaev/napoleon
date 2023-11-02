using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class SummaryDataEx : SummaryData
   {
      private DateTime firstDocDate = DateTime.MinValue;

      public SummaryDataEx(Agent agent, DataSet<int, CommonConfig> dsConfig)
         :base(agent, dsConfig)
      {
      }

      public override void AddOrg(BaseDocument doc)
      {
         base.AddOrg(doc);

         if (firstDocDate == DateTime.MinValue || firstDocDate > doc.created)
            firstDocDate = doc.created;
      }

      public override string LastAccess
      {
         get
         {
            return firstDocDate != DateTime.MinValue ? firstDocDate.ToString("dd.MM.yyyy HH:mm") : string.Empty;
         }
         set
         {
            //base.LastAccess = value;
         }
      }
   }
}
