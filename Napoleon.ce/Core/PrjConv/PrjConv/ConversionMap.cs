using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;

namespace PrjConv
{
   public abstract class ConversionMap
   {
      public Dictionary<string, KeyValuePair<string, string>> prjConv = new Dictionary<string, KeyValuePair<string, string>>();
      public Dictionary<string, string> solConv = new Dictionary<string, string>();
      public Dictionary<string, string> prjConvTextVal = new Dictionary<string, string>();

      protected abstract void Mapping();
   }

   public class ConversionMap2005 : ConversionMap
   {
      public ConversionMap2005()
      {
         Mapping();
      }

      protected override void Mapping()
      {
         solConv.Add("Format Version 10.00", "Format Version 9.00");
         solConv.Add("# Visual Studio 2008", "# Visual Studio 2005");
         prjConv.Add("Project", new KeyValuePair<string, string>("ToolsVersion", "2.0"));
         prjConv.Add("Import", new KeyValuePair<string, string>("Project", "$(MSBuildBinPath)\\Microsoft.CSharp.targets"));
         prjConvTextVal.Add("ProductVersion", "8.0.50727");
         prjConvTextVal.Add("SubType", "Component");

         Console.WriteLine(ConversionMapFactory.MAP_2005);
      }
   }

   public class ConversionMap2008 : ConversionMap
   {
      public ConversionMap2008()
      {
         Mapping();
      }

      protected override void Mapping()
      {
         solConv.Add("Format Version 9.00", "Format Version 10.00");
         solConv.Add("# Visual Studio 2005", "# Visual Studio 2008");
         prjConv.Add("Project", new KeyValuePair<string, string>("ToolsVersion", "3.5"));
         prjConv.Add("Import", new KeyValuePair<string, string>("Project", "$(MSBuildToolsPath)\\Microsoft.CSharp.targets"));
         prjConvTextVal.Add("ProductVersion", "9.0.30729");
         prjConvTextVal.Add("SubType", "UserControl");
      }
   }

   public class ConversionMapFactory
   {
      public const string MAP_2005 = "2005";
      public const string MAP_2008 = "2008";

      public static ConversionMap GetMap(string mapName)
      {
         switch (mapName)
         {
            case MAP_2005: return new ConversionMap2005();
            case MAP_2008: return new ConversionMap2008();
            default: return null;
         }
      }
   }
}
