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
            
            itemPlan.Clear();
            itemFact.Clear();

            if (dsAgentPlan.Count > 0)
            {
               AgentPlan plan = dsAgentPlan[0];

               foreach (AgentPlanItem item in plan.groups)
                  groupPlan[item.id] = item.value;

               foreach (AgentPlanItem item in plan.items)
                  itemPlan[item.id] = item.value;
            }

            DateTime end = new DateTime(dtpDate.Value.Year, dtpDate.Value.Month,
               DateTime.DaysInMonth(dtpDate.Value.Year, dtpDate.Value.Month));

            if (dsOrder.Count > 0)
            {
               foreach (Order order in dsOrder.Data)
                  foreach (OrderItem item in order.items)
                  {
                     if (item.item != null)
                     {
                        double w = item.qty;

                        if(itemFact.ContainsKey(item.id))
                           itemFact[item.id] += w;
                        else
                           itemFact[item.id] = w;

                        if(groupFact.ContainsKey(item.item.fid))
                           groupFact[item.item.fid] += w;
                        else
                           groupFact[item.item.fid] = w;
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
         TreeGridNode result = parent.Add(dsFolder[f.id].name, plan, fact, Math.Round(plan == 0 ? 0 : fact / plan * 100));
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

      virtual protected TreeGridNode AddPriceNode(TreeGridNodeCollection parent, Price p)
      {
         double plan = GetPricePlan(p.id);
         double fact = GetPriceFact(p.id);
         TreeGridNode result = parent.Add(p.name, plan, fact, Math.Round(plan == 0 ? 0 : fact / plan * 100));
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
