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

         ToolStripButton btnOrg = new System.Windows.Forms.ToolStripButton();
         btnOrg.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnOrg.Image = Properties.Resources.monitor_doc;
         btnOrg.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnOrg.Name = "btnOrg";
         btnOrg.Size = new System.Drawing.Size(23, 22);
         btnOrg.Text = "Справочник организаций";
         btnOrg.Click += new System.EventHandler(btnOrg_Click);

         tb.Items.Add(btnOrg);
      }

      void btnOrg_Click(object sender, EventArgs e)
      {
         new FmOrg().Show();
      }
   }
}
