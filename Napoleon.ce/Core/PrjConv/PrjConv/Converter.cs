using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Xml;

namespace PrjConv
{
   public class Converter
   {
      public static void makeSolConv(string path, Dictionary<string, string> map)
      {
         string context = File.ReadAllText(path,Encoding.UTF8);

         foreach (KeyValuePair<string, string> pair in map)
         {
            context = context.Replace(pair.Key, pair.Value);
         }

         File.WriteAllText(path, context, Encoding.UTF8);
      }

      public static void makePrjConv(string path, Dictionary<string, KeyValuePair<string, string>> val,
         Dictionary<string, string> textVal)
      {
         XmlDocument document = new XmlDocument();
         document.Load(path);

         foreach (KeyValuePair<string, KeyValuePair<string, string>> VNode in val)
         {
            
            if (document.GetElementsByTagName(VNode.Key).Item(0).Attributes[VNode.Value.Key] == null)
            {
               XmlAttribute attr =  document.CreateAttribute(VNode.Value.Key);
               attr.Value = VNode.Value.Value;
               document.GetElementsByTagName(VNode.Key).Item(0).Attributes.Prepend(attr);
            }
            else
            {
               document.GetElementsByTagName(VNode.Key).Item(0).Attributes[VNode.Value.Key].Value = VNode.Value.Value;
            }
         }

         foreach(KeyValuePair<string,string> VTNode in textVal)
         {
            if (VTNode.Key.Equals("SubType"))
            {
               foreach (XmlNode node in document.GetElementsByTagName(VTNode.Key))
               {
                  if (node.InnerText.Equals("UserControl") || node.InnerText.Equals("Component"))
                  {
                     node.InnerText = VTNode.Value;
                  }
               }
            }
            else
            {
               document.GetElementsByTagName(VTNode.Key).Item(0).InnerText = VTNode.Value;
            }
         }

         document.Save(path);
      }
   }

   
}
