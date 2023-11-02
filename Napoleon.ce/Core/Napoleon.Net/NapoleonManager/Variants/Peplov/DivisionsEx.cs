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
         btn.Name = "btnStartWork";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "Начало рабочего дня";
         btn.Click += new System.EventHandler((s, e) => { new FmStartWork().Show(); });
         btn.DisplayStyle = ToolStripItemDisplayStyle.Text;

         ToolStripSeparator sp = new ToolStripSeparator();
         tb.Items.Add(sp);
         tb.Items.Add(btn);
      }
   }
}
