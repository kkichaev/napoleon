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
            btn.Image = GRSoft.NapoleonManager.Properties.Resources.planogram_edit;
            btn.Name = "btnBrands";
            btn.Size = new System.Drawing.Size(101, 22);
            btn.Text = "Бренды";
            btn.Click += new System.EventHandler((obj, arg) =>
            {
               FmBrands.Open();
            });

            tb.Items.Add(btn);

            btn = new System.Windows.Forms.ToolStripButton();
            btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            btn.Image = GRSoft.NapoleonManager.Properties.Resources.ic_grid_on;
            btn.Name = "btnSuppl";
            btn.Size = new System.Drawing.Size(101, 22);
            btn.Text = "Поставщики";
            btn.Click += new System.EventHandler((obj, arg) =>
            {
               FmSuppl.Open();
            });

            tb.Items.Add(btn);

            setColor.Visible = false;
            tsbMatrixDesigner.Visible = false;
        }
    }
}
