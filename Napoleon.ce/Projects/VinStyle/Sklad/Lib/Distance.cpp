/*
 * Copyright (C), 2007-2010, Денис Мосягин
 *
 * GPS Distance
 *
 *  ert   14/01/2010   creating
 */
#include "stdafx.h"
#include "GPSUnit.h"
#include <math.h>

const double PI = 3.14159265;
const double DC_FACTOR = 1852; // distance convert factor

const double A = 6378.137/1.852; // ellipse
const double F = 1/298.257223563; // ellipse

const int  MAXITER = 100;
const double EPS = 0.00000000005;

double mod(double x, double y)
{
   return x - y * floor(x/y);
}

double modcrs(double x)
{
   return mod(x, 2 * PI);
}

double crsdist_ell(double lat1, double lon1, double lat2, double lon2)
{
   double r, tu1, tu2, cu1, su1, cu2, s1, b1, f1;
   double x, sx, cx, sy, cy,y, sa, c2a, cz, e, c, d;
   double faz, baz, s;
   int iter = 1;

   if ((lat1 + lat2 == 0) && (fabs(lon1 - lon2) == PI))
   {
      lat1 += 0.00001; // allow algorithm to complete
   }

   if (lat1 == lat2 && (lon1 == lon2 || fabs(fabs(lon1 - lon2) - 2*PI) <  EPS))
   {
      return 0;
   }

   r = 1 - F;
   tu1 = r * tan(lat1);
   tu2 = r * tan(lat2);
   cu1 = 1 / sqrt(1 + tu1 * tu1);
   su1 = cu1 * tu1;
   cu2 = 1 / sqrt(1 + tu2 * tu2);
   s1 = cu1 * cu2;
   b1 = s1 * tu2;
   f1 = b1 * tu1;
   x = lon2 - lon1;
   d = x + 1; // force one pass

   while ((fabs(d - x) > EPS) && (iter < MAXITER))
   {
      iter++;
      sx = sin(x);
      cx = cos(x);
      tu1 = cu2 * sx;
      tu2 = b1 - su1 * cu2 * cx;
      sy = sqrt(tu1 * tu1 + tu2 * tu2);
      cy = s1 * cx + f1;
      y = atan2(sy, cy);
      sa = s1 * sx / sy;
      c2a = 1 - sa * sa;
      cz = f1 + f1;
      if (c2a > 0)
         cz = cy - cz / c2a;

      e = cz * cz * 2 - 1;
      c = ((-3. * c2a + 4) * F + 4) * c2a * F / 16;
      d = x;
      x = ((e * cy * c + cz) * sy * c + y) * sa;
      x = (1 - c) * x * F + lon2 - lon1;
   }

   faz = modcrs(atan2(tu1, tu2));
   baz = modcrs(atan2(cu1 * sx, b1 * cx - su1 * cu2) + PI);
   x = sqrt ((1 / (r * r) - 1) * c2a + 1);
   x += 1;
   x = (x - 2) / x;
   c = 1 - x;
   c = (x * x / 4 + 1) / c;
   d = (0.375 * x * x - 1) * x;
   x = e * cy;
   s = ((((sy * sy * 4 - 3) * (1 - e - e)* cz* d / 6 - x) * d / 4 + cz) * sy * d + y) * c * A * r;

   return s; 
}

int Distance(int latitude1, int longitude1, int latitude2, int longitude2)
{
   double lat1, lat2, lon1, lon2;

   lat1 = PI / 180 * ((double)latitude1 / GPS_SCALE);
   lat2 = PI / 180 * ((double)latitude2 / GPS_SCALE);
   lon1 = PI / 180 * ((double)longitude1 / GPS_SCALE);
   lon2 = PI / 180 * ((double)longitude2 / GPS_SCALE);

   double cde = crsdist_ell(lat1, -lon1, lat2, -lon2);
   return (int)(cde * DC_FACTOR);
}
