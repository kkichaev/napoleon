using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class DivisionChiefEx : DivisionChief
   {
      private SplitContainer container;
      private TreeView tree;
      private DataSet<string, Question> dsCommonQuest = new DataSet<string, Question>(Question.OBJECT_NAME, false);
      SimpleDataSet<ManagerQuest> dsManagerQuest = new SimpleDataSet<ManagerQuest>(ManagerQuest.OBJECT_NAME);
      Dictionary<string, List<String>> mapQuest = new Dictionary<string, List<string>>();

      public DivisionChiefEx(Division division)
         : base(division)
      {
         Size = new System.Drawing.Size(800, 600);

         container = new SplitContainer();
         tree = new TreeView();

         tree.CheckBoxes = true;
         tree.AfterCheck += tree_AfterCheck;
         container.Dock = DockStyle.Fill;
         container.Panel1.Controls.Add(dgvManagers);
         container.Panel2.Controls.Add(tree);
         tree.Dock = DockStyle.Fill;
         container.SplitterDistance = 100;
         Controls.Remove(dgvManagers);
         
         Controls.Add(container);
         container.BringToFront();

         Load += DivisionChiefEx_Load;
         dgvManagers.RowEnter += dgvManagers_RowEnter;

         foreach (DivisionManager ds in dsManager.Data)
         {
            if (!mapQuest.ContainsKey(ds.login))
               mapQuest[ds.login] = new List<string>();
         }
      }

      void tree_AfterCheck(object sender, TreeViewEventArgs e)
      {
         DataGridViewRow row = dgvManagers.CurrentRow;

         if (row != null)
         {
            DivisionManager manager = row.DataBoundItem as DivisionManager;

            if (manager != null && e.Node != null && mapQuest.ContainsKey(manager.login))
            {
               Question quest = e.Node.Tag as Question;

               if (quest != null)
               {
                  if (e.Node.Checked)
                     mapQuest[manager.login].Add(quest.idquest);
                  else
                     mapQuest[manager.login].Remove(quest.idquest);
               }
            }
         }

         btnSave.Enabled = true;
      }

      void dgvManagers_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         tree.AfterCheck -= tree_AfterCheck;
         ClearChecked(e.RowIndex);
         tree.AfterCheck += tree_AfterCheck;
      }

      private void ClearChecked(int row)
      {
         tree.BeginUpdate();

         foreach (TreeNode node in tree.Nodes)
            node.Checked = false;

         DivisionManager manager = dgvManagers.Rows[row].DataBoundItem as DivisionManager;
         foreach (TreeNode node in tree.Nodes)
         {
            Question q = node.Tag as Question;

            if (q != null && mapQuest.ContainsKey(manager.login))
               node.Checked = mapQuest[manager.login].Contains(q.idquest);
            }
                        

         tree.EndUpdate();
      }

      private void DivisionChiefEx_Load(object sender, EventArgs e)
      {
         dsCommonQuest.Filter = "\"idquest\" is null or \"idquest\" is not null";
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsCommonQuest);
         upd.Add(dsManagerQuest);

         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      private void DoLoadData()
      {
         FillMapQuest();
         FillQuest();

         if (dgvManagers.Rows.Count > 0)
            dgvManagers_RowEnter(dgvManagers, new DataGridViewCellEventArgs(0,0));
      }

      private void FillMapQuest()
      {
         foreach (ManagerQuest mq in dsManagerQuest.Values)
         {
            if(!mapQuest.ContainsKey(mq.idmanager))
            {
               mapQuest[mq.idmanager] = new List<string>();
            }

            foreach(ManagerQuest.ManagerQuestItem i in mq.items)
               mapQuest[mq.idmanager].Add(i.idquest);
            
         }
      }

      private void FillQuest()
      {
         tree.BeginUpdate();

         tree.Nodes.Clear();

         List<Question> list = new List<Question>();
         list.AddRange(dsCommonQuest.Values);
         list.Sort(new Comparison<Question>(delegate(Question q1, Question q2)
         {
            return q1.Number.CompareTo(q2.Number);
         }));

         foreach (Question quest in list)
         {
            TreeNode node = new TreeNode();
            node.Tag = quest;
            node.Text = quest.Name;

            if (quest.items != null)
               foreach (QuestionItem item in quest.items)
               {
                  TreeNode n = node.Nodes.Add(item.Id);
                  n.StateImageIndex = 0;
               }

            tree.Nodes.Add(node);
         }

         tree.EndUpdate();
      }

      protected override void UpdateWriteSet(List<IDataSet> wr)
      {
         SimpleDataSet<ManagerQuest> ds = new SimpleDataSet<ManagerQuest>(ManagerQuest.OBJECT_NAME, false);

         foreach(string id in mapQuest.Keys)
         {
            ManagerQuest mq = new ManagerQuest();
            mq.idmanager = id;
            mq.items = new List<ManagerQuest.ManagerQuestItem>();

            foreach (string idquest in mapQuest[id])
            {
               ManagerQuest.ManagerQuestItem i = new ManagerQuest.ManagerQuestItem();
               i.idquest = idquest;
               mq.items.Add(i);
            }

            ds.Add(mq);
         }

         wr.Add(ds);
      }
   }
}
