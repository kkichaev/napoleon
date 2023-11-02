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


namespace GRSoft.NapoleonManager
{
   public partial class FmAgentPlans : Form
   {
      DateTime curPlanDate;

      public FmAgentPlans()
      {
         InitializeComponent();
         dgvPlans.AutoGenerateColumns = false;
         dtPlanDate.Value = new DateTime(DateTime.Now.Year, DateTime.Now.Month, 1);
         LoadData(dtPlanDate.Value);
      }

      SimpleDataSet<AgentPlan> plans = new SimpleDataSet<AgentPlan>(AgentPlan.OBJECT_NAME, false);
      SimpleDataSet<Order> orders = new SimpleDataSet<Order>(Order.OBJECT_NAME, false);
      DataSet<string, Price> price;

      void LoadData(DateTime date)
      {
         Config c = Config.GetConfig();
         if (!c.CheckLogin())
            return;

         DBConnection conn = c.GetConnection();
         List<IDataSet> upd = new List<IDataSet>();
         Agents a = Agents.GetDataSet();
         if (a.Count == 0)
         {
            upd.Add(a);
            upd.Add(DivisionList.GetDataSet());
            CurrentUser.InitCurrentUser(upd, true);
         }

         if (price == null)
         {
            price = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ??
               new DataSet<string, Price>(Price.OBJECT_NAME, true);
         }
         if (price.Count == 0)
            upd.Add(price);

         curPlanDate = date.Date;
         DateTime endDate = curPlanDate.AddMonths(1);

         const String FILTER = "\"{0}\" >= ToDate('{1:dd/MM/yyyy}') and \"{0}\" < ToDate('{2:dd/MM/yyyy}')";

         plans.Filter = String.Format(FILTER, "begin", curPlanDate, endDate);
         upd.Add(plans);

         orders.Filter = String.Format(FILTER, "created", curPlanDate, endDate);
         upd.Add(orders);

         DataModule.OnDataResponceError += new EventDataResponseError(DataError);
         DataModule.DataProcessed += new EventHandler(DataReceived);

         Thread t = DataModule.RefreshGiveSets(conn, upd, FmWait.ProgressIndicator);         
         FmWait.ShowForm(this, t);
      }

      void DataReceived(object sender, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         if (CurrentUser.user == null)
         {
            CurrentUser.SetCurrentUser(false);
            if (!(CurrentUser.user is Manager))
            {
               MessageBox.Show(this, "Возможно, пользователь не менеджер или в программе отсутствют подразделения",
                  "Ошибка",MessageBoxButtons.OK, MessageBoxIcon.Error);
               
               return;
            }
         }
         Invoke(new EmptyParamHandler(delegate() {
            RefreshData();
         }));
      }

      int CmpPlans(PlanItem i1, PlanItem i2)
      {
         return i1.Name.CompareTo(i2.Name);
      }

      void OnPlanItemChanged(object sender, EventArgs args)
      {
         PlanItem item = (PlanItem)sender;
         
         AgentPlan plan = null;
         foreach (AgentPlan ap in plans.Data)
         {
            if (ap.userid.CompareTo(item.agent.id) == 0)
            {
               plan = ap;
               break;
            }
         }

         if (plan == null)
         {
            plan = new AgentPlan();
            plan.userid = item.agent.id;
            plan.agent = item.agent;
            plan.begin = curPlanDate;
            plan.end = curPlanDate.AddMonths(1);
            plans.Add(plan);
            plan.items = new List<AgentPlanItem>();
         } else
            plan.items.Clear();

         AgentPlanItem pi = new AgentPlanItem();
         pi.id = AgentPlan.PLAN1_TAG;
         pi.value = item.Plan1;
         plan.items.Add(pi);

         pi = new AgentPlanItem();
         pi.id = AgentPlan.PLAN2_TAG;
         pi.value = item.Plan2;
         plan.items.Add(pi);

         tbSave.Enabled = true;
      }      

      void RefreshData()
      {
         List<PlanItem> items = new List<PlanItem>();
         Manager m = CurrentUser.user as Manager;
         if (m == null)
            return;
         Agents a = m.GetAgents();
         foreach (Agent agent in a.Data)
         {
            PlanItem pi = new PlanItem(agent, curPlanDate);
            pi.FillAgentPlans(agent, plans.Data, orders.Data, price);
            pi.changed += new EventHandler(OnPlanItemChanged);
            items.Add(pi);
         }
         items.Sort(CmpPlans);

         clmnPlan1.ReadOnly = !CanEditPlan();
         clmnPlan2.ReadOnly = !CanEditPlan();
         dgvPlans.DataSource = items;
      }

      private bool CanEditPlan()
      {
         DateTime check = new DateTime(curPlanDate.Year, curPlanDate.Month, 1);
         DateTime now = new DateTime(DateTime.Now.Year, DateTime.Now.Month, 1);
         return (check.CompareTo(now) >= 0);
      }

      void DataError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         MessageBox.Show(e.Msg);
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         if (tbSave.Enabled)
         {
            DialogResult res = MessageBox.Show(this, "Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
            if (res == DialogResult.Cancel || (res == DialogResult.Yes && !SaveData()))
            {
               e.Cancel = true;
               return;
            }
         }
         base.OnClosing(e);
      }

      bool SaveData()
      {
         Config c = Config.GetConfig();
         String filter = plans.Name + ":" + plans.Filter;
         bool ret = DataModule.ReplaceDataSet(plans, c.GetConnection(), filter);
         if (!ret)
            MessageBox.Show("Ошибка при записи планов");
         else
            tbSave.Enabled = false;
         return ret;
      }

      private void tbSave_Click(object sender, EventArgs e)
      {
         SaveData();
      }

      private void tbRefresh_Click(object sender, EventArgs e)
      {
         LoadData(dtPlanDate.Value.Date);
      }

      private void tbReport_Click(object sender, EventArgs e)
      {
         FmAgentPlanReport rep = new FmAgentPlanReport();
         rep.ShowDialog();
      }
   }

   class PlanItem : IComparable<PlanItem>
   {
      public Agent agent;
      double plan1, plan2;
      double fact1, fact2;
      double predict1, predict2;
      DateTime begin;

      public PlanItem(Agent agent, DateTime begin)
      {
         this.agent = agent;
         this.begin = begin;
      }

      public void FillAgentPlans(Agent a, ICollection planData, OrderData factData)
      {
         LoadPlan(a, planData);
         if (factData != null)
         {
            fact1 = factData.fact1;
            fact2 = factData.fact2;
         }
      }

      public DateTime Begin { get { return begin; } }

      void LoadPlan(Agent a, ICollection planData)
      {
         foreach (AgentPlan p in planData)
         {
            if (p.userid.CompareTo(a.id) == 0 && p.begin.CompareTo(begin) == 0)
            {
               foreach (AgentPlanItem pi in p.items)
               {
                  if (pi.id.StartsWith(AgentPlan.PLAN1_TAG))
                     plan1 = pi.value;
                  else if (pi.id.StartsWith(AgentPlan.PLAN2_TAG))
                     plan2 = pi.value;
               }
               break;
            }
         }
      }

      public void FillAgentPlans(Agent a, ICollection planData, ICollection orderData, DataSet<string, Price> price)
      {
         LoadPlan(a, planData);

         fact1 = 0;
         fact2 = 0;
         foreach (Order order in orderData)
         {
            if (order.AgentID.CompareTo(a.id) == 0)
            {
               double f1 = 0, f2 = 0;
               foreach (OrderItem oi in order.items)
               {
                  if (price.ContainsKey(oi.id))
                  {
                     double weight = price[oi.id].weight * oi.qty;
                     if (oi.id.StartsWith(AgentPlan.PLAN1_TAG))
                        f1 += weight;
                     else if (oi.id.StartsWith(AgentPlan.PLAN2_TAG))
                        f2 += weight;
                  }
               }

               fact1 += f1/1000;
               fact2 += f2/1000;
            }
         }

         if (DateTime.Now.Month == begin.Month && DateTime.Now.Year == begin.Year)
         {
            DateTime lastMontDate = (new DateTime(begin.Year, begin.Month, 1)).AddMonths(1).AddDays(-1);
            CalcPredict(DateTime.Now.Day, lastMontDate.Day);
         }
      }

      public void CalcPredict(int curDate, int lastDate)
      {
         predict1 = fact1 * lastDate / curDate;
         predict2 = fact2 * lastDate / curDate;
      }

      public string Name { get { return agent.Name; } }

      public event EventHandler changed;

      public double Plan1
      {
         get { return plan1; }
         set
         {
            plan1 = value;
            if (changed != null)
               changed.Invoke(this, EventArgs.Empty);
         }
      }

      public double Plan2
      {
         get { return plan2; }
         set
         {
            plan2 = value;
            if (changed != null)
               changed.Invoke(this, EventArgs.Empty);
         }
      }

      public string Fact1
      {
         get
         {
            if( predict1 == 0 )
               return String.Format("{0:N3}", fact1);

            return String.Format("{0:N3} / {1:N3}",  fact1, predict1);
         }
      }

      public string Fact2
      {
         get
         {
            if (predict2 == 0)
               return String.Format("{0:N3}", fact2);

            return String.Format("{0:N3} / {1:N3}", fact2, predict2);
         }
      }

      #region Члены IComparable<PlanItem>

      public int CompareTo(PlanItem other)
      {
         return Name.CompareTo(other.Name);
      }

      #endregion
   }

   class OrderData
   {
      public double fact1;
      public double fact2;

      internal void Add(Order order, DataSet<string, Price> price)
      {
         double f1 = 0, f2 = 0;
         foreach (OrderItem oi in order.items)
         {
            if (price.ContainsKey(oi.id))
            {
               double weight = price[oi.id].weight * oi.qty;
               if (oi.id.StartsWith(AgentPlan.PLAN1_TAG))
                  f1 += weight;
               else if (oi.id.StartsWith(AgentPlan.PLAN2_TAG))
                  f2 += weight;
            }
         }

         fact1 += f1 / 1000;
         fact2 += f2 / 1000;
      }
   }

   class AgentPlanItem : GRSoft.Network.DataObject
   {
      public string id;
      public double value;
   }

   class AgentPlan : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "AgentPlan";
      public string userid = "";

      public static readonly string PLAN1_TAG = "1\t";
      public static readonly string PLAN2_TAG = "2\t";

      [Reference("Agents", "userid")]
      public Agent agent = null;

      public DateTime begin = DateTime.MinValue;
      public DateTime end = DateTime.MinValue;

      [ItemType(typeof(AgentPlanItem))]
      public List<AgentPlanItem> items = null;
   }
}
