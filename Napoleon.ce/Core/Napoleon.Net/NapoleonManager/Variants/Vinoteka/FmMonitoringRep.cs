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
   public partial class FmMonitoringRep : Form
   {
      SimpleDataSet<PriceMonOrgDoc> dsMon = new SimpleDataSet<PriceMonOrgDoc>(PriceMonOrgDoc.OBJECT_NAME, false);
      SimpleDataSet<PriceMonItem> dsPrice = new SimpleDataSet<PriceMonItem>(PriceMonItem.OBJECT_NAME, false);

      Dictionary<Agent, SimpleDataSet<PriceMonOrgs>> orgs = new Dictionary<Agent, SimpleDataSet<PriceMonOrgs>>();

      public FmMonitoringRep()
      {
         InitializeComponent();
      }

      public static void Open(DateTime start, DateTime end)
      {
         FmMonitoringRep rep = new FmMonitoringRep();
         rep.dtStart.Value = start;
         rep.dtEnd.Value = end;

         rep.Show();
      }

      private void button1_Click(object sender, EventArgs e)
      {
         button1.Enabled = false;
         List<IDataSet> upd = new List<IDataSet>();

         DateTime start = dtStart.Value.Date;
         DateTime end = dtEnd.Value.Date.AddDays(1);

         Agents a = ((Manager)CurrentUser.user).GetAgents();
         List<Agent> agents = new List<Agent>((IEnumerable<Agent>)a.Data);


         foreach (Agent agent in agents)
         {
            DataSet<string, Org> ao = DataModule.GetUserDataSet(agent.id, Org.OBJECT_NAME, typeof(DataSet<string, Org>), true) as DataSet<string, Org>;
            if (ao.Count == 0)
               upd.Add(ao);

            SimpleDataSet<PriceMonOrgs> porg = DataModule.GetUserDataSet(agent.id, PriceMonOrgs.OBJECT_NAME, typeof(SimpleDataSet<PriceMonOrgs>), true) as SimpleDataSet<PriceMonOrgs>;
            orgs[agent] = porg;
            if (porg.Count == 0)
               upd.Add(porg);
         }

         DataSet<string, Price> ap = DataModule.Get(Price.OBJECT_NAME) as DataSet<string, Price> ?? new DataSet<string, Price>(Price.OBJECT_NAME);
         if (ap.Count == 0)
         {
            ap.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
            upd.Add(ap);
         }

         string uid = DataUtils.MakeFilterFromAgents(null, agents);

         //dsMon.Filter = String.Format("{0} and \"created\"=(select max(\"created\") from PriceMonOrgDoc as a1 where a1.id = PriceMonOrgDoc.id and a1.created < ToDate('{1:dd/MM/yyyy}'))", uid, end);
         dsMon.Filter = uid + " and " + DataUtils.MakeCreatedDataFilter(start, end);
         upd.Add(dsPrice);
         upd.Add(dsMon);

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
         FmWait.ProgressIndicator.SetText("Построение отчета");
         MonRepBuilder builder = new MonRepBuilder();
         
         builder.Build(dsMon, dsPrice, orgs, dtStart.Value.Date, dtEnd.Value.Date);

         FmWait.CloseForm();
      }

      class MonRepBuilder : Excel
      {
         public MonRepBuilder() { }

         public void Build(SimpleDataSet<PriceMonOrgDoc> dsMon, SimpleDataSet<PriceMonItem> dsPrice, Dictionary<Agent, SimpleDataSet<PriceMonOrgs>> orgs, DateTime start, DateTime end)
         {
            int startRow = 1;
            startRow = BuildAgentReport(dsMon, dsPrice, orgs, start, end, startRow) + 1;

            Visible = true;
         }

         int BuildAgentReport(SimpleDataSet<PriceMonOrgDoc> dsMon, SimpleDataSet<PriceMonItem> dsPrice, Dictionary<Agent, SimpleDataSet<PriceMonOrgs>> orgs, DateTime start, DateTime end, int startRow)
         {
            Data data = new Data();

            //if( orgs.Count == 0 )
            //{
            //   //MessageBox.Show("Нет данных для отчета");
            //   return startRow;
            //}

            data.Load(dsMon, dsPrice, orgs);

            int cr = startRow + 1;
            int cc = 1;
            object cell;
            cell = GetCell(cr, cc);
            SetValue(cell, "Вид товара ( позиции )");
            SetCellBoldFont(cell, true);
            SetBackColor(cell, Color.LightGray);
            SetBorders(cell, xlContinuous);

            cr = startRow + 2;
            foreach(Price p in data.rows)
            {
               cc = 1;
               cell = GetCell(cr, cc++);
               SetValue(cell, p.Name);
               SetBorders(cell, xlContinuous);

               double minCost = 0;
               ColumnData cd = (data.data.ContainsKey(p)) ? data.data[p] : null;
               foreach (Org o in data.columns)
               {
                  cell = GetCell(cr, cc++);
                  SetBorders(cell, xlContinuous);

                  if (cd != null && cd.ContainsKey(o.id))
                  {
                     double val = cd[o.id];
                     SetValue(cell, val);
                     if (val != 0 && (minCost > val || minCost == 0))
                        minCost = val;
                  }
               }
               cell = GetCell(cr, cc++);
               SetBorders(cell, xlContinuous);
               SetValue(cell, minCost);

               cr++;
            }
            AutoFit(1);

            cr = startRow + 1;
            cc = 2;
            foreach (Org o in data.columns)
            {
               cell = GetCell(cr, cc);
               SetValue(cell, o.Name);
               SetCellBoldFont(cell, true);
               SetBorders(cell, xlContinuous);
               SetBackColor(cell, Color.LightGray);
               AutoFit(cc++);
            }
            {
               cell = GetCell(cr, cc);
               SetValue(cell, "Мин.цена");
               SetCellBoldFont(cell, true);
               SetBorders(cell, xlContinuous);
               SetBackColor(cell, Color.LightGray);
               AutoFit(cc++);
            }
            cell = GetCell(startRow, 1);
            SetValue(cell, "Мониторинг цен " + String.Format(" {0:dd.MMMM.yyyy} - {1:dd.MMMM.yyyy}", start, end));
            SetCellBoldFont(cell, true);

            return startRow + data.rows.Count + 2;
         }

         class ColumnData : Dictionary<string, double>
         {

         }

         class RowData : Dictionary<Price, ColumnData>
         {
         }

         class Data
         {
            public List<Price> rows = new List<Price>();
            public List<Org> columns = new List<Org>();
            public RowData data = new RowData();

            internal void Load(SimpleDataSet<PriceMonOrgDoc> dsMon, SimpleDataSet<PriceMonItem> dsPrice, Dictionary<Agent, SimpleDataSet<PriceMonOrgs>> orgs)
            {
               Dictionary<string, DateTime> loaded = new Dictionary<string, DateTime>();

               foreach (PriceMonOrgDoc doc in dsMon.Data)
               {
                  if (doc.org == null)
                     continue;
                  foreach (PriceMonOrgDoc.Item i in doc.items)
                  {
                     if (i.item == null || i.cost == 0)
                        continue;

                     // проверим, что цена последняя
                     string key = doc.id + "," + i.id;
                     if (loaded.ContainsKey(key) == false || doc.created.CompareTo(loaded[key]) > 0)
                     {
                        loaded[key] = doc.created;
                        if (doc.org != null && columns.Contains(doc.org) == false)
                           columns.Add(doc.org);

                        if (rows.Contains(i.item) == false)
                           rows.Add(i.item);

                        ColumnData cd = null;
                        if (data.ContainsKey(i.item))
                           cd = data[i.item];
                        else
                        {
                           cd = new ColumnData();
                           data[i.item] = cd;
                        }
                        cd[doc.id] = i.cost;
                     }
                  }
               }
               columns.Sort();
            }
         }
      }
   }
}
