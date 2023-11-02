using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   public class SelectData
   {
      private Band band;
      private int hour;
      private BandItem item;

      public SelectData(Band band, int hour, BandItem item)
      {
         this.band = band;
         this.hour = hour;
         this.item = item;
      }

      public Band Band { get { return band; } set { band = value; } }
      public int Hour { get { return hour; } set { hour = value; } }
      public BandItem BandItem { get { return item; } set { item = value; } }

      public object Stored
      {
         get
         {
            object result = null;

            if (item != null)
               result = item.Stored;

            return result;
         }
      }
   }
}
