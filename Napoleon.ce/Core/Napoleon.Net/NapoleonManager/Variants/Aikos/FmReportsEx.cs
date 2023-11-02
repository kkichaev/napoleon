using GRSoft.NapoleonManager.Utils;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Drawing;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class FmReportsEx : FmReports
   {
      public static readonly string CORDIANT_STAFF_REPORT = "staff_report";
      public static readonly string CORDIANT_VISIT_REPORT = "visit_report";
      public static readonly string CORDIANT_KBOFFLINE_REPORT = "kboffline_report";
      public static readonly string CORDIANT_SKU_REPORT = "sku_report";
      public static readonly string CORDIANT_SKUTYPE_REPORT = "skutype_report";
      public static readonly string CORDIANT_KBNEW_REPORT = "kbnew_report";
      public static readonly string CORDIANT_MONITORING_REPORT = "monitoring_report";
      public static readonly string CORDIANT_REMNANT_REPORT = "remnant_report";

      public FmReportsEx()
      {
         panel5.Visible = false;
         panel6.Visible = false;
         panel4.Size = new System.Drawing.Size(832, 250);

         RichButton btn = new RichButton();
         btn.Location = new System.Drawing.Point(495, 3);
         btn.Size = new System.Drawing.Size(240, 51);
         btn.Description = "Персонал";
         btn.Caption = "Персонал";
         btn.Click += btn_Click;
         btn.Icon = Properties.Resources.work_report;
         panel4.Controls.Add(btn);

         Debug.WriteLine(String.Format("heigh: {0}, width: {1}", btnDistance.Size.Height, btnDistance.Size.Width));
         Debug.WriteLine(String.Format("heigh: {0}, width: {1}", btn.Size.Height, btn.Size.Width));

         //btn = new RichButton();
         //btn.Location = new System.Drawing.Point(3, 60);
         //btn.Size = new System.Drawing.Size(240, 51);
         //btn.Description = "Визиты";
         //btn.Caption = "Визиты";
         //btn.Click += btn_Click2;
         //btn.Icon = Properties.Resources.work_report;
         //panel4.Controls.Add(btn);

         //btn = new RichButton();
         //btn.Location = new System.Drawing.Point(249, 60);
         //btn.Size = new System.Drawing.Size(240, 51);
         //btn.Description = "КБ offline";
         //btn.Caption = "КБ offline";
         //btn.Click += btn_Click3;
         //btn.Icon = Properties.Resources.work_report;
         //panel4.Controls.Add(btn);

         //btn = new RichButton();
         //btn.Location = new System.Drawing.Point(495, 60);
         //btn.Size = new System.Drawing.Size(240, 51);
         //btn.Description = "SKU";
         //btn.Caption = "SKU";
         //btn.Click += btn_Click4;
         //btn.Icon = Properties.Resources.work_report;
         //panel4.Controls.Add(btn);

         //btn = new RichButton();
         //btn.Location = new System.Drawing.Point(3, 120);
         //btn.Size = new System.Drawing.Size(240, 51);
         //btn.Description = "SKU по типам РТТ";
         //btn.Caption = "SKU по типам РТТ";
         //btn.Click += btn_Click5;
         //btn.Icon = Properties.Resources.work_report;
         //panel4.Controls.Add(btn);

         btn = new RichButton();
         btn.Location = new System.Drawing.Point(249, 60);
         //btn.Location = new System.Drawing.Point(249, 120);
         btn.Size = new System.Drawing.Size(240, 51);
         btn.Description = "КБ new";
         btn.Caption = "КБ new";
         btn.Click += btn_Click6;
         btn.Icon = Properties.Resources.work_report;
         panel4.Controls.Add(btn);

         //btn = new RichButton();
         //btn.Location = new System.Drawing.Point(495, 120);
         //btn.Size = new System.Drawing.Size(240, 51);
         //btn.Description = "Ценовой мониторинг";
         //btn.Caption = "Ценовой мониторинг";
         //btn.Click += btn_Click7;
         //btn.Icon = Properties.Resources.work_report;
         //panel4.Controls.Add(btn);

         btn = new RichButton();
         btn.Location = new System.Drawing.Point(3, 60);
         //btn.Location = new System.Drawing.Point(3, 180);
         btn.Size = new System.Drawing.Size(240, 51);
         btn.Description = "Представление";
         btn.Caption = "Представление";
         btn.Click += btn_Click8;
         btn.Icon = Properties.Resources.work_report;
         panel4.Controls.Add(btn);
      }

      private void btn_Click8(object sender, EventArgs e)
      {
         ResetPanel();
         gbDivision.Enabled = true;
         rbDiv.Enabled = true;
         rbDiv.Checked = true;
         gbDate.Enabled = true;
         btnDoReport.Enabled = true;
         rbAgent.Enabled = true;
         cbAgent.Enabled = true;
         gbTime.Enabled = false;
         selectedReport = CORDIANT_REMNANT_REPORT;
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
         selectedReport = CORDIANT_STAFF_REPORT;
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
         selectedReport = CORDIANT_VISIT_REPORT;
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
         selectedReport = CORDIANT_KBOFFLINE_REPORT;
      }

      void btn_Click4(object sender, EventArgs e)
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
         selectedReport = CORDIANT_SKU_REPORT;
      }

      void btn_Click5(object sender, EventArgs e)
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
         selectedReport = CORDIANT_SKUTYPE_REPORT;
      }

      void btn_Click6(object sender, EventArgs e)
      {
         ResetPanel();
         gbDivision.Enabled = true;
         rbDiv.Enabled = false;
         rbDiv.Checked = false;
         gbDate.Enabled = false;
         btnDoReport.Enabled = true;
         rbAgent.Enabled = true;
         rbAgent.Checked = true;
         cbAgent.Enabled = true;
         gbTime.Enabled = false;
         selectedReport = CORDIANT_KBNEW_REPORT;
      }

      void btn_Click7(object sender, EventArgs e)
      {
         ResetPanel();
         gbDivision.Enabled = true;
         rbDiv.Enabled = false;
         rbDiv.Checked = false;
         gbDate.Enabled = true;
         btnDoReport.Enabled = true;
         rbAgent.Enabled = true;
         cbAgent.Enabled = true;
         rbAgent.Checked = true;
         gbTime.Enabled = false;
         selectedReport = CORDIANT_MONITORING_REPORT;
      }

      protected override Network.DataObject CreateParam(string selectedReport)
      {
         ReportParam res = (ReportParam)base.CreateParam(selectedReport);

         if (selectedReport.Equals(CORDIANT_STAFF_REPORT) ||
            selectedReport.Equals(CORDIANT_VISIT_REPORT) ||
            selectedReport.Equals(CORDIANT_KBOFFLINE_REPORT)) 
         {
            res.start = new DateTime(res.start.Year, res.start.Month, 1);
            res.finish = new DateTime(res.finish.Year, res.finish.Month, DateTime.DaysInMonth(res.finish.Year, res.finish.Month));
         }
         //else if (selectedReport.Equals(CORDIANT_SKU_REPORT))
         //{
         //   res.start = new DateTime(res.start.Year, FmCPlan.GetQuartedStartMonth(FmCPlan.GetQuarted(res.start)), 1);
         //   res.finish = res.start.AddMonths(3);
         //}
         else if (selectedReport.Equals(CORDIANT_SKUTYPE_REPORT))
         {
            res.start = new DateTime(res.start.Year, res.start.Month, 1);
            res.finish = res.start.AddMonths(1);
         }

         return res;
      }
   }
}
