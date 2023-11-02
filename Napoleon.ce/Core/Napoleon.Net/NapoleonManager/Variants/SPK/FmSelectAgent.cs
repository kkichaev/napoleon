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
   public partial class FmSelectAgent : Form
   {
      private System.Threading.Timer textWait = null;
      List<String> selected;
      List<TreeNode> allNotes = new List<TreeNode>();
      bool loading = false;

      public FmSelectAgent()
      {
         InitializeComponent();
      }

      public List<Agent> GetSelected()
      {
         List<Agent> ret = new List<Agent>();

         foreach(TreeNode tn in treeView.Nodes)
         {
            if(tn.Checked)
            {
               Agent o = tn.Tag as Agent;
               if (o != null)
                  ret.Add(o);
            }
         }

         return ret;
      }

      void SetSelected(List<Agent> selected)
      {
         this.selected = new List<string>();
         selected.ForEach(x => this.selected.Add(x.id));
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);

         if (divisions != null && divisions.Count > 0)
            putAgentFromDiv();
         else
            puAllAgents();

      }

      private void puAllAgents()
      {
         Manager m = CurrentUser.user as Manager;

         if (m != null)
         {
            List<Agent> list = new List<Agent>();
            m.Division.GetAllAgents().ForEach((x) => { list.Add(x.agent); });
            putAgentList(list);
         }
      }

      private void putAgentList(List<Agent> list)
      {
         list.Sort((x, y) => { return x.name.CompareTo(y.name); });

         foreach (Agent a in list)
         {
            TreeNode n = new TreeNode();
            n.Text = a.name;
            n.Tag = a;
            n.Checked = selected.Contains(a.id);

            treeView.Nodes.Add(n);

            allNotes.Add(n);
         }
      }

      private void putAgentFromDiv()
      {
         List<Agent> list = new List<Agent>();

         if (divisions != null) 
         {
            foreach (Division d in divisions)
               foreach (Division.DivisionAgent a in d.agents)
                  list.Add(a.agent);

            putAgentList(list);
         }
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

      public static bool DoSelect(List<Division> divs, List<Agent> sel)
      {
         bool res = false;

         FmSelectAgent form = new FmSelectAgent();
         form.SetSelected(sel);
         form.divisions = divs;

         if (form.ShowDialog() == DialogResult.OK && form.treeView.Nodes.Count > 0)
         {
            sel.Clear();
            sel.AddRange(form.GetSelected());
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
         foreach (TreeNode otn in allNotes)
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
            Mutex m = new Mutex(false, "FMSelAgentMutex");
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

      public List<Division> divisions { get; set; }
   }
}
