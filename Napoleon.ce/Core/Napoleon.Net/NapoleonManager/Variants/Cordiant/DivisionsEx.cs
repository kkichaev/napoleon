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
            btn.Name = "btnPriceLoad";
            btn.Size = new System.Drawing.Size(101, 22);
            btn.Text = "Загрузка товаров";
            btn.Click += new System.EventHandler((obj, arg) =>
            {
                new FmPriceLoad().Show();
            });

            tb.Items.Add(btn);

            btn = new System.Windows.Forms.ToolStripButton();
            btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            btn.Image = GRSoft.NapoleonManager.Properties.Resources.ic_grid_on;
            btn.Name = "btnTimeSheet";
            btn.Size = new System.Drawing.Size(101, 22);
            btn.Text = "Табель учета рабочего времени";
            btn.Click += new System.EventHandler((obj, arg) =>
            {
                new FmTimeSheet().Show();
            });

            tb.Items.Add(btn);

            btn = new System.Windows.Forms.ToolStripButton();
            btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            btn.Image = GRSoft.NapoleonManager.Properties.Resources.time_shedule;
            btn.Name = "btnPlan";
            btn.Size = new System.Drawing.Size(101, 22);
            btn.Text = "План";
            btn.Click += new System.EventHandler((obj, arg) =>
            {
               new FmCPlan().Show();
            });

            tb.Items.Add(btn);

            setColor.Visible = false;
            tsbMatrixDesigner.Visible = false;
        }
    }
}
