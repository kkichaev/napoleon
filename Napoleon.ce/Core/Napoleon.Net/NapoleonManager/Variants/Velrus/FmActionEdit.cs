using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmActionEdit : Form
   {
      public OrderAction Action { get; set; }

      private BindingList<OrderAction.ActionItem> itemData = new BindingList<OrderAction.ActionItem>();
      private BindingList<OrderAction.Item> bonusData = new BindingList<OrderAction.Item>();

      public FmActionEdit()
      {
         InitializeComponent();

         gridItems.DataSource = itemData;
         gridBonus.DataSource = bonusData;
      }

      private void FmActionEdit_Load(object sender, EventArgs e)
      {
         if (Action != null)
         {
            tbName.Text = Action.name;
            tbDescr.Text = Action.descr;
            pickerStart.Value = Action.start;
            pickerFinish.Value = Action.finish;

            foreach (OrderAction.ActionItem item in Action.items)
               itemData.Add(item);

         }
      }

      private void FmActionEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult != DialogResult.OK)
            return;

         if (Action == null)
         {
            Action = new OrderAction()
            {
               id = GRSoft.Network.DataObject.GenId(),
               created = DateTime.Now
            };
         }

         Action.name = tbName.Text.Trim();
         Action.descr = tbDescr.Text.Trim();
         Action.start = pickerStart.Value;
         Action.finish = pickerFinish.Value;
         Action.items = collectItems();
      }

      private List<OrderAction.ActionItem> collectItems()
      {
         List<OrderAction.ActionItem> res = new List<OrderAction.ActionItem>();

         foreach (OrderAction.ActionItem item in itemData)
            res.Add(item);

         return res;
      }

      private void btnItemAdd_Click(object sender, EventArgs e)
      {
         Price price = new Price();

         if (FmSelectSKU.SkuDialogQuery(this, out price) == System.Windows.Forms.DialogResult.OK && checkItems(itemData, price.id))
         {
            OrderAction.ActionItem item = new OrderAction.ActionItem();
            item.iditem = GRSoft.Network.DataObject.GenId();
            item.id = price.id;
            item.item = price;
            item.qty = 1;

            itemData.Add(item);
         }
      }

      private bool checkItems(IList list, string id)
      {
         bool res = true;

         foreach(OrderAction.Item item in list)
            if (item.id.Equals(id))
               return false;

         return res;
      }

      private void btnItemDel_Click(object sender, EventArgs e)
      {
         OrderAction.ActionItem item = gridItems.CurrentRow.DataBoundItem as OrderAction.ActionItem;

         if (item != null && DialogUtil.AskToDel(this))
         {
            itemData.Remove(item);
         }
      }

      private void gridItems_RowEnter(object sender, DataGridViewCellEventArgs e)
      {

         OrderAction.ActionItem item = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as OrderAction.ActionItem;

         if (item != null)
         {
            bonusData.Clear();

            foreach (OrderAction.Item bonus in item.bonus)
            {
               bonusData.Add(bonus);
            }
         }
      }

      private void btnBonusAdd_Click(object sender, EventArgs e)
      {
         OrderAction.ActionItem item = gridItems.CurrentRow.DataBoundItem as OrderAction.ActionItem;

         Price price = new Price();

         if (FmSelectSKU.SkuDialogQuery(this, out price) == System.Windows.Forms.DialogResult.OK && checkItems(bonusData, price.id))
         {
            OrderAction.Item b = new OrderAction.Item();
            b.id = price.id;
            b.item = price;
            b.qty = 1;

            item.bonus.Add(b);
            bonusData.Add(b);
         }
      }

      private void btnBonusDel_Click(object sender, EventArgs e)
      {
         OrderAction.ActionItem item = gridItems.CurrentRow.DataBoundItem as OrderAction.ActionItem;
         OrderAction.Item bonus = gridBonus.CurrentRow.DataBoundItem as OrderAction.Item;

         if (item != null && bonus != null && DialogUtil.AskToDel(this))
         {
            item.bonus.Remove(bonus);
            bonusData.Remove(bonus);
         }
      }
   }
}
