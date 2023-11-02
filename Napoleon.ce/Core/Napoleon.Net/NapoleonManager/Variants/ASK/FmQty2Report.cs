using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Reports.Excel;

namespace GRSoft.NapoleonManager
{
   public partial class FmQty2Report : Form
   {
      public DataSet<int, Order> dsOrder;
      public DataSet<string, Price> dsPrice;
      protected const string FILTER_STR = "\"{0}\" >= ToDate('{1:dd/MM/yyyy}') and \"{0}\" < ToDate('{2:dd/MM/yyyy}')";

      public FmQty2Report()
      {
         InitializeComponent();

         dsOrder = (DataSet<int, Order>)DataModule.Get(Order.OBJECT_NAME) ?? 
            new DataSet<int, Order>(Order.OBJECT_NAME);
         dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ?? 
            new DataSet<string, Price>(Price.OBJECT_NAME);

         Manager m = CurrentUser.user as Manager;
         if (m != null)
         {
            List<Agent> list = new List<Agent>();

            foreach (Division.DivisionAgent a in m.Division.GetAllAgents())
               if (a.agent != null && cbAgents.Items.Contains(a.agent) == false)
                  list.Add(a.agent);

            list.Sort(new Comparison<Agent>(delegate (Agent lhs, Agent rhs) {return lhs.name.CompareTo(rhs.name);}));
            foreach(Agent a in list)
               cbAgents.Items.Add(a);
            
            cbDivisions.Items.Add(m.Division);
            foreach (Division d in m.Childs)
               cbDivisions.Items.Add(d);
         }

         cbAgents.Enabled = false;
         cbDivisions.Enabled = false;
      }

      private void btnExcel_Click(object sender, EventArgs e)
      {
         if (rbDivision.Checked && cbDivisions.SelectedIndex == -1)
            return;

         if (rbAgents.Checked && cbAgents.SelectedIndex == -1)
            return;

         DataModule.SetDataRepsonceHandlers(DataProcessed, DataConnectionError);

         StringBuilder filter = new StringBuilder();
         filter.Append(String.Format(FILTER_STR, "created", dtpStart.Value.Date, dtpFinish.Value.Date.AddDays(1)));

         String agentWhere = AgentWhere();

         if (agentWhere.Length > 0)
            filter.Append(" and ").Append(agentWhere);

         dsOrder.Filter = filter.ToString();

         List<IDataSet> updSets = new List<IDataSet>();

         if (dsPrice.Count == 0)
         {
            updSets.Add(dsPrice);
         }
         
         updSets.Add(dsOrder);

         FmWait.ShowForm(this, DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), 
            updSets, FmWait.ProgressIndicator));
      }

      //Окончание выборки, заполняются внутренние наборы
      void DataProcessed(System.Object setnder, EventArgs e)
      {
         DataModule.ClearEvents();

         this.Invoke(new InvokeDelegate(delegate
         {
            FmWait.CloseForm();
            Report();
            btnExcel.Enabled = true;
         }));
      }

      private void Report()
      {
         new Qty2Report().Show(this);
      }

      //Произошла ошибка в соединении
      private void DataConnectionError(EDataResponse e)
      {
         DataModule.ClearEvents();

         Invoke(new EmptyParamHandler(delegate
         {
            FmWait.CloseForm();
            btnExcel.Enabled = true;

            const string TITLE = "Ошибка";

            MessageBox.Show(e.Msg, TITLE, MessageBoxButtons.OK,
               MessageBoxIcon.Error);
         }));
      }

      private void rbAll_CheckedChanged(object sender, EventArgs e)
      {
         cbAgents.Enabled = false;
         cbDivisions.Enabled = false;

         if (rbAgents.Checked)
            cbAgents.Enabled = true;
         else if (rbDivision.Checked)
            cbDivisions.Enabled = true;
      }

      private String AgentWhere()
      {
         StringBuilder result = new StringBuilder();

         if (rbAgents.Checked && cbAgents.SelectedItem != null)
         {
            Agent agent = cbAgents.SelectedItem as Agent;
            result.Append("\"userid\" = '").Append(agent.id).Append("'");
         }
         else if (rbDivision.Checked && cbDivisions.SelectedItem != null)
         {
            Division division = cbDivisions.SelectedItem as Division;

            if (division != null)
            {
               List<GRSoft.NapoleonManager.Division.DivisionAgent> agents = division.GetAllAgents();

               if (agents.Count > 0)
               {
                  result.Append("\"userid\" in (");

                  List<GRSoft.NapoleonManager.Division.DivisionAgent>.Enumerator iter = agents.GetEnumerator();
                  List<string> ids = new List<string>();

                  while (iter.MoveNext())
                  {
                     GRSoft.NapoleonManager.Division.DivisionAgent agent = iter.Current;

                     if (agent != null)
                        ids.Add(String.Format("'{0}'", agent.id));
                  };

                  result.Append(String.Join(",", ids.ToArray()));
                  result.Append(")");
               }
            }
         }

         return result.ToString();
      }
   }

   class Qty2Report : Excel
   { 
      public void Show(FmQty2Report data)
      {
         summarySheet(data);
         detailSheet(data);
         SelectSheet(2);
         Visible = true;
      }

      private void summarySheet(FmQty2Report data)
      {
         Dictionary<string, double> dic = new Dictionary<string, double>();

         foreach (Order o in data.dsOrder.Values)
         {
            foreach (OrderItem i in o.items)
            {
               if (i.qty2 <= 0)
                  continue;

               if (!dic.ContainsKey(i.id))
                  dic[i.id] = i.qty2;
               else
                  dic[i.id] += i.qty2;
            }
         }

         Title(data);

         SetValue(2, 1, "SKU");
         SetValue(2, 2, "Заказ");

         int row = 3;
         double sum = 0;

         foreach (KeyValuePair<string, double> pair in dic)
         {
            String sku = string.Empty;

            if (data.dsPrice.ContainsKey(pair.Key))
               sku = data.dsPrice[pair.Key].Name;
            else
               sku = String.Format("Код объекта <{0}> не найден", pair.Key);

            SetValue(row, 1, sku);
            SetValue(row, 2, pair.Value);
            row++;

            sum += pair.Value;
         }

         SetValue(row, 1, "Итого:");
         SetValue(row, 2, sum);

         SetColumnWidth(1, 33);
         SetColumnWidth(2, 25);
         SetSheetName(ActiveSheet, "Суммарно");
      }

      
      private void detailSheet(FmQty2Report data)
      {
         SetSheetName(AddSheet(), "Подробно");
         
         Title(data);
         SetValue(2, 1, "Дата");
         SetValue(2, 2, "SKU");
         SetValue(2, 3, "Заказ");
         SetValue(2, 4, "Комментарий");

         SetColumnWidth(1, 20);
         SetColumnWidth(2, 50);
         SetColumnWidth(3, 10);
         SetColumnWidth(4, 50);

         DetailData dd = new DetailData(data);

         int i = 3;

         foreach (DetailDataRow ddr in dd.items)
         {
            SetValue(i, 1, ddr.created);
            SetValue(i, 2, ddr.item);
            SetWrapeText(i, 2, true);
            SetValue(i, 3, ddr.qty);
            SetValue(i, 4, ddr.remark);
            SetWrapeText(i, 4, true);
            i++;
         }
      }

      class DetailData
      {
         public List<DetailDataRow> items = new List<DetailDataRow>();

         public DetailData(FmQty2Report data)
         { 
            List<Order> order = new List<Order>();
            order.AddRange(data.dsOrder.Values);
            order.Sort((lhs, rhs) => { return lhs.created.CompareTo(rhs.created); });

            foreach (Order o in order)
            {
               o.items.Sort((lhs, rhs) => { return lhs.Item.CompareTo(rhs.Item); });

               foreach (OrderItem i in o.items)
                  if (i.qty2 > 0)
                     AddRow(o, i);
            }
         }

         private void AddRow(Order o, OrderItem i)
         {
            DetailDataRow ddr = new DetailDataRow();
            ddr.created = o.created.ToString("dd/MM/yyyy");
            ddr.item = i.Item;
            ddr.remark = i.remark;
            ddr.qty = i.qty2;

            items.Add(ddr);
         }
      }

      class DetailDataRow
      {
         public String created;
         public String item;
         public String remark;
         public double qty;
      }

      private void Title(FmQty2Report data)
      {
         StringBuilder title = new StringBuilder();

         if (data.rbAgents.Checked)
            title.Append("ТП: ").Append((data.cbAgents.SelectedItem as Agent).Name);
         else if (data.rbDivision.Checked)
            title.Append("Подразделение: ").Append((data.cbDivisions.SelectedItem as Division).name);

         if (title.Length > 0)
            title.Append(" ");

         title.Append("период: с ").Append(data.dtpStart.Value.Date.ToString("dd.MM.yyyy"))
            .Append(" по ").Append(data.dtpFinish.Value.Date.ToString("dd.MM.yyyy"));

         SetValue(1, 1, title.ToString());
      }
   }
}
