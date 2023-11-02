using GRSoft.NapoleonManager.Properties;
using GRSoft.Network;
using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Data.OleDb;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmGoods : Form
   {
      private DataSet<string, Price> dsPrice;
      private DataSet<string, GroupGoods> dsGroupGoods;
      private List<GoodsNode> removed = new List<GoodsNode>();

      public FmGoods()
      {
         InitializeComponent();

         dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ?? new DataSet<string, Price>(Price.OBJECT_NAME);
         dsGroupGoods = (DataSet<string, GroupGoods>)DataModule.Get(GroupGoods.OBJECT_NAME) ?? new DataSet<string, GroupGoods>(GroupGoods.OBJECT_NAME);

         imageList1.Images.Add("group", Resources.group_add);
         imageList1.Images.Add("goods", Resources.pnt_doc);
         btnSave.Enabled = false;
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         AddItem(typeof(GroupGoodsNode));
      }
      
      private void AddItem(System.Type type)
      {
         GoodsNode gg = (GoodsNode)Activator.CreateInstance(type);
         TreeNode sel = tree.SelectedNode;
         FmPriceEdit dlg = new FmPriceEdit();
         if (dlg.ShowDialog() == DialogResult.OK)
         {
            CreateNew(sel, dlg.ItemName, tree.Nodes, gg);

            Price p = gg.innerObject as Price;

            if (p != null)
            {
               p.categ = dlg.Category;
               p.product = dlg.Production;
               p.barcode = dlg.Barcode;
            }

            tree.SelectedNode = gg;
            btnSave.Enabled = true;
         }
      }

      private void CreateNew(TreeNode node, string text, TreeNodeCollection root, GoodsNode item)
      {
         item.ObjText = text;

         GoodsNode parent = node as GoodsNode;

         if (parent != null)
            parent = parent.GetGroup() as GoodsNode;

         if(parent != null)
         {
            item.PID = parent.ID;
            parent.Nodes.Add(item);
            parent.ExpandAll();
         }
         else
            root.Add(item);
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         GoodsNode node = tree.SelectedNode as GoodsNode;

         if (node != null)
         {
            FmPriceEdit dlg = new FmPriceEdit();
            dlg.ItemName = node.ObjText;

            Price p = node.innerObject as Price;

            if (p != null)
            {
               dlg.Production = p.product;
               dlg.Category = p.categ;
               dlg.Weight = p.weight;
               dlg.Code = p.code;
               dlg.Barcode = p.barcode;
            }

            if (dlg.ShowDialog() == DialogResult.OK)
            {
               node.ObjText = dlg.ItemName.Trim();

               if (p != null)
               {
                  p.product = dlg.Production;
                  p.categ = dlg.Category;
                  p.weight = dlg.Weight;
                  p.code = dlg.Code;
                  p.barcode = dlg.Barcode;
               }

               tree.Invalidate();
               btnSave.Enabled = true;
            }
         }
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         GoodsNode node = tree.SelectedNode as GoodsNode;

         if (node != null)
         {
            if (DialogUtil.AskToDel(this))
            {
               MarkToDelRecursive(node);
               node.Remove();
               btnSave.Enabled = true;
            }
         }
      }

      private void MarkToDelRecursive(GoodsNode node)
      {
         foreach (TreeNode n in node.Nodes)
            MarkToDelRecursive((GoodsNode)n);

         node.MarkToDel();
         removed.Add(node);
      }

      private void btnAddGoods_Click(object sender, EventArgs e)
      {
         AddItem(typeof(GoodsNode));
      }

      private void tree_MouseDown(object sender, MouseEventArgs e)
      {
         if (e.Button == System.Windows.Forms.MouseButtons.Left)
         {
            TreeViewHitTestInfo hinfo = ((TreeView)sender).HitTest(e.X, e.Y);

            if (hinfo.Node == null)
               ((TreeView)sender).SelectedNode = null;
         } else if (e.Button == System.Windows.Forms.MouseButtons.Right)
         {
            TreeView tv = (TreeView)sender;
            tv.SelectedNode = tv.GetNodeAt(new Point(e.X, e.Y));
         }
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         DataSet<string, Price> goods = new DataSet<string, Price>(Price.OBJECT_NAME, false);
         DataSet<string, GroupGoods> groups = new DataSet<string, GroupGoods>(GroupGoods.OBJECT_NAME, false);

         List<GoodsNode> list = new List<GoodsNode>();
         CollectChangedItems(tree.Nodes, list);
         list.AddRange(removed);

         foreach (GoodsNode n in list)
         {
            if (n is GroupGoodsNode)
               groups[n.ID] = (GroupGoods)n.innerObject;
            else
            {
               Price p = (Price)n.innerObject;
               GroupGoodsNode ggn = n.Parent as GroupGoodsNode;
               if (ggn != null)
                  p.my = (n.Parent as GroupGoodsNode).IsMy ? 1 : 0;
               goods[n.ID] = p;
            }
         }

         List<IDataSet> wrSet = new List<IDataSet>();

         if(goods.Count > 0)
            wrSet.Add(goods);

         if(groups.Count > 0)
            wrSet.Add(groups);

         if (!DataModule.UpdateDataSet(wrSet, null, null, Config.GetConfig().GetConnection()))
            DialogUtil.UpdateErrMsg(this);
         else
         {
            btnSave.Enabled = false;

            foreach (GoodsNode n in list)
               n.changed = false;
         }
      }

      private void CollectChangedItems(TreeNodeCollection nodes, List<GoodsNode> list)
      { 
         foreach(TreeNode n in nodes)
         {
            CollectChangedItems(n.Nodes, list);

            if (((GoodsNode)n).changed)
               list.Add((GoodsNode)n);
         }
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         RefreshData();
      }

      private void DoLoadData()
      {
         Dictionary<string, GoodsNode> nodes = MakeTree(dsGroupGoods, dsPrice);

         tree.BeginUpdate();
         tree.Nodes.Clear();

         List<TreeNode> src = new List<TreeNode>();
         foreach (TreeNode tn in nodes.Values)
            src.Add(tn);

         src.Sort(NodeCmp);
         foreach (TreeNode n in src)
            tree.Nodes.Add(n);

         //tree.TreeViewNodeSorter = new NodesComparer();

         tree.ExpandAll();
         tree.EndUpdate();
      }

      int NodeCmp(TreeNode x, TreeNode y) { return x.Text.CompareTo(y.Text); }

      public static Dictionary<string, GoodsNode> MakeTree(DataSet<string, GroupGoods> dsGroupGoods, DataSet<string, Price> dsPrice)
      {
         Dictionary<string, GoodsNode> nodes = new Dictionary<string, GoodsNode>();
         Dictionary<string, GroupGoodsNode> data = new Dictionary<string, GroupGoodsNode>();
         List<string> preccessed = new List<string>();

         foreach (GroupGoods gg in dsGroupGoods.Values)
         {
            GroupGoodsNode ggn = new GroupGoodsNode(gg);

            if (ggn.PID.Trim().Length == 0)
               nodes[ggn.ID] = ggn;

            if (data.ContainsKey(ggn.PID))
            {
               data[ggn.PID].Nodes.Add(ggn);
               preccessed.Add(ggn.ID);
            }

            data[ggn.ID] = ggn;
         }

         bool process = false;

         List<GroupGoodsNode> list = new List<GroupGoodsNode>();

         foreach (GroupGoodsNode ggn in data.Values)
            list.Add(ggn);

         do
         {
            process = false;

            foreach (GroupGoodsNode ggn in list)
            {
               if (data.ContainsKey(ggn.PID) && !preccessed.Contains(ggn.ID))
               {
                  data[ggn.PID].Nodes.Add(ggn);
                  process = true;
                  preccessed.Add(ggn.ID);
               }
            }
         } while (process);

         foreach (Price g in dsPrice.Values)
         {
            GoodsNode gn = new GoodsNode(g);

            if (gn.PID.Trim().Length == 0)
               nodes[gn.ID] = gn;

            if (data.ContainsKey(gn.PID))
            {
               GroupGoodsNode ggn = data[gn.PID];
               ggn.Nodes.Add(gn);
               ggn.IsMy = g.my != 0;
            }
         }
         return nodes;
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
      }

      void RefreshData()
      {
         List<IDataSet> upd = new List<IDataSet>();
         dsPrice.Filter = Price.GOODS_FILTER;
         upd.Add(dsPrice);
         upd.Add(dsGroupGoods);

         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      private void btnMatrix_Click(object sender, EventArgs e)
      {
         new FmGoodsMatrixDesigner().Show();
      }

      private void tsbGoodsValues_Click(object sender, EventArgs e)
      {
         new FmGoodsValues().Show();
      }

      private void miSetMy_Click(object sender, EventArgs e)
      {
         SetIsMy(tree.Nodes, false);

         TreeNode tn = tree.SelectedNode;
         do
         {
            GroupGoodsNode gn = tn as GroupGoodsNode;
            if (gn == null)
               tn = tn.Parent;
            else
            {
               gn.IsMy = true;
               SetIsMy(tn.Nodes, true);
               break;
            }
         } while (tn != null);
         tree.Invalidate();
         btnSave.Enabled = true;
      }

      private void SetIsMy(TreeNodeCollection nodes, bool setMy)
      {
         foreach(TreeNode tn in nodes)
         {
            GroupGoodsNode ggn = tn as GroupGoodsNode;
            if (ggn != null)
            {
               ggn.IsMy = setMy;
               SetIsMy(tn.Nodes, setMy);
            }
            else
            {
               GoodsNode gn = tn as GoodsNode;
               Price p = gn.innerObject;
               int val = (setMy) ? 1 : 0;
               if (p.my != val)
               {
                  p.my = val;
                  gn.changed = true;
               }
            }
         }
      }

      private void tree_DrawNode(object sender, DrawTreeNodeEventArgs e)
      {
         GroupGoodsNode gn = e.Node as GroupGoodsNode;
         if (gn != null && gn.IsMy)
            GroupNode.MyGroupDraw(sender, e);
         else
            e.DrawDefault = true;
      }

      private void btnLoad_Click(object sender, EventArgs e)
      {
         OpenFileDialog dlg = new OpenFileDialog();
         if (dlg.ShowDialog() == DialogResult.OK)
            LoadItems(dlg.FileName);
      }

      private void LoadItems(string file)
      {
         List<string> pc = new List<string>(); // price cache

         foreach (Price p in dsPrice.Data)
            if (!pc.Contains(p.name))
               pc.Add(p.name);

         var connectionString = string.Format("Provider=Microsoft.Jet.OLEDB.4.0;Data Source={0}; Extended Properties=Excel 8.0;", file);
         var objConn = new OleDbConnection(connectionString);
         objConn.Open();
         var dt = objConn.GetOleDbSchemaTable(OleDbSchemaGuid.Tables, null);

         if (dt == null)
         {
            return;
         }

         tree.BeginUpdate();

         Dictionary<string, GroupGoodsNode> nc = new Dictionary<string, GroupGoodsNode>(); // nodecashe
         CollectGroupNodes(nc, tree.Nodes);

         foreach (DataRow sh in dt.Rows)
         {
            string group = sh["TABLE_NAME"].ToString().Trim();
            var adapter = new System.Data.OleDb.OleDbDataAdapter("SELECT * FROM [" + group + "]", connectionString);
            var ds = new DataSet();
            adapter.Fill(ds, group);

            DataTable data = ds.Tables[group];

            if (group.StartsWith("'"))
               group = group.Substring(1, group.Length - 3); // remove $ character
            else
               group = group.Substring(0, group.Length - 1); // remove $ character

            GroupGoodsNode curNode = null;
            if(nc.ContainsKey(group))
            {
               curNode = nc[group];
            } else
            {
               GroupGoods p = new GroupGoods();
               p.id = GRSoft.Network.DataObject.GenId();
               p.name = group;

               curNode = new GroupGoodsNode(p);
               curNode.changed = true;
               nc[group] = curNode;
               tree.Nodes.Add(curNode);
            }

            foreach (DataRow row in data.Rows)
            {
               object[] r = row.ItemArray;

               if (r[0].ToString().Length == 0)
                  break;

               Price p = new Price();
               p.id = GRSoft.Network.DataObject.GenId();
               p.name = r[0].ToString().Trim();
               p.categ = r[1].ToString().Trim();
               p.product = r[2].ToString().Trim();
               p.code = r[3].ToString().Trim();
               double weight = 0;
               Double.TryParse(r[3].ToString().Trim(), out weight);
               p.weight = weight;

               p.isGoods = 1;
               p.fid = curNode.innerObject.id;

               if (pc.Contains(p.name)) // skip if org presents
                  continue;

               GoodsNode pn = new GoodsNode(p);
               pn.changed = true;
               curNode.Nodes.Add(pn);
            }
         }

         tree.EndUpdate();
         btnSave.Enabled = true;
      }

      private void CollectGroupNodes(Dictionary<string, GroupGoodsNode> nc, TreeNodeCollection nodes)
      {
         foreach(TreeNode tn in nodes)
         {
            GroupGoodsNode ggn = tn as GroupGoodsNode;
            if (ggn != null)
               nc[ggn.Text] = ggn;
         }
      }
   }

   class NodesComparer : IComparer
   {
      public int Compare(object x, object y)
      {
         return ((GoodsNode)x).ObjText.CompareTo(((GoodsNode)y).ObjText);
      }
   }

   public class GoodsNode : TreeNode
   {
      public Price innerObject;
      public bool changed = true;

      public GoodsNode(Price obj)
      {
         this.innerObject = obj;
         this.changed = false;
         Tag = obj;

         ImageIndex = GetImageIndex();
         SelectedImageIndex = GetImageIndex();

         if (this.innerObject == null)
         {
            innerObject = CreateDataObject();
            innerObject.isGoods = 1;
            ((Price)innerObject).id = GRSoft.Network.DataObject.GenId();
            this.changed = true;
         }
         else
            this.Text = obj.name;
      }

      public GoodsNode() : this(null) {}

      protected virtual Price CreateDataObject(){ return new Price();}
      protected virtual int GetImageIndex() { return 1; }

      public virtual TreeNode GetGroup() { return Parent; }

      public string ObjText
      {
         get { return ((Price)innerObject).name; }
         set 
         { 
            ((Price)innerObject).name = value.Trim(); 
            Text = value; 
            changed = true; 
         }
      }

      public string ID { get { return innerObject.id; } }
      public string PID { get { return innerObject.fid; } set { innerObject.fid = value; } }

      public void MarkToDel()
      {
         innerObject.rem = 1;
         changed = true;
      }
   }

   class GroupGoodsNode : GoodsNode
   {
      public GroupGoodsNode(Price gg) : base(gg) { }
      public GroupGoodsNode() : this(null) { }

      public bool IsMy { get; set; }

      public override TreeNode GetGroup() { return this; }
      protected override Price CreateDataObject() { return new GroupGoods(); }
      protected override int GetImageIndex() { return 0; }
   } 
}
