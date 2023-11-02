using System;
using System.Collections.Generic;
using System.Text;
using System.Reflection;
using GRSoft.Network;

namespace GRSoft.Ads.Report
{
   class OrderReport : Excel
   {
      public void build(string[] data, string[] items, List<IOrderItems> orders)
      {
         List<string> dataCaptions = new List<string>();

         int row = 1;
         int col = 1;

         foreach (string d in data)
         {
            PropertyInfo pi = typeof(OrderRcv).GetProperty(d);
            object[] captions = pi.GetCustomAttributes(typeof(Caption), true);

            if (captions.Length == 1 && captions[0] is Caption)
            {
               SetValue(row, col, ((Caption)captions[0]).Value);
               SetCellBoldFont(row, col, true);
               SetColumnWidth(col, ((Caption)captions[0]).Width);
            }

            col++;

         }

         row++;

         foreach (IOrderItems rcv in orders)
         {
            col = 1;
            foreach (string d in data)
            {
               if (rcv.GetType().GetProperty(d).Name == "Text")
               {
                  string s = ((string)rcv.GetType().GetProperty(d).GetValue(rcv, null)).Replace("\r\n", ";");
                  
                  if (s.IndexOf(";") == s.Length - 1)
                     s = s.Substring(0, s.Length - 1);

                  SetValue(row, col, s);
               }
               else
                  SetValue(row, col, rcv.GetType().GetProperty(d).GetValue(rcv, null));
               col++;
            }
            row++;

            if (items.Length > 0 && rcv.Items.Count > 0)
            {
               int innercol = 1;

               foreach (string i in items)
               {
                  PropertyInfo pi = typeof(OrderItem).GetProperty(i);
                  object[] captions = pi.GetCustomAttributes(typeof(Caption), true);

                  if (captions.Length == 1 && captions[0] is Caption)
                  {
                     SetValue(row, innercol, ((Caption)captions[0]).Value);
                     SetCellBoldFont(row, innercol, true);
                  }

                  innercol++;
               }

               row++;
               foreach (OrderItem item in rcv.Items)
               {
                  innercol = 1;
                  foreach (string i in items)
                  {
                     SetValue(row, innercol, item.GetType().GetProperty(i).GetValue(item, null));
                     innercol++;
                  }
                  row++;
               }
            }
         }

         Visible = true;
         SetSelectedCell("A1");
      }
   }
}
