using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Data;
using System.Data.OleDb;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{

   class FmGoodsMatrixDesigner : FmMatrixDesigner
   {
      //private DataSet<string, Price> dsPrice;
      private DataSet<string, GroupGoods> dsGroupGoods;
      private DataSet<string, GoodsMatrix> dsGoodsMatrix;
      TreeSearch searchPrice;


      public FmGoodsMatrixDesigner()
      {
         tsbFindBack.Visible = false;
         tsbFind.Image = Properties.Resources.edit_clear_4;

         tsbFind.Click += ClearFind;
         tsbFind.Text = "Очистить поиск";

         dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ?? new DataSet<string, Price>(Price.OBJECT_NAME);
         dsGroupGoods = (DataSet<string, GroupGoods>)DataModule.Get(GroupGoods.OBJECT_NAME) ?? new DataSet<string, GroupGoods>(GroupGoods.OBJECT_NAME);
         dsGoodsMatrix = (DataSet<string, GoodsMatrix>)DataModule.Get(GoodsMatrix.OBJECT_NAME) ?? new DataSet<string, GoodsMatrix>(GoodsMatrix.OBJECT_NAME);

         ToolStripButton btnLoad = new System.Windows.Forms.ToolStripButton();
         btnLoad.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnLoad.Image = global::GRSoft.NapoleonManager.Properties.Resources.excel;
         btnLoad.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnLoad.Name = "btnLoad";
         btnLoad.Size = new System.Drawing.Size(23, 22);
         btnLoad.Text = "Загрузить из Excel";
         btnLoad.Click += new System.EventHandler(btnLoad_Click);

         toolStrip1.Items.Add(btnLoad);

         searchPrice = new TreeSearch(tvPrice, tstbFind.TextBox);
      }

      private void ClearFind(object sender, EventArgs e)
      {
         tstbFind.Text = string.Empty;
      }

      protected override void RefreshData()
      {
         List<IDataSet> upd = new List<IDataSet>();
         dsPrice.Filter = Price.GOODS_FILTER;
         upd.Add(dsPrice);
         upd.Add(dsGroupGoods);
         dsGoodsMatrix.Filter = "\"name\" is null or \"name\" is not null";

         upd.Add(dsGoodsMatrix);

         FmWait.StdDataRefresh(this, upd, DoLoadData, tsbRefresh);
      }

      void DoLoadData()
      {
         Dictionary<string, GoodsNode> nodes = FmGoods.MakeTree(dsGroupGoods, dsPrice);

         tvPrice.BeginUpdate();
         tvPrice.Nodes.Clear();

         foreach (TreeNode n in nodes.Values)
            tvPrice.Nodes.Add(n);

         tvPrice.EndUpdate();

         tvMatrix.BeginUpdate();
         tvMatrix.Nodes.Clear();
        
         foreach (Matrix matrix in dsGoodsMatrix.Data)
            if (CheckMatrix(matrix))
            {
               TreeNode node = new TreeNode();
               node.Text = matrix.name;
               node.Tag = matrix;

               foreach (MatrixItem item in matrix.items)
               {
                  if (item != null && dsPrice.ContainsKey(item.id))
                  {
                     TreeNode nodeItem = new TreeNode(dsPrice[item.id].name);
                     nodeItem.Tag = item;
                     node.Nodes.Add(nodeItem);
                  }
               }

               tvMatrix.Nodes.Add(node);
            }

         tvMatrix.EndUpdate();
      }

      protected override void ItemsDragDrop(DragEventArgs e)
      {
         TreeNode targetNode = DataUtils.GetNodeFromPoint(tvMatrix, new Point(e.X, e.Y));

         if (targetNode != null)
         {
            TreeNode parent = DataUtils.getTopParent(targetNode);
            if (e.Data.GetDataPresent(typeof(GroupGoodsNode)))
               AppendGroupToMatrix(parent, e.Data.GetData(typeof(GroupGoodsNode)) as GroupGoodsNode);
            else if (e.Data.GetDataPresent(typeof(GoodsNode)))
               AppendGoodsToMatrix(parent, e.Data.GetData(typeof(GoodsNode)) as GoodsNode);

            tsbSave.Enabled = true;
         }
      }

      protected override void PriceNodeDblClick(TreeNodeMouseClickEventArgs e, TreeNode parent)
      {
         if (e.Node is GroupGoodsNode)
            AppendGroupToMatrix(parent, e.Node as GroupGoodsNode);
         else  if (e.Node is GoodsNode)
            AppendGoodsToMatrix(parent, e.Node as GoodsNode);

         tsbSave.Enabled = true;
      }

      private void AppendGoodsToMatrix(TreeNode parent, GoodsNode goodsNode)
      {
         if (parent != null)
         {
            Matrix matrix = parent.Tag as Matrix;

            if (matrix != null)
            {
               MatrixItem mi = new MatrixItem();
               mi.id = goodsNode.ID;

               TreeNode child = new TreeNode(goodsNode.Text);
               child.Tag = mi;

               parent.Nodes.Add(child);
               parent.Expand();
            }
         }
      }

      private void AppendGroupToMatrix(TreeNode parent, GroupGoodsNode groupGoodsNode)
      {
         if (parent != null)
         {
            Matrix matrix = parent.Tag as Matrix;

            if (matrix != null)
            {
               foreach (TreeNode n in groupGoodsNode.Nodes)
                  if (n is GroupGoodsNode)
                     AppendGroupToMatrix(parent, n as GroupGoodsNode);
                  else
                     AppendGoodsToMatrix(parent, n as GoodsNode);
            }
         }
      }

      protected override bool SaveData()
      {
         List<ReplacedSet> list = new List<ReplacedSet>();
         ReplacedSet rs = new ReplacedSet(GetMatrixDataSet());
         list.Add(rs);

         return DataModule.UpdateDataSet(null, null, list, Config.GetConfig().GetConnection());
      }

      public override IDataSet GetMatrixDataSet()
      {
         SimpleDataSet<GoodsMatrix> result = new SimpleDataSet<GoodsMatrix>(GoodsMatrix.OBJECT_NAME, false);

         foreach (TreeNode matrixNode in tvMatrix.Nodes)
         {
            GoodsMatrix matrix = new GoodsMatrix();
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

      void btnLoad_Click(object sender, EventArgs e)
      {
         OpenFileDialog dlg = new OpenFileDialog();
         if (dlg.ShowDialog() == DialogResult.OK)
            LoadItems(dlg.FileName);
      }

      private void LoadItems(string file)
      {
         Dictionary<string, Price> pdic = new Dictionary<string, Price>(); // price cache

         foreach (Price p in dsPrice.Data)
            pdic[p.name] = p;

         var connectionString = string.Format("Provider=Microsoft.Jet.OLEDB.4.0;Data Source={0}; Extended Properties=Excel 8.0;", file);
         var objConn = new OleDbConnection(connectionString);
         try
         {
            objConn.Open();

         }
         catch (Exception e)
         {
            MessageBox.Show(e.Message);
            return;
         } 
         var dt = objConn.GetOleDbSchemaTable(OleDbSchemaGuid.Tables, null);

         if (dt == null)
         {
            return;
         }

         tvMatrix.BeginUpdate();

         Dictionary<string, TreeNode> nc = new Dictionary<string, TreeNode>(); // nodecashe
         foreach (TreeNode matrixNode in tvMatrix.Nodes)
            nc[matrixNode.Text] = matrixNode;

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

            TreeNode curNode = null;
            if (nc.ContainsKey(group))
            {
               curNode = nc[group];
            }
            else
            {
               curNode = new TreeNode();
               curNode.Text = group;
               nc[group] = curNode;
               tvMatrix.Nodes.Add(curNode);
            }

            foreach (DataRow row in data.Rows)
            {
               object[] r = row.ItemArray;

               string name = r[0].ToString().Trim();
               if (name.Length == 0 || pdic.ContainsKey(name) == false)
                  break;

               Price prc = pdic[name];
               MatrixItem p = new MatrixItem();
               p.id = prc.id;
               p.price = prc;
               TreeNode tn = new TreeNode();
               tn.Tag = p;
               tn.Text = prc.Name;

               curNode.Nodes.Add(tn);
            }
         }

         tvMatrix.EndUpdate();
         tsbSave.Enabled = true;
      }
   }
}
