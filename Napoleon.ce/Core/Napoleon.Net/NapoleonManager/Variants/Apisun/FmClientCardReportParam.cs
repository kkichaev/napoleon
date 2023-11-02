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
   public partial class FmClientCardReportParam : Form
   {
      private DataSet<string, OrgEx> dsOrg;
      private DataSet<string, OrgRegion> dsOrgRegion;
      private Tree data;

      public FmClientCardReportParam()
      {
         InitializeComponent();
         dsOrgRegion = (DataSet<string, OrgRegion>)DataModule.Get(OrgRegion.OBJECT_NAME) ?? new DataSet<string, OrgRegion>(OrgRegion.OBJECT_NAME);
         dsOrgRegion.Filter = "id not null";
         dsOrg = new DataSet<string, OrgEx>(Org.COMMON_OBJECT_NAME, false);
         dsOrg.Filter = "id not null";

         data = Tree.Create(dsOrgRegion, dsOrg);
      }

      public string ID { 
         get 
         {
            string result = string.Empty;

            TreeNode node = treeView.SelectedNode;

            if (node != null && (node.Tag is OrgEx))
               result = ((OrgEx)node.Tag).id;

            return result;
         }}

      private void FmClientCardReportParam_Load(object sender, EventArgs e)
      {
         btnRefresh.PerformClick();
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsOrg);
         list.Add(dsOrgRegion);

         FmWait.StdDataRefresh(this, list, DoLoadData);
      }

      private void DoLoadData()
      {
         data = Tree.Create(dsOrgRegion, dsOrg);
         LoadTreeView(string.Empty);
      }

      private void LoadTreeView(string filter)
      {
         treeView.SuspendLayout();
         treeView.Nodes.Clear();

         MakeTreeView(treeView.Nodes, data.nodes, filter);
         treeView.Sort();
         RemoveEmptyNodes(treeView.Nodes);

         if (filter.Length > 0)
            treeView.ExpandAll();

         treeView.ResumeLayout();
      }

      private void RemoveEmptyNodes(TreeNodeCollection list)
      {
         List<TreeNode> toRemove = new List<TreeNode>();

         foreach(TreeNode n in list)
         {
            RemoveEmptyNodes(n.Nodes);

            if (!(n.Tag is Org) && n.Nodes.Count == 0)
               toRemove.Add(n);
         }

         foreach (TreeNode n in toRemove)
            list.Remove(n);
      }

      private void MakeTreeView(TreeNodeCollection parent, List<Tree.Node> list, String filter)
      {
         foreach (Tree.Node n in list)
         {
            string name = n.value.ToString();
            TreeNode tn = new TreeNode(name);
            tn.Tag = n.value;

            if(!(n.value is Org) || ((n.value is Org) && (filter.Length == 0 || name.ToUpper().Contains(filter.ToUpper()))))
               parent.Add(tn);

            MakeTreeView(tn.Nodes, n.nodes, filter);
         }
      }

      private void FmClientCardReportParam_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == System.Windows.Forms.DialogResult.OK)
         {
            TreeNode node = treeView.SelectedNode;

            if (node == null || !(node.Tag is OrgEx))
            {
               MessageBox.Show("Выберите организацию для отчета!");
               e.Cancel = true;
            }
         }
      }

      System.Threading.Timer textWait = null;

      void TimePassed(object o)
      {
         try
         {
            Mutex m = new Mutex(false, "FmClientCardReportParam");
            if (m.WaitOne(0))
               Invoke(new InvokeParamHandler(
                  delegate(object param)
                  {
                     DoSearch((string)param);
                  }), new object[] { o });
            m.ReleaseMutex();
         }
         catch (Exception)
         {
         }
      }

      private void DoSearch(string p)
      {
         LoadTreeView(tbFind.Text.Trim());
      }

      void tbFind_TextChanged(object sender, EventArgs e)
      {
         if (textWait != null)
            textWait.Dispose();
         textWait = new System.Threading.Timer(new TimerCallback(TimePassed), tbFind.Text, 500, 0);
      }

      private void btnClear_Click(object sender, EventArgs e)
      {
         tbFind.Text = string.Empty;
      }
   }
}
