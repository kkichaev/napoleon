using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.Ads
{
   public partial class FmPriceEdit : Form
   {
      public FmPriceEdit()
      {
         InitializeComponent();
      }

      public static Warehouse ShowInstance(Warehouse warehous)
      {
         FmPriceEdit instance = new FmPriceEdit();
         Warehouse result = warehous ?? new Warehouse();

         if (warehous == null)
         {
            instance.Text = "Добавить";
            result.id = DateTime.Now.Ticks.ToString();
         }
         else
         {
            instance.Text = "Изменить";
            instance.tbName.Text = result.Name;
            instance.tbCost.Text = result.Cost;
            instance.tbQty.Text = result.Qty;
         }

         if (instance.ShowDialog() == DialogResult.OK)
         {
            result.name = instance.tbName.Text;
            result.cost = Double.Parse(instance.tbCost.Text);
            result.qty = Double.Parse(instance.tbQty.Text);
            return result;
         }

         return null;
      }

      private void FmPriceEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK)
         {
            if (tbName.Text.Trim().Length <= 0)
            {
               MessageBox.Show("Заполните поле \"Наименование\"");
               tbName.Focus();
               e.Cancel = true;
            }

            double cost;
            
            if (!Double.TryParse(tbCost.Text.Trim(), out cost))
            {
               MessageBox.Show("Неправильный формат цены");
               tbCost.Focus();
               e.Cancel = true;
            }

            double qty;
            tbQty.Text = "0";
            if (!Double.TryParse(tbQty.Text.Trim(), out qty))
            {
               MessageBox.Show("Неправильный формат кол-ва");
               tbQty.Focus();
               e.Cancel = true;
            }
         }
      }
   }
}
