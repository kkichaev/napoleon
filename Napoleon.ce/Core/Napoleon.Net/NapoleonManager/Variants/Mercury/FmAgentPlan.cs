using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using GRSoft.UILib;

namespace GRSoft.NapoleonManager
{
   public partial class FmAgentPlan : Form
   {
      private DataSet<int, Delivery> dsDelivery;
      private SimpleDataSet<AgentPlan> dsAgentPlan = new SimpleDataSet<AgentPlan>(AgentPlan.OBJECT_NAME, false);
      private Dictionary<string, PlanValue> itemPlan = new Dictionary<string, PlanValue>();
      private Dictionary<string, PlanValue> itemFact = new Dictionary<string, PlanValue>();
      private DataSet<string, Price> dsPrice = new DataSet<string, Price>(Price.OBJECT_NAME, false);

      Agent selectedAgent = null;

      public FmAgentPlan()
      {
         InitializeComponent();
         dsDelivery = (DataSet<int, Delivery>)DataModule.Get(Delivery.OBJECT_NAME) ?? new DataSet<int, Delivery>(Delivery.OBJECT_NAME, true, true);
      }

      public string COMMON_FILTER_STR = "\"{0}\" >= ToDate('{1:dd/MM/yyyy}') and \"{0}\" <= ToDate('{2:dd/MM/yyyy} 23:59:59') and \"userid\"='{3}'";

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         if (selectedAgent != null)
         {
            DataModule.SetDataRepsonceHandlers(DataProcessed, DataConnectionError);

            DateTime begin = new DateTime(dtpDate.Value.Year, dtpDate.Value.Month, 1);
            DateTime end  = new DateTime(dtpDate.Value.Year, dtpDate.Value.Month,
                DateTime.DaysInMonth(dtpDate.Value.Year, dtpDate.Value.Month));
            dsAgentPlan.Filter = string.Format("userid='{0}' and begin=ToDate('{1:dd/MM/yyyy}') and end=ToDate('{2:dd/MM/yyyy}')",
               selectedAgent.id, begin, end);
            dsDelivery.Filter = String.Format(COMMON_FILTER_STR, "created", begin, end, selectedAgent.id);
            dsPrice.Command = new ServerCommand(Commands.Impersonate(Commands.GET, selectedAgent.id), dsPrice.Name);

            List<IDataSet> updSet = new List<IDataSet>();

            updSet.Add(dsPrice);
            updSet.Add(dsAgentPlan);
            updSet.Add(dsDelivery);

            FmWait.ShowForm(this, DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
               updSet, FmWait.ProgressIndicator));
         }
      }

      void DataProcessed(System.Object setnder, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         Invoke(new EmptyParamHandler(delegate()
         {
            itemPlan.Clear();
            itemFact.Clear();

            if (dsAgentPlan.Count > 0)
            {
               AgentPlan plan = dsAgentPlan[0];

               foreach (AgentPlanItem item in plan.items)
               {
                  PlanValue val = new PlanValue(item.valueSum, item.valueWeight);
                  itemPlan[item.id] = val;
               }
            }

            if (dsDelivery.Count > 0)
            {
               foreach (Delivery dlv in dsDelivery.Data)
                  foreach (DeliveryItem item in dlv.items)
                  {
                     PlanValue pv = null;

                     if (itemFact.ContainsKey(item.id))
                        pv = itemFact[item.id];
                     else
                        pv = new PlanValue();

                     pv.qty += item.qty;
                     pv.sum  += item.sum;

                     itemFact[item.id] = pv;
                  }
            }

            CreatePriceTree();
            double dummy = 0.0;
            UpdateForChilds(tgvPrice.Nodes, out dummy, out dummy);
            btnSave.Enabled = false;
         }));
      }

      private void UpdateForChilds(TreeGridNodeCollection nodes, out double sumout, out double weightout)
      {
         sumout = 0; weightout = 0;

         foreach (TreeGridNode node in nodes)
         {
            double sum = 0;
            double weight = 0;

            if (node.HasChildren)
               UpdateForChilds(node.Nodes, out sum, out weight);

            Price p = node.Tag as Price;

            double planSum = GetFolderPlanSum(p.id);
            double planWeight = GetFolderPlanWeight(p.id);
            double factSum = sum;
            double factWeight = weight;

            DateTime begin = new DateTime(dtpDate.Value.Year, dtpDate.Value.Month, 1);
            DateTime end = new DateTime(dtpDate.Value.Year, dtpDate.Value.Month,
                   DateTime.DaysInMonth(dtpDate.Value.Year, dtpDate.Value.Month));
            DateTime now = new DateTime(DateTime.Now.Year, DateTime.Now.Month, DateTime.Now.Day);
            DateTime tommorow = now.AddDays(1);

            int wd = WorkingDay(begin, now);
            double avgSum = factSum / wd;
            double avgWeight = factWeight / wd;

            wd = WorkingDay(tommorow, end);
            double endSum = avgSum * wd;
            double apprSum = factSum + endSum;
            double endWeight = avgWeight * wd;
            double apprWeight = factWeight + endWeight;

            node.Cells[1].Value = planSum;
            node.Cells[2].Value = planWeight;
            node.Cells[3].Value = factSum;
            node.Cells[4].Value = factWeight;
            node.Cells[5].Value = Math.Round(planSum == 0 ? 0 : factSum / planSum * 100);
            node.Cells[6].Value = Math.Round(planWeight == 0 ? 0 : factWeight / planWeight * 100);
            node.Cells[7].Value = Math.Round(planSum == 0 ? 0 : apprSum / planSum * 100);
            node.Cells[8].Value = Math.Round(planWeight == 0 ? 0 : apprWeight / planWeight * 100);

            sumout += sum;
            weightout += weight;
         }
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

      protected virtual void CreatePriceTree()
      {
         List<Price> list = new List<Price>();
         list.AddRange(dsPrice.Values);
         list.Sort((lhs, rhs) => { return lhs.Name.CompareTo(rhs.Name); });

         tgvPrice.SuspendLayout();
         tgvPrice.Nodes.Clear();
         tgvPrice.Rows.Clear();

         foreach (Price p in list)
         {
            TreeGridNode n = tgvPrice.Nodes.Add(p.Name, 0, 0, 0, 0, 0, 0, 0, 0);
            n.Tag = p;
         }


         tgvPrice.ResumeLayout();
      }

      //private void fillGridRecursive(TreeNode node, TreeGridNodeCollection parent)
      //{
      //   if (node.Tag is ManagerFolder)
      //   {
      //      TreeGridNode child = AddFolderNode(parent, (ManagerFolder)node.Tag);

      //      foreach (TreeNode n in node.Nodes)
      //         fillGridRecursive(n, child.Nodes);
      //   }
      //}

      private int WorkingDay(DateTime start, DateTime end)
      {
         int result = 0;

         while (end >= start)
         {
            if (!(end.DayOfWeek == DayOfWeek.Sunday || end.DayOfWeek == DayOfWeek.Saturday))
               result++;

            end = end.AddDays(-1);
         }

         return result;
      }

      //virtual protected TreeGridNode AddFolderNode(TreeGridNodeCollection parent, ManagerFolder f)
      //{
      //   TreeGridNode result = parent.Add(dsFolder[f.id].name, 0, 0, 0, 0, 0, 0, 0, 0);
      //   result.Tag = f;
      //   return result;
      //}

      private double GetFolderPlanSum(string id)
      {
         return itemPlan.ContainsKey(id) ? itemPlan[id].sum : 0;
      }

      private double GetFolderPlanWeight(string id)
      {
         return itemPlan.ContainsKey(id) ? itemPlan[id].qty : 0;
      }

      private double GetFolderFactSum(string id)
      {
         return itemFact.ContainsKey(id) ? itemFact[id].sum : 0;
      }

      private double GetFolderFactWeight(string id)
      {
         return itemFact.ContainsKey(id) ? itemFact[id].qty : 0;
      }

      private void FmAgentPlan_Load(object sender, EventArgs e)
      {
         if (CurrentUser.user != null)
         {
            List<Agent> list = new List<Agent>();
            foreach (Agent a in ((Manager)CurrentUser.user).GetAgents().Data)
               list.Add(a);

            list.Sort(new Comparison<Agent>(delegate(Agent lhs, Agent rhs) { return lhs.Name.CompareTo(rhs.Name); }));

            cbAgent.Items.AddRange(list.ToArray());
         }
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         SaveData();
      }

      private void FillPlanRecursive(AgentPlan plan, TreeGridNode node)
      {
         InsertPlan(plan, node);

         foreach (TreeGridNode child in node.Nodes)
            FillPlanRecursive(plan, child);
      }

      private void InsertPlan(AgentPlan plan, TreeGridNode node)
      {
         double valSum = 0.0;
         double valWeight = 0.0;
         Double.TryParse(node.Cells[1].Value.ToString(), out valSum);
         Double.TryParse(node.Cells[2].Value.ToString(), out valWeight);

         if (valSum > 0 || valWeight > 0)
         {
            AgentPlanItem item = new AgentPlanItem();
            item.valueSum = valSum;
            item.valueWeight = valWeight;

            item.id = ((Price)node.Tag).id;
            plan.items.Add(item);
         }
      }

      private void FmAgentPlan_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (btnSave.Enabled)
         {
            DialogResult res = MessageBox.Show(this, "Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
            if (res == DialogResult.Cancel || (res == DialogResult.Yes && !SaveData()))
            {
               e.Cancel = true;
               return;
            }
         }

      }

      private bool SaveData()
      {
         bool result = false;
        
         AgentPlan plan = new AgentPlan();
         plan.items = new List<AgentPlanItem>();
         plan.agent = selectedAgent;
         plan.userid = selectedAgent.id;
         plan.begin = new DateTime(dtpDate.Value.Year, dtpDate.Value.Month, 1);
         plan.end = new DateTime(dtpDate.Value.Year, dtpDate.Value.Month,
             DateTime.DaysInMonth(dtpDate.Value.Year, dtpDate.Value.Month));

         foreach (TreeGridNode node in tgvPrice.Nodes)
            FillPlanRecursive(plan, node);

         SimpleDataSet<AgentPlan> changedPlan = new SimpleDataSet<AgentPlan>(AgentPlan.OBJECT_NAME);
         changedPlan.Add(plan);
         List<IDataSet> wrSet = new List<IDataSet>();
         wrSet.Add(changedPlan);

         if (!DataModule.UpdateDataSet(wrSet, null, null, Config.GetConfig().GetConnection()))
         {
            MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK,
               MessageBoxIcon.Error);
         }
         else
         {
            btnSave.Enabled = false;
            result = true;
         }

         return result;
      }

      private void tgvPrice_CellEndEdit(object sender, DataGridViewCellEventArgs e)
      {
         btnSave.Enabled = true;
      }

      private void cbAgent_SelectionChangeCommitted(object sender, EventArgs e)
      {
         if (btnSave.Enabled &&
            MessageBox.Show(this, "Сохранить изменения?", "Вопрос", MessageBoxButtons.OKCancel,
            MessageBoxIcon.Question) == DialogResult.OK)
            SaveData();

         selectedAgent = cbAgent.SelectedItem as Agent;
         tgvPrice.Nodes.Clear();
         tgvPrice.Rows.Clear();
      }
   }

   class PlanValue
   {
      public double sum;
      public double qty;

      public PlanValue() { }
      public PlanValue(double sum, double weight)
      {
         this.sum = sum;
         this.qty = weight;
      }
   }
}
