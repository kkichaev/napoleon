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
         Size = new System.Drawing.Size(800, 600);

         ToolStripButton btn = new System.Windows.Forms.ToolStripButton();
         btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         btn.Name = "btnOrgMtx";
         btn.Size = new System.Drawing.Size(101, 22);
         btn.Text = "Редактор типов торговых точек";
         btn.Click += new System.EventHandler((obj, arg) =>
         {
            new FmOrgTypeEditor().Show();
         });

         tb.Items.Add(btn);

         ToolStripMenuItem tsCoef = new ToolStripMenuItem("Offtake коэф.");
         tsCoef.Click += new EventHandler((o, e) => { Form f = new OffTakeCoeffEditor(); f.Show(); });
         tb.Items.Add(tsCoef);

         btn = new System.Windows.Forms.ToolStripButton();
         btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         btn.Name = "btnOrgMtx";
         btn.Size = new System.Drawing.Size(101, 22);
         btn.Text = "Запрет на возврат";
         btn.Click += new System.EventHandler((s, e) => { new FmRejectReturn().Show(); });

         tb.Items.Add(btn);
      }

   }
}
