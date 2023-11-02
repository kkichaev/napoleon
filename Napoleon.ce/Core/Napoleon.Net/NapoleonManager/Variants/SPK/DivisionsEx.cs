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

         ToolStripButton tsb;
         tsb = new System.Windows.Forms.ToolStripButton();
         tsb.Name = "btnReturnCause";
         tsb.Size = new System.Drawing.Size(23, 22);
         tsb.Text = "Причины для выкладки";
         tsb.Click += new System.EventHandler((s, e) => { new FmLayoutCauseEditor().Show(); });
         tsb.DisplayStyle = ToolStripItemDisplayStyle.Text;

         tb.Items.Add(tsb);

         tsb = new System.Windows.Forms.ToolStripButton();
         tsb.Name = "btnManagerQuestRep";
         tsb.Size = new System.Drawing.Size(23, 22);
         tsb.Text = "Отчет по анкетам";
         tsb.Click += new System.EventHandler((s, e) => { new FmManagerQuestRep().Show(); });
         tsb.DisplayStyle = ToolStripItemDisplayStyle.Image;
         tsb.Image = Properties.Resources.excel;

         tb.Items.Insert(11,tsb);

         tsb = new System.Windows.Forms.ToolStripButton();
         tsb.Name = "btnCause";
         tsb.Size = new System.Drawing.Size(23, 22);
         tsb.Text = "Причины для отсутствия";
         tsb.Click += new System.EventHandler((s, e) => { new FmCauseEditor().Show(); });
         tsb.DisplayStyle = ToolStripItemDisplayStyle.Text;

         tb.Items.Add(tsb);

         Size = new System.Drawing.Size(800, 600); 
      }
   }
}
