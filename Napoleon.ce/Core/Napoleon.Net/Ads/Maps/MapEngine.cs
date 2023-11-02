using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Globalization;
using System.Resources;
using System.Collections;
using System.Reflection;
using GRSoft.NapoleonManager.Properties;

namespace GRSoft.NapoleonManager
{
   class MapEngine
   {
      private static readonly string NAMESTR = "##name##";
      private static readonly string LONGITUDESTR = "##longitude##";
      private static readonly string LATITUDESTR = "##latitude##";
      private static readonly string NUMBERSTR = "##number##";
      private static readonly string COLORSTR = "##color##";
      private static readonly string DESCRSTR = "##descr##";

      public static string[] GetNamesMaps() 
      {
         ResourceManager rm = Resources.ResourceManager;
         ResourceSet set = rm.GetResourceSet(
            CultureInfo.CurrentCulture, true, true);

         List<string> result = new List<string>();

         const string NAME = "!name";
         foreach (DictionaryEntry o in set)
         {
            String resName = (string)o.Key;

            if (resName.EndsWith("maps"))
            {
               string val = (string)o.Value;
               result.Add(val.Substring(val.IndexOf(NAME) + NAME.Length, val.IndexOf(";") - NAME.Length).Trim());
            }
         }

         rm.ReleaseAllResources();

         DirectoryInfo di = new DirectoryInfo(".");
         FileInfo[] fi = di.GetFiles("*.maps");

         for (int i = 0; i < fi.Length; i++)
         {
            string name = fi[i].Name
               .Substring(0, fi[i].Name.IndexOf("."));

            if (!result.Contains(name))
               result.Add(name);
         }

         return result.ToArray();
      }

      public static string OrgChangeLocation(string source)
      {
         return ReadSection("changeorgaddress", source);
      }

      public static string OrgAddress(string source, string orgName, Location location)
      {
         if (source == null || source.Length <= 0 || orgName == null || location == null)
            return string.Empty;

         NumberFormatInfo nfi = new CultureInfo("en-US", false).NumberFormat;
         nfi.NumberDecimalSeparator = ".";
         string result = ReadSection("orgaddress", source);

         if (result.Trim().Length == 0)
            return SOURCE_MAP_ERR_MSG;

         result = result.Replace(NAMESTR, StringUtil.EscapeQuotes(orgName));
         result = result.Replace(LONGITUDESTR, location.Longitude.ToString(nfi));
         result = result.Replace(LATITUDESTR, location.Latitude.ToString(nfi));

         /*
          * DEBUG DEBUG DEBUG
         TextWriter tw = new StreamWriter("page1.html");
         tw.Write(result);
         tw.Close();
          * DEBUG DEBUG DEBUG
          */

         return result;
      }

      public static string Route(string source, IList<OrgRouteQueueItem> queue)
      {
         if (source == null || source.Length <= 0 || queue == null || queue.Count == 0)
            return string.Empty;

         NumberFormatInfo nfi = new CultureInfo("en-US", false).NumberFormat;
         nfi.NumberDecimalSeparator = ".";
         string html = ReadSection("route", source);

         if (html.Trim().Length == 0)
            return SOURCE_MAP_ERR_MSG;

         string[] content;
         string var = CutVarSection(html, true, out content);

         StringBuilder data = new StringBuilder();

         Location centerMap = GRSoft.NapoleonManager.Route.GetFirstKnownPoint(queue);

         if (centerMap == null)
         {
            return string.Empty;
         }

         content[0] = content[0].Replace(LONGITUDESTR, centerMap.Longitude.ToString(nfi));
         content[0] = content[0].Replace(LATITUDESTR, centerMap.Latitude.ToString(nfi));

         foreach (OrgRouteQueueItem item in queue)
         {
            string dataStr = var;
            dataStr = dataStr.Replace(NAMESTR, StringUtil.EscapeQuotes(item.OrgName));
            dataStr = dataStr.Replace(NUMBERSTR, item.Pos.ToString());
            Location loc = item.Location == null ? GRSoft.NapoleonManager.Route.GetLocation(item.Address) : item.Location;

            if (loc == null)
               continue;

            dataStr = dataStr.Replace(LONGITUDESTR, loc.Longitude.ToString(nfi));
            dataStr = dataStr.Replace(LATITUDESTR, loc.Latitude.ToString(nfi));
            data.AppendLine(dataStr);
         }

         string result = content[0] + data + content[1];

         /*
          * DEBUG DEBUG DEBUG
         TextWriter tw = new StreamWriter("page.html");
         tw.Write(result);
         tw.Close();
          * DEBUG DEBUG DEBUG
          */

         return result;
      }

      public enum PoitType { All, GPS, GSM }

      class DocPointData
      {
         public string tag;
         public string descr;
      }

      private const string SOURCE_MAP_ERR_MSG = "Ошибка, невозможно прочитать файл карты, проверьте правильность настройки источника карт.";
      public static string TraceRoute(string source, List<Location> listLocation,
         List<VisitQueueItem> visitQueue, List<RoadPoint> roadPoint, PoitType pt)
      {
         if (source == null || source.Length <= 0)
            return string.Empty;

         NumberFormatInfo nfi = new CultureInfo("en-US", false).NumberFormat;
         nfi.NumberDecimalSeparator = ".";
         string html = ReadSection("traceroute", source);

         if (html.Trim().Length == 0)
            return SOURCE_MAP_ERR_MSG;

         const string GPS_COLOR_DEF = "!colorGPS=";
         string GPS_COLOR = html.Substring(html.IndexOf(GPS_COLOR_DEF) + 
            GPS_COLOR_DEF.Length, html.IndexOf(";") - GPS_COLOR_DEF.Length);
         html = html.Substring(html.IndexOf(";") + 1);
         string GSM_COLOR_DEF = "!colorGSM=";
         string GSM_COLOR = html.Substring(html.IndexOf(GSM_COLOR_DEF) + 
            GSM_COLOR_DEF.Length, html.IndexOf(";") - GSM_COLOR_DEF.Length);

         html = html.Substring(html.IndexOf(";") + 1);

         string[] c1, c2, c3;
         string var1 = CutVarSection(html, true, out c1);
         string var2 = CutVarSection(c1[1], true, out c2);
         string var3 = CutVarSection(c2[1], true, out c3);

         StringBuilder data = new StringBuilder();
         Dictionary<ItemLoc, DocPointData> docData = new Dictionary<ItemLoc, DocPointData>(new ItemLocCmp());
         if (visitQueue != null && visitQueue.Count > 0)
         {
            foreach (VisitQueueItem visitItem in visitQueue)
            {
               if (!visitItem.HavePosition)
                  continue;

               string ballonTag = visitItem.number;
               
               ItemLoc il = new ItemLoc(visitItem.latitude, visitItem.longitude);
               string descr = (visitItem.objType.IsStopType) ? 
                  StringUtil.EscapeQuotes("Остановка " + visitItem.StopTime) :
                  StringUtil.EscapeQuotes(visitItem.OrgName);
               if (docData.ContainsKey(il))
               {
                  DocPointData dd = docData[il];
                  dd.tag += "," + visitItem.number;
                  dd.descr += "<br>" + descr;
               }
               else
               {
                  DocPointData dd = new DocPointData();
                  docData[il] = dd;
                  dd.tag += visitItem.number;
                  dd.descr += descr;
               }
            }

            foreach(KeyValuePair<ItemLoc, DocPointData> i in docData)
            {
               string dataStr = var1;

               dataStr = dataStr.Replace(NUMBERSTR, i.Value.tag.Replace(",",""));
               dataStr = dataStr.Replace(NAMESTR, i.Value.tag);
               dataStr = dataStr.Replace(DESCRSTR, i.Value.descr);
               dataStr = dataStr.Replace(LONGITUDESTR, i.Key.lon.ToString(nfi));
               dataStr = dataStr.Replace(LATITUDESTR, i.Key.lat.ToString(nfi));
               data.AppendLine(dataStr);
            }
         }

         data.AppendLine(c2[0]);

         bool first = true;
         if (listLocation != null && listLocation.Count > 0)
         {
            int locNumber = 0;
            string[] c21;
            string var21 = CutVarSection(var2, false, out c21);

            foreach (Location loc in listLocation)
            {
               string dataStr = c21[0];
               dataStr = dataStr.Replace(NUMBERSTR, locNumber.ToString());
               string contStr = var21;

               contStr = contStr.Replace(LONGITUDESTR, loc.Longitude.ToString(nfi));
               contStr = contStr.Replace(LATITUDESTR, loc.Latitude.ToString(nfi));

               if (first)
               {
                  first = false;
                  //isGsm = loc.IsGsm;
                  data.Append(dataStr);
               }

               if (pt != PoitType.All && !loc.isVisitPoint)
               {
                  if (loc.IsGsm && pt != PoitType.GSM)
                     continue;
                  if (!loc.IsGsm && pt != PoitType.GPS)
                     continue;
               }

               data.AppendFormat("{0},", contStr);

               if (loc == listLocation[listLocation.Count-1])
               {
                  dataStr = c21[1].Replace(COLORSTR, loc.IsGsm ? GSM_COLOR : GPS_COLOR);
                  dataStr = dataStr.Replace(NUMBERSTR, locNumber.ToString());
                  dataStr = dataStr.Replace(LATITUDESTR, loc.Latitude.ToString(nfi));
                  dataStr = dataStr.Replace(LONGITUDESTR, loc.Longitude.ToString(nfi));
                  first = true;
                  if (data[data.Length - 1] == ',')
                     data.Remove(data.Length - 1, 1);
                  data.AppendLine(dataStr);
                  locNumber++;
               }
            }
         }

         if (roadPoint != null && roadPoint.Count > 0)
         {
            foreach (RoadPoint rp in roadPoint)
            {
               if (rp.loc.Latitude == 0 && rp.loc.Longitude == 0)
                  continue;

               ItemLoc il = new ItemLoc(rp.loc.Latitude, rp.loc.Longitude);
               if (docData.ContainsKey(il))
                  continue;

               string dataStr = var3;
               dataStr = dataStr.Replace(NUMBERSTR, rp.Caption);
               dataStr = dataStr.Replace(NAMESTR, rp.Caption.ToString());
               dataStr = dataStr.Replace(DESCRSTR, "время: " +
                  rp.loc.date.ToString("HH:mm") + "<br>" +
                  "<i>скорость: " + (rp.loc.speed * 3.6).ToString() + " км/ч</i>");
               dataStr = dataStr.Replace(LONGITUDESTR, rp.loc.Longitude.ToString(nfi));
               dataStr = dataStr.Replace(LATITUDESTR, rp.loc.Latitude.ToString(nfi));
               data.AppendLine(dataStr);
            }
         }

         if (data[data.Length - 1] == ',')
            data.Remove(data.Length - 1, 1);

         string result = c1[0] + data + c3[1];

         /*
          * DEBUG DEBUG DEBUG
         TextWriter tw = new StreamWriter("page.html");
         tw.Write(result);
         tw.Close();
          * DEBUG DEBUG DEBUG
          */

         return result;
      }

      public static string CoverArea(string source, List<VisitQueueItem> visitQueue)
      {
         if (source == null || source.Length <= 0)
            return string.Empty;

         NumberFormatInfo nfi = new CultureInfo("en-US", false).NumberFormat;
         nfi.NumberDecimalSeparator = ".";
         string html = ReadSection("coverarea", source);

         if (html.Trim().Length == 0)
            return SOURCE_MAP_ERR_MSG;

         string[] c1;
         string var1 = CutVarSection(html, true, out c1);

         StringBuilder data = new StringBuilder();
         Dictionary<ItemLoc, List<VisitQueueItem>> items = new Dictionary<ItemLoc, List<VisitQueueItem>>(new ItemLocCmp());
         if (visitQueue != null && visitQueue.Count > 0)
         {
            foreach (VisitQueueItem visitItem in visitQueue)
            {
               if (!visitItem.HavePosition)
                  continue;

               string ballonTag = visitItem.number;
               ItemLoc il = new ItemLoc(visitItem.latitude, visitItem.longitude);
               if (items.ContainsKey(il))
               {
                  String stag = "";
                  foreach (VisitQueueItem vi in items[il])
                     stag += vi.number + ",";
                  ballonTag = stag + ballonTag;
               }
               else
                  items.Add(il, new List<VisitQueueItem>(new VisitQueueItem[] { visitItem }));

               string dataStr = var1;
               //dataStr = dataStr.Replace(NUMBERSTR, visitNumber.ToString());
               dataStr = dataStr.Replace(NUMBERSTR, ballonTag.Replace(",", ""));
               dataStr = dataStr.Replace(NAMESTR, ballonTag);
               if (visitItem.objType.IsStopType)
               {
                  //dataStr = dataStr.Replace(NAMESTR, "P");
                  dataStr = dataStr.Replace(DESCRSTR, StringUtil.EscapeQuotes("Остановка " + visitItem.StopTime));
               }
               else
               {
                  //dataStr = dataStr.Replace(NAMESTR, visitNumber.ToString());
                  dataStr = dataStr.Replace(DESCRSTR, StringUtil.EscapeQuotes(visitItem.OrgName));
               }

               dataStr = dataStr.Replace(LONGITUDESTR, visitItem.longitude.ToString(nfi));
               dataStr = dataStr.Replace(LATITUDESTR, visitItem.latitude.ToString(nfi));
               dataStr = dataStr.Replace(COLORSTR, visitItem.color);
               data.AppendLine(dataStr);

               //visitNumber++;
            }
         }

        // data.AppendLine(c2[0]);

         if (data.Length > 2 && data[data.Length - 1] == ',')
            data.Remove(data.Length - 1, 1);

         string result = c1[0] + data + c1[1];

         /*
          * DEBUG DEBUG DEBUG
         TextWriter tw = new StreamWriter("page.html");
         tw.Write(result);
         tw.Close();
         * DEBUG DEBUG DEBUG
         */

         return result;
      }

      private static string ReadSection(string section, string source)
      {
         string BEGINMAP = "!beginmap " + section;
         const string ENDMAP = "!endmap";
         string fileName = source + ".maps";
         StringBuilder result = new StringBuilder();
         TextReader textReader = null;

         if (File.Exists(fileName))
           textReader = new StreamReader(fileName);
         else
         {
            ResourceManager rm = Resources.ResourceManager;
            ResourceSet set = rm.GetResourceSet(
               CultureInfo.CurrentCulture, true, true);

            const string NAME = "!name";
            foreach (DictionaryEntry o in set)
            {
               String resName = (string)o.Key;

               if (resName.EndsWith("maps"))
               {
                  string val = (string)o.Value;
                  string sectionName = val.Substring(
                     val.IndexOf(NAME) + NAME.Length, val.IndexOf(";") - NAME.Length).Trim();

                  if (sectionName.Equals(source))
                  {
                     textReader = new StringReader((string)o.Value);
                     break;
                  }
               }
            }
         }

         if (textReader != null)
         {
            string line = null;

            while ((line = textReader.ReadLine()) != null)
            {
               if (line.Equals(BEGINMAP))
               {
                  while ((line = textReader.ReadLine()) != null &&
                     !line.Equals(ENDMAP))
                     result.Append(line);

                  break;
               }
            }
         }

         return result.ToString();
      }

      private static string CutVarSection(string html, bool isVar, out string[] content)
      {
         string BEGINVAR = isVar ? "!var" : "!cont";
         string ENDVAR = isVar ? "!endvar" : "!endcont";

         int beginVarPos = html.IndexOf(BEGINVAR);
         int endVarPos = html.IndexOf(ENDVAR);
         
         string result = html.Substring(beginVarPos + BEGINVAR.Length, 
            endVarPos - beginVarPos - BEGINVAR.Length);

         content = new string[2];
         content[0] = html.Substring(0, beginVarPos);
         content[1] = html.Substring(endVarPos + ENDVAR.Length);

         return result;
      }

      public static string UserLocation(List<UserLocationData> loc)
      {
         if (loc.Count == 0)
         {
            return "Нет данных";
         }

         String source = Config.GetConfig().mapSource;
         if (source == null)
            return "";

         string[] content;
         string html = ReadSection("userlocation", source);
         string var = CutVarSection(html, true, out content);

         NumberFormatInfo nfi = new CultureInfo("en-US", false).NumberFormat;
         nfi.NumberDecimalSeparator = ".";

         String data = "";

         for (int i = 0; i < loc.Count; i++)
         {
            UserLocationData p = loc[i];

            if (p != null)
            {
               UserLocation pu = p.location;

               if (pu != null)
               {
                  string dataStr = var;
                  dataStr = dataStr.Replace(NAMESTR, p.UserName);
                  dataStr = dataStr.Replace(LONGITUDESTR, pu.longitude.ToString(nfi));
                  dataStr = dataStr.Replace(LATITUDESTR, pu.latitude.ToString(nfi));
                  dataStr = dataStr.Replace(COLORSTR, pu.isGSM == 0 ? "green" : "blue");
                  dataStr = dataStr.Replace(NUMBERSTR, p.Pos.ToString());
                  data += dataStr;
               }
            }
         }

         string result = content[0] + data + content[1];
#if DEBUG
         TextWriter tw = new StreamWriter("page1.html");
         tw.Write(result);
         tw.Close();
#endif
         return result;
      }


   }

   class ItemLoc
   {
      public double lat, lon;
      
      public ItemLoc(double lat, double lon)
      {
         this.lat = lat;
         this.lon = lon;
      }
   }

   class ItemLocCmp : IEqualityComparer<ItemLoc>
   {
      #region Члены IEqualityComparer<ItemLoc>

      public bool Equals(ItemLoc x, ItemLoc y)
      {
         return (x.lon == y.lon) && (x.lat == y.lat);
      }

      public int GetHashCode(ItemLoc obj)
      {
         double v = obj.lat + obj.lon * 1000;
         return v.GetHashCode();
      }

      #endregion
   }


}
