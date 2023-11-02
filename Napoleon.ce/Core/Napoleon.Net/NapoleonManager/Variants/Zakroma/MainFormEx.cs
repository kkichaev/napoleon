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
         ToolStripButton button = new System.Windows.Forms.ToolStripButton();
         button.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         button.Image = Properties.Resources.audit_frg;
         button.ImageTransparentColor = System.Drawing.Color.Magenta;
         button.Name = "btnOVReport";
         button.Size = new System.Drawing.Size(23, 22);
         button.Text = "Отчёт о посещениях и заказах.";
         button.Click += new System.EventHandler(ovrpt_Click);

         tsbConfig.Items.Add(button);

         button = new System.Windows.Forms.ToolStripButton();
         button.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         button.Image = Properties.Resources.work_time_rpt;
         button.ImageTransparentColor = System.Drawing.Color.Magenta;
         button.Name = "rttReport";
         button.Size = new System.Drawing.Size(23, 22);
         button.Text = "Отчет по синхронизации";
         button.Click += new System.EventHandler((o, e) => { SyncRpt.Do(this, GetBeginDateForSelection(), GetRangeEndDate()); });

         tsbConfig.Items.Add(button);

         button = new System.Windows.Forms.ToolStripButton();
         button.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         button.Image = Properties.Resources.view_statistics;
         button.ImageTransparentColor = System.Drawing.Color.Magenta;
         button.Name = "rttSumReport";
         button.Size = new System.Drawing.Size(23, 22);
         button.Text = "Итоговый отчет подразделения";
         button.Click += new System.EventHandler((o, e) => { GRSoft.NapoleonManager.SummaryReport.Do(this, GetBeginDateForSelection(), GetRangeEndDate()); });

         tsbConfig.Items.Add(button);
      }

      private void ovrpt_Click(object sender, EventArgs e)
      {
         FmOrdVztRep form = new FmOrdVztRep();
         form.Start = GetBeginDateForSelection();
         form.Finish = GetRangeEndDate();

         form.Show();
      }
   }
}
