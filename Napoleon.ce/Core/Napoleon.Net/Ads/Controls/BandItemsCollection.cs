using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   public class BandItemsCollection : List<BandItem>
   {
      public event EventHandler Changed 
      {
         add { }
         remove { }
      }

      Band owner;
      
      public BandItemsCollection(Band owner)
      {
         this.owner = owner;
      }

      new public void Add(BandItem item)
      {
         base.Add(item);
      }

      new public void AddRange(IEnumerable<BandItem> items)
      {
         base.AddRange(items);
      }

      public void Replace(IEnumerable<BandItem> items)
      {
         Clear(false);
         AddRange(items);
      }

      public void Clear(bool fireEvent)
      {
         base.Clear();
      }

      new public void Clear()
      {
         Clear(true);
      }

      new public void Remove(BandItem item)
      {
         base.Remove(item);
      }
   }
}
