using GRSoft.NapoleonManager;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class MainFormEx : MainForm
   {
      public MainFormEx() :
         base()
      {
         tgvAgentsSummary.Columns[2].HeaderText = "Продажи";
         btnCensus.Visible = false;

         ToolStripButton rttReport = new System.Windows.Forms.ToolStripButton();
         rttReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         rttReport.Image = Properties.Resources.bonus_doc;
         rttReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         rttReport.Name = "rttReport";
         rttReport.Size = new System.Drawing.Size(23, 22);
         rttReport.Text = "Раппорт о неисправностях";
         rttReport.Click += (o, e) =>
         {
            FmDefectReport fm = new FmDefectReport();
            fm.Begin = GetBeginDateForSelection().Date;
            fm.End = GetRangeEndDate().Date;
            //fm.Agent = GetSelectedAgent();

            fm.Show();
         };
         tsbConfig.Items.Add(rttReport);
      }
   }
}
