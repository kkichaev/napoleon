using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Globalization;
using System.Resources;
using System.Collections;
using System.Reflection;
using GRSoft.Ads;
using GRSoft.Ads.Properties;
using GRSoft.Ads.Utils;

namespace GRSoft.Ads
{
   class MapEngine
   {
      public static readonly string NAMESTR = "##name##";
      public static readonly string LONGITUDESTR = "##longitude##";
      public static readonly string LATITUDESTR = "##latitude##";
      public static readonly string NUMBERSTR = "##number##";
      public static readonly string COLORSTR = "##color##";
      public static readonly string DESCRSTR = "##descr##";

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

         return result;
      }

      public static string BrigadeAddress(string source, List<BrigadeAddress> adrList)
      {
         if (source != null && adrList != null && adrList.Count <= 0)
            return string.Empty;

         string html = ReadSection("brigade_location", source);
         if (html.Trim().Length == 0)
            return SOURCE_MAP_ERR_MSG;

         StringBuilder data = new StringBuilder();
         string[] c1;
         string var1 = CutVarSection(html, true, out c1);
         
         int index = 1;
         NumberFormatInfo nfi = new CultureInfo("en-US", false).NumberFormat;
         nfi.NumberDecimalSeparator = ".";

         foreach (BrigadeAddress ba in adrList)
         {
            string dataStr = var1;

            dataStr = dataStr.Replace(NAMESTR, index++.ToString());
            dataStr = dataStr.Replace(DESCRSTR, ba.address);
            dataStr = dataStr.Replace(LONGITUDESTR, ba.longitude.ToString(nfi));
            dataStr = dataStr.Replace(LATITUDESTR, ba.latitude.ToString(nfi));

            data.Append(dataStr);
         }

         return c1[0] + data.ToString() + c1[1];
      }

      public static string OrderMap(string source, List<BrigadeInfo> brigadeList, ClientInfo clientInfo)
      {
         if (source != null && brigadeList != null && brigadeList.Count <= 0)
            return string.Empty;

         string html = ReadSection("order_map", source);
         
         if (html.Trim().Length == 0)
            return SOURCE_MAP_ERR_MSG;

         StringBuilder data = new StringBuilder();
         string[] c1, c2, c3;
         string var1 = CutVarSection(html, true, out c1);
         string var2 = CutVarSection(c1[1], true, out c2);
         string var3 = CutVarSection(c2[1], true, out c3);

         int index = 1;
         NumberFormatInfo nfi = new CultureInfo("en-US", false).NumberFormat;
         nfi.NumberDecimalSeparator = ".";

         StringBuilder data1 = new StringBuilder();

         foreach (BrigadeInfo bi in brigadeList)
         {
            string dataStr = var1;

            dataStr = dataStr.Replace(NAMESTR, index++.ToString());
            dataStr = dataStr.Replace(DESCRSTR, bi.brigade.Name);
            dataStr = dataStr.Replace(LONGITUDESTR, bi.location.Longitude.ToString(nfi));
            dataStr = dataStr.Replace(LATITUDESTR, bi.location.Latitude.ToString(nfi));

            data.Append(dataStr);

            int index2 = 1;
            foreach (OrderInfo oi in bi.order)
            {
               string dataStr2 = var3;

               dataStr2 = dataStr2.Replace(NAMESTR, index2++.ToString());
               StringBuilder text = new StringBuilder();
               text.Append(oi.order.client.Address).Append("<br>");
               text.Append(oi.order.BrigadeName).Append("<br>");
               text.Append(oi.order.Text).Append("<br>");
               text.Append(oi.order.WorkTimeStr);
               dataStr2 = dataStr2.Replace(DESCRSTR, text.ToString());
               dataStr2 = dataStr2.Replace(LONGITUDESTR, oi.location.Longitude.ToString(nfi));
               dataStr2 = dataStr2.Replace(LATITUDESTR, oi.location.Latitude.ToString(nfi));
               dataStr2 = dataStr2.Replace(COLORSTR, bi.color.Name.ToLower());

               data1.Append(dataStr2);
            }
         }

         var2 = var2.Replace(NAMESTR, index++.ToString());
         var2 = var2.Replace(DESCRSTR, clientInfo.client.Name);
         var2 = var2.Replace(LONGITUDESTR, clientInfo.location.Longitude.ToString(nfi));
         var2 = var2.Replace(LATITUDESTR, clientInfo.location.Latitude.ToString(nfi));

         data.Append(var2);
         data.Append(data1.ToString());

         return c1[0] + data.ToString() + c3[1];;
      }

      public const string SOURCE_MAP_ERR_MSG = "Ошибка, невозможно прочитать файл карты, проверьте правильность настройки источника карт.";
      public static string TraceRoute(string source, 
         List<Location> listLocation, List<VisitQueueItem> visitQueue,
         List<RoadPoint> roadPoint)
      {
         return RouteDistination(source, listLocation, visitQueue, roadPoint, null);
      }

      public static string ReadSection(string section, string source)
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

      public static string CutVarSection(string html, bool isVar, out string[] content)
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

      public static string RouteDistination(string source,
         List<Location> listLocation, List<VisitQueueItem> visitQueue,
         List<RoadPoint> roadPoint, BrigadeAddress dist)
      {
         if (source == null || source.Length <= 0)
            return string.Empty;

         NumberFormatInfo nfi = new CultureInfo("en-US", false).NumberFormat;
         nfi.NumberDecimalSeparator = ".";
         string html = ReadSection(dist == null ? "traceroute" : "route_distination", source);

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

         string[] c1, c2, c3, c4, c5 = null;

         string var1 = CutVarSection(html, true, out c1);
         string var2 = CutVarSection(c1[1], true, out c2);
         string var3 = CutVarSection(c2[1], true, out c3);

         string var4 = string.Empty; 
         string var5 = string.Empty;

         string endHtml = c3[1];

         if (dist != null)
         {
            var4 = CutVarSection(c3[1], true, out c4);
            var5 = CutVarSection(c4[1], true, out c5);
            endHtml = c5[1];
         }

         StringBuilder data = new StringBuilder();

         if (visitQueue != null && visitQueue.Count > 0)
         {
            foreach (VisitQueueItem visitItem in visitQueue)
            {
               if (visitItem.latitude == 0 && visitItem.longitude == 0)
                  continue;

               string dataStr = var1;
               dataStr = dataStr.Replace(NUMBERSTR, visitItem.VisitNumber.ToString());

               if (visitItem.objType.IsStopType)
               {
                  dataStr = dataStr.Replace(NAMESTR, "P");
                  dataStr = dataStr.Replace(DESCRSTR, StringUtil.EscapeQuotes("Остановка " + visitItem.StopTime));
               }
               else
               {
                  dataStr = dataStr.Replace(NAMESTR, visitItem.VisitNumber.ToString());
                  dataStr = dataStr.Replace(DESCRSTR, StringUtil.EscapeQuotes(visitItem.OrgName));
               }

               dataStr = dataStr.Replace(LONGITUDESTR, visitItem.longitude.ToString(nfi));
               dataStr = dataStr.Replace(LATITUDESTR, visitItem.latitude.ToString(nfi));
               data.AppendLine(dataStr);
            }
         }

         data.AppendLine(c2[0]);

         bool isGsm = false;
         if (listLocation != null && listLocation.Count > 0)
         {
            int locNumber = 0;
            string[] c21;
            string var21 = CutVarSection(var2, false, out c21);
            bool first = true;

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
                  isGsm = loc.IsGsm;
                  data.Append(dataStr);
               }

               if (isGsm != loc.IsGsm || loc == listLocation[listLocation.Count - 1])
               {
                  dataStr = c21[1].Replace(COLORSTR, isGsm ? GSM_COLOR : GPS_COLOR);
                  dataStr = dataStr.Replace(NUMBERSTR, locNumber.ToString());
                  dataStr = dataStr.Replace(LATITUDESTR, loc.Latitude.ToString(nfi));
                  dataStr = dataStr.Replace(LONGITUDESTR, loc.Longitude.ToString(nfi));
                  first = true;
                  if (data[data.Length - 1] == ',')
                     data.Remove(data.Length - 1, 1);
                  data.AppendLine(dataStr);
                  locNumber++;
               }
               else
                  data.AppendFormat("{0},", contStr);
            }
         }

         if (roadPoint != null && roadPoint.Count > 0)
         {
            foreach (RoadPoint rp in roadPoint)
            {
               if (rp.loc.Latitude == 0 && rp.loc.Longitude == 0)
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

         if (listLocation != null && listLocation.Count > 0)
         {
            string dataStr = var4;
            Location loc = listLocation[listLocation.Count - 1];
            dataStr = dataStr.Replace(LONGITUDESTR, loc.Longitude.ToString(nfi));
            dataStr = dataStr.Replace(LATITUDESTR, loc.Latitude.ToString(nfi));
            dataStr = dataStr.Replace(DESCRSTR, StringUtil.EscapeQuotes("текущее положение"));
            data.AppendLine(dataStr);
         }

         if (dist != null)
         {
            string dataStr = var5;
            dataStr = dataStr.Replace(LONGITUDESTR, dist.longitude.ToString(nfi));
            dataStr = dataStr.Replace(LATITUDESTR, dist.latitude.ToString(nfi));
            dataStr = dataStr.Replace(DESCRSTR, StringUtil.EscapeQuotes(dist.address));
            data.AppendLine(dataStr);
         }

         if (data[data.Length - 1] == ',')
            data.Remove(data.Length - 1, 1);

         string result = c1[0] + data + endHtml;

         /*
          * DEBUG DEBUG DEBUG
          * 
         TextWriter tw = new StreamWriter("page.html");
         tw.Write(result);
         tw.Close();
          * 
          * DEBUG DEBUG DEBUG
          */

         return result;
      }
   }
}
