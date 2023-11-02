using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class MainFormEx : MainForm
   {
      public MainFormEx() : base()
      {
         btnPriceRemnants.Visible = false;

         ToolStripButton rttReport = new System.Windows.Forms.ToolStripButton();
         rttReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         rttReport.Image = Properties.Resources.assort_ch;
         rttReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         rttReport.Name = "rttReport";
         rttReport.Size = new System.Drawing.Size(23, 22);
         rttReport.Text = "Отчет для дистрибутора";
         rttReport.Click += (o, e) => {
            if (!CheckIsMainDataPresents(true))
               return;

            FmDistrReport f = new FmDistrReport();
            f.SetPeriod(dtpBeginDate.Value.Date, GetRangeEndDate());
            f.Show();
         };
         tsbConfig.Items.Add(rttReport);
      }
   }
}
