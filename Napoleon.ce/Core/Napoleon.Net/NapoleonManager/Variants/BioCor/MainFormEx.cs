using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class MainFormEx : MainForm
   {
      public static string ORG_FOLDERS_FILTER = "\"date\">=ToDate('{0:dd/MM/yyyy}') and \"date\"<=ToDate('{1:dd/MM/yyyy}')";

      public MainFormEx()
      {
         ToolStripButton btnOrg = new System.Windows.Forms.ToolStripButton();
         btnOrg.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnOrg.Image = Properties.Resources.monitor_doc;
         btnOrg.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnOrg.Name = "btnOrg";
         btnOrg.Size = new System.Drawing.Size(23, 22);
         btnOrg.Text = "Справочник организаций";
         btnOrg.Click += new System.EventHandler(btnOrg_Click);

         tsbConfig.Items.Add(btnOrg);

         ToolStripButton btn = new ToolStripButton();
         btn.Name = "btnVisitReport";
         btn.Image = global::GRSoft.NapoleonManager.Properties.Resources.abiword_3;
         btn.ImageTransparentColor = System.Drawing.Color.Magenta;
         btn.Size = new System.Drawing.Size(23, 22);
         btn.ToolTipText = "Отчет о визитах";
         btn.Click += btnVisitReport_Click;

         tsbConfig.Items.Add(btn);

         btnCensus.Visible = false;
         btnOrderReport.Visible = false;
         tgvAgentsSummaryCount.Visible = false;
         tgvAgentsSummarySum.Visible = false;
         tgvAgentsSummaryProgres.Visible = false;
         tsbMakeHtml.Visible = false;

         if (menuAgentsSummary.Items.Contains(smiRoute))
            menuAgentsSummary.Items.Remove(smiRoute);
      }

      void btnOrg_Click(object sender, EventArgs e)
      {
         new FmOrg().Show();
      }

      void btnVisitReport_Click(object sender, EventArgs e)
      {
         VisitReport.Do(dtpBeginDate.Value.Date, GetRangeEndDate(), this, string.Empty);
      }

      protected override void AdjustFilterForDS(DateTime dateBegin, DateTime dateEnd)
      {
         base.AdjustFilterForDS(dateBegin, dateEnd);

         dsOrgFolder.Filter = String.Format(ORG_FOLDERS_FILTER, dateBegin, dateEnd);
      }
   }

   class SummaryDataEx : SummaryData
   {
      public SummaryDataEx(Agent a, DataSet<int, CommonConfig> config) : base(a, config) { }

      public override List<OrgFolderItem> GetAgentRoute(DateTime date, System.Collections.ICollection dsOrgFolder)
      {
         foreach (OrgFolder of in dsOrgFolder)
            if (of.agent != null && of.agent.id.Equals(AgentID) && of.date.Date.Equals(date.Date))
                  return of.items;

         return null;
      }
   }
}
