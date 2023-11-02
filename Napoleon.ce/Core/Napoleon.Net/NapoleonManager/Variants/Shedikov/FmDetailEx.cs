using System;
using System.Collections.Generic;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
  [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public
   class FmDetailEx : FmDetail
   {
      RemnCtrl remnantCtrl;
      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         remnantCtrl = new RemnCtrl();
         detailPanel.Controls.Add(remnantCtrl);
         remnantCtrl.Dock = DockStyle.Fill;
         remnantCtrl.Location = new Point(0,0);
         remnantCtrl.tabControl.TabPages[0].Controls.Add(dgvRemnantsItems);
      }

      protected override void UpdateVisibility(Control visible, Network.DataObject src)
      {
         base.UpdateVisibility(visible, src);

         if (visible == dgvRemnantsItems) 
         {
            OrgRemnants r = src as OrgRemnants;

            if (r != null)
            {
               List<ConcurentItem> list = new List<ConcurentItem>();
               ConcurentItem i = new ConcurentItem();
               i.name = "Наши";
               i.grk = r.ourgrkqty;
               i.vtr = r.ourvtrqty;
               i.cmn = r.ourcmnqty;
               list.Add(i);

               i = new ConcurentItem();
               i.name = "Конкуренты";
               i.grk = r.cncgrkqty;
               i.vtr = r.cncvtrqty;
               i.cmn = r.cnccmnqty;
               list.Add(i);

               list.AddRange(((OrgRemnants)src).cncs);
               remnantCtrl.grid.DataSource = list;
               visible = remnantCtrl;
               visible.Visible = true;
               visible.BringToFront();
            }
         }
      }
   }

   
}
