using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.NapoleonManager.Reports.Excel;
using System.Windows.Forms;
using System.Reflection;
using System.Globalization;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
  [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public
   class FmDetailEx : FmDetail
   {
      public FmDetailEx(FmDetailData data)
         : base(data)
      {
      }

      protected override void OpenVisitReport()
      {
         //base.OpenVisitReport();
         new VisitReport(dgvDetail, new TimeInterval(dtpBegin.Value, dtpEnd.Value), (cbAgents.Items[cbAgents.SelectedIndex] as Agent));
      }

      protected override void AfterRefreshData()
      {
         Dictionary<string, Org> ido = new Dictionary<string, Org>();
         foreach (Org o in dsOrg.Data)
            ido[o.ido] = o;

         IDataSet[] sets = new IDataSet[] { dsOrder, dsVisit, dsOrgRemnants };
         foreach(IDataSet set in sets)
         {
            foreach(BaseDocument doc in set.Data)
            {
               if ((doc.org == null || doc.org is EmptyOrg) && ido.ContainsKey(doc.id))
                  doc.org = ido[doc.id];
            }
         }
      }
   }

   class VisitReport : Excel
   {
      private int curRow = 4;

      public VisitReport(DataGridView grid, TimeInterval interval, Agent agent)
      {
         SetValue(1, 1, "Отчет агента " + agent.name);
         SetValue(2, 1, String.Format("Период: {0} - {1}",
            interval.begin.ToString("dd.MM.yyyy"), interval.end.ToString("dd.MM.yyyy")));

         HtmlReport.RouteDetailReport report = new HtmlReport.RouteDetailReport(grid, agent);
         CultureInfo culture = Config.GetCultureInfo();

         foreach (KeyValuePair<DateTime, HtmlReport.RouteDetailData> kv in report)
         {
            SetValue(curRow, 1, String.Format("Дата {0:dd/MM (dddd)}", kv.Key));
            curRow += 2;
            int startRow = curRow;
            SetValueWA(1, 4, "контрагенты");
            SetValueWA(5, 2, "тип посещения");
            SetValueWA(7, 2, "по маршруту");
            SetValueWA(9, 2, "дата");
            SetValueWA(11, 2, "время создания");
            SetValueWA(13, 2, "дата передачи");
            SetValueWA(15, 2, "сумма");
            SetValueWA(17, 2, "штук");
            SetValueWA(19, 2, "позиций");
            SetValueWA(21, 2, "вес, кг");
            SetValueWA(23, 4, "комментарий");
            SetValueWA(27, 4, "адрес");

            curRow += 1;

            HtmlReport.Total total = new HtmlReport.Total();

            List<Org> orgs = new List<Org>();
            foreach (HtmlReport.RouteDetailItem item in kv.Value)
            {
               OrderDetailRepresentation data = item.data;

               total.docs++;
               if (orgs.Contains(data.NOrg) == false)
               {
                  if (data.DateCreatedDT == DateTime.MaxValue)
                  {
                     total.notVisit++;
                  }
                  else
                  {
                     total.visit++;
                     if (item.outRoute) total.outVisit++;
                     else total.routeVisit++;
                  }
               }

               SetValueW(1, 4, data.Org);
               SetValueW(5, 2, data.Doctype.ToString());
               SetValueWA(7, 2,  item.outRoute ? "нет" : string.Empty);
               SetValueW(9, 2,  data.DateExec.Length > 0 ? data.DateExec : string.Empty);

               string v;

               v = (data.DateCreatedDT == DateTime.MinValue || data.DateCreatedDT == new DateTime(1601, 1, 1) || data.DateCreatedDT == DateTime.MaxValue) ?
                  String.Empty :
                  data.DateCreatedDT.ToString("HH:mm");

               SetValueW(11, 2, v);

               v = (data.DateSendedDT == DateTime.MinValue || data.DateSendedDT == new DateTime(1601, 1, 1)) ?
                  string.Empty :
                  data.DateSendedDT.ToString("dd.MM.yy HH:mm");
               
               SetValueW(13, 2, v);

               v = data.Sum;
               int cqty = data.Qty;

               total.qty += cqty;
               total.sum += data.DblSum;

               SetValueW(15, 2, v.Length > 0 ? v : string.Empty);
               SetValueW(17, 2, cqty > 0 ? cqty.ToString() : string.Empty);

               Order order = data.StoreObject as Order;

               /*Позиций*/
               SetValueW(19, 2, order == null ? string.Empty : order.items.Count.ToString());

               /*Вес*/
               double cw = 0;
               if (order != null)
                  cw = order.Weight;
               total.weigth += cw;

               SetValueW(21, 2, (cw == 0) ? string.Empty : cw.ToString());

               /*Комментарий*/
               string comment = string.Empty;
               GRSoft.Network.DataObject obj = data.StoreObject;

               if (obj != null)
               {
                  FieldInfo fi = obj.GetType().GetField("remark");
                  if (fi != null)
                  {
                     object val = fi.GetValue(obj);

                     if (val != null && val is string)
                        comment = (string)val;
                  }
               }

               SetValueW(23, 4, comment.Length == 0 ? string.Empty : comment);

               /*Адрес*/
               SetValueW(27, 4, data.OrgAddr);
               curRow++;
            }

            SetBordersOnRange(startRow, 1, curRow-1, 30, xlContinuous);

            curRow += 2;

            SetValue(curRow, 1, string.Format("Итого по дню: посетил: {0}, по маршруту {1}, вне маршрута {2}, не посетил {3}" +
               " документов: {4}, сумма: {5:0.00}, штук {6}, вес {7:0} кг",
               total.visit, total.routeVisit, total.outVisit, total.notVisit, total.docs, total.sum.ToString("C", culture), total.qty, total.weigth
               ));

            curRow += 3;
         }

         Visible = true;
      }

      private void SetValueW(int cell, int len, string val)
      {
         MergeCells(curRow, cell, curRow, cell + len - 1);
         SetValue(curRow, cell, val);
         SetWrapeText(curRow, cell, true);
      }

      private void SetValueWA(int cell, int len, string val)
      {
         SetValueW(cell, len, val);
         SetCellHorizontalAlign(curRow, cell, xlCenter);
      }
   }
}
