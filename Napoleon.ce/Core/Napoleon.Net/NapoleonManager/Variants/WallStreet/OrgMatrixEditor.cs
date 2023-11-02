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
   public partial class OrgMatrixEditor : Form
   {
      DataSet<string, OrgCategory> categories = new DataSet<string, OrgCategory>(OrgCategory.OBJECT_NAME, false);
      DataSet<string, Org> orgs = new DataSet<string, Org>(Org.COMMON_OBJECT_NAME, false);
      DataSet<string, ManagerFolder> folders = new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME);
      DataSet<string, Price> prices = new DataSet<string, Price>(Price.OBJECT_NAME);

      SimpleDataSet<OrgAssortimentMatrix> asmMatrix = new SimpleDataSet<OrgAssortimentMatrix>(OrgAssortimentMatrix.OBJECT_NAME, false);

      List<OrgAssortimentMatrix> modified = new List<OrgAssortimentMatrix>();
      List<OrgAssortimentMatrix> removed = new List<OrgAssortimentMatrix>();

      TreeSearch searchOrg;
      TreeSearch searchPrice;

      Color customMatrixBack = Color.LightPink;

      static OrgMatrixEditor instance = null;

      public OrgMatrixEditor()
      {
         InitializeComponent(); 
         
         searchPrice = new TreeSearch(priceTree, tbFindPrice.TextBox);
         searchOrg = new OrgTreeSearch(matrixTree, tbFind.TextBox);

         searchOrg.BlockSearch(true);
         tscbMatrixMode.SelectedIndex = 0;
      }

      private void btnClearFind_Click(object sender, EventArgs e)
      {
         searchOrg.ClearFind();
      }

      private void tbClearFindPrice_Click(object sender, EventArgs e)
      {
         searchPrice.ClearFind();
      }

      public static void Open()
      {
         if (instance == null)
         {
            instance = new OrgMatrixEditor();
            instance.Show();
         }
         else
         {
            instance.RefreshData(false);
            instance.BringToFront();
         }
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData(false);
      }

      protected override void OnClosed(EventArgs e)
      {
         base.OnClosed(e);
         instance = null;
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

      TreeNode MakeNode(OrgCategory category)
      {
         TreeNode tn = new TreeNode(category.name);
         tn.ImageIndex = 0;
         tn.Tag = category;

         OrgAssortimentMatrix mtx = FindMatrix(category);
         if( mtx != null )
            AddMatrixItems(tn, mtx);

         return tn;
      }

      OrgAssortimentMatrix FindMatrix(OrgCategory category)
      {
         foreach (OrgAssortimentMatrix item in asmMatrix.Data)
            if (item.category == category.id && item.id.Length == 0)
               return item;

         return null;
      }

      OrgAssortimentMatrix FindMatrix(Org org)
      {
         foreach (OrgAssortimentMatrix item in asmMatrix.Data)
            if (item.id == org.id)
               return item;

         return null;
      }

      TreeNode MakeNode(Org org)
      {
         OrgEx oe = new OrgEx(org);
         TreeNode tn = new TreeNode(org.Name);
         tn.ImageIndex = 0;
         tn.Tag = oe;

         OrgAssortimentMatrix mtx = FindMatrix(org);
         if (mtx != null)
         {
            AddMatrixItems(tn, mtx);
            oe.haveCustomMatrix = true;
            tn.BackColor = customMatrixBack;
         } else
         {
            if (categories.ContainsKey(org.category))
            {
               OrgCategory category = categories[org.category];
               mtx = FindMatrix(category);
               if (mtx != null)
                  AddMatrixItems(tn, mtx);
            }
         }

         return tn;
      }

      private void AddMatrixItems(TreeNode tn, OrgAssortimentMatrix item)
      {
         foreach (MatrixItem mi in item.items)
         {
            if (mi.price == null)
               continue;

            TreeNode tnItem = new TreeNode(mi.price.Name);
            tnItem.ImageIndex = 1;
            tnItem.Tag = mi.price;
            tn.Nodes.Add(tnItem);
         }
      }

      private bool SaveChanges(bool showDialog)
      {
         if (modified.Count == 0 && removed.Count == 0)
            return true;

         List<IDataSet> upd = null;
         List<IDataSet> rmv = null;

         if (modified.Count > 0)
         {
            upd = new List<IDataSet>();
            SimpleDataSet<OrgAssortimentMatrix> wr = new SimpleDataSet<OrgAssortimentMatrix>(OrgAssortimentMatrix.OBJECT_NAME, false);
            foreach (OrgAssortimentMatrix omx in modified)
               wr.Add(omx);
            upd.Add(wr);
         }

         if (removed.Count > 0)
         {
            rmv = new List<IDataSet>();
            SimpleDataSet<OrgAssortimentMatrix> sds = new SimpleDataSet<OrgAssortimentMatrix>(OrgAssortimentMatrix.OBJECT_NAME, false);
            foreach (OrgAssortimentMatrix omx in removed)
               sds.Add(omx);
            rmv.Add(sds);
         }

         bool ret = DataModule.UpdateDataSet(upd, rmv, null, Config.GetConfig().GetConnection());
         if (ret)
         {
            modified.Clear();
            removed.Clear();
         }

         if (showDialog)
         {
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");
         }

         return ret;
      }

      void RefreshData(bool loadPrice)
      {
         List<IDataSet> upd = new List<IDataSet>();
         
         if (loadPrice || prices.Count == 0)
         {
            prices.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
            upd.Add(prices);
         }

         if (loadPrice || folders.Count == 0)
         {
            folders.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
            upd.Add(folders);
         }

         foreach (Agent a in ((Manager)CurrentUser.user).GetAgents().Data)
         {
            DataSet<string, Org> aorgs = DataModule.GetUserDataSet(a.id, Org.OBJECT_NAME, typeof(DataSet<string, Org>)) as DataSet<string, Org>;
            if (loadPrice || aorgs.Count == 0)
            {
               aorgs.Command = new ServerCommand(Commands.Impersonate(Commands.GET, a.id), aorgs.Name);
               upd.Add(aorgs);
            }
         }

         upd.Add(categories);
         upd.Add(asmMatrix);
         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      private void tsbRefresh_Click(object sender, EventArgs e)
      {
         RefreshData(true);
      }

      private void tsbSave_Click(object sender, EventArgs e)
      {
         tsbSave.Enabled = !SaveChanges(true);
      }

      private void tscbMatrixMode_SelectedIndexChanged(object sender, EventArgs e)
      {
         matrixTree.SuspendLayout();
         TreeNodeCollection nodes = matrixTree.Nodes;
         nodes.Clear();

         if (IsOrgMode)
         {
            Dictionary<string, bool> used = new Dictionary<string,bool>();
            List<Org> orgs = new List<Org>();
            foreach (Agent a in ((Manager)CurrentUser.user).GetAgents().Data)
            {
               DataSet<string, Org> aorgs = DataModule.GetUserDataSet(a.id, Org.OBJECT_NAME, typeof(DataSet<string, Org>)) as DataSet<string, Org>;
               foreach (Org o in aorgs.Data)
               {
                  if (used.ContainsKey(o.id) == false)
                     orgs.Add(o);
               }
            }
            orgs.Sort();
            foreach (Org o in orgs)
               nodes.Add(MakeNode(o));

            searchOrg.BlockSearch(false);
         }
         else
         {
            foreach (OrgCategory oc in categories.Data)
               nodes.Add(MakeNode(oc));

            searchOrg.BlockSearch(true);
         }

         if (nodes.Count > 0)
            matrixTree.SelectedNode = nodes[0];
         matrixTree.ResumeLayout();
      }

      bool IsOrgMode { get { return tscbMatrixMode.SelectedIndex == 1; } }

      void DoLoadData()
      {
         ArticlesTreeConstructor t = new ArticlesTreeConstructor(priceTree, folders, prices);
         t.MakeArticlesTree(0, 1);

         tscbMatrixMode_SelectedIndexChanged(this, EventArgs.Empty);
      }

      class OrgEx
      {
         public Org src;
         public bool haveCustomMatrix = false;

         public OrgEx(Org src)
         {
            this.src = src;
         }
      }

      OrgAssortimentMatrix CreateOrgMatrix(OrgEx oe)
      {
         OrgAssortimentMatrix mtx = new OrgAssortimentMatrix();
         mtx.id = oe.src.id;
         asmMatrix.Add(mtx);

         if (categories.ContainsKey(oe.src.category))
         {
            OrgAssortimentMatrix src = FindMatrix(categories[oe.src.category]);
            if (src != null)
            {
               foreach (MatrixItem si in src.items)
               {
                  if (si.price == null)
                     continue;

                  MatrixItem di = new MatrixItem();
                  di.id = si.id;
                  di.price = si.price;
                  mtx.items.Add(di);
               }
            }
         }

         return mtx;
      }

      void AddNodesToMatrix(TreeNode selected, List<TreeNode> nodes)
      {
         OrgAssortimentMatrix mtx = null;
         if (IsOrgMode)
         {
            OrgEx oe = selected.Tag as OrgEx;
            mtx = FindMatrix(oe.src);
            if (mtx == null)
            {
               oe.haveCustomMatrix = true;
               selected.BackColor = customMatrixBack;
               mtx = CreateOrgMatrix(oe);
            }
         }
         else
         {
            OrgCategory oc = selected.Tag as OrgCategory;
            mtx = FindMatrix(oc);
            if (mtx == null)
            {
               mtx = new OrgAssortimentMatrix();
               mtx.category = oc.id;
               asmMatrix.Add(mtx);
            }
         }

         if (modified.Contains(mtx) == false)
            modified.Add(mtx);
         removed.Remove(mtx);
         tsbSave.Enabled = true;

         TreeNodeCollection dest = selected.Nodes;
         AddFolder(mtx, dest, nodes);
      }

      private void AddFolder(OrgAssortimentMatrix mtx, TreeNodeCollection dest, ICollection src)
      {
         foreach (TreeNode tn in src)
         {
            Price p = tn.Tag as Price;
            if (p != null)
            {
               AddPriceItem(mtx, dest, p);
               continue;
            }
            if (tn.Tag is ManagerFolder)
               AddFolder(mtx, dest, tn.Nodes);
         }
      }

      private void AddPriceItem(OrgAssortimentMatrix mtx, TreeNodeCollection dest, Price p)
      {
         foreach (MatrixItem mi in mtx.items)
            if (mi.price.id == p.id)
               return;

         MatrixItem newItem = new MatrixItem();
         newItem.price = p;
         newItem.id = p.id;
         mtx.items.Add(newItem);

         TreeNode tn = new TreeNode(p.Name);
         tn.Tag = p;
         tn.ImageIndex = 1;
         dest.Add(tn);
      }

      private void priceTree_NodeMouseDoubleClick(object sender, TreeNodeMouseClickEventArgs e)
      {
         TreeNode selected = matrixTree.SelectedNode;
         if (selected == null)
            return;

         selected = DataUtils.getTopParent(selected);
         AddNodesToMatrix(selected, new List<TreeNode>(new TreeNode[] { e.Node }));
      }

      private void priceTree_ItemDrag(object sender, ItemDragEventArgs e)
      {
         if (e.Button == MouseButtons.Left)
         {
            List<TreeNode> sel = priceTree.SelectedNodes;
            TreeNode src = e.Item as TreeNode;
            if (sel.Contains(src) == false)
               sel.Add(src);
            DoDragDrop(sel, DragDropEffects.Copy);
         }
      }

      private void matrixTree_DragEnter(object sender, DragEventArgs e)
      {
         if (e.Data.GetDataPresent(typeof(List<TreeNode>)))
            e.Effect = DragDropEffects.Copy;
      }

      private void matrixTree_DragDrop(object sender, DragEventArgs e)
      {
         TreeNode targetNode = DataUtils.GetNodeFromPoint(matrixTree, new Point(e.X, e.Y));
         if (targetNode == null)
            return;

         TreeNode parent = DataUtils.getTopParent(targetNode);
         if (e.Data.GetDataPresent(typeof(List<TreeNode>)))
            AddNodesToMatrix(parent, e.Data.GetData(typeof(List<TreeNode>)) as List<TreeNode>);
      }

      class OrgTreeSearch : TreeSearch
      {
         public OrgTreeSearch(TreeView tree, TextBox text)
            : base(tree, text)
         {
            testNode = TestMatrixNode;
         }

         bool TestMatrixNode(TreeNode node, String text)
         {
            return node.Text.ToUpper().Contains(text.ToUpper());
         }

         protected override void SearchingNode(TreeNode node)
         {
            if (testNode(node, text.Text))
            {
               TreeNode newNode = new TreeNode();
               tree.Nodes.Add(newNode);

               newNode.Tag = node.Tag;
               newNode.Text = node.Text;
               newNode.ImageIndex = node.ImageIndex;

               foreach (TreeNode tn in node.Nodes)
               {
                  TreeNode ch = new TreeNode();
                  ch.Tag = tn.Tag;
                  ch.Text = tn.Text;
                  ch.ImageIndex = tn.ImageIndex;

                  newNode.Nodes.Add(ch);
               }
            }
         }
      }

      private void matrixTree_MouseDown(object sender, MouseEventArgs e)
      {
         if (e.Button == MouseButtons.Right)
         {  
            TreeNode tn = matrixTree.GetNodeAt(new Point(e.X, e.Y));
            if (tn != null)
            {
               matrixTree.SelectedNode = tn;
               tsmiRemoveItem.Visible = (tn.Level != 0);
               tsmiRemoveMatrix.Visible = (IsOrgMode && tn.Level == 0);
            }
         }
      }

      private void tsmiRemoveMatrix_Click(object sender, EventArgs e)
      {
         TreeNode node = matrixTree.SelectedNode;
         if (node == null || node.Level != 0 || !IsOrgMode)
            return;

         OrgEx oe = node.Tag as OrgEx;
         OrgAssortimentMatrix mtx = FindMatrix(oe.src);
         if (mtx != null)
         {
            if (removed.Contains(mtx) == false)
               removed.Add(mtx);

            modified.Remove(mtx);

            foreach (KeyValuePair<int, OrgAssortimentMatrix> kv in asmMatrix)
            {
               if (kv.Value == mtx)
               {
                  asmMatrix.Remove(kv.Key);
                  break;
               }
            }

            node.Nodes.Clear();
            if (categories.ContainsKey(oe.src.category))
            {
               OrgCategory category = categories[oe.src.category];
               mtx = FindMatrix(category);
               if (mtx != null)
                  AddMatrixItems(node, mtx);
            }
         }

         node.BackColor = Color.Transparent;
         oe.haveCustomMatrix = false;
         tsbSave.Enabled = true;
      }

      private void tsmiRemoveItem_Click(object sender, EventArgs e)
      {
         TreeNode node = matrixTree.SelectedNode;
         if (node == null || node.Level == 0)
            return;

         TreeNode parent = DataUtils.getTopParent(node);
         OrgAssortimentMatrix mtx = null;
         if (IsOrgMode)
         {
            OrgEx oe = parent.Tag as OrgEx;
            mtx = FindMatrix(oe.src);
            if (mtx == null)
            {
               oe.haveCustomMatrix = true;
               parent.BackColor = customMatrixBack;
               mtx = CreateOrgMatrix(oe);
            }
         }
         else
         {
            OrgCategory category = parent.Tag as OrgCategory;
            mtx = FindMatrix(category);
         }

         Price p = node.Tag as Price;
         foreach(MatrixItem mi in mtx.items)
            if (mi.id == p.id)
            {
               mtx.items.Remove(mi);
               break;
            }

         parent.Nodes.Remove(node);
         modified.Add(mtx);
         removed.Remove(mtx);
         tsbSave.Enabled = true;
      }
   }
}
