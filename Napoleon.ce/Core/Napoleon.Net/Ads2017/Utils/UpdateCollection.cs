using System.Collections;
using System.Collections.Generic;

namespace Ads2017
{
   public class UpdateCollection
   {
      public UpdateCollection()
      {
         Data = new List<Update.WriteData>();
      }

      public List<object> Add(string name)
      {
         return Add(name, null);
      }

      public List<object> Add(string name, object add)
      {
         Update.WriteData wd = new Update.WriteData(name);
         Data.Add(wd);

         if (add != null)
            wd.items.Add(add);

         return wd.items;
      }

      public List<object> Add(string name, ICollection list)
      {
         Update.WriteData wd = new Update.WriteData(name);
         Data.Add(wd);

         if (list != null)
            foreach(object o in list)
            {
               wd.items.Add(o);
            }

         return wd.items;
      }

      public List<Update.WriteData> Data { get; set; }
   }
}
