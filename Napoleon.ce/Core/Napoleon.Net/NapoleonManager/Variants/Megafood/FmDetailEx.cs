using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
  [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public
   class FmDetailEx : FmDetail
   {
      public FmDetailEx(FmDetailData data) : base(data)
      {
         ToolStripItem tsi = new ToolStripMenuItem();
         tsi.Name = "tsbActions";
         tsi.Size = new System.Drawing.Size(152, 22);
         tsi.Text = "Незаказанный фокусный ассортимент";
         tsi.Click += new EventHandler((o, e) => {
            FmRejectFocus fmr = new FmRejectFocus();
            fmr.SetData(GetDateForStartPeriod(), GetDateForEndPeriod(), GetSelectedAgent());
            fmr.Show();
         });

         tsReportMenu.DropDownItems.Add(tsi);
      }
   }
}
