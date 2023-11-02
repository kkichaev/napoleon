using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Globalization;
using System.IO;
using System.Reflection;
using System.Text;
using GRSoft.Network;

namespace GRSoft.NapoleonAdmin
{
   public delegate void EmptyParamHandler();

   internal interface IFormDecorator
   {
      void Decorate(System.Windows.Forms.Form form);
   }

   class EmptyDecorator : IFormDecorator
   {
      #region Члены IFormDecorator

      public void Decorate(System.Windows.Forms.Form form)
      {
      }

      #endregion
   }

   public partial class Agent : DataObject, IComparable<Agent>
   {
      [KeyField]
      public string id = "";
      public string name = "";
      public string login = "";
      public string password = "";
      public string progid = "";

      public string Name { get { return name; } }

      public override string ToString()
      {
         return name;
      }

#if Vyatich
      public string kisID = "";
#endif

      public int CompareTo(Agent other)
      {
         return name.CompareTo(other.name);
      }
   }

   public class UserLog : GRSoft.Network.DataObject
   {
      [Reference("Agents", "userid")]
      public Agent agent = null;
      public DateTime date = DateTime.Now;
      public string objType = "";
      public DateTime objDate = DateTime.Now;

      public DateTime Date { get { return objDate; } }
      public string Agent { get { return (agent == null) ? "?" : agent.name; } }
      public string Action
      {
         get
         {
            switch (objType)
            {
               case "Order":
                  return "Заявка";
               case "OrgRemnants":
                  return "Съем остатков";
               case "Visit":
                  return "Посещение";
            }

            return "";
         }
      }
   }

   public class LicenseType : DataObject
   {
      public static readonly string OBJECT_NAME = "LicenseType";

      [KeyField]
      public string type = "";
      public string title = "";
      public int forAgents = 0;
   }

   public class LicenseProjectData : DataObject
   {
      public static readonly string OBJECT_NAME = "LicenseProjectData";

      [KeyField]
      public int id = 0;
      public string type = "";
      public int count = 0;

      public DateTime start = DateTime.Now;
      public DateTime end = DateTime.Now;
   }

   public class LicensingUsersData : DataObject
   {
      public static readonly string OBJECT_NAME = "LicensingUsersData";

      [KeyField]
      public string login = "";
      public int licenseID = 0;
   }

   internal class LicenseCount : DataObject
   {
      public int pda = 0;
      public int manager = 0;
      public int exclusiveManager = 0;
   }

   internal class LicensedUser : DataObject
   {
      [KeyField]
      public string id = String.Empty;
      public string type = LicensedUsers.PDA.Type;

      [Reference("Agents", "id")]
      public Agent agent = null;
   }

   internal class LicenseCountEx : DataObject
   {
      public static readonly string OBEJCT_NAME = "LicenseCountEx";
      [KeyField]
      public string type = "";
      public int count = 0;
   }

   public partial class DivisionManager : DataObject
   {
      public static readonly string OBJECT_NAME = "DivisionManager";

      [KeyField]
      public string id = "";

      public string login = "";
      public string password = "";
      public int division = 0;
      public string name = "";

      public class Rights : DataObject
      {
         public string token = "";
         public int type = 0;
         public int right = 0;

         public bool HaveEmptyToken { get { return token.Trim().Length == 0; } }
      }
      public List<Rights> rights = new List<Rights>();

      public void AddRights(List<Rights> src)
      {
         foreach(Rights r in src)
         {
            bool finded = false;
            foreach(Rights dr in rights)
            {
               if(dr.token == r.token)
               {
                  dr.right = r.right;
                  dr.type = r.type;
                  finded = true;
                  break;
               }
               if(!finded)
               {
                  Rights newR = new Rights();
                  newR.token = r.token;
                  newR.type = 0;
                  newR.right = (int)r.right;
                  rights.Add(newR);
               }
            }
         }
      }

      public bool RemvoeBadRights()
      {
         bool updated = false;

         List<Rights> rmv = new List<Rights>();
         foreach(Rights r in rights)
         {
            if(r.HaveEmptyToken)
            {
               rmv.Add(r);
            }
         }

         if(rmv.Count > 0)
         {
            updated = true;
            rmv.ForEach(x => rights.Remove(x));
         }

         return updated;
      }

      public bool HaveRight(RightToken token, RightActions action)
      {
         foreach(Rights r in rights)
            if( r.token == token.key )
               return r.right >= (int)action;

#if Servolux || SPK || NBtl
         return false;
#else
         return true;
#endif
      }

      public void ChangeRight(RightToken token, RightActions action)
      {
         foreach(Rights r in rights)
            if( r.token == token.key )
            {
               r.right = (int)action;
               return;
            }

         Rights newR = new Rights();
         newR.token = token.key;
         newR.type = 0;
         newR.right = (int)action;
         rights.Add(newR);
      }

      public void RemoveRight(RightToken token)
      {
         foreach (Rights r in rights)
            if (r.token == token.key)
            {
               rights.Remove(r);
               return;
            }
      }
   }

   public class ManagerActivity : DataObject
   {
      public static readonly string OBJECT_NAME = "%ActiveUsers";

      public string userid = "";
      public int duration;
      public int isExclusive;
      public string ip = "";
   }

   public class Agents : DataSet<string, Agent>
   {
      public static readonly string OBJECT_NAME = "Agents";
      public Agents()
         : base(OBJECT_NAME, true, true)
      {
      }

      public Agents(bool addToDataModule)
         : base(OBJECT_NAME, addToDataModule, true)
      {
      }

      public static Agents GetDataSet()
      {
         if (DataModule.Get(OBJECT_NAME) == null)
         {
            return new Agents();
         }

         return (Agents)DataModule.Get(OBJECT_NAME);
      }
   }

   public class UserActivity : DataObject
   {
      [KeyField]
      public string id = string.Empty;
      public DateTime date = DateTime.Now;
      public string version = string.Empty;
   }

   public class LogData : DataObject, IComparable<LogData>
   {
      public DateTime date = DateTime.Now;
      public string text = String.Empty;

      public string Date { get { return date.ToString("dd.MM.yyyy HH:mm:ss"); } }
      public string Event { get { return text; } }

      public int CompareTo(LogData other)
      {
         return other.date.CompareTo(date);
      }
   }

   public class ServerConfig : DataObject
   {
      public static readonly string OBJECT_NAME = "ServerConfig";

      [KeyField]
      public string userid = string.Empty;
      [KeyField]
      public string key = string.Empty;
      public string value = string.Empty;
   }

   public class Division : DataObject
   {
      public static readonly string OBJECT_NAME = "Division";

      public class DivisionAgent : DataObject
      {
         [Reference("Agents", "id")]
         public Agent agent = null;

         public string id = "";

         public string AgentName
         {
            get { return (agent == null) ? "?" : agent.name; }
         }
      }

      [KeyField]
      public int id = 0;

      public string name = "";
      public string description = "";

      [Reference("Agents", "cheif")]
      public Agent cheif = null;

      [ItemType(typeof(DivisionAgent))]
      public List<DivisionAgent> agents = new List<DivisionAgent>();

      public int parent = 0;

      public override string ToString()
      {
         return name;
      }

      public Division Self { get { return this; } }

      public string DivisionName
      {
         get { return name; }
      }

      public static Division Empty = new Division();
   }

   public class ManagerConfigObj : DataObject
   {
      static public readonly string OBJECT_NAME = "ManagerConfig";

      public string manager;
      public string key;
      public string value;
   }

   public partial class SyncInfo : DataObject
   {
      static public readonly string OBJECT_NAME = "SyncInfo";

      public DateTime created = DateTime.MinValue;
      public int syncparam = 0;
      public string userid = string.Empty;

      [Reference("Agents", "userid")]
      public Agent agent = null;

      public DateTime Date { get { return created; } }
      public string Agent { get { return agent != null ? agent.ToString() : userid; } }
      public string Info { get { return SyncInfoHelper.InfoToString(this); } }
   }

   internal class SyncInfoHelper
   {
      public static readonly int CLEAR 		= 1;
	   public static readonly int GEN_DATA 	= 2;
	   public static readonly int DOCS 		= 4;
	   public static readonly int VISIT 		= 8;
	   public static readonly int INCASS 		= 16;
	   public static readonly int PRESENT     = 32;
	   public static readonly int COST        = 64;
	   public static readonly int DEBT 		= 128;
      public static readonly int RESTORE = 256;

      public static string InfoToString(SyncInfo info)
      {
         const String DLM = " \\ ";
         StringBuilder sb = new StringBuilder();

         if ((info.syncparam & CLEAR) == CLEAR)
         {
            if (sb.Length > 0)
               sb.Append(DLM);
            sb.Append("очистка");
         }

         if ((info.syncparam & GEN_DATA) == GEN_DATA)
         {
            if (sb.Length > 0)
               sb.Append(DLM);
            sb.Append("основные данные");
         }

         if ((info.syncparam & DOCS) == DOCS)
         {
            if (sb.Length > 0)
               sb.Append(DLM);
            sb.Append("документы");
         }

         if ((info.syncparam & VISIT) == VISIT)
         {
            if (sb.Length > 0)
               sb.Append(DLM);
            sb.Append("посещения");
         }

         if ((info.syncparam & INCASS) == INCASS)
         {
            if (sb.Length > 0)
               sb.Append(DLM);
            sb.Append("инкассации");
         }

         if ((info.syncparam & PRESENT) == PRESENT)
         {
            if (sb.Length > 0)
               sb.Append(DLM);
            sb.Append("презентация");
         }

         if ((info.syncparam & COST) == COST)
         {
            if (sb.Length > 0)
               sb.Append(DLM);
            sb.Append("цены");
         }

         if ((info.syncparam & DEBT) == DEBT)
         {
            if (sb.Length > 0)
               sb.Append(DLM);
            sb.Append("долги");
         }

         if ((info.syncparam & RESTORE) == RESTORE)
         {
            if (sb.Length > 0)
               sb.Append(DLM);
            sb.Append("восстановление");
         }

         return sb.ToString();
      }
   }

   public class ReqConnect
   {
      public string id = "";
      public string type = "";
      public string code = "";
      public int till = 0;
   }

   public class LinkUser
   {
      public string id = "";
      public string type = "";

      public LinkUser(DataItem src)
      {
         id = src.Id;
         type = src.Type;
      }
   }

   public class LinkedUsers : DataObject
   {
      public static readonly string OBJECT_NAME = "LinkedUsers";

      [KeyField]
      public string id = "";
      public string uuid = "";

   }
}
