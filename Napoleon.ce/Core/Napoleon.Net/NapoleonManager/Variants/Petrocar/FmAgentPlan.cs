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
      const int PRICE_TYPE = 0;
      const int FOLDER_TYPE = 1;

      private DataSet<string, ManagerFolder> dsFolder = new DataSet<string,ManagerFolder>(ManagerFolder.OBJECT_NAME, false);
      private DataSet<int, Delivery> dsOrder;
      private SimpleDataSet<AgentPlan> dsAgentPlan = new SimpleDataSet<AgentPlan>(AgentPlan.OBJECT_NAME, false);
      private Dictionary<string, PlanValue> planData = new Dictionary<string, PlanValue>();
      private Dictionary<string, PlanValue> itemFact = new Dictionary<string, PlanValue>();
      private DataSet<string, Price> dsPrice = new DataSet<string, Price>(Price.OBJECT_NAME, false);

      Dictionary<TreeGridNode, PlanValue> factTreeCache = new Dictionary<TreeGridNode, PlanValue>();

      Agent selectedAgent = null;

      public FmAgentPlan()
      {
         InitializeComponent();
         dtpDate.Value = new DateTime(DateTime.Now.Year, DateTime.Now.Month, 1);
         dsOrder = DataModule.Get(Delivery.OBJECT_NAME) == null ? new DataSet<int, Delivery>(Delivery.OBJECT_NAME, false) :
            (DataSet<int, Delivery>)DataModule.Get(Delivery.OBJECT_NAME);
      }
      //string COMMON_FILTER_STR = "\"{0}\" >= ToDate('{1:dd/MM/yyyy}') and \"{0}\" <= ToDate('{2:dd/MM/yyyy} 23:59:59') and \"userid\"='{3}'";

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         if (selectedAgent != null)
         {
            DataModule.SetDataRepsonceHandlers(DataProcessed, DataConnectionError);

            dsFolder.Command = new ServerCommand(Commands.Impersonate(Commands.GET, selectedAgent.id), dsFolder.Name);
            DateTime begin = new DateTime(dtpDate.Value.Year, dtpDate.Value.Month, 1);
            DateTime end  = new DateTime(dtpDate.Value.Year, dtpDate.Value.Month,
                DateTime.DaysInMonth(dtpDate.Value.Year, dtpDate.Value.Month), 23, 59, 59);
            dsAgentPlan.Filter = string.Format("userid='{0}' and begin=ToDate('{1:dd/MM/yyyy HH:mm:ss}') and end=ToDate('{2:dd/MM/yyyy HH:mm:ss}')",
               selectedAgent.id, begin, end);
            //dsOrder.Filter = String.Format(COMMON_FILTER_STR, "created", begin, end, selectedAgent.id);
            dsPrice.Command = new ServerCommand(Commands.Impersonate(Commands.GET, selectedAgent.id), dsPrice.Name);
            dsOrder.Command = new ServerCommand(Commands.Impersonate(Commands.GET, selectedAgent.id), dsOrder.Name);

            List<IDataSet> updSet = new List<IDataSet>();

            updSet.Add(dsPrice);
            updSet.Add(dsFolder);
            updSet.Add(dsAgentPlan);
            updSet.Add(dsOrder);

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
            planData.Clear();
            itemFact.Clear();

            if (dsAgentPlan.Count > 0)
            {
               AgentPlan plan = dsAgentPlan[0];

               foreach (AgentPlanItem item in plan.groups)
               {
//                  PlanValue val = new PlanValue(item.valueSum, item.valueQty, item.type);
                  PlanValue val = new PlanValue(item.valueSum, item.type);
                  planData[item.type.ToString() + "|" + item.id] = val;
               }
            }

            if (dsOrder.Count > 0)
            {
               int month = dtpDate.Value.Month;
               int year = dtpDate.Value.Year;
               foreach (Delivery order in dsOrder.Data)
               {
                  if (order.date.Year != year || order.date.Month != month)
                     continue;

                  foreach (DeliveryItem item in order.items)
                  {
                     if (item.item != null)
                     {
                        PlanValue pv = null;
                        if(!itemFact.TryGetValue(item.id, out pv))
                        {
                           pv = new PlanValue();
                           itemFact[item.id] = pv;
                        }
                        //pv.qty += item.qty;
                        pv.sum += item.sum;
                     }
                  }
               }
            }

            CreatePriceTree();
            UpdateChildren(tgvPrice.Nodes);
         }));
      }

      //Построить дерево для прайса
      protected virtual void CreatePriceTree()
      {
         TreeView tmpTree = new TreeView();
         ArticlesTreeConstructor treeCnt = new ArticlesTreeConstructor(tmpTree, dsFolder, dsPrice);
         treeCnt.MakeArticlesTree();

         tgvPrice.SuspendLayout();
         tgvPrice.Nodes.Clear();
         tgvPrice.Rows.Clear();

         foreach (TreeNode n in tmpTree.Nodes)
            fillGridRecursive(n, tgvPrice.Nodes);

         tgvPrice.ResumeLayout();
      }

      private void fillGridRecursive(TreeNode node, TreeGridNodeCollection parent)
      {
         TreeGridNode result = null;

         if (node.Tag is ManagerFolder)
         {
            ManagerFolder folder = node.Tag as ManagerFolder;
            result = parent.Add(dsFolder[folder.id].name, 0, 0, 0, 0);

            foreach (TreeNode n in node.Nodes)
               fillGridRecursive(n, result.Nodes);
         }
         else if (node.Tag is Price)
         {
            Price price = node.Tag as Price;
            result = parent.Add(dsPrice[price.id].name, 0, 0, 0, 0);
         }

         if (null != result)
            result.Tag = node.Tag;
      }

      private void UpdateChildren(TreeGridNodeCollection parentNodes)
      {
         factTreeCache.Clear();
         foreach (TreeGridNode node in parentNodes)
         {
            UpdateChildrenRecurs(node);
         }
      }

      PlanValue CountFact(TreeGridNode node)
      {
         PlanValue pv;
         if (factTreeCache.TryGetValue(node, out pv))
            return pv;

         pv = new PlanValue();
         foreach(TreeGridNode ch in node.Nodes)
         {
            Price p = ch.Tag as Price;
            if( p == null)
               pv.Add(CountFact(ch));
            else
            {
               PlanValue priceValue;
               if (itemFact.TryGetValue(p.id, out priceValue))
                  pv.Add(priceValue);
            }
         }

         factTreeCache[node] = pv;
         return pv;
      }

      private void UpdateChildrenRecurs(TreeGridNode parentNode)
      {
         foreach (TreeGridNode childNode in parentNode.Nodes)
         {
            UpdateChildrenRecurs(childNode);
         }
         if (parentNode.Tag is ManagerFolder)
         {
            ManagerFolder folder = parentNode.Tag as ManagerFolder;
            UpdateNode(parentNode, FOLDER_TYPE, folder.id);//, currentQty);
         }
         else if (parentNode.Tag is Price)
         {
            Price price = parentNode.Tag as Price;
            UpdateNode(parentNode, PRICE_TYPE, price.id);//, currentQty);
         }
      }

//      private void UpdateNode(string objectId, TreeGridNode folderNode, double currentSum, double currentQty)
      private void UpdateNode(TreeGridNode folderNode, int type, string id)
      {
         PlanValue pv;

         string tag = type.ToString() + "|" + id;
         double planSum = 0;
         if (planData.TryGetValue(tag, out pv))
         {
            planSum = pv.sum;
         }
         double factSum = 0;
         if(planSum != 0)
         {
            if (type == PRICE_TYPE)
            {
               if (itemFact.TryGetValue(id, out pv))
                  factSum = pv.sum;
            }
            else
            {
               pv = CountFact(folderNode);
               factSum = pv.sum;
            }
         }

         DateTime begin = new DateTime(dtpDate.Value.Year, dtpDate.Value.Month, 1);
         DateTime end = new DateTime(dtpDate.Value.Year, dtpDate.Value.Month,
                DateTime.DaysInMonth(dtpDate.Value.Year, dtpDate.Value.Month), 23, 59, 59);
         DateTime now = new DateTime(DateTime.Now.Year, DateTime.Now.Month, DateTime.Now.Day);
         DateTime tommorow = now.AddDays(1);

         int wd = WorkingDay(begin, now);
         double avgSum = factSum / wd;
         //double avgQty = factQty / wd;

         wd = WorkingDay(tommorow, end);
         double endSum = avgSum * wd;
         double apprSum = factSum + endSum;
         //double endQty = avgQty * wd;
         //double apprQty = factQty + endQty;

         //FillCells(folderNode, planSum, planQty, factSum, factQty, apprSum, apprQty);
         FillCells(folderNode, planSum, factSum, apprSum);
      }

//      void FillCells(TreeGridNode node, double planSum, double planQty, double factSum, double factQty, double apprSum, double apprQty)
      void FillCells(TreeGridNode node, double planSum, double factSum, double apprSum)
      {
         int ctr = 1;
         node.Cells[ctr++].Value = planSum;
         //node.Cells[ctr++].Value = planQty;
         node.Cells[ctr++].Value = factSum;
         //node.Cells[ctr++].Value = factQty;
         node.Cells[ctr++].Value = Math.Round(planSum == 0 ? 0 : factSum / planSum * 100);
         //node.Cells[ctr++].Value = Math.Round(planQty == 0 ? 0 : factQty / planQty * 100);
         node.Cells[ctr++].Value = Math.Round(planSum == 0 ? 0 : apprSum / planSum * 100);
         //node.Cells[ctr++].Value = Math.Round(planQty == 0 ? 0 : apprQty / planQty * 100);
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

      private void FmAgentPlan_Load(object sender, EventArgs e)
      {
         List<Agent> list = new List<Agent>();
         foreach (Agent a in ((Manager)CurrentUser.user).GetAgents().Data)
            list.Add(a);

         list.Sort(new Comparison<Agent>(delegate(Agent lhs, Agent rhs){return lhs.Name.CompareTo(rhs.Name); }));

         cbAgent.Items.AddRange(list.ToArray());
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
         double valQty = 0.0;
         Double.TryParse(node.Cells[1].Value.ToString(), out valSum);
         Double.TryParse(node.Cells[2].Value.ToString(), out valQty);

         if (valSum > 0 || valQty > 0)
         {
            AgentPlanItem item = new AgentPlanItem();
            item.valueSum = valSum;
            item.valueQty = valQty;

            string itemId = string.Empty;
            if (node.Tag is ManagerFolder)
            {
               itemId = (node.Tag as ManagerFolder).id;
               item.type = FOLDER_TYPE;
            }
            else if (node.Tag is Price)
            {
               itemId = (node.Tag as Price).id;
               item.type = PRICE_TYPE;
            }

            item.id = itemId;
            plan.groups.Add(item);
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
         plan.groups = new List<AgentPlanItem>();
         plan.agent = selectedAgent;
         plan.userid = selectedAgent.id;
         plan.begin = new DateTime(dtpDate.Value.Year, dtpDate.Value.Month, 1);
         plan.end = new DateTime(dtpDate.Value.Year, dtpDate.Value.Month,
             DateTime.DaysInMonth(dtpDate.Value.Year, dtpDate.Value.Month), 23, 59, 59);

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
      //public double qty;
      public int type;

      public PlanValue() { }
      //public PlanValue(double sum, double qty, int type)
      public PlanValue(double sum, int type)
      {
         this.sum = sum;
         //this.qty = qty;
         this.type = type;
      }

      public PlanValue Add(PlanValue other)
      {
         sum += other.sum;
         return this;
      }
   }
}
