using GRSoft.NapoleonManager.Utils;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   
   class FmReportsEx : FmReports
   {
      public static readonly string WORK_TIME_REPORT = "rmr_agent_work_time_report";
      public static readonly string DISTRIB_REPORT = "rmr_distirb_report";
      public static readonly string GROUTE_REPORT = "rmr_route_detai_report";
      public static readonly string REPRESENTATION_REPORT = "rmr_representation_report";
      public static readonly string PLAN_SKU_REPORT = "rmr_plan_sku_report";

      public ComboBox cbNetwork;
      public GroupBox gbNet;

      public DataSet<string, Org> dsOrg;

      class ReportParamEx : ReportParam
      {
         public string brand = "";
      }

      public override FmReports.ReportParam CreateParamInstance()
      {
         return new ReportParamEx(); ;
      }

      public FmReportsEx()
      {
         dsOrg = (DataSet<string, Org>)DataModule.Get(Org.OBJECT_NAME) ?? new DataSet<String, Org>(Org.OBJECT_NAME);
         dsOrg.Filter = "\"userid\" is null or \"userid\" is not null";

         RichButton btn = new RichButton();

         btn.Caption = "Время работы в точке";
         btn.Checked = false;
         btn.Description = "Время работы в точке";
         btn.Icon = global::GRSoft.NapoleonManager.Properties.Resources.ic_av_timer;
         btn.Location = new System.Drawing.Point(249, 62);
         btn.Name = "btnVisit";
         btn.Size = new System.Drawing.Size(240, 52);

         btn.Click += new System.EventHandler(this.btnWorkTime_Click);

         panel2.Controls.Add(btn);

         btn = new RichButton();
         btn.Caption = "Дистрибуция";
         btn.Checked = false;
         btn.Description = "Отчет о наличие остатков в точке";
         btn.Icon = global::GRSoft.NapoleonManager.Properties.Resources.ic_list_alt;
         btn.Location = new System.Drawing.Point(495, 3);
         btn.Name = "btnDistrib";
         btn.Size = new System.Drawing.Size(240, 52);
         btn.Click += Distrib_Click;

         panel4.Controls.Add(btn);

         btn = new RichButton();
         btn.Caption = "Анализ посещаемости";
         btn.Checked = false;
         btn.Description = "План/факт выполнения маршрутов";
         btn.Icon = global::GRSoft.NapoleonManager.Properties.Resources.audit_frg;
         btn.Location = new System.Drawing.Point(3, 67);
         btn.Name = "btnRoutePlan";
         btn.Size = new System.Drawing.Size(240, 52);
         btn.Click += Route_Detail_Click;

         panel4.Controls.Add(btn);

         btn = new RichButton();
         btn.Caption = "Отчет по представленности";
         btn.Checked = false;
         btn.Description = "Представленность продукции по сетям";
         btn.Icon = global::GRSoft.NapoleonManager.Properties.Resources.work_report;
         btn.Location = new System.Drawing.Point(249, 67);
         btn.Name = "btnNetworkRepresentation";
         btn.Size = new System.Drawing.Size(240, 52);
         btn.Click += NetworkRepresentation_Click;

         panel4.Controls.Add(btn);

         btn = new RichButton();
         btn.Caption = "План/Факт SKU";
         btn.Checked = false;
         btn.Description = "План/Факт по SKU в визитах";
         btn.Icon = global::GRSoft.NapoleonManager.Properties.Resources.work_report;
         btn.Location = new System.Drawing.Point(495, 67);
         btn.Name = "btnPlanFaktSku";
         btn.Size = new System.Drawing.Size(240, 52);
         btn.Click += PlanFaktSku_Click;

         panel4.Controls.Add(btn);

         panel4.Height += 64;
		 
		   label3.Visible = false;
		   panel6.Visible = false;

         Size = new System.Drawing.Size(1213, 630);

         gbNet = new GroupBox();
         gbNet.Size = new System.Drawing.Size(351, 45);
         gbNet.Location = new System.Drawing.Point(0, 72);
         gbNet.Dock = DockStyle.Top;

         this.splitContainer1.Panel2.Controls.Clear();
         this.splitContainer1.Panel2.Controls.Add(this.pnlQuest);
         this.splitContainer1.Panel2.Controls.Add(this.gbField);
         this.splitContainer1.Panel2.Controls.Add(this.gbLayout);
         this.splitContainer1.Panel2.Controls.Add(this.gbGSM);
         this.splitContainer1.Panel2.Controls.Add(this.gbTime);
         this.splitContainer1.Panel2.Controls.Add(this.gbDate);
         this.splitContainer1.Panel2.Controls.Add(gbNet);
         this.splitContainer1.Panel2.Controls.Add(this.gbDivision);
         this.splitContainer1.Panel2.Controls.Add(this.btnDoReport);

         cbNetwork = new ComboBox();
         cbNetwork.Location = new System.Drawing.Point(118, 14);
         cbNetwork.Size = new System.Drawing.Size(228, 22);

         Label lbl = new Label();
         lbl.Text = "Сеть";
         lbl.Location = new System.Drawing.Point(27, 16);

         gbNet.Controls.Add(cbNetwork);
         gbNet.Controls.Add(lbl);

         gbDate.Location = new Point(0, 100);

         cbDivision.SelectedIndexChanged += cbDivision_SelectedIndexChanged;
      }

      private void PlanFaktSku_Click(object sender, EventArgs e)
      {
         ResetPanel();
         gbNet.Enabled = true;
         gbDivision.Enabled = true;
         gbDate.Enabled = true;
         rbDiv.Enabled = true;
         rbDiv.Checked = true;
         rbAgent.Enabled = false;
         cbAgent.Enabled = false;
         btnDoReport.Enabled = true;
         selectedReport = PLAN_SKU_REPORT;
      }

      void cbDivision_SelectedIndexChanged(object sender, EventArgs e)
      {
         Division d = ((ComboBox)sender).SelectedItem as Division;

         if (d != null)
         {
            List<string> ids = new List<string>();
            d.GetAllAgents().ForEach((a) => ids.Add(a.id));

            List<string> formats = new List<string>();

            foreach (Org o in dsOrg.Values)
            {
               if (ids.Contains(o.userid) && !formats.Contains(o.brand))
                  formats.Add(o.brand);
            }

            formats.Sort();
            formats.ForEach((f) => cbNetwork.Items.Add(f));

            if (cbNetwork.Items.Count > 0)
               cbNetwork.SelectedIndex = 0;
         }
      }

      private void NetworkRepresentation_Click(object sender, EventArgs e)
      {
         ResetPanel();
         gbNet.Enabled = true;
         gbDivision.Enabled = true;
         gbDate.Enabled = true;
         rbDiv.Enabled = true;
         rbDiv.Checked = true;
         rbAgent.Enabled = true;
         cbAgent.Enabled = false;
         btnDoReport.Enabled = true;
         selectedReport = REPRESENTATION_REPORT;
      }

      private void Route_Detail_Click(object sender, EventArgs e)
      {
         ResetPanel();
         gbDivision.Enabled = true;
         gbDate.Enabled = true;
         rbDiv.Enabled = true;
         rbDiv.Checked = true;
         rbAgent.Enabled = false;
         cbAgent.Enabled = false;
         btnDoReport.Enabled = true;
         selectedReport = GROUTE_REPORT;
      }

      private void Distrib_Click(object sender, EventArgs e)
      {
         ResetPanel();
         gbDivision.Enabled = true;
         gbDate.Enabled = true;
         rbDiv.Enabled = true;
         rbDiv.Checked = true;
         rbAgent.Enabled = false;
         cbAgent.Enabled = false;
         btnDoReport.Enabled = true;
         selectedReport = DISTRIB_REPORT;
      }

      protected override void btnVisit_Click(object sender, EventArgs e)
      {
         base.btnVisit_Click(sender, e);
         rbDiv.Enabled = true;
         rbDiv.Checked = true;
         rbAgent.Checked = false;
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

      protected override void BeforeRefresh(List<IDataSet> upd)
      {
         base.BeforeRefresh(upd);
         upd.Add(dsOrg);
      }

      protected override Network.DataObject CreateParam(string selectedReport)
      {
         ReportParamEx res = (ReportParamEx)base.CreateParam(selectedReport);

         if (cbNetwork.SelectedIndex >= 0 )
            res.brand = cbNetwork.SelectedItem.ToString();

         return res;
      }
   }
}
