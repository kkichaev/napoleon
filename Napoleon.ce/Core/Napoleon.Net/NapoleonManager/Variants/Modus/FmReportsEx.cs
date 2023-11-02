using GRSoft.NapoleonManager.Utils;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class FmReportsEx : FmReports
   {
      public static readonly string START_WORK_REPORT = "start_work_report";

      public FmReportsEx()
      {
         RichButton btn = new RichButton();
         btn.Location = new System.Drawing.Point(500,0);
         btn.Size = new System.Drawing.Size(240, 55);
         btn.Description = "Время начала работы";
         btn.Caption = "Время начала работы";
         btn.Click += btn_Click;
         panel4.Controls.Add(btn);
      }

      void btn_Click(object sender, EventArgs e)
      {
         ResetPanel();
         gbDivision.Enabled = true;
         rbDiv.Enabled = true;
         rbDiv.Checked = true;
         gbDate.Enabled = true;
         btnDoReport.Enabled = true;
         rbAgent.Enabled = false;
         cbAgent.Enabled = false;
         selectedReport = START_WORK_REPORT;
      }
   }
}
