using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public class Factory : DataObject
   {
      public static readonly string OBJECT_NAME = "Firms";
      private static Dictionary<String, String> dogCache = new Dictionary<string, string>();

      [KeyField]
      public string id = "";
      public string name = "";
      public string shortName = "";
      public double dropSize = 0;


      public Factory(string val)
      {
         string[] tv = val.Split(new char[] { '\t' });
         name = tv[0];
         if (tv.Length > 1)
            id = tv[1];
      }

      public Factory() { }

      public override string ToString()
      {
         return name;
      }

      //public static List<Factory> GetFactories(SimpleDataSet<OrderAddConfig> config)
      public static List<Factory> GetFactories()
      {
         List<Factory> ret = new List<Factory>();
         DataSet<string, Factory> fc =  (DataSet<string, Factory>)DataModule.Get(OBJECT_NAME);
         if (fc != null)
            foreach (Factory f in fc.Data)
               ret.Add(f);

         //foreach (OrderAddConfig kv in config.Data)
         //{
         //   if (kv.key == "Организация")
         //   {
         //      foreach (String iv in kv.value.Split(new char[] { ';' }))
         //         ret.Add(new Factory(iv));
         //      break;
         //   }
         //}
         return ret;
      }

      public static Factory Get(string firmCode)
      {
         Factory f = new Factory();
         DataSet<string, Factory> fc = (DataSet<string, Factory>)DataModule.Get(OBJECT_NAME);
         if (fc != null && fc.ContainsKey(firmCode))
            f = fc[firmCode];

         return f;
      }

      public static bool HaveFirm(String ido, String firmId, IDataSet dogovors)
      {
         if (dogCache.Count == 0)
         {
            foreach (OrgDogovor od in dogovors.Data)
            {
               if (od == null)
                  continue;
               string value = od.firm + "|";
               if (dogCache.ContainsKey(od.ido))
                  dogCache[od.ido] += value;
               else
                  dogCache.Add(od.ido, value);
            }
         }

         if (dogCache.ContainsKey(ido))
            return dogCache[ido].Contains(firmId);

         return false;
      }
   }

   public class AgentDailyPlanData
   {
      public double plan;
      public double order;
      public double planChanges;
   }
}
