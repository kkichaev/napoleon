using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Threading;
using System.Collections;
using System.Reflection;

namespace GRSoft.NapoleonManager
{
   public partial class FmAgentPlans : Form
   {
      IshimPlan plan = new IshimPlan();
      List<AgentPlan> agentPlans = new List<AgentPlan>();

      Dictionary<AgentEx, AgentPlan> changedPlans = new Dictionary<AgentEx, AgentPlan>();

      SimpleDataSet<IshimPlan> rcvPlan;
      SimpleDataSet<AgentPlan> rcvAgentPlans;

      DateTime lastPlanDate;

      public FmAgentPlans()
      {
         InitializeComponent();
         dgvPlans.AutoGenerateColumns = false;
         lastPlanDate = new DateTime(DateTime.Now.Year, DateTime.Now.Month, 1);
         dtPlanDate.Value = lastPlanDate;

         dtPlanDate.CloseUp += dtPlanDate_ValueChanged;
         dtPlanDate.LostFocus += dtPlanDate_ValueChanged;
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();

      }

      protected override void OnClosing(CancelEventArgs e)
      {
         if (!CheckChanges())
            return;

         base.OnClosing(e);
      }

      void RefreshAgents()
      {
         AgentEx[] src = new AgentEx[cbAgents.Items.Count];
         int idx = cbAgents.SelectedIndex;
         cbAgents.Items.CopyTo(src, 0);

         cbAgents.BeginUpdate();
         cbAgents.SelectedIndexChanged -= cbAgents_SelectedIndexChanged;
         cbAgents.Items.Clear();
         cbAgents.Items.AddRange(src);
         cbAgents.SelectedIndex = idx;
         cbAgents.SelectedIndexChanged += cbAgents_SelectedIndexChanged;
         cbAgents.EndUpdate();
      }

      public void SetDirty()
      {
         tbSave.Enabled = true;
         dtPlanDate.Enabled = false;

         AgentEx a = cbAgents.SelectedItem as AgentEx;
         changedPlans[a] = GeteAgentPlan(a);

         RefreshAgents();
      }

      void ClearDirty()
      {
         tbSave.Enabled = false;
         dtPlanDate.Enabled = true;
         changedPlans.Clear();
         RefreshAgents();
      }

      void RefreshData()
      {
         List<IDataSet> upd = new List<IDataSet>();
         DateTime planDate = dtPlanDate.Value;

         string filter = string.Format("begin=ToDate('01.{0:MM/yyyy}')", planDate);
         rcvPlan = new SimpleDataSet<IshimPlan>(IshimPlan.OBJECT_NAME, false);
         rcvAgentPlans = new SimpleDataSet<AgentPlan>(AgentPlan.OBJECT_NAME, false);

         rcvPlan.Filter = filter;
         rcvAgentPlans.Filter = filter;
         
         upd.Add(rcvPlan);
         upd.Add(rcvAgentPlans);

         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      void DoLoadData()
      {
         if(cbDivisions.Items.Count == 0)
         {
            Manager m = CurrentUser.user as Manager;
            if (m == null)
               return;

            foreach(Division d in m.AllDivisions)
            {
               cbDivisions.Items.Add(d);
            }
            if(cbDivisions.Items.Count > 0)
            {
               cbDivisions.SelectedIndex = 0;
            }
         }

         if(rcvPlan.Count == 0 && !plan.Empty)
         {
            DialogResult r = MessageBox.Show("Нет плана на выбранную дату. Скопировать текущий?", "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question);
            if( r == DialogResult.No)
            {
               SetReceivedPlan();
            }
            else
            {
               SavePlan();
               SetDirty();
            }
         } else
         {
            SetReceivedPlan();
         }
      }

      private void SavePlan()
      {
         Dictionary<string, string> idcnv = new Dictionary<string, string>();

         SimpleDataSet<IshimPlan> wr = new SimpleDataSet<IshimPlan>(IshimPlan.OBJECT_NAME, false);
         List<IDataSet> wrs = new List<IDataSet>();
         wrs.Add(wr);
         plan.begin = PlanDate;
         foreach(IshimPlan.PlanItem pi in plan.plans)
         {
            string newId = Guid.NewGuid().ToString().Replace("-", "");
            idcnv[pi.id] = newId;
            pi.id = newId;
         }
         wr.Add(plan);

         SimpleDataSet<AgentPlan> ap = PrepareToWrite(agentPlans, idcnv);
         if(ap.Count > 0)
         {
            wrs.Add(ap);
         }

         if(DataModule.UpdateDataSet(wrs, null, null, Config.GetConfig().GetConnection()))
         {
            ClearDirty();
         }
      }

      void SetReceivedPlan()
      {
         plan = new IshimPlan();
         foreach (IshimPlan ip in rcvPlan.Data)
         {
            plan = ip;
            break;
         }

         agentPlans.Clear();
         foreach(AgentPlan ap in rcvAgentPlans.Data)
         {
            agentPlans.Add(ap);
         }

         ClearDirty();
         RefreshPlan();
      }

      bool CheckChanges()
      {
         if (!tbSave.Enabled)
            return true;

         DialogResult dr = MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
         if (dr == DialogResult.No)
            return true;
         if (dr == DialogResult.Cancel)
            return false;

         return SaveChanges(false);
      }

      SimpleDataSet<AgentPlan> PrepareToWrite(IEnumerable<AgentPlan> plans, Dictionary<string, string> idcnv = null)
      {
         DateTime planDate = PlanDate;
         SimpleDataSet<AgentPlan> aps = new SimpleDataSet<AgentPlan>(AgentPlan.OBJECT_NAME, false);
         foreach (AgentPlan api in plans)
         {
            AgentPlan dst = new AgentPlan();
            dst.begin = planDate;
            dst.userid = api.userid;
            foreach (AgentPlan.Item pi in api.items)
            {
               if (idcnv != null)
                  pi.id = idcnv[pi.id];
               if (!pi.Empty)
                  dst.items.Add(pi);
            }
            if (dst.items.Count > 0)
               aps.Add(dst);
         }

         return aps;
      }

      bool SaveChanges(bool showDialog)
      {
         DateTime planDate = PlanDate;
         if(!dgvPlans.EndEdit())
         {
            dgvPlans.CancelEdit();
         }
         //if (!dgvPlans.CommitEdit(DataGridViewDataErrorContexts.Commit))
         //{
         //}

         SimpleDataSet<AgentPlan> aps = PrepareToWrite(changedPlans.Values);
         List<IDataSet> wr = new List<IDataSet>();
         wr.Add(aps);

         bool ret = DataModule.UpdateDataSet(wr, null, null, Config.GetConfig().GetConnection());

         if (showDialog)
         {
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");
         }

         if (ret)
            ClearDirty();
         return ret;
      }

      private void tbSave_Click(object sender, EventArgs e)
      {
         SaveChanges(true);
      }

      private void tbRefresh_Click(object sender, EventArgs e)
      {
         if (!CheckChanges())
            return;

         RefreshData(); 
      }

      private void tsbEditPlan_Click(object sender, EventArgs e)
      {
         DateTime pd = dtPlanDate.Value;
         FmPlanEditor form = new FmPlanEditor();
         form.PlanDate = pd;
         if(form.ShowDialog() == DialogResult.OK)
         {
            plan = form.Plan;
            RefreshPlan();
         }
      }

      public bool IsPlanChanged(AgentEx a) { return changedPlans.ContainsKey(a); }

      private void cbDivisions_SelectedIndexChanged(object sender, EventArgs e)
      {
         Division d = cbDivisions.SelectedItem as Division;
         if(d != null)
         {
            List<AgentEx> src = new List<AgentEx>();
            foreach(Division.DivisionAgent da in d.agents)
            {
               if (da.agent != null)
               {
                  AgentEx a = new AgentEx(da.agent, this);
                  src.Add(a);
               }
            }
            src.Sort();
            cbAgents.Items.Clear();
            cbAgents.Items.AddRange(src.ToArray());
            if (cbAgents.Items.Count > 0)
               cbAgents.SelectedIndex = 0;
         }
      }

      private void cbAgents_SelectedIndexChanged(object sender, EventArgs e)
      {
         RefreshPlan();
      }

      AgentPlan GeteAgentPlan(AgentEx a)
      {
         AgentPlan cplan = null;
         foreach (AgentPlan ap in agentPlans)
         {
            if (ap.userid == a.ID)
            {
               cplan = ap;
               break;
            }
         }
         if (cplan == null)
         {
            cplan = new AgentPlan();
            cplan.begin = PlanDate;
            cplan.userid = a.ID;
            agentPlans.Add(cplan);
         }

         return cplan;
      }

      DateTime PlanDate { get { return new DateTime(dtPlanDate.Value.Year, dtPlanDate.Value.Month, 1); } }

      void RefreshPlan()
      {
         List<DataItem> src = new List<DataItem>();
         AgentEx a = cbAgents.SelectedItem as AgentEx;

         AgentPlan cplan = GeteAgentPlan(a);

         Dictionary<string, AgentPlan.Item> dict = new Dictionary<string, AgentPlan.Item>();
         foreach(AgentPlan.Item pi in cplan.items)
         {
            dict[pi.id] = pi;
         }

         plan.plans.Sort();
         foreach(IshimPlan.PlanItem cpi in plan.plans)
         {
            DataItem di = new DataItem();
            di.owner = this;
            di.plan = cpi;

            AgentPlan.Item api;
            if(!dict.TryGetValue(cpi.id, out api))
            {
               api = new AgentPlan.Item();
               api.id = cpi.id;
               cplan.items.Add(api);
            }
            else
            {
               dict.Remove(cpi.id);
            }
            di.agent = api;

            src.Add(di);
         }

         foreach(KeyValuePair<string, AgentPlan.Item> kv in dict)
         {
            cplan.items.Remove(kv.Value);
         }

         dgvPlans.DataSource = src;
      }

      public class AgentEx : IComparable<AgentEx>
      {
         FmAgentPlans owner;
         Agent agent;

         public AgentEx(Agent a, FmAgentPlans owner) { this.agent = a; this.owner = owner; }

         public override string ToString()
         {
            return owner.IsPlanChanged(this) ? "* " + agent.name : agent.name;
         }

         public int CompareTo(AgentEx other)
         {
            return agent.CompareTo(other.agent);
         }

         public string ID {  get { return agent.id; } }
      }
       
      class DataItem
      {
         public FmAgentPlans owner;

         public IshimPlan.PlanItem plan;
         public AgentPlan.Item agent;

         public string Name { get { return plan.name; } }
         public double Weight { get { return agent.weight; } set { agent.weight = value; owner.SetDirty(); } }
         public int AKB { get { return agent.akb; } set { agent.akb = value; owner.SetDirty(); } }
      }

      private void dtPlanDate_ValueChanged(object sender, EventArgs e)
      {
         if (lastPlanDate != PlanDate)
         {
            lastPlanDate = PlanDate;
            RefreshData();
         }
      }

      private void dgvPlans_DataError(object sender, DataGridViewDataErrorEventArgs e)
      {
      }
   }
}
