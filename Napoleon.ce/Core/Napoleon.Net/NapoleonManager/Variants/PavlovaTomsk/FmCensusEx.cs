using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class FmCensusEx : FmCensus 
   {
      public FmCensusEx()
      {
         ToolStripButton btn = new ToolStripButton();
         btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btn.Size = new System.Drawing.Size(101, 22);
         btn.Text = "Отчет Ценсус";
         btn.Image = Properties.Resources.excel;
         btn.Click += new System.EventHandler((o, e) => { PtzOrgRpt.Do(this);} );

         tsbConfig.Items.Add(btn);
      }
   }
}
