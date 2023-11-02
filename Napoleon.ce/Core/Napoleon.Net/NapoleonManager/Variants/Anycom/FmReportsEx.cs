using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GRSoft.NapoleonManager
{
   class FmReportsEx : FmReports
   {
      public FmReportsEx()
      {
         GRSoft.NapoleonManager.Utils.RichButton btn = new Utils.RichButton();

         btn.Caption = "Отчет по планам";
         btn.Checked = false;
         btn.Description = "Отчет по выполнению планов";
         btn.Icon = global::GRSoft.NapoleonManager.Properties.Resources.view_calendar_timeline;
         btn.Location = new System.Drawing.Point(495, 62);
         btn.Name = "btnTimeRep";
         btn.Size = new System.Drawing.Size(240, 52);
         btn.TabIndex = 0;
         btn.Click += PlanReportClick;

         panel2.Controls.Add(btn);
      }

      private void PlanReportClick(object sender, EventArgs e)
      {
         ResetPanel();

         gbDivision.Enabled = true;
         gbDate.Enabled = true;
         rbDiv.Enabled = true;
         rbAgent.Checked = true;
         btnDoReport.Enabled = true;
         selectedReport = "agent_plans";
      }
   }
}
