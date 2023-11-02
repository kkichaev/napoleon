using GRSoft.NapoleonManager.Properties;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class DivisionsEx : Divisions
   {
      public DivisionsEx()
      {
         tsbMatrixDesigner.Visible = false;
         setColor.Visible = false;

         ToolStripButton btn = new System.Windows.Forms.ToolStripButton();
         btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btn.Name = "btn";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "База данных";
         btn.Click += new System.EventHandler((s, e) => { new FmDataBase().Show(); });
         btn.DisplayStyle = ToolStripItemDisplayStyle.Image;
         btn.Image = Resources.database_upload_24;

         ToolStripSeparator sp = new ToolStripSeparator();
         tb.Items.Add(sp);
         tb.Items.Add(btn);
      }
   }
}
