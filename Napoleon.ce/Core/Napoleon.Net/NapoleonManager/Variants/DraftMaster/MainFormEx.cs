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
         ToolStripButton btnRemn = new ToolStripButton();
         btnRemn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnRemn.Image = Properties.Resources.return_doc;
         btnRemn.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnRemn.Name = "btnRemn";
         btnRemn.Size = new System.Drawing.Size(23, 22);
         btnRemn.Text = "Отчет по возвратным кегам";
         btnRemn.Click += new System.EventHandler((o, e) => { RemnantsReport.Do(GetBeginDateForSelection(), GetRangeEndDate().AddDays(-1), this); });

         tsbConfig.Items.Add(btnRemn);
      }
   }
}
