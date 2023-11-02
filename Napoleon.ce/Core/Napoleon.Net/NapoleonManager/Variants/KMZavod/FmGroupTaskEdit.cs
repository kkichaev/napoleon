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
   public partial class FmGroupTaskEdit : Form
   {
      class TaskPool
      {
         //userid - orgid
         private Dictionary<string, Dictionary<string, GroupTask>> data = new Dictionary<string, Dictionary<string, GroupTask>>();
         private List<string> orgids = new List<string>();

         public static TaskPool Parse(List<GroupTask> input)
         {
            TaskPool res = new TaskPool();
            if (input != null)
               res.LoaData(input);

            return res;
         }

         private void LoaData(List<GroupTask> input)
         {
            data.Clear();
            orgids.Clear();

            foreach(GroupTask t in input)
            {
               if (!orgids.Contains(t.orgid))
                  orgids.Add(t.orgid);

               if (!data.ContainsKey(t.userid))
                  data[t.userid] = new Dictionary<string, GroupTask>();

               data[t.userid][t.orgid] = t;
            }
         }

         internal GroupTask Find(string userid, string orgid)
         {
            GroupTask res = null;

            if (data.ContainsKey(userid) && data[userid].ContainsKey(orgid))
               res = data[userid][orgid];

            return res;
         }

         public bool IsAgent(string userid)
         {
            return data.ContainsKey(userid);
         }

         public bool IsOrg(string orgid)
         {
            return orgids.Contains(orgid);
         }
      }

      private TaskPool data;
      private System.Object lockThis = new System.Object();

      public static List<GroupTask> EditTask(List<GroupTask> input)
      {
         List<GroupTask> result = new List<GroupTask>();
         FmGroupTaskEdit form = new FmGroupTaskEdit();
         form.data = TaskPool.Parse(input);
         string groupid = GRSoft.Network.DataObject.GenId();

         if (input != null && input.Count > 0)
         {
            GroupTask task = input[0];
            form.dtpStart.Value = task.start;
            form.dtpFinish.Value = task.finish;
            form.tbTask.Text = task.text;
            groupid = task.groupid;
         }

         if (form.ShowDialog() == DialogResult.OK)
         {
            foreach (object a in form.agents.CheckedItems)
            {
               foreach (object o in form.orgs.CheckedItems)
               {
                  string userid = ((GRSoft.NapoleonManager.Division.DivisionAgent)a).id;
                  string orgid = ((Org)o).id;

                  GroupTask task = form.data.Find(userid, orgid);

                  if (task == null)
                  {
                     task = new GroupTask();
                     task.groupid = groupid;
                     task.id = GRSoft.Network.DataObject.GenId();
                     task.userid = userid;
                     task.orgid = orgid;
                     task.created = DateTime.Now;
                     task.manager = CurrentUser.user.User.id;
                  }

                  task.start = form.dtpStart.Value.Date;
                  task.finish = form.dtpFinish.Value.Date;
                  task.text = form.tbTask.Text;

                  result.Add(task);
               }
            }
         }
         else
            result = null;


         return result;
      }

      public FmGroupTaskEdit()
      {
         InitializeComponent();
      }

      private void FmGroupTaskEdit_Load(object sender, EventArgs e)
      {
         Manager m = (CurrentUser.user as Manager);

         if (m != null)
         {
            foreach (Division v in m.AllDivisions)
               cbDiv.Items.Add(v);

            if (cbDiv.Items.Count > 0)
               cbDiv.SelectedIndex = 0;
         }
      }

      private List<Org> orgsData = new List<Org>();

      private void cbDiv_SelectedIndexChanged(object sender, EventArgs e)
      {
         Division d = ((ComboBox)sender).SelectedItem as Division;

         if (d != null)
         {
            agents.Items.Clear();
            orgs.Items.Clear();
            orgsData.Clear();

            Dictionary<string, Org> m = new Dictionary<string, Org>();

            foreach (Division.DivisionAgent a in d.GetAllAgents())
               if (a.agent != null && agents.Items.Contains(a.agent) == false)
               {
                  int idx = agents.Items.Add(a);
                  agents.SetItemChecked(idx, this.data.IsAgent(a.id));

                  DataSet<string, Org> ds = (DataSet<string, Org>)DataModule.GetUserDataSet(a.id, Org.OBJECT_NAME, typeof(DataSet<string, Org>), true);

                  foreach (Org o in ds.Values)
                     if (!m.ContainsKey(o.id))
                        m[o.id] = o;
               }


            foreach (Org o in m.Values)
            {
               int idx = orgs.Items.Add(o);
               orgs.SetItemChecked(idx, this.data.IsOrg(o.id));
               orgsData.Add(o);
            }

            agents.Sorted = true;
            orgs.Sorted = true;
         }
      }

      private void timer1_Tick(object sender, EventArgs e)
      {
         DoSearch();
      }

      void DoSearch()
      {
         lock (lockThis)
         {
            timer1.Stop();
            orgs.SuspendLayout();
            orgs.Items.Clear();

            foreach (Org o in orgsData)
            {
               if (o.Name.ToUpper().Contains(edFind.Text.ToUpper()))
               {
                  int idx = orgs.Items.Add(o);
                  orgs.SetItemChecked(idx, this.data.IsOrg(o.id));
               }
            }

            orgs.ResumeLayout();
         }
      }

      private void edFind_TextChanged(object sender, EventArgs e)
      {
         timer1.Stop();
         timer1.Start();
      }

      private void btnCheckAllAgents_Click(object sender, EventArgs e)
      {
         for(int i  = 0; i < agents.Items.Count; i++)
            agents.SetItemChecked(i, true);
      }

      private void btnUncheckAllAgents_Click(object sender, EventArgs e)
      {
         for (int i = 0; i < agents.Items.Count; i++)
            agents.SetItemChecked(i, false);
      }

      private void btnCheckAllOrgs_Click(object sender, EventArgs e)
      {
         for (int i = 0; i < orgs.Items.Count; i++)
            orgs.SetItemChecked(i, true);
      }

      private void btnUncheckAllOrgs_Click(object sender, EventArgs e)
      {
         for (int i = 0; i < orgs.Items.Count; i++)
            orgs.SetItemChecked(i, false);
      }
   }
}
