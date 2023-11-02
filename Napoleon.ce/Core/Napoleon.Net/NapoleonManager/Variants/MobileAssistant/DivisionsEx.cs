using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class DivisionsEx : Divisions
   {
      public DivisionsEx()
      {
         tsbMatrixDesigner.Visible = false;

         System.Windows.Forms.ToolStripButton btn = new System.Windows.Forms.ToolStripButton();
         btn.Image = GRSoft.NapoleonManager.Properties.Resources.accessorieseditor;
         btn.Text = "Задачи";
         btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.ImageAndText;
         btn.Size = new System.Drawing.Size(212, 22);
         btn.Click += ((o, e) => { FmTasktemplateEdit.Open(); });
         tb.Items.Add(btn);
      }
   }
}
