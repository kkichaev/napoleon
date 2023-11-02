
using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using GRSoft.Network;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class ClientCard
   {
      static int docNumer = 0;
      protected string MakeHead(List<DateTime> dates, Org org)
      {
         string html = "<table width='100%' cellpadding=5 CELLSPACING=0 border=1 BORDERCOLOR='#000000'>";

         string headRow1 = "<tr BGCOLOR='#CCCCCC'>" +
         "<td rowspan='3'><FONT SIZE='2'><b>№ п/п</b></FONT></td>" +
         "<td rowspan='3'><FONT SIZE='2'><b>Продукты</b></FONT></td>" +
#if Demetra
         String.Format("<td colspan='{0}' align='center'><FONT SIZE='2'><b>{1}, {2}</b></FONT></td></tr>",
            dates.Count * 4, org.Name, (org.address == null) ? "" : org.address);
#else
         OrgHead(dates, org);
#endif
         string headRow2 = "<tr BGCOLOR='#CCCCCC'>";
         string headRow3 = "<tr BGCOLOR='#CCCCCC'>";
         foreach (DateTime d in dates)
         {
#if Demetra
            headRow2 += String.Format("<td colspan='4' align='center'><FONT SIZE='2'><b>{0}</b></FONT></td>", d.ToShortDateString());
            headRow3 += "<td><FONT SIZE='2'><b>ост.</b></FONT></td><td><FONT SIZE='2'><b>ост.,кг</b></FONT></td><td><FONT SIZE='2'><b>зак.</b></FONT></td><td><FONT SIZE='2'><b>зак.,кг</b></FONT></td>";
#else
            headRow2 += HeadDate(d);
            headRow3 += HeadItem();
#endif
         }
         headRow2 += "</tr>";
         headRow3 += "</tr>";

         html += headRow1;
         html += headRow2;
         html += headRow3;

         return html;
      }

      protected virtual string HeadItem()
      {
         return "<td><FONT SIZE='2'><b>ост.</b></FONT></td><td><FONT SIZE='2'><b>зак.</b></FONT></td>";
      }

      protected virtual string HeadDate(DateTime d)
      {
         return String.Format("<td colspan='2' align='center'><FONT SIZE='2'><b>{0}</b></FONT></td>", d.ToShortDateString());
      }

      protected virtual string OrgHead(List<DateTime> dates, Org org)
      {
         return String.Format("<td colspan='{0}' align='center'><FONT SIZE='2'><b>{1}, {2}</b></FONT></td></tr>",
                     dates.Count * 2, org.Name, (org.Address == null) ? "" : org.Address);
      }

      internal void DoReport(System.DateTime start, System.DateTime end)
      {
         ClientCardData data = CreateClientCardData();
         if (data.Load(start, end) == false)
            return;

         StringBuilder html = new StringBuilder("<html><head>" +
            "<meta http-equiv='content-type' content='text/html; charset=utf-8'></head>" +
            "<body><FONT FACE='Arial'>");
         List<Org> orgs = data.Orgs;
         foreach (Org o in orgs)
         {
            List<Price> goods = data.GoodsAxe(o);
            List<DateTime> dates = data.DateAxe(o);

            html.Append(MakeHead(dates, o));

            int count = 0;
            for (; count < goods.Count; count++)
            {
               Price p = goods[count];
               StringBuilder row = new StringBuilder();
               row.AppendFormat("<tr><td width='5%' align='right'><FONT SIZE='2'>{0}</FONT></td>", count + 1);
               row.AppendFormat("<td width='50%' ><FONT SIZE='2'>{0}</FONT></td>", p.name);
               foreach (DateTime d in dates)
               {
                  ClientCardValue v = data.Value(o, d, p);
#if Demetra
                  row.AppendFormat("<td align='right' BGCOLOR='#F0F0F0'><FONT SIZE='2'>{0}</FONT></td><td align='right' BGCOLOR='#F0F0F0'><FONT SIZE='2'>{1}</FONT></td><td align='right'><FONT SIZE='2'>{2}</FONT></td><td align='right' BGCOLOR='#F0F0F0'><FONT SIZE='2'>{3}</FONT></td>",
                     v.remain, v.remainWeight, v.order, v.orderWeight);
#else
                  DataRow(row, v);
#endif
               }
               row.Append("</tr>");
               html.Append(row);
            }

            html.Append("</table><p>");
         }
         html.Append("<FONT SIZE=\"2\"><SUB>Построен в системе 'Наполеон' <a href=http://grsoft.ru/>http://grsoft.ru/</a></SUB></FONT></body></html>");

         string fileName = String.Format("card_info_{0}.html", docNumer++);
         string result = System.IO.Path.GetTempPath() + fileName; //.GetTempFileName();
         using( StreamWriter sw = new StreamWriter(result) )
         {
            sw.Write(html.ToString());
            sw.Flush();
         }

         OpenLink.NewWindow(result);
      }

      protected virtual ClientCardData CreateClientCardData()
      {
         return new ClientCardData();
      }

      protected virtual void DataRow(StringBuilder row, ClientCardValue v)
      {
         row.AppendFormat("<td align='right' BGCOLOR='#F0F0F0'><FONT SIZE='2'>{0}</FONT></td><td align='right'><FONT SIZE='2'>{1}</FONT></td>",
            v.remain, v.order);
      }
   }

   partial class ClientCardValue
   {
      public double order = 0;
      public double orderWeight = 0;
      public double remain = 0;
      public double remainWeight = 0;
   }

   class ClientCardData
   {
      class DataKey
      {
         public String id;
         public DateTime date;

         public DataKey(Price p, DateTime d) { id = p.id; date = d; }
      }

      class DataKeyCmp : IEqualityComparer<DataKey>
      {
         public bool Equals(DataKey x, DataKey y)
         {
            if( x.id.CompareTo(y.id) != 0 ) return false;
            return (x.date.CompareTo(y.date) == 0);
         }

         public int GetHashCode(DataKey obj) { return obj.id.GetHashCode() ^ obj.date.GetHashCode(); }
      }

      class OrgCmp : IEqualityComparer<Org>
      {
         public bool Equals(Org x, Org y) { return (x.Name.CompareTo(y.Name) == 0); }
         public int GetHashCode(Org obj) { return obj.GetHashCode(); }
      }

      //Dictionary<DataKey, ClientCardValue> values = new Dictionary<DataKey,ClientCardValue>(new DataKeyCmp());
      //Dictionary<Price, bool> priceAxe = new Dictionary<Price, bool>();
      //Dictionary<DateTime, bool> dateAxe = new Dictionary<DateTime, bool>();
      Dictionary<Org, Dictionary<DataKey, ClientCardValue>> values = new Dictionary<Org, Dictionary<DataKey, ClientCardValue>>(new OrgCmp());
      Dictionary<Org, Dictionary<Price, bool>> priceAxe = new Dictionary<Org, Dictionary<Price, bool>>();
      Dictionary<Org, Dictionary<DateTime, bool>> dateAxe = new Dictionary<Org, Dictionary<DateTime, bool>>();

      public bool Load(System.DateTime start, System.DateTime end)
      {
         bool ret = true;
         IDataSet cdata;
         cdata = DataModule.Get(Order.OBJECT_NAME);
         if (cdata != null)
         {
            foreach (Order doc in cdata.Data)
            {
#if DELIVERY_ADDRESS
               Org org = new OrgPoint(doc.org, doc.adrCode);
#else
               Org org = doc.org;
#endif
               if (org == null) continue;
               if( values.ContainsKey(org) == false )
               {
                  values[org] = new Dictionary<DataKey, ClientCardValue>(new DataKeyCmp());
                  priceAxe[org] = new Dictionary<Price, bool>();
                  dateAxe[org] = new Dictionary<DateTime, bool>();
               }

               Dictionary<DataKey, ClientCardValue> v = values[org];
               Dictionary<Price, bool> pAxe = priceAxe[org];
               Dictionary<DateTime, bool> dAxe = dateAxe[org];

               DateTime dt = new DateTime(doc.Created.Year, doc.Created.Month, doc.Created.Day);
               dAxe[dt] = true;
               foreach (OrderItem item in doc.items)
               {
                  if (item.item == null) continue;
                  pAxe[item.item] = true;
                  DataKey key = new DataKey(item.item, dt);
                  ClientCardValue cc;
                  if( v.ContainsKey(key) ) cc = v[key];
                  else
                  {
                     cc = new ClientCardValue();
                     v[key] = cc;
                  }
                  cc.order += item.qty;
                  cc.orderWeight += item.Weight;
               }
            }
         }

         cdata = DataModule.Get(OrgRemnants.OBJECT_NAME);
         if (cdata != null)
         {
            foreach (OrgRemnants doc in cdata.Data)
            {
               if (doc.org == null) continue;
               if (values.ContainsKey(doc.org) == false)
               {
                  values[doc.org] = new Dictionary<DataKey, ClientCardValue>(new DataKeyCmp());
                  priceAxe[doc.org] = new Dictionary<Price, bool>();
                  dateAxe[doc.org] = new Dictionary<DateTime, bool>();
               }

               Dictionary<DataKey, ClientCardValue> v = values[doc.org];
               Dictionary<Price, bool> pAxe = priceAxe[doc.org];
               Dictionary<DateTime, bool> dAxe = dateAxe[doc.org];

               DateTime dt = new DateTime(doc.date.Year, doc.date.Month, doc.date.Day);
               dAxe[dt] = true;
               foreach (OrgRemnantsItem item in doc.items)
               {
                  if (item.item == null) continue;
                  pAxe[item.item] = true;
                  DataKey key = new DataKey(item.item, dt);
                  ClientCardValue cc;
                  if (v.ContainsKey(key)) cc = v[key];
                  else
                  {
                     cc = new ClientCardValue();
                     v[key] = cc;
                  }

                  SetRemnantValue(item, cc);
               }
            }
         }

#if Alecon
         cdata = DataModule.Get(OrderW.OBJECT_NAME);
         if (cdata != null)
         {
            foreach (OrderW doc in cdata.Data)
            {
               if (doc.org == null) continue;
               if (values.ContainsKey(doc.org) == false)
               {
                  values[doc.org] = new Dictionary<DataKey, ClientCardValue>(new DataKeyCmp());
                  priceAxe[doc.org] = new Dictionary<Price, bool>();
                  dateAxe[doc.org] = new Dictionary<DateTime, bool>();
               }

               Dictionary<DataKey, ClientCardValue> v = values[doc.org];
               Dictionary<Price, bool> pAxe = priceAxe[doc.org];
               Dictionary<DateTime, bool> dAxe = dateAxe[doc.org];

               DateTime dt = new DateTime(doc.Created.Year, doc.Created.Month, doc.Created.Day);
               dAxe[dt] = true;
               foreach (OrderItem item in doc.items)
               {
                  if (item.item == null) continue;
                  pAxe[item.item] = true;
                  DataKey key = new DataKey(item.item, dt);
                  ClientCardValue cc;
                  if (v.ContainsKey(key)) cc = v[key];
                  else
                  {
                     cc = new ClientCardValue();
                     v[key] = cc;
                  }
                  cc.order += item.qty;
               }
            }
         }
#endif

         return ret;
      }

      protected virtual void SetRemnantValue(OrgRemnantsItem item, ClientCardValue cc)
      {
         cc.remain += item.qty;
         cc.remainWeight += item.Weight;
      }

      int CmpOrg(Org l, Org r) { return l.name.CompareTo(r.name); }
      public List<Org> Orgs
      {
         get
         {
            Org[] orgs = new Org[values.Keys.Count];
            values.Keys.CopyTo(orgs, 0);
            List<Org> ret = new List<Org>(orgs);
            ret.Sort(CmpOrg);

            return ret;
         }
      }

      public List<DateTime> DateAxe(Org o)
      {
         List<DateTime> ret;
         if (dateAxe.ContainsKey(o))
         {
            Dictionary<DateTime, bool> dAxe = dateAxe[o];
            DateTime[] dt = new DateTime[dAxe.Count];
            dAxe.Keys.CopyTo(dt, 0);
            ret = new List<DateTime>(dt);
            ret.Sort();
         }
         else
            ret = new List<DateTime>();
         return ret;
      }

      int CmpPrice(Price l, Price r) { return l.name.CompareTo(r.name); }
      public List<Price> GoodsAxe(Org o)
      {
         List<Price> ret;
         if (dateAxe.ContainsKey(o))
         {
            Dictionary<Price, bool> pAxe = priceAxe[o];
            Price[] dt = new Price[pAxe.Count];
            pAxe.Keys.CopyTo(dt, 0);
            ret = new List<Price>(dt);
            ret.Sort(CmpPrice);
         }
         else
            ret = new List<Price>();
         return ret;
      }

      public ClientCardValue Value(Org o, DateTime date, Price goods)
      {
         if (values.ContainsKey(o))
         {
            Dictionary<DataKey, ClientCardValue> v = values[o];
            DataKey key = new DataKey(goods, date);
            if( v.ContainsKey(key) )
               return v[key];
         }
         return new ClientCardValue();
      }
   }
}