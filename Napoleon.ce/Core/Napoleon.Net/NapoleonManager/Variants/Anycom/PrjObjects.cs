using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GRSoft.NapoleonManager
{
   public class AgentPlan : DataObject
   {
      public static readonly string OBJECT_NAME = "AgentPlan";

      public bool dirty = false;
      public string userid = "";
      public DateTime begin = DateTime.Now;

      public bool Empty { get { return plans.Count == 0; } }

      public List<Item> plans = new List<Item>();
      public class Item : DataObject
      {
         public string id = "";
         
         [Reference("ManagerFolder", "id")]
         public ManagerFolder folder = null;

         public int akb = 0;
         public double order = 0;

         public bool Empty { get { return akb == 0 && order == 0; } }

         public string Name { get { return folder != null ? folder.name : ""; } }
         public double Order { get { return order; } set { order = value; } }

         public int AKB { get { return akb; } set { akb = value; } }
      }
   }

   partial class Agent
   {
      public string snils = "";
   }
}
