using GRSoft.NapoleonManager.Properties;
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
         ToolStripButton btn = new ToolStripButton();
         btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btn.Image = Resources.view_calendar_timeline;
         btn.ImageTransparentColor = System.Drawing.Color.Magenta;
         btn.Name = "btnTimeTracking";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "Учет рабочего времени";
         btn.Click += TimeTrackingClick;

         tsbConfig.Items.Add(btn);
      }

      private void TimeTrackingClick(object o, EventArgs e)
      {
         if (CheckIsMainDataPresents(false))
            new FmTimeTracking().Show();
         else
            MessageBox.Show("Нажмите Обновить!");
      }
   }

   
}
