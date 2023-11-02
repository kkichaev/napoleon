/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Отчет по заявкам
 * 
 * kki   21/12/2010   creating
 */
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using GRSoft.NapoleonManager.Reports.Excel;
using GRSoft.NapoleonManager.Reports;
using GRSoft.NapoleonManager.Reports.Html;
using System.Reflection;

namespace GRSoft.NapoleonManager
{
   public partial class FmOrdersReport : Form
   {
      protected static FmOrdersReport instance;
      private DivisionList dsDivision = DivisionList.GetDataSet();
      private DataSet<int, Order> dsOrder;
      private DataSet<int, Delivery> dsDelivery;
      private Division curDivision;
      private DataSet<string, Price> dsPrice;
      private DataSet<string, ManagerFolder> dsManagerFolder;

      public FmOrdersReport()
      {
         InitializeComponent();

#if PoultryNSib || TDLider || Antonov || MeatAlliance
         cbPackets.Text = "Килограммы";
#endif
         Visible = false;
         InitDataSets();

         dtpBegin.Value = DateTime.Now;
         dtpEnd.Value = DateTime.Now;

         Manager m = CurrentUser.user as Manager;
         if (m != null)
         {
            foreach (Division.DivisionAgent a in m.Division.GetAllAgents())
               if (a.agent != null && cbAgents.Items.Contains(a.agent) == false)
                  cbAgents.Items.Add(a.agent);

            cbDivisions.Items.Add(m.Division);
            foreach (Division d in m.Childs)
               cbDivisions.Items.Add(d);
         }

         rbDivision_Click(rbDivision, EventArgs.Empty);
      }

      private void InitDataSets()
      {
         dsOrder = (DataSet<int, Order>)DataModule.Get(Order.OBJECT_NAME) ??
            new DataSet<int, Order>(Order.OBJECT_NAME);
         dsDelivery = (DataSet<int, Delivery>)DataModule.Get(Delivery.OBJECT_NAME) ??
            new DataSet<int, Delivery>(Delivery.OBJECT_NAME);
         dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ?? 
            new DataSet<string, Price>(Price.OBJECT_NAME);
         dsManagerFolder = (DataSet<string, ManagerFolder>)DataModule.Get(ManagerFolder.OBJECT_NAME) ??
            new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME);
      }

      public static void ShowInstance(Division division)
      {
         if (instance == null)
         {
            Type prcType = FormEntries.GetFormType(typeof(FmOrdersReport));
            ConstructorInfo ci = prcType.GetConstructor(BindingFlags.Instance | BindingFlags.NonPublic | BindingFlags.Public, null, new Type[]{} ,null);
            instance = (FmOrdersReport)ci.Invoke(new object[] { });
            instance.curDivision = division;
            instance.Show();
         }
         else
            instance.Activate();
      }

      //Запрос к базе данных на получения списка подразделений
      private void UpdateSets()
      {
         List<IDataSet> updSets = new List<IDataSet>();
         AddUpdatableSets(updSets);
         DataModule.SetDataRepsonceHandlers(FirstTimeDataProcessed, DataConnectionError);
         FmWait.ShowForm(this, DataModule.RefreshGiveSets(
            Config.GetConfig().GetConnection(), updSets, FmWait.ProgressIndicator));
      }

      virtual protected void AddUpdatableSets(List<IDataSet> updSets)
      {
         updSets.Add(dsDivision);
      }

      //Конец запроса
      private void EndOfDataReceive()
      {
         FmWait.CloseForm();
         DataModule.ClearEvents();
      }

      virtual protected void FillControls()
      {
         cbDivisions.Items.Clear();
         dsDivision.CheckAgents();

         DivisionItem di = null;

         foreach (Division division in dsDivision.Data)
         {
            DivisionItem cdi = new DivisionItem(division);
            cbDivisions.Items.Add(cdi);

            if (division.Equals(curDivision))
               di = cdi;
         }

         if (di != null)
            cbDivisions.SelectedItem = di;
         else if (cbDivisions.Items.Count > 0)
            cbDivisions.SelectedIndex = 0;

         Visible = true;
      }

      //Обработка данных необходимых для начального вывода формы
      private void FirstTimeDataProcessed(object o, EventArgs e)
      {
         EndOfDataReceive();

         Invoke(new EmptyParamHandler(delegate
         {
            FillControls();
         }));
      }

      //Произошла ошибка в соединении
      private void DataConnectionError(EDataResponse e)
      {
         EndOfDataReceive();

         Invoke(new EmptyParamHandler(delegate
         {
            Visible = false;
            const string TITLE = "Ошибка";

            MessageBox.Show(e.Msg, TITLE, MessageBoxButtons.OK,
               MessageBoxIcon.Error);

            Close();
         }));
      }

      private void FmOrdersReport_Load(object sender, EventArgs e)
      {
         UpdateSets();
      }

      private void FmOrdersReport_FormClosed(object sender, FormClosedEventArgs e)
      {
         instance = null;
      }

      private void cbPeriod_CheckedChanged(object sender, EventArgs e)
      {
         dtpEnd.Enabled = ((CheckBox)sender).Checked;
      }

      private DateTime GetEndPeriod()
      {
         return cbPeriod.Checked
            ? dtpEnd.Value.Date.AddDays(1)
            : dtpBegin.Value.Date.AddDays(1);
      }

      private void btnExcelReport_Click(object sender, EventArgs e)
      {
         CreateReport(ExcelReportDataProcessed);
      }

      private void setWarning(string warning, Control control)
      {
         MessageBox.Show(String.Format("Выберите {0}!", warning));
         control.Focus();
      }

      virtual protected string GetOrderFilter()
      {
         string AGENTS_FILTER = rbDivision.Checked ?
            FmMessageHistory.UserIdIsStr(((DivisionItem)cbDivisions.SelectedItem).Agents) :
            String.Format("\"userid\" in ('{0}')", (cbAgents.SelectedItem as Agent).id);

         if (AGENTS_FILTER.Length == 0)
         {
            MessageBox.Show("Выбранное подразделение не содержит агентов");
            return string.Empty;
         }

         string orderQryField = "date";

         if (rbByCreatedFld.Checked)
            orderQryField = "created";

         string DATA_FILTER = String.Format("\"{0}\" >= ToDate('{1:dd/MM/yyyy}') and \"{0}\" < ToDate('{2:dd/MM/yyyy}')",
            orderQryField, dtpBegin.Value.Date, GetEndPeriod());

         string FULL_FILTER = String.Format("{0} and {1}",
            AGENTS_FILTER, DATA_FILTER);

         return FULL_FILTER;
      }

      virtual protected string GetDeliveryFilter()
      {
         string AGENTS_FILTER = rbDivision.Checked ?
            FmMessageHistory.UserIdIsStr(((DivisionItem)cbDivisions.SelectedItem).Agents) :
            String.Format("\"userid\" in ('{0}')", (cbAgents.SelectedItem as Agent).id);

         return AGENTS_FILTER;
      }

      private void CreateReport(EventHandler reportDataProcessed)
      {
         if (rbAgents.Checked && (cbAgents.SelectedItem as Agent) == null)
         {
            setWarning("агента", cbAgents);
            return;
         }
         else if (rbDivision.Checked && (cbDivisions.SelectedItem as DivisionItem) == null)
         {
            setWarning("подразделение", cbAgents);
            return;
         }

         dsOrder.Filter = GetOrderFilter();
         if (string.Empty == dsOrder.Filter)
         {
            MessageBox.Show("Выбранное подразделение не содержит агентов");
            return;
         }
         dsDelivery.Filter = GetDeliveryFilter();

         List<IDataSet> updSets = new List<IDataSet>();

         if (dsPrice.Count == 0)
         {
#if Vyatich
#else
            dsPrice.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
#endif
            updSets.Add(dsPrice);
         }

         if (dsManagerFolder.Count == 0)
         {
            dsManagerFolder.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
            updSets.Add(dsManagerFolder);
         }

         if (rbDivision.Checked)
            foreach (Division.DivisionAgent a in ((DivisionItem)cbDivisions.SelectedItem).Agents)
            {
               if (a.agent == null)
                  continue;
               DataSet<string, Org> dsOrg = DataModule.GetUserDataSet(a.id, "Org", typeof(DataSet<string, Org>)) as DataSet<string, Org>;

               if (dsOrg.Count == 0)
               {
                  dsOrg.Command = new ServerCommand(Commands.Impersonate(Commands.GET, a.id), dsOrg.Name);
                  updSets.Add(dsOrg);
               }
            }
         else
         {
            Agent a = cbAgents.SelectedItem as Agent;
            DataSet<string, Org> dsOrg = DataModule.GetUserDataSet(a.id, "Org", typeof(DataSet<string, Org>)) as DataSet<string, Org>;

            if (dsOrg.Count == 0)
            {
               dsOrg.Command = new ServerCommand(Commands.Impersonate(Commands.GET, a.id), dsOrg.Name);
               updSets.Add(dsOrg);
            }
         }


         updSets.Add(dsOrder);
         updSets.Add(dsDelivery);

         DataModule.SetDataRepsonceHandlers(reportDataProcessed,
            DataConnectionError);

         FmWait.ShowForm(this, DataModule.RefreshGiveSets(
            Config.GetConfig().GetConnection(),
            updSets, FmWait.ProgressIndicator));
      }

      protected virtual string MakeFilterStr()
      {
         return String.Format("период с {0} по {1} ", dtpBegin.Value.Date.ToShortDateString(),
            dtpEnd.Value.Date.ToShortDateString());
      }

      private void ExcelReportDataProcessed(object o, EventArgs e)
      {
         EndOfDataReceive();
         Invoke(new EmptyParamHandler(
            delegate 
            {
               OrderReportOptions options = new OrderReportOptions();
               PrepareDataForReport(out options.data);
               options.folders = dsManagerFolder;
               options.itemType = GetOrderType();
#if REPORT_INCLUDE_DELIVERIES
               options.includeDeliveries = cbDelivery.Checked;
#endif
               options.division = (DivisionItem)cbDivisions.SelectedItem;
               options.agent = cbAgents.SelectedItem as Agent;
               options.begin = dtpBegin.Value.Date;
               options.end = dtpEnd.Value.Date;
               options.onlyTotal = OnlyTotalData();
               options.filter = MakeFilterStr();
               IReport report = new OrderReport(options, new ExcelOrderReport());
               report.Build();
               report.Show();
            }));
      }

      //Возвращает тип данных по которому будет строиться отчет
      private ItemType GetOrderType()
      {
         if (cbPiece.Checked == true && cbPackets.Checked == false)
            return ItemType.itPiece;
         else if (cbPackets.Checked == true && cbPiece.Checked == false)
            return ItemType.itPackets;
         else
            return ItemType.itBoth;
      }

      protected virtual bool InSet(Price p) { return true; } 

      //Формировать структуру данных для отчета
      private void PrepareDataForReport(out DataPrice data)
      {
         data = new DataPrice();

#if Kolpakov
         Dictionary<string, Price> priceHolder = new Dictionary<string, Price>();
#endif

         foreach (Order order in dsOrder.Data)
         { 
            if (order.items == null || order.org == null)
               continue;

            foreach (OrderItem orderItem in order.items)
            {
#if Kolpakov
               if (priceHolder.ContainsKey(orderItem.item.ido))
                  orderItem.item = priceHolder[orderItem.item.ido];
               else
               {
                  Price np = new Price();
                  System.Reflection.FieldInfo[] flds = np.GetType().GetFields(System.Reflection.BindingFlags.Public | System.Reflection.BindingFlags.Instance);
                  foreach(System.Reflection.FieldInfo fi in flds)
                  {
                     try
                     {
                        fi.SetValue(np, fi.GetValue(orderItem.item));
                     }
                     catch (Exception)
                     {

                     }
                  }
                  priceHolder[orderItem.item.ido] = np;
                  orderItem.item = np;
                  np.id = np.ido;
               }
#endif
               if (!InSet(orderItem.item))
                  continue;

               Price curPrice = orderItem.item;
               if (!data.ContainsKey(curPrice))
                  data.Add(curPrice, new DataOrgPrice());

               DataOrgPrice orgsVal = data[curPrice];

               double val = orderItem.qty;
               double packs = curPrice.inPack == 0 ? 0 : Math.Round(orderItem.qty / curPrice.inPack);
               if (!orgsVal.ContainsKey(order.org))
               {
                  orgsVal.Add(order.org, new PricesStruct());
               }
               PricesStruct prices = orgsVal[order.org];
               int priceIndex = 0;
               val += prices.GetValue(priceIndex);
               prices.SetValue(priceIndex++, val);

#if PoultryNSib || Antonov || MeatAlliance
               prices.SetValue(priceIndex, val * curPrice.weight);
#else
               packs += prices.GetValue(priceIndex);
               prices.SetValue(priceIndex++, packs);
#endif
#if REPORT_INCLUDE_DELIVERIES
               foreach (Delivery delivery in dsDelivery.Data)
               {
                  if (delivery.id != order.org.id)
                     continue;

                  foreach (DeliveryItem deliveryItem in delivery.items)
                  {
                     if (deliveryItem.id != orderItem.id)
                        continue;

                     double dlvVal = deliveryItem.qty;
                     dlvVal += prices.GetValue(priceIndex);
                     prices.SetValue(priceIndex, dlvVal);

                     double dlvPacks = curPrice.inPack == 0 ? 0 : Math.Round(dlvVal / curPrice.inPack);
                     dlvPacks += prices.GetValue(priceIndex);
                     prices.SetValue(priceIndex+1, packs);
                  }
               }
               priceIndex = 2;
#endif
            }
         }
      }

      private void cbPiece_CheckStateChanged(object sender, EventArgs e)
      {
         if (sender == cbPiece &&
               cbPiece.Checked == false)
         {
            cbPackets.Checked = true;
         }
         else if (sender == cbPackets &&
                  cbPackets.Checked == false)
         {
            cbPiece.Checked = true;
         }
      }

      /* Сформировать отчет в HTML */
      private void btnHtml_Click(object sender, EventArgs e)
      {
         CreateReport(HTMLReportDataProcessed);
      }

      protected virtual bool OnlyTotalData() { return false; }

      private void HTMLReportDataProcessed(object o, EventArgs e)
      {
         EndOfDataReceive();
         Invoke(new EmptyParamHandler(
            delegate
            {
               OrderReportOptions options = new OrderReportOptions();
               PrepareDataForReport(out options.data);
               options.folders = dsManagerFolder;
               options.itemType = GetOrderType();
#if REPORT_INCLUDE_DELIVERIES
               options.includeDeliveries = cbDelivery.Checked;
#endif
               options.division = (DivisionItem)cbDivisions.SelectedItem;
               options.agent = cbAgents.SelectedItem as Agent;
               options.begin = dtpBegin.Value.Date;
               options.end = dtpEnd.Value.Date;
               options.onlyTotal = OnlyTotalData();
               options.filter = MakeFilterStr();
               IReport report = new OrderReport(options, new HTMLOrderReport());
               report.Build();
               report.Show();

            }));
      }

      private void rbDivision_Click(object sender, EventArgs e)
      {
         cbAgents.Enabled = false;
         cbDivisions.Enabled = true;
      }

      private void rbAgents_Click(object sender, EventArgs e)
      {
         cbAgents.Enabled = true;
         cbDivisions.Enabled = false;
      }
   }
}