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
         btn.Name = "btnReturnCause";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "Причины возврата";
         btn.Click += new System.EventHandler((s, e) => { new FmReturnCauseEdit().Show(); });
         btn.DisplayStyle = ToolStripItemDisplayStyle.Text;

         tb.Items.Add(btn);

         btn = new System.Windows.Forms.ToolStripButton();
         btn.Name = "btnCause";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "Причины для отсутствия";
         btn.Click += new System.EventHandler((s, e) => { new FmCauseEditor().Show(); });
         btn.DisplayStyle = ToolStripItemDisplayStyle.Text;

         tb.Items.Add(btn);

         ToolStripSeparator sp = new ToolStripSeparator();
         tb.Items.Add(sp);

         Size = new System.Drawing.Size(800, 600);

      }
   }
}
