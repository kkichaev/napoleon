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
         btn.Name = "btnOrgProp";
         btn.Size = new System.Drawing.Size(101, 22);
         btn.Text = "Зонирование";
         btn.Click += new System.EventHandler((obj, arg) =>
         {
            new FmAgentZones().Show();
         });

         tb.Items.Add(btn);
      }
   }
}
