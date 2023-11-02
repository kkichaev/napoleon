using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class MainFormEx : MainForm
   {
      public MainFormEx()
      {
         ToolStripButton tsb = new System.Windows.Forms.ToolStripButton();
         tsb.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         tsb.Image = Properties.Resources.return_doc;
         tsb.ImageTransparentColor = System.Drawing.Color.Magenta;
         tsb.Name = "rttReturnReport";
         tsb.Size = new System.Drawing.Size(23, 22);
         tsb.Text = "Отчёт по взовратам";
         tsb.Click += new System.EventHandler(delegate(object sender, EventArgs e)
         {
            FmReturnReport form = new FmReturnReport();
            form.SetDate(dtpBeginDate.Value.Date, dtpBeginDate.Value.Date, GetSelectedAgent(), GetSelectedDivision());
            form.Show();
         });

         ToolStripButton rttReport = new System.Windows.Forms.ToolStripButton();
         rttReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         rttReport.Image = Properties.Resources.accessorieseditor;
         rttReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         rttReport.Name = "rttPlans";
         rttReport.Size = new System.Drawing.Size(23, 22);
         rttReport.Text = "Планы";
         rttReport.Click += new System.EventHandler(rttPlans_Click);

         tsbConfig.Items.Add(rttReport);
         tsbConfig.Items.Add(tsb);
      }

      private void rttPlans_Click(object sender, EventArgs e)
      {
         new FmAgentPlan().Show();
      }
   }
}
