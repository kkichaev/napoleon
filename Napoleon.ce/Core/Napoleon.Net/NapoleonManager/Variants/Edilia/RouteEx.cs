using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;
using GRSoft.NapoleonManager.Utils;

namespace GRSoft.NapoleonManager
{
   class RouteEx : Route
   {
      protected override void MakeReport()
      {
         Invoke(new EmptyParamHandler(
            delegate
            {
               bool isCompolexRoute = GetConnectedDataSource().IsRouteComplex();
               //FillOrgQueueMaster();

               //OrgRouteQueue orgQueue = GetRouteQueue(dsOrgFolder, null);

               OrgRouteQueue src = new OrgRouteQueue();
               //if (isCompolexRoute == false)
               //   src.AddRange(orgQueue);
               //else
               //{
               //   int selected = cbWeek.SelectedIndex + 1;
               //   foreach(OrgRouteQueueItem oi in orgQueue)
               //   {
               //      if (oi.IsItemActiveForWeek(selected))
               //         src.Add(oi);
               //   }
               //}

               RRep report = new RRep("route_report_{0}.html");
               RouteReportData data = new RouteReportData();
               data.agent = cbAgents.SelectedItem as Agent;
               data.queue.AddRange(src);
               data.week = isCompolexRoute ? cbWeek.SelectedItem as string : "";
               data.weekIndex = isCompolexRoute ? cbWeek.SelectedIndex + 1 : 0;
               report.Build(data, dsOrgFolder);
               report.Show();
            }));
      }
   }

   class RRep : HtmlReport
   {
      public RRep(string fileMask)
         : base(fileMask)
      {
      }

      public void Build(RouteReportData data, DataSet<int, OrgFolder> dsOrgFolder)
      {
         StringBuilder html = new StringBuilder("<html><head>" +
         "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"></head>" +
         "<body><FONT FACE=\"Arial\">");

         html.AppendFormat("Дата формирования: {0:dd/MM/yyyy}<br/>", DateTime.Now);
         html.Append("Агент: ");
         Agent agent = data.agent;
         if (agent != null)
            html.Append(agent.name);
         html.Append("<br/>");
         if(data.week != null && data.week.Length > 0)
            html.Append("Неделя: " + data.week + "<br/>");

         for (int p = 0; p < 7; p++)
         {
            WeekDay d = new WeekDay(p + 1);
            OrgRouteQueue filter = Route.GetRouteQueue(dsOrgFolder, d);
            List<OrgRouteQueueItem> rmv = new List<OrgRouteQueueItem>();
            foreach(OrgRouteQueueItem ori in filter)
            {
               if(ori.IsItemActiveForWeek(data.weekIndex) == false)
                  rmv.Add(ori);
            }
            rmv.ForEach(x => filter.Remove(x));

            //OrgRouteQueue filter =  data.queue.Filter(d);
            //Route.AdjustIndex(dsOrgFolder, filter, d);
            //filter.DoSort("index", SortOrder.Ascending);

            bool printHead = true;

            int index = 1;
            foreach (OrgRouteQueueItem item in filter)
            {
               if (item.ContainsDay(d))
               {
                  if( printHead )
                  {
                     html.AppendLine(d.FullName + "<br/>");
                     html.AppendLine("<table cellpadding=5 CELLSPACING=0 border=1 BORDERCOLOR=\"#000000\" width=\"100%\">\n");
                     html.AppendLine("<tr><td width='10%'>№</td><td width='45%'>Клиент</td><td width='45%'>Адрес</td></tr>");
                     printHead = false;
                  }

                  string name = item.Item.org == null ? "" : item.Item.org.name;
                  html.AppendFormat("<tr><td>{0}</td><td>{1}</td><td>{2}</td></tr>\n", index++, name, item.Address);
               }
            }
            if( !printHead )
               html.AppendLine("</table><br/>");
         }
         html.AppendLine(GetFooter());
         html.Append("</body></html>");

         WriteToTmpFile(html.ToString());
      }

      internal void Show()
      {
         OpenReportInAssociationBrowser();
      }
   }
}