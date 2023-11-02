using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Controls;

namespace Ads2017
{
   static class CollectionHelper
   {
      public static void ForEach<T>(this UIElementCollection collection, Action<T> a)
      {
         foreach (object o in collection)
            a((T)o);
      }
   }
}
