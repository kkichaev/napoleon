using System;
using System.Collections.Generic;
using System.Drawing;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class PrjObj
   {
   }

   partial class Matrix
   {
      public int color = 0;

      // это св-во для отображения цвета в .Net
      public Color Color
      {
         get
         {
            int r = color & 0xFF;
            int g = (color & 0xFF00) >> 8;
            int b = (color & 0xFF0000) >> 16;
            return Color.FromArgb(r, g, b);
         }

         set
         {
            // меняем местаи r & b
            int clr = value.ToArgb() & 0xFFFFFF;
            color = (((clr & 0xFF0000) >> 16) | (clr & 0xFF00) | ((clr & 0xFF) << 16));
         }
      }

      public string Name { get { return name; } set { name = value; } }
   }
}
