using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using System.Threading;

namespace GRSoft.NapoleonManager
{
   public partial class FmPriceEdit : Form
   {
      private DataSet<string, Price> dsPrice;
      private DataSet<string, ManagerFolder> dsFolder;
      
      public FmPriceEdit()
      {
         InitializeComponent();
         dgvPrice.AutoGenerateColumns = false;
         dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ?? new DataSet<string, Price>(Price.OBJECT_NAME);
         dsFolder = new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME, false);
         btnSave.Enabled = false;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         DataModule.SetDataRepsonceHandlers(DataProcessed, DataConnectionError);

         List<IDataSet> list = new List<IDataSet>();
         dsPrice.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
         dsFolder.Filter = DataUtils.USERID_IS_NULL_STR;

         list.Add(dsPrice);
         list.Add(dsFolder);

         Thread t = DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),list,
            FmWait.ProgressIndicator);
         FmWait.ShowForm(this, t);
      }

      void DataProcessed(System.Object setnder, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         Invoke(new EmptyParamHandler(delegate()
         {
            btnSave.Enabled = false;
            List<Price> data = new List<Price>();
            data.AddRange(dsPrice.Values);
            data.Sort(new Comparison<Price>(delegate(Price lhs, Price rhs){return lhs.Name.CompareTo(rhs.Name);}));
            BindingListView<Price> list = new BindingListView<Price>(data);
            
            dgvPrice.DataSource = list;

            ArticlesTreeConstructor t = new ArticlesTreeConstructor(tvPrice, dsFolder, dsPrice);
            t.MakeArticlesTree(0, 1);
         }));
      }

      //Произошла ошибка в соединении
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

      private void btnFilter_Click(object sender, EventArgs e)
      {
         BindingListView<Price> list = (BindingListView<Price>)dgvPrice.DataSource;

         if (list != null)
         {
            list.ApplyFilter(delegate(Price price) 
            { 
               return price.Name.ToUpper().Contains(tbFilter.Text.ToUpper()); 
            });
         }

      }

      private void btnResetFilter_Click(object sender, EventArgs e)
      {
         BindingListView<Price> list = (BindingListView<Price>)dgvPrice.DataSource;

         if (list != null)
         {
            tbFilter.Text = string.Empty;
            list.RemoveFilter();
         }
      }

      private void btnAddFolder_Click(object sender, EventArgs e)
      {
         TreeNode node = tvPrice.SelectedNode;

         if (node != null && node.Tag != null)
         {
            FmFolderEdit folderEdit = new FmFolderEdit();
            ManagerFolder parent = node.Tag as ManagerFolder;

            if (parent == null && node.Parent != null)
               parent = node.Parent.Tag as ManagerFolder;

            if (parent != null && 
               folderEdit.ShowDialog() == DialogResult.OK && 
               folderEdit.tbName.Text.Trim().Length > 0)
            {
               ManagerFolder f = new ManagerFolder();
               f.id = Folder.GenId();
               f.name = folderEdit.tbName.Text;
               f.level = parent.level + 1;

               TreeNode n = new TreeNode(f.name);
               n.Tag = f;
               node.Nodes.Add(n);

               dsFolder.Add(f.id, f);
               btnSave.Enabled = true;
            }
         }
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         DataSet<String, Folder> dsRplFolder = new DataSet<string, Folder>(Folder.OBJECT_NAME, false);
         DataSet<string, FID> dsFid = new DataSet<string, FID>(FID.OBJECT_NAME, false);

         fillRplSet(tvPrice.Nodes, dsRplFolder, dsFid);

         List<ReplacedSet> rplList = new List<ReplacedSet>();
         ReplacedSet setFolder = new ReplacedSet(null, dsRplFolder);
         ReplacedSet setFid = new ReplacedSet(dsFid);
         rplList.Add(setFolder);
         rplList.Add(setFid);

         if (!DataModule.UpdateDataSet(null, null, rplList, Config.GetConfig().GetConnection()))
         {
            MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK,
                  MessageBoxIcon.Error);
         }
         else
            btnSave.Enabled = false;
      }

      private void fillRplSet(TreeNodeCollection nodes, DataSet<string, Folder> dsFolder, DataSet<string, FID> dsFid)
      {
         foreach(TreeNode n in nodes)
         {
            if (n.Tag != null && n.Tag is ManagerFolder)
            {
               ManagerFolder mf = (ManagerFolder)n.Tag;
               Folder f = new Folder();
               f.fid = mf.id;
               f.name = mf.name;
               f.level = mf.level;

               dsFolder.Add(f.fid, f);

               if (n.Nodes.Count > 0)
                  fillRplSet(n.Nodes, dsFolder, dsFid);
            }
            else if (n.Tag != null && n.Parent != null && n.Parent.Tag != null && 
               n.Tag is Price && n.Parent.Tag is ManagerFolder)
            {
               Price p = (Price)n.Tag;
               FID fid = new FID();
               fid.fid = (n.Parent.Tag as ManagerFolder).id;
               fid.pid = p.id;

               if (!dsFid.ContainsKey(fid.pid))
                  dsFid.Add(fid.pid, fid);
            }
         }
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         TreeNode node = tvPrice.SelectedNode;

         if (node != null && node.Tag != null && node.Tag is ManagerFolder )
         {
            FmFolderEdit folderEdit = new FmFolderEdit();
            ManagerFolder f = (ManagerFolder)node.Tag;
            folderEdit.tbName.Text = f.name;

            if (folderEdit.ShowDialog() == DialogResult.OK && folderEdit.tbName.Name.Trim().Length > 0)
            {
               f.name = folderEdit.tbName.Text.Trim();
               node.Text = f.name;
               btnSave.Enabled = true;
            }
         }
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         TreeNode node = tvPrice.SelectedNode;

         if (node != null && node.Tag != null)
         {
            const string TITLE_STR = "Вопрос";
            const string MSF_STR = "Внимание запись будет удалена! Удалить?";

            if (MessageBox.Show(MSF_STR, TITLE_STR,
               MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
            {
               if (node.Tag is ManagerFolder)
               {
                  ManagerFolder f = (ManagerFolder)node.Tag;
                  dsFolder.Remove(f.id);
                  node.Remove();
                  btnSave.Enabled = true;
               }
               else if (node.Tag is Price)
               {
                  node.Remove();
                  btnSave.Enabled = true;
               }
            }
         }

      }

      private void FmPriceEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (btnSave.Enabled)
         {
            DialogResult dr = Divisions.AskToSaveChanges();

            if (dr == DialogResult.Cancel)
            {
               e.Cancel = true;
               return;
            }

            if (dr == DialogResult.Yes)
               btnSave.PerformClick();
         }
      }

      private void tvPrice_DragEnter(object sender, DragEventArgs e)
      {
         e.Effect = DragDropEffects.Move;
      }

      private void tvPrice_DragOver(object sender, DragEventArgs e)
      {
         e.Effect = DragDropEffects.Move;
         Point p = tvPrice.PointToClient(new Point(e.X, e.Y));

         TreeViewHitTestInfo info = tvPrice.HitTest(p);

         if (info != null && info.Node != null)
         {
            TreeNode node = info.Node;
            if (!(node.Tag is ManagerFolder) && node.Parent != null)
               node = node.Parent;

            tvPrice.SelectedNode = node;
         }
      }

      private void tvPrice_ItemDrag(object sender, ItemDragEventArgs e)
      {
         TreeNode[] selNodes = new TreeNode[tvPrice.SelectedNodes.Count];
         tvPrice.SelectedNodes.CopyTo(selNodes);
         tvPrice.DoDragDrop(selNodes, DragDropEffects.Move);
      }

      private void tvPrice_DragDrop(object sender, DragEventArgs e)
      {
         TreeNode node = tvPrice.SelectedNode;

         if (node != null && e.Data.GetDataPresent(typeof(TreeNode[])))
         {
            if (!(node.Tag is ManagerFolder) && node.Parent != null)
               node = node.Parent;

            TreeNode[] setNodes = (TreeNode[])e.Data.GetData(typeof(TreeNode[]));

            if (setNodes.Length == 1 && setNodes[0].Tag is ManagerFolder && 
               ((ManagerFolder)setNodes[0].Tag).id.Equals(((ManagerFolder)node.Tag).id))
               return;

            tvPrice.Invoke(new MoveNode(MoveNode), new object[] {node, setNodes});
            btnSave.Enabled = true;
         }
      }

      public void MoveNode(TreeNode dist, TreeNode[] arr)
      {
         foreach (TreeNode n in arr)
         {
            TreeNode c = (TreeNode)n.Clone();
            n.Remove();
            dist.Nodes.Add(c);
         }

         FixNodeLevel(dist);
         dist.Expand();
      }

      private void FixNodeLevel(TreeNode dist)
      {
         foreach (TreeNode node in dist.Nodes)
         {
            if (node.Nodes.Count > 0)
               FixNodeLevel(node);

            if (node.Tag is ManagerFolder && dist.Tag is ManagerFolder)
            {
               ManagerFolder f = (ManagerFolder)node.Tag;
               f.level = ((ManagerFolder)dist.Tag).level + 1;
            }
         }
      }

      private void tvPrice_MouseDown(object sender, MouseEventArgs e)
      {
         TreeViewHitTestInfo info = tvPrice.HitTest(e.X, e.Y);

         if (info != null && info.Node != null && tvPrice.SelectedNodes.Count == 0)
            tvPrice.SelectedNodes.Add(info.Node);
      }

      private void miTop_Click(object sender, EventArgs e)
      {
         if (tvPrice.SelectedNodes.Count > 0)
         {
            TreeNode[] selNodes = new TreeNode[tvPrice.SelectedNodes.Count];
            tvPrice.SelectedNodes.CopyTo(selNodes);

            foreach (TreeNode n in selNodes)
            {
               if (n.Tag is ManagerFolder)
               {
                  TreeNode node = (TreeNode)n.Clone();
                  n.Remove();
                  tvPrice.Nodes.Insert(0, node);
                  btnSave.Enabled = true;
                  ((ManagerFolder)node.Tag).level = 1;
                  FixNodeLevel(node);
               }
            }
         }
      }

      private void menu_Opening(object sender, CancelEventArgs e)
      {
         if (tvPrice.SelectedNodes.Count > 0)
         {
            foreach (TreeNode n in tvPrice.SelectedNodes)
            {
               if (!(n.Tag is ManagerFolder))
               {
                  e.Cancel = true;
                  return;
               }
            }
         }
         else
            e.Cancel = true;

         e.Cancel = false;
      }

      private void btnUp_Click(object sender, EventArgs e)
      {
         MoveNode(Direct.UPDIRECT);
      }

      private void MoveNode(Direct direct)
      {
         if (tvPrice.SelectedNodes.Count == 1)
         {
            TreeNode node = (TreeNode)tvPrice.SelectedNode.Clone();
            direct.idx = tvPrice.SelectedNode.Index;
            TreeNodeCollection parent = tvPrice.SelectedNode.Parent == null ?
                  tvPrice.Nodes : tvPrice.SelectedNode.Parent.Nodes;

            if (direct.IsAllow(parent.Count))
            {
               tvPrice.SelectedNode.Remove();
               parent.Insert(direct.GetIndex(), node);
               tvPrice.SelectedNode = node;
               btnSave.Enabled = true;
            }
         }
      }

      abstract class Direct
      {
         public int idx = 0;
         abstract public bool IsAllow(int limit);
         abstract public int GetIndex();

         public static Direct UPDIRECT = new UpDirect();
         public static Direct DOWNDIRECT = new DownDirect();
      }

      class UpDirect : Direct 
      {

         public override bool IsAllow(int limit) { return idx > 0; }

         public override int GetIndex() { return idx - 1; }
      }

      class DownDirect : Direct
      {
         public override bool IsAllow(int limit) { return idx < limit-1; }

         public override int GetIndex() { return idx + 1; }
      }
      
      private void btnDown_Click(object sender, EventArgs e)
      {
         MoveNode(Direct.DOWNDIRECT);
      }

      private void dgvPrice_MouseDoubleClick(object sender, MouseEventArgs e)
      {
         DataGridView.HitTestInfo info = dgvPrice.HitTest(e.X, e.Y);

         if (info != null && info.RowIndex >= 0)
         {
            ObjectView<Price> obj = (ObjectView<Price>)((DataGridView)sender).Rows[info.RowIndex].DataBoundItem;

            TreeNode node = tvPrice.SelectedNode;

            if (node != null)
            {
               if (!(node.Tag is ManagerFolder) && node.Parent != null)
                  node = node.Parent;

               if (node.Tag is ManagerFolder)
               {
                  TreeNode n = new TreeNode(((Price)obj.Object).Name);
                  n.Tag = obj.Object;
                  n.ImageIndex = 1;
                  n.SelectedImageIndex = 1;
                  node.Nodes.Add(n);

                  node.Expand();
                  btnSave.Enabled = true;
               }
            }
         }
      }
           
   }

   delegate void MoveNode(TreeNode dist, TreeNode[] arr);

   class FID : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "FID";
      [KeyField]
      public String pid = string.Empty;
      public String fid = string.Empty;
   }
}
