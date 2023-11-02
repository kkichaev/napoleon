using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class DivisionsEx : Divisions
   {
      public DivisionsEx()
      {
         ToolStripButton retCause;
         retCause = new System.Windows.Forms.ToolStripButton();
         retCause.Name = "btnPlans";
         retCause.Size = new System.Drawing.Size(23, 22);
         retCause.Text = "Общий и ПДЗ";
         retCause.Click += new System.EventHandler((s, e) => { FmAgentPlanAssign.Open(); });
         retCause.DisplayStyle = ToolStripItemDisplayStyle.Text;

         ToolStripButton retLimit = new System.Windows.Forms.ToolStripButton();
         retLimit.Name = "btnReturnLimit";
         retLimit.Size = new System.Drawing.Size(23, 22);
         retLimit.Text = "Фокусный план";
         retLimit.Click += new System.EventHandler((s, e) => { new FmAgentPlan().Show(); });
         retLimit.DisplayStyle = ToolStripItemDisplayStyle.Text;


         ToolStripSplitButton tsb = new ToolStripSplitButton();
         tsb.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         tsb.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            retCause,
            retLimit});
         tsb.Name = "tsb";
         tsb.Size = new System.Drawing.Size(108, 22);
         tsb.Text = "Планы";
         tb.Items.Add(tsb);
         retCause = new System.Windows.Forms.ToolStripButton();

         retCause.Name = "btnReturnCause";
         retCause.Size = new System.Drawing.Size(23, 22);
         retCause.Text = "Причины возврата";
         retCause.Click += new System.EventHandler((s, e) => { (new FmReturnCauseEditor()).Show(); });
         retCause.DisplayStyle = ToolStripItemDisplayStyle.Text;
         tb.Items.Add(retCause);

         tsbMatrixDesigner.Visible = false;

         Size = new System.Drawing.Size(Width + 200, Height + 50);
      }

      protected override void CheckData()
      {
         base.CheckData();
      }
   }
}
