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

      public virtual void __Initing()
      {
         this.tsbAdd.Click += new System.EventHandler(this.tsbAdd_Click);
         this.tsbRemove.Click += new System.EventHandler(this.tsbRemove_Click);
         this.tsbUp.Click += new System.EventHandler(this.tsbUp_Click);
         this.tsbDown.Click += new System.EventHandler(this.tsbDown_Click);
         this.tsbSort.Click += new System.EventHandler(this.tsbSort_Click);
         this.tsbRefresh.Click += new System.EventHandler(this.tsbRefresh_Click);
         this.tsbSave.Click += new System.EventHandler(this.tsbSave_Click);
         this.tstbFind.KeyDown += new System.Windows.Forms.KeyEventHandler(this.tstbFind_KeyDown);
         this.tsbFind.Click += new System.EventHandler(this.tsbFind_Click);
         this.tsbFindBack.Click += new System.EventHandler(this.tsbFindBack_Click);
         this.tvMatrix.DragDrop += new System.Windows.Forms.DragEventHandler(this.tvMatrix_DragDrop);
         this.tvMatrix.DragEnter += new System.Windows.Forms.DragEventHandler(this.tvMatrix_DragEnter);
         this.tvMatrix.MouseDown += new System.Windows.Forms.MouseEventHandler(this.tvMatrix_MouseDown);
         this.tvPrice.ItemDrag += new System.Windows.Forms.ItemDragEventHandler(this.tvPrice_ItemDrag);
         this.tvPrice.NodeMouseDoubleClick += new System.Windows.Forms.TreeNodeMouseClickEventHandler(this.tvPrice_NodeMouseDoubleClick);
         this.dgvPrice.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.dgvPrice_CellFormatting);
         this.dgvPrice.MouseDown += new System.Windows.Forms.MouseEventHandler(this.dgvPrice_MouseDown);
         this.tsbRename.Click += new System.EventHandler(this.tsbRename_Click);

         bool designMode = (LicenseManager.UsageMode == LicenseUsageMode.Designtime);
         if (!designMode)
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
         dsMatrix = (DataSet<int, Matrix>)DataModule.Get(MatrixObjectName) ?? new DataSet<int, Matrix>(MatrixObjectName, true);
         dsFolder = new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME, false);
      }

      protected virtual string MatrixObjectName { get { return Matrix.OBJECT_NAME; } }

      protected virtual void RefreshData()
      {
         List<IDataSet> list = new List<IDataSet>();
         PullRefreshList(list);
         FmWait.StdDataRefresh(this, list, ControlsFillAfterLoaded);
      }

      protected virtual void PullRefreshList(List<IDataSet> list)
      {
         dsMatrix.Filter = DataUtils.USERID_IS_NULL_STR;
         dsFolder.Filter = DataUtils.USERID_IS_NULL_STR;
         dsPrice.Filter = DataUtils.COMMON_PRICE_FILTER_STR;

         list.Add(dsPrice);
         list.Add(dsFolder);
         list.Add(dsMatrix);
      }

      protected virtual void ControlsFillAfterLoaded()
      {
         FillPrice();
         FillMatrix();
         tbMatrixName.Text = string.Empty;
      }

      protected virtual void MakePriceTree(TreeView tv, DataSet<string, ManagerFolder> folders, DataSet<string, Price> price)
      {
         ArticlesTreeConstructor t = new ArticlesTreeConstructor(tv, folders, price);
         t.GetPriceName = GetPriceName;
         t.MakeArticlesTree(0, 1);
         PostTreeConstruct(t);
      }

      protected void FillPrice()
      {
         if (dsFolder.Count > 0)
         {
            MakePriceTree(tvPrice, dsFolder, dsPrice);

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
      }

      protected virtual void PostTreeConstruct(ArticlesTreeConstructor atc) { }

      void DoSearch(string str)
      {
         if (str.Length == 0)
         {
            ArticlesTreeConstructor t = new ArticlesTreeConstructor(tvPrice, dsFolder, dsPrice);
            t.GetPriceName = GetPriceName;
            t.MakeArticlesTree(0, 1);
         }
         else
         {
            tvPrice.SuspendLayout();

            tvPrice.Nodes.Clear();
            str = str.ToLower();
            foreach (Price p in dsPrice.Data)
            {
               String name = GetPriceName(p);
               if (name.ToLower().Contains(str))
               {
                  TreeNode newNode = new TreeNode(name);
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
               tvPrice.Invoke(new InvokeParamHandler(
                  delegate (object param)
                  {
                     DoSearch((string)param);
                  }), new object[] { o });
            m.ReleaseMutex();
         }
         catch (Exception)
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

      protected virtual string GetPriceName(Price p) { return p.name; }
      protected virtual string GetMatrixPriceName(MatrixItem item) { return GetPriceName(item.price); }

      public virtual void InitNode(TreeNode n, Matrix m){}

      protected void FillMatrix()
      {
         tvMatrix.Nodes.Clear();

         if (dsMatrix != null)
         {
            foreach (Matrix matrix in dsMatrix.Data)
               if (CheckMatrix(matrix))
               {
                  TreeNode node = new TreeNode();
                  node.Text = matrix.name;
                  node.Tag = matrix;

                  InitNode(node, matrix);

                  foreach (MatrixItem item in matrix.items)
                  {
                     if (item != null && item.price != null)
                     {
                        TreeNode nodeItem = new TreeNode(GetMatrixPriceName(item));
                        nodeItem.Tag = item;
                        node.Nodes.Add(nodeItem);
                     }
                  }

                  tvMatrix.Nodes.Add(node);
               }
         }

         FillMatrixEnded();
      }

      protected virtual bool CheckMatrix(Matrix mtx) { return true; }

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
         ItemsDragDrop(e);
      }

      protected virtual void ItemsDragDrop(DragEventArgs e)
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
         MatrixItem matrixItem = new MatrixItem();
         matrixItem.id = price.id;
         matrixItem.price = price;
         TreeNode child = new TreeNode(GetMatrixPriceName(matrixItem));
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
            Matrix m = targetNode.Tag as Matrix;

            if (m != null)
               m.items.Add(matrixItem);

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
            SetMatrixName(node.Text);
         }
         else
         {
            SetMatrixName(node.Parent.Text);
         }

         dgvPrice.Refresh();
      }

      protected virtual void SetMatrixName(string text)
      {
         tbMatrixName.Text = text;
      }

      protected virtual bool IsMatrixInSet(string matrixName)
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

      public virtual IDataSet GetMatrixDataSet()
      {
         SimpleDataSet<Matrix> result = new SimpleDataSet<Matrix>(Matrix.OBJECT_NAME, false);

         foreach (TreeNode matrixNode in tvMatrix.Nodes)
         {
            Matrix matrix = new Matrix();
            matrix.name = matrixNode.Text;

            InitMatrix(matrix, matrixNode);

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

      public virtual void InitMatrix(Matrix m, TreeNode node){}

      private void tsbAdd_Click(object sender, EventArgs e)
      {
         string newMatrixName = EditMatrixName(string.Empty);

         if (newMatrixName.Trim().Length == 0)
         {
            EmptyNameMantrixHandler();
            return;
         }

         if (IsMatrixInSet(newMatrixName))
         {
            MessageBox.Show(String.Format("Матрица с именем \"{0}\" присутствует в наборе", newMatrixName),
               "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
            return;
         }

         Matrix m = new Matrix();
         TreeNode node = new TreeNode();
         node.Text = newMatrixName.Trim();
         node.Tag = m;
         m.name = node.Text;
         InitNewMtx(m);
         dsMatrix.Add(dsMatrix.Count, m);
         tvMatrix.Nodes.Add(node);
         tvMatrix.SelectedNode = node;
         controller.SetNoSaveStatus();
      }

      protected virtual void InitNewMtx(Matrix m) { }

      protected virtual void EmptyNameMantrixHandler()
      {
         MessageBox.Show("Невозможно создать матрицу с пустым именем", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
      }

      private void tsbRename_Click(object sender, EventArgs e)
      {
         TreeNode node = DataUtils.getTopParent(tvMatrix.SelectedNode);

         if (node == null)
         {
            return;
         }


         node.Text = EditMatrixName(tbMatrixName.Text);

         Matrix m = node.Tag as Matrix;

         if (m != null && !m.name.Equals(node.Text))
         {
            RenameMatrix(m, node.Text);
            controller.SetNoSaveStatus();
         }
      }

      protected virtual void RenameMatrix(Matrix m, string name) { }

      protected virtual string EditMatrixName(string val)
      {
         return tbMatrixName.Text.Trim();
      }

      private void tsbRemove_Click(object sender, EventArgs e)
      {
         TreeNode node = tvMatrix.SelectedNode;

         if (node == null)
         {
            return;
         }

         OnItemRemove(node);

         Matrix m = node.Tag as Matrix;

         if (m != null)
            RemoveMatrix(m);

         MatrixItem mi = node.Tag as MatrixItem;

         if (mi != null)
            RemoveMatrixItem(mi);

         node.Remove();

         controller.SetNoSaveStatus();
         dgvPrice.Refresh();
      }

      protected virtual void RemoveMatrixItem(MatrixItem mi)
      {
      }

      protected virtual void OnItemRemove(TreeNode node) { }

      protected virtual void RemoveMatrix(Matrix mtx)
      {
         foreach (int k in dsMatrix.Keys)
         {
            Matrix m = dsMatrix[k];
            if (m.name.Equals(mtx.name))
            {
               dsMatrix.Remove(k);
               break;
            }
         }
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
         int newpos = pos + (int)direction;

         if (newpos < 0 || newpos >= nodeCollection.Count)
         {
            return;
         }

         if (node.Tag is Matrix)
            MoveMatrix(pos, newpos, node.Tag as Matrix);
         if (node.Tag is MatrixItem)
            MoveItem(pos, newpos, node.Parent.Tag as Matrix);

         //MatrixItem mi = node.Tag as MatrixItem;
         //if(mi != null)
         //MoveItem(pos, newpos, node.Parent as Matrx);
         node.Remove();
         nodeCollection.Insert(newpos, node);
         tvMatrix.SelectedNode = node;
         controller.SetNoSaveStatus();
      }

      protected virtual void MoveMatrix(int pos, int newpos, Matrix m) { }

      protected void MoveItem(int pos, int newpos, Matrix m)
      {
         if (m != null && m.items != null && pos < m.items.Count)
         {
            MatrixItem mi = m.items[pos];
            m.items.Remove(mi);
            m.items.Insert(newpos, mi);
         }
      }

      protected virtual bool SaveData()
      {
         List<ReplacedSet> list = new List<ReplacedSet>();
         ReplacedSet rs = new ReplacedSet(null, GetMatrixDataSet());

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

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         bool designMode = (LicenseManager.UsageMode == LicenseUsageMode.Designtime);
         if (!designMode)
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
         //if (e.KeyCode == Keys.Enter)
         //   searchEngine.find(tstbFind.Text, Direction.DOWN);
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
            if (sel != null && sel.Count > 1)
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

      protected virtual void PriceNodeDblClick(TreeNodeMouseClickEventArgs e, TreeNode parent)
      {
         if (e.Node.Tag is Price)
            AddPriceToMatrix(parent, e.Node.Tag as Price);
         else if (e.Node.Tag is ManagerFolder)
            AddFolderToMatrix(parent, e.Node);
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
         PriceNodeDblClick(e, parent);
      }

      static int CmpPriceNodes(TreeNode left, TreeNode right)
      {
         MatrixItem p1 = left.Tag as MatrixItem;
         MatrixItem p2 = right.Tag as MatrixItem;

         if (p1 == null)
            return (p2 == null) ? 0 : -1;
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

         Matrix mtx = parent.Tag as Matrix;

         if (mtx != null && mtx.items != null)
            mtx.items.Sort((x, y) => { return x.price.Name.CompareTo(y.price.Name); });

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