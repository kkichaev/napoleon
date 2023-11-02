using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;

namespace GRSoft.NapoleonManager
{
   public partial class FocusedItemsEditor : Form
   {
      DataSet<string, FocusedItems> dsFocused;
      FocusController controller;
      TreeSearch treeSearch;

      public FocusedItemsEditor()
      {
         InitializeComponent();

         dgvItems.AutoGenerateColumns = false;

         List<FocusedItemsItem> items = new List<FocusedItemsItem>();
         dgvItems.DataSource = items;

         dsFocused = new DataSet<string, FocusedItems>(FocusedItems.OBJECT_NAME, false);

         controller = new FocusController(cbAgents, dgvOrgs, tvFolders, tbSave, dsFocused, true);
         controller.BeforeSave += new EventHandler(controller_BeforeSave);
         controller.OrgChanged += new OrgChangedHandle(controller_OrgChanged);
         controller.Init();

         treeSearch = new TreeSearch(tvFolders, tbFind);
      }

      void controller_BeforeSave(object sender, EventArgs e)
      {
         List<String> removed = new List<string>();
         foreach (KeyValuePair<string, FocusedItems> kv in dsFocused)
            if (kv.Value.items.Count == 0)
               removed.Add(kv.Key);

         foreach (String key in removed)
            dsFocused.Remove(key);
      }

      void controller_OrgChanged(object sender, OrgChangedArgs args)
      {
         tbFind.Clear();

         FocusedItems curr = null;
         if (dsFocused.ContainsKey(args.newOrg.id))
            curr = dsFocused[args.newOrg.id];
         else
         {
            curr = new FocusedItems();
            curr.userid = controller.CurAgent.id;
            curr.id = args.newOrg.id;
            curr.items = new List<FocusedItemsItem>();
            dsFocused[args.newOrg.id] = curr;
         }

         if (curr.items.Count == 0)
         {
            FocusedItemsItem i = new FocusedItemsItem();
            i.id = "1";
            curr.items.Add(i);

            dgvItems.DataSource = curr.items;
            curr.items.Clear();
            dgvItems.DataSource = null;
         }
         dgvItems.DataSource = curr.items;
      }

      private void tsbDelItem_Click(object sender, EventArgs e)
      {
         if (dgvItems.SelectedRows.Count > 0)
         {
            List<FocusedItemsItem> items = (List<FocusedItemsItem>)dgvItems.DataSource;
            foreach (DataGridViewRow r in dgvItems.SelectedRows)
               items.Remove(r.DataBoundItem as FocusedItemsItem);

            dgvItems.DataSource = null;
            dgvItems.DataSource = items;

            controller.MarkDirty(true);
         }
      }

      private void tvFolders_NodeMouseDoubleClick(object sender, TreeNodeMouseClickEventArgs e)
      {
         Price p = e.Node.Tag as Price;
         if (p != null)
         {
            List<FocusedItemsItem> items = (List<FocusedItemsItem>)dgvItems.DataSource;
            foreach (FocusedItemsItem i in items)
            {
               if (i.id.Equals(p.id))
                  return;
            }

            FocusedItemsItem newItem = new FocusedItemsItem();
            newItem.id = p.id;
            newItem.price = p;
            items.Add(newItem);

            dgvItems.DataSource = null;
            dgvItems.DataSource = items;
            
            controller.MarkDirty(true);
         }
      }

      protected override void OnFormClosing(FormClosingEventArgs e)
      {
         base.OnFormClosing(e);

         if (!e.Cancel && !controller.CheckChanges())
            e.Cancel = true;
      }

      private void ClearFind(object sender, EventArgs e)
      {
         treeSearch.ClearFind();
      }
   }

   class FocusedItemsItem : GRSoft.Network.DataObject
   {
      public string id = "";

      [Reference("ManagerPrice,Price", "id")]
      public Price price;

      public String ItemName { get { return price != null ? price.Name : "<Товар с кодом '" + id + "'>"; } }
   }

   class FocusedItems : GRSoft.Network.DataObject
   {
      public static string OBJECT_NAME = "FocusedItems";

      public string userid = "";

      [KeyField]
      public string id = "";

      [ItemType(typeof(FocusedItemsItem))]
      public List<FocusedItemsItem> items = null;

      public bool ContainsItem(string id)
      {
         if (items != null)
         {
            foreach (FocusedItemsItem item in items)
               if (item.id == id)
                  return true;
         }

         return false;
      }
   }
}
