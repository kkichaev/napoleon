using System;
using System.Collections.Generic;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class BitmapUtil
   {
      public static Image ScaleImage(Image src, Size size)
      {
         if (src == null)
            return null;

         int sourceWidth = src.Width;
         int sourceHeight = src.Height;

         float nPercent = 0;
         float nPercentW = 0;
         float nPercentH = 0;

         //float width = Math.Max((float)size.Width, (float)size.Height);
         float width = Math.Min((float)size.Width, (float)size.Height);
         nPercentW = (width / (float)sourceWidth);
         nPercentH = (width / (float)sourceHeight);

         if (nPercentH < nPercentW)
            nPercent = nPercentH;
         else
            nPercent = nPercentW;

         int destWidth = (int)(sourceWidth * nPercent);
         int destHeight = (int)(sourceHeight * nPercent);

         Bitmap b = new Bitmap(destWidth, destHeight);
         Graphics g = Graphics.FromImage((Image)b);
         g.InterpolationMode = InterpolationMode.HighQualityBicubic;

         g.DrawImage(src, 0, 0, destWidth, destHeight);
         g.Dispose();

         return (Image)b;
      }

   }
}
