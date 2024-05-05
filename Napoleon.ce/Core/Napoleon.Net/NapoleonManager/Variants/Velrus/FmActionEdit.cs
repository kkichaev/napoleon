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

      OrderAction itemData = new OrderAction();

      public FmActionEdit()
      {
         InitializeComponent();
         cbKind.SelectedIndex = 0;

         //gridItems.DataSource = itemData;
         //gridBonus.DataSource = bonusData;
      }

      private void FmActionEdit_Load(object sender, EventArgs e)
      {
         string soc = "";
         string sorg = "";

         if (Action != null)
         {
            soc = Action.cluster;
            sorg = Action.org;

            tbName.Text = Action.name;
            tbDescr.Text = Action.descr;
            pickerStart.Value = Action.start;
            pickerFinish.Value = Action.finish;

            cbKind.SelectedIndex = Action.kind;
            if (Action.sum > 0)
               tbSum.Text = Action.sum.ToString();

            cbMultiApply.Checked = Action.applyManyTimes > 0;
            if(Action.gift > 0)
            {
               rbGift.Checked = true;
            } else
            {
               rbDiscount.Checked = true;
               tbDiscount.Text = Action.discount.ToString();
            }

            foreach (OrderAction.Item item in Action.items)
               itemData.items.Add(item);

            foreach (OrderAction.Item item in Action.gifts)
               itemData.gifts.Add(item);

         }

         OrgCluster selOC = null;
         Org selOrg = null;
         foreach (OrgCluster oc in DivisionsEx.clusters.Data)
         {
            if(oc.id == soc)
            {
               selOC = oc;
            }
            cbCluster.Items.Add(oc);
         }

         foreach (Org o in DivisionsEx.commonOrgs.Data)
         {
            if(o.id == sorg)
            {
               selOrg = o;
            }
            cbOrg.Items.Add(o);
         }
         cbCluster.SelectedItem = selOC;
         cbOrg.SelectedItem = selOrg;

         gridItems.DataSource = new BindingList<OrderAction.Item>(itemData.items);
         gridBonus.DataSource = new BindingList<OrderAction.Item>(itemData.gifts);
      }

      private void FmActionEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         //if (DialogResult != DialogResult.OK)
         //   return;

         //if (Action == null)
         //{
         //   Action = new OrderAction()
         //   {
         //      id = GRSoft.Network.DataObject.GenId(),
         //      created = DateTime.Now
         //   };
         //}

         //Action.name = tbName.Text.Trim();
         //Action.descr = tbDescr.Text.Trim();
         //Action.start = pickerStart.Value;
         //Action.finish = pickerFinish.Value;
         //Action.items = collectItems();
      }

      //private List<OrderAction.ActionItem> collectItems()
      //{
      //   List<OrderAction.ActionItem> res = new List<OrderAction.ActionItem>();

      //   foreach (OrderAction.ActionItem item in itemData)
      //      res.Add(item);

      //   return res;
      //}

      void AddItems(BindingList<OrderAction.Item> src)
      {
         Dictionary<string, bool> used = new Dictionary<string, bool>();
         List<Price> selected = new List<Price>();
         foreach (OrderAction.Item i in src)
         {
            if (i.item != null)
            {
               selected.Add(i.item);
               used[i.id] = true;
            }
         }

         List<Price> prc = FmSelectSKU.SelectItems(this, selected, null, true);
         if (prc != null)
         {
            foreach (Price pi in prc)
            {
               if (used.ContainsKey(pi.id)) continue;

               OrderAction.Item item = src.AddNew();
               item.id = pi.id;
               item.item = pi;
               item.qty = 1;
            }
         }
      }

      private void btnItemAdd_Click(object sender, EventArgs e)
      {
         BindingList<OrderAction.Item> src = (BindingList<OrderAction.Item>)gridItems.DataSource;
         AddItems(src);
      }

      void RemoveItems(DataGridView dgv)
      {
         BindingList<OrderAction.Item> src = (BindingList<OrderAction.Item>)dgv.DataSource;
         foreach (DataGridViewRow dr in dgv.SelectedRows)
         {
            src.Remove((OrderAction.Item)dr.DataBoundItem);
         }
      }

      private void btnItemDel_Click(object sender, EventArgs e)
      {
         RemoveItems(gridItems);

      }

      private void btnBonusAdd_Click(object sender, EventArgs e)
      {
         BindingList<OrderAction.Item> src = (BindingList<OrderAction.Item>)gridBonus.DataSource;
         AddItems(src);
      }

      private void btnBonusDel_Click(object sender, EventArgs e)
      {
         RemoveItems(gridBonus);
      }

      private void cbKind_SelectedIndexChanged(object sender, EventArgs e)
      {
         bool setSelected = cbKind.SelectedIndex == 0;
         tbSum.Enabled = !setSelected;
         toolStrip1.Enabled = setSelected;
         gridItems.Enabled = setSelected;
         rbDiscount.Enabled = setSelected;
         if (!setSelected)
         {
            rbGift.Checked = true;
            tbDiscount.Enabled = false;
         }
      }

      private void rbDiscount_CheckedChanged(object sender, EventArgs e)
      {
         tbDiscount.Enabled = rbDiscount.Checked;
      }

      private void button1_Click(object sender, EventArgs e)
      {
         if(Action == null)
         {
            Action = new OrderAction();
            Action.id = Guid.NewGuid().ToString().Replace("-", "");
         }

         Action.name = tbName.Text;
         Action.descr = tbDescr.Text;
         Action.kind = cbKind.SelectedIndex;

         double.TryParse(tbSum.Text, out Action.sum);
         Action.start = pickerStart.Value;
         Action.finish = pickerFinish.Value;
         OrgCluster oc = cbCluster.SelectedItem as OrgCluster;
         if (oc != null)
            Action.cluster = oc.id;
         else
            Action.cluster = "";

         Org o = cbOrg.SelectedItem as Org;
         if (o != null)
            Action.org = o.id;
         else
            Action.org = "";
         Action.applyManyTimes = cbMultiApply.Checked ? 1 : 0;
         Action.gift = rbGift.Checked ? 1 : 0;
         Action.discount = 0;
         if (rbDiscount.Checked)
            double.TryParse(tbDiscount.Text, out Action.discount);

         Action.items = itemData.items;
         Action.gifts = itemData.gifts;
      }

      private void rbGift_CheckedChanged(object sender, EventArgs e)
      {

      }
   }
}
