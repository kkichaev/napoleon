using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public partial class FmAgentOrg : Form
   {
      DataSet<string, Agent> dsAgent;
      DataSet<string, OrgEx> dsCommonOrs;
      DataSet<string, OrgRegion> dsOrgRegion;
      DataSet<string, AgentOrg> dsAgentOrg;

      public FmAgentOrg()
      {
         InitializeComponent();
         dsAgent = (DataSet<string, Agent>)DataModule.Get(Agent.OBJECT_NAME);
         IDataSet ds = DataModule.Get(Org.COMMON_OBJECT_NAME);
         dsCommonOrs = new DataSet<string, OrgEx>(Org.COMMON_OBJECT_NAME, false);
         dsOrgRegion = (DataSet<string, OrgRegion>)DataModule.Get(OrgRegion.OBJECT_NAME) ??
            new DataSet<string, OrgRegion>(OrgRegion.OBJECT_NAME);
         dsAgentOrg = new DataSet<string, AgentOrg>(AgentOrg.OBJECT_NAME,false);
         btnSave.Enabled = false;
      }

      private bool agentUpdate = false;
      private bool expanded = false;

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         if (btnSave.Enabled == true && MessageBox.Show("Сохранить изменения?", "Вопрос",
            MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
         {
            btnSave_Click(btnSave, EventArgs.Empty);
         }

         Agent agent = cbAgent.SelectedItem as Agent;
         agentUpdate = false;
         List<IDataSet> updSet = new List<IDataSet>();
         DataModule.SetDataRepsonceHandlers(DataProcessed, DataConnectionError);

         if (agent != null)
         {
            dsAgentOrg.Filter="userid='"+agent.id+"'";
            updSet.Add(dsCommonOrs);
            updSet.Add(dsAgentOrg);
            updSet.Add(dsOrgRegion);
         }

         if (dsAgent.Count == 0)
         {
            agentUpdate = true;
            updSet.Add(dsAgent);
         }

         if(updSet.Count > 0)
            FmWait.ShowForm(this, DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
               updSet, FmWait.ProgressIndicator));
      }

      //Окончание выборки, заполняются внутренние наборы
      void DataProcessed(System.Object setnder, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         Invoke(new EmptyParamHandler(delegate()
         {
            tvOrg.SuspendLayout();
            tvOrg.Nodes.Clear();

            List<OrgRegion> list = new List<OrgRegion>();
            list.AddRange(dsOrgRegion.Values);
            list.Sort(new Comparison<OrgRegion>(delegate(OrgRegion o1, OrgRegion o2) { return o1.name.CompareTo(o2.name); }));

            Tree data = Tree.Create(dsOrgRegion, dsCommonOrs) ;

            foreach (Tree.Node n in data.nodes)
               InsertTreeNode(tvOrg.Nodes, n);

            SortTreeRecursive(tvOrg.Nodes);
            CheckParentNode(tvOrg.Nodes);

            tvOrg.ResumeLayout();

            if (agentUpdate)
               FillAgent();
         }));
      }

      private bool CheckParentNode(TreeNodeCollection nodes)
      {
         bool result = true;

         foreach (TreeNode n in nodes)
         {
            if (n.Nodes.Count > 0 && CheckParentNode(n.Nodes))
               n.Checked = true;

            if (result && !n.Checked)
               result = false;
         }

         return result;
      }

      private void SortTreeRecursive(TreeNodeCollection nodes)
      {
      }

      private void InsertTreeNode(TreeNodeCollection nodes, Tree.Node n)
      {
         TreeNode nn = nodes.Add(((TreeData)n.value).Data[0]);
         nn.Tag = n.value;

         if (n.value is OrgEx)
            nn.Checked = dsAgentOrg.ContainsKey(((OrgEx)n.value).id);

         foreach (Tree.Node child in n.nodes)
            InsertTreeNode(nn.Nodes, child);
      }

      //Произошла ошибка в соединении
      private void DataConnectionError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         Invoke(new EmptyParamHandler(delegate
         {
            const string TITLE = "Ошибка";

            MessageBox.Show(e.Msg, TITLE, MessageBoxButtons.OK,
               MessageBoxIcon.Error);
         }));
      }

      private void FmAgentOrg_Load(object sender, EventArgs e)
      {
         FillAgent();
      }

      private void FillAgent()
      {
         if (dsAgent != null)
         {
            List<Agent> list = new List<Agent>();
            list.AddRange(dsAgent.Values);
            list.Sort(new Comparison<Agent>(delegate(Agent a1, Agent a2) { return a1.Name.CompareTo(a2.Name); }));

            cbAgent.Items.AddRange(list.ToArray());
         }
      }

      private void tvOrg_DrawNode(object sender, DrawTreeNodeEventArgs e)
      {
         if (e.Node.Tag is OrgRegion)
         {
            Font nodeFont = new Font(((TreeView)sender).Font, FontStyle.Bold);
            SizeF sz = e.Graphics.MeasureString(e.Node.Text, nodeFont);
            Rectangle bounds = e.Bounds;
            bounds.Width = (int)sz.Width + 5;
            e.Graphics.DrawString(e.Node.Text, nodeFont, Brushes.Black,
                 Rectangle.Inflate(bounds, 0, -2));
         }
         else
            e.DrawDefault = true;
      }

      private void CheckNodeRecursive(TreeNodeCollection nodes, bool ch)
      {
         foreach (TreeNode tn in nodes)
         {
            if (tn.Nodes.Count > 0)
               CheckNodeRecursive(tn.Nodes, ch);

            tn.Checked = ch;
         }
      }

      private void tvOrg_AfterCheck(object sender, TreeViewEventArgs e)
      {
         if (e.Action != TreeViewAction.Unknown)
         {
            tvOrg.BeginUpdate();
            CheckNodeRecursive(e.Node.Nodes, e.Node.Checked);

            if (e.Node.Parent != null)
               e.Node.Parent.Checked = CheckParentNode(e.Node.Parent.Nodes);

            tvOrg.EndUpdate();

            btnSave.Enabled = true;
         }
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
          Agent agent = cbAgent.SelectedItem as Agent;

          if (agent != null)
          {
             dsAgentOrg.Clear();
             CollectAgentOrg(tvOrg.Nodes, agent.id);
             List<ReplacedSet> rplSet = new List<ReplacedSet>();
             rplSet.Add(new ReplacedSet(agent.id, dsAgentOrg));

             if (!DataModule.UpdateDataSet
                (null, null, rplSet, Config.GetConfig().GetConnection(), agent.id))
                MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK,
                   MessageBoxIcon.Error);
             else
                btnSave.Enabled = false;
          }
      }

      private void CollectAgentOrg(TreeNodeCollection nodes, string userid)
      {
         foreach (TreeNode n in nodes)
         {
            if (n.Nodes.Count > 0)
               CollectAgentOrg(n.Nodes, userid);

            if (n.Tag is OrgEx && n.Checked)
            {
               OrgEx org = (OrgEx)n.Tag;
               AgentOrg ao = new AgentOrg();
               ao.id = org.id;
               ao.userid = userid;
               dsAgentOrg.Add(ao.id, ao);
            }
         }
      }

      //Искать в направлениее Direction
      private void Find(Direction dir)
      {
         expanded = false;
         ExpandNodes();
         TreeNode node = tvOrg.SelectedNode;

         if (node == null && tvOrg.Nodes.Count > 0)
            node = tvOrg.Nodes[0];

         if (node != null)
         {
            int index = node.Index;

            foreach(TreeNode n in node.Nodes)
               if (FindRecursive(n))
                  return;
         }
      }

      private bool FindRecursive(TreeNode node)
      {
         foreach (TreeNode nn in  node.Nodes)
         {
            if(nn.Nodes.Count > 0)
               if (FindRecursive(nn))
                  return true;

            if (nn.Text.ToString().ToUpper().Contains(tbFind.Text.ToUpper()))
            {
               tvOrg.SelectedNode = nn;
               return true;
            }
         }

         return node.Text.ToString().ToUpper().Contains(tbFind.Text.ToUpper());
      }

      private bool IsFindOver(Direction dir, TreeNodeCollection nodes,  ref int index)
      {
         index = Next(dir, index);

         if (dir == Direction.UP)
            return index < 0;
         else
            return index >= nodes.Count;
      }

      //Вычислить следующий индекс в соответсвии с направление поиска
      private int Next(Direction dir, int value)
      {
         if (dir == Direction.UP)
            return --value;
         else
            return ++value;
      }

      //Раскрыть дерево прайса
      private void ExpandNodes()
      {
         if (tvOrg.Nodes.Count > 0)
         {
            tvOrg.SuspendLayout();
            ExpandNodesRecursive(tvOrg.Nodes);
            tvOrg.ResumeLayout();
            expanded = !expanded;
         }
      }

      private void ExpandNodesRecursive(TreeNodeCollection nodes)
      {
         foreach (TreeNode node in nodes)
         {
            if (expanded)
               node.Collapse();
            else
               node.Expand();

            if (node.Nodes.Count > 0)
               ExpandNodesRecursive(node.Nodes);
         }
      }

      private void btnFindDown_Click(object sender, EventArgs e)
      {
         Find(Direction.DOWN);
      }

      private void btnFindUp_Click(object sender, EventArgs e)
      {
         Find(Direction.UP);
      }

      private void tbFind_KeyDown(object sender, KeyEventArgs e)
      {
         if (e.KeyCode == Keys.Enter)
            Find(Direction.DOWN);
      }

      private void FmAgentOrg_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (btnSave.Enabled == true && MessageBox.Show("Сохранить изменения?", "Вопрос",
            MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
         {
            btnSave_Click(btnSave, EventArgs.Empty);
         }
      }
   }

   public class AgentOrg : GRSoft.Network.DataObject
   {
      public readonly static string OBJECT_NAME = "AgentOrg";

      [KeyField]
      public string id = string.Empty;
      public string userid = string.Empty;
   }
}
