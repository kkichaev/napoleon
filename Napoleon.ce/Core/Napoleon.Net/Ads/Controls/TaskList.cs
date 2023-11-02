using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.Ads
{
   class TaskList : List<ItemDraw>
   {
      public TaskList(BandItemsCollection tasks)
      {
         List<BandItem> src = new List<BandItem>(tasks);
         src.Sort(CompareTask);

         ItemDraw current = null;
         foreach (BandItem task in src)
         {
            ItemDraw check = new ItemDraw(task);
            if (current == null)
            {
               current = check;
               Add(current);

               continue;
            }
            ItemDraw ints = current.FindIntersect(check);
            if (ints == null)
            {
               current = check;
               Add(current);

               continue;
            }

            ints.Add(check);
         }
      }

      int CompareTask(BandItem t1, BandItem t2)
      {
         return t1.Start.CompareTo(t2.Start);
      }
   }
}
