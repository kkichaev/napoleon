using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager.Utils
{
   delegate bool TestNodeProc(TreeNode node, String text);

   class TreeSearch
   {
      protected TreeView tree;
      protected TextBox text;
      Timer timer;

      protected TestNodeProc testNode;

      bool clearing = false, inSearch = false, blockSearch = false;
      TreeNode[] saveTreeNodes;

      public TreeSearch(TreeView tree, TextBox text) :
         this(tree, text, null)
      {
         testNode = TestPriceNode;
      }

      public TreeSearch(TreeView tree, TextBox text, TestNodeProc tn)
      {
         this.testNode = tn;
         this.text = text;
         this.tree = tree;

         timer = new Timer();
         timer.Interval = 500;
         text.TextChanged += new EventHandler(text_TextChanged);

         timer.Tick += new EventHandler((o, e) =>
         {
            timer.Stop();
            DoSearch(text.Text);
         });
      }

      void text_TextChanged(object sender, EventArgs e)
      {
         timer.Stop();

         if (text.Text.Length > 0)
            timer.Start();
         else if (!clearing)
            ClearFind();
      }

      public void ClearFind()
      {
         tree.BeginUpdate();

         if (saveTreeNodes == null)
            return;

         clearing = true;
         text.Clear();

         tree.SuspendLayout();
         tree.Nodes.Clear();

         foreach (TreeNode tn in saveTreeNodes)
            tree.Nodes.Add(tn);

         tree.ResumeLayout();
         clearing = false;

         tree.EndUpdate();
      }
      
      bool TestPriceNode(TreeNode node, String text)
      {
         Price p = node.Tag as Price;
         return (p != null && p.Name.ToUpper().Contains(text.ToUpper()));
      }

      void TestNode(TreeNode node)
      {
         if (testNode(node, text.Text))
         {
            TreeNode newNode = (TreeNode)node.Clone();
            tree.Nodes.Add(newNode);

            newNode.Tag = node.Tag;
            newNode.Text = node.Text;
            newNode.ImageIndex = node.ImageIndex;
         }
      }

      public void BlockSearch(bool blocking)
      {
         blockSearch = blocking;
         if (blockSearch)
         {
            ClearFind();
            timer.Stop();
         }
      }

      protected virtual void SearchingNode(TreeNode node)
      {
         TestNode(node);

         foreach (TreeNode child in node.Nodes)
         {
            TestNode(child);

            if (child.Nodes.Count > 0)
               SearchingNode(child);
         }
      }

      void DoSearch(String str)
      {
         tree.BeginUpdate();

         if (inSearch || blockSearch)
            return;

         inSearch = true;

         if (saveTreeNodes == null)
         {
            saveTreeNodes = new TreeNode[tree.Nodes.Count];
            tree.Nodes.CopyTo(saveTreeNodes, 0);
         }

         tree.SuspendLayout();
         tree.Nodes.Clear();

         foreach (TreeNode tn in saveTreeNodes)
            SearchingNode(tn);

         tree.ResumeLayout();

         inSearch = false;

         tree.EndUpdate();
      }
   }
}
