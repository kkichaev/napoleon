using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class SyncParam : Network.DataObject
   {
      public DateTime start = DateTime.Now;
      public DateTime finish = DateTime.Now;
      public string ids = string.Empty;
   }
}
