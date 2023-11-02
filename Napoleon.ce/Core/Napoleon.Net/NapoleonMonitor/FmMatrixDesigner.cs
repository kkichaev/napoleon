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
   public partial class FmMatrixDesigner : Form
   {
      protected DataSet<int, Matrix> dsMatrix;
      protected DataSet<string, Price> dsPrice;
      protected DataSet<string, ManagerFolder> dsFolder;
      protected MatrixFromController controller;
      private SearchEngine searchEngine;

      public FmMatrixDesigner()
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
         dsMatrix = DataModule.Get(Matrix.OBJECT_NAME) == null ? new DataSet<int, Matrix>(Matrix.OBJECT_NAME, true) :
            (DataSet<int, Matrix>)DataModule.Get(Matrix.OBJECT_NAME);
         dsFolder = new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME, false);
      }

      protected virtual void RefreshData()
      {
         DataModule.DataProcessed += new EventHandler(DataLoaded);
         dsMatrix.Filter = DataUtils.USERID_IS_NULL_STR;
         dsFolder.Filter = DataUtils.USERID_IS_NULL_STR;
         dsPrice.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
         Thread t = DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), 
            new object[] { dsPrice, dsMatrix, dsFolder }, 
            FmWait.ProgressIndicator);
         FmWait.ShowForm(this, t);
      }

      private void DataLoaded(object sender, EventArgs e)
      {
         DataModule.DataProcessed -= new EventHandler(DataLoaded);
         BeginInvoke(new EmptyParamHandler(ControlsFillAfterLoaded));
      }

      protected void ControlsFillAfterLoaded()
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
         tbMatrixName.Text = string.Empty;
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

      protected virtual void FillMatrixEnded()
      {
      }

      private void FillMatrix()
      {
         tvMatrix.Nodes.Clear();

         if (dsMatrix != null)
         {
            foreach (Matrix matrix in dsMatrix.Data)
            {
               TreeNode node = new TreeNode();
               node.Text = matrix.name;
               node.Tag = matrix;

               foreach (MatrixItem item in matrix.items)
               {
                  if (item != null && item.price != null)
                  {
                     TreeNode nodeItem = new TreeNode(item.price.name);
                     nodeItem.Tag = item;
                     node.Nodes.Add(nodeItem);
                  }
               }

               tvMatrix.Nodes.Add(node);
            }
         }
         FillMatrixEnded();
      }

      private void tsbRefresh_Click(object sender, EventArgs e)
      {
         RefreshData();
      }

      private void tvMatrix_DragEnter(object sender, DragEventArgs e)
      {
         e.Effect = DragDropEffects.Copy;
      }

      void AddPriceNodes(TreeNode parent, TreeNodeCollection col)
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

      void AddFolderToMatrix(TreeNode parent, TreeNode folder)
      {
         //DialogResult dr = MessageBox.Show("Добавить товары из папки?", "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question);
         //if (dr == DialogResult.Yes)
            AddPriceNodes(parent, folder.Nodes);
      }

      private void tvMatrix_DragDrop(object sender, DragEventArgs e)
      {
         TreeNode targetNode = DataUtils.GetNodeFromPoint(tvMatrix, new Point(e.X, e.Y));

         if (targetNode != null)
         {
            TreeNode parent = DataUtils.getTopParent(targetNode);
            if (e.Data.GetDataPresent(typeof(Price)))
            {
               AddPriceToMatrix(parent, e.Data.GetData(typeof(Price)) as Price);
            }
            else if (e.Data.GetDataPresent(typeof(TreeNode)))
            {
               TreeNode tn = e.Data.GetData(typeof(TreeNode)) as TreeNode;
               if (tn.Tag is Price)
                  AddPriceToMatrix(parent, tn.Tag as Price);
               else if (tn.Tag is ManagerFolder)
                  AddFolderToMatrix(parent, tn);
            }
            else if (e.Data.GetDataPresent(typeof(List<TreeNode>)))
            {
               List<TreeNode> nodes = e.Data.GetData(typeof(List<TreeNode>)) as List<TreeNode>;
               foreach (TreeNode tn in nodes)
               {
                  if (tn.Tag is Price)
                     AddPriceToMatrix(parent, tn.Tag as Price);
               }
            }
         }
      }

      private void AddPriceToMatrix(TreeNode targetNode, Price price)
      {
         TreeNode child = new TreeNode(price.ToString());
         MatrixItem matrixItem = new MatrixItem();
         matrixItem.id = price.id;
         matrixItem.price = price;
         child.Tag = matrixItem;

         bool contains = false;
         foreach (TreeNode tn in targetNode.Nodes)
         {
            if (tn.Text.ToString().ToUpper().Equals(child.Text.ToUpper()))
            {
               contains = true;
               break;
            }
         }

         if (!contains)
         {
            targetNode.Nodes.Add(child);
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
         TreeNode node = tvMatrix.GetNodeAt(new Point(e.X, e.Y));

         if (node == null)
         {
            return;
         }

         tvMatrix.SelectedNode = node;

         if (node.Level == 0)
         {
            tbMatrixName.Text = node.Text;
         }
         else
         {
            tbMatrixName.Text = node.Parent.Text;
         }

         dgvPrice.Refresh();
      }

      private bool isMatrixContainsMatrix(string matrixName)
      {
         foreach (TreeNode node in tvMatrix.Nodes)
         {
            if (node.Text.ToUpper().Equals(matrixName.ToUpper()))
            {
               return true;
            }
         }

         return false;
      }

      protected virtual void BeforeMatrixAdded(Matrix matrix, TreeNode matrixNode)
      {
      }

      public DataSet<int, Matrix> getMatrixDataSet()
      {
         SimpleDataSet<Matrix> result = new SimpleDataSet<Matrix>(Matrix.OBJECT_NAME, false);

         foreach (TreeNode matrixNode in tvMatrix.Nodes)
         {
            Matrix matrix = new Matrix();
            matrix.name = matrixNode.Text;

            List<MatrixItem> matrixItemsList = new List<MatrixItem>();
            foreach (TreeNode matrixItem in matrixNode.Nodes)
            {
               matrixItemsList.Add(matrixItem.Tag as MatrixItem);
            }

            if (matrixItemsList.Count > 0)
            {
               matrix.items = matrixItemsList;
               BeforeMatrixAdded(matrix, matrixNode);
               result.Add(matrix);
            }
         }

         return result;
      }

      private void tsbAdd_Click(object sender, EventArgs e)
      {
         string newMatrixName = tbMatrixName.Text;

         if (newMatrixName.Trim().Length == 0)
         {
            MessageBox.Show("Невозможно создать матрицу с пустым именем",
               "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
            return;
         }

         if (isMatrixContainsMatrix(newMatrixName))
         {
            MessageBox.Show(String.Format("Матрица с именем \"{0}\" присутствует в наборе", newMatrixName),
               "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
            return;
         }

         TreeNode matrix = new TreeNode();
         matrix.Text = newMatrixName;
         tvMatrix.Nodes.Add(matrix);
         tvMatrix.SelectedNode = matrix;
         controller.SetNoSaveStatus();
      }

      private void tsbRename_Click(object sender, EventArgs e)
      {
         TreeNode node = DataUtils.getTopParent(tvMatrix.SelectedNode);

         if (node == null)
         {
            return;
         }

         node.Text = tbMatrixName.Text;
         controller.SetNoSaveStatus();
      }

      private void tsbRemove_Click(object sender, EventArgs e)
      {
         TreeNode node = tvMatrix.SelectedNode;

         if (node == null)
         {
            return;
         }

         node.Remove();

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
         TreeNode node = tvMatrix.SelectedNode;

         if (node == null)
         {
            return;
         }

         TreeNodeCollection nodeCollection = node.Level == 0 ? tvMatrix.Nodes : node.Parent.Nodes;
         int pos = nodeCollection.IndexOf(node);

         if ((pos + (int)direction) < 0 || (pos + (int)direction) >= nodeCollection.Count)
         {
            return;
         }

         node.Remove();
         nodeCollection.Insert(pos + (int)direction, node);
         tvMatrix.SelectedNode = node;
         controller.SetNoSaveStatus();
      }

      protected virtual bool SaveData()
      {
         List<ReplacedSet> list = new List<ReplacedSet>();
         ReplacedSet rs = new ReplacedSet(null, getMatrixDataSet());

         dsMatrix.Clear();

         list.Add(rs);
         return DataModule.UpdateDataSet(null, null, list, Config.GetConfig().GetConnection());
      }

      private void tsbSave_Click(object sender, EventArgs e)
      {
         if (!SaveData())
         {
            MessageBox.Show("Ошибка при записи в базу данных.", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
         }
         else
            controller.SetSaveStatus();
      }

      private void FmMatrixDesigner_Shown(object sender, EventArgs e)
      {
         RefreshData();
      }


      /// <summary>
      /// Управление визуальными компонентами формы
      /// в зависимости от текущего статуса
      /// </summary>
      public class MatrixFromController
      {
         FmMatrixDesigner control;

         public MatrixFromController(FmMatrixDesigner control)
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
         searchEngine.find(tstbFind.Text, Direction.DOWN);
      }

      private void tstbFind_KeyDown(object sender, KeyEventArgs e)
      {
         if (e.KeyCode == Keys.Enter)
            searchEngine.find(tstbFind.Text, Direction.DOWN);
      }

      private void tsbFindBack_Click(object sender, EventArgs e)
      {
         searchEngine.find(tstbFind.Text, Direction.UP);
      }
      
      private void dgvPrice_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         TreeNode node = tvMatrix.SelectedNode;

         if (node != null)
         { 
            TreeNodeCollection nodes = node.Level == 0 ? node.Nodes : node.Parent.Nodes;
            bool inMatrix = false;

            foreach (TreeNode n in nodes)
               if (n.Tag is MatrixItem && 
                  ((MatrixItem)n.Tag).id.Equals(((Price)e.Value).id))
               {
                  inMatrix = true;
                  break;
               }

            if (inMatrix)
               e.CellStyle.BackColor = Color.LightBlue;
         }
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
         TreeNode parent = tvMatrix.SelectedNode;
         if (parent == null)
         {
            MessageBox.Show("Не выбрана матрица для добавления", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Stop);
            return;
         }
         parent = DataUtils.getTopParent(parent);
         if (e.Node.Tag is Price)
            AddPriceToMatrix(parent, e.Node.Tag as Price);
         else if (e.Node.Tag is ManagerFolder)
            AddFolderToMatrix(parent, e.Node);
      }

      static int CmpPriceNodes(TreeNode left, TreeNode right)
      {
         MatrixItem p1 = left.Tag as MatrixItem;
         MatrixItem p2 = right.Tag as MatrixItem;

         if (p1 == null)
            return (p2 == null) ? 0 : - 1;
         if (p2 == null)
            return 1;

         return p1.price.name.CompareTo(p2.price.name);
      }

      private void tsbSort_Click(object sender, EventArgs e)
      {
         TreeNode parent = tvMatrix.SelectedNode;
         if (parent == null)
         {
            MessageBox.Show("Не выбрана матрица для сортировки", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Stop);
            return;
         }

         tvMatrix.SuspendLayout();
         
         parent = DataUtils.getTopParent(parent);
         TreeNode[] nodes = new TreeNode[parent.Nodes.Count];
         parent.Nodes.CopyTo(nodes, 0);
         Array.Sort(nodes, CmpPriceNodes);
         parent.Nodes.Clear();
         parent.Nodes.AddRange(nodes);
         controller.SetNoSaveStatus();

         tvMatrix.ResumeLayout();
      }

   }

   
}