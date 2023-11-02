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
         ToolStripButton rttReport = new System.Windows.Forms.ToolStripButton();
         rttReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         rttReport.Image = Properties.Resources.assort_ch;
         rttReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         rttReport.Name = "rttReport";
         rttReport.Size = new System.Drawing.Size(23, 22);
         rttReport.Text = "Отчет по работе агента";
         rttReport.Click += (o, e) => { WorkReport.Do(this, dtpBeginDate.Value.Date, GetRangeEndDate()); };
         tsbConfig.Items.Add(rttReport);
      
      
         tsbConfig.Items.Add(rttReport);

         ToolStripButton button = new System.Windows.Forms.ToolStripButton();
         button.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         button.Image = Properties.Resources.abiword_3;
         button.ImageTransparentColor = System.Drawing.Color.Magenta;
         button.Name = "mtxtimw";
         button.Size = new System.Drawing.Size(23, 22);
         button.Text = "Oтчёт по времени пребывания в точке";
         button.Click += new System.EventHandler((s, e) => { ScriptTimeRpt.Do(dtpBeginDate.Value.Date, dtpEndDate.Value.Date, this); });

         tsbConfig.Items.Add(button);


      }
   }
}
