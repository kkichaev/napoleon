using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class FmRouteEx : FmRoute
   {
      public DataSet<string, OrgFolder> dsOrgFolder = new DataSet<string, OrgFolder>(OrgFolder.OBJECT_NAME, false);

      public FmRouteEx(string idAgent, DateTime date)
         :base(idAgent, date)
      {
         cbOrgRoute.Checked = true;
      }

      protected override void AdjustFilterForDS(string idAgent, DateTime date)
      {
         base.AdjustFilterForDS(idAgent, date);
         dsOrgFolder.Filter=String.Format("\"date\"=ToDate('{0:dd/MM/yyyy}') and \"userid\"='{1}'", date, idAgent);
      }

      protected override void InitUpdateList(List<IDataSet> list)
      {
         list.Add(dsOrgFolder);
      }
   }
}
