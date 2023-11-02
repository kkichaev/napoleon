using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class FmPriceEx : FmPrice
   {
      ToolStripComboBox cbSklads;
      private DataSet<string, OrderAddConfig> dsConfig;

      public FmPriceEx()
      {
         cbSklads = new ToolStripComboBox();
         toolStrip1.Items.Add(cbSklads);

         dsConfig = (DataSet<string, OrderAddConfig>)DataModule.Get(OrderAddConfig.OBJECT_NAME) ?? new DataSet<string, OrderAddConfig>(OrderAddConfig.OBJECT_NAME, false);
         
      }

      private void cbSklads_SelectedIndexChanged(object sender, EventArgs e)
      {
         CreatePriceTree();
      }

      protected override void BeforeRefresh(List<IDataSet> updSet)
      {
         base.BeforeRefresh(updSet);
         updSet.Add(dsConfig);
      }

      protected override void BeforeProceeded()
      {
         base.BeforeProceeded();

         if (cbSklads.Items.Count == 0)
         {
            string SKLADS = "Склады";

            if (dsConfig.ContainsKey(SKLADS))
            {
               string[] val = dsConfig[SKLADS].value.Split(';');

               foreach (string v in val)
                  cbSklads.Items.Add(v.Substring(0, v.IndexOf('\t')).Trim());

            }

            if (cbSklads.Items.Count > 0)
               cbSklads.SelectedIndex = 0;

            cbSklads.SelectedIndexChanged += cbSklads_SelectedIndexChanged;
         }
      }

      protected override double GetQty(Price p)
      {
         if (cbSklads.SelectedIndex < 1 || p.whQty.Count <= cbSklads.SelectedIndex - 1)
            return base.GetQty(p);
         else
         {
            return p.whQty[cbSklads.SelectedIndex - 1].qty;
         }
      }
   }
}
