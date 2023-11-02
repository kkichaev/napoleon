using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   internal interface IItemType
   {
      string Name { get; }
      int Code { get; }
   }
   abstract class PItemType: IItemType
   {
      private String name;
      private int code;

      public PItemType(String name, int code)
      {
         this.name = name;
         this.code = code;
      }

      public string Name
      {
         get {return name; }
         set { name = value; }
      }

      public int Code
      {
         get { return code; }
      }

      public override string ToString()
      {
         return Name;
      }

      public override bool Equals(object obj)
      {
         if (obj != null && obj is PItemType)
         {
            return Code == (obj as PItemType).Code;
         }
         else
            return false;

      }

      public override int GetHashCode()
      {
         return base.GetHashCode();
      }
   }
}
