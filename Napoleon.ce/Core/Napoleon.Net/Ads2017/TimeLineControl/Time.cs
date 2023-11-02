using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Ads2017
{
   //[TypeConverter(typeof(TimeTypeConverter))]
   //public class Time
   //{
   //   public static Time MinTime = new Time(0, 0);
   //   public static Time MaxTime = new Time(24, 0);

   //   public Time(int h, int m)
   //   {
   //      this.hour = h;
   //      this.minute = m;
   //   }

   //   private int hour = 0;
   //   private int minute = 0;

   //   public int ToMinutes()
   //   {
   //      return hour * 60 + minute;
   //   }

   //   internal static Time Parse(object p)
   //   {
   //      Time result = new Time(MinTime.Hour, MinTime.Minute);

   //      if (p is string)
   //         ParseFromString((string)p, result);
   //      if (p is DateTime)
   //         ParseFromDateTime((DateTime)p, result);

   //      return result;
   //   }

   //   private static void ParseFromDateTime(DateTime p, Time result)
   //   {
   //      result.hour = p.Hour;
   //      result.minute = p.Minute;
   //   }

   //   private static void ParseFromString(string p, Time result)
   //   {
   //      try
   //      {
   //         string[] s = p.Split(':');
   //         int h = Int32.Parse(s[0]);
   //         int m = Int32.Parse(s[1]);

   //         result.hour = h;
   //         result.minute = m;
   //      }
   //      catch { }
   //   }

   //   internal Time AddHour(int v)
   //   {
   //      return new Time(hour+v, minute);
   //   }

   //   public override string ToString()
   //   {
   //      return string.Format("{0}:{1}", hour.ToString("D2"), minute.ToString("D2"));
   //   }

   //   public override bool Equals(object obj)
   //   {
   //      bool result = false;

   //      if (obj is Time)
   //      {
   //         Time t = (Time) obj;
   //         result = ToMinutes() == t.ToMinutes();
   //      }else
   //         result = base.Equals(obj);

   //      return result;
   //   }

   //   public int Hour
   //   {
   //      get { return hour; }
   //      set { hour = value; }
   //   }

   //   public int Minute
   //   {
   //      get { return minute; }
   //      set { minute = value; }
   //   }

   //   public override int GetHashCode()
   //   {
   //      return base.GetHashCode();
   //   }

   //   public Time AddMinutes(TimeSpan ts)
   //   {
   //      return new Time(hour + ts.Hours, minute + ts.Minutes);
   //   }
   //}
}
