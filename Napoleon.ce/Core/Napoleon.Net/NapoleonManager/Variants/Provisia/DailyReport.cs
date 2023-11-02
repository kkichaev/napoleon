using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.NapoleonManager.Utils;
using System.Windows.Forms;
using System.ComponentModel;
using System.Reflection;
using GRSoft.NapoleonManager.Reports.Excel;
using GRSoft.NapoleonManager.Reports;
using GRSoft.Network;
using System.Threading;
using System.Drawing;

namespace GRSoft.NapoleonManager
{
   class DailyReport : Excel, IReportImplementation
   {
      class Data : SalesHistory.Data
      {
         public List<Org> route;
      }

      public ReportData MakeData(FmDetail form)
      {
         Data data = new Data();
         data.from = form.GetDateForStartPeriod();
         data.agent = form.GetSelectedAgent();

         DailyReportParams paramDlg = new DailyReportParams(data);
         if (paramDlg.ShowDialog() == DialogResult.OK)
         {
            data.till = data.from.AddDays(1);
            data.route = OrdersDetail.GetRoutePeriod(data.from, data.till, data.agent.id);
            return data;
         }

         return null;
      }

      class OrgEx
      {
         public Org org;
         public bool inRoute = false;
      }

      class IntData
      {
         public void Load(DataSet<int, OrgRemnants> rests, List<Org> route)
         {
            foreach (KeyValuePair<int, OrgRemnants> kv in rests)
            {
               OrgRemnants doc = kv.Value;
               Dictionary<String, int> data;
               if (items.ContainsKey(doc.id))
                  data = items[doc.id];
               else
               {
                  data = new Dictionary<string, int>();
                  items.Add(doc.id, data);

                  OrgEx oe = new OrgEx();
                  oe.org = doc.org;
                  foreach (Org o in route)
                  {
                     if (o.id == doc.id)
                     {
                        oe.inRoute = true;
                        break;
                     }
                  }

                  orgs.Add(doc.id, oe);
               }

               foreach (OrgRemnantsItem i in doc.items)
               {
                  if( i.item == null )
                     continue;
                  if (data.ContainsKey(i.item.id))
                     data[i.item.id] = data[i.item.id] + (int)i.qty;
                  else
                     data[i.item.id] = (int)i.qty;
               }
            }
         }

         public int Qty(String org, String item)
         {
            if (items.ContainsKey(org) == false)
               return 0;

            Dictionary<String, int> data = items[org];
            if (data.ContainsKey(item) == false)
               return 0;

            return data[item];
         }

         public OrgEx[] Orgs
         {
            get
            {
               OrgEx[] ret = new OrgEx[orgs.Count];
               orgs.Values.CopyTo(ret, 0);
               return ret;
            }
         }

         Dictionary<String, Dictionary<String, int>> items = new Dictionary<string,Dictionary<string,int>>();
         Dictionary<String, OrgEx> orgs = new Dictionary<string, OrgEx>();
      }

      public void Show()
      {
         Visible = true;
      }

      class OrgCmp : IComparer<OrgEx>
      {
         public int Compare(OrgEx x, OrgEx y)
         {
            if (x.inRoute == y.inRoute)
               return x.org.name.CompareTo(y.org.name);

            if (x.inRoute)
               return -1;
            return 1;
         }
      }

      class PriceCmp : IComparer<Price>
      {
         DataSet<string, ManagerFolder> folders;

         public PriceCmp(DataSet<string, ManagerFolder> folders)
         {
            this.folders = folders;
         }

         public int Compare(Price x, Price y)
         {
            if (x.fid == y.fid)
               return x.name.CompareTo(y.name);

            ManagerFolder xf = folders[x.fid];
            ManagerFolder yf = folders[y.fid];

            return xf.name.CompareTo(yf.name);
         }
      }

      public void Build(ReportData data)
      {
         Data d = (Data)data;

         DataSet<int, OrgRemnants> rests = new DataSet<int, OrgRemnants>(OrgRemnants.OBJECT_NAME, false);
         DataSet<string, Price> price = new DataSet<string, Price>(Price.OBJECT_NAME, false);
         DataSet<string, ManagerFolder> folders = new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME, false);

         FetchData(rests, price, folders, d);
         if (dataFetched == false)
            return;

         IntData id = new IntData();
         id.Load(rests, d.route);

         List<OrgEx> orgs = new List<OrgEx>();
         orgs.AddRange(id.Orgs);
         orgs.Sort(new OrgCmp());

         List<Price> p = new List<Price>();
         foreach (Price prc in price.Values)
         {
            if (folders.ContainsKey(prc.fid))
               p.Add(prc);
            if (p.Count > 250)
               break;
         }
         p.Sort(new PriceCmp(folders));


         int startRow = 4;
         int startCell = 2;

         SetFontSize(8);

         int lastCell = DrawHead(d, folders, p, startRow, startCell);

         object cell = null;
         int ir = startRow + 2;
         bool showRoute = true;
         foreach (OrgEx o in orgs)
         {
            int ic = startCell;

            if (showRoute && o.inRoute == false)
            {
               showRoute = false;
               cell = GetCell(ir, ic);
               SetValue(cell, "Незапланированные посещения");
               SetCellBoldFont(cell, true);
               SetCellHorizontalAlign(cell, xlCenter);
               SetBackColor(cell, Color.LightGray);
               MergeCells(ir, ic, ir, lastCell);
               ir++;
            }

            cell = GetCell(ir, ic++);
            SetValue(cell, o.org.Name);

            foreach (Price prc in p)
            {
               int qty = id.Qty(o.org.id, prc.id);
               if (qty != 0)
               {
                  cell = GetCell(ir, ic);
                  SetValue(cell, qty);
               }

               ic++;
            }

            ir++;
         }

         SetBordersOnRange(startRow, startCell, ir-1, lastCell, xlContinuous);
         AutoFit(startCell);

         SetSelectedCell("A1");
      }

      private int DrawHead(Data d, DataSet<string, ManagerFolder> folders, List<Price> p, int startRow, int startCell)
      {
         object cell;
         cell = GetCell(1, startCell);
         SetValue(cell, "Агент: " + d.agent.Name);
         SetCellBoldFont(cell, true);

         cell = GetCell(2, startCell);
         SetValue(cell, "Дата: " + d.from.ToString("dd.MM.yyyy"));
         SetCellBoldFont(cell, true);

         int ic = startCell;
         int ir = startRow;
         cell = GetCell(ir, ic++);
         SetValue(cell, "Торговая точка");
         SetCellBoldFont(cell, true);
         SetCellHorizontalAlign(cell, xlCenter);

         object fcell = null;
         Color curColor = Color.LightGray;
         int fPos = -1;
         ManagerFolder mf = null;
         foreach (Price prc in p)
         {
            if (mf == null || mf.id != prc.fid)
            {
               mf = folders[prc.fid];
               curColor = (curColor == Color.LightGray) ? Color.White : Color.LightGray;
               if (fcell != null)
                  MergeCells(ir, fPos, ir, ic-1);
               fPos = ic;
               fcell = GetCell(ir, ic);
               SetBackColor(fcell, curColor);
               SetValue(fcell, mf.name);
               SetCellBoldFont(fcell, true);
               SetCellHorizontalAlign(fcell, xlCenter);
            }

            cell = GetCell(ir + 1, ic);
            SetValue(cell, prc.name);
            SetCellBoldFont(cell, true);
            SetOrientation(cell, 90);
            SetBackColor(cell, curColor);
            SetColumnWidth(ic, 2.7);
            ic++;
         }
         ic--;
         if (fcell != null)
            MergeCells(ir, fPos, ir, ic);

         return ic;
      }

      public bool IsPrepared { get { return dataFetched; } }

      AutoResetEvent waitEvent;
      bool dataFetched;
      private void FetchData(DataSet<int, OrgRemnants> rests, DataSet<string, Price> price, DataSet<string, ManagerFolder> folders, SalesHistory.Data data)
      {
         rests.Filter = String.Format("date >= ToDate('{0}') and date < ToDate('{1}') and userid='{2}'",
            data.from.ToString("dd.MM.yyyy"), data.till.ToString("dd.MM.yyyy"), data.agent.id);

         //price.Filter = String.Format("userid in ('{0}')", data.agent.id);
         //folders.Filter = price.Filter;
         price.Command = new ServerCommand(Commands.GET, price.Name);
         folders.Command = new ServerCommand(Commands.GET, folders.Name);

         List<IDataSet> updSets = new List<IDataSet>();
         updSets.Add(folders);
         updSets.Add(rests);
         updSets.Add(price);

         DataModule.SetDataRepsonceHandlers(DataProcessed, DataConnectionError);

         waitEvent = new AutoResetEvent(false);
         FmWait.ShowForm(Form.ActiveForm, DataModule.RefreshGiveSets(MainForm.Instance.conn, updSets, FmWait.ProgressIndicator));
         waitEvent.WaitOne();
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