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
   public partial class FmFocusItemsEdit : Form
   {
      DataSet<string, Price> dsPrice = new DataSet<string, Price>(Price.OBJECT_NAME, false);
      DataSet<string, ManagerFolder> dsFolder = new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME, false);
      SimpleDataSet<OrgType> dsOrgTypes = new SimpleDataSet<OrgType>(OrgType.OBJECT_NAME, false);
      DataSet<string, FocusedItemsTC> dsFocused = new DataSet<string, FocusedItemsTC>(FocusedItemsTC.OBJECT_NAME, false);

      public FmFocusItemsEdit()
      {
         InitializeComponent();

         dgvBadItems.AutoGenerateColumns = false;
         dgvGoodItems.AutoGenerateColumns = false;
      }

      void DoRefresh()
      {
         DataModule.SetDataRepsonceHandlers(DataProcessed, DataConnectionError);
         List<IDataSet> updSet = new List<IDataSet>();
         if (dsFolder.Count == 0)
         {
            dsFolder.Filter = "not \"userid\" is null";
            //dsFolder.Filter = "\"userid\" is null or \"userid\"=''";
         }

         if (dsPrice.Count == 0)
         {
            dsPrice.Filter = "not \"userid\" is null";
            //dsPrice.Filter = "\"userid\" is null or \"userid\"=''";
         }

         updSet.Add(dsPrice);
         updSet.Add(dsFolder);
         updSet.Add(dsOrgTypes);
         updSet.Add(dsFocused);

         FmWait.ShowForm(this, DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
            updSet, FmWait.ProgressIndicator));
      }

      protected override void OnHandleCreated(EventArgs e)
      {
         base.OnHandleCreated(e);
         DoRefresh();
      }

      private void bntRefresh_Click(object sender, EventArgs e)
      {
         DoRefresh();
      }

      private void DataConnectionError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         Invoke(new EmptyParamHandler(delegate
         {
            const string TITLE = "Ошибка";

            MessageBox.Show(e.Msg, TITLE, MessageBoxButtons.OK,
               MessageBoxIcon.Error);
         }));
      }

      void DataProcessed(System.Object setnder, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
      
         Invoke(new EmptyParamHandler(delegate() { LoadData(); }));
      }

      private void LoadData()
      {
         ArticlesTreeConstructor atc = new ArticlesTreeConstructor(tvPrice, dsFolder, dsPrice);
         atc.MakeArticlesTree(0, 1);

         foreach(OrgType ot in dsOrgTypes.Data)
            tbOrgTypes.Items.Add(ot);
      }

      private void tbOrgTypes_SelectedIndexChanged(object sender, EventArgs e)
      {
         OrgType ot = tbOrgTypes.SelectedItem as OrgType;
         if (ot != null)
            LoadItems(ot.type);
      }

      string BadKey(String key) { return "None|" + key; }

      private void LoadItems(string orgType)
      {
         List<FocusedItemsTC.Item> goodItems = new List<FocusedItemsTC.Item>();
         List<FocusedItemsTC.Item> badItems = new List<FocusedItemsTC.Item>();

         if (!dsFocused.ContainsKey(orgType))
         {
            FocusedItemsTC fi = new FocusedItemsTC();
            fi.type = orgType;
            fi.items = new List<FocusedItemsTC.Item>();
            dsFocused[orgType] = fi;
         }
         dgvGoodItems.DataSource = dsFocused[orgType].items;

         String badKey = BadKey(orgType);
         if (!dsFocused.ContainsKey(badKey))
         {
            FocusedItemsTC fi = new FocusedItemsTC();
            fi.type = badKey;
            fi.items = new List<FocusedItemsTC.Item>();
            dsFocused[badKey] = fi;
         }
         dgvBadItems.DataSource = dsFocused[badKey].items;
      }

      private void btnDelGood_Click(object sender, EventArgs e)
      {
         OrgType ot = tbOrgTypes.SelectedItem as OrgType;
         if (ot != null)
            RemoveItems(dgvGoodItems, ot.type);
      }

      private void RemoveItems(DataGridView items, string type)
      {
         DataGridViewSelectedRowCollection sel = items.SelectedRows;
         if (sel.Count > 0 && MessageBox.Show("Удалить элементы?", "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes)
         {
            FocusedItemsTC fi = dsFocused[type];
            foreach (DataGridViewRow r in sel)
               fi.items.Remove(r.DataBoundItem as FocusedItemsTC.Item);

            items.DataSource = null;
            items.DataSource = fi.items;
            btnSave.Enabled = true;
         }
      }

      private void btnDelBad_Click(object sender, EventArgs e)
      {
         OrgType ot = tbOrgTypes.SelectedItem as OrgType;
         if (ot != null)
            RemoveItems(dgvBadItems, BadKey(ot.type));
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         SaveData();
      }

      private void SaveData()
      {
         ReplacedSet rs = new ReplacedSet(dsFocused);
         List<ReplacedSet> rpl = new List<ReplacedSet>();
         rpl.Add(rs);
         if (DataModule.UpdateDataSet(null, null, rpl, Config.GetConfig().GetConnection()))
            btnSave.Enabled = false;
         else
            MessageBox.Show("Ошибка при записи", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         base.OnClosing(e);
         if (btnSave.Enabled && MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes)
         {
            SaveData();
         }
      }

      private void tvPrice_ItemDrag(object sender, ItemDragEventArgs e)
      {
         TreeNode tn = e.Item as TreeNode;
         if (tn != null)
            tvPrice.DoDragDrop(tn, DragDropEffects.Copy);
      }

      private void DoDragEnter(object sender, DragEventArgs e)
      {
         OrgType ot = tbOrgTypes.SelectedItem as OrgType;
         if (ot == null)
            return;

         e.Effect = DragDropEffects.Copy;
      }

      FocusedItemsTC.Item HaveItem(List<FocusedItemsTC.Item> check, Price item)
      {
         foreach (FocusedItemsTC.Item i in check)
            if (i.price == item)
               return i;

         return null;
      }

      void AddItems(List<FocusedItemsTC.Item> dest, List<FocusedItemsTC.Item> check, TreeNodeCollection items, ref bool needAsk, ref bool confilctedAdding)
      {
         foreach (TreeNode node in items)
         {
            if (node.Tag is ManagerFolder)
            {
               AddItems(dest, check, node.Nodes, ref needAsk, ref confilctedAdding);
            }
            else
            {
               Price p = node.Tag as Price;
               if (p != null)
               {
                  FocusedItemsTC.Item checkItem = HaveItem(check, p);
                  if (checkItem != null)
                  {
                     if (needAsk)
                     {
                        confilctedAdding = (MessageBox.Show("Обнаружен товар из второго списка. Удалить его?", "Ошибка вставки",
                           MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes);
                        needAsk = false;
                     }

                     if (!confilctedAdding)
                        continue;

                     check.Remove(checkItem);
                  }
                  AddPriceItem(dest, p);
               }
            }
         }
      }

      private void DoDragDrop(object sender, DragEventArgs e)
      {
         OrgType ot = tbOrgTypes.SelectedItem as OrgType;
         if (ot == null)
            return;

         TreeNode tn = e.Data.GetData(typeof(TreeNode)) as TreeNode;
         if (tn != null )
         {
            DataGridView dgvDest = sender as DataGridView;
            DataGridView dgvCheck = ((sender == dgvBadItems) ? dgvGoodItems : dgvBadItems);

            List<FocusedItemsTC.Item> dest = dgvDest.DataSource as List<FocusedItemsTC.Item>;
            List<FocusedItemsTC.Item> check = dgvCheck.DataSource as List<FocusedItemsTC.Item>;

            bool refreshCheck = false;
            ManagerFolder mf = tn.Tag as ManagerFolder;
            if (mf != null)
            {
               bool needAsk = true;
               bool confilctedAdding = false;
               
               AddItems(dest, check, tn.Nodes, ref needAsk, ref confilctedAdding);
               
               refreshCheck = !needAsk;
            }
            else
            {
               Price p = tn.Tag as Price;
               if (p != null)
               {
                  FocusedItemsTC.Item checkItem = HaveItem(check, p);
                  if (checkItem != null)
                  {
                     if( MessageBox.Show("Удалить " + p.Name + " из второго списка?\nЕсли выберите Нет, товар не добавится.", "Ошибка вставки",
                           MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.No)
                     {
                        return;
                     }
                     check.Remove(checkItem);
                     refreshCheck = true;
                  }

                  AddPriceItem(dest, p);
               }
            }
            if (refreshCheck)
            {
               dgvCheck.DataSource = null;
               dgvCheck.DataSource = check;
            }

            dgvDest.DataSource = null;
            dgvDest.DataSource = dest;
            btnSave.Enabled = true;
         }
      }

      private void AddPriceItem(List<FocusedItemsTC.Item> dest, Price p)
      {
         if (HaveItem(dest, p) != null)
            return;

         FocusedItemsTC.Item add = new FocusedItemsTC.Item();
         add.id = p.id;
         add.price = p;
         dest.Add(add);
      }
   }

   class OrgType : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "OrgTypes";

      public string type = "";
      public string name = "";

      public override string ToString()
      {
         return name;
      }
   }

   class FocusedItemsTC : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "FocusedItems";

      public class Item : GRSoft.Network.DataObject
      {
         public string id = "";

         [Reference("ManagerPrice", "id", typeof(Price))]
         public Price price = null;

         public string Name { get { return (price == null) ? "товар с кодом <" + id + ">" : price.name; } }
      }

      [KeyField]
      public string type = "";

      [ItemType(typeof(Item))]
      public List<Item> items = null;
   }
}
