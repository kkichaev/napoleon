using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class MainFormEx : MainForm
   {
      private GRSoft.Network.DataSet<string, OrgType> dsOrgType = new Network.DataSet<string, OrgType>(OrgType.OBJECT_NAME);

      protected override void AddUpdateDataSet(List<Network.IDataSet> updSets)
      {
         base.AddUpdateDataSet(updSets);
         updSets.Add(dsOrgType);
      }
   }
}
