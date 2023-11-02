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
         retCause.Name = "btnReturnCause";
         retCause.Size = new System.Drawing.Size(23, 22);
         retCause.Text = "Причины отказа";
         retCause.Click += new System.EventHandler((s, e) => { new FmFocusRejectReason().Show(); });
         retCause.DisplayStyle = ToolStripItemDisplayStyle.Text;

         ToolStripButton retLimit = new System.Windows.Forms.ToolStripButton();
         retLimit.Name = "btnReturnLimit";
         retLimit.Size = new System.Drawing.Size(23, 22);
         retLimit.Text = "Редактор матриц";
         retLimit.Click += new System.EventHandler((s, e) => { new FmFocusMatrix().Show(); });
         retLimit.DisplayStyle = ToolStripItemDisplayStyle.Text;


         ToolStripSplitButton tsb = new ToolStripSplitButton();
         tsb.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         tsb.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            retLimit, retCause,
            });
         tsb.Name = "tsb";
         tsb.Size = new System.Drawing.Size(108, 22);
         tsb.Text = "Фокусный ассортимент";
         tb.Items.Add(tsb);

      }
   }
}
