using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class BonusPrice : UserControl, IBonus
   {
      public static readonly int CODE = 0;
      public event EventHandler ValueChanged;
      private Price selectedItem = null;

      public BonusPrice()
      {
          InitializeComponent();
      }

      private void btnSelect_Click(object sender, EventArgs e)
      {
         List<Price> presentPrices = new List<Price>();

         if(selectedItem != null)
            presentPrices.Add(selectedItem);

         List<Price> newPrices = FmSelectSKU.SelectItems(this, presentPrices, null, true);
         if (null != newPrices && newPrices.Count > 0)
         {
            SelectedItem = newPrices[0];
         }
      }

      public Price SelectedItem { get { return selectedItem; } set { selectedItem = value; tbItem.Text = selectedItem != null ? selectedItem.Name : string.Empty; } }

      public bool save(BonusDef bonus) 
      {
         double qty = 0.0;
         bool result = selectedItem != null && Double.TryParse(tbQty.Text.Trim(), out qty) && qty > 0;
         
         if (result)
         {
            bonus.item = selectedItem;
            bonus.iditem = selectedItem.id;
            bonus.qty = qty;
            bonus.type = CODE;
         }

         return result;
      }

      public void load(BonusDef curBonus)
      {
         SelectedItem = curBonus.item;
         tbQty.Text = curBonus.qty.ToString();
      }

      private void tbItem_TextChanged(object sender, EventArgs e)
      {
         fireValueChanged();
      }

      private void fireValueChanged()
      {
         if (ValueChanged != null)
            ValueChanged(this, EventArgs.Empty);
      }

      private void tbQty_TextChanged(object sender, EventArgs e)
      {
         fireValueChanged();
      }
   }
}
