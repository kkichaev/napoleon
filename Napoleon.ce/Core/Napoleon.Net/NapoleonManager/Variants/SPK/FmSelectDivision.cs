using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Threading;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmSelectDivision : Form
   {
      private System.Threading.Timer textWait = null;
      List<int> selected;
      bool loading = false;

      public FmSelectDivision()
      {
         InitializeComponent();
      }

      public List<Division> GetSelected(TreeNode root, List<Division> list)
      {
         List<Division> ret = list ?? new List<Division>();

         if (root.Checked)
         {
            Division o = root.Tag as Division;
            if (o != null)
               ret.Add(o);
         }

         foreach (TreeNode ch in root.Nodes)
            GetSelected(ch, ret);

         return ret;
      }

      void SetSelected(List<Division> divs)
      {
         this.selected = new List<int>();
         divs.ForEach(x => this.selected.Add(x.id));
      }

      void DoLoadData()
      {
         treeView.BeginUpdate();
         treeView.Nodes.Clear();

         Manager m = CurrentUser.user as Manager;

         if (m != null)
         {
            Division d = m.Division;
            AddDivisionNode(d, treeView.Nodes);

         }

         treeView.EndUpdate();
      }

      void AddDivisionNode(Division parent, TreeNodeCollection tree)
      {
         TreeNode node = new TreeNode();
         node.Text = parent.Name;
         node.Tag = parent;
         node.Checked = selected.Contains(parent.id);

         tree.Add(node);

         foreach (Division child in parent.Childs)
            AddDivisionNode(child, node.Nodes);
      }

      private bool IsAllChecked(TreeNodeCollection nodes)
      {
         foreach (TreeNode tn in nodes)
            if (!tn.Checked)
               return false;

         return (nodes.Count > 0);
      }

      int NodeCmp(TreeNode l, TreeNode r)
      {
         return l.Text.CompareTo(r.Text);
      }

      public static bool DoSelect(List<Division> sel)
      {
         bool res = false;
             
         FmSelectDivision form = new FmSelectDivision();
         form.SetSelected(sel);

         if (form.ShowDialog() == DialogResult.OK && form.treeView.Nodes.Count > 0)
         {
            sel.Clear();
            sel.AddRange(form.GetSelected(form.treeView.Nodes[0], null));
            res = true;
         }

         return res;
      }

      private void tsbOK_Click(object sender, EventArgs e)
      {
         DialogResult = System.Windows.Forms.DialogResult.OK;
         Close();
      }

      private void tsbCancel_Click(object sender, EventArgs e)
      {
         DialogResult = System.Windows.Forms.DialogResult.Cancel;
         Close();
      }

      void CheckNodes(TreeNodeCollection nodes, bool check)
      {
         foreach (TreeNode node in nodes)
         {
            node.Checked = check;
            if (node.Nodes.Count > 0)
               CheckNodes(node.Nodes, check);
         }
      }

      private void treeView_AfterCheck(object sender, TreeViewEventArgs e)
      {
         if(!loading)
            CheckNodes(e.Node.Nodes, e.Node.Checked);
      }

      private void btnSelectAll_Click_1(object sender, EventArgs e)
      {
         CheckNodes(treeView.Nodes, true);
      }

      private void btnReset_Click_1(object sender, EventArgs e)
      {
         CheckNodes(treeView.Nodes, false);
      }

      private void FmSelectDivision_Load(object sender, EventArgs e)
      {
         DoLoadData();
      }
   }
}
