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
   public class FmDetailEx : FmDetail
   {
      public FmDetailEx(FmDetailData data)
         : base(data)
      {
      }

      protected override void FocusReport_Click()
      {
         FocusExcelReport.Do(dtpBegin.Value.Date, dtpEnd.Value.Date.AddDays(1), GetSelectedIdAgent(), this);
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
         }

         public List<Item> items = new List<Item>();
      }
   }
}
