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
using GRSoft.UILib;

namespace GRSoft.NapoleonManager
{
   public partial class FmOrgMatrixDesigner : Form
   {
      private DataSet<string, OrgMatrix> dsMatrix;
      private DataSet<string, Price> dsPrice;
      private DataSet<string, ManagerFolder> dsFolder;
      private MatrixFromController controller;
      private SearchEngine searchEngine;

      public FmOrgMatrixDesigner()
      {
         InitializeComponent();
         InitDataSets();
         Init();
      }

      private void Init()
      {
         controller = new MatrixFromController(this);
         searchEngine = new SearchEngine(new FindDataGridObject(dgvPrice, 0)); 
      }

      private void InitDataSets()
      {
         dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ?? new DataSet<string, Price>(Price.OBJECT_NAME);
         dsFolder = new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME, false);
      }

      private void RefreshData()
      {
         DataModule.DataProcessed += new EventHandler(DataLoaded);

         if (cbAgents.SelectedItem as Agent != null)
         {
            string id = ((Agent)cbAgents.SelectedItem).id;
            dsMatrix = DataModule.GetUserDataSet(id, "Org", typeof(DataSet<string, OrgMatrix>)) as DataSet<string, OrgMatrix>;
            dsMatrix.Command = new ServerCommand(Commands.Impersonate(Commands.GET, id), dsMatrix.Name);
         }

         List<IDataSet> upd = new List<IDataSet>();
         
         if(dsPrice.Count == 0)
            upd.Add(dsPrice);

         if(dsMatrix != null)
            upd.Add(dsMatrix);

         upd.Add(dsFolder);

         dsFolder.Filter = DataUtils.USERID_IS_NULL_STR;
         dsPrice.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
         Thread t = DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), 
            upd, FmWait.ProgressIndicator);
         FmWait.ShowForm(this, t);
      }

      private void DataLoaded(object sender, EventArgs e)
      {
         DataModule.DataProcessed -= new EventHandler(DataLoaded);
         BeginInvoke(new EmptyParamHandler(ControlsFillAfterLoaded));
      }

      private void ControlsFillAfterLoaded()
      {
         FmWait.CloseForm();
         if (dsFolder.Count > 0)
         {
            ArticlesTreeConstructor t = new ArticlesTreeConstructor(tvPrice, dsFolder, dsPrice);
            t.MakeArticlesTree(0, 1);
            tsbFind.Visible = false;
            tsbFindBack.Visible = false;
            tstbFind.TextChanged += new EventHandler(tstbFind_TextChanged);
         }
         else
         {
            tstbFind.TextChanged -= new EventHandler(tstbFind_TextChanged);
            tsbFind.Visible = true;
            tsbFindBack.Visible = true;
            dgvPrice.BringToFront();
            DataUtils.FillGridFromDS(dgvPrice, dgvPrice.Columns[0], dsPrice);
         }
         FillMatrix();
      }

      void DoSearch(string str)
      {
         if (str.Length == 0)
         {
            ArticlesTreeConstructor t = new ArticlesTreeConstructor(tvPrice, dsFolder, dsPrice);
            t.MakeArticlesTree(0, 1);
         }
         else
         {
            tvPrice.SuspendLayout();

            tvPrice.Nodes.Clear();
            str = str.ToLower();
            foreach (Price p in dsPrice.Data)
            {
               if (p.name.ToLower().Contains(str))
               {
                  TreeNode newNode = new TreeNode(p.name);
                  newNode.Tag = p;
                  newNode.ForeColor = p.Color;
                  tvPrice.Nodes.Add(newNode);
               }
            }

            tvPrice.ResumeLayout();
         }
      }

      System.Threading.Timer textWait = null;
      void TimePassed(object o)
      {
         try
         {
            Mutex m = new Mutex(false, "FMMatrixMutex");
            if (m.WaitOne(0))
               tvPrice.Invoke( new InvokeParamHandler( 
                  delegate(object param) 
                  { 
                     DoSearch((string)param); 
                  }), new object[] {o} );
            m.ReleaseMutex();
         }
         catch(Exception)
         {
         }
      }

      void tstbFind_TextChanged(object sender, EventArgs e)
      {
         if (textWait != null)
            textWait.Dispose();
         textWait = new System.Threading.Timer(new TimerCallback(TimePassed), tstbFind.Text, 500, 0);
      }

      //class OrgNode : TreeGridNode
      //{
      //   public OrgMatrix matrix;

      //   public OrgNode(OrgMatrix orgMatrix, TreeGridView grid)
      //   {
      //      this.matrix = orgMatrix;
      //      TreeGridNode node = grid.Nodes.Add(matrix.Name);

      //      foreach (OrgMatrixItem item in matrix.matrix)
      //      {
      //         if (item != null && item.price != null)
      //         {
      //            TreeGridNode nodeItem = node.Nodes.Add(item.price.Name, item.qty, item.face);
      //            nodeItem.Tag = item;
      //         }
      //      }
      //   }
      //}

      private void FillMatrix()
      {
         tgvMatrix.Nodes.Clear();
         tgvMatrix.Rows.Clear();

         List<OrgMatrix> list = new List<OrgMatrix>();
         list.AddRange(dsMatrix.Values);
         list.Sort(new Comparison<OrgMatrix>(delegate(OrgMatrix lhs, OrgMatrix rhs)
            { return lhs.Name.CompareTo(rhs.Name); }));

         if (dsMatrix != null)
         {
            foreach (OrgMatrix matrix in list)
            {
               TreeGridNode node = tgvMatrix.Nodes.Add(matrix.Name);
               node.Tag = matrix.id;

               foreach (OrgMatrixItem item in matrix.matrix)
               {
                  if (item != null && item.price != null)
                  {
                     TreeGridNode nodeItem = node.Nodes.Add(item.price.Name, item.qty, item.face);
                     nodeItem.Tag = item;
                  }
               }

               node.Expand();
            }
         }
      }

      private void tsbRefresh_Click(object sender, EventArgs e)
      {
         RefreshData();
      }

      private void tvMatrix_DragEnter(object sender, DragEventArgs e)
      {
         e.Effect = DragDropEffects.Copy;
      }

      void AddPriceNodes(TreeGridNode parent, TreeNodeCollection col)
      {
         if (col == null)
            return;
         foreach (TreeNode tn in col)
         {
            if (tn.Tag is ManagerFolder)
               AddPriceNodes(parent, tn.Nodes);
            else if (tn.Tag is Price)
               AddPriceToMatrix(parent, tn.Tag as Price);
         }
      }

      void AddFolderToMatrix(TreeGridNode parent, TreeNode folder)
      {
         //DialogResult dr = MessageBox.Show("Добавить товары из папки?", "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question);
         //if (dr == DialogResult.Yes)
            AddPriceNodes(parent, folder.Nodes);
      }

      private void tvMatrix_DragDrop(object sender, DragEventArgs e)
      {
         
         //TreeNode targetNode = DataUtils.GetNodeFromPoint(tvMatrix, new Point(e.X, e.Y));

         //TreeGridNode node = tgvMatrix.Nodes.. tgvMatrix.PointToClient(new Point(e.X, e.Y));

         //if (targetNode != null)
         //{
         //   TreeNode parent = DataUtils.getTopParent(targetNode);
         //   if (e.Data.GetDataPresent(typeof(Price)))
         //   {
         //      AddPriceToMatrix(parent, e.Data.GetData(typeof(Price)) as Price);
         //   }
         //   else if (e.Data.GetDataPresent(typeof(TreeNode)))
         //   {
         //      TreeNode tn = e.Data.GetData(typeof(TreeNode)) as TreeNode;
         //      if (tn.Tag is Price)
         //         AddPriceToMatrix(parent, tn.Tag as Price);
         //      else if (tn.Tag is ManagerFolder)
         //         AddFolderToMatrix(parent, tn);
         //   }
         //   else if (e.Data.GetDataPresent(typeof(List<TreeNode>)))
         //   {
         //      List<TreeNode> nodes = e.Data.GetData(typeof(List<TreeNode>)) as List<TreeNode>;
         //      foreach (TreeNode tn in nodes)
         //      {
         //         if (tn.Tag is Price)
         //            AddPriceToMatrix(parent, tn.Tag as Price);
         //      }
         //   }
         //}
      }

      private void AddPriceToMatrix(TreeGridNode targetNode, Price price)
      {
         OrgMatrixItem matrixItem = new OrgMatrixItem();
         matrixItem.id = price.id;
         matrixItem.price = price;
         matrixItem.orgid = targetNode.Tag.ToString();

         bool contains = false;
         foreach (TreeGridNode tn in targetNode.Nodes)
         {
            if (tn.Tag is OrgMatrixItem)
            {
               OrgMatrixItem item = tn.Tag as OrgMatrixItem;
               if (item.price.id.Equals(price.id))
               {
                  contains = true;
                  break;
               }
            }
         }

         if (!contains)
         {
            TreeGridNode node = targetNode.Nodes.Add(matrixItem.price, matrixItem.qty, matrixItem.face);
            node.Tag = matrixItem;

            targetNode.Expand();
            controller.SetNoSaveStatus();
         }
      }

      private void dgvPrice_MouseDown(object sender, MouseEventArgs e)
      {
         DataUtils.beginDragAndDropOnDataGrid<Price>(sender as DataGridView, e);
      }

      private void tvMatrix_MouseDown(object sender, MouseEventArgs e)
      {
         //TreeNode node = tvMatrix.GetNodeAt(new Point(e.X, e.Y));

         //if (node == null)
         //{
         //   return;
         //}

         //tvMatrix.SelectedNode = node;
         //dgvPrice.Refresh();
      }

      private bool isMatrixContainsMatrix(string matrixName)
      {
         //foreach (TreeNode node in tvMatrix.Nodes)
         //{
         //   if (node.Text.ToUpper().Equals(matrixName.ToUpper()))
         //   {
         //      return true;
         //   }
         //}

        return false;
      }

      private DataSet<string, OrgMatrix> getMatrixDataSet()
      {
         foreach (TreeGridNode matrixNode in tgvMatrix.Nodes)
         {
            string id = matrixNode.Tag.ToString();
            if (dsMatrix.ContainsKey(id))
            {
               OrgMatrix om = dsMatrix[id];
               om.matrix.Clear();

               foreach (TreeGridNode i in matrixNode.Nodes)
               {
                  om.matrix.Add((OrgMatrixItem)i.Tag);
               }
            }
         }

         return dsMatrix;
      }

      private void tsbRemove_Click(object sender, EventArgs e)
      {
         TreeGridNode node = tgvMatrix.CurrentRow;

         if (node == null && node.Tag != null && node.Tag.ToString().Length <= 0)
         {
            return;
         }

         tgvMatrix.Rows.Remove(node);
         node.Parent.Nodes.Remove(node);
         
         controller.SetNoSaveStatus();
         dgvPrice.Refresh();
      }

      private void tsbUp_Click(object sender, EventArgs e)
      {
         MoveMatrixNode(Direction.UP);
      }

      private void tsbDown_Click(object sender, EventArgs e)
      {
         MoveMatrixNode(Direction.DOWN);
      }

      //Передвинуть пункт в матрице
      private void MoveMatrixNode(Direction direction)
      {
         TreeGridNode node = tgvMatrix.CurrentRow;
         int idx = node.RowIndex;

         if (node == null && node.Tag != null && node.Tag is OrgMatrixItem)
         {
            return;
         }

         TreeGridNode parent = node.Parent;

         string idorg = parent.Tag.ToString();
         OrgMatrixItem item = node.Tag as OrgMatrixItem;

         if (dsMatrix.ContainsKey(idorg))
         {
            int pos = dsMatrix[idorg].matrix.IndexOf(item);

            if ((pos + (int)direction) < 0 || (pos + (int)direction) >= dsMatrix[idorg].matrix.Count)
            {
               return;
            }

            dsMatrix[idorg].matrix.Remove(item);
            dsMatrix[idorg].matrix.Insert(pos + (int)direction, item);
            FillMatrix();

            tgvMatrix.CurrentCell = tgvMatrix.Rows[idx + (int)direction].Cells[0];

            controller.SetNoSaveStatus();
         }
      }

      private void tsbSave_Click(object sender, EventArgs e)
      {
         List<ReplacedSet> list = new List<ReplacedSet>();

         if (cbAgents.SelectedItem as Agent != null)
         {
            string id = ((Agent)cbAgents.SelectedItem).id;
            ReplacedSet rs = new ReplacedSet(id, getMatrixDataSet());

            list.Add(rs);
            if (DataModule.UpdateDataSet(null, null, list, Config.GetConfig().GetConnection()) == false)
            {
               MessageBox.Show("Ошибка при записи в базу данных.", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
            else
               controller.SetSaveStatus();
            
         }
      }

      private void FmOrgMatrixDesigner_Shown(object sender, EventArgs e)
      {
         //RefreshData();
      }


      /// <summary>
      /// Управление визуальными компонентами формы
      /// в зависимости от текущего статуса
      /// </summary>
      class MatrixFromController
      {
         FmOrgMatrixDesigner control;

         public MatrixFromController(FmOrgMatrixDesigner control)
         {
            this.control = control;
            Init();
         }

         private void Init()
         {
            control.tsbSave.Enabled = false;
         }

         public void SetNoSaveStatus()
         {
            control.tsbSave.Enabled = true;
         }

         public void SetSaveStatus()
         {
            control.tsbSave.Enabled = false;
         }
      }

      private void tsbFind_Click(object sender, EventArgs e)
      {
         if (dgvPrice.Rows.Count > 0)
            searchEngine.find(tstbFind.Text, Direction.DOWN);
      }

      private void tstbFind_KeyDown(object sender, KeyEventArgs e)
      {
         if (e.KeyCode == Keys.Enter)
            searchEngine.find(tstbFind.Text, Direction.DOWN);
      }

      private void tsbFindBack_Click(object sender, EventArgs e)
      {
         if (dgvPrice.Rows.Count > 0)
            searchEngine.find(tstbFind.Text, Direction.UP);
      }
      
      private void dgvPrice_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         //TreeNode node = tvMatrix.SelectedNode;

         //if (node != null)
         //{ 
         //   TreeNodeCollection nodes = node.Level == 0 ? node.Nodes : node.Parent.Nodes;
         //   bool inMatrix = false;

         //   foreach (TreeNode n in nodes)
         //      if (n.Tag is OrgMatrixItem && 
         //         ((OrgMatrixItem)n.Tag).orgid.Equals(((Price)e.Value).id))
         //      {
         //         inMatrix = true;
         //         break;
         //      }

         //   if (inMatrix)
         //      e.CellStyle.BackColor = Color.LightBlue;
         //}
      }

      private void tvPrice_ItemDrag(object sender, ItemDragEventArgs e)
      {
         if (e.Button == MouseButtons.Left)
         {
            List<TreeNode> sel = tvPrice.SelectedNodes;
            if (sel.Count > 1)
            {
               DoDragDrop(sel, DragDropEffects.Move | DragDropEffects.Copy);
            }
            else
            {
               TreeNode tn = e.Item as TreeNode;
               DoDragDrop(tn, DragDropEffects.Move | DragDropEffects.Copy);
            }
         }
      }

      private void tvPrice_NodeMouseDoubleClick(object sender, TreeNodeMouseClickEventArgs e)
      {
         //TreeNode parent = tvMatrix.SelectedNode;
         //if (parent == null)
         //{
         //   MessageBox.Show("Не выбрана матрица для добавления", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Stop);
         //   return;
         //}
         //parent = DataUtils.getTopParent(parent);
         //if (e.Node.Tag is Price)
         //   AddPriceToMatrix(parent, e.Node.Tag as Price);
         //else if (e.Node.Tag is ManagerFolder)
         //   AddFolderToMatrix(parent, e.Node);
      }

      static int CmpPriceNodes(TreeNode left, TreeNode right)
      {
         OrgMatrixItem p1 = left.Tag as OrgMatrixItem;
         OrgMatrixItem p2 = right.Tag as OrgMatrixItem;

         if (p1 == null)
            return (p2 == null) ? 0 : - 1;
         if (p2 == null)
            return 1;

         return p1.price.name.CompareTo(p2.price.name);
      }

      private void tsbSort_Click(object sender, EventArgs e)
      {
         TreeGridNode node = tgvMatrix.CurrentRow;
         int idx = node.RowIndex;
         if (node == null)
         {
            MessageBox.Show("Не выбрана матрица для сортировки", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Stop);
            return;
         }

         if (node.Parent != null)
            node = node.Parent;

         if (node.Tag != null && 
            node.Tag is String && 
            dsMatrix.ContainsKey(node.Tag.ToString()))
         {
            List<OrgMatrixItem> list = dsMatrix[node.Tag.ToString()].matrix;
            list.Sort(new Comparison<OrgMatrixItem>(delegate(OrgMatrixItem lhs, OrgMatrixItem rhs)
               { 
                  if(lhs.price == null && rhs.price == null)
                     return 0;

                  return lhs.price.Name.CompareTo(rhs.price.Name);
               }));
            FillMatrix();
            tgvMatrix.CurrentCell = tgvMatrix.Rows[idx].Cells[0];
            controller.SetNoSaveStatus();
         }
      }

      private void FillAgents()
      {
         cbAgents.Items.Clear();
         List<Agent> list = new List<Agent>();

         foreach (Agent a in CurrentUser.user.GetAgents().Data)
            list.Add(a);

         if (list.Count > 0)
         {
            list.Sort(new Comparison<Agent>(delegate(Agent a1, Agent a2) { return a1.Name.CompareTo(a2.Name); }));
         }

         cbAgents.Items.AddRange(list.ToArray());
         cbAgents.SelectedIndex = 0;
      }

      private void FmOrgMatrixDesigner_Load(object sender, EventArgs e)
      {
         FillAgents();
      }

      private void cbAgents_DropDownClosed(object sender, EventArgs e)
      {
         //tvMatrix.Nodes.Clear();
      }

      private void tgvMatrix_DoubleClick(object sender, EventArgs e)
      {
         TreeGridView grid = sender as TreeGridView;

         if (grid != null && grid.CurrentRow != null && grid.CurrentRow.Tag is OrgMatrixItem)
         {
            OrgMatrixItem item = grid.CurrentRow.Tag as OrgMatrixItem;
            
            if (new FmEditItem(item).ShowDialog() == DialogResult.OK)
            {
               grid.CurrentRow.Cells[tgvMatrixQty.Name].Value = item.qty;
               grid.CurrentRow.Cells[tgvMatrixFace.Name].Value = item.face;
               tsbSave.Enabled = true;
            }
         }
      }

      private void tgvMatrix_DragDrop(object sender, DragEventArgs e)
      {
         Point p = tgvMatrix.PointToClient(new Point(e.X, e.Y));
         DataGridView.HitTestInfo info = tgvMatrix.HitTest(p.X, p.Y);

         if (info != null)
         {
            TreeGridNode node = tgvMatrix.Rows[info.RowIndex] as TreeGridNode;

            if (node != null)
            {
               if (node.Level > 1)
                  node = node.Parent;

               if (node != null)
               {
                  if (e.Data.GetDataPresent(typeof(Price)))
                  {
                     AddPriceToMatrix(node, e.Data.GetData(typeof(Price)) as Price);
                  }
                  else if (e.Data.GetDataPresent(typeof(TreeNode)))
                  {
                     TreeNode tn = e.Data.GetData(typeof(TreeNode)) as TreeNode;
                     if (tn.Tag is Price)
                        AddPriceToMatrix(node, tn.Tag as Price);
                     else if (tn.Tag is ManagerFolder)
                        AddFolderToMatrix(node, tn);
                  }
                  else if (e.Data.GetDataPresent(typeof(List<TreeNode>)))
                  {
                     List<TreeNode> nodes = e.Data.GetData(typeof(List<TreeNode>)) as List<TreeNode>;
                     foreach (TreeNode tn in nodes)
                     {
                        if (tn.Tag is Price)
                           AddPriceToMatrix(node, tn.Tag as Price);
                     }
                  }
               }
            }
         }
      }

      private void tgvMatrix_DragEnter(object sender, DragEventArgs e)
      {
         e.Effect = DragDropEffects.Copy;
      }
   }

   public class OrgMatrix : Org
   {
      [ItemType(typeof(OrgMatrixItem))]
      public List<OrgMatrixItem> matrix = null;
   }

   public class OrgMatrixItem : GRSoft.Network.DataObject
   {
      public string orgid = string.Empty;
      public string id = string.Empty;
      [Reference("ManagerPrice", "id", typeof(Price))]
      public Price price = null;
      public double qty = 0.0;
      public double face = 0.0;
   }
}