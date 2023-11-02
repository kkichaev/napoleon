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

      List<Org> GetSelected()
      {
         List<Org> ret = new List<Org>();

         foreach(TreeNode tn in treeView.Nodes)
         {
            foreach(TreeNode ch in tn.Nodes)
            {
               if(ch.Checked)
               {
                  Org o = ch.Tag as Org;
                  if (o != null)
                     ret.Add(o);
               }
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
         base.OnLoad(e);

         List<IDataSet> upd = new List<IDataSet>();
         foreach (Agent a in (CurrentUser.user as Manager).GetAgents().Data)
         {
            DataSet<string, Org> orgs = DataModule.GetUserDataSet(a.id, Org.OBJECT_NAME, typeof(DataSet<string, Org>), true) as DataSet<string, Org>;
            if (orgs.Count == 0)
               upd.Add(orgs);
         }

         if(upd.Count > 0)
            FmWait.StdDataRefresh(this, upd, DoLoadData, null);
         else
         {
            DoLoadData();
         }
      }

      void DoLoadData()
      {
         loading = true;

         Dictionary<string, bool> loadedOrgs = new Dictionary<string, bool>();
         Dictionary<string, TreeNode> nodes = new Dictionary<string, TreeNode>();
         foreach (Agent a in (CurrentUser.user as Manager).GetAgents().Data)
         {
            DataSet<string, Org> orgs = DataModule.GetUserDataSet(a.id, Org.OBJECT_NAME, typeof(DataSet<string, Org>), true) as DataSet<string, Org>;
            foreach (Org o in orgs.Data)
            {
               if (loadedOrgs.ContainsKey(o.id))
                  continue;
               loadedOrgs[o.id] = true;

               TreeNode orgNode = null;
               if (nodes.ContainsKey(o.ido))
                  orgNode = nodes[o.ido];
               else
               {
                  orgNode = new TreeNode();
                  orgNode.Text = o.name;
                  nodes[o.ido] = orgNode;
               }

               TreeNode chNode = new TreeNode(o.address);
               chNode.Tag = o;
               chNode.Checked = (selected != null && selected.Contains(o.id));
               orgNode.Nodes.Add(chNode);
            }
         }

         orgNodes = new List<TreeNode>(nodes.Values);
         orgNodes.Sort(NodeCmp);
         foreach (TreeNode tn in orgNodes)
            tn.Checked = IsAllChecked(tn.Nodes);

         loading = false;
         treeView.Nodes.Clear();
         orgNodes.ForEach(x => treeView.Nodes.Add(x));
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

      public static List<Org> DoSelect(List<Org> selected)
      {
         FmSelectOrgs form = new FmSelectOrgs();
         form.SetSelected(selected);

         if(form.ShowDialog() == DialogResult.OK)
            return form.GetSelected();

         return null;
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
            foreach(TreeNode ch in otn.Nodes)
            {
               if(filter.Length == 0 || ch.Text.ToUpper().Contains(filter))
               {
                  if (find == null)
                  {
                     find = new TreeNode(otn.Text);
                     newNodes.Add(find);
                  }
                  TreeNode chNode = new TreeNode(ch.Text);
                  chNode.Tag = ch.Tag;
                  find.Nodes.Add(chNode);
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
   }
}
