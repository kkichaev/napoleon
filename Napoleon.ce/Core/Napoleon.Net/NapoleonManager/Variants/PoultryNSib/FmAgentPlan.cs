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
      private DataSet<string, Price> dsPrice = new DataSet<string,Price>(Price.OBJECT_NAME, false);
      private DataSet<string, ManagerFolder> dsFolder = new DataSet<string,ManagerFolder>(ManagerFolder.OBJECT_NAME, false);
      private DataSet<int, Order> dsOrder;
      private SimpleDataSet<AgentPlan> dsAgentPlan = new SimpleDataSet<AgentPlan>(AgentPlan.OBJECT_NAME, false);
      private Dictionary<string, double> groupPlan = new Dictionary<string, double>();
      private Dictionary<string, double> itemPlan = new Dictionary<string, double>();
      private Dictionary<string, double> groupFact = new Dictionary<string, double>();
      private Dictionary<string, double> itemFact = new Dictionary<string, double>();
      private Dictionary<string, double> groupFact1W = new Dictionary<string, double>();
      private Dictionary<string, double> itemFact1W = new Dictionary<string, double>();
      private Dictionary<string, double> groupFact2W = new Dictionary<string, double>();
      private Dictionary<string, double> itemFact2W = new Dictionary<string, double>();
      private Dictionary<string, double> groupFact3W = new Dictionary<string, double>();
      private Dictionary<string, double> itemFact3W = new Dictionary<string, double>();
      private Dictionary<string, double> groupFact4W = new Dictionary<string, double>();
      private Dictionary<string, double> itemFact4W = new Dictionary<string, double>();

      Agent selectedAgent = null;

      public FmAgentPlan()
      {
         InitializeComponent();
         dsOrder = DataModule.Get("Order") == null ? new DataSet<int, Order>("Order", true, true) :
            (DataSet<int, Order>)DataModule.Get("Order");
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         if (selectedAgent != null)
         {
            DataModule.SetDataRepsonceHandlers(DataProcessed, DataConnectionError);

            dsPrice.Command = new ServerCommand(Commands.Impersonate(Commands.GET, selectedAgent.id), dsPrice.Name);
            dsFolder.Command = new ServerCommand(Commands.Impersonate(Commands.GET, selectedAgent.id), dsFolder.Name);
            DateTime begin = new DateTime(dtpDate.Value.Year, dtpDate.Value.Month, 1);
            DateTime end  = new DateTime(dtpDate.Value.Year, dtpDate.Value.Month,
                DateTime.DaysInMonth(dtpDate.Value.Year, dtpDate.Value.Month));
            dsAgentPlan.Filter = string.Format("userid='{0}' and begin=ToDate('{1:dd/MM/yyyy}') and end=ToDate('{2:dd/MM/yyyy}')",
               selectedAgent.id, begin, end);
            dsOrder.Filter = String.Format(FmDetailBase.COMMON_FILTER_STR, "created", begin, end, selectedAgent.id);

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
            groupPlan.Clear();
            groupFact.Clear();
            groupFact1W.Clear();
            groupFact2W.Clear();
            groupFact3W.Clear();
            groupFact4W.Clear();
            itemPlan.Clear();
            itemFact.Clear();
            itemFact1W.Clear();
            itemFact2W.Clear();
            itemFact3W.Clear(); 
            itemFact4W.Clear();


            if (dsAgentPlan.Count > 0)
            {
               AgentPlan plan = dsAgentPlan[0];

               foreach (AgentPlanItem item in plan.groups)
                  groupPlan[item.id] = item.value;

               foreach (AgentPlanItem item in plan.items)
                  itemPlan[item.id] = item.value;
            }

            DateTime begin1W = new DateTime(dtpDate.Value.Year, dtpDate.Value.Month, 1);
            DateTime begin2W = begin1W.AddDays(7);
            DateTime begin3W = begin2W.AddDays(7);
            DateTime begin4W = begin3W.AddDays(7);
            DateTime end = new DateTime(dtpDate.Value.Year, dtpDate.Value.Month,
               DateTime.DaysInMonth(dtpDate.Value.Year, dtpDate.Value.Month));

            if (dsOrder.Count > 0)
            {
               foreach (Order order in dsOrder.Data)
                  foreach (OrderItem item in order.items)
                  {
                     if (item.item != null)
                     {
                        double w = item.qty * item.item.weight;

                        if(itemFact.ContainsKey(item.id))
                           itemFact[item.id] += w;
                        else
                           itemFact[item.id] = w;

                        if(groupFact.ContainsKey(item.item.fid))
                           groupFact[item.item.fid] += w;
                        else
                           groupFact[item.item.fid] = w;

                        // 1 неделя
                        if (order.created >= begin1W && order.created < begin2W)
                        {
                           if(itemFact1W.ContainsKey(item.id))
                              itemFact1W[item.id] += w;
                           else
                              itemFact1W[item.id] = w;

                           if (groupFact1W.ContainsKey(item.item.fid))
                              groupFact1W[item.item.fid] += w;
                           else
                              groupFact1W[item.item.fid] = w;
                        }

                        // 2 неделя
                        if (order.created >= begin2W && order.created < begin3W)
                        {
                           if (itemFact2W.ContainsKey(item.id))
                              itemFact2W[item.id] += w;
                           else
                              itemFact2W[item.id] = w;

                           if (groupFact2W.ContainsKey(item.item.fid))
                              groupFact2W[item.item.fid] += w;
                           else
                              groupFact2W[item.item.fid] = w;
                        }

                        // 3 неделя
                        if (order.created >= begin3W && order.created < begin4W)
                        {
                           if (itemFact3W.ContainsKey(item.id))
                              itemFact3W[item.id] += w;
                           else
                              itemFact3W[item.id] = w;

                           if (groupFact3W.ContainsKey(item.item.fid))
                              groupFact3W[item.item.fid] += w;
                           else
                              groupFact3W[item.item.fid] = w;
                        }

                        // 4 неделя
                        if (order.created >= begin4W && order.created <= end)
                        {
                           if (itemFact4W.ContainsKey(item.id))
                              itemFact4W[item.id] += w;
                           else
                              itemFact4W[item.id] = w;

                           if (groupFact4W.ContainsKey(item.item.fid))
                              groupFact4W[item.item.fid] += w;
                           else
                              groupFact4W[item.item.fid] = w;
                        }
                     }
                  }
            }

            CreatePriceTree();
         }));
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
         if (node.Tag is ManagerFolder)
         {
            TreeGridNode child = AddFolderNode(parent, (ManagerFolder)node.Tag);

            foreach (TreeNode n in node.Nodes)
               fillGridRecursive(n, child.Nodes);
         }
         else if (node.Tag is Price)
         {
            Price p = (Price)node.Tag;
            AddPriceNode(parent, p).Tag = p;
         }
      }

      virtual protected TreeGridNode AddFolderNode(TreeGridNodeCollection parent, ManagerFolder f)
      {
         double plan = GetFolderPlan(f.id);
         double fact = GetFolderFact(f.id);
         TreeGridNode result = parent.Add(dsFolder[f.id].name, plan, fact, Math.Round(plan == 0 ? 0 : fact / plan * 100),
            Math.Round(plan == 0 ? 0 : GetFolderFact1W(f.id) / plan * 100),
            Math.Round(plan == 0 ? 0 : GetFolderFact2W(f.id) / plan * 100),
            Math.Round(plan == 0 ? 0 : GetFolderFact3W(f.id) / plan * 100),
            Math.Round(plan == 0 ? 0 : GetFolderFact4W(f.id) / plan * 100));
         result.Tag = f;
         return result;
      }

      private double GetFolderPlan(string id)
      {
         return groupPlan.ContainsKey(id) ? groupPlan[id] : 0;
      }

      private double GetFolderFact(string id)
      {
         return groupFact.ContainsKey(id) ? groupFact[id] : 0;
      }

      private double GetFolderFact1W(string id)
      {
         return groupFact1W.ContainsKey(id) ? groupFact1W[id] : 0;
      }

      private double GetFolderFact2W(string id)
      {
         return groupFact2W.ContainsKey(id) ? groupFact2W[id] : 0;
      }

      private double GetFolderFact3W(string id)
      {
         return groupFact3W.ContainsKey(id) ? groupFact3W[id] : 0;
      }

      private double GetFolderFact4W(string id)
      {
         return groupFact4W.ContainsKey(id) ? groupFact4W[id] : 0;
      }

      virtual protected TreeGridNode AddPriceNode(TreeGridNodeCollection parent, Price p)
      {
         double plan = GetPricePlan(p.id);
         double fact = GetPriceFact(p.id);
         TreeGridNode result = parent.Add(p.name, plan, fact, Math.Round(plan == 0 ? 0 : fact / plan * 100),
            Math.Round(plan == 0 ? 0 : GetPriceFact1W(p.id) / plan * 100),
            Math.Round(plan == 0 ? 0 : GetPriceFact2W(p.id) / plan * 100),
            Math.Round(plan == 0 ? 0 : GetPriceFact3W(p.id) / plan * 100),
            Math.Round(plan == 0 ? 0 : GetPriceFact4W(p.id) / plan * 100));
         result.Tag = p;
         return result;
      }

      private double GetPricePlan(string id)
      {
         return itemPlan.ContainsKey(id) ? itemPlan[id] : 0;
      }

      private double GetPriceFact(string id)
      {
         return itemFact.ContainsKey(id) ? itemFact[id] : 0; ;
      }

      private double GetPriceFact1W(string id)
      {
         return itemFact1W.ContainsKey(id) ? itemFact1W[id] : 0; ;
      }

      private double GetPriceFact2W(string id)
      {
         return itemFact2W.ContainsKey(id) ? itemFact2W[id] : 0; ;
      }

      private double GetPriceFact3W(string id)
      {
         return itemFact3W.ContainsKey(id) ? itemFact3W[id] : 0; ;
      }

      private double GetPriceFact4W(string id)
      {
         return itemFact4W.ContainsKey(id) ? itemFact4W[id] : 0; ;
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
         double val = 0.0;
         
         if (Double.TryParse(node.Cells[1].Value.ToString(), out val) && val > 0)
         {
            AgentPlanItem item = new AgentPlanItem();
            item.value = val;

            if (node.Tag is ManagerFolder)
            {
               item.id = ((ManagerFolder)node.Tag).id;
               plan.groups.Add(item);
            }
            else if (node.Tag is Price)
            {
               item.id = ((Price)node.Tag).id;
               plan.items.Add(item);
            }
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
}
