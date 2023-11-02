using System;
using System.Collections.Generic;
using System.Drawing;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class TaskHelper
   {
      public static Color BkgItemColor(int val)
      {
         const int a = 170;
         return BkgItemColor(val, a);
      }

      public static Color BkgItemColor(int val, int a)
      {
         Color result = Color.FromArgb(0x4f007F0E);

         switch (val)
         {
            case Task.NEW:
               result = Color.FromArgb(a, 0x00, 0x7F, 0x0E);
               break;
            case Task.APPLY:
               result = Color.FromArgb(a, 0x00, 0xAE, 0x59);
               break;
            case Task.REJECT:
               result = Color.FromArgb(a, 0xFE, 0x52, 0x4E);
               break;
            case Task.INWORK:
               result = Color.FromArgb(a, 0xFF, 0xD8, 0x00);
               break;
            case Task.RESOLVED:
               result = Color.FromArgb(a, 0xff, 0xff, 0xff);
               break;
         }

         return result;
      }
   }


}
