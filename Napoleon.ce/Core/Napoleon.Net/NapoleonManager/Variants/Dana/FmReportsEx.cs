using GRSoft.NapoleonManager.Utils;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   
   class FmReportsEx : FmReports
   {
      public static readonly string REPORT_NAME = "actions_rpt";

      public FmReportsEx()
      {
         //panel5.Visible = false;
         //panel6.Visible = false;

         RichButton btn = new RichButton();

         btn.Caption = "Акции";
         btn.Checked = false;
         btn.Description = "Отчет об использовании акций";
         btn.Icon = global::GRSoft.NapoleonManager.Properties.Resources.visit_report;
         btn.Location = new System.Drawing.Point(249, 3);
         btn.Name = "btnActions";
         btn.Size = new System.Drawing.Size(240, 52);

         btn.Click += new System.EventHandler(this.btnWorkTime_Click);

         panel6.Controls.Add(btn);

      }

      private void btnWorkTime_Click(object sender, EventArgs e)
      {
         ResetPanel();
         gbDivision.Enabled = true;
         gbDate.Enabled = true;
         rbDiv.Enabled = true;
         rbAgent.Checked = true;
         btnDoReport.Enabled = true;
         selectedReport = REPORT_NAME;
      }
   }
}
