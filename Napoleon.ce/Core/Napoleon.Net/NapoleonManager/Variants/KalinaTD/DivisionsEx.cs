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
         btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         btn.Name = "btnOrderRemarkEditor";
         btn.Size = new System.Drawing.Size(101, 22);
         btn.Text = "Комментарий заявки";
         btn.Click += new System.EventHandler((obj, arg) => {new FmRemarkEditor().Show(); });

         ToolStripSeparator sp = new ToolStripSeparator();
         tb.Items.Add(sp);
         tb.Items.Add(btn);
      }
   }
}
