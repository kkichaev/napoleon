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
   public partial class ItemGroupEditor : Form
   {
      static ItemGroupEditor instance = null;

      SimpleDataSet<ItemGroup> groups = new SimpleDataSet<ItemGroup>(ItemGroup.OBJECT_NAME, false);
      DataSet<string, ManagerFolder> dsFolders;
      DataSet<string, Price> dsPrice;
      List<ItemGroup> data = new List<ItemGroup>();

      BindingList<ItemGroup> dataSource;

      public ItemGroupEditor()
      {
         InitializeComponent();

         dgvGroups.AutoGenerateColumns = false;
         dgvItems.AutoGenerateColumns = false;

         //dsFolders = (DataSet<string, ManagerFolder>)DataModule.Get(ManagerFolder.COMMON_FOLDERS_NAME) ??
         //   new DataSet<string, ManagerFolder>(ManagerFolder.COMMON_FOLDERS_NAME);
         dsFolders = (DataSet<string, ManagerFolder>)DataModule.Get(ManagerFolder.OBJECT_NAME) ??
            new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME);

         dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ??
            new DataSet<string, Price>(Price.OBJECT_NAME);

         groups.Filter = "(not (\"userid\" is null)) or \"userid\" is null";
      }

      public static void Open()
      {
         if (instance == null)
         {
            instance = new ItemGroupEditor();
            instance.Show();
         }
         else
         {
            instance.RefreshData();
            instance.BringToFront();
         }
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         base.OnClosing(e);
         if (!CheckChanges())
            e.Cancel = true;
      }

      protected override void OnClosed(EventArgs e)
      {
         base.OnClosed(e);
         instance = null;
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
      }

      void RefreshData()
      {
         List<IDataSet> updSets = new List<IDataSet>();
         if (dsFolders.Count == 0)
         {
            dsFolders.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
            updSets.Add(dsFolders);
         }

         if (dsPrice.Count == 0)
         {
            dsPrice.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
            updSets.Add(dsPrice);
         }

         updSets.Add(groups);
         FmWait.StdDataRefresh(this, updSets, DoLoadData, tsbRefresh);
      }

      void DoLoadData()
      {
         data.Clear();
         foreach (ItemGroup i in groups.Data)
         {
            data.Add(i);
         }

         dataSource = new SortableBindingList<ItemGroup>(data);
         dgvGroups.DataSource = dataSource;

         tsbSave.Enabled = false;
      }

      private void dgvGroups_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         if (dgvGroups.RowCount <= e.RowIndex)
         {
            dgvItems.DataSource = new SortableBindingList<ItemGroup.Item>();
            return;
         }

         ItemGroup item = dgvGroups.Rows[e.RowIndex].DataBoundItem as ItemGroup;
         if (item == null)
            return;
         dgvItems.DataSource = new SortableBindingList<ItemGroup.Item>(item.items);
         tsStatusText.Text = "Всего " + item.items.Count + " товаров";
      }

      private void tsbRefresh_Click(object sender, EventArgs e)
      {
         if (CheckChanges())
            RefreshData();
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
         SimpleDataSet<ItemGroup> items = new SimpleDataSet<ItemGroup>(ItemGroup.OBJECT_NAME, false);

         foreach (ItemGroup ig in data)
            if (ig.name.Length > 0)
               items.Add(ig);

         List<ReplacedSet> rpl = new List<ReplacedSet>();
         rpl.Add(new ReplacedSet(items));

         bool ret = DataModule.UpdateDataSet(null, null, rpl, Config.GetConfig().GetConnection());
         if (showDialog)
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");

         return ret;
      }

      private void tsbAddGroup_Click(object sender, EventArgs e)
      {
         ItemGroup newGroup = dataSource.AddNew();
         newGroup.id = GRSoft.Network.DataObject.GenId();
         newGroup.name = "";
         newGroup.userid = "";

         data.Add(newGroup);

         tsbSave.Enabled = true;
      }

      private void tsbSave_Click(object sender, EventArgs e)
      {
         tsbSave.Enabled = !SaveChanges(true);
      }

      private void tsbDelGroup_Click(object sender, EventArgs e)
      {
         List<ItemGroup> removed = new List<ItemGroup>();
         foreach(DataGridViewRow row in dgvGroups.SelectedRows)
            removed.Add(row.DataBoundItem as ItemGroup);

         removed.ForEach(x => { 
            dataSource.Remove(x);

            foreach (ItemGroup i in data)
            {
               if(i.id.Equals(x.id))
               {
                  data.Remove(i);
                  break;
               }
            }

         });
         dgvGroups.DataSource = null;
         dgvGroups.DataSource = dataSource;
         //dgvItems.DataSource = null;
         tsbSave.Enabled = true;
      }

      private void tsbDelItem_Click(object sender, EventArgs e)
      {
         if( dgvGroups.CurrentRow == null )
            return;

         ItemGroup item = dgvGroups.CurrentRow.DataBoundItem as ItemGroup;
         if (item == null)
            return;

         List<ItemGroup.Item> removed = new List<ItemGroup.Item>();
         foreach (DataGridViewRow row in dgvItems.SelectedRows)
            removed.Add(row.DataBoundItem as ItemGroup.Item);

         removed.ForEach(x => item.items.Remove(x));
         dgvItems.DataSource = null;
         dgvItems.DataSource = item.items;

         tsbSave.Enabled = true;
      }

      private void tsbAddItem_Click(object sender, EventArgs e)
      {
         if (dgvGroups.CurrentRow == null)
            return;

         List<Price> curList = new List<Price>();
         SortableBindingList<ItemGroup.Item> dsrc = (SortableBindingList<ItemGroup.Item>)dgvItems.DataSource;
         foreach (ItemGroup.Item i in dsrc)
            if (i.price != null)
               curList.Add(i.price);

         ItemGroup src = dgvGroups.CurrentRow.DataBoundItem as ItemGroup;
         List<Price> newSel = FmSelectPriceWithQTY.GetSelectedItems(this, curList, null, true);
         if (newSel != null)
         {
            src.items.Clear();
            foreach (Price p in newSel)
            {
               ItemGroup.Item item = new ItemGroup.Item();
               item.id = p.id;
               item.price = p;
               src.items.Add(item);
            }

            dsrc = new SortableBindingList<ItemGroup.Item>(src.items);
            dgvItems.DataSource = dsrc;

            tsbSave.Enabled = true;
         }
      }

      private void dgvGroups_CellEndEdit(object sender, DataGridViewCellEventArgs e)
      {
         tsbSave.Enabled = true;
      }

      private void tsbAgentSet_Click(object sender, EventArgs e)
      {
         if (!CheckChanges())
            return;
         FmSetItemGroups fm = new FmSetItemGroups();
         fm.Groups = groups;
         fm.ShowDialog();
      }
   }

   class FmSelectPriceWithQTY : FmSelectSKU
   {
      public FmSelectPriceWithQTY(List<Price> checkList, string userID) : base(checkList, userID)
      {

      }

      public override bool ApplyTreeNodeFilter(TreeNode treeNode)
      {
         Price p = treeNode.Tag as Price;
         if (p != null && p.qty == 0)
            return false;

         return base.ApplyTreeNodeFilter(treeNode);
      }

      public static List<Price> GetSelectedItems(IWin32Window owner, List<Price> checkList, string userID, bool checkingFolder)
      {
         FmSelectPriceWithQTY form = new FmSelectPriceWithQTY(checkList, userID);
         form.checkingFolder = checkingFolder;

         if (form.ShowDialog(owner) == DialogResult.OK)
            return form.checkedPrice;

         return null;
      }
   }

   class DefAgent : Agent
   {
      public DefAgent()
      {
         name = "По умолчанию";
      }
   }

   class FmSelectSKUEx : FmSelectSKU
   {
      public FmSelectSKUEx(List<Price> checkList, string userID)
         : base(checkList, userID)
      {

      }

      public static List<Price> DoSelectItems(IWin32Window owner, List<Price> checkList, string userID, bool checkingFolder)
      {
         FmSelectSKUEx form = new FmSelectSKUEx(checkList, userID);
         form.checkingFolder = checkingFolder;

         if (form.ShowDialog(owner) == DialogResult.OK)
            return form.checkedPrice;

         return null;
      }

      protected override void FillTreeView(TreeView treeView, DataSet<string, ManagerFolder> dsManagerFolder, DataSet<string, Price> dsPrice)
      {
         ArticlesTreeConstructorWithCondition a = new ArticlesTreeConstructorWithCondition(tvArticles, dsManagerFolder, dsPrice, this);
         a.GetPriceName = GetPriceName;
         a.MakeArticlesTree(0, 1, ((checkedPrice == null) ? (IsPriceChecked)null : PriceChecked));
      }

      string GetPriceName(Price p) { return String.Format("{0} ({1})", p.Name, p.qty); }
   }
}
