using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.Ads
{
   class Coordutils
   {
      const double PI = 3.14159265;
      const double DC_FACTOR = 1852; // distance convert factor

      const double A = 6378.137 / 1.852; // ellipse
      const double F = 1 / 298.257223563; // ellipse

      const int MAXITER = 100;
      const double EPS = 0.00000000005;

      const double GPS_SCALE = 1;

      public static double mod(double x, double y)
      {
         return x - y * Math.Floor(x / y);
      }

      public static double modcrs(double x)
      {
         return mod(x, 2 * PI);
      }

      public static double crsdist_ell(double lat1, double lon1, double lat2, double lon2)
      {
         double r, tu1, tu2, cu1, su1, cu2, s1, b1, f1;
         double x, sx = 0, cx = 0, sy = 0, cy = 0,y = 0, sa, c2a = 0, cz = 0, e = 0, c, d;
         double faz, baz, s;
         int iter = 1;

         if ((lat1 + lat2 == 0) && (Math.Abs(lon1 - lon2) == PI))
         {
            lat1 += 0.00001; // allow algorithm to complete
         }

         if (lat1 == lat2 && (lon1 == lon2 || Math.Abs(Math.Abs(lon1 - lon2) - 2 * PI) < EPS))
         {
            return 0;
         }

         r = 1 - F;
         tu1 = r * Math.Tan(lat1);
         tu2 = r * Math.Tan(lat2);
         cu1 = 1 / Math.Sqrt(1 + tu1 * tu1);
         su1 = cu1 * tu1;
         cu2 = 1 / Math.Sqrt(1 + tu2 * tu2);
         s1 = cu1 * cu2;
         b1 = s1 * tu2;
         f1 = b1 * tu1;
         x = lon2 - lon1;
         d = x + 1; // force one pass

         while ((Math.Abs(d - x) > EPS) && (iter < MAXITER))
         {
            iter++;
            sx = Math.Sin(x);
            cx = Math.Cos(x);
            tu1 = cu2 * sx;
            tu2 = b1 - su1 * cu2 * cx;
            sy = Math.Sqrt(tu1 * tu1 + tu2 * tu2);
            cy = s1 * cx + f1;
            y = Math.Atan2(sy, cy);
            sa = s1 * sx / sy;
            c2a = 1 - sa * sa;
            cz = f1 + f1;
            if (c2a > 0)
               cz = cy - cz / c2a;

            e = cz * cz * 2 - 1;
            c = ((-3 * c2a + 4) * F + 4) * c2a * F / 16;
            d = x;
            x = ((e * cy * c + cz) * sy * c + y) * sa;
            x = (1 - c) * x * F + lon2 - lon1;
         }

         faz = modcrs(Math.Atan2(tu1, tu2));
         baz = modcrs(Math.Atan2(cu1 * sx, b1 * cx - su1 * cu2) + PI);
         x = Math.Sqrt ((1 / (r * r) - 1) * c2a + 1);
         x += 1;
         x = (x - 2) / x;
         c = 1 - x;
         c = (x * x / 4 + 1) / c;
         d = (0.375 * x * x - 1) * x;
         x = e * cy;
         s = ((((sy * sy * 4 - 3) * (1 - e - e)* cz* d / 6 - x) * d / 4 + cz) * sy * d + y) * c * A * r;

         return s; 
      }

      public static int  Distance(double latitude1, double longitude1, double latitude2, double longitude2)
      {
         double lat1, lat2, lon1, lon2;

         lat1 = PI / 180 * (double)latitude1;
         lat2 = PI / 180 * (double)latitude2;
         lon1 = PI / 180 * (double)longitude1;
         lon2 = PI / 180 * (double)longitude2;

         double cde = crsdist_ell(lat1, -lon1, lat2, -lon2);
         return (int)(cde * DC_FACTOR);
      }

   }
}
