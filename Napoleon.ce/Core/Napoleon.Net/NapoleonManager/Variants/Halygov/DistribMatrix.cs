using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using System.Collections;

namespace GRSoft.NapoleonManager
{
   public partial class DistribMatrix : Form
   {
      DataSet<string, Org> dsOrgs;
      DataSet<string, ManagerFolder> dsFolders;

      DataSet<string, DistributionMatrix> dsDistrib = new DataSet<string, DistributionMatrix>(DistributionMatrix.OBJECT_NAME, false);

      DataSet<string, Price> dsPrice;

      public DistribMatrix()
      {
         InitializeComponent();

         dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ?? new DataSet<string, Price>(Price.OBJECT_NAME);
         dgvOrgs.AutoGenerateColumns = false;
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);

         List<IDataSet> upd = new List<IDataSet>();
         if (dsPrice.Count == 0)
            upd.Add(dsPrice);
         upd.Add(dsDistrib);

         FmWait.StdDataRefresh(this, upd, DataLoaded);
      }

      void DataLoaded()
      {
         List<Agent> agents = new List<Agent>((IEnumerable<Agent>)((Manager)CurrentUser.user).GetAgents().Data);
         agents.Sort();
         agents.ForEach(x => cbAgents.Items.Add(x));
         if (agents.Count > 0)
            cbAgents.SelectedIndex = 0;
      }

      private void cbAgents_SelectedIndexChanged(object sender, EventArgs e)
      {
         Agent a = cbAgents.SelectedItem as Agent;
         if (a == null)
            return;

         dsOrgs = DataModule.GetUserDataSet(a.id, Org.OBJECT_NAME, typeof(DataSet<string, Org>), true) as DataSet<string, Org>;
         dsFolders = DataModule.GetUserDataSet(a.id, ManagerFolder.OBJECT_NAME, typeof(DataSet<string, ManagerFolder>), true) as DataSet<string, ManagerFolder>;

         List<IDataSet> upd = new List<IDataSet>();
         if (dsOrgs.Count == 0)
            upd.Add(dsOrgs);
         if (dsFolders.Count == 0)
            upd.Add(dsFolders);

         if (upd.Count > 0)
            FmWait.StdDataRefresh(this, upd, OrgLoaded);
         else
            OrgLoaded();
      }

      void OrgLoaded()
      {
         ArticlesTreeConstructor ctr = new ArticlesTreeConstructor(tvPrice, dsFolders, dsPrice);
         ctr.MakeArticlesTree(0, 1);

         List<Org> orgs = new List<Org>();
         orgs.AddRange((IEnumerable<Org>)dsOrgs.Data);
         orgs.Sort();

         dgvOrgs.DataSource = orgs;
      }

      private void dgvOrgs_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         Org o = dgvOrgs.Rows[e.RowIndex].DataBoundItem as Org;
         tsbCopy.Enabled = dsDistrib.ContainsKey(o.id);
         RefreshOrgMatrix(o);
      }

      void RefreshOrgMatrix(Org o)
      {
         DataSet<string, Price> tmpPrice = new DataSet<string, Price>(Price.OBJECT_NAME, false);
         if (dsDistrib.ContainsKey(o.id))
         {
            DistributionMatrix mtx = dsDistrib[o.id];
            foreach (DistributionMatrix.Item item in mtx.items)
               if (dsPrice.ContainsKey(item.id))
                  tmpPrice[item.id] = dsPrice[item.id];
         }

         tvOrgMatrix.Enabled = false;
         tvOrgMatrix.SuspendLayout();

         List<ManagerFolder> expanded = new List<ManagerFolder>();
         foreach (TreeNode tn in tvOrgMatrix.Nodes)
         {
            ManagerFolder mf = tn.Tag as ManagerFolder;
            if (tn.IsExpanded && mf != null)
               expanded.Add(mf);
         }

         ArticlesTreeConstructor ctr = new ArticlesTreeConstructor(tvOrgMatrix, dsFolders, tmpPrice);
         ctr.MakeArticlesTree(0, 1);
         ctr.RemoveEmptyNodes();

         foreach (TreeNode tn in tvOrgMatrix.Nodes)
         {
            if (expanded.Contains(tn.Tag as ManagerFolder))
               tn.Expand();
         }

         tvOrgMatrix.ResumeLayout();
         tvOrgMatrix.Enabled = true;
      }

      private void tvPrice_ItemDrag(object sender, ItemDragEventArgs e)
      {
         if (e.Button == MouseButtons.Left)
         {
            List<TreeNode> sel = tvPrice.SelectedNodes;
            TreeNode tn = tvPrice.GetNodeAt(tvPrice.PointToClient(Cursor.Position));
            if (sel.Contains(tn) == false)
               tvPrice.SelectedNode = tn;
            DoDragDrop(tvPrice.SelectedNodes, DragDropEffects.Move | DragDropEffects.Copy);
         }
      }

      private void tvOrgMatrix_DragEnter(object sender, DragEventArgs e)
      {
         if (e.Data.GetDataPresent(typeof(List<TreeNode>)) && ((e.AllowedEffect & DragDropEffects.Copy) != 0) )
            e.Effect = DragDropEffects.Copy;
      }

      void AddNodes(IList nodes, DistributionMatrix matrix)
      {
         foreach (TreeNode tn in nodes)
         {
            if (tn.Tag is ManagerFolder)
            {
               AddNodes(tn.Nodes, matrix);
               continue;
            }
            Price p = tn.Tag as Price;
            if (p != null)
               matrix.Add(p);
         }
      }

      private void tvOrgMatrix_DragDrop(object sender, DragEventArgs e)
      {
         if (dgvOrgs.SelectedRows.Count == 0)
            return;

         if (e.Data.GetDataPresent(typeof(List<TreeNode>)))
         {
            List<TreeNode> nodes = e.Data.GetData(typeof(List<TreeNode>)) as List<TreeNode>;
            if (nodes.Count == 0)
               return;

            AddMatrixItems(nodes);
         }
      }

      private void AddMatrixItems(List<TreeNode> nodes)
      {
         Org o = dgvOrgs.SelectedRows[0].DataBoundItem as Org;
         DistributionMatrix dm;
         if (dsDistrib.ContainsKey(o.id))
            dm = dsDistrib[o.id];
         else
         {
            dm = new DistributionMatrix();
            dm.id = o.id;
            dsDistrib[o.id] = dm;
         }

         int count = dm.items.Count;
         AddNodes(nodes, dm);

         RefreshOrgMatrix(o);

         if (dm.items.Count != count)
            tsbSave.Enabled = true;
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         base.OnClosing(e);
         if (!CheckChanges())
            e.Cancel = true;
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
         ReplacedSet rs = new ReplacedSet(dsDistrib);
         List<IDataSet> wr = new List<IDataSet>();
         bool ret = DataModule.UpdateDataSet(null, null, new List<ReplacedSet>(new ReplacedSet[] {rs}), Config.GetConfig().GetConnection());
         if (showDialog)
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");

         return ret;
      }

      private void tsbSave_Click(object sender, EventArgs e)
      {
         tsbSave.Enabled = !SaveChanges(true);
      }

      private void tvPrice_NodeMouseDoubleClick(object sender, TreeNodeMouseClickEventArgs e)
      {
         AddMatrixItems(tvPrice.SelectedNodes);
      }

      void RemovePriceItems(DistributionMatrix dm, IList nodes)
      {
         foreach (TreeNode tn in nodes)
         {
            Price prc = tn.Tag as Price;
            if(prc != null)
               dm.Remove(prc);
            else if( tn.Tag is ManagerFolder)
               RemovePriceItems(dm, tn.Nodes);
         }
      }

      private void tsbRemove_Click(object sender, EventArgs e)
      {
         Org o = dgvOrgs.SelectedRows[0].DataBoundItem as Org;
         DistributionMatrix dm;
         if (!dsDistrib.ContainsKey(o.id))
            return;

         dm = dsDistrib[o.id];
         int count = dm.items.Count;
         RemovePriceItems(dm, tvOrgMatrix.SelectedNodes);
         RefreshOrgMatrix(o);
         if (dm.items.Count != count)
            tsbSave.Enabled = true;
      }

      private void tsmCopyTo_Click(object sender, EventArgs e)
      {
         Org org = dgvOrgs.CurrentRow.DataBoundItem as Org;
         List<Org> cpy = CopyMatrixTo.SelectedOrgsToCopy(dsOrgs, org, dsDistrib);
         if (cpy.Count > 0)
         {
            Dictionary<Org, int> indexes = new Dictionary<Org, int>();
            foreach (DataGridViewRow row in dgvOrgs.Rows)
               indexes[row.DataBoundItem as Org] = row.Index;

            DistributionMatrix dm = dsDistrib[org.id];
            foreach (Org o in cpy)
            {
               DistributionMatrix dest = new DistributionMatrix();
               dest.id = o.id;
               foreach (DistributionMatrix.Item i in dm.items)
               {
                  DistributionMatrix.Item destI = new DistributionMatrix.Item();
                  destI.id = i.id;
                  dest.items.Add(destI);
               }
               dsDistrib[o.id] = dest;
               dgvOrgs.InvalidateRow(indexes[o]);
            }
            tsbSave.Enabled = true;
         }
      }

      private void contextMenuStrip1_Opening(object sender, CancelEventArgs e)
      {
         bool diabled = true;
         Point pt = dgvOrgs.PointToClient(Cursor.Position);
         DataGridView.HitTestInfo info = dgvOrgs.HitTest(pt.X, pt.Y);
         if (info.RowIndex >= 0 && info.Type == DataGridViewHitTestType.Cell)
         {
            if (dgvOrgs.CurrentRow.Index != info.RowIndex)
               dgvOrgs.CurrentCell = dgvOrgs.Rows[info.RowIndex].Cells[0];
         }

         Org o = dgvOrgs.CurrentRow.DataBoundItem as Org;
         if (o != null && dsDistrib.ContainsKey(o.id))
            diabled = false;

         tsmCopyTo.Enabled = !diabled;
      }

      private void dgvOrgs_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         Org o = dgvOrgs.Rows[e.RowIndex].DataBoundItem as Org;
         if (dsDistrib.ContainsKey(o.id))
            e.CellStyle.BackColor = Color.LightGray;
      }
   }

   class DistributionMatrix : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "DistributionMatrix";

      [KeyField]
      public string id = "";

      public class Item : GRSoft.Network.DataObject
      {
         [Reference("ManagerPrice", "id", typeof(Price))]
         public Price item = null;
         public string id = "";
      }

      [ItemType(typeof(Item))]
      public List<Item> items = new List<Item>();

      internal void Add(Price p)
      {
         foreach (Item i in items)
            if (i.id == p.id)
               return;

         Item ii = new Item();
         ii.id = p.id;
         items.Add(ii);
      }

      internal void Remove(Price prc)
      {
         foreach (Item i in items)
            if (i.id == prc.id)
            {
               items.Remove(i);
               break;
            }
      }
   }
}
