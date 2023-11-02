using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.NapoleonManager.Utils;
using System.Windows.Forms;
using System.ComponentModel;
using System.Reflection;
using GRSoft.NapoleonManager.Reports;
using GRSoft.NapoleonManager.Reports.Excel;
using GRSoft.Network;
using System.Threading;
using System.Drawing;

namespace GRSoft.NapoleonManager
{
   class SalesHistory : Excel, IReportImplementation
   {
      const int StartColumn = 2;
      const int StartRow = 7;
      public class Data : ReportData
      {
         public DateTime from, till;
         public Agent agent;
         public Org org = null;
      }

      class IntData
      {
         public DateTime[] Dates
         { 
            get 
            {
               DateTime[] ret = new DateTime[dates.Keys.Count];
               dates.Keys.CopyTo(ret, 0);
               return ret;
            } 
         }

         public Qty GetQty(String id, DateTime date)
         {
            if( data.ContainsKey(id) == false )
               return null;

            Dictionary<DateTime, Qty> row = data[id];
            if( row.ContainsKey(date) == false )
               return null;

            return row[date];
         }

         public class Qty
         {
            public int order;
            public int rest;
         }

         private void Add(DateTime date, String id, int qty, bool isOrder)
         {
            Dictionary<DateTime, Qty> row;
            if (data.ContainsKey(id))
               row = data[id];
            else
            {
               row = new Dictionary<DateTime, Qty>();
               data.Add(id, row);
            }

            Qty q;
            if (row.ContainsKey(date))
               q = row[date];
            else
            {
               q = new Qty();
               row.Add(date, q);
            }

            if (isOrder)
               q.order += qty;
            else
               q.rest += qty;
         }

         private void CheckDate(DateTime date)
         {
            if (dates.ContainsKey(date) == false)
               dates.Add(date, true);
         }

         public void Load(DataSet<int, Order> orders, DataSet<int, OrgRemnants> rests)
         {
            foreach (KeyValuePair<int, Order> kvo in orders)
            {
               Order o = kvo.Value;
               DateTime d = new DateTime(o.Date.Year, o.Date.Month, o.Date.Day);
               CheckDate(d);

               foreach (OrderItem oi in o.items)
                  Add(d, oi.id, (int)oi.qty, true);
            }

            foreach (KeyValuePair<int, OrgRemnants> kvo in rests)
            {
               OrgRemnants o = kvo.Value;
               DateTime d = new DateTime(o.date.Year, o.date.Month, o.date.Day);
               CheckDate(d);

               foreach (OrgRemnantsItem oi in o.items)
               {
                  if( oi.item != null )
                     Add(d, oi.item.id, (int)oi.qty, true);
               }
            }
         }
         
         Dictionary<String, Dictionary<DateTime, Qty>> data = new Dictionary<string,Dictionary<DateTime,Qty>>();
         Dictionary<DateTime, bool> dates = new Dictionary<DateTime, bool>();
      }

      public SalesHistory()
      {
      }

      public void Show()
      {
         Visible = true;
      }

      public bool IsPrepared { get { return dataFetched; } }

      public ReportData MakeData(FmDetail form)
      {
         Data data = new Data();
         data.from = form.GetDateForStartPeriod();
         data.till = form.GetDateForEndPeriod();
         data.agent = form.GetSelectedAgent();

         SalesHistoryParams paramDlg = new SalesHistoryParams(data, form.GetAgentOrgs, form.CurrentOrgId);
         if (paramDlg.ShowDialog() == DialogResult.OK)
         {
            return data;
         }

         return null;
      }

      AutoResetEvent waitEvent;
      bool dataFetched;
      private void FetchData(DataSet<int, Order> orders, DataSet<int, OrgRemnants> rests, DataSet<string, Price> price, Data data)
      {
         String filter = String.Format("date >= ToDate('{0}') and date < ToDate('{1}') and userid='{2}'",
            data.from.ToString("dd.MM.yyyy"), data.till.AddDays(1).ToString("dd.MM.yyyy"), data.agent.id);

         orders.Filter = filter;
         rests.Filter = filter;
         price.Command = new ServerCommand(Commands.GET, price.Name);
         //price.Filter = String.Format("userid in ('{0}')", data.agent.id);

         List<IDataSet> updSets = new List<IDataSet>();
         updSets.Add(orders);
         updSets.Add(rests);
         updSets.Add(price);

         DataModule.SetDataRepsonceHandlers(DataProcessed, DataConnectionError);

         waitEvent = new AutoResetEvent(false);
         FmWait.ShowForm(Form.ActiveForm, DataModule.RefreshGiveSets(MainForm.Instance.conn, updSets, FmWait.ProgressIndicator));
         waitEvent.WaitOne();
      }

      public void Build(ReportData data)
      {
         Data d = (Data)data;

         DataSet<int, Order> orders = new DataSet<int,Order>(Order.OBJECT_NAME, false);
         DataSet<int, OrgRemnants> rests = new DataSet<int,OrgRemnants>(OrgRemnants.OBJECT_NAME, false);
         DataSet<string, Price> price =  new DataSet<string, Price>(Price.OBJECT_NAME, false);

         FetchData(orders, rests, price, d);
         if (dataFetched == false)
            return;

         IntData id = new IntData();
         id.Load(orders, rests);

         DrawHead(d, id);
         int lastRow = DrawBody(price, id);

         int startRow = StartRow - 3;
         SetBordersOnRange(startRow, StartColumn, lastRow, StartColumn + 1, xlContinuous);
         int col = StartColumn + 1;
         foreach (DateTime dt in id.Dates)
         {
            SetBordersOnRange(startRow, col, lastRow, col + 1, xlContinuous);
            col += 2;
         }
         SetSelectedCell("A1");
      }

      class PriceCmp : IComparer<Price>
      {
         #region Члены IComparer<Price>

         public int Compare(Price x, Price y)
         {
            int cmp = x.fid.CompareTo(y.fid);
            if (cmp != 0)
               return cmp;

            return x.name.CompareTo(y.name);
         }

         #endregion
      }

      private int DrawBody(DataSet<string, Price> _price, IntData id)
      {
         List<Price> price = new List<Price>();
         price.AddRange(_price.Values);
         price.Sort(new PriceCmp());

         int row = StartRow;

         String curFolder = "";
         DateTime[] dates = id.Dates;
         foreach(Price p in price)
         {
            if( curFolder.CompareTo(p.fid) != 0 )
            {
               curFolder = p.fid;
               MergeCells(row, StartColumn, row, dates.Length * 2 + StartColumn);
               object fcell = GetCell(row, StartColumn);
               SetBackColor(fcell, Color.LightGray);
               SetBorders(fcell, xlContinuous);
               row++;
            }

            int clmn = StartColumn + 1;
            object cell = GetCell(row, StartColumn);
            SetValue(cell, p.name);

            foreach(DateTime d in dates)
            {
               IntData.Qty q = id.GetQty(p.id, d);

               if( q != null )
               {
                  if( q.order != 0 )
                     SetValue(row, clmn, q.order);
                  if (q.rest != 0)
                  {
                     cell = GetCell(row, clmn + 1);
                     SetBackColor(cell, Color.LightGray);
                     SetValue(cell, q.rest);
                  }
               }

               clmn += 2;
            }

            row++;
         }

         AutoFit(StartColumn);
         return row;
      }

      private void DrawHead(Data d, IntData id)
      {
         SetFontSize(8);

         SetValue(1, StartColumn, "История продаж клиента " + d.org.Name);
         SetCellBoldFont(1, 1, true);
         SetCellHorizontalAlign(1, 1, xlRight);

         SetValue(3, StartColumn, "Торговый представитель: " + d.agent.Name);

         object cc;
         cc = GetCell(4, StartColumn);
         // рисуем заголовок
         SetValue(cc, "Дата заказа");
         SetCellHorizontalAlign(cc, xlCenter);

         cc = GetCell(5, StartColumn);
         SetValue(cc, "Дата поставки");
         SetCellHorizontalAlign(cc, xlCenter);

         cc = GetCell(6, StartColumn);
         SetValue(cc, "Наименование");
         SetCellHorizontalAlign(cc, xlCenter);

         int col = StartColumn + 1;
         DateTime[] dates = id.Dates;
         foreach (DateTime date in dates)
         {
            cc = GetCell(4, col);
            MergeCells(4, col, 4, col + 1);
            SetValue(cc, date);
            SetCellHorizontalAlign(cc, xlCenter);

            cc = GetCell(6, col);
            SetValue(cc, "зак");
            SetCellHorizontalAlign(cc, xlCenter);

            cc = GetCell(6, col+1);
            SetValue(cc, "ост");
            SetCellHorizontalAlign(cc, xlCenter);

            SetColumnWidth(col, 8.33);
            SetColumnWidth(col + 1, 8.33);

            col += 2;
         }
      }

      void DataProcessed(System.Object setnder, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         dataFetched = true;
         if (waitEvent != null)
            waitEvent.Set();
      }

      private void DataConnectionError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         dataFetched = false;

         const string TITLE = "Ошибка";
         MessageBox.Show(e.Msg, TITLE, MessageBoxButtons.OK, MessageBoxIcon.Error);

         if (waitEvent != null)
            waitEvent.Set();
      }
   }
}