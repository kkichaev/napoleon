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
      BindingList<BonusDefItem> items;

      public FmBonus()
      {
         InitializeComponent();

         dgvBonusItems.AutoGenerateColumns = false;
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

         items = new BindingList<BonusDefItem>(curBonus.items);
         dgvBonusItems.DataSource = items;

         this.dtFromDate.ValueChanged += MarkChanged;
         this.dtTillDate.ValueChanged += MarkChanged;
         this.dgvBonusItems.CellValueChanged += MarkChanged;

         if(curBonus.type < cbBonusType.Items.Count)
            cbBonusType.SelectedIndex = curBonus.type;

         if (panel.Controls.Count == 1)
         {
            IBonus b = panel.Controls[0] as IBonus;
            b.ValueChanged += MarkChanged;
         }

         cbBonusType.SelectedIndexChanged += MarkChanged;
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
         if (!tsbSave.Enabled)
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

         tsbSave.Enabled = !result;

         if (showDialog)
            MessageBox.Show(result ? "Изменения сохранены" : "Ошибка при записи изменений");
         return result;
      }

      private void tsbAdd_Click(object sender, EventArgs e)
      {
         List<Price> presentPrices = new List<Price>();
         foreach (BonusDefItem item in items)
            if (null != item.price)
               presentPrices.Add(item.price);

         List<Price> newPrices = FmSelectSKU.SelectItems(this, presentPrices, null, true);
         if (null != newPrices)
         {
            RemoveUncheckedBonusItems(newPrices);
            AddCheckedBonusItems(newPrices);
            tsbSave.Enabled = true;
         }
      }

      private void AddCheckedBonusItems(List<Price> prices)
      {
         List<BonusDefItem> newItems = new List<BonusDefItem>();
         foreach (Price price in prices)
         {
            bool present = false;
            foreach (BonusDefItem item in curBonus.items)
            {
               if (null == item.price || price.id == item.price.id)
               {
                  present = true;
                  break;
               }
            }
            if (!present)
            {
               BonusDefItem newItem = items.AddNew();
               newItem.price = price;
               newItem.Qty = 0;
            }
         }
      }

      private void RemoveUncheckedBonusItems(List<Price> prices)
      {
         List<BonusDefItem> forRemove = new List<BonusDefItem>();
         foreach (BonusDefItem item in curBonus.items)
         {
            if (null == item.price)
               continue;

            bool present = false;
            foreach (Price price in prices)
            {
               if (price.id == item.price.id)
               {
                  present = true;
                  break;
               }
            }
            if (!present)
            {
               forRemove.Add(item);
            }
         }
         forRemove.ForEach(x => items.Remove(x));
      }

      private void tsbRemove_Click(object sender, EventArgs e)
      {
         DataGridViewSelectedRowCollection rows = dgvBonusItems.SelectedRows;
         foreach (DataGridViewRow row in rows)
         {
            BonusDefItem item = row.DataBoundItem as BonusDefItem;
            if (null != item)
            {
               items.Remove(item);
               tsbSave.Enabled = true;
            }
         }
      }

      private void tsbSave_Click(object sender, EventArgs e)
      {
         curBonus.Start = dtFromDate.Value.Date;
         curBonus.Till = dtTillDate.Value.Date;
         dgvBonusItems.CommitEdit(DataGridViewDataErrorContexts.Commit);

         bool st = false;

         if(panel.Controls.Count == 1)
         {
            IBonus ibonus = panel.Controls[0] as IBonus;

            if (ibonus.save(curBonus))
            {
               SaveChanges(true);
               st = true;
            }
         }

         if (!st)
            MessageBox.Show("Заполните данные для тип заявки(товар или сумма)");
      }

      private void MarkChanged(object sender, EventArgs e)
      {
         tsbSave.Enabled = true;
      }

      private void cbBonusType_SelectedIndexChanged(object sender, EventArgs e)
      {
         ToolStripComboBox checkbok = (ToolStripComboBox)sender;
         panel.Controls.Clear();
         switch (checkbok.SelectedIndex)
         { 
            case 0:
               panel.Controls.Add(new BonusPrice());
               break;
            case 1:
               panel.Controls.Add(new BonusSum());
               break;
         }

         if (panel.Controls.Count == 1)
         {
            IBonus b = panel.Controls[0] as IBonus;

            if(b != null)
               b.load(curBonus);
         }
      }
   }
}
