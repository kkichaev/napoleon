using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Resources;
using System.Windows;

namespace Ads2017
{
   class MapHelper
   {
      private string ReadResource(string map)
      {
         string result = string.Empty;

         var uri = new Uri(string.Format("/MapsWeb/{0}.html", map), UriKind.Relative);
         var resourceStream = Application.GetResourceStream(uri);

         using (var reader = new StreamReader(resourceStream.Stream))
         {
             result = reader.ReadToEnd();
         }

         return result;
      }

      public string CreateMap(string map, MapData data)
      {
         string result = string.Empty;

         if (!data.HasData())
            map = "Empty";

         string html = ReadResource(map);

         if (html != null)
         {
            string json = new JSon().Serialize(data);
            const String DATASECTION = "DATASECTION";
            result = html.Replace(DATASECTION, json);
#if DEBUG
            File.WriteAllText(map + ".html", result);
#endif
         }

         return result;
      }

      public List<string> ReadMaps(out int idx)
      {
         idx = -1;

         List<string> result = new List<string>();
         string s = (string)Application.Current.FindResource("mapSources");

         foreach (string m in s.Split(';'))
            result.Add(m);

         if (result.Count > 0)
            idx = result.IndexOf(Properties.Settings.Default.CurrentMap);

         return result;
      }

      public void SaveCurMap(string name)
      {
         Properties.Settings.Default.CurrentMap = name;
         Properties.Settings.Default.Save();
      }
   }
}
