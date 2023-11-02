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
   public partial class OrdersReport : Form, OrderReportParams
   {
      SimpleDataSet<Order> dsOrder = new SimpleDataSet<Order>(Order.OBJECT_NAME, false);
      DataSet<string, Price> dsPrice;
      DataSet<string, ManagerFolder> dsManagerFolder;

      public OrdersReport()
      {
         InitializeComponent();

         dtpBegin.Value = DateTime.Now;
         dtpEnd.Value = DateTime.Now;

         Manager mgr = (CurrentUser.user as Manager);
         cbDivisions.Items.Add(mgr.Division);
         foreach (Division d in mgr.Childs)
            cbDivisions.Items.Add(d);
         cbDivisions.SelectedIndex = 0;

         List<Agent> agents = new List<Agent>();
         foreach (Agent a in mgr.GetAgents().Data)
            agents.Add(a);
         
         agents.Sort();
         agents.ForEach(x => { cbAgents.Items.Add(x); });

         RefreshCtrlStates(true);
      }

      void RefreshCtrlStates(bool divisionEnable)
      {
         if (rbDivision.Checked != divisionEnable)
            rbDivision.Checked = divisionEnable;

         if (rbAgents.Checked == divisionEnable)
            rbAgents.Checked = !divisionEnable;
         cbDivisions.Enabled = divisionEnable;
         cbAgents.Enabled = !divisionEnable;
      }

      public void SetDivision(Division current)
      {
         cbDivisions.SelectedItem = current;
      }

      private void rbDivision_CheckedChanged(object sender, EventArgs e)
      {
         RefreshCtrlStates(rbDivision.Checked);
      }

      private void btnHtml_Click(object sender, EventArgs e)
      {
         DoReport(true);
      }

      private void btnExcelReport_Click(object sender, EventArgs e)
      {
         DoReport(false);
      }

      void DoReport(bool html)
      {
         ReportData rd = new ReportData();
         rd.start = dtpBegin.Value.Date;
         rd.end = dtpEnd.Value.Date.AddDays(1);
         rd.isHtml = html;
         rd.drawPack = cbPackets.Checked;
         rd.drawQty = cbPiece.Checked;

         if (rbAgents.Checked)
            rd.agent = cbAgents.SelectedItem as Agent;
         if (rbDivision.Checked)
            rd.division = cbDivisions.SelectedItem as Division;

         LoadData(rd);
      }
      
      private void LoadData(ReportData rd)
      {
         dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ??
            new DataSet<string, Price>(Price.OBJECT_NAME);
         dsManagerFolder = (DataSet<string, ManagerFolder>)DataModule.Get(ManagerFolder.OBJECT_NAME) ??
            new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME);


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
            foreach (Division.DivisionAgent a in ((Division)cbDivisions.SelectedItem).GetAllAgents())
            {
               if (a.agent == null)
                  continue;
               DataSet<string, Org> dsOrg = DataModule.GetUserDataSet(a.id, "Org", typeof(DataSet<string, Org>), true) as DataSet<string, Org>;
               if (dsOrg.Count == 0)
                  updSets.Add(dsOrg);
            }
         else
         {
            Agent a = cbAgents.SelectedItem as Agent;
            DataSet<string, Org> dsOrg = DataModule.GetUserDataSet(a.id, "Org", typeof(DataSet<string, Org>), true) as DataSet<string, Org>;

            if (dsOrg.Count == 0)
               updSets.Add(dsOrg);
         }

         dsOrder.Filter = GetOrderFilter(rd);
         updSets.Add(dsOrder);

         if (string.Empty == dsOrder.Filter)
         {
            MessageBox.Show("Выбранное подразделение не содержит агентов");
            return;
         }


         DataModule.DataProcessed += new EventHandler((o, e1) =>
         {
            DataModule.ClearEvents();

            try
            {
               FmWait.ProgressIndicator.SetText("Построение отчета");
               rd.Load(dsOrder, dsPrice, dsManagerFolder);
               if( rd.isHtml)
               {
                  HTMLOrderReportBuilder hb = new HTMLOrderReportBuilder();
                  string fileName = hb.Build(rd);
                  OpenLink.NewWindow(String.Format("\"{0}\"", fileName));
               }
               else
               {
                  ExcelOrderReportBuilder eb = new ExcelOrderReportBuilder();
                  eb.Build(rd, FmWait.ProgressIndicator);
                  eb.Visible = true;
               }
               FmWait.CloseForm();
            }
            catch (Exception)
            {
            }
         });

         DataModule.OnDataResponceError += new EventDataResponseError((e1) => { FmWait.StdErrorHandler(e1); });

         FmWait.ShowForm(this, DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), updSets, FmWait.ProgressIndicator));
      }

      virtual protected string GetOrderFilter(ReportData rd)
      {
         string AGENTS_FILTER = rbDivision.Checked ?
            FmMessageHistory.UserIdIsStr(((Division)cbDivisions.SelectedItem).GetAllAgents()) :
            String.Format("\"userid\" in ('{0}')", (cbAgents.SelectedItem as Agent).id);

         if (AGENTS_FILTER.Length == 0)
            return string.Empty;

         string orderQryField = "date";
         if (rbByCreatedFld.Checked)
            orderQryField = "created";

         string DATA_FILTER = String.Format("\"{0}\" >= ToDate('{1:dd/MM/yyyy}') and \"{0}\" < ToDate('{2:dd/MM/yyyy}')", orderQryField, rd.start, rd.end);

         return String.Format("{0} and {1}", AGENTS_FILTER, DATA_FILTER);
      }
   
      public class ReportData
      {
         public DateTime start, end;
         public Division division;
         public Agent agent;

         public bool isHtml, drawQty, drawPack, totalOnly;

         public List<ManagerFolder> folders = new List<ManagerFolder>();

         public void Load(SimpleDataSet<Order> dsOrder, DataSet<string, Price> dsPrice, DataSet<string, ManagerFolder> dsFolders)
         {
            List<Price> lprice  = new List<Price>();
            foreach(Order o in dsOrder.Data)
            {
               if( o.agent == null || o.org == null )
                  continue;

               ItemData itemD = null;
               if (!data.ContainsKey(o.org))
               {
                  itemD = new ItemData();
                  data[o.org] = itemD;
               }
               else
                  itemD = data[o.org];

               foreach(OrderItem ordItem in o.items)
               {
                  if (ordItem.item == null)
                     continue;

                  Item i = null;
                  if (!itemD.ContainsKey(ordItem.item))
                  {
                     i = new Item();
                     itemD[ordItem.item] = i;
                     lprice.Add(ordItem.item);
                  }
                  else
                     i = itemD[ordItem.item];
                  i.qty += ordItem.qty;
                  i.sum += ordItem.Sum;
               }
            }

            foreach (Price p in lprice)
            {
               if (dsFolders.ContainsKey(p.fid))
               {
                  ManagerFolder f = dsFolders[p.fid];
                  if (!folders.Contains(f))
                     folders.Add(f);
               }
            }

            folders.Sort(CompareFolders);
         }

         int CompareFolders(ManagerFolder x, ManagerFolder y) { return x.name.CompareTo(y.name); }
         int ComparePrice(Price x, Price y) { return x.name.CompareTo(y.name); }

         public class Item
         {
            public double qty = 0;
            public double sum = 0;
         }

         public class ItemData : Dictionary<Price, Item>
         { }

         public class OrgData : Dictionary<Org, ItemData>
         { }

         public OrgData data = new OrgData();

         public virtual string Filter
         {
            get
            {
               return String.Format("период с {0:dd/MM/yyyy} по {1:dd/MM/yyyy}", start, end.Date.AddDays(-1));
            }
         }

         internal List<Price> GetPriceList(ManagerFolder mf)
         {
            List<Price> ret = new List<Price>();

            foreach(KeyValuePair<Org, ItemData> kv in data)
            {
               foreach (Price p in kv.Value.Keys)
                  if (!ret.Contains(p))
                     ret.Add(p);
            }

            ret.Sort(ComparePrice);
            return ret;
         }

         internal Item GetValue(Org o, Price p)
         {
            Item ret = new Item();
            if(data.ContainsKey(o))
            {
               ItemData id = data[o];
               if(id.ContainsKey(p))
                  ret = id[p];
            }
            return ret;
         }
      }
   }

}
