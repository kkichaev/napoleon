using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using System.Collections;

namespace GRSoft.NapoleonManager
{
   public partial class FmBonus : Form
   {
      static FmBonus instance = null;
      BonusDef curBonus = null;

      public FmBonus()
      {
         InitializeComponent();
      }

      public static FmBonus Open(BonusDef bonus)
      {
         if (instance == null)
         {
            instance = new FmBonus();
            instance.LoadBonus(bonus);
            instance.Show();
         }
         else
         {
            instance.LoadBonus(bonus);
            instance.BringToFront();
         }
         return instance;
      }

      protected void LoadBonus(BonusDef bonus)
      {
         curBonus = bonus;
         dtFromDate.Value = curBonus.Start.Date;
         dtTillDate.Value = curBonus.Till.Date;

         tbItem.Text = curBonus.item == null ? "" : curBonus.item.name;
         tbQty.Text = curBonus.qty.ToString();

         button1.Enabled = false;
      }

      protected override void OnClosed(EventArgs e)
      {
         base.OnClosed(e);
         instance = null;
      }
      
      protected override void OnClosing(CancelEventArgs e)
      {
         base.OnClosing(e);
         if (!CheckChanges())
            e.Cancel = true;
      }

      bool CheckChanges()
      {
         if (!button1.Enabled)
            return true;

         DialogResult dr = MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
         if (dr == DialogResult.No)
            return true;
         if (dr == DialogResult.Cancel)
            return false;

         return SaveChanges(false);
      }

      private bool SaveChanges(bool showDialog)
      {
         SimpleDataSet<BonusDef> bonusUpdateSet = new SimpleDataSet<BonusDef>(BonusDef.OBJECT_NAME, false, true);
         bonusUpdateSet.Add(curBonus);
         bool result = DataModule.UpdateDataSet(new List<IDataSet>(){ bonusUpdateSet }, null, null, Config.GetConfig().GetConnection());

         button1.Enabled = !result;

         if (showDialog)
            MessageBox.Show(result ? "Изменения сохранены" : "Ошибка при записи изменений");
         return result;
      }

      private void MarkChanged(object sender, EventArgs e)
      {
         button1.Enabled = true;
      }

      private void btnSelect_Click(object sender, EventArgs e)
      {
         List<Price> presentPrices = new List<Price>();

         Price selectedItem = curBonus.item;
         if (selectedItem != null)
            presentPrices.Add(selectedItem);

         List<Price> newPrices = FmSelectSKU.SelectItems(this, presentPrices, null, true);
         if (null != newPrices && newPrices.Count > 0)
         {
            curBonus.item = newPrices[0];
            curBonus.iditem = curBonus.item.id;
            MarkChanged(this, EventArgs.Empty);
         }

      }

      private void button1_Click(object sender, EventArgs e)
      {
         double qty = 0;
         Double.TryParse(tbQty.Text.Trim(), out qty);
         if( qty == 0)
         {
            MessageBox.Show("Укажите колиество товара");
            return;
         }
         if (curBonus.item == null )
         {
            MessageBox.Show("Выберите товар");
            return;
         }

         curBonus.Start = dtFromDate.Value.Date;
         curBonus.Till = dtTillDate.Value.Date;
         curBonus.qty = qty;

         SaveChanges(true);
      }

      private void button2_Click(object sender, EventArgs e)
      {
         Close();
      }
   }
}
