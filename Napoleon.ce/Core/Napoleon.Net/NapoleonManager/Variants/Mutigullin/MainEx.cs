using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class MainEx : MainForm
   {
      public DataSet<string, NetOrg> dsNetOrg = new DataSet<string, NetOrg>(NetOrg.OBJECT_NAME);

      public MainEx()
      {
         ToolStripButton btn = new System.Windows.Forms.ToolStripButton();
         btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btn.Image = Properties.Resources.view_statistics;
         btn.ImageTransparentColor = System.Drawing.Color.Magenta;
         btn.Name = "btnDistrRep";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "Отчет о дистрибуции";
         btn.Click += new System.EventHandler((o, e) => { new FmDistrReport().Show(); });

         tsbConfig.Items.Add(btn);
      }

      protected override void AddUpdateDataSet(List<IDataSet> updSets)
      {
         base.AddUpdateDataSet(updSets);

         updSets.Add(dsNetOrg);
      }
   }
}
