using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class ScriptOverviewEx : ScriptOverview
   {
      public override void SetData(Network.DataObject dataObject)
      {
         base.SetData(dataObject);
         ScriptDoc sd = dataObject as ScriptDoc;
         if (sd != null)
         {
            string txtRsn = sd.noOrderReason;
            if (txtRsn.Length > 0)
            {
               DataSet<string, NoOrderReason> set = DataModule.Get(NoOrderReason.OBJECT_NAME) as DataSet<string, NoOrderReason>;
               NoOrderReason item;
               if (set != null && set.TryGetValue(txtRsn, out item))
                  txtRsn = item.name;

               text.Text += "\nПричина отказа от заявки: " + txtRsn;
            }
         }
      }
   }
}
