using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   //Ќевозможно создать пользовател€
   public class ECantCreateUser : Exception
   { 
   }

   //Ќевозможно найти пользовател€
   public class EUserNotFound : Exception
   { 
   }

   // ласс содрежит текущего пользовател€, что работает в системе
   static class CurrentUser
   {
      public static Employee user;
      private static DataSet<string, DivisionManager> dsManager;
      private static DataSet<int, ManagerConfigObj> dsManagerConfig;

      public static void InitCurrentUser(List<IDataSet> refreshSet)
      {
         InitCurrentUser(refreshSet, false);
      }

      public static void Clear()
      {
         user = null;
      }

      public static void InitCurrentUser(List<IDataSet> refreshSet, bool forceReload)
      {
         if (user != null && !forceReload)
            return;

         dsManager = new DataSet<string, DivisionManager>(DivisionManager.OBJECT_NAME, false);
         Config cfg = Config.GetConfig();
         dsManager.Filter = "\"login\" = '" + cfg.login + "' and \"password\" = '" + cfg.password + "'";
         refreshSet.Add(dsManager);

         dsManagerConfig = new DataSet<int, ManagerConfigObj>(ManagerConfigObj.OBJECT_NAME, false);
         dsManagerConfig.Filter = "\"manager\" = '" + cfg.login + "'";
         refreshSet.Add(dsManagerConfig);

         DivisionList dl = DivisionList.GetDataSet();
         if (refreshSet.Contains(dl) == false)
         {
            refreshSet.Add(dl);
         }
      }

      public static void SetCurrentUser(bool throwException)
      {
         if (dsManager != null)
         {
            if (dsManager.Count > 0)
               foreach (DivisionManager dm in dsManager.Data)
               {
                  CreateCurrentUser(dm);
                  break;
               }
            else
            {
               Config c = Config.GetConfig();
               CreateCurrentUser(c.login, c.password, throwException);
            }
            dsManager = null;
         }
         else
            if (user != null)
               user.UpdateDivision();
      }

      //ћетод фабрика позвол€ет создать текущего пользовател€
      private static void CreateCurrentUser(string login, string password, bool throwException)
      {
         foreach (Agent a in DataModule.Get("Agents").Data)
         {
            if (a.login == login && a.password == password)
            {
               Division d = null;
               DivisionList dl = DataModule.Get("Division") as DivisionList;

               if ((d = dl.FindRelated(a)) != null)
               {
                  user = new Manager(a, d, dsManagerConfig, null, null);
               } else if ((d = dl.Find(a)) != null)
               {
                  user = new Employee(a, d);
               }
               else 
               {
                  if( throwException )
                     throw new ECantCreateUser();
               }
            }
         }

         if( user == null && throwException )
            throw new EUserNotFound();
      }

      private static void CreateCurrentUser(DivisionManager manager)
      {
         Agent agent = new Agent();
         agent.id = manager.login;
         agent.name = manager.login;
         agent.login = manager.login;
         agent.password = manager.password;

         DivisionList dl = DivisionList.GetDataSet();
         if (manager.division != 0 && dl.ContainsKey(manager.division))
         {
            Division division = dl[manager.division];
            agent.name = division.name;

            user = new Manager(agent, division, dsManagerConfig, manager.rights, manager);
         }
         else
            user = new Employee(agent, null);
      }
   }

   public class ManagerConfigObj : DataObject
   {
      static public readonly string OBJECT_NAME = "ManagerConfig";

      public string key;
      public string value;
   }

   public class ManagerConfig
   {
      public static readonly string CAN_CHANGE_KEY = "CanChangePassword";

      public bool canChangePassword = true;

      public void Load(DataSet<int, ManagerConfigObj> dsObjects)
      {
         foreach (ManagerConfigObj obj in dsObjects.Data)
         {
            if (obj.key == CAN_CHANGE_KEY)
            {
               int res = 0;
               Int32.TryParse(obj.value, out res);
               canChangePassword = (res != 0);
               break;
            }
         }
      }
   };

   public class Employee
   {
      protected Agent agent;
      protected Division division;

      public Agent User { get { return agent; } }

      public Employee(Agent agent, Division division)
      {
         this.agent = agent;
         this.division = division;
      }

      public Division Division { get { return division; } set { division = value; } }

      internal virtual Agents GetAgents()
      {
         Agents a = new Agents(false);
         a[agent.id] = agent;
         return a;
      }

      public virtual bool HaveRight(RightToken token, RightActions action) { return false; }

      internal void UpdateDivision()
      {
         if( division != null )
         {
            DivisionList dl = DivisionList.GetDataSet();
            if (dl.ContainsKey(division.id))
               division = dl[division.id];
         }
      }
   }

   public class Manager : Employee
   {
      public DivisionManager src;

      class Rights : List<DivisionManager.Rights>
      {
         public bool HaveRight(RightToken token, RightActions action)
         {
            foreach(DivisionManager.Rights r in this)
               if( r.token == token.key )
                  return action == RightActions.Read ? r.right >= 1 : r.right >= 2;

#if Servolux
            return false;
#else
            return true;
#endif
         }
      }

      private List<GRSoft.NapoleonManager.Division.DivisionAgent> agents;
      private List<Division> childs = new List<Division>();
      Rights rights = new Rights();

      public Manager(Agent agent, Division division, DataSet<int, ManagerConfigObj> dsObjects, List<DivisionManager.Rights> rights, DivisionManager src)
         : base(agent, division)
      {
         agents = division.agents;
         childs = division.Childs;
         
         config.Load(dsObjects);
         if (rights != null)
            this.rights.AddRange(rights);

         this.src = src;
      }

      public List<GRSoft.NapoleonManager.Division.DivisionAgent> Agents { get { return agents; } }
      public List<Division> Childs{get { return childs; } }

      public override bool HaveRight(RightToken token, RightActions action)
      {
         return rights.HaveRight(token, action);
      }

      void AddDivisions(List<Division> ret, List<Division> src)
      {
         foreach (Division d in src)
         {
            ret.Add(d);
            AddDivisions(ret, d.Childs);
         }
      }

      public List<Division> AllDivisions
      {
         get
         {
            List<Division> ret = new List<Division>();
            ret.Add(division);

            AddDivisions(ret, division.Childs);

            return ret;
         }
      }

      public ManagerConfig config = new ManagerConfig();

      Division TestDivision(Division d, Agent a)
      {
         if (d.HaveAgent(a))
            return d;

         foreach (Division ch in d.Childs)
         {
            Division ret = TestDivision(ch, a);
            if (ret != null)
               return ret;
         }

         return null;
      }

      public Division GetAgentDivision(Agent a)
      {
         return TestDivision(division, a);
      }

      internal override Agents GetAgents()
      {
         Agents ret = new Agents(false);
         foreach (Division.DivisionAgent a in division.GetAllAgents())
         {
            if (a.agent != null)
               ret[a.id] = a.agent;
         }
         return ret;
      }
   }
}
