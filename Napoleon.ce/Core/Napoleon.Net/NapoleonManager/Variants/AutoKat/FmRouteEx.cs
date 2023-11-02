using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class FmRouteEx : FmRoute
   {
      public FmRouteEx(string idAgent, DateTime date):base(idAgent, date)
      {
         cbOrgRoute.Visible = false;

         //dgvOrgs.Columns[2].FillWeight = 10;
         //dgvOrgs.Columns[3].FillWeight = 10;
         dgvOrgs.Columns[4].Visible = false;
         dgvOrgs.Columns[5].Visible = false;
         dgvOrgs.Columns[6].Visible = false;
         clmnDuration.Visible = false;
         clmnSum.Visible = false;
      }

      public override void PostFillVisitGrid()
      {
         base.PostFillVisitGrid();

         foreach(DataGridViewRow row in dgvOrgs.Rows)
         {
            VisitQueueItem item = (VisitQueueItem)row.Tag;

            if (item != null)
            {
               if (!item.objType.IsStopType)
               {
                  row.Cells[2].Value = "Визит";
               }
            }
         }
      }
   }
}
