using GRSoft.NapoleonManager.Properties;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Reflection;
using System.Resources;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class MapHelper
   {
      private string ReadResource(string map, string file)
      {
         ResourceManager rm = Resources.ResourceManager;
         ResourceSet set = rm.GetResourceSet(CultureInfo.CurrentCulture, true, true);

         string key = string.Format("{0}_{1}", map, file);

         return set.GetString(key);
      }

      public string CreateMap(string map, string file, object data)
      {
         string result = string.Empty;

         string html = ReadResource(map, file);

         if (html != null)
         {
            string json = new JSon().Serialize(data);
            const String DATASECTION = "DATASECTION";
            result = html.Replace(DATASECTION, json);
#if DEBUG
            File.WriteAllText(file + ".html", result);
#endif
         }

         return result;
      }

      public List<string> ReadMaps(out int idx)
      {
         idx = -1;

         List<string> result = new List<string>();

         foreach (string m in Properties.Resources.mapSources.Split(';'))
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

      public void InitControl(ToolStripComboBox cb)
      {
         cb.Items.Clear();
         int sel = -1;
         cb.Items.AddRange(ReadMaps(out sel).ToArray());
         cb.SelectedIndexChanged += new EventHandler((s,e)=>{ 
            string map = ((ToolStripComboBox)s).SelectedItem.ToString();
            SaveCurMap(map);
         });

         cb.SelectedIndex = sel;
      }
   }
}
