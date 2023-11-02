using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{ 
   class ClientCardEx : ClientCard
   {
      protected override ClientCardData CreateClientCardData()
      {
         return new ClientCardDataEx();
      }

      protected override string OrgHead(List<DateTime> dates, Org org)
      {
         return String.Format("<td colspan='{0}' align='center'><FONT SIZE='2'><b>{1}, {2}</b></FONT></td></tr>", dates.Count * 4, org.Name, (org.Address == null) ? "" : org.Address);
      }

      protected override string HeadDate(DateTime d)
      {
         return String.Format("<td colspan='4' align='center'><FONT SIZE='2'><b>{0}</b></FONT></td>", d.ToShortDateString());
      }

      protected override string HeadItem()
      {
         return "<td><FONT SIZE='2'><b>склад</b></FONT></td><td><FONT SIZE='2'><b>полка</b></FONT></td><td><FONT SIZE='2'><b>ост.</b></FONT></td><td><FONT SIZE='2'><b>зак.</b></FONT></td>";
      }

      protected override void DataRow(StringBuilder row, ClientCardValue v)
      {
         row.AppendFormat("<td align='right' BGCOLOR='#F0F0F0'><FONT SIZE='2'>{0}</FONT></td><td align='right'><FONT SIZE='2'>{1}</FONT></td><td align='right' BGCOLOR='#F0F0F0'><FONT SIZE='2'>{2}</FONT></td><td align='right' BGCOLOR='#F0F0F0'><FONT SIZE='2'>{3}</FONT></td>",
            v.qtyWh, v.qtySh, v.remain, v.order);
      }
   }

   class ClientCardDataEx : ClientCardData
   {
      protected override void SetRemnantValue(OrgRemnantsItem item, ClientCardValue cc)
      {
         base.SetRemnantValue(item, cc);
         cc.qtySh += item.QtySh;
         cc.qtyWh += item.qtyWh;
      }
   }

   partial class ClientCardValue
   {
      public double qtyWh = 0.0;
      public double qtySh = 0.0;
   }
}
