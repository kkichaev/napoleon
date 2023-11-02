using GRSoft.NapoleonManager.Utils;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   
   class FmReportsEx : FmReports
   {
      public static readonly string WORK_TIME_REPORT = "rmr_work_time_report";

      public FmReportsEx()
      {
         //panel5.Visible = false;
         //panel6.Visible = false;

         RichButton btn = new RichButton();

         btn.Caption = "Отчет о визитах";
         btn.Checked = false;
         btn.Description = "Отчет о визитах";
         btn.Icon = global::GRSoft.NapoleonManager.Properties.Resources.visit_report;
         btn.Location = new System.Drawing.Point(249, 62);
         btn.Name = "btnVisit";
         btn.Size = new System.Drawing.Size(240, 52);

         btn.Click += new System.EventHandler(this.btnWorkTime_Click);

         panel2.Controls.Add(btn);

      }

      private void btnWorkTime_Click(object sender, EventArgs e)
      {
         ResetPanel();
         gbDivision.Enabled = true;
         gbDate.Enabled = true;
         rbDiv.Enabled = false;
         rbAgent.Checked = true;
         btnDoReport.Enabled = true;
         selectedReport = WORK_TIME_REPORT;
      }
   }
}
