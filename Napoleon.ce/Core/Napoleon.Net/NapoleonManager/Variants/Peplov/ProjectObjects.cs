using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   public class StartWork : DataObject
   {
      public static readonly string OBJECT_NAME = "StartWork";

      public string userid = string.Empty;
      public int day = 0;
      public string time = string.Empty;
   }
}
