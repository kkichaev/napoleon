using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.UILib;
using GRSoft.NapoleonManager.Utils;
using System.IO;
using System.Drawing.Drawing2D;
using System.Drawing.Imaging;
using System.Threading;
using System.Reflection;

namespace GRSoft.NapoleonManager
{
   public partial class FmPriceViewAH : Form
   {
      protected DataSet<string, Price> dsCommonPrice;
      protected DataSet<string, ManagerFolder> dsCommonFolder;

      TreeGridNode[] priceNodes;
      bool clearing = false;

      public FmPriceViewAH()
      {
         InitializeComponent();
         
         dsCommonPrice = new DataSet<string, Price>(Price.OBJECT_NAME, false);
         dsCommonFolder = new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME, false);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> updSet = new List<IDataSet>();

         if (dsCommonFolder.Count == 0)
         {
            dsCommonFolder.Filter = "\"userid\" is null or \"userid\"=''";
            updSet.Add(dsCommonFolder);
         }

         if (dsCommonPrice.Count == 0)
         {
            dsCommonPrice.Filter = "\"userid\" is null or \"userid\"=''";
            updSet.Add(dsCommonPrice);
         }

         FmWait.StdDataRefresh(this, updSet, DoLoadData, btnRefresh);
      }

      void DoLoadData()
      {
         TreeView tmpTree = new TreeView();
         ArticlesTreeConstructor treeCnt = new ArticlesTreeConstructor(tmpTree, dsCommonFolder, dsCommonPrice);
         treeCnt.MakeArticlesTree();

         tgvPrice.SuspendLayout();
         tgvPrice.Nodes.Clear();
         tgvPrice.Rows.Clear();

         foreach (TreeNode n in tmpTree.Nodes)
            fillGridRecursive(n, tgvPrice.Nodes);

         priceNodes = new TreeGridNode[tgvPrice.Nodes.Count];
         tgvPrice.Nodes.CopyTo(priceNodes, 0);

         tgvPrice.ResumeLayout();
      }

      virtual protected TreeGridNode AddPriceNode(TreeGridNodeCollection parent, Price p)
      {
         double cost = getCost(p);
         TreeGridNode result = parent.Add(p.name, cost, p.qty);
         result.Tag = p;
         return result;
      }

      virtual protected double getCost(Price p)
      {
         return p.cost != null && p.cost.Length > 0 ? p.cost[0] : 0.0;
      }

      virtual protected TreeGridNode AddFolderNode(TreeGridNodeCollection parent, ManagerFolder f)
      {
         TreeGridNode result = parent.Add(dsCommonFolder[f.id].name, null, null);
         result.Tag = f;
         return result;
      }

      private void fillGridRecursive(TreeNode node, TreeGridNodeCollection parent)
      {
         if (node.Tag is ManagerFolder)
         {
            TreeGridNode child = AddFolderNode(parent, (ManagerFolder)node.Tag);

            foreach (TreeNode n in node.Nodes)
               fillGridRecursive(n, child.Nodes);
         }
         else if (node.Tag is Price)
         {
            Price p = (Price)node.Tag;
            AddPriceNode(parent, p).Tag = p;
         }
      }

      private void tbFind_TextChanged(object sender, EventArgs e)
      {
         timer1.Stop();

         if (tbFind.Text.Length > 0)
            timer1.Start();
         else if (!clearing)
            btnClearSearch_Click(this, EventArgs.Empty);
      }

      private void btnClearSearch_Click(object sender, EventArgs e)
      {
         clearing = true;
         tbFind.Clear();

         tgvPrice.SuspendLayout();
         tgvPrice.Nodes.Clear();
         tgvPrice.Rows.Clear();

         if (priceNodes != null)
            foreach (TreeGridNode tn in priceNodes)
               tgvPrice.Nodes.Add(tn);

         tgvPrice.ResumeLayout();
         clearing = false;
      }

      void TestNode(TreeGridNode node, FindProc testProc)
      {
         Price p = node.Tag as Price;
         if (p != null && testProc(p))
         {
            TreeGridNode newNode = new TreeGridNode();
            tgvPrice.Nodes.Add(newNode);

            newNode.Tag = node.Tag;
            newNode.Height = node.Height;
            newNode.DefaultCellStyle = node.DefaultCellStyle;

            int pos = 0;
            foreach (DataGridViewCell cell in node.Cells)
            {
               DataGridViewCell dest = newNode.Cells[pos++];
               dest.Value = cell.Value;
               dest.Style.BackColor = cell.Style.BackColor;
            }
         }
      }

      void SearchingNode(TreeGridNode node, FindProc testProc)
      {
         TestNode(node, testProc);

         foreach (TreeGridNode child in node.Nodes)
         {
            TestNode(child, testProc);

            if (child.HasChildren)
               SearchingNode(child, testProc);
         }
      }

      void DoSearchPrice(FindProc testProc)
      {
         if (priceNodes == null)
            return;

         tgvPrice.SuspendLayout();
         tgvPrice.Nodes.Clear();
         tgvPrice.Rows.Clear();

         foreach (TreeGridNode tn in priceNodes)
            SearchingNode(tn, testProc);

         tgvPrice.ResumeLayout();
      }

      bool ContainsText(Price p)
      {
         return p.Name.ToUpper().Contains(tbFind.Text.ToUpper());
      }

      private void timer1_Tick(object sender, EventArgs e)
      {
         timer1.Stop();
         DoSearchPrice(ContainsText);
      }
   }
}
