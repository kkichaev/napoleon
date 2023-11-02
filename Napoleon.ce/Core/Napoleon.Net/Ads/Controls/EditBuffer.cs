using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   public class EditBuffer
   {
      private SelectData data;
      private TimeGrid grid;
      public enum Operation { Cut, Copy }
      Operation oper = Operation.Copy;

      public EditBuffer(TimeGrid grid)
      {
         this.grid = grid;
      }

      public virtual void Copy()
      {
         if (grid.SelectData != null)
         {
            data = grid.SelectData;
            oper = Operation.Copy;
            grid.Invalidate();
         }
      }

      public virtual void Cut()
      {
         if (grid.SelectData != null)
         {
            data = grid.SelectData;
            oper = Operation.Cut;
            grid.Invalidate();
         }
      }

      public virtual Operation Past()
      {
         if (data != null && grid.SelectData != null)
            grid.FireGridPastHandler(this);

         data = null;
         return oper;
      }

      public BandItem BandItem { get { return data != null ? data.BandItem : null; } }

      public bool Contains() { return data != null; }
      public SelectData Data { get { return data; } }
      public Operation CurrentOper { get { return oper; } }

      public object DataStored
      {
         get
         {
            object result = null;

            if (Data != null)
               result = Data.Stored;

            return result;
         }
      }

   }
}
