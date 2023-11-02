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
         btnOrder.Visible = false;
         btnRouteList.Visible = false;
         btnTask.Visible = false;

         GRSoft.NapoleonManager.Utils.RichButton btn = new Utils.RichButton();

         btn.Caption = "Отчет о визитах";
         btn.Checked = false;
         btn.Description = "Отчет по визитам агентов за период";
         btn.Icon = global::GRSoft.NapoleonManager.Properties.Resources.view_calendar_timeline;
         btn.Location = new System.Drawing.Point(495, 6);
         btn.Margin = new System.Windows.Forms.Padding(4);
         btn.Name = "btnTimeRep";
         btn.Padding = new System.Windows.Forms.Padding(1);
         btn.Size = new System.Drawing.Size(240, 55);
         btn.TabIndex = 0;
         btn.Click += VisitScriptReportClick;
         panel2.Controls.Add(btn);
         panel2.Controls.Remove(btnTask);
         panel2.Controls.Remove(btnRouteList);


         btn = new Utils.RichButton();
         btn.Caption = "Отчет о рабочем времени";
         btn.Checked = false;
         btn.Description = "Отчет по рабочему времени агентов";
         btn.Icon = global::GRSoft.NapoleonManager.Properties.Resources.work_time_rpt;
         btn.Location = new System.Drawing.Point(495, 3);
         btn.Margin = new System.Windows.Forms.Padding(4);
         btn.Name = "btnTimeRep";
         btn.Padding = new System.Windows.Forms.Padding(1);
         btn.Size = new System.Drawing.Size(240, 55);
         btn.TabIndex = 0;
         btn.Click += WorkTImeReportClick;
         panel4.Controls.Add(btn);

         btn = new Utils.RichButton();
         btn.Caption = "Отчет о продажах";
         btn.Checked = false;
         btn.Description = "Итоговый отчет о продажах";
         btn.Icon = global::GRSoft.NapoleonManager.Properties.Resources.document_revert_2;
         btn.Location = new System.Drawing.Point(3, 3);
         btn.Margin = new System.Windows.Forms.Padding(4);
         btn.Name = "btnTimeRep";
         btn.Padding = new System.Windows.Forms.Padding(1);
         btn.Size = new System.Drawing.Size(240, 55);
         btn.TabIndex = 0;
         btn.Click += SalesClick;
         panel6.Controls.Add(btn);
         panel6.Controls.Remove(btnOrder);

         LayoutControls();
      }

      void SalesClick(object sender, EventArgs e)
      {
         ResetPanel();

         gbDivision.Enabled = true;
         gbDate.Enabled = true;
         rbDiv.Enabled = true;
         rbAgent.Checked = true;
         btnDoReport.Enabled = true;
         selectedReport = "rmr_sales_report";
      }


      void WorkTImeReportClick(object sender, EventArgs e)
      {
         ResetPanel();

         gbDivision.Enabled = true;
         gbDate.Enabled = true;
         rbDiv.Enabled = true;
         rbDiv.Checked = true;

         rbAgent.Enabled = false;
         cbAgent.Enabled = false;

         btnDoReport.Enabled = true;
         selectedReport = "rmr_wrk_time_report";
      }

      public override void ResetPanel()
      {
         base.ResetPanel();
         rbAgent.Enabled = true;
         cbAgent.Enabled = true;
      }

      private void VisitScriptReportClick(object sender, EventArgs e)
      {
         ResetPanel();

         gbDivision.Enabled = true;
         gbDate.Enabled = true;
         rbDiv.Enabled = true;
         rbAgent.Checked = true;
         btnDoReport.Enabled = true;
         selectedReport = "rmr_visit_script_report";
      }
   }
}
