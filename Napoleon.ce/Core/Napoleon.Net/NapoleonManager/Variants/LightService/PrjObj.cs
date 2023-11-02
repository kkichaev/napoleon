using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Text;

namespace GRSoft.NapoleonManager
{
    public class AgentRouteParam : DataObject
    {
        public string ids = "";
        public DateTime start = DateTime.Now;
        public DateTime end = DateTime.Now;
    }

    public class AgentRouteLightService : DataObject
    {
        public static readonly string OBJECT_NAME = "AgentRouteQueryResult";

        public string uid = "";

        [Reference("Agents", "uid")]
        public Agent agent = null;

        public string errMsg = "";
        public int error;


        public string Name { get { return agent == null ? uid : agent.Name; } }
        public int OrgCount { get { return orgs.Count; } }

        public List<Item> orgs = new List<Item>();

        public Color ColorRef { get { return FmAgentZones.AllColors[ColorIndex % FmAgentZones.AllColors.Length]; } }
        public int ColorIndex { get; set; }

        public Image Color
        {
            get
            {
                Bitmap b = new Bitmap(50, 50);

                using (Graphics G = Graphics.FromImage(b))
                {
                    Brush back = Brushes.LightGray;
                    Brush front = new SolidBrush(ColorRef);
                    GraphicsUnit gu = GraphicsUnit.Pixel;

                    RectangleF bounds = b.GetBounds(ref gu);
                    G.FillRectangle(front, bounds);
                }

                return b;
            }
        }

        List<double[]> shape = null;
        List<double[]> Shape
        {
            get
            {
                if (shape == null)
                    shape = MakePoygon(orgs);
                return shape;
            }
        }

        RectangleF bounds = RectangleF.Empty;
        RectangleF Bounds
        {
            get
            {
                if(bounds == RectangleF.Empty && orgs.Count > 0)
                {
                    PointF lt = new PointF((float)orgs[0].lon, (float)orgs[0].lat);
                    PointF rb = new PointF((float)orgs[0].lon, (float)orgs[0].lat);

                    for(int i=1; i<orgs.Count; i++)
                    {
                        Item oi = orgs[i];
                        if (lt.X > oi.lon) lt.X = (float)oi.lon;
                        if (lt.Y > oi.lat) lt.Y = (float)oi.lat;

                        if (rb.X < oi.lon) rb.X = (float)oi.lon;
                        if (rb.Y < oi.lat) rb.Y = (float)oi.lat;
                    }
                    SizeF sz = new SizeF(rb.X - lt.X, rb.Y - lt.Y);
                    bounds = new RectangleF(lt, sz);
                }
                return bounds;
            }
        }

        bool OrgInShape(Item org)
        {
            List<double[]> points = Shape;

            int i;
            int j;
            bool result = false;
            for (i = 0, j = points.Count - 1; i < points.Count; j = i++)
            {
                if ((points[i][0] > org.lat) != (points[j][0] > org.lat))
                {
                    if (org.lon < (points[j][1] - points[i][1]) * (org.lat - points[i][0]) / (points[j][0] - points[i][0]) + points[i][1])
                    {
                        result = !result;
                    }
                }
            }
            return result;
        }


        internal void GetIntersects(AgentRouteLightService r, List<AgentRouteLightService.Item> s1, List<AgentRouteLightService.Item> s2)
        {
            if (!Bounds.IntersectsWith(r.Bounds))
                return;

            foreach(Item i in orgs)
            {
                if(r.OrgInShape(i))
                {
                    s1.Add(i);
                }
            }

            foreach(Item i in r.orgs)
            {
                if(OrgInShape(i))
                {
                    s2.Add(i);
                }
            }
        }

        internal void RemoveStartFinish()
        {
            bool havestart = false, havefinish = false;
            List<Item> rmv = new List<Item>();
            foreach(Item i in orgs)
            {
                if(i.isStart)
                {
                    if (!havestart) havestart = true;
                    else rmv.Add(i);
                } else if(i.isFinish)
                {
                    if (!havefinish) havefinish = true;
                    else rmv.Add(i);
                }
            }

            rmv.ForEach(x => orgs.Remove(x));
        }

        public object Polygon { get; set; }

        public object[] PolygonParams
        {
            get
            {
                object[] ret = new object[4];
                ret[0] = ToJSON(Shape);
                ret[1] = "#" + (ColorRef.ToArgb() & 0xFFFFFF).ToString("X6");
                ret[2] = ToOrgJSON(orgs);
                ret[3] = ColorIndex;
                return ret;
            }
        }


        static string ToOrgJSON(List<Item> orgs)
        {
            StringBuilder ret = new StringBuilder("[");

            foreach (Item i in orgs)
            {
                ret.Append("{\"name\":\"").Append(i.name.Replace("\"", "\\\"")).Append("\",\"lat\":").Append(i.lat.ToString().Replace(",", ".")).
                    Append(",\"lon\":").Append(i.lon.ToString().Replace(",", ".")).Append(",\"isHome\":").Append(i.isFinish || i.isStart ? "1" : "0").Append("},");
            }

            ret.Remove(ret.Length - 1, 1);
            ret.Append("]");
            return ret.ToString();
        }

        static string ToJSON(List<double[]> list)
        {
            StringBuilder ret = new StringBuilder("[");

            foreach(double[] cp in list)
            {
                ret.Append("[").Append(cp[0].ToString().Replace(",", ".")).Append(",").Append(cp[1].ToString().Replace(",", ".")).Append("],");
            }

            ret.Remove(ret.Length - 1, 1);
            ret.Append("]");
            return ret.ToString();
        }

        static bool cw(double[] a, double[] b, double[] c)
        {
            return a[0] * (b[1] - c[1]) + b[0] * (c[1] - a[1]) + c[0] * (a[1] - b[1]) < 0;
        }

        static bool ccw(double[] a, double[] b, double[] c)
        {
            return a[0] * (b[1] - c[1]) + b[0] * (c[1] - a[1]) + c[0] * (a[1] - b[1]) > 0;
        }

        // [lat, lon]
        public static List<double[]> MakePoygon(List<Item> orgs)
        {
            if (orgs.Count == 0) return new List<double[]>();

            List<double[]> points = new List<double[]>();
            foreach(Item i in orgs)
            {
                double[] point = new double[] { i.lat, i.lon };
                points.Add(point);
            }

            points.Sort((x, y) => { return x[0] < y[0] ? -1 : x[0] > y[0] ? 1 : Math.Sign(x[1] - y[1]); });
            double[] p1 = points[0], p2 = points[points.Count - 1];
            List<double[]> up = new List<double[]>(), down = new List<double[]>();

            up.Add(p1);
            down.Add(p1);

            for(int i=1; i<points.Count; i++)
            {
                double[] cp = points[i];
                if (i == points.Count - 1 || cw(p1, cp, p2))
                {
                    while (up.Count >= 2 && !cw(up[up.Count - 2], up[up.Count - 1], cp))
                        up.RemoveAt(up.Count - 1);
                    up.Add(cp);
                }
                if (i == points.Count - 1 || ccw(p1, cp, p2))
                {
                    while (down.Count >= 2 && !ccw(down[down.Count - 2], down[down.Count - 1], cp))
                        down.RemoveAt(down.Count - 1);
                    down.Add(cp);
                }
            }


            for (int i = down.Count - 2; i > 0; i--) up.Add(down[i]);
            return up;
        }

        public class Item : DataObject
        {
            public string id = "";
            public string name = "";

            public string address = "";
            public double lat = 0;
            public double lon = 0;

            public double income = 0;
            public double expense = 0;

            public bool isStart { get { return address.Contains("СТАРТ"); } }
            public bool isFinish { get { return address.Contains("ФИНИШ"); } }
        }
    }
}
