using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Collections;

namespace GRSoft.Ads
{
   public partial class FmPrice : Form
   {
      private static FmPrice instance;
      DsFolders dsFolders;
      DsWarehouse dsWarehouse;

      public FmPrice()
      {
         InitializeComponent();
         dsFolders = (DsFolders)DataModule.Get(Folder.OBJECT_NAME) ?? new DsFolders(true);
         dsWarehouse = (DsWarehouse)DataModule.Get(Warehouse.OBJECT_NAME) ?? new DsWarehouse(true);

         saveObserver = new ChangesObserver<bool>(delegate(Boolean value)
         {
            btnSave.Enabled = value;
         });
      }

      public static void ShowInstance()
      {
         if (instance == null)
         {
            instance = new FmPrice();
            instance.Show();
         }
         else
            instance.Activate();
      }

      private void FmPrice_FormClosed(object sender, FormClosedEventArgs e)
      {
         instance = null;
      }

      private void btnAddFolder_Click(object sender, EventArgs e)
      {
         TreeNode selectedNode = tvFolders.SelectedNode;
         Folder folder = FmFolderEdit.ShowInstance(null);
         tvFolders.SelectedNode = selectedNode;

         if (folder != null)
         {
            if (!dsFolders.ContainsKey(folder.id))
            {
               dsFolders.Add(folder.id, folder);
               FolderTreeNode parent = GetSelectedFolder();
               TreeNode node = new FolderTreeNode(folder);

               if (parent != null)
               {
                  folder.parent = parent.Folder.id;
                  parent.Nodes.Add(node);
                  parent.Expand();
               }
               else
                  tvFolders.Nodes.Add(node);

               tvFolders.SelectedNode = node;

               saveObserver.Changed = true;
            }
            else
               MessageBox.Show("Ошибка при создании");
         }
      }

      private FolderTreeNode GetSelectedFolder()
      {
         return (FolderTreeNode)tvFolders.SelectedNode;
      }

      private void tvFolders_MouseUp(object sender, MouseEventArgs e)
      {
         TreeViewHitTestInfo info = tvFolders.HitTest(e.X, e.Y);
         tvFolders.SelectedNode = info.Node;
         UpdateWarehouseGrid();
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         List<ReplacedSet> list = new List<ReplacedSet>();

         list.Add(new ReplacedSet(dsWarehouse));
         list.Add(new ReplacedSet(dsFolders));
         DataModule.UpdateDataSet(null, null, list, Config.GetConfig().GetConnection());

         //list.Clear();
         //list.Add(new ReplacedSet(dsFolders));
         //DataModule.UpdateDataSet(null, null, list, Config.GetConfig().GetConnection());

         saveObserver.Changed = false;
      }

      private ChangesObserver<Boolean> saveObserver;

      private void btnEditGroup_Click(object sender, EventArgs e)
      {
         FolderTreeNode ftn = GetSelectedFolder();

         if (ftn != null)
         {
            if (FmFolderEdit.ShowInstance(ftn.Folder) != null)
            {
               ftn.Refresh();
               saveObserver.Changed = true;
            }
         }
      }

      private void FmPrice_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (saveObserver.Changed &&
               MessageBox.Show(this, "Сохранить изменения?", "Вопрос", 
               MessageBoxButtons.OKCancel) == DialogResult.OK)
            btnSave_Click(null, null);
      }

      private void MakeTree()
      {
         tvFolders.BeginUpdate();

         TreeNodeCollection nodes = tvFolders.Nodes;
         nodes.Clear();
         List<Folder> freeNode = new List<Folder> ();
         FolderTreeNode prop = null;

         foreach (Folder folder in dsFolders.Data)
         {
            FolderTreeNode candidat = new FolderTreeNode(folder);

            if (folder.parent.Equals(string.Empty))
               nodes.Add(candidat);
            else if ((prop =  GetTreeNode(folder.parent)) != null)
               prop.Nodes.Add(candidat);
            else
               freeNode.Add(folder);
         }

         while (freeNode.Count > 0)
         {
            IEnumerator<Folder> enumer = freeNode.GetEnumerator();

            bool listBroken = true;

            while (enumer.MoveNext())
               if ((prop = GetTreeNode(enumer.Current.parent)) != null)
               {
                  prop.Nodes.Add(new FolderTreeNode(enumer.Current));
                  freeNode.Remove(enumer.Current);
                  listBroken = false;
                  break;
               }

            if (listBroken)
               break;
         }

         tvFolders.EndUpdate();
      }

      private FolderTreeNode GetTreeNode(string p)
      {
         foreach (FolderTreeNode ftn in tvFolders.Nodes)
         {
            if (ftn.Folder.id.Equals(p))
               return ftn;
            else
            {
               FolderTreeNode result = GetTreeNode(p, ftn);

               if (result != null)
                  return result;
            }
         }

         return null;
      }

      private FolderTreeNode GetTreeNode(string p, FolderTreeNode ftn)
      {
         foreach (FolderTreeNode node in ftn.Nodes)
         {
            if (node.Folder.id.Equals(p))
               return node;
            else
            {
               FolderTreeNode result = GetTreeNode(p, node);

               if (result != null)
                  return result;
            }
         }

         return null;
      }

      class TreeMaker
      {
         Dictionary<string, Folder> tree = new Dictionary<string,Folder>();
         List<Folder> freeNodes = new List<Folder>();

         public TreeMaker()
         {
            tree.Add(string.Empty, null);
         }

         public void insert(Folder folder)
         { 
         }
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsFolders);
         list.Add(dsWarehouse);

         DataModule.SetDataRepsonceHandlers(DataModule_DataProcessed,
            DataModule_OnDataResponceError);
         FmWait.ShowForm(this,
            DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
            list, FmWait.ProgressIndicator));
      }

      void DataModule_DataProcessed(object sender, EventArgs e)
      {
         FmWait.CloseForm();
         DataModule.ClearEvents();
         Invoke(new InvokeDelegate(RefreshData));
      }

      void DataModule_OnDataResponceError(EDataResponse e)
      {
         FmWait.CloseForm();
         DataModule.ClearEvents();
         MessageBox.Show(e.Msg);
      }

      void RefreshData()
      {
         MakeTree();
         UpdateWarehouseGrid();
      }

      private void FmPrice_Load(object sender, EventArgs e)
      {
         btnSave.Enabled = false;
         btnRefresh_Click(null, null);
      }

      private void btnDelGroup_Click(object sender, EventArgs e)
      {
         FolderTreeNode selectedFolder = GetSelectedFolder();

         if (MessageBox.Show(this, "Удалить?", "Вопрос", MessageBoxButtons.OKCancel,
            MessageBoxIcon.Question) == DialogResult.OK)
         {
            if (selectedFolder != null)
            {
               RemoveFolders(selectedFolder);
               saveObserver.Changed = true;
               UpdateWarehouseGrid();
            }
         }
      }

      private void RemoveFolders(FolderTreeNode parent)
      {
         foreach(TreeNode node in parent.Nodes)
            RemoveFolders((FolderTreeNode)node);

         dsFolders.Remove(parent.Folder.id);
         tvFolders.Nodes.Remove(parent);

         List<string> itemsToRemove = new List<string>();

         foreach (Warehouse warehouse in dsWarehouse.Data)
            if (warehouse.folder.Equals(parent.Folder.id))
               itemsToRemove.Add(warehouse.id);

         foreach (string key in itemsToRemove)
            dsWarehouse.Remove(key);
      }

      private void btnAddPrice_Click(object sender, EventArgs e)
      {
         Warehouse warehouse = null;
         FolderTreeNode selectedFolder = GetSelectedFolder();

         if (selectedFolder != null &&
            selectedFolder.Folder != null &&
            (warehouse = FmPriceEdit.ShowInstance(null)) != null)
         {
            if (!dsWarehouse.ContainsKey(warehouse.id))
            {
               warehouse.folder = selectedFolder.Folder.id;
               dsWarehouse.Add(warehouse.id, warehouse);
               saveObserver.Changed = true;
               UpdateWarehouseGrid();
            }
            else
               MessageBox.Show("Ошибка при создании");
         }
      }

      private void UpdateWarehouseGrid()
      {
         FolderTreeNode selectedFolder = GetSelectedFolder();
         List<Warehouse> list = new List<Warehouse>();

         if (selectedFolder != null && selectedFolder.Folder != null)
         {
            foreach (Warehouse warehouse in dsWarehouse.Data)
               if (warehouse.folder.Equals(selectedFolder.Folder.id))
                  list.Add(warehouse);
         }

         dgvPrice.DataSource = list;
      }

      private void btnEditPrice_Click(object sender, EventArgs e)
      {
         if (dgvPrice.CurrentRow != null)
         {
            Warehouse selectedWarehouse = (Warehouse)dgvPrice.CurrentRow.DataBoundItem;

            if (selectedWarehouse != null &&
               FmPriceEdit.ShowInstance(selectedWarehouse) != null)
            {
               dgvPrice.Refresh();
               saveObserver.Changed = true;
            }
         }
      }

      private void btnDelPrice_Click(object sender, EventArgs e)
      {
         if (dgvPrice.CurrentRow != null)
         {
            Warehouse selectedWarehouse = (Warehouse)dgvPrice.CurrentRow.DataBoundItem;

            if (dsWarehouse.ContainsKey(selectedWarehouse.id) &&
               MessageBox.Show(this, "Удалить?", "Вопрос", MessageBoxButtons.OKCancel,
                  MessageBoxIcon.Question) == DialogResult.OK)
            {
               dsWarehouse.Remove(selectedWarehouse.id);
               saveObserver.Changed = true;
               UpdateWarehouseGrid();
            }
         }
      }
   }

   class ChangesObserver<StateType>
   {
      public delegate void OnChange<StateTye>(StateTye newValue);

      private StateType changed;
      private OnChange<StateType> onChange;

      public ChangesObserver(OnChange<StateType> onChange)
      {
         this.onChange = onChange;
      }

      public StateType Changed 
      { 
         get { return changed; } 
         set 
         { 
            changed = value;

            if (onChange != null)
               onChange(value);
         } 
      }
   }

   class FolderTreeNode : TreeNode
   {
      public FolderTreeNode(Folder folder)
         : base(folder.name)
      {
         Tag = folder;
      }

      public Folder Folder { get { return (Folder)Tag; } }

      public void Refresh()
      {
         Text = Folder.name;
      }
   }
}
