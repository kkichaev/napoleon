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
using System.Collections;

namespace GRSoft.NapoleonManager
{
   public partial class FmOrgTypeEditor : Form
   {
      protected DataSet<string, OrgType> dsOrgType;
      protected DataSet<string, OrgType> dsOrgTypeUpdated;
      Dictionary<string, Org> orgs = new Dictionary<string, Org>();

      public FmOrgTypeEditor()
      {
         InitializeComponent();
         InitDataSets();
         Init();
      }

      private void Init()
      {
         grid.AutoGenerateColumns = false;
         btnSave.Enabled = false;
         tree.TreeViewNodeSorter = new NodeSorter();
      }

      // Create a node sorter that implements the IComparer interface.
      public class NodeSorter : IComparer
      {
         // Compare the length of the strings, or the strings
         // themselves, if they are the same length.
         public int Compare(object x, object y)
         {
            TreeNode tx = x as TreeNode;
            TreeNode ty = y as TreeNode;

            // If they are the same length, call Compare.
            return string.Compare(tx.Text, ty.Text);
         }
      }

      private void InitDataSets()
      {
         dsOrgType = (DataSet<string, OrgType>)DataModule.Get(OrgType.OBJECT_NAME) ?? new DataSet<string, OrgType>(OrgType.OBJECT_NAME, true);
         dsOrgTypeUpdated = new DataSet<string, OrgType>(OrgType.OBJECT_NAME, false);
      }

      protected virtual string MatrixObjectName { get { return Matrix.OBJECT_NAME; } }

      protected virtual void DoLoadData()
      {
         orgs = FillGrid();
         FillTree(orgs);
         tbName.Text = string.Empty;
         btnSave.Enabled = false;
      }

      protected Dictionary<string, Org> FillGrid()
      {
         Dictionary<string, Org> orgs = new Dictionary<string, Org>();

         Manager dm = CurrentUser.user as Manager;

         if (dm != null)
         {
            foreach (Agent a in dm.GetAgents().Data)
            {
               DataSet<string, Org> ao = DataModule.GetUserDataSet(a.id, Org.OBJECT_NAME, typeof(DataSet<string, Org>)) as DataSet<string, Org>;

               foreach (Org o in ao.Values)
                  orgs[o.id] = o;
            }

            List<Org> list = new List<Org>();
            list.AddRange(orgs.Values);
            list.Sort((x, y) => { return x.Name.CompareTo(y.Name); });

            grid.DataSource = list;
         }

         return orgs;
      }

      void DoSearch(string str)
      {
         List<Org> data = new List<Org>();

         if (str.Trim().Length > 0)
         {
            foreach (Org o in orgs.Values)
            {
               if (o.Name.ToUpper().Contains(str.ToUpper()))
                  data.Add(o);
            }
         }
         else
            data.AddRange(orgs.Values);

         data.Sort((x, y) => { return x.Name.CompareTo(y.Name); });
         grid.DataSource = data;
      }

      System.Threading.Timer textWait = null;

      void TimePassed(object o)
      {
         try
         {
            Mutex m = new Mutex(false, "FMMatrixMutex");
            if (m.WaitOne(0))
               grid.Invoke(new InvokeParamHandler( delegate(object param) { DoSearch((string)param);}), new object[] { o });
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

      protected virtual string GetMatrixPriceName(MatrixItem item) { return item.price.name;  }

      protected void FillTree(Dictionary<string, Org> orgs)
      {
         tree.BeginUpdate();
         tree.Nodes.Clear();

         foreach (OrgType o in dsOrgType.Values)
            tree.Nodes.Add(new OrgTypeNode(o, orgs));

         tree.EndUpdate();
      }

      protected virtual bool CheckMatrix(Matrix mtx) { return true; }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> list = new List<IDataSet>();

         Manager dm = CurrentUser.user as Manager;
         if (dm == null)
            return;

         list.Add(dsOrgType);

         foreach (Agent a in dm.GetAgents().Data)
         {
            DataSet<string, Org> orgs = DataModule.GetUserDataSet(a.id, Org.OBJECT_NAME, typeof(DataSet<string, Org>), true) as DataSet<string, Org>;

            if (orgs.Count == 0)
               list.Add(orgs);
         }

         if (list.Count > 0)
            FmWait.StdDataRefresh(this, list, DoLoadData);
      }

      private void tree_DragEnter(object sender, DragEventArgs e)
      {
         e.Effect = DragDropEffects.Copy;
      }

      private void grid_MouseDown(object sender, MouseEventArgs e)
      {
         if (e.Button == System.Windows.Forms.MouseButtons.Left && e.Clicks == 2)
         {
            DataGridView.HitTestInfo info = ((DataGridView)sender).HitTest(e.X, e.Y);

            if(info != null && info.RowIndex > -1)
            {
               Org org = ((DataGridView)sender).Rows[info.RowIndex].DataBoundItem as Org;

               if(org != null)
               {
                  OrgTypeNode n = DataUtils.getTopParent(tree.SelectedNode) as OrgTypeNode;

                  if (n != null)
                  {
                     n.AddItem(org);
                     btnSave.Enabled = true;
                     dsOrgTypeUpdated[n.data.id] = n.data;
                  }
               }
            }
         }else
            DataUtils.beginDragAndDropOnDataGrid<Org>(sender as DataGridView, e);
      }

      private void tree_MouseDown(object sender, MouseEventArgs e)
      {
         TreeNode node = tree.GetNodeAt(new Point(e.X, e.Y));

         if (node == null)
         {
            return;
         }

         tree.SelectedNode = node;

         if (node.Level == 0)
         {
            tbName.Text = node.Text;
         }
         else
         {
            tbName.Text = node.Parent.Text;
         }

         grid.Refresh();
      }

      protected virtual bool IsInSet(string matrixName)
      {
         foreach (TreeNode node in tree.Nodes)
         {
            if (node.Text.ToUpper().Equals(matrixName.ToUpper()))
            {
               return true;
            }
         }

         return false;
      }

      private void tsbAdd_Click(object sender, EventArgs e)
      {
         string name = tbName.Text.Trim();

         if (name.Length == 0)
         {
            EmptyNameMantrixHandler();
            return;
         }

         if (IsInSet(name))
         {
            MessageBox.Show(String.Format("Тип с именем \"{0}\" присутствует в наборе", name),"Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
            return;
         }

         OrgTypeNode n = new OrgTypeNode(name);
         
         dsOrgTypeUpdated[n.data.id] = n.data;
         tree.Nodes.Add(n);
         tree.SelectedNode = n;
         btnSave.Enabled = true;
      }

      protected virtual void EmptyNameMantrixHandler()
      {
         MessageBox.Show("Невозможно создать матрицу с пустым именем", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
      }

      private void tsbRename_Click(object sender, EventArgs e)
      {
         OrgTypeNode node = DataUtils.getTopParent(tree.SelectedNode) as OrgTypeNode;

         if (node != null)
         {
            node.Rename(tbName.Text.Trim());
            btnSave.Enabled = true;
            dsOrgTypeUpdated[node.data.id] = node.data;
         }
      }

      private void tsbRemove_Click(object sender, EventArgs e)
      {
         TreeNode node = tree.SelectedNode;

         if (node == null)
         {
            return;
         }

         OrgTypeNode change = null;

         if (node is OrgTypeNode)
         {
            OrgTypeNode otn = (OrgTypeNode)node;
            otn.data.rem = 1;
            node.Remove();
            otn.data.rem = 1;

            change = otn;
         }
         else
         {
            OrgTypeNode otn = DataUtils.getTopParent(node) as OrgTypeNode;

            if (otn != null)
            {
               otn.RemoveItem(node);
               change = otn;
            }
         }

         if(change != null)
         {
            dsOrgTypeUpdated[change.data.id] = change.data;
            btnSave.Enabled = true;
         }
      }

      protected virtual bool SaveData()
      {
         List<IDataSet> list = new List<IDataSet>();
         
         if(dsOrgTypeUpdated.Count > 0)
            list.Add(dsOrgTypeUpdated);

         return DataModule.UpdateDataSet(list, null, null, Config.GetConfig().GetConnection());
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         if (!SaveData())
         {
            MessageBox.Show("Ошибка при записи в базу данных.", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
         }
         else
            btnSave.Enabled = false;
      }

      private void grid_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         Org o = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as Org;

         if (o != null)
         {

            foreach(TreeNode n in tree.Nodes)
            {
               OrgTypeNode otn = n as OrgTypeNode;

               bool inset = false;

               foreach (OrgType.OrgTypeItem i in otn.data.items)
                  if (i.id.Equals(o.id))
                  {
                     inset = true;
                     break;
                  }

               if (inset)
                  e.CellStyle.BackColor = Color.LightBlue;
            }
         }
      }

      private void FmMatrixDesigner_Load(object sender, EventArgs e)
      {
         btnRefresh.PerformClick();
      }

      class OrgTypeNode : TreeNode
      {
         public OrgType data;

         public OrgTypeNode(string text) : base(text)
         {
            data = new OrgType();
            data.id = GRSoft.Network.DataObject.GenId();
            data.name = text;
         }

         public OrgTypeNode(OrgType ot, Dictionary<string, Org> orgs) : base(ot.name)
         {
            data = ot;

            foreach (OrgType.OrgTypeItem i in data.items)
            {
               if (i != null && orgs.ContainsKey(i.id))
               {
                  TreeNode n = new TreeNode(orgs[i.id].Name);
                  n.Tag = orgs[i.id];
                  Nodes.Add(n);
               }
            }
         }

         public bool AddItem(Org org)
         {
            bool result = true;

            if(org != null)
            {
               foreach(OrgType.OrgTypeItem i in data.items)
               {
                  if (i.id.Equals(org.id))
                  {
                     result = false;
                     break;
                  }
               }

               if (result)
               {
                  TreeNode n = new TreeNode(org.Name);
                  n.Tag = org;
                  Nodes.Add(n);

                  OrgType.OrgTypeItem i = new OrgType.OrgTypeItem();
                  i.id = org.id;
                  data.items.Add(i);
               }
            }

            return result;
         }

         public void Rename(string text)
         {
            data.name = text;
            Text = text;
         }

         internal void RemoveItem(TreeNode node)
         {
            int idx = Nodes.IndexOf(node);

            if(idx != -1)
            {
               TreeNode n = Nodes[idx];

               if (n != null)
               {
                  Org o = n.Tag as Org;

                  if (o != null)
                  {
                     foreach(OrgType.OrgTypeItem i in data.items)
                        if (i.id.Equals(o.id))
                        {
                           data.items.Remove(i);
                           break;
                        }
                  }

                  Nodes.Remove(n);
               }
            }
         }
      }

      private void tree_DragDrop(object sender, DragEventArgs e)
      {
         TreeNode n = DataUtils.GetNodeFromPoint((TreeView)sender, new Point(e.X, e.Y));

         if (n != null)
         {
            OrgTypeNode otn = (OrgTypeNode)DataUtils.getTopParent(n);

            if (otn != null && e.Data.GetDataPresent(typeof(Org)))
            {
               otn.AddItem(e.Data.GetData(typeof(Org)) as Org);
               dsOrgTypeUpdated[otn.data.id] = otn.data;
               btnSave.Enabled = true;
            }
         }
      }

      private void FmOrgTypeEditor_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (btnSave.Enabled == true && DialogUtil.AskToSave(this))
            SaveData();
      }

      private void btnOrgTypeMatrix_Click(object sender, EventArgs e)
      {
         new FmOrgTypeMatrix().Show();
      }
   }
}