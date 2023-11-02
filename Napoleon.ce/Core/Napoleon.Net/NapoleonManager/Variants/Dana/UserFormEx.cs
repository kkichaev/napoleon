using GRSoft.Network;
using System.Collections.Generic;
using System.Windows.Forms;
using GRSoft.UILib;
using System;

namespace GRSoft.NapoleonManager
{
    class UserFormEx : UserForm
    {
        DataSet<string, GoodsProjects> projects;
        DataSet<string, DanaAction> actions;

        SimpleDataSet<AgentProjects> agentProjects;
        SimpleDataSet<AgentActions> agentActions;

        CheckedListBox cbProjects, cbActions;
        bool loading = false;

        public UserFormEx(Divisions owner) :
           base(owner)
        {
            TabPage tp = new TabPage();
            tp.Text = "Проекты";
            tp.Name = "tpAgentProjects";
            tp.Location = new System.Drawing.Point(4, 23);
            tp.Padding = new System.Windows.Forms.Padding(3);
            tp.Size = new System.Drawing.Size(466, 279);
            tp.UseVisualStyleBackColor = true;

            userDetails.TabPages.Add(tp);

            cbProjects = new CheckedListBox();
            cbProjects.Location = new System.Drawing.Point(0, 0);
            cbProjects.Size = new System.Drawing.Size(20, 20);
            cbProjects.Dock = DockStyle.Fill;
            cbProjects.Sorted = true;

            cbProjects.CheckOnClick = true;
            cbProjects.ItemCheck += CbProjects_ItemCheck;

            tp.Controls.Add(cbProjects);

            agentProjects = new SimpleDataSet<AgentProjects>(AgentProjects.OBJECT_NAME, false);

            LoadActions();

            Manager m = CurrentUser.user as Manager;

            if (m != null)
            {
               if (m.HaveRight(RightTokens.Get(FormEntries.DISABLE_SAVE), RightActions.Write))
               {
                  tvAgentMatrix.Enabled = false;
                  tvScript.Enabled = false;
               }
            }
        }

        void LoadActions()
        {
            TabPage tp = new TabPage();
            tp.Text = "Акции";
            tp.Name = "tpAgentActions";
            tp.Location = new System.Drawing.Point(4, 23);
            tp.Padding = new System.Windows.Forms.Padding(3);
            tp.Size = new System.Drawing.Size(466, 279);
            tp.UseVisualStyleBackColor = true;

            userDetails.TabPages.Add(tp);

            cbActions = new CheckedListBox();
            cbActions.Location = new System.Drawing.Point(0, 0);
            cbActions.Size = new System.Drawing.Size(20, 20);
            cbActions.Dock = DockStyle.Fill;
            cbActions.Sorted = true;

            cbActions.CheckOnClick = true;
            cbActions.ItemCheck += cbActions_ItemCheck;

            tp.Controls.Add(cbActions);

            agentActions= new SimpleDataSet<AgentActions>(AgentActions.OBJECT_NAME, false);
        }

        private void CbProjects_ItemCheck(object sender, ItemCheckEventArgs e)
        {
            if (loading)
                return;

            owner.AddReplacedSet(Agent.ID, GetChecked(e));
        }

        private void cbActions_ItemCheck(object sender, ItemCheckEventArgs e)
        {
            if (loading)
                return;

            owner.AddReplacedSet(Agent.ID, GetCheckedActions(e));
        }

        private IDataSet GetCheckedActions(ItemCheckEventArgs e)
        {
            DanaAction rmvd = null;
            if (e.NewValue == CheckState.Unchecked)
                rmvd = cbActions.Items[e.Index] as DanaAction;

            SimpleDataSet<AgentActions> ret = new SimpleDataSet<AgentActions>(AgentActions.OBJECT_NAME, false);
            foreach (DanaAction gp in cbActions.CheckedItems)
            {
                if (gp == rmvd) continue;

                AgentActions ap = new AgentActions();
                ap.userid = Agent.ID;
                ap.actionid = gp.id;
                ret.Add(ap);
            }

            if (e.NewValue == CheckState.Checked)
            {
                AgentActions ap = new AgentActions();
                ap.userid = Agent.ID;
                ap.actionid = (cbActions.Items[e.Index] as DanaAction).id;
                ret.Add(ap);
            }
            return ret;
        }

        private IDataSet GetChecked(ItemCheckEventArgs e)
        {
            GoodsProjects rmvd = null;
            if (e.NewValue == CheckState.Unchecked)
                rmvd = cbProjects.Items[e.Index] as GoodsProjects;

            SimpleDataSet<AgentProjects> ret = new SimpleDataSet<AgentProjects>(AgentProjects.OBJECT_NAME, false);
            foreach(GoodsProjects gp in cbProjects.CheckedItems)
            {
                if (gp == rmvd) continue;

                AgentProjects ap = new AgentProjects();
                ap.userid = Agent.ID;
                ap.id = gp.id;
                ret.Add(ap);
            }

            if(e.NewValue == CheckState.Checked)
            {
                AgentProjects ap = new AgentProjects();
                ap.userid = Agent.ID;
                ap.id = (cbProjects.Items[e.Index] as GoodsProjects).id;
                ret.Add(ap);
            }
            return ret;
        }

        protected override void BeforeUpdateData(string userid, List<IDataSet> updSets)
        {
            projects = ((DivisionsEx)owner).projects;
            actions = ((DivisionsEx)owner).actions;

            base.BeforeUpdateData(userid, updSets);

            string uidFilter = string.Format("\"userid\" = '{0}'", userid);
            agentProjects.Filter = uidFilter;
            agentActions.Filter = uidFilter;

            updSets.Add(agentProjects);
            updSets.Add(agentActions);

            loading = true;
        }

        protected override void AfterControlFilled()
        {
            base.AfterControlFilled();

            cbProjects.Items.Clear();
            foreach (GoodsProjects gp in projects.Data)
                cbProjects.Items.Add(gp);

            foreach (AgentProjects ap in agentProjects.Data)
            {
                if (ap.project == null) continue;
                int idx = cbProjects.Items.IndexOf(ap.project);
                if(idx >= 0)
                    cbProjects.SetItemChecked(idx, true);
            }

            cbActions.Items.Clear();
            foreach (DanaAction da in actions.Data)
                cbActions.Items.Add(da);

            foreach(AgentActions aa in agentActions.Data)
            {
                if (aa.action == null) continue;
                int idx = cbActions.Items.IndexOf(aa.action);
                if (idx >= 0)
                    cbActions.SetItemChecked(idx, true);
            }

            loading = false;
        }
    }
}
