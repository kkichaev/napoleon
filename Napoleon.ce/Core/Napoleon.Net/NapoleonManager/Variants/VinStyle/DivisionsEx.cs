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
         Size = new System.Drawing.Size(800,500);

         ToolStripButton btn = new System.Windows.Forms.ToolStripButton();
         btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         btn.Name = "btnRejectCause";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "Причины отсутствия";
         btn.Click += new System.EventHandler((s, e) => { new FmRejectCauseEdit().Show(); });

         ToolStripSeparator sp = new ToolStripSeparator();
         tb.Items.Add(sp);
         tb.Items.Add(btn);

         btn = new System.Windows.Forms.ToolStripButton();
         btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         btn.Name = "btnOrgMtx";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "Матрица дистрибьюции";
         btn.Click += new System.EventHandler((s, e) => { new OrgMatrix().Show(); });
         tb.Items.Add(btn);

         btn = new System.Windows.Forms.ToolStripButton();
         btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         btn.Name = "btnDMP";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "Редактор видов ДМП";
         btn.Click += new System.EventHandler((s, e) => { new FmDMPEdit().Show(); });
         tb.Items.Add(btn);
      }
   }
}
