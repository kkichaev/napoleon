using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class TimeGridAdapter
   {
      protected BandCollection bands;

      public TimeGridAdapter()
      {
         bands = CreateBands();
      }

      protected virtual BandCollection CreateBands()
      {
         return new BandCollection();
      }

      public int GetBandCount()
      {
         return bands.Count;
      }

      public Band GetBandAt(int pos)
      {
         if (pos < 0 || pos >= GetBandCount())
            throw new IndexOutOfRangeException();

         return bands[pos];
      }

      public void SetOnBandsChanged(EventHandler hndl) 
      {
         bands.Changed += hndl;
      }

      public int GetBandIndexOf(Band b)
      {
         return bands.IndexOf(b);
      }

      public void Clear()
      {
         bands.Clear();
      }

      public void AddBand(Band b)
      {
         bands.Add(b);
      }

      internal string GetBandNameAt(int i)
      {
         return GetBandAt(i).Name;
      }

      public bool RemoveBandItem(BandItem item)
      {
         bool result = true;

         foreach (Band b in bands)
         {
            foreach(BandItem bi in b.Items)
            {
               if (bi == item)
               {
                  b.Items.Remove(bi);
                  result = true;
                  break;
               }
            }
         }

         return result;
      }
   }
}
