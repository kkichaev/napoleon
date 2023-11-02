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
      public FmDetailEx(FmDetailData data)
         : base(data)
      {
/*
         ToolStripItem tsi = new ToolStripMenuItem();
         tsi.Name = "tsbReturnsReport";
         tsi.Size = new System.Drawing.Size(152, 22);
         tsi.Text = "Возвраты";
         tsi.Click += new EventHandler(tsbReturnsReport_Click);

         tsReportMenu.DropDownItems.Add(tsi);
*/
      }

      public void tsbReturnsReport_Click(object sender, EventArgs arg)
      {
         ReturnsReports.Do(GetDateForStartPeriod(), GetDateForEndPeriod().AddDays(-1), GetSelectedIdAgent(), this);
      }
   }

   class DivisionsEx : Divisions
   {
      public DivisionsEx()
      {
         ToolStripItem tsi = new ToolStripMenuItem();
         tsi.Name = "RouteTempl";
         tsi.Size = new System.Drawing.Size(152, 22);
         tsi.Text = "Шаблоны маршутов";
         tsi.Click += new EventHandler((s, e) => { FmRouteTemplate.Open(); });

         tb.Items.Add(tsi);
      }
   }
}
