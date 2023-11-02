using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class RouteReportData
   {
      public OrgRouteQueue queue = new OrgRouteQueue();
      public Agent agent;
      public String week = "";
      public int weekIndex = 0;
   }


   class RouteReport : HtmlReport
   {
      public RouteReport(string fileMask)
         : base(fileMask)
      {
      }

      internal void Build(RouteReportData data, DataSet<int, RouteTemplate> ds)
      {
         //DataSet<int, OrgFolder> dsOrgFolder = DataModule.Get(OrgFolder.OBJECT_NAME) as DataSet<int, OrgFolder>;

         StringBuilder html = new StringBuilder("<html><head>" +
         "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"></head>" +
         "<body><FONT FACE=\"Arial\">");

         html.Append("Агент: ");

         Agent agent = data.agent;

         if (agent != null)
            html.Append(agent.name);

         const int WEEK_LEN = 7;
         const string SPACE = "&nbsp;";
         List<string>[] w1 = new List<string>[WEEK_LEN];
         List<string>[] w2 = new List<string>[WEEK_LEN];
         List<string>[] w3 = new List<string>[WEEK_LEN];
         List<string>[] w4 = new List<string>[WEEK_LEN];
         List<List<string>[]> month = new List<List<string>[]>();
         month.Add(w1);
         month.Add(w2);
         month.Add(w3);
         month.Add(w4);

         int max = 0;

         for (int p = 0; p < WEEK_LEN; p++)
         {
            if(w1[p] == null)
              w1[p] = new List<string>();
            if (w2[p] == null)
               w2[p] = new List<string>();
            if (w3[p] == null)
               w3[p] = new List<string>();
            if (w4[p] == null)
               w4[p] = new List<string>();

            OrgRouteQueue filter = Route.GetRouteQueue(ds, p+1);
            int d = p + 1;
            
            foreach (OrgRouteQueueItem item in filter)
            {
               if (item.ContainsDay(d))
               {
                  if (isWeekEnable(item.OrgID, 1, d, ds))
                     w1[p].Add(item.OrgName);
                  if (isWeekEnable(item.OrgID, 2, d, ds))
                     w2[p].Add(item.OrgName);
                  if (isWeekEnable(item.OrgID, 3, d, ds))
                     w3[p].Add(item.OrgName);
                  if (isWeekEnable(item.OrgID, 4, d, ds))
                     w4[p].Add(item.OrgName);
               }
            }

            if (max < w1[p].Count)
               max = w1[p].Count;
            if (max < w2[p].Count)
               max = w2[p].Count;
            if (max < w3[p].Count)
               max = w3[p].Count;
            if (max < w4[p].Count)
               max = w4[p].Count;
         }

         html.Append("<br>");
         html.Append("<br>");

         for (int w = 0; w < month.Count; w++)
         {
            List<string>[] week = month[w];

            if (w == 0 || !WeekEquals(month[w - 1], month[w]))
            {
               html.Append("Неделя: ");
               html.Append(w + 1); ;
               html.Append("<table cellpadding=5 CELLSPACING=0 border=1 BORDERCOLOR=\"#000000\" width=\"100%\">\n");
               html.Append("<tr>");
               for (int i = 1; i <= WEEK_LEN; i++)
               {
                  html.Append("<td width=\"15%\">");
                  html.Append(new WeekDay(i).FullName);
                  html.Append("</td>");
               }

               html.Append("</tr>");

               for (int i = 0; i < max; i++)
               {
                  html.Append("<tr>");

                  for (int y = 0; y < WEEK_LEN; y++)
                  {
                     html.Append("<td>");

                     if (week[y].Count > i)
                        html.Append(StringUtil.EscapeQuotes(week[y][i]));
                     else
                        html.Append(SPACE);

                     html.Append("</td>");
                  }

                  html.Append("</tr>");
               }

               html.Append("</table>\n");
               html.Append("<br>");
            }
         }
         
         html.Append(GetFooter());
         html.Append("</body></html>");

         WriteToTmpFile(html.ToString());
      }

      public bool isWeekEnable(string id, int week, int dayOfWeek, DataSet<int, RouteTemplate> ds)
      {
         bool result = false;
         //DataSet<int, OrgFolder> dsOrgFolder = DataModule.Get(OrgFolder.OBJECT_NAME) as DataSet<int, OrgFolder>;

         if(ds != null)
            foreach (RouteTemplate of in ds.Data)
            {
               try
               {
                  if (of.dayOfWeek == dayOfWeek)
                     continue;
               } catch(Exception )
               {
                  continue;
               }
               foreach (RouteTemplate.Item i in of.items)
               {
                  if (i.org.id.Equals(id))
                  {
                     //int wi = -1;

                     if (of.weekIndex == week)
                     {
                        return true;
                     }
                  }

               }
            }

         return result;
      }

      private bool WeekEquals(List<string>[] list, List<string>[] list_2)
      {
         bool result = false;
         result = list.Length == list_2.Length;

         if (result)
         {
            for(int i = 0; i < list.Length && result; i++)
            {
               result = list[i].Count == list_2[i].Count;

               if(result)
               {
                  for (int x = 0; x < list[i].Count; x++)
                  {
                     result = list[i][x].Equals(list_2[i][x]);

                     if (!result)
                        break;
                  }
               }
            }
         }
         return result;
      }

      internal void Show()
      {
         OpenReportInAssociationBrowser();
      }
   }
}
