using GRSoft.NapoleonManager.Reports.Excel;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class IncassReport : Excel
   {
      public List<Network.IDataSet> org;
      public System.Collections.ICollection doc;
      double sum = 0.0;
      Dictionary<string, Item> items = new Dictionary<string, Item>();
      public Dictionary<string, Agent> agent;
      public DateTime start = DateTime.Now;
      public DateTime finish = DateTime.Now;

      internal void Build()
      {
         CollectData();
         MakeReport();
         Visible = true;
      }

      private void MakeReport()
      {
         SetColumnWidth(1, 15);
         SetColumnWidth(2, 15);
         SetColumnWidth(3, 15);
         SetColumnWidth(4, 15);
         //SetColumnWidth(5, 15);

         SetValue(1, 1, string.Format("Отчет по поступлению денежных средств за период с {0:dd/MM/yyyy} по {1:dd/MM/yyyy}", start, finish));

         const int START = 3;
         int r = START;

         List<Item> list = new List<Item>();
         list.AddRange(items.Values);
         list.Sort((lhs, rhs) => { return lhs.name.CompareTo(rhs.name); });

         foreach (Item i in list)
         {
            MergeCells(r, 1, r, 2);
            SetValue(r, 1, i.name);
            SetCellHorizontalAlign(r, 1, xlCenter);
            SetValue(r, 3, i.sum);
            SetCellHorizontalAlign(r,  3, xlLeft);

            r += 1;

            foreach (IItem ii in i.items)
            {
               //MergeCells(r, 1, r, 2);
               SetValue(r, 2, ii.org);
               SetCellHorizontalAlign(r, 1, xlCenter);
               //MergeCells(r, 3, r, 4);
               //SetCellHorizontalAlign(r, 3, xlCenter);
               SetValue(r, 3, ii.sum);
               SetCellHorizontalAlign(r, 3, xlLeft);
               SetValue(r, 4, ii.created);
               r += 1;
            }
         }

         MergeCells(r, 1, r, 2);
         SetCellHorizontalAlign(r, 1, xlCenter);
         SetValue(r, 1, "Итого:");
         SetValue(r, 4, sum);
         SetCellHorizontalAlign(r, 3, xlLeft);
         SetBordersOnRange(START, 1, r, 4, xlContinuous);
      }

      private void CollectData()
      {
         if (org != null && doc != null && agent != null)
         {
            Dictionary<string, Org> om = new Dictionary<string, Org>();
            foreach (IDataSet d in org)
               foreach (Org o in d.Data)
                  om[o.id] = o;

            foreach (Incass i in doc)
            {
               if (!items.ContainsKey(i.userid))
               {
                  Item it = new Item();
                  it.name = i.AgentName;
                  items[i.userid] = it;
               }

               IItem iit = new IItem();

               if (om.ContainsKey(i.id))
               {
                  Org o = om[i.id];
                  iit.org = o.Name;
               }


               iit.sum = i.Sum();
               iit.created = i.created.ToString("dd/MM/yyyy HH:mm");
               items[i.userid].sum += iit.sum;
               items[i.userid].items.Add(iit);
               sum += iit.sum;
            }
         }
      }
   }

   class Item
   {
      public string name = string.Empty;
      public double sum = 0.0;

      public List<IItem> items = new List<IItem>();
   }

   class IItem
   {
      public string org = string.Empty;
      //public string unit = string.Empty;
      public double sum = 0.0;
      public String created;
   }
}
