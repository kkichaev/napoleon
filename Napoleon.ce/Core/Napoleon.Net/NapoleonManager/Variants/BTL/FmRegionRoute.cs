using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using System.Drawing;

namespace GRSoft.NapoleonManager
{
   public partial class FmRegionRoute : Form
   {
      private Agent agent = null;
      private DataSet<int, OrgFolder> dsOrgFolder;
      private DataSet<string, Region> dsRegion;
      private DataSet<string, Region1> dsRegion1;
      private DataSet<string, Region2> dsRegion2;
      private DataSet<string, Region> dsDelRegion;
      private SearchEngine searchEngine;

      public static void Show(Agent agent)
      {
         FmRegionRoute fmRoute = new FmRegionRoute();
         fmRoute.agent = agent;
         fmRoute.Show();
      }

      private FmRegionRoute()
      {
         InitializeComponent();

         DataSet<string, Agent> dsAgents = (DataSet<string, Agent>)DataModule.Get(Agent.OBJECT_NAME);
         dsOrgFolder = (DataSet<int, OrgFolder>)DataModule.Get(OrgFolder.OBJECT_NAME) ??
            new DataSet<int, OrgFolder>(OrgFolder.OBJECT_NAME);
         dsRegion = (DataSet<string, Region>)DataModule.Get(GRSoft.NapoleonManager.Region.OBJECT_NAME) ??
            new DataSet<string, Region>(GRSoft.NapoleonManager.Region.OBJECT_NAME);
         dsRegion1 = (DataSet<string, Region1>)DataModule.Get(GRSoft.NapoleonManager.Region1.OBJECT_NAME) ??
            new DataSet<string, Region1>(GRSoft.NapoleonManager.Region1.OBJECT_NAME);
         dsRegion2 = (DataSet<string, Region2>)DataModule.Get(GRSoft.NapoleonManager.Region2.OBJECT_NAME) ??
            new DataSet<string, Region2>(GRSoft.NapoleonManager.Region2.OBJECT_NAME);

         dsDelRegion = new DataSet<string, Region>(GRSoft.NapoleonManager.Region.OBJECT_NAME, false);

         if (dsAgents != null)
         {
            List<Agent> list = new List<Agent>();
            list.AddRange(dsAgents.Values);
            list.Sort(new Comparison<Agent>(delegate(Agent a1, Agent a2) { return a1.Name.CompareTo(a2.Name); }));
            cbAgents.Items.AddRange(list.ToArray());
         }

         searchEngine = new SearchEngine(new FindDataGridObject(dgvRegion, 0));
         dgvRegion.AutoGenerateColumns = false;
      }

      private void FmRegionRoute_Load(object sender, EventArgs e)
      {
         cbAgents.SelectedItem = agent;
         EnableControl(false);
      }

      private void btnAddDay_Click(object sender, EventArgs e)
      {
         string day = tbRoute.Text.Trim();
         Agent agent = (Agent)cbAgents.SelectedItem;

         if (agent != null && day.Length > 0)
         {
            OrgFolder o = new OrgFolder();
            o.agent = agent;
            o.name = day;

            tvRoute.Nodes.Add(new RouteTreeNode(o));
         }

         btnSave.Enabled = true;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         btnSave.Enabled = false;
         const string USERID_IN_STR = "userid in ('{0}')";
         Agent agent = (Agent)cbAgents.SelectedItem;

         if (agent != null)
         {
            dsOrgFolder.Filter = String.Format(USERID_IN_STR, agent.id);

            dsDelRegion.Clear();
            DataModule.SetDataRepsonceHandlers(DataModule_DataProcessed, DataModule_OnDataResponceError);

            List<IDataSet> list = new List<IDataSet>();
            list.Add(dsOrgFolder);
            list.Add(dsRegion1);
            list.Add(dsRegion2);
            list.Add(dsRegion);

            FmWait.ShowForm(this,
               DataModule.RefreshGiveSets(Config.GetConfig().
               GetConnection(), list, FmWait.ProgressIndicator));
         }
      }

      void DataModule_DataProcessed(object sender, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         BeginInvoke(new EmptyParamHandler(DataProcessed));
      }

      void DataModule_OnDataResponceError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         MessageBox.Show(e.Msg);
      }

      void DataProcessed()
      {
         tvRoute.SuspendLayout();

         try
         {
            tvRoute.Nodes.Clear();

            List<OrgFolder> list = new List<OrgFolder>();
            list.AddRange(dsOrgFolder.Values);
            list.Sort(new Comparison<OrgFolder>(delegate(OrgFolder f1, OrgFolder f2) { return f1.id.CompareTo(f2.id); }));

            foreach (OrgFolder of in list)
               tvRoute.Nodes.Add(new RouteTreeNode(of));
         }
         finally
         {
            tvRoute.ResumeLayout();
         }

         UpdateDgvRegion();
         EnableControl(true);
      }

      internal class RouteTreeNode : TreeNode
      {
         public OrgFolder orgFolder;

         public RouteTreeNode(OrgFolder of)
         {
            Text = of.name;
            orgFolder = of;
            
            if (of.items == null)
               of.items = new List<OrgFolderItem>();

            createChildNodes(of);
         }

         private void createChildNodes(OrgFolder of)
         {
            foreach (OrgFolderItem item in of.items)
            {
               TreeNode node = createNode(item);
               Nodes.Add(node);
            }
         }

         private static TreeNode createNode(OrgFolderItem item)
         {
            DataSet<string, Region> dsRegion =
               (DataSet<string, Region>)DataModule.Get
               (GRSoft.NapoleonManager.Region.OBJECT_NAME);

            string caption = string.Empty;

            if (dsRegion != null && dsRegion.ContainsKey(item.name))
            {
               caption = dsRegion[item.name].Name;
            }
            else
            {
               caption = String.Format("Объект с ключом {0} в наборе не найден", item.name);
            }

            TreeNode node = new TreeNode();
            node.Text = caption;
            node.Tag = item.name;
            return node;
         }

         public void addNode(string id)
         {
            OrgFolderItem item = new OrgFolderItem();
            item.name = id;

            orgFolder.items.Add(item);
            TreeNode node = createNode(item);
            Nodes.Add(node);
         }
      }

      private bool HasRegionValue(String name)
      {
         bool result = false;
         foreach(Region r in dsRegion.Data)
            if (r.Name.Equals(name))
            {
               result = true;
               break;
            }

         return result;
      }

      private void btnAddReg_Click(object sender, EventArgs e)
      {
         LiveArea la = FmRegionEdit.Open(null);
         if (la != null)
         {
            Region r = new Region();
            r.id = la.id;
            r.name = la.name;
            r.code = la.code;

            dsRegion.Add(r.id, r);
            UpdateDgvRegion();
            btnSave.Enabled = true;
         }
      }

      private void UpdateDgvRegion()
      {
         if (dsRegion != null)
         {
            List<Region> list = new List<Region>();
            list.AddRange(dsRegion.Values);
            list.Sort(new Comparison<Region>
               (delegate(Region r1, Region r2) 
               { return r1.Name.CompareTo(r2.Name); }));

            dgvRegion.DataSource = list;

            List<Region2> listR2 = new List<Region2>();
            listR2.AddRange(dsRegion2.Values);
            cbRegionR2.DataSource = listR2;

            List<Region1> listR1 = new List<Region1>();
            listR1.AddRange(dsRegion1.Values);
            cbRegionR1.DataSource = listR1;

            cbRegionR1.SelectedIndex = -1;
            cbRegionR2.SelectedIndex = -1;
         }
      }

      private IDataSet GetChangedOrgFolder()
      {
         dsOrgFolder.Clear();
         int index = 1;

         foreach (TreeNode node in tvRoute.Nodes)
         {
            if (node is RouteTreeNode)
            {
               OrgFolder of = ((RouteTreeNode)node).orgFolder;
               of.name = node.Text;
               of.id = index++;

               of.items.Clear();

               foreach (TreeNode cn in node.Nodes)
               {
                  if (cn.Tag != null && cn.Tag is string)
                  {
                     OrgFolderItem ofi = new OrgFolderItem();
                     ofi.name = (string)cn.Tag;
                     of.items.Add(ofi);
                  }
               }

               dsOrgFolder.Add(dsOrgFolder.Count, of);
            }
         }

         return dsOrgFolder;
      }

      private bool HasEmptyNode()
      {
         foreach (TreeNode node in tvRoute.Nodes)
            if (node.Nodes.Count == 0)
               return true;

         return false;
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         Agent agent = (Agent)cbAgents.SelectedItem;

         if (agent != null)
         {
            if (HasEmptyNode() && MessageBox.Show(
               "В списке есть пустые папки, которые не будут сохранены в базе.\nСохранить?",
               "Внимание", MessageBoxButtons.OKCancel, MessageBoxIcon.Warning) != DialogResult.OK)
                  return;

            List<IDataSet> wrSet = new List<IDataSet>();
            List<IDataSet> rmvSet = new List<IDataSet>();

            if (dsRegion.Count > 0)
               wrSet.Add(dsRegion);

            if (dsDelRegion.Count > 0)
               rmvSet.Add(dsDelRegion);

            List<ReplacedSet> replaced = new List<ReplacedSet>();
            replaced.Add(new ReplacedSet(agent.id, GetChangedOrgFolder()));

            if (DataModule.UpdateDataSet(wrSet, rmvSet, replaced, Config.GetConfig().GetConnection()) == false)
            {
               MessageBox.Show("Ошибка при записи в базу данных.", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
            else
               btnSave.Enabled = false;
         }
      }

      private void dgvRegion_MouseDown(object sender, MouseEventArgs e)
      {
         DataGridView grid = (DataGridView)sender;

         DataGridView.HitTestInfo info = grid.HitTest(e.X, e.Y);

         if (info.ColumnIndex != -1 && info.RowIndex != -1)
         {
            grid.CurrentCell = grid[info.ColumnIndex, info.RowIndex];

            Region region = grid.Rows[info.RowIndex].DataBoundItem as Region;
            UpdateCbControls(region);

            if (region != null)
               grid.DoDragDrop(region, DragDropEffects.Copy);
         }
      }

      private void tvRoute_DragEnter(object sender, DragEventArgs e)
      {
         e.Effect = DragDropEffects.Copy;
      }

      private void tvRoute_DragDrop(object sender, DragEventArgs e)
      {
         TreeNode targetNode = DataUtils.GetNodeFromPoint(tvRoute, new Point(e.X, e.Y));

         if (targetNode != null)
         {
            if (e.Data.GetDataPresent(typeof(Region)))
            {
               TreeNode parent = DataUtils.getTopParent(targetNode);
               Region region = e.Data.GetData(typeof(Region)) as Region;

               if (parent is RouteTreeNode)
               {
                  RouteTreeNode rtn = (RouteTreeNode)parent;
                  rtn.addNode(region.id);

                  btnSave.Enabled = true;
               }
            }
         }
      }

      private void btnDelDay_Click(object sender, EventArgs e)
      {
         TreeNode selNode = tvRoute.SelectedNode;

         if (selNode != null &&
            MessageBox.Show("Запись будет удалена, удалить?",
               "Вопрос", MessageBoxButtons.OKCancel,
               MessageBoxIcon.Question) == DialogResult.OK)
         {
            selNode.Remove();
            btnSave.Enabled = true;
         }
      }

      private TreeNode GetTopLevelNode(TreeView treeView, int x, int y)
      {
         TreeNode selNode = treeView.GetNodeAt(x, y);

         if (selNode != null)
         {
            while (selNode.Parent != null)
               selNode = selNode.Parent;
         }

         return selNode;
      }

      private TreeNode GetTopLevelNode(TreeView treeView)
      {
         TreeNode selNode = treeView.SelectedNode;

         if (selNode != null)
         {
            while (selNode.Parent != null)
               selNode = selNode.Parent;
         }

         return selNode;
      }

      private void tvRoute_MouseDown(object sender, MouseEventArgs e)
      {
         TreeNode selNode = GetTopLevelNode((TreeView)sender, e.X, e.Y);

         if (selNode != null)
            tbRoute.Text = selNode.Text;
      }

      private void btnEditDay_Click(object sender, EventArgs e)
      {
         TreeNode selNode = GetTopLevelNode(tvRoute);
         string name = tbRoute.Text;

         if (selNode != null && name.Length > 0)
            selNode.Text = name;

         btnSave.Enabled = true;
      }

      private void btnUp_Click(object sender, EventArgs e)
      {
         TreeNode selNode = tvRoute.SelectedNode;

         if (selNode != null)
         {
            int index = selNode.Index;
            TreeNode parenNode = selNode.Parent;

            if (index > 0)
            {
               selNode.Remove();

               if (parenNode == null)
                  tvRoute.Nodes.Insert(index - 1, selNode);
               else
                  parenNode.Nodes.Insert(index - 1, selNode);

               tvRoute.SelectedNode = selNode;
               btnSave.Enabled = true;
            }
         }
      }

      private void btnDown_Click(object sender, EventArgs e)
      {
         TreeNode selNode = tvRoute.SelectedNode;

         if (selNode != null)
         {
            int index = selNode.Index;
            TreeNode parenNode = selNode.Parent;

            if (index < (parenNode != null ? parenNode.Nodes.Count : tvRoute.Nodes.Count))
            {
               selNode.Remove();

               if (parenNode == null)
                  tvRoute.Nodes.Insert(index + 1, selNode);
               else
                  parenNode.Nodes.Insert(index + 1, selNode);

               tvRoute.SelectedNode = selNode;
               btnSave.Enabled = true;
            }
         }
      }

      private void btnFindDown_Click(object sender, EventArgs e)
      {
         searchEngine.find(tbFind.Text, Direction.DOWN);
      }

      private void btnFindUp_Click(object sender, EventArgs e)
      {
         searchEngine.find(tbFind.Text, Direction.UP);
      }

      private void tbFind_KeyDown(object sender, KeyEventArgs e)
      {
         if (e.KeyCode == Keys.Enter)
         {
            searchEngine.find(tbFind.Text, Direction.DOWN);
         }
      }

      private void btnDelReg_Click(object sender, EventArgs e)
      {
         Region region = (Region)dgvRegion.CurrentRow.DataBoundItem;

         if (region != null &&
            MessageBox.Show("Запись будет удалена, удалить?", "Вопрос", MessageBoxButtons.OKCancel,
            MessageBoxIcon.Question) == DialogResult.OK)
         {
            if (!dsDelRegion.ContainsKey(region.id))
               dsDelRegion.Add(region.id, region);

            if (dsRegion.ContainsKey(region.id))
               dsRegion.Remove(region.id);

            UpdateDgvRegion();
            btnSave.Enabled = true;
         }
      }

      private void btnEditReg_Click(object sender, EventArgs e)
      {
         Region region = (Region)dgvRegion.CurrentRow.DataBoundItem;
         LiveArea la = FmRegionEdit.Open(region);

         if (la != null)
         {
            dgvRegion.Refresh();
            btnSave.Enabled = true;
         }
      }

      private void FmRegionRoute_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK && btnSave.Enabled == true &&
            MessageBox.Show("Сохранить изменения", "Вопрос", MessageBoxButtons.OKCancel
               , MessageBoxIcon.Question) == DialogResult.OK)
         {
            btnSave_Click(null, null);
         }
      }

      private void EnableControl(bool enable)
      {
         tbRoute.Enabled = enable;
         btnAddDay.Enabled = enable;
         btnEditDay.Enabled = enable;
         btnDelDay.Enabled = enable;
         btnUp.Enabled = enable;
         btnDown.Enabled = enable;
         btnAddReg.Enabled = enable;
         btnEditReg.Enabled = enable;
         btnDelReg.Enabled = enable;
         tbFind.Enabled = enable;
         btnFindDown.Enabled = enable;
         btnFindUp.Enabled = enable;
         btnRegionR1R2.Enabled = enable;
      }

      private void cbAgents_SelectedIndexChanged(object sender, EventArgs e)
      {
         EnableControl(false);
         tvRoute.Nodes.Clear();
         btnSave.Enabled = false;
      }

      private void dgvRegion_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         TreeNode node = GetTopLevelNode(tvRoute);

         if (node != null)
         {
            bool p = false;
           
            foreach (TreeNode n in node.Nodes)
               if (n.Tag is string && 
                  ((string)n.Tag).Equals(((Region)dgvRegion.Rows[e.RowIndex].DataBoundItem).id))
               {
                  p = true;
                  break;
               }

            if (p)
               e.CellStyle.BackColor = Color.LightBlue;
         }
      }

      private void tvRoute_MouseUp(object sender, MouseEventArgs e)
      {
         dgvRegion.Invalidate();
      }

      private void btnRegionR1R2_Click(object sender, EventArgs e)
      {
         FmRegionParent.Open();
      }

      private Region GetSelectedRegion()
      {
         Region result = null;
         DataGridViewRow row = dgvRegion.CurrentRow;

         if (row != null)
            result = row.DataBoundItem as Region;

         return result;
      }

      private Boolean updatingCbControls = false;

      private void UpdateCbControls(Region region)
      {
         updatingCbControls = true;

         if (region != null)
         {
            List<Region2> listR2 = new List<Region2>();
            listR2.AddRange(dsRegion2.Values);
            cbRegionR2.DataSource = listR2;

            List<Region1> listR1 = new List<Region1>();
            listR1.AddRange(dsRegion1.Values);
            cbRegionR1.DataSource = listR1;

            cbRegionR1.SelectedIndex = -1;
            cbRegionR2.SelectedIndex = -1;

            if (region.region1.Length > 0)
            {
               foreach (Region1 r1 in (List<Region1>)cbRegionR1.DataSource)
               {
                  if (r1.Id.Equals(region.region1))
                  {
                     cbRegionR1.SelectedItem = r1;
                     break;
                  }
               }
            }

            if (region.region2.Length > 0)
            {
               foreach (Region2 r2 in (List<Region2>)cbRegionR2.DataSource)
               {
                  if (r2.Id.Equals(region.region2))
                  {
                     cbRegionR2.SelectedItem = r2;
                     break;
                  }
               }
            }
         }

         updatingCbControls = false;
      }

      private void cbRegionR1_SelectedIndexChanged(object sender, EventArgs e)
      {
         if (!updatingCbControls)
         {
            Region1 r1 = cbRegionR1.SelectedItem as Region1;
            Region r = GetSelectedRegion();

            if (r != null &&
               r1 != null && r1.region2 != null && r1.region2.Length > 0)
            {
               r.region1 = r1.id;
               r.r1 = r1;

               foreach (Region2 r2 in cbRegionR2.Items)
               {
                  if (r2.id.Equals(r1.region2))
                  {
                     cbRegionR2.SelectedItem = r2;
                     r.region2 = r2.id;
                     break;
                  }
               }

               btnSave.Enabled = true;
            }
         }
      }

      private void cbRegionR2_SelectedIndexChanged(object sender, EventArgs e)
      {
         if (!updatingCbControls)
         {
            Region2 r2 = cbRegionR2.SelectedItem as Region2;
            Region r = GetSelectedRegion();

            if (r != null && r2 != null)
            {
               r.region2 = r2.id;
               r.r2 = r2;

               List<Region1> listR1 = new List<Region1>();
               
               foreach (Region1 r1 in dsRegion1.Data)
                  if (r1.region2.Equals(r2.id))
                     listR1.Add(r1);

               cbRegionR1.DataSource = listR1;
               btnSave.Enabled = true;
            }
         }
      }
   }
}
