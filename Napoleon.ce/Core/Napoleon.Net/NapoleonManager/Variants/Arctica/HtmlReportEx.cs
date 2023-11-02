using GRSoft.UILib;
using System;
using System.Collections.Generic;
using System.Reflection;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class SumReportItem : GRSoft.Network.DataObject
   {
      public int isDivision = 0;
      public string name = "";
      public int visits = 0;
      public int orders = 0;
      public int byphone = 0;
      public int calls = 0;
      public double progress = 0;
      public double sum = 0;
      public double phoneSum = 0;

      public SumReportItem(SummaryDataEx src)
      {
         isDivision = 0;
         name = src.Agent.Name;
         visits = src.Visits + src.Calls;
         orders = src.Orders + src.ByPhone;
         sum = src.sum + src.PhoneSum;
         progress = src.ProgressValue;
         byphone = src.ByPhone;
         phoneSum = src.phoneSum;
         calls = src.Calls;
      }

      public SumReportItem(SummaryDivisionDataEx src)
      {
         isDivision = 1;
         name = src.Name;
         visits = src.Visits + src.Calls;
         orders = src.Orders + src.ByPhone;
         sum = src.DocSum + src.PhoneSum;
         progress = src.ProgressValue;
         byphone = src.ByPhone;
         phoneSum = src.phoneSum;
         calls = src.Calls;
      }
   }

   class SummaryReportParams : GRSoft.Network.DataObject
   {
      public DateTime start;
      public DateTime end;
      public List<SumReportItem> items = new List<SumReportItem>();
   }
   class HtmlReportEx : HtmlReport
   {

      void PutNode(SummaryReportParams param, TreeGridNode node)
      {
         bool expanded = false;

         SummaryDataEx sde = node.DataItem as SummaryDataEx;
         if (sde != null)
         {
            SumReportItem sri = new SumReportItem(sde);
            param.items.Add(sri);
         }
         else
         {
            SummaryDivisionDataEx dde = node.DataItem as SummaryDivisionDataEx;
            if (dde != null)
            {
               SumReportItem sri = new SumReportItem(dde);
               param.items.Add(sri);
            }
         }

         if (node.Nodes.Count > 0)
         {
            if (!node.IsExpanded)
            {
               try
               {
                  node.Expand();
                  expanded = true;
               }
               catch (Exception) { }
            }
         }

         foreach (TreeGridNode n in node.Nodes)
         {
            PutNode(param, n);
         }

         if (expanded)
         {
            node.Collapse();
         }
      }

      public override string makeAgentSummaryFileInfo(UILib.TreeGridView grid, TimeInterval interval)
      {
         SummaryReportParams param = new SummaryReportParams();

         param.start = interval.begin;
         param.end = interval.end;

         foreach (TreeGridNode node in grid.Nodes)
         {
            PutNode(param, node);
         }

         string fn = GRSoft.NapoleonManager.ReportResult.GetReport("arctic_summary", param);
         return fn;
//         return base.makeAgentSummaryFileInfo(grid, interval);
      }

      List<string> icfolders = new List<string>();
      public override string makeDetailsFileInfo(DataGridView dgvDetail, TimeInterval interval, Agent agent)
      {
         HtmlDetailReportSettings setting = BaseFormSetting<HtmlDetailReportSettings>.Load();
         FmVisitReportParams prm = new FmVisitReportParams();

         prm.CheckedFolders = setting.iceCreams;
         if( prm.ShowDialog() == DialogResult.OK)
         {
            setting.iceCreams = prm.CheckedFolders;
            setting.Save();
         }

         icfolders.Clear();
         icfolders.AddRange(setting.iceCreams);

         return base.makeDetailsFileInfo(dgvDetail, interval, agent);
      }

      public override HtmlReport.Total CreateTotal()
      {
         return new TotalEx();
      }

      protected override HtmlReport.RouteDetailReport CreateRouteDetailReport(DataGridView dgvDetail, Agent agent)
      {
         return new RouteDetailReportEx(dgvDetail, agent, icfolders);
      }

      protected override string[] headerTableDetail(Dictionary<OrderDetailRepresentation, HtmlReport.DocTime> docsTime)
      {
         string[] head;
         if (docsTime != null)
            head = new string[] { "контрагенты", "тип посещения", "по маршруту", "дата", 
               "время создания", "дата передачи", "сумма", "штук", "позиций", "вес, кг", "вес мороженного", "мест", "время в пути", 
               "время в точке", "комментарий", "адрес" };
         else
            head = new string[] { "контрагенты", "тип посещения", "по маршруту", "дата", 
               "время создания", "дата передачи", "сумма", "штук", "позиций", "вес, кг", "вес мороженного", "мест", "комментарий", "адрес" 
            };
         return head;
      }

      public override void DayResult(System.Globalization.CultureInfo culture, StringBuilder sb, HtmlReport.Total t)
      {
         TotalEx te = (TotalEx)t;
         sb.AppendFormat("<FONT SIZE=\"2\"><br><b>Итого по дню:</b> посетил: <b>{0}</b>, по маршруту <b>{1}</b>, вне маршрута <b>{2}</b>, не посетил <b>{3}</b>" +
            " документов: <b>{4}</b>, сумма: <b>{5:0.00}</b>, штук <b>{6}</b>, вес <b>{7:0}</b> кг</FONT>, мороженного <b>{8:0}</b> кг, мест <b>{9:0}</b></FONT>",
            t.visit, t.routeVisit, t.outVisit, t.notVisit, t.docs, ToHtmlConv(t.sum.ToString("C", culture)), t.qty, t.weigth,
            te.iceCreameWeight, te.packQty
            );
      }

      public override void ReportResult(System.Globalization.CultureInfo culture, StringBuilder sb, HtmlReport.Total total)
      {
         TotalEx te = (TotalEx)total;
         sb.AppendFormat("<FONT SIZE=\"2\"><br><b>Всего:</b> посетил: <b>{0}</b>, по маршруту <b>{1}</b>, вне маршрута <b>{2}</b>, не посетил <b>{3}</b>" +
            " документов: <b>{4}</b>, сумма: <b>{5:0.00}</b>, штук <b>{6}</b>, вес <b>{7:0}</b> кг, мороженного <b>{8:0}</b> кг, мест <b>{9:N2}</b></FONT>",
            total.visit, total.routeVisit, total.outVisit, total.notVisit, total.docs, ToHtmlConv(total.sum.ToString("C", culture)), total.qty, total.weigth,
            te.iceCreameWeight, te.packQty
            );
      }
   
      class RouteDetailReportEx : RouteDetailReport
      {
         List<string> icf = new List<string>();

         public RouteDetailReportEx(DataGridView dgvDetail, Agent agent, List<string> icFolders) : 
            base(dgvDetail, agent) 
         {
            icf.AddRange(icFolders);
         }
         protected override RouteDetailData CreateRouteDetailData() { return new RouteDetailDataEx(icf); }
      }

      class RouteDetailDataEx : RouteDetailData
      {
         List<string> icf;

         public RouteDetailDataEx(List<string> icf) { this.icf = icf; }

         public override void PrintAfterWeight(StringBuilder sb, Total ret, RouteDetailItem item, OrderDetailRepresentation data)
         {
            double icWeight = 0;
            double packQty = 0;

            Order order = data.StoreObject as Order;
            if( order != null )
            {
               foreach(OrderItem oi in order.items)
               {
                  if( oi.item != null )
                  {
                     double inp = oi.item.inPack;
                     if (inp == 0)
                        inp = 1;
                     packQty += oi.qty / inp;

                     if(icf.Contains(oi.item.fid) )
                     {
                        icWeight += oi.qty * oi.item.weight;
                     }
                  }
               }
            }

            sb.Append("<td><FONT SIZE=\"2\">" + ((icWeight == 0) ? "&nbsp;" : icWeight.ToString()) + "</td>");
            sb.Append("<td><FONT SIZE=\"2\">" + ((packQty == 0) ? "&nbsp;" : packQty.ToString("N2")) + "</td>");

            TotalEx te = (TotalEx)ret;
            te.packQty += packQty;
            te.iceCreameWeight += icWeight;
         }
      }

      class TotalEx : Total
      {
         public double packQty = 0;
         public double iceCreameWeight = 0;

         public override void Add(Total src)
         {
            base.Add(src);
            TotalEx te = (TotalEx)src;
            packQty += te.packQty;
            iceCreameWeight += te.iceCreameWeight;
         }
      }
   }

   [Serializable]
   class HtmlDetailReportSettings : BaseFormSetting<HtmlDetailReportSettings>
   {
      public List<string> iceCreams = new List<string>();
   }
}
