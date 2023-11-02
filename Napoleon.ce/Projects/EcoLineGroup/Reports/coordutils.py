# -*- coding: cp1251 -*-


import sys
import math

PI = 3.14159265;
DC_FACTOR = 1852; # distance convert factor
A = 6378.137 / 1.852; # ellipse
F = 1 / 298.257223563; # ellipse
MAXITER = 100;
EPS = 0.00000000005;

GPS_SCALE = 1;

def mod(x, y):
    return x - y * math.floor(x / y)

def modcrs(x):
    return mod(x, 2 * PI)

def crsdist_ell(lat1, lon1, lat2, lon2):
     iter = 1

     if (lat1 + lat2 == 0) and (math.fabs(lon1 - lon2) == PI):
        lat1 += 0.00001 # allow algorithm to complete

     if lat1 == lat2 and (lon1 == lon2 or math.fabs(math.fabs(lon1 - lon2) - 2 * PI) < EPS):
        return 0

     r = 1 - F
     tu1 = r * math.tan(lat1)
     tu2 = r * math.tan(lat2)
     cu1 = 1 / math.sqrt(1 + tu1 * tu1)
     su1 = cu1 * tu1
     cu2 = 1 / math.sqrt(1 + tu2 * tu2)
     s1 = cu1 * cu2
     b1 = s1 * tu2
     f1 = b1 * tu1
     x = lon2 - lon1
     d = x + 1 # force one pass

     while (math.fabs(d - x) > EPS) and (iter < MAXITER):
        iter = iter + 1
        sx = math.sin(x)
        cx = math.cos(x)
        tu1 = cu2 * sx
        tu2 = b1 - su1 * cu2 * cx
        sy = math.sqrt(tu1 * tu1 + tu2 * tu2)
        cy = s1 * cx + f1
        y = math.atan2(sy, cy)
        sa = s1 * sx / sy
        c2a = 1 - sa * sa
        cz = f1 + f1
        
        if c2a > 0:
           cz = cy - cz / c2a

        e = cz * cz * 2 - 1
        c = ((-3 * c2a + 4) * F + 4) * c2a * F / 16
        d = x
        x = ((e * cy * c + cz) * sy * c + y) * sa
        x = (1 - c) * x * F + lon2 - lon1

     faz = modcrs(math.atan2(tu1, tu2))
     baz = modcrs(math.atan2(cu1 * sx, b1 * cx - su1 * cu2) + PI)
     x = math.sqrt ((1 / (r * r) - 1) * c2a + 1)
     x += 1
     x = (x - 2) / x
     c = 1 - x
     c = (x * x / 4 + 1) / c
     d = (0.375 * x * x - 1) * x
     x = e * cy
     s = ((((sy * sy * 4 - 3) * (1 - e - e)* cz* d / 6 - x) * d / 4 + cz) * sy * d + y) * c * A * r

     return s

def distance(latitude1, longitude1, latitude2, longitude2):
     lat1 = PI / 180 * latitude1
     lat2 = PI / 180 * latitude2
     lon1 = PI / 180 * longitude1
     lon2 = PI / 180 * longitude2

     cde = crsdist_ell(lat1, -lon1, lat2, -lon2)
     return cde * DC_FACTOR
