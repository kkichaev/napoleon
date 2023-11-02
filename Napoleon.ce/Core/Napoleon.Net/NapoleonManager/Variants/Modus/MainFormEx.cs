using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   class MainFormEx : MainForm
   {
      private DataSet<int, Facing> dsFacing = new DataSet<int, Facing>(Facing.OBJECT_NAME);

      public MainFormEx()
      {
         //System.Windows.Forms.ToolStripButton btn = new System.Windows.Forms.ToolStripButton();
         //btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         //btn.Image = Properties.Resources.appointment_new_3;
         //btn.ImageTransparentColor = System.Drawing.Color.Magenta;
         //btn.Name = "btnStartWorkReport";
         //btn.Size = new System.Drawing.Size(23, 22);
         //btn.Text = "Время начала работы";
         //btn.Click += new System.EventHandler((o, e) => { StartWorkReport.Do(this); });
         //tsbConfig.Items.Add(btn);
      }

      protected override void AdjustFilterForDS(DateTime dateBegin, DateTime dateEnd)
      {
         base.AdjustFilterForDS(dateBegin, dateEnd);
         dsFacing.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd);
      }

      protected override void AddUpdateDataSet(List<IDataSet> updSets)
      {
         base.AddUpdateDataSet(updSets);
         updSets.Add(dsFacing);
      }
   }
}
