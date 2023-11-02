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
         button.Image = Properties.Resources.work_time_rpt;
         button.ImageTransparentColor = System.Drawing.Color.Magenta;
         button.Name = "btnReport";
         button.Size = new System.Drawing.Size(23, 22);
         button.Text = "Нарушения регламента работы";
         button.Click += new System.EventHandler((s, e) =>
         {
            new FmStartWorkReport().Show();
         }
          );

         tsbConfig.Items.Add(button);

      }
   }
}
