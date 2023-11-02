using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;
using System.Reflection;
using System.Collections;

namespace GRSoft.NapoleonManager
{
  [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public
   class FmDetailEx : FmDetail
   {
      SimpleDataSet<Bonus> dsBonus = new SimpleDataSet<Bonus>(Bonus.OBJ_NAME);
      SimpleDataSet<Order> dsReqOrd = new SimpleDataSet<Order>("ReqOrder");

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         documents.Add(new DocumentInfo(dsBonus, ObjType.TObjType.Bonus));
      }

      protected override void FocusReport_Click()
      {
         FocusExcelReport.Do(dtpBegin.Value.Date, dtpEnd.Value.Date.AddDays(1), GetSelectedIdAgent(), this);
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         dsBonus.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsReqOrd.Filter = dsBonus.Filter;
         updSets.Add(dsBonus);
         updSets.Add(dsReqOrd);
         
         base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);
      }


      internal override Control RefreshDetail(OrderDetailRepresentation odr)
      {
         if (odr.Doctype.Val == ObjType.TObjType.Bonus)
         {
            dgvOrderItems.DataSource =  (odr.StoreObject as Bonus).items;
            return dgvOrderItems;
         }

         return null; 
      }

      protected override IDataSet GetDuplicate(GRSoft.Network.DataObject dataObject)
      {
         if (dataObject is Bonus)
         {
            DataSet<int, Bonus> ord = new DataSet<int, Bonus>(Bonus.OBJECT_NAME, false, true);
            ord.Add(ord.Count, (Bonus)dataObject);

            return ord;
         }
         return null;
      }

      protected override void DoClientCardReport()
      {
         ClientCardData data = new ClientCardData();
         data.userid = GetSelectedIdAgent();
         data.start = dtpBegin.Value.Date;
         data.finish = dtpEnd.Value.Date;

         ReportResult.DoReport("clientcard", data, this);
      }

      protected override void OpenVisitReport()
      {
         Agent a = GetSelectedAgent();
         
         HtmlReport.RouteDetailReport report = new HtmlReport.RouteDetailReport(dgvDetail, a);
         VisitReportData data = new VisitReportData();
         data.start = dtpBegin.Value;
         data.finish = dtpEnd.Value;
         data.name = a.Name;

         foreach(KeyValuePair<DateTime, HtmlReport.RouteDetailData> kv in report)
         {
            VisitReportData.Item i = new VisitReportData.Item();
            i.workDate = kv.Key.Date;
            i.workDateName = kv.Key.ToString("dddd");
            i.routeCount = kv.Value.route == null ? 0 : kv.Value.route.Count;
            data.items.Add(i);
            foreach(HtmlReport.RouteDetailItem rdi in kv.Value)
            {
               i = new VisitReportData.Item();
               i.workDate = kv.Key.Date;
               i.name = rdi.data.Org;
               i.id = rdi.data.NOrg.id;
               i.address = rdi.data.OrgAddr;
               i.outRoute = rdi.outRoute ? 1 : 0;
               i.type = rdi.data.GetDocTypeCaption();
               i.docDate = rdi.data.DateExec;
               i.sended = rdi.data.DateSendedDT;
               i.created = rdi.data.DateCreatedDT;
               i.sum = rdi.data.DblSum;
               i.qty = rdi.data.Qty;
               i.weight = rdi.data.Weight;

               BaseDocument doc = rdi.data.StoreObject as BaseDocument;
               if (doc != null)
               {
                  i.remark = doc.remark;

                  Order o = rdi.data.StoreObject as Order;
                  if (o != null)
                     i.items = o.items.Count;
               }

               Incass incass = rdi.data.StoreObject as Incass;

               if (incass != null)
                  i.dover = incass.dover;

               data.items.Add(i);
            }
         }

         ReportResult.DoReport("agent_visit_report", data, this);
      }

      public class ClientCardData : GRSoft.Network.DataObject
      {
         public string userid = "";
         public DateTime start = DateTime.MinValue;
         public DateTime finish = DateTime.MinValue;
      }

      public class VisitReportData : Network.DataObject
      {
         public DateTime start = DateTime.MinValue;
         public DateTime finish = DateTime.MinValue;
         public string name = "";

         public class Item : Network.DataObject
         {
            public DateTime workDate = DateTime.Now;
            public string workDateName = "";

            public string id = "";
            public string name = "";
            public string type = "";
            public int outRoute = 0;
            public int routeCount = 0;
            public string docDate = "";
            public DateTime created = DateTime.Now;
            public DateTime sended = DateTime.Now;
            public double sum = 0;

            public double qty = 0;
            public int items = 0;
            public double weight = 0;

            public string remark = "";
            public string address = "";
            public string dover = "";
         }

         public List<Item> items = new List<Item>();
      }

      protected override void MakePKOText(RichTextBox tb, object p)
      {
         base.MakePKOText(tb, p);

         Incass i = p as Incass;
         if (i != null)
         {
             tb.Text = tb.Text + '\n' + String.Format("Номер доверенности: {0}", i.dover);
         }
      }
   }
}
