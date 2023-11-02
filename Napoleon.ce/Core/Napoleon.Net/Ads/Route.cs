using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Net;
using System.Text;
using System.Xml;

namespace GRSoft.NapoleonManager
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

      static public Location GetLocation(Org org)
      {
         OrgLocations ol = OrgLocations.GetDataSet();
         OrgLocation loc = ol.GetLocation(org.id);
         if (loc != null)
         {
            Location l = new Location(loc.latitude, loc.longitude);
            if (org.Address.Length > 0)
               cachedLocations[org.Address] = l;
            return l;
         }

         if (org.Address.Length > 0 && cachedLocations.ContainsKey(org.Address))
            return cachedLocations[org.Address];

         return GetLocation(org.Address);
      }

      static public Location GetLocation(string address)
      {
         Location location = null;
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

            Config c = Config.GetConfig();
            if (c.proxyIP.Length > 0)
            {
               proxy = new WebProxy(c.proxyIP + ":" + c.proxyPort.ToString(), false);
            }
            else
            {
               proxy = WebProxy.GetDefaultProxy();
               //proxy = null;
            }

            if (c.proxyLogin.Length > 0)
            {
               //CredentialCache credentialCache = new CredentialCache();
               //credentialCache.Add(new Uri("http://geocode-maps.yandex.ru"), "Kerberos", new NetworkCredential(c.proxyLogin, c.proxyPassword));
               //credentails = credentialCache;

               credentails = new NetworkCredential(c.proxyLogin, c.proxyPassword, c.proxyDomen);//, "prodo_ru");
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

      static public bool IsNearestToOrg(Org org, ref Location check, DateTime pointTime, double accurace, DataSet<DateTime, GPSPos> route)
      {
         return IsNearestToOrg(org.Address, ref check, pointTime, accurace, route);
      }

      static public bool IsNearestToOrg(string address, ref Location check, DateTime pointTime, double accurace, DataSet<DateTime, GPSPos> route)
      {
         Location l = Route.GetLocation(address);
         if (l == null)
            return true;

         if (NapoleonManager.Location.Distance(l, check) < accurace)
            return true;

         NapoleonManager.Location check1 = FindNearesRoutePoint(pointTime, route);
         if (check1 != null && Location.Distance(l, check1) < accurace)
         {
            check.Latitude = check1.Latitude;
            check.Longitude = check1.Longitude;
            return true;
         }

         return false;
      }

      static Location FindNearesRoutePoint(DateTime dateTime, DataSet<DateTime, GPSPos> dsGPSPos)
      {
         Location check = null;
         List<DateTime> keys = new List<DateTime>(dsGPSPos.Keys);
         keys.Sort();
         foreach (DateTime key in keys)
         {
            if (key.CompareTo(dateTime) > 0)
            {
               GPSPos pos = dsGPSPos[key];
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
