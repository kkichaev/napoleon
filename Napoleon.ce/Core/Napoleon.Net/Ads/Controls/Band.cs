using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   public class Band
   {
      private string name;
      BandItemsCollection items;

      public event EventHandler Changed;

      public Band()
         : this("")
      {
      }

      public Band(string name)
      {
         this.name = name;

         items = CreateBandCollection();
         items.Changed += new EventHandler(tasks_Changed);
      }

      protected virtual BandItemsCollection CreateBandCollection()
      {
         return new BandItemsCollection(this);
      }

      void tasks_Changed(object sender, EventArgs e)
      {
         if (Changed != null)
            Changed.Invoke(this, EventArgs.Empty);
      }

      public BandItemsCollection Items { get { return items; } }

      public string Name { get { return name; } set { name = value; } }
   }
}
