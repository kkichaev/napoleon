using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class MainEx : MainForm
   {
      public MainEx()
      {
         ToolStripButton button = new System.Windows.Forms.ToolStripButton();
         button.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         button.Image = Properties.Resources.everyday_report;
         button.ImageTransparentColor = System.Drawing.Color.Magenta;
         button.Name = "btnReport";
         button.Size = new System.Drawing.Size(23, 22);
         button.Text = "Отчет о посещениях";
         button.Click += new System.EventHandler((s, e) =>
         {
            new FmVisitRpt().Show();
         }
          );

         tsbConfig.Items.Add(button);

         button = new System.Windows.Forms.ToolStripButton();
         button.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         button.Image = Properties.Resources.autoorderreport;
         button.ImageTransparentColor = System.Drawing.Color.Magenta;
         button.Name = "btnAutoOrderReport";
         button.Size = new System.Drawing.Size(23, 22);
         button.Text = "Отчет по автозаказу";
         button.Click += new System.EventHandler((s, e) =>
         {
            FmVisitRpt fmt = new FmVisitRpt();
            fmt.Text = "Отчет по автозаказу";
            fmt.ReportName = "autoorder_report";
            fmt.Show();
         }
          );

         tsbConfig.Items.Add(button);

         button = new System.Windows.Forms.ToolStripButton();
         button.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         button.Image = Properties.Resources.taskreport;
         button.ImageTransparentColor = System.Drawing.Color.Magenta;
         button.Name = "btnTaskReport";
         button.Size = new System.Drawing.Size(23, 22);
         button.Text = "Отчет по выполнению задач";
         button.Click += new System.EventHandler((s, e) =>
         {
            FmVisitRpt fmt = new FmVisitRpt();
            fmt.Text = "Отчет по выполнению задач";
            fmt.ReportName = "task_report";
            fmt.Show();
         }
          );

         tsbConfig.Items.Add(button);
      }
   }
}
