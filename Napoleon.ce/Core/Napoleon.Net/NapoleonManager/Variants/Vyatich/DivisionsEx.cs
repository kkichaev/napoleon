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
         ToolStripButton btn = new System.Windows.Forms.ToolStripButton();
         btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btn.Image = GRSoft.NapoleonManager.Properties.Resources.planogram_edit;
         btn.Name = "btnPlanograms";
         btn.Size = new System.Drawing.Size(101, 22);
         btn.Text = "Планограммы";
         btn.Click += new System.EventHandler((obj, arg) =>
         {
            new FmPlanograms().Show();
         });

         tb.Items.Add(btn);
      }
   }
}
