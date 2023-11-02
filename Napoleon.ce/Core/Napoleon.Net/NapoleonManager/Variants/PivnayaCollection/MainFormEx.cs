using GRSoft.Network;
using GRSoft.UILib;
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
         button.Image = Properties.Resources.abiword_3;
         button.ImageTransparentColor = System.Drawing.Color.Magenta;
         button.Name = "mtxtimw";
         button.Size = new System.Drawing.Size(23, 22);
         button.Text = "Oтчёт по таре";
         button.Click += new System.EventHandler((s, e) => { (new FmTareReport()).Show(); });

         tsbConfig.Items.Add(button);

      }
   }
}
