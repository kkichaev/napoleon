using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Controls.Ribbon;
using System.Windows.Data;

namespace Ads2017
{
   class MapControlHelper
   {
      public MapHelper mapHelper = new MapHelper();

      public void InitControl(RibbonGallery rg, RibbonGalleryCategory gc)
      {
         gc.Items.Clear();
         int sel = -1;
         string[] maps = mapHelper.ReadMaps(out sel).ToArray();

         foreach (string m in maps)
            gc.Items.Add(m);
         
         rg.SelectedItem = Properties.Settings.Default.CurrentMap;
         rg.SelectionChanged += SelectionChanged;
      }

      private void SelectionChanged(object sender, System.Windows.RoutedPropertyChangedEventArgs<object> e)
      {
         RibbonGallery rg = (RibbonGallery)sender;
         mapHelper.SaveCurMap(rg.SelectedItem.ToString());
      }

      internal string CreateMap(string map, MapData data)
      {
         return mapHelper.CreateMap(map, data);
      }
   }
}
