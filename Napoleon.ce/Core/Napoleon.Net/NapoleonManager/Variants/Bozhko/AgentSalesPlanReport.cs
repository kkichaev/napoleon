using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
using GRSoft.NapoleonManager.Reports.Excel;

namespace GRSoft.NapoleonManager
{
   public partial class AgentSalesPlanReport : Form
   {
      AgentSalesPlanReportData data = new AgentSalesPlanReportData();

      public AgentSalesPlanReport()
      {
         InitializeComponent();

         dtpEnd.Value = data.dateEnd;
         dtpStart.Value = data.dateStart;
         tbAgents.Text = data.AgentText;
         tbItems.Text = data.PriceText;
      }

      public DateTime StartDate { get { return data.dateStart; } set { data.dateStart = value; dtpStart.Value = value; } }
      public DateTime EndDate { get { return data.dateEnd; } set { data.dateEnd = value; dtpEnd.Value = value; } }

      private void btnClearAgents_Click(object sender, EventArgs e)
      {
         data.agents.Clear();
         tbAgents.Text = data.AgentText;
      }

      private void btnClearItems_Click(object sender, EventArgs e)
      {
         data.price.Clear();
         tbItems.Text = data.PriceText;
      }

      private void btnItems_Click(object sender, EventArgs e)
      {
         List<Price> price = FmSelectSKU.SelectItems(this, data.price, null, true);
         if (price != null)
         {
            data.price = price;
            tbItems.Text = data.PriceText;
         }
      }

      private void btnAgents_Click(object sender, EventArgs e)
      {
         AgentChoose ac = new AgentChoose(data.agents);
         if (ac.ShowDialog() == DialogResult.OK)
         {
            data.agents = ac.SelectedAgents;
            tbAgents.Text = data.AgentText;
         }
      }

      SimpleDataSet<AgentPlanData> plans;
      SimpleDataSet<Order> orders;
      private void btnReport_Click(object sender, EventArgs e)
      {
         string uidFilter = (data.agents.Count > 0) ? DataUtils.MakeFilterFromAgents(null, data.agents) : "";
         string dateFilter = String.Format("(\"dateStart\" >= ToDate('{0:dd/MM/yyyy}') and \"dateStart\" <= ToDate('{1:dd/MM/yyyy} 23:59:59')) or ((\"dateEnd\" >= ToDate('{0:dd/MM/yyyy}') and \"dateEnd\" <= ToDate('{1:dd/MM/yyyy} 23:59:59')))",
            dtpStart.Value, dtpEnd.Value);
         string createdFilter = String.Format("(\"created\" >= ToDate('{0:dd/MM/yyyy}') and \"created\" <= ToDate('{1:dd/MM/yyyy} 23:59:59'))",
            dtpStart.Value, dtpEnd.Value);
         if (uidFilter.Length > 0)
            uidFilter = " and " + uidFilter;
      
         plans = new SimpleDataSet<AgentPlanData>(AgentPlanData.OBJECT_NAME, false);
         plans.Filter = dateFilter + uidFilter;
         orders = new SimpleDataSet<Order>(Order.OBJECT_NAME, false);
         orders.Filter = createdFilter + uidFilter;

         List<IDataSet> updSet = new List<IDataSet>(new IDataSet[] { plans, orders });
         DataSet<string, Price> dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ?? new DataSet<string, Price>(Price.OBJECT_NAME);
         if (dsPrice.Count == 0)
         {
            dsPrice.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
            updSet.Add(dsPrice);
         }

         DataModule.SetDataRepsonceHandlers(DataProcessed, DataConnectionError);

         btnReport.Enabled = false;
         FmWait.ShowForm(this, DataModule.RefreshGiveSets(MainForm.Instance.conn, updSet, FmWait.ProgressIndicator));
      }

      void DataProcessed(System.Object setnder, EventArgs e)
      {
         DataModule.ClearEvents();
         this.Invoke(new InvokeDelegate(delegate
         {
            FmWait.CloseForm();
            Focus();

            AgentSalesExcelReport rep = new AgentSalesExcelReport();
            rep.Do(plans, orders, data.dateStart, data.dateEnd, data.price);
            rep.Show();
            btnReport.Enabled = true;
         }));
      }

      private void DataConnectionError(EDataResponse e)
      {
         DataModule.ClearEvents();
         Invoke(new EmptyParamHandler(delegate
         {
            FmWait.CloseForm();
            btnReport.Enabled = true;
            MessageBox.Show(e.Msg, "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
         }));
      }
   }

   class AgentSalesExcelReport : Excel
   {
      public AgentSalesExcelReport()
      {
      }

      class SalesData
      {
         public DateTime date;
         public double qty;

         public SalesData(DateTime date, double qty)
         {
            this.date = date;
            this.qty = qty;
         }
      }

      class PriceSalesData : Dictionary<String, List<SalesData>>
      {
      }

      class AgentSalesData : Dictionary<String, PriceSalesData>
      {
         public void Load(SimpleDataSet<Order> orders)
         {
            foreach (Order o in orders.Data)
            {
               PriceSalesData psd = null;
               if( ContainsKey(o.AgentID) )
                  psd = this[o.AgentID];
               else
               {
                  psd = new PriceSalesData();
                  this[o.AgentID] = psd;
               }

               foreach (OrderItem oi in o.items)
               {
                  List<SalesData> salesData = null;
                  if (psd.ContainsKey(oi.id))
                     salesData = psd[oi.id];
                  else
                  {
                     salesData = new List<SalesData>();
                     psd[oi.id] = salesData;
                  }
                  salesData.Add(new SalesData(o.created.Date, oi.qty));
               }
            }
         }

         public double GetItemSales(String agent, String item, DateTime date)
         {
            if (!ContainsKey(agent))
               return 0;
            PriceSalesData psd = this[agent];
            if( !psd.ContainsKey(item) )
               return 0;
            
            double qty = 0;
            foreach (SalesData sd in psd[item])
            {
               if (sd.date.CompareTo(date) == 0)
                  qty += sd.qty;
            }
            return qty;
         }
      }

      const int LAST_TABLE_COLUMN = 3;
      public void Do(SimpleDataSet<AgentPlanData> plans, SimpleDataSet<Order> orders, DateTime start, DateTime end, 
         List<Price> priceFilter)
      {
         List<AgentPlanData> planData = new List<AgentPlanData>(plans.Data as IEnumerable<AgentPlanData>);
         planData.Sort((x, y) => {
            int cmp = x.userid.CompareTo(y.userid);
            return (cmp != 0) ? cmp : x.dateStart.CompareTo(y.dateStart);
         });
         
         DataSet<string, Price> dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME);

         AgentSalesData salesData = new AgentSalesData();
         salesData.Load(orders);

         int cr = 1;
         int startRow = 0;
         String curAgent = null;
         foreach(AgentPlanData apd in planData)
         {
            if (apd.agent == null)
               continue;
            if (curAgent != apd.userid)
            {
               curAgent = apd.userid;
               SetValue(cr, 1, apd.agent.Name);
               SetCellBoldFont(cr, 1, true);
               MergeCells(cr, 1, cr, LAST_TABLE_COLUMN);
               cr++;

               string val = String.Format("Планы за период с {0:dd/MM/yyyy} по {1:dd/MM/yyyy}", start, end);
               SetValue(cr, 1, val);
               SetCellBoldFont(cr, 1, true);
               MergeCells(cr, 1, cr, LAST_TABLE_COLUMN);
               cr += 2;
            }

            bool headerDraw = false;

            foreach (AgentPlanData.Item pi in apd.items)
            {
               if (priceFilter != null && priceFilter.Count > 0 && !priceFilter.Contains(pi.item))
                  continue;

               if (!headerDraw)
               {
                  String text = String.Format("План {0} с {1:dd/MM/yyyy} по {2:dd/MM/yyyy}", apd.Name, apd.Start, apd.End);
                  SetValue(cr, 1, text);
                  MergeCells(cr, 1, cr, LAST_TABLE_COLUMN);
                  cr++;

                  startRow = cr;
                  cr = PrintHeader(cr, apd.agent, start, end);
                  headerDraw = true;
               }
               int cc = 1;
               SetValue(cr, cc++, pi.Name);

               SetValue(cr, cc++, pi.Qty);
               double qty = 0;
               DateTime dt = apd.Start;
               for (; dt.CompareTo(apd.End) <= 0; dt = dt.AddDays(1))
                  qty += salesData.GetItemSales(apd.userid, pi.id, dt.Date);
               
               // Масштабируем на кол-во в упаковке
               if (dsPrice.ContainsKey(pi.id))
                  qty /= dsPrice[pi.id].inPack;

               SetValue(cr, cc, qty);
               cr++;
            }

            if (headerDraw)
            {
               SetBordersOnRange(startRow, 1, cr - 1, LAST_TABLE_COLUMN, xlContinuous);
               cr++;
            }
         }

         AutoFit(1);
      }

      private int PrintHeader(int cr, Agent agent, DateTime start, DateTime end)
      {
         int cc = 1;
         object cell = GetCell(cr, cc++);
         SetValue(cell, "Товар");
         SetCellBoldFont(cell, true);
         SetCellHorizontalAlign(cell, xlCenter);
         SetCellVerticalAlign(cell, xlCenter);

         cell = GetCell(cr, cc++);
         SetValue(cell, "План");
         SetCellBoldFont(cell, true);
         SetCellHorizontalAlign(cell, xlCenter);
         SetCellVerticalAlign(cell, xlCenter);

         cell = GetCell(cr, cc++);
         SetValue(cell, "Факт");
         SetCellBoldFont(cell, true);
         SetCellHorizontalAlign(cell, xlCenter);
         SetCellVerticalAlign(cell, xlCenter);

         SetBordersOnRange(cr, 1, cr, cc - 1, xlContinuous);
         cr++;
         return cr;
      }

      internal void Show()
      {
         Visible = true;
      }
   }

   class AgentSalesPlanReportData
   {
      public DateTime dateStart;
      public DateTime dateEnd;

      public List<Agent> agents = new List<Agent>();
      public List<Price> price = new List<Price>();

      public String AgentText
      {
         get
         {
            String ret = "";
            foreach(Agent a in agents)
            {
               if (ret.Length > 150)
                  break;
               if (ret.Length > 0)
                  ret += ",";
               ret += a.Name;
            }
            if (ret.Length > 0)
               ret += "...";
            return ret;
         }
      }

      public String PriceText
      {
         get
         {
            String ret = "";
            foreach (Price p in price)
            {
               if (ret.Length > 150)
                  break;
               if (ret.Length > 0)
                  ret += ",";
               ret += p.Name;
            }
            if (ret.Length > 0)
               ret += "...";
            return ret;
         }
      }

      public AgentSalesPlanReportData()
      {
         dateStart = DateTime.Now.Date.AddDays(-7);
         dateEnd = DateTime.Now.Date.AddDays(7);
      }
   }
}
