using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Net;
using System.Text;
using System.Xml;

namespace Ads2017
{
   class Route
   {
      private static Dictionary<string, Location> cachedLocations = new Dictionary<string, Location>();

      public static Location GetFirstKnownPoint(IList<OrgRouteQueueItem> queue)
      {
         Location loc = null;

         foreach (OrgRouteQueueItem item in queue)
         {
            loc = item.Location;

            if (loc == null)
            {
               loc = Route.GetLocation(item.Address);
            }

            if (loc != null)
               break;
         }

         return loc;
      }

      static public Location GetLocation(string address)
      {
         Location location = new Location(0,0);
         if (cachedLocations.ContainsKey(address))
         {
            return cachedLocations[address];
         }

         if (address != null && address.Length > 0)
         {
            try
            {
               XmlDocument doc = GetYandexRequest(address);

               XmlNodeList result = doc.GetElementsByTagName("featureMember");
               foreach (XmlNode node in result)
               {
                  if (GoodPrecision(node))
                  {
                     XmlNodeList posList = (node as XmlElement).GetElementsByTagName("pos");
                     if (posList.Count > 0)
                     {
                        string posText = posList.Item(0).InnerText;
                        string[] posA = posText.Split(new char[] { ' ' });
                        location = new Location();
                        CultureInfo en = CultureInfo.GetCultureInfo("en-US");
                        location.Longitude = double.Parse(posA[0], en);
                        location.Latitude = double.Parse(posA[1], en);

                        break;
                     }
                  }
               }
            }
            catch (Exception)
            {
               //MessageBox.Show(e.Message, "Ошибка при получении адреса", MessageBoxButtons.OK, MessageBoxIcon.Stop);
               //using (StreamWriter w = new StreamWriter("log.txt", true))
               //{
               //   w.Write(e.Message);
               //   w.Flush();
               //}
            }
         }

         if (location != null)
            cachedLocations[address] = location;
         return location;
      }

      static bool GoodPrecision(XmlNode node)
      {
         XmlElement element = node as XmlElement;
         if (element == null) return false;

         bool res = false;
         XmlNodeList resCount = element.GetElementsByTagName("precision");
         if (resCount.Count > 0)
         {
            //XmlNode n = resCount.Item(0);
            //if (n.InnerText == "exact" || n.InnerText == "near" || n.InnerText == "street")
            res = true;
         }

         return res;
      }

      static bool initedProxy = false;
      static IWebProxy proxy = null;
      static ICredentials credentails = null;
      public static void FreeProxyInfo() { initedProxy = false; }

      public static XmlDocument GetYandexRequest(string reqStr)
      {
         String req = "http://geocode-maps.yandex.ru/1.x/?geocode=" + reqStr +
            "&key=ANzXmEoBAAAAkjQFPwIAQphddQGLBfkqE3qCI3Vo8OCM8yYAAAAAAAAAAACYpxbA0OaEdJRuAHC50onaXfYLBQ==";
         HttpWebRequest request = (HttpWebRequest)WebRequest.Create(req);

#pragma warning disable 618
         if (!initedProxy)
         {
            credentails = null;
            proxy = null;

            if (Properties.Settings.Default.ProxiIP.Length > 0)
            {
               proxy = new WebProxy(Properties.Settings.Default.ProxiIP + ":" + Properties.Settings.Default.ProxiPort.ToString(), false);
            }
            else
            {
               proxy = WebProxy.GetDefaultProxy();
               //proxy = null;
            }

            if (Properties.Settings.Default.ProxiLogin.Length > 0)
            {
               //CredentialCache credentialCache = new CredentialCache();
               //credentialCache.Add(new Uri("http://geocode-maps.yandex.ru"), "Kerberos", new NetworkCredential(c.proxyLogin, c.proxyPassword));
               //credentails = credentialCache;

               credentails = new NetworkCredential(Properties.Settings.Default.ProxiLogin, Properties.Settings.Default.ProxiPwd, Properties.Settings.Default.ProxiDomen);//, "prodo_ru");
               proxy.Credentials = credentails;
            }
            initedProxy = true;
         }

         //request.Proxy = WebProxy.GetDefaultProxy();
         //request.UseDefaultCredentials = true;
         request.Proxy = proxy;
         //request.Proxy.Credentials = credentails;
         //request.Credentials = credentails;

         //request.Method = "GET";
         //request.KeepAlive = true;
         //request.Accept = @"*/*";

         //request.Credentials = CredentialCache.DefaultCredentials;

         HttpWebResponse response = (HttpWebResponse)request.GetResponse();

         Stream resStream = response.GetResponseStream();
         int count = 0;
         StringBuilder sb = new StringBuilder();
         byte[] buf = new byte[8192];
         do
         {
            count = resStream.Read(buf, 0, buf.Length);
            if (count != 0)
               sb.Append(Encoding.UTF8.GetString(buf, 0, count));
         } while (count > 0);

         XmlDocument doc = new XmlDocument();
         doc.LoadXml(sb.ToString());

         return doc;
      }

      static public bool IsNearestToOrg(Org org, ref Location check, DateTime pointTime, double accurace, List<GPSPos> route)
      {
         return IsNearestToOrg(org.Address, ref check, pointTime, accurace, route);
      }

      static public bool IsNearestToOrg(string address, ref Location check, DateTime pointTime, double accurace, List<GPSPos> route)
      {
         Location l = Route.GetLocation(address);
         if (l == null)
            return true;

         if (Ads2017.Location.Distance(l, check) < accurace)
            return true;

         Ads2017.Location check1 = FindNearesRoutePoint(pointTime, route);
         if (check1 != null && Location.Distance(l, check1) < accurace)
         {
            check.Latitude = check1.Latitude;
            check.Longitude = check1.Longitude;
            return true;
         }

         return false;
      }

      static Location FindNearesRoutePoint(DateTime dateTime, List<GPSPos> gps)
      {
         Location check = null;
         foreach (GPSPos p in gps)
         {
            if (p.date.CompareTo(dateTime) > 0)
            {
               GPSPos pos = p;
               check = new Location(pos.latitude, pos.longitude);
               break;
            }
         }

         return check;
      }
   }

   public class AgentItem
   {
      public string id;
      public string name;

      public AgentItem(Agent a)
      {
         id = a.id;
         name = a.name;
      }

      public override string ToString()
      {
         return name;
      }
   }

   class RoadPoint
   {
      public string Caption = string.Empty;
      public Location loc = null;
   }
}
