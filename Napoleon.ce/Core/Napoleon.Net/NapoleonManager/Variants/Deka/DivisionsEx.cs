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
         ToolStripButton btn = new ToolStripButton();
         btn = new System.Windows.Forms.ToolStripButton();
         btn.Name = "btnOrgMatrix";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "Товары";
         btn.Click += new System.EventHandler((s, e) => { new FmPricePhoto().Show(); });
         btn.DisplayStyle = ToolStripItemDisplayStyle.Text;

         ToolStripSeparator sp = new ToolStripSeparator();
         tb.Items.Add(sp);
         tb.Items.Add(btn);

         btn = new System.Windows.Forms.ToolStripButton();
         btn.Name = "btnOrgMatrix";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "Контрагенты";
         btn.Click += new System.EventHandler((s, e) => { new FmContragents().Show(); });
         btn.DisplayStyle = ToolStripItemDisplayStyle.Text;
         tb.Items.Add(btn);

         btn = new System.Windows.Forms.ToolStripButton();
         btn.Name = "btnOrgMatrix";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "Дистрибюторы";
         btn.Click += new System.EventHandler((s, e) => { new FmDistributors().Show(); });
         btn.DisplayStyle = ToolStripItemDisplayStyle.Text;
         tb.Items.Add(btn);
      }
   }
}
