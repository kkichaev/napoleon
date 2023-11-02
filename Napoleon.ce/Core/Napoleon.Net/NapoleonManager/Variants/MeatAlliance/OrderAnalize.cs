using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;

namespace GRSoft.NapoleonManager
{
   public partial class OrderAnalize : Form
   {
      List<Agent> selectedAgents = new List<Agent>();
      List<AgentData> allAgents = new List<AgentData>();
      SimpleDataSet<Order> orders = new SimpleDataSet<Order>(Order.OBJECT_NAME, false, true);
      DataSet<string, Price> dsPrice;

      List<WeekData> weeks = new List<WeekData>();

      DataGridViewColumn[] assortiments = new DataGridViewColumn[8];
      DataGridViewColumn[] ordChanges = new DataGridViewColumn[4];

      DateTime end;
      DateTime start;

      bool refreshData = false;

      static OrderAnalize instance = null;

      public OrderAnalize()
      {
         InitializeComponent();
         dgvAgents.AutoGenerateColumns = false;
         dgvOrders.AutoGenerateColumns = false;

         end = DateTime.Now.Date.AddDays(1);
         start = end.AddMonths(-2);
         start = new DateTime(start.Year, start.Month, 1);

         int i = 0;
         for (DateTime cd = end; cd >= start; )
         {
            WeekData wd = new WeekData();
            wd.end = cd;
            wd.start = StartOfWeek(cd);
            weeks.Insert(0, wd);

            String head = String.Format("{0:dd.MM} {1:dd.MM}", wd.start, wd.end);
            if (i < assortiments.Length)
            {
               DataGridViewColumn clmn = new DataGridViewTextBoxColumn();
               clmn.HeaderText = head;
               clmn.Name = "AW" + i.ToString();
               clmn.DataPropertyName = "AvgW" + i.ToString();
               clmn.Width = 60;
               assortiments[assortiments.Length - i - 1] = clmn;
            }

            if (i < ordChanges.Length)
            {
               DataGridViewColumn clmn = new DataGridViewTextBoxColumn();
               clmn.HeaderText = head;
               clmn.Name = "OW" + i.ToString();
               clmn.DataPropertyName = "OW" + i.ToString();
               clmn.DefaultCellStyle.Format = "N2";
               clmn.Width = 60;
               ordChanges[ordChanges.Length - i - 1] = clmn;
            }

            cd = wd.start.AddDays(-1);
            i++;
         }

         cbRepType.SelectedIndex = 0;
         dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ?? new DataSet<string, Price>(Price.OBJECT_NAME);
      }

      DateTime StartOfWeek(DateTime dt)
      {
         int diff = dt.DayOfWeek - DayOfWeek.Monday;
         if (diff < 0)
            diff += 7;
         return dt.AddDays(-diff).Date;
      }

      public static void Open()
      {
         if (instance == null)
         {
            instance = new OrderAnalize();
            instance.Show();
         }
         else
         {
            instance.RefreshData();
            instance.BringToFront();
         }
      }
      protected override void OnClosed(EventArgs e)
      {
         instance = null;
         base.OnClosed(e);
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
      }

      private void RefreshData()
      {
         Manager dm = CurrentUser.user as Manager;
         if (allAgents.Count == 0)
         {
            if (dm == null)
               return;

            foreach (Agent a in dm.GetAgents().Data)
               allAgents.Add(new AgentData(a, this));

            dgvAgents.DataSource = allAgents;
         }

         List<IDataSet> upd = new List<IDataSet>();

         foreach (Agent a in dm.GetAgents().Data)
         {
            DataSet<string, Org> orgs =
               DataModule.GetUserDataSet(a.id, Org.OBJECT_NAME, typeof(DataSet<string, Org>)) as DataSet<string, Org>;

            if (orgs.Count == 0)
            {
               orgs.Command = new ServerCommand(Commands.Impersonate(Commands.GET, a.id), orgs.Name);
               upd.Add(orgs);
            }
         }

         if (dsPrice.Count == 0)
            upd.Add(dsPrice);

         string uid = DataUtils.MakeFilterFromAgents(null, dm.GetAgents());
         string docsFilter = uid + " and " + DataUtils.MakeCreatedDataFilter(start, end);
         orders.Filter = docsFilter;
         upd.Add(orders);

         FmWait.StdDataRefresh(this, upd, RefreshOrders);
      }

      bool IsAgentChecked(Agent a)
      {
         return selectedAgents.Contains(a);
      }

      void SetAgentChecked(Agent a, bool check)
      {
         selectedAgents.Remove(a);
         if (check)
            selectedAgents.Add(a);

         RefreshOrders();
      }

      void RefreshOrders()
      {
         Dictionary<Org, Boolean> orgs = new Dictionary<Org, bool>();
         List<OrderAnalizeData> src = new List<OrderAnalizeData>();
         foreach (Order o in orders.Data)
         {
            if (IsAgentChecked(o.agent) && orgs.ContainsKey(o.org) == false)
            {
               src.Add(new OrderAnalizeData(o.org, o.agent, orders, weeks));
               orgs[o.org] = true;
            }
         }
         
         dgvOrders.DataSource = src;
      }

      class AgentData
      {
         Agent agent;
         OrderAnalize owner;

         public AgentData(Agent a, OrderAnalize owner)
         {
            agent = a;
            this.owner = owner;
         }

         public string Name { get { return agent.Name; } }
         public bool Checked
         {
            get { return owner.IsAgentChecked(agent); }
            set { owner.SetAgentChecked(agent, value); }
         }
      }

      private void toolStripComboBox1_SelectedIndexChanged(object sender, EventArgs e)
      {
         dgvOrders.SuspendLayout();
         dgvOrders.Columns.Clear();

         dgvOrders.Columns.Add(clmnOrg);
         int sel = cbRepType.SelectedIndex;
         if (sel == 0) //active ass
            dgvOrders.Columns.AddRange(assortiments);
         else // order changes
            dgvOrders.Columns.AddRange(ordChanges);

         dgvOrders.ResumeLayout();
      }

      private void dgvOrders_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         string clmnName = dgvOrders.Columns[e.ColumnIndex].Name;
         if (clmnName.StartsWith("AvgW"))
         {
            OrderAnalizeData oad = (OrderAnalizeData)dgvOrders.Rows[e.RowIndex].DataBoundItem;
            int val = (int)e.Value;
            e.CellStyle.BackColor = (val < oad.avgSku) ? Color.LightPink : dgvOrders.DefaultCellStyle.BackColor;
         }
         else if (clmnName.StartsWith("OW"))
         {
            OrderAnalizeData oad = (OrderAnalizeData)dgvOrders.Rows[e.RowIndex].DataBoundItem;
            double val = (double)e.Value;
            e.CellStyle.BackColor = (val < oad.avgWeight) ? Color.LightPink : dgvOrders.DefaultCellStyle.BackColor;
         }
      }

      private void dgvAgents_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         if (dgvAgents.CurrentCell.ColumnIndex == clmnChecked.DisplayIndex)
            dgvAgents.CommitEdit(DataGridViewDataErrorContexts.Commit);
      }
   }

   class OrderAnalizeData
   {
      Agent agent;
      Org org;

      public int avgSku = 0;
      public double avgWeight = 0;
      List<WeekDataReport> data = new List<WeekDataReport>();

      public OrderAnalizeData(Org org, Agent a, SimpleDataSet<Order> orders, List<WeekData> weeks)
      {
         this.org = org;
         this.agent = a;

         foreach (WeekData wd in weeks)
         {
            data.Add(new WeekDataReport(wd));
         }

         int ordCount = 0;
         foreach (Order o in orders.Data)
         {
            if (o.agent != a || o.org != org)
               continue;
            
            ordCount++;
            double weight = o.Weight;
            avgSku += o.items.Count;
            avgWeight += weight;

            foreach (WeekDataReport wd in data)
               if (wd.Contains(o.created))
               {
                  wd.ordCount++;
                  wd.sku += o.items.Count;
                  wd.weight += weight;
                  break;
               }
         }

         if (ordCount == 0)
            ordCount = 1;
         avgSku /= ordCount;
         avgWeight /= ordCount;
      }

      public int AvgW0 { get { return GetWeekAvg(0); } }
      public int AvgW1 { get { return GetWeekAvg(1); } }
      public int AvgW2 { get { return GetWeekAvg(2); } }
      public int AvgW3 { get { return GetWeekAvg(3); } }
      public int AvgW4 { get { return GetWeekAvg(4); } }
      public int AvgW5 { get { return GetWeekAvg(5); } }
      public int AvgW6 { get { return GetWeekAvg(6); } }
      public int AvgW7 { get { return GetWeekAvg(7); } }
      public int AvgW8 { get { return GetWeekAvg(8); } }

      public double OW0 { get { return GetWeekAvgWeight(0); } }
      public double OW1 { get { return GetWeekAvgWeight(1); } }
      public double OW2 { get { return GetWeekAvgWeight(2); } }
      public double OW3 { get { return GetWeekAvgWeight(3); } }
      public double OW4 { get { return GetWeekAvgWeight(4); } }
      public double OW5 { get { return GetWeekAvgWeight(5); } }
      public double OW6 { get { return GetWeekAvgWeight(6); } }
      public double OW7 { get { return GetWeekAvgWeight(7); } }
      public double OW8 { get { return GetWeekAvgWeight(8); } }

      public int GetWeekAvg(int index)
      {
         if (index >= data.Count)
            return 0;
         return data[data.Count - index - 1].Avg;
      }

      public double GetWeekAvgWeight(int index)
      {
         if (index >= data.Count)
            return 0;
         return data[data.Count - index - 1].AvgWeight;
      }

      public string Name { get { return org.Name; } }
   }

   class WeekData
   {
      public DateTime start;
      public DateTime end;

      public bool Contains(DateTime d)
      {
         return d >= start && d <= end;
      }
   }

   class WeekDataReport : WeekData
   {
      public int sku = 0;
      public int ordCount;
      public double weight = 0;

      public WeekDataReport(WeekData src)
      {
         this.start = src.start;
         this.end = src.end;
      }

      public int Avg { get { return ordCount == 0 ? 0 : sku / ordCount; } }
      public double AvgWeight { get { return ordCount == 0 ? 0 : weight / ordCount; } }
   }

}
