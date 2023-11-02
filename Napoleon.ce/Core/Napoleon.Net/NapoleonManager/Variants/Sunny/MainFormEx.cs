using GRSoft.NapoleonManager.Properties;
using GRSoft.Network;
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
         ToolStripButton btnDubDocs = new ToolStripButton();
         btnDubDocs.Click += OpenSKUReport;
         btnDubDocs.Image = Resources.excel;
         btnDubDocs.Name = "btnSKURep";
         btnDubDocs.Text = "Продажи SKU";
         btnDubDocs.DisplayStyle = ToolStripItemDisplayStyle.Image;
         tsbConfig.Items.Add(btnDubDocs);
      }

      private void OpenSKUReport(object sender, EventArgs e)
      {
         FmSunnySKUReport.Open();
      }
   }
}
