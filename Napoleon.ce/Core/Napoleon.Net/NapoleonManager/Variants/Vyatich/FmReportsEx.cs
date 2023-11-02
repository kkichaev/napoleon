using GRSoft.NapoleonManager.Utils;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class FmReportsEx : FmReports
   {
      public static readonly string REMARK_REPORT = "rmr_remark_report";

      public FmReportsEx()
      {
         RichButton btn = new RichButton();
         btn.Location = new System.Drawing.Point(500,0);
         btn.Size = new System.Drawing.Size(240, 55);
         btn.Description = "Отчет по заметкам";
         btn.Caption = "Заметки за период";
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
         rbAgent.Enabled = true;
         cbAgent.Enabled = true;
         selectedReport = REMARK_REPORT;
      }
   }
}
