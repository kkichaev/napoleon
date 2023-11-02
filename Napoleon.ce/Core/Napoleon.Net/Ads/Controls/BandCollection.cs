using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   public class BandCollection : List<Band>
   {
      public BandCollection()
      {
      }

      public event EventHandler Changed;

      new public void Add(Band band)
      {
         base.Add(band);
         band.Changed += new EventHandler(band_Changed);

         if (Changed != null)
            Changed.Invoke(this, EventArgs.Empty);
      }

      new public void AddRange(IEnumerable<Band> items)
      {
         base.AddRange(items);
         foreach (Band band in items)
            band.Changed += new EventHandler(band_Changed);

         if (Changed != null)
            Changed.Invoke(this, EventArgs.Empty);
      }

      new public void Clear()
      {
         foreach (Band band in this)
            band.Changed -= new EventHandler(band_Changed);

         base.Clear();

         if (Changed != null)
            Changed.Invoke(this, EventArgs.Empty);
      }

      void band_Changed(object sender, EventArgs e)
      {
         if (Changed != null)
            Changed.Invoke(this, EventArgs.Empty);
      }

      new public void Remove(Band band)
      {
         if (base.Remove(band) && Changed != null)
            Changed.Invoke(this, EventArgs.Empty);
      }
   }
}
