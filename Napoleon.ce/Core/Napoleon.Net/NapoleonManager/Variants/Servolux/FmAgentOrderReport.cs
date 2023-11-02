using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmAgentOrderReport : Form, OrderReportParams
   {
      private DivisionList dsDivision = DivisionList.GetDataSet();
      Division selDivision;

      public FmAgentOrderReport()
      {
         InitializeComponent();
         
         cbGroupBy.SelectedIndex = 0;
         cbThermal.SelectedIndex = 0;

         dtpBegin.Value = MainForm.Instance.GetBeginDateForSelection();
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
      }

      private void RefreshData()
      {
         List<IDataSet> upd = new List<IDataSet>();
         if (dsDivision.Count == 0 && !MainForm.Instance.CheckIsMainDataPresents(true))
            return;

         cbDivisions.Items.Clear();
         foreach (Division d in dsDivision.Data)
            cbDivisions.Items.Add(d);

         if (selDivision != null)
            cbDivisions.SelectedItem = selDivision;
         else if (cbDivisions.Items.Count > 0)
            cbDivisions.SelectedIndex = 0;

         cbFirms.Items.Clear();
         cbFirms.SelectedIndex = cbFirms.Items.Add("<Все>");
         Factory.GetFactories().ForEach((x) =>
         {
            cbFirms.Items.Add(x);
         });

         cbAgents.Items.Clear();
         foreach (Division.DivisionAgent a in (CurrentUser.user as Manager).Division.GetAllAgents())
            if (a.agent != null && cbAgents.Items.Contains(a.agent) == false)
               cbAgents.Items.Add(a.agent);
      }

      public void SetDivision(Division current)
      {
         selDivision = current;
      }

      private void rbDivision_Click(object sender, EventArgs e)
      {
         cbAgents.Enabled = false;
         cbDivisions.Enabled = true;
      }

      private void rbAgents_Click(object sender, EventArgs e)
      {
         cbAgents.Enabled = true;
         cbDivisions.Enabled = false;
      }

      private void btnExcelReport_Click(object sender, EventArgs e)
      {
         AgentOrderReportData data = new AgentOrderReportData();
         if (rbDivision.Checked)
         {
            Division sel = (Division)cbDivisions.SelectedItem;
            data.division = sel.id;
            data.name = sel.name;
         } else
         {
            Agent sel = (Agent)cbAgents.SelectedItem;
            data.agent = sel.id;
            data.name = sel.name;
         }
         if (cbFirms.SelectedIndex > 0)
         {
            Factory fabric = (Factory)cbFirms.SelectedItem;
            data.firm = fabric.id;
            data.firmName = fabric.name;
         }
         if (cbThermal.SelectedIndex > 0)
            data.thState = (String)cbThermal.SelectedItem;

         data.start = dtpBegin.Value.Date;
         data.end = cbPeriod.Checked ? dtpEnd.Value.Date : data.start;

         if (rbByCreatedFld.Checked)
            data.docsFromCreated = 1;

         if (cbPiece.Checked)
            data.useQty = 1;
         if (cbPackets.Checked)
            data.usePack = 1;
         if (cbTotal.Checked)
            data.totals = 1;

         if ((data.useQty + data.usePack + data.totals) == 0)
            data.useQty = 1;

         data.groupBy = cbGroupBy.SelectedIndex;
         if (Config.GetConfig().isFullOrgName)
            data.fullOrgName = 1;

         ReportResult.DoReport("agent_ordes_report", data, this);
      }

      private void cbPeriod_CheckedChanged(object sender, EventArgs e)
      {
         dtpEnd.Enabled = cbPeriod.Checked;
      }
   }
}
