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

         btn.Caption = "Отчет по эффективности";
         btn.Checked = false;
         btn.Description = "Отчет по эффективности в разрезе по ТТ";
         btn.Icon = global::GRSoft.NapoleonManager.Properties.Resources.view_calendar_timeline;
         btn.Location = new System.Drawing.Point(249, 62);
         btn.Name = "btnTimeRep";
         btn.Size = new System.Drawing.Size(240, 52);
         btn.TabIndex = 0;
         btn.Click += ActionReportClick;

         panel2.Controls.Add(btn);

         btn = new Utils.RichButton();
         btn.Caption = "Отчёт по инкассации";
         btn.Checked = false;
         btn.Description = "Отчёт по инкассации";
         btn.Icon = global::GRSoft.NapoleonManager.Properties.Resources.incass_doc;
         btn.Location = new System.Drawing.Point(495, 62);
         btn.Name = "btnTimeRep";
         btn.Size = new System.Drawing.Size(240, 52);
         btn.TabIndex = 0;
         btn.Click += IncassReportClick;

         panel2.Controls.Add(btn);

         btn = new Utils.RichButton();
         btn.Caption = "Пребывание в точке";
         btn.Checked = false;
         btn.Description = "Oтчёт по времени пребывания в точке";
         btn.Icon = global::GRSoft.NapoleonManager.Properties.Resources.view_calendar_timeline;
         btn.Location = new System.Drawing.Point(495, 3);
         btn.Name = "btnScriptRep";
         btn.Size = new System.Drawing.Size(240, 52);
         btn.TabIndex = 0;
         btn.Click += ScriptReportClick;

         panel4.Controls.Add(btn);
      }

      private void ScriptReportClick(object sender, EventArgs e)
      {
         ResetPanel();

         gbDivision.Enabled = true;
         gbDate.Enabled = true;
         rbDiv.Enabled = true;
         rbAgent.Checked = true;
         btnDoReport.Enabled = true;
         selectedReport = "scrtime";
      }

      private void ActionReportClick(object sender, EventArgs e)
      {
         ResetPanel();

         gbDivision.Enabled = true;
         gbDate.Enabled = true;
         rbDiv.Enabled = true;
         rbAgent.Checked = true;
         btnDoReport.Enabled = true;
         selectedReport = "action";
      }

      private void IncassReportClick(object sender, EventArgs e)
      {
         ResetPanel();

         gbDivision.Enabled = true;
         gbDate.Enabled = true;
         rbDiv.Enabled = true;
         rbAgent.Checked = true;
         btnDoReport.Enabled = true;
         selectedReport = "incass_report";
      }
   }
}
