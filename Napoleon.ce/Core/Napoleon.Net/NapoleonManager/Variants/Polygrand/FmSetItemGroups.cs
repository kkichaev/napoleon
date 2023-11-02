using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmSetItemGroups : Form
   {
      SimpleDataSet<ItemGroup> groups;
      SimpleDataSet<ItemGroupsAssign> groupAssign = new SimpleDataSet<ItemGroupsAssign>(ItemGroupsAssign.OBJECT_NAME, false);

      public FmSetItemGroups()
      {
         InitializeComponent();
      }

      public SimpleDataSet<ItemGroup> Groups { 
         set {
            groups = value;
         } 
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         base.OnClosing(e);
         if (!CheckChanges())
            e.Cancel = true;
      }

      void RefreshData()
      {
         List<IDataSet> updSets = new List<IDataSet>();
         updSets.Add(groupAssign);
         FmWait.StdDataRefresh(this, updSets, DoLoadData, null);
      }

      void AddChilds(TreeNode parent, Dictionary<string, bool> assigns)
      {
         foreach(ItemGroup i in groups.Values)
         {
            TreeNode tn = new TreeNode(i.name);
            tn.Tag = i;
            tn.Checked = (assigns == null) ? false : assigns.ContainsKey(i.id);

            parent.Nodes.Add(tn);
         }
      }

      void DoLoadData()
      {
         Manager m = CurrentUser.user as Manager;
         tvAgents.Nodes.Clear();
         List<TreeNode> nodes = new List<TreeNode>();
         Dictionary<string, Dictionary<string, bool>> assigns = new Dictionary<string, Dictionary<string, bool>>();

         foreach(ItemGroupsAssign iga in groupAssign.Data)
         {
            Dictionary<string, bool> items = null;
            if (assigns.ContainsKey(iga.userid))
               items = assigns[iga.userid];
            else
            {
               items = new Dictionary<string, bool>();
               assigns.Add(iga.userid, items);
            }
            items[iga.id] = true;
         }

         foreach(Agent a in m.GetAgents().Data)
         {
            TreeNode agNode = new TreeNode(a.Name);
            agNode.Tag = a;
            nodes.Add(agNode);
            AddChilds(agNode, assigns.ContainsKey(a.id) ? assigns[a.id] : null);
         }
         nodes.Sort(SortNodes);
         nodes.ForEach(x => { tvAgents.Nodes.Add(x); });
      }

      int SortNodes(TreeNode a, TreeNode b) { return a.Text.CompareTo(b.Text); }

      private void tvAgents_AfterCheck(object sender, TreeViewEventArgs e)
      {
         Agent a = e.Node.Tag as Agent;
         if( a != null )
         {
            foreach (TreeNode tn in e.Node.Nodes)
               tn.Checked = e.Node.Checked;
         }
         tsbSave.Enabled = true;
      }

      bool CheckChanges()
      {
         if (!tsbSave.Enabled)
            return true;

         DialogResult dr = MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
         if (dr == DialogResult.No)
            return true;
         if (dr == DialogResult.Cancel)
            return false;

         return SaveChanges(false);
      }

      private bool SaveChanges(bool showDialog)
      {
         List<ReplacedSet> rpl = new List<ReplacedSet>();
         foreach(TreeNode tn in tvAgents.Nodes)
         {
            Agent a = tn.Tag as Agent;
            SimpleDataSet<ItemGroupsAssign> addSet = new SimpleDataSet<ItemGroupsAssign>(ItemGroupsAssign.OBJECT_NAME, false);
            ReplacedSet rs = new ReplacedSet(a.id, addSet);
            rpl.Add(rs);
            foreach(TreeNode child in tn.Nodes)
            {
               if( child.Checked)
               {
                  ItemGroup ig = child.Tag as ItemGroup;
                  ItemGroupsAssign iga = new ItemGroupsAssign();
                  iga.id = ig.id;
                  iga.userid = a.id;
                  addSet.Add(iga);
               }
            }
         }

         bool ret = DataModule.UpdateDataSet(null, null, rpl, Config.GetConfig().GetConnection());
         if (showDialog)
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");

         return ret;
      }

      private void tsbSave_Click(object sender, EventArgs e)
      {
         tsbSave.Enabled = !SaveChanges(true);
      }
   }
}
