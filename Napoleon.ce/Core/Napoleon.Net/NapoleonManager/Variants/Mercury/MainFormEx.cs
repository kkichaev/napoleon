using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class MainFormEx : MainForm
   {
      private DataSet<int, InvAudit> dsInvAudit = new DataSet<int,InvAudit>(InvAudit.OBJECT_NAME);

      public MainFormEx()
      {
         ToolStripButton btnAgentPlan = new System.Windows.Forms.ToolStripButton();
         btnAgentPlan.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnAgentPlan.Image = Properties.Resources.plan_editor;
         btnAgentPlan.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnAgentPlan.Name = "btnAgentPlan";
         btnAgentPlan.Size = new System.Drawing.Size(23, 22);
         btnAgentPlan.Text = "План";
         btnAgentPlan.Click += new System.EventHandler((o, e) => new FmAgentPlan().Show());

         tsbConfig.Items.Add(btnAgentPlan);
      }
      protected override void AddUpdateDataSet(List<IDataSet> updSets)
      {
         base.AddUpdateDataSet(updSets);
         updSets.Add(dsInvAudit);
      }

      protected override void AdjustFilterForDS(DateTime dateBegin, DateTime dateEnd)
      {
         base.AdjustFilterForDS(dateBegin, dateEnd);
         dsInvAudit.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd);
      }
   }

   public class DivisionSummaryEx : DivisionSummary
   {
      protected DivisionSummaryEx(DataSet<int, CommonConfig> dsConfig) : base(dsConfig)
      {
      }

      protected override void PostAddData()
      {
         IDataSet cdata = DataModule.Get(InvAudit.OBJECT_NAME);
         if (cdata != null)
            foreach (InvAudit g in cdata.Data)
               this.Add(g);
      }

      private void Add(InvAudit g)
      {
         if (ContainsKey(g.AgentID))
         {
            SummaryDataEx sd = (SummaryDataEx)this[g.AgentID];
            sd.Add(g);
         }
      }
   }

   public class SummaryDataEx : SummaryData
   {
      public SummaryDataEx(Agent a, DataSet<int, CommonConfig> config) : base(a, config) { }

      public void Add(InvAudit o)
      {
         AddOrg(o);
      }
   }
}
