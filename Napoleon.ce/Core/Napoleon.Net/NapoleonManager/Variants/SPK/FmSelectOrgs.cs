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
   public partial class FmSelectOrgs : Form
   {
      private System.Threading.Timer textWait = null;
      List<String> selected;
      List<TreeNode> orgNodes;
      bool loading = false;

      public FmSelectOrgs()
      {
         InitializeComponent();
      }

      public List<Org> GetSelected()
      {
         List<Org> ret = new List<Org>();

         foreach(TreeNode tn in treeView.Nodes)
         {
            if(tn.Checked)
            {
               Org o = tn.Tag as Org;
               if (o != null)
                  ret.Add(o);
            }
         }

         return ret;
      }

      void SetSelected(List<Org> selected)
      {
         this.selected = new List<string>();
         selected.ForEach(x => this.selected.Add(x.id));
      }

      protected override void OnLoad(EventArgs e)
      {
         loading = true;

         Dictionary<string, bool> loadedOrgs = new Dictionary<string, bool>();
         Dictionary<string, TreeNode> nodes = new Dictionary<string, TreeNode>();

         foreach (Org o in DataSet)
         {
            if (loadedOrgs.ContainsKey(o.id))
               continue;
            loadedOrgs[o.id] = true;

            TreeNode orgNode = null;
            if (nodes.ContainsKey(o.id))
               orgNode = nodes[o.id];
            else
            {
               orgNode = new TreeNode();
               orgNode.Tag = o;
               orgNode.Text = string.Format("{0} ({1})", o.name, o.address);
               nodes[o.id] = orgNode;
            }
         }

         orgNodes = new List<TreeNode>(nodes.Values);
         orgNodes.Sort(NodeCmp);

         foreach (TreeNode tn in orgNodes)
            tn.Checked = IsAllChecked(tn.Nodes);

         loading = false;

         treeView.BeginUpdate();
         treeView.Nodes.Clear();
         orgNodes.ForEach(x => treeView.Nodes.Add(x));
         treeView.EndUpdate();
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

      public static bool DoSelect(List<Org> dataset, List<Org> selected)
      {
         bool res = false;

         FmSelectOrgs form = new FmSelectOrgs();
         form.DataSet = dataset;
         form.SetSelected(selected);
         

         if (form.ShowDialog() == DialogResult.OK && form.treeView.Nodes.Count > 0)
         {
            selected.Clear();
            selected.AddRange(form.GetSelected());
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

      private void tbSearch_TextChanged(object sender, EventArgs e)
      {
         if (textWait != null)
            textWait.Dispose();
         textWait = new System.Threading.Timer(new TimerCallback(TimePassed), ((ToolStripTextBox)sender).Text, 500, 0);

      }

      private void btnClear_Click(object sender, EventArgs e)
      {
         tbSearch.Text = string.Empty;
      }

      private void DoSearch(string filter)
      {
         List<TreeNode> newNodes = new List<TreeNode>();

         filter = filter.ToUpper();
         foreach (TreeNode otn in orgNodes)
         {
            TreeNode find = null;
            
            if(filter.Length == 0 || otn.Text.ToUpper().Contains(filter))
            {
               if (find == null)
               {
                  find = new TreeNode(otn.Text);
                  find.Tag = otn.Tag;
                  newNodes.Add(find);
               }
            }
         }

         treeView.BeginUpdate();
         treeView.Nodes.Clear();
         newNodes.ForEach(x => { treeView.Nodes.Add(x); });
         treeView.EndUpdate();
      }

      void TimePassed(object o)
      {
         try
         {
            Mutex m = new Mutex(false, "FMSelOrgMutex");
            if (m.WaitOne(0))
               treeView.Invoke(new InvokeParamHandler(delegate(object param) { DoSearch((string)param); }), new object[] { o });
            m.ReleaseMutex();
         }
         catch (Exception) { }
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

      private void btnSelectAll_Click(object sender, EventArgs e)
      {
         CheckNodes(treeView.Nodes, true);
      }

      private void btnReset_Click(object sender, EventArgs e)
      {
         CheckNodes(treeView.Nodes, false);
      }

      public string SearchText { get { return tbSearch.Text; } }

      public List<Org> DataSet { get; set; }
   }
}
