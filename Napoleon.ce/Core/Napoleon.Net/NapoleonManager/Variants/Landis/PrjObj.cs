using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   public class MatrixFmt : DataObject
   {
      public static readonly String OBJECT_NAME = "MatrixFmt";

      [KeyField]
      public string format = "";
      public string matrix = "";

      public string Format { get { return format; } set { format = value; } }

      public string Matrix { get { return matrix; } set { matrix = value; } }
   }
}
