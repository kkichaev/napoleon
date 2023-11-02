using GRSoft.NapoleonManager.Reports.Excel;
using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmAvalonSalesReport : Form
   {
      public static readonly int ORG_REPORT_MASK = 1;
      public static readonly int PRICE_REPORT_MASK = 2;
      public static readonly int DETAIL_REPORT_MASK = 4;

      string moneyID = "";

      SimpleDataSet<VandAudit> audits = new SimpleDataSet<VandAudit>(VandAudit.OBJECT_NAME);
      SimpleDataSet<VandSales> sales = new SimpleDataSet<VandSales>(VandSales.OBJECT_NAME);
      SimpleDataSet<VandAudit> auditsAdd = new SimpleDataSet<VandAudit>(VandAudit.OBJECT_NAME);
      SimpleDataSet<VandSales> salesAdd = new SimpleDataSet<VandSales>(VandSales.OBJECT_NAME);
      DataSet<string, OrderAddConfig> config = new DataSet<string, OrderAddConfig>("Config");

      public FmAvalonSalesReport()
      {
         InitializeComponent();
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);

         if (!MainForm.Instance.CheckIsMainDataPresents(false))
            button1.Enabled = false;
      }

      private void button1_Click(object sender, EventArgs e)
      {
         button1.Enabled = false;
         List<IDataSet> upd = new List<IDataSet>();

         DateTime start = dtpStart.Value.Date;
         DateTime end = dtpEnd.Value.Date.AddDays(1);

         Agents agents = ((Manager)CurrentUser.user).GetAgents();
         string uid = DataUtils.MakeFilterFromAgents(null, agents);

         foreach (Agent a in ((Manager)CurrentUser.user).GetAgents().Data)
         {
            DataSet<string, Org> ao = DataModule.GetUserDataSet(a.id, Org.OBJECT_NAME, typeof(DataSet<string, Org>)) as DataSet<string, Org>;
            if (ao.Count == 0)
            {
               ao.Command = new ServerCommand(Commands.Impersonate(Commands.GET, a.id), ao.Name);
               upd.Add(ao);
            }

            DataSet<string, Price> ap = DataModule.GetUserDataSet(a.id, Price.OBJECT_NAME, typeof(DataSet<string, Price>)) as DataSet<string, Price>;
            if (ap.Count == 0)
            {
               ap.Command = new ServerCommand(Commands.Impersonate(Commands.GET, a.id), ap.Name);
               upd.Add(ap);
            }
         }

         audits.Filter = uid + String.Format(" and \"created\"=(select max(\"created\") from audit as a1 where a1.id = audit.id and a1.created < ToDate('{0:dd/MM/yyyy}'))", start);
         sales.Filter = uid + String.Format(" and \"created\" > (select max(\"created\") from audit as a1 where a1.id = vandsell.id and a1.created < ToDate('{0:dd/MM/yyyy}')) and \"created\" < ToDate('{1:dd/MM/yyyy}')", start, end);

         auditsAdd.Filter = uid + String.Format(" and \"created\"=(select min(\"created\") from audit as a1 where a1.id = audit.id and a1.created > ToDate('{0:dd/MM/yyyy}') and a1.created < ToDate('{1:dd/MM/yyyy}'))", start, end);
         salesAdd.Filter = uid + String.Format(" and \"created\" > (select min(\"created\") from audit as a1 where a1.id = vandsell.id and a1.created > ToDate('{0:dd/MM/yyyy}') and a1.created < ToDate('{1:dd/MM/yyyy}')) and \"created\" < ToDate('{1:dd/MM/yyyy}')", start, end);

         upd.Add(audits);
         upd.Add(sales);
         upd.Add(auditsAdd);
         upd.Add(salesAdd);
         upd.Add(config);

         DataModule.DataProcessed += new EventHandler((o, e1) =>
         {
            DataModule.ClearEvents();

            try
            {
               Invoke(new EmptyParamHandler(delegate { button1.Enabled = true; }));
               DoReport();
            }
            catch (Exception)
            {
            }
         });

         DataModule.OnDataResponceError += new EventDataResponseError((e1) =>
         {
            try
            {
               Invoke(new EmptyParamHandler(delegate { button1.Enabled = true; }));
            }
            catch (Exception)
            {
            }
            FmWait.StdErrorHandler(e1);
         });

         FmWait.ShowForm(this, DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), upd, FmWait.ProgressIndicator));
      }

      void DoReport()
      {
         moneyID = "";
         if(config.ContainsKey("РазменнаяМонета"))
         {
            string val = config["РазменнаяМонета"].value;
            int idx = val.IndexOf('\t');

            moneyID = idx >= 0 ? val.Substring(idx+1) : val;
         }

         Dictionary<string, bool> used = new Dictionary<string,bool>();
         List<Org> orgs = new List<Org>();
         foreach (Agent a in ((Manager)CurrentUser.user).GetAgents().Data)
         {
            DataSet<string, Org> ao = DataModule.GetUserDataSet(a.id, Org.OBJECT_NAME, typeof(DataSet<string, Org>)) as DataSet<string, Org>;
            foreach(Org o in ao.Data)
               if( used.ContainsKey(o.id) == false)
               {
                  orgs.Add(o);
                  used[o.id] = true;
               }
         }
         orgs.Sort();

         FmWait.ProgressIndicator.SetText("Построение отчета");

         DateTime start = dtpStart.Value.Date;
         DateTime end = dtpEnd.Value.Date.AddDays(1);
         
         ShortOrgData od = new ShortOrgData(start, end, moneyID);
         ShortPriceData pd = new ShortPriceData(start, end, moneyID);
         DetailReportData dd = new DetailReportData(start, end, moneyID);

         Dictionary<string, bool> usedId = new Dictionary<string, bool>();
         Dictionary<string, bool> addedId = new Dictionary<string, bool>();
         foreach (VandAudit va in audits.Data)
         {
            usedId[va.id] = true;

            if (va.Date.AddMonths(1) < start)
               continue;

            od.AddStart(va);
            pd.AddStart(va);
            dd.AddStart(va);
         }

         foreach(VandSales vs in sales.Data)
         {
            od.AddSale(vs);
            pd.AddSale(vs);
            dd.AddSale(vs);
         }

         foreach(VandAudit va in auditsAdd.Data)
         {
            if (!usedId.ContainsKey(va.id))
            {
               if (va.created < start || va.created > end)
                  continue;

               od.AddStart(va);
               pd.AddStart(va);
               dd.AddStart(va);
               addedId[va.id] = true;
            }
         }

         foreach (VandSales vs in salesAdd.Data)
         {
            if (addedId.ContainsKey(vs.id))
            {
               od.AddSale(vs);
               pd.AddSale(vs);
               dd.AddSale(vs);
            }
         }

         int mask = 0;
         if (cbOrg.Checked)
            mask |= ORG_REPORT_MASK;
         if (cbPrice.Checked)
            mask |= PRICE_REPORT_MASK;
         if (cbDetail.Checked)
            mask |= DETAIL_REPORT_MASK;

         SalesReportBuilder rb = new SalesReportBuilder();
         rb.Build(od, pd, dd, mask, orgs, FmWait.ProgressIndicator);
         rb.Visible = true;

         FmWait.CloseForm();
      }
   }

   class SalesReportBuilder : Excel
   {
      const double COLUMN_WIDTH = 9;
      const int FONT_SIZE = 10;

      public SalesReportBuilder() { }

      public void Build(ShortOrgData od, ShortPriceData pd, DetailReportData dd, int reports, List<Org> orgs, IProgress progress)
      {
         bool haveReport = false;

         int count = 0;
         if ((reports & FmAvalonSalesReport.ORG_REPORT_MASK) != 0)
            count += od.items.Count;
         if ((reports & FmAvalonSalesReport.PRICE_REPORT_MASK) != 0)
            count += pd.items.Count;
         if ((reports & FmAvalonSalesReport.DETAIL_REPORT_MASK) != 0)
            count += dd.items.Count;

         progress.SetMax(count);
         if ((reports & FmAvalonSalesReport.ORG_REPORT_MASK) != 0)
         {
            DrawOrgReport(ActiveSheet, od, orgs, progress);
            haveReport = true;
         }
         
         if ((reports & FmAvalonSalesReport.PRICE_REPORT_MASK) != 0)
         {
            DrawPriceReport(haveReport ? AddSheet() : ActiveSheet, pd, progress);
            haveReport = true;
         }

         if ((reports & FmAvalonSalesReport.DETAIL_REPORT_MASK) != 0)
         {
            DrawDetailReport(haveReport ? AddSheet() : ActiveSheet, dd, orgs, progress);
            haveReport = true;
         }
      }

      void MakeHead(object sheet, DateTime start, DateTime end)
      {
         object curCell = GetCell(1, 1);
         SetValue(curCell, "Avalon Distribution");
         SetFontSize(curCell, 16);
         SetCellBoldFont(curCell, true);

         curCell = GetCell(2, 1);
         SetValue(curCell, String.Format("Отчёт о продажах за период с {0:dd/MM/yyyy} по {1:dd/MM/yyyy}", start, end.AddDays(-1)));
         SetFontSize(curCell, 11);
      }

      void MakeSTDHead(int cr, int cc)
      {
         object cell;

         cell = GetCell(cr, cc);
         SetValue(cell, "Остаток входящий, шт");
         SetWrapeText(cell, true);
         SetCellBoldFont(cell, true);
         SetFontSize(cell, FONT_SIZE);
         SetBackColor(cell, Color.LightGray);
         SetBorders(cell, xlContinuous);
         SetCellHorizontalAlign(cell, xlCenter);
         SetCellVerticalAlign(cell, xlCenter);
         SetColumnWidth(cc, COLUMN_WIDTH);

         cell = GetCell(cr, ++cc);
         SetValue(cell, "Загрузка, шт");
         SetWrapeText(cell, true);
         SetCellBoldFont(cell, true);
         SetFontSize(cell, FONT_SIZE);
         SetBackColor(cell, Color.LightGray);
         SetBorders(cell, xlContinuous);
         SetCellHorizontalAlign(cell, xlCenter);
         SetCellVerticalAlign(cell, xlCenter);
         SetColumnWidth(cc, COLUMN_WIDTH);

         cell = GetCell(cr, ++cc);
         SetValue(cell, "Выгрузка, шт");
         SetWrapeText(cell, true);
         SetCellBoldFont(cell, true);
         SetFontSize(cell, FONT_SIZE);
         SetBackColor(cell, Color.LightGray);
         SetBorders(cell, xlContinuous);
         SetCellHorizontalAlign(cell, xlCenter);
         SetCellVerticalAlign(cell, xlCenter);
         SetColumnWidth(cc, COLUMN_WIDTH);

         cell = GetCell(cr, ++cc);
         SetValue(cell, "Продажи, шт");
         SetWrapeText(cell, true);
         SetCellBoldFont(cell, true);
         SetFontSize(cell, FONT_SIZE);
         SetBackColor(cell, Color.Yellow);
         SetBorders(cell, xlContinuous);
         SetCellHorizontalAlign(cell, xlCenter);
         SetCellVerticalAlign(cell, xlCenter);
         SetColumnWidth(cc, COLUMN_WIDTH);

         cell = GetCell(cr, ++cc);
         SetValue(cell, "Продажи, руб");
         SetWrapeText(cell, true);
         SetCellBoldFont(cell, true);
         SetFontSize(cell, FONT_SIZE);
         SetBackColor(cell, Color.Yellow);
         SetBorders(cell, xlContinuous);
         SetCellHorizontalAlign(cell, xlCenter);
         SetCellVerticalAlign(cell, xlCenter);
         SetColumnWidth(cc, COLUMN_WIDTH);

         cell = GetCell(cr, ++cc);
         SetValue(cell, "Остаток исходящий, шт");
         SetWrapeText(cell, true);
         SetCellBoldFont(cell, true);
         SetFontSize(cell, FONT_SIZE);
         SetBackColor(cell, Color.LightGray);
         SetBorders(cell, xlContinuous);
         SetCellHorizontalAlign(cell, xlCenter);
         SetCellVerticalAlign(cell, xlCenter);
         SetColumnWidth(cc, COLUMN_WIDTH);
      }

      private void DrawPriceReport(object sheet, ShortPriceData data, IProgress progress)
      {
         SetSheetName(sheet, "Краткий по товару");
         SetSelectedSheet(sheet);
         MakeHead(sheet, data.start, data.end);

         int cr = 4;
         int cc = 1;
         object cell;
         
         cell = GetCell(cr, cc);
         SetValue(cell, "Товар");
         SetCellBoldFont(cell, true);
         SetFontSize(cell, 10);
         SetBackColor(cell, Color.LightGray);
         SetBorders(cell, xlContinuous);
         SetCellHorizontalAlign(cell, xlCenter);
         SetCellVerticalAlign(cell, xlCenter);
         SetColumnWidth(cc, 50);

         MakeSTDHead(cr++, cc + 1);

         progress.SetText("Построение отчета: краткий по товару");
         foreach(KeyValuePair<Price, ReportItem> kv in data.items)
         {
            progress.AdvancePos(1);

            ReportItem ri = kv.Value;
            cc = 1;

            cell = GetCell(cr, cc++);
            SetValue(cell, kv.Key.Name);
            SetBorders(cell, xlContinuous);

            cell = GetCell(cr, cc++);
            SetValue(cell, ri.start);
            SetBorders(cell, xlContinuous);

            cell = GetCell(cr, cc++);
            SetValue(cell, ri.load);
            SetBorders(cell, xlContinuous);

            cell = GetCell(cr, cc++);
            SetValue(cell, ri.unload);
            SetBorders(cell, xlContinuous);

            cell = GetCell(cr, cc++);
            SetValue(cell, ri.sales);
            SetBorders(cell, xlContinuous);

            cell = GetCell(cr, cc++);
            SetValue(cell, ri.sum);
            SetBorders(cell, xlContinuous);

            cell = GetCell(cr, cc++);
            SetValue(cell, ri.Rest);
            SetBorders(cell, xlContinuous);

            cr++;
         }

         string formula = String.Format("=СУММ(R[-1]C:R[-{0}]C)", data.items.Count);
         cc = 1;

         cell = GetCell(cr, cc++);
         SetValue(cell, "Всего");
         SetBorders(cell, xlContinuous);

         for (int i = 0; i < 6; i++)
         {
            cell = GetCell(cr, cc++);
            SetValue(cell, formula);
            SetBorders(cell, xlContinuous);
         }
      }

      private void DrawOrgReport(object sheet, ShortOrgData data, List<Org> orgs, IProgress progress)
      {
         SetSheetName(sheet, "Краткий по автоматам");
         SetSelectedSheet(sheet);
         MakeHead(sheet, data.start, data.end);

         int cr = 4;
         int cc = 1;
         object cell;

         cell = GetCell(cr, cc);
         SetValue(cell, "Автомат");
         SetCellBoldFont(cell, true);
         SetFontSize(cell, 10);
         SetBackColor(cell, Color.LightGray);
         SetBorders(cell, xlContinuous);
         SetCellHorizontalAlign(cell, xlCenter);
         SetCellVerticalAlign(cell, xlCenter);
         SetColumnWidth(cc, 50);

         MakeSTDHead(cr++, cc + 1);

         progress.SetText("Построение отчета: краткий по автоматам");
         //foreach (KeyValuePair<Org, ReportItem> kv in data.items)
         foreach(Org o in orgs)
         {
            progress.AdvancePos(1);

            ReportItem ri = data.items.ContainsKey(o) ? data.items[o] : new ReportItem();

            cc = 1;

            cell = GetCell(cr, cc++);
            SetValue(cell, o.Name);
            SetBorders(cell, xlContinuous);

            cell = GetCell(cr, cc++);
            SetValue(cell, ri.start);
            SetBorders(cell, xlContinuous);

            cell = GetCell(cr, cc++);
            SetValue(cell, ri.load);
            SetBorders(cell, xlContinuous);

            cell = GetCell(cr, cc++);
            SetValue(cell, ri.unload);
            SetBorders(cell, xlContinuous);

            cell = GetCell(cr, cc++);
            SetValue(cell, ri.sales);
            SetBorders(cell, xlContinuous);

            cell = GetCell(cr, cc++);
            SetValue(cell, ri.sum);
            SetBorders(cell, xlContinuous);

            cell = GetCell(cr, cc++);
            SetValue(cell, ri.Rest);
            SetBorders(cell, xlContinuous);

            cr++;
         }

         string formula = String.Format("=СУММ(R[-1]C:R[-{0}]C)", orgs.Count);
         cc = 1;

         cell = GetCell(cr, cc++);
         SetValue(cell, "Всего");
         SetBorders(cell, xlContinuous);

         for (int i = 0; i < 6; i++)
         {
            cell = GetCell(cr, cc++);
            SetValue(cell, formula);
            SetBorders(cell, xlContinuous);
         }
      }

      private void DrawDetailReport(object sheet, DetailReportData data, List<Org> orgs, IProgress progress)
      {
         SetSheetName(sheet, "Развернутый");
         SetSelectedSheet(sheet);
         MakeHead(sheet, data.start, data.end);

         int cr = 4;
         int cc = 1;
         object cell;

         cell = GetCell(cr, cc);
         SetValue(cell, "Автомат/Товар");
         SetCellBoldFont(cell, true);
         SetFontSize(cell, 10);
         SetBackColor(cell, Color.LightGray);
         SetBorders(cell, xlContinuous);
         SetCellHorizontalAlign(cell, xlCenter);
         SetCellVerticalAlign(cell, xlCenter);
         SetColumnWidth(cc, 70);

         cell = GetCell(cr, ++cc);
         SetValue(cell, "Цена");
         SetCellBoldFont(cell, true);
         SetFontSize(cell, 10);
         SetBackColor(cell, Color.LightGray);
         SetBorders(cell, xlContinuous);
         SetCellHorizontalAlign(cell, xlCenter);
         SetCellVerticalAlign(cell, xlCenter);
         SetColumnWidth(cc, COLUMN_WIDTH);

         MakeSTDHead(cr++, cc + 1);

         progress.SetText("Построение отчета: развернутый");

         //foreach (KeyValuePair<Org, DetailReportData.OrgItem> kv in data.items)
         foreach(Org o in orgs)
         {
            progress.AdvancePos(1);

            cc = 1;
            cell = GetCell(cr, cc++);
            SetValue(cell, o.Name);
            SetCellBoldFont(cell, true);
            SetBackColor(cell, Color.LightSkyBlue);
            SetBorders(cell, xlContinuous);

            cell = GetCell(cr, cc++);
            SetCellBoldFont(cell, true);
            SetBackColor(cell, Color.LightSkyBlue);
            SetBorders(cell, xlContinuous);

            DetailReportData.OrgItem oi = data.items.ContainsKey(o) ?  data.items[o] : null;
            if (oi != null && oi.Count > 0)
            {
               string formula = String.Format("=СУММ(R[1]C:R[{0}]C)", oi.Count);
               for (int i = 0; i < 6; i++)
               {
                  cell = GetCell(cr, cc++);
                  SetFormulaR1C1(cell, formula);
                  SetCellBoldFont(cell, true);
                  SetBackColor(cell, Color.LightSkyBlue);
                  SetBorders(cell, xlContinuous);
               }
            }

            cr++;
            if( oi != null )
            {
               foreach (KeyValuePair<Price, DetailReportData.Item> oikv in oi)
               {
                  DetailReportData.Item ri = oikv.Value;
                  cc = 1;

                  cell = GetCell(cr, cc++);
                  SetValue(cell, oikv.Key.Name);
                  SetBorders(cell, xlContinuous);

                  cell = GetCell(cr, cc++);
                  SetValue(cell, ri.cost);
                  SetBorders(cell, xlContinuous);

                  cell = GetCell(cr, cc++);
                  SetValue(cell, ri.start);
                  SetBorders(cell, xlContinuous);

                  cell = GetCell(cr, cc++);
                  SetValue(cell, ri.load);
                  SetBorders(cell, xlContinuous);

                  cell = GetCell(cr, cc++);
                  SetValue(cell, ri.unload);
                  SetBorders(cell, xlContinuous);

                  cell = GetCell(cr, cc++);
                  SetValue(cell, ri.sales);
                  SetBorders(cell, xlContinuous);

                  cell = GetCell(cr, cc++);
                  SetValue(cell, ri.sum);
                  SetBorders(cell, xlContinuous);

                  cell = GetCell(cr, cc++);
                  SetValue(cell, ri.Rest);
                  SetBorders(cell, xlContinuous);

                  cr++;
               }
            }
         }

         AutoOutline(GetCell(5, 2));
      }
   }

   public class ReportItem
   {
      public double start = 0;
      public double load = 0;
      public double unload = 0;
      public double sales = 0;
      public double sum = 0;

      public double Rest { get { return start + load - unload - sales;  } }

      internal void AddData(VandSales.Item i, bool addToStart)
      {
         if (addToStart)
         {
            start += (i.load - i.unload - i.chek);
         }
         else
         {
            load += i.load;
            sum += i.chek * i.cost;
            sales += i.chek;
            unload += i.unload;
         }
      }
   }

   class ReportDataBase
   {
      public DateTime start;
      public DateTime end;
      public string moneyID;

      public ReportDataBase(DateTime start, DateTime end, string moneyID)
      {
         this.start = start;
         this.end = end;
         this.moneyID = moneyID;
      }

      public Org GetOrg(VandAudit doc) { return doc.org == null ? EmptyOrg.Get(doc.id) : doc.org; }
      public Org GetOrg(VandSales doc) { return doc.org == null ? EmptyOrg.Get(doc.id) : doc.org; }

      public static Price GetPrice(ItemBase i) { return i.item == null ? EmptyPrice.Get(i.id) : i.item; }
   }

   class ShortOrgData : ReportDataBase
   {
      public Dictionary<Org, ReportItem> items = new Dictionary<Org, ReportItem>();

      public ShortOrgData(DateTime start, DateTime end, string moneyID) : base(start, end, moneyID) { }

      public void AddStart(VandAudit va)
      {
         ReportItem ri = new ReportItem();
         foreach (VandAudit.Item i in va.items)
         {
            if (i.id == moneyID)
               continue;

            ri.start += i.qty;
         }

         Org o = GetOrg(va);
         items[o] = ri;
      }

      internal void AddSale(VandSales doc)
      {
         Org o = GetOrg(doc);
         if (items.ContainsKey(o) == false)
            items[o] = new ReportItem();

         ReportItem ri = items[o];
         bool addToStart = doc.created < start;
         foreach (VandSales.Item i in doc.items)
            if( i.id != moneyID )
               ri.AddData(i, addToStart);
      }
   }

   class ShortPriceData : ReportDataBase
   {
      public Dictionary<Price, ReportItem> items = new Dictionary<Price, ReportItem>();

      public ShortPriceData(DateTime start, DateTime end, string moneyID) : base(start, end, moneyID) { }

      public void AddStart(VandAudit va)
      {
         foreach (VandAudit.Item i in va.items)
         {
            if( i.id == moneyID )
               continue;

            Price p = GetPrice(i);
            if (items.ContainsKey(p) == false)
               items[p] = new ReportItem();

            items[p].start += i.qty;
         }
      }

      internal void AddSale(VandSales doc)
      {
         bool addToStart = doc.created < start;
         foreach (VandSales.Item i in doc.items)
         {
            if( i.id == moneyID )
               continue;

            Price p = GetPrice(i);
            if (items.ContainsKey(p) == false)
               items[p] = new ReportItem();

            ReportItem ri = items[p];
            ri.AddData(i, addToStart);
         }
      }
   }

   class DetailReportData : ReportDataBase
   {
      public class Item : ReportItem
      {
         public double cost;

         public Item(double cost) { this.cost = cost; }
      }

      public class OrgItem : Dictionary<Price, Item>
      {
         public void SetStart(VandAudit.Item i, Org o)
         {
            Price p = GetPrice(i);
            if (ContainsKey(p) == false)
               this[p] = new Item(i.cost);

            Item di = this[p];
            di.start += i.qty;
         }

         public void AddSales(VandSales doc, bool addToStart, string moneyID)
         {
            foreach (VandSales.Item i in doc.items)
            {
               if( i.id == moneyID )
                  continue;

               Price p = GetPrice(i);
               if (ContainsKey(p) == false)
                  this[p] = new Item(i.cost);

               Item ri = this[p];
               ri.AddData(i, addToStart);
            }
         }
      }

      public Dictionary<Org, OrgItem> items = new Dictionary<Org, OrgItem>();

      public DetailReportData(DateTime start, DateTime end, string moneyID) : base(start, end, moneyID) { }

      public void AddStart(VandAudit va)
      {
         Org o = va.org == null ? EmptyOrg.Get(va.id) : va.org;
         if (items.ContainsKey(o) == false)
            items[o] = new OrgItem();

         OrgItem oi = items[o];
         foreach (VandAudit.Item i in va.items)
         {
            if( i.id == moneyID )
               continue;

            oi.SetStart(i, o);
         }
      }

      internal void AddSale(VandSales doc)
      {
         Org o = GetOrg(doc);
         if (items.ContainsKey(o) == false)
            items[o] = new OrgItem();

         OrgItem oi = items[o];
         bool addToStart = doc.created < start;
         oi.AddSales(doc, addToStart, moneyID);
      }
   }
}
