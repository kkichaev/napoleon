using GRSoft.NapoleonManager.Utils;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class FmReportsEx : FmReports
   {
      public static readonly string SCR_TIME_REPORT = "scrtime";
      public static readonly string WORK_TIME_REPORT = "work_report";
      public static readonly string DSC_REPORT = "order_decision_report";

      public FmReportsEx()
      {
         panel4.Size = new System.Drawing.Size(832, 125);
         RichButton btn = new RichButton();
         btn.Location = new System.Drawing.Point(500, 0);
         btn.Size = new System.Drawing.Size(240, 55);
         btn.Description = "Отчет о работе";
         btn.Caption = "Отчет о работе";
         btn.Click += btn_Click;
         btn.Icon = Properties.Resources.work_report;
         panel4.Controls.Add(btn);

         btn = new RichButton();
         btn.Location = new System.Drawing.Point(3, 60);
         btn.Size = new System.Drawing.Size(240, 55);
         btn.Description = "Oтчёт по времени пребывания в точке";
         btn.Caption = "Oтчёт по времени пребывания в точке";
         btn.Click += btn_Click2;
         btn.Icon = Properties.Resources.ic_av_timer;
         panel4.Controls.Add(btn);

         btn = new RichButton();
         btn.Location = new System.Drawing.Point(250, 60);
         btn.Size = new System.Drawing.Size(240, 55);
         btn.Description = "Утверждение заказов";
         btn.Caption = "Утверждение заказов со скидками";
         btn.Click += btn_Click3;
         btn.Icon = Properties.Resources.result_report;
         panel4.Controls.Add(btn);
		 
		 btnVisitTIme.Visible = false;
		 btnWorkTime.Visible = false;
		 panel11.Visible = false;
		 label8.Visible = false;
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
         gbTime.Enabled = true;
         selectedReport = WORK_TIME_REPORT;
      }

      void btn_Click2(object sender, EventArgs e)
      {
         ResetPanel();
         gbDivision.Enabled = true;
         rbDiv.Enabled = true;
         rbDiv.Checked = true;
         gbDate.Enabled = true;
         btnDoReport.Enabled = true;
         rbAgent.Enabled = false;
         cbAgent.Enabled = false;
         gbTime.Enabled = true;
         selectedReport = SCR_TIME_REPORT;
      }

      void btn_Click3(object sender, EventArgs e)
      {
         ResetPanel();
         gbDivision.Enabled = true;
         rbDiv.Enabled = true;
         rbDiv.Checked = true;
         gbDate.Enabled = true;
         btnDoReport.Enabled = true;
         rbAgent.Enabled = false;
         cbAgent.Enabled = false;
         gbTime.Enabled = true;
         selectedReport = DSC_REPORT;
      }
   }
}
