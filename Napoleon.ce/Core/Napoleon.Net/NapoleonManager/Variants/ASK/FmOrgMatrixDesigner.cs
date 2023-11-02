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

      class OrgNode : TreeNode
      {
         public OrgMatrix matrix;

         public OrgNode(OrgMatrix orgMatrix)
         {
            this.matrix = orgMatrix;

            foreach (OrgMatrixItem item in matrix.matrix)
            {
               if (item != null && item.price != null)
               {
                  TreeNode nodeItem = new TreeNode(item.price.name);
                  nodeItem.Tag = item;
                  Nodes.Add(nodeItem);
               }
            }
         }
      }

      private void FillMatrix()
      {
         tvMatrix.BeginUpdate();
         tvMatrix.Nodes.Clear();
         List<OrgMatrix> list = new List<OrgMatrix>();
         list.AddRange(dsMatrix.Values);
         list.Sort(new Comparison<OrgMatrix>(delegate(OrgMatrix lhs, OrgMatrix rhs)
            { return lhs.Name.CompareTo(rhs.Name); }));

         if (dsMatrix != null)
         {
            foreach (OrgMatrix matrix in list)
            {
               TreeNode node = new OrgNode(matrix);
               node.Text = matrix.name;
               tvMatrix.Nodes.Add(node);
            }
         }
         tvMatrix.EndUpdate();
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
         OrgMatrixItem matrixItem = new OrgMatrixItem();
         matrixItem.id = price.id;
         matrixItem.price = price;
         matrixItem.orgid = (targetNode as OrgNode).matrix.id;
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

      private DataSet<string, OrgMatrix> getMatrixDataSet()
      {
         foreach (TreeNode matrixNode in tvMatrix.Nodes)
         {
            OrgNode node = matrixNode as OrgNode;

            if (node != null)
            {
               if (dsMatrix.ContainsKey(node.matrix.id))
               {
                  OrgMatrix om = dsMatrix[node.matrix.id];
                  om.matrix.Clear();

                  foreach (TreeNode i in node.Nodes)
                  {
                     om.matrix.Add((OrgMatrixItem)i.Tag);
                  }
               }
            }
         }

         return dsMatrix;
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
               if (n.Tag is OrgMatrixItem && 
                  ((OrgMatrixItem)n.Tag).orgid.Equals(((Price)e.Value).id))
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

      class OrgMatrix : Org
      {
         [ItemType(typeof(OrgMatrixItem))]
         public List<OrgMatrixItem> matrix = null;
      }

      class OrgMatrixItem : GRSoft.Network.DataObject
      {
         public string orgid = string.Empty;
         public string id = string.Empty;
         [Reference("ManagerPrice", "id", typeof(Price))]
         public Price price = null;
      }

      private void cbAgents_DropDownClosed(object sender, EventArgs e)
      {
         tvMatrix.Nodes.Clear();
      }
   }
}