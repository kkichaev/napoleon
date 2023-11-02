using GRSoft.NapoleonManager.Utils;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
    class FmReportsEx : FmReports
    {
        public static readonly string WORK_TIME_REPORT_2 = "work_merch_report";
        public static readonly string WORK_TIME_REPORT = "work_report";
        public static readonly string ROUTE_CONTROL_REPORT = "route_control_report";

        public FmReportsEx()
        {
            panel4.Size = new System.Drawing.Size(832, 125);
            RichButton btn = new RichButton();
            btn.Location = new System.Drawing.Point(500, 0);
            btn.Size = new System.Drawing.Size(240, 55);
            btn.Description = "Отчет по работе агентов";
            btn.Caption = "Отчет по работе";
            btn.Click += btn_Click;
            panel4.Controls.Add(btn);

            btn = new RichButton();
            btn.Location = new System.Drawing.Point(3, 60);
            btn.Size = new System.Drawing.Size(240, 55);
            btn.Description = "Отчет по работе мерчандайзеров";
            btn.Caption = "Отчет по мерчандайзингу";
            btn.Click += btn_Click2;
            panel4.Controls.Add(btn);

            btn = new RichButton();
            btn.Location = new System.Drawing.Point(249, 60);
            btn.Size = new System.Drawing.Size(240, 55);
            btn.Description = "Контроль соответсвия маршрута";
            btn.Caption = "Контроль маршрута";
            btn.Click += RouteControlReportClick;
            panel4.Controls.Add(btn);
        }

        void RouteControlReportClick(object sender, EventArgs e)
        {
            ResetPanel();
            gbDivision.Enabled = true;
            cbDivision.Enabled = false;
            rbDiv.Checked = false;
            rbDiv.Enabled = false;
            rbAgent.Checked = true;
            cbAgent.Enabled = true;

            gbDate.Enabled = true;
            btnDoReport.Enabled = true;
            gbTime.Enabled = false;
            selectedReport = ROUTE_CONTROL_REPORT;
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
            selectedReport = WORK_TIME_REPORT_2;
        }
    }
}
