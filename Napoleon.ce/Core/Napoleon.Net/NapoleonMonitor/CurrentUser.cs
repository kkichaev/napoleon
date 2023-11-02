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
      static DataSet<string, NBTLViewer> dsViewers;

      public static void InitCurrentUser(List<IDataSet> refreshSet)
      {
         InitCurrentUser(refreshSet, false);
      }

      public static void Clear()
      {
         user = null;
      }

      public static void SetViewers(DataSet<string, NBTLViewer> dsViewers)
      {
         CurrentUser.dsViewers = dsViewers;
      }

      public static void InitCurrentUser(List<IDataSet> refreshSet, bool forceReload)
      {
         if (MainForm.Instance.CheckIsMainDataPresents(true) == false)
            return;

         if (user != null && !forceReload)
            return;

         dsManager = new DataSet<string, DivisionManager>(DivisionManager.OBJECT_NAME, false);
         Config cfg = Config.GetConfig();
         dsManager.Filter = "\"login\" = '" + cfg.login + "' and \"password\" = '" + cfg.password + "'";
         refreshSet.Add(dsManager);

         dsManagerConfig = new DataSet<int, ManagerConfigObj>(ManagerConfigObj.OBJECT_NAME, false);
         dsManagerConfig.Filter = "\"manager\" = '" + cfg.login + "'";
         refreshSet.Add(dsManagerConfig);

         DivisionList dl = DataModule.Get("Division") as DivisionList;
         if (dl == null)
         {
            dl = new DivisionList();
            refreshSet.Add(dl);
         }
      }

      public static void SetCurrentUser(bool throwException)
      {
         Config c = Config.GetConfig();
         if (dsViewers.ContainsKey(c.login))
         {
            CreateCurrentUser(dsViewers[c.login]);
         }
         //if (dsManager != null )
         //{
         //   if (dsManager.Count > 0)
         //      foreach (DivisionManager dm in dsManager.Data)
         //      {
         //         CreateCurrentUser(dm);
         //         break;
         //      }
         //   else
         //   {
         //      Config c = Config.GetConfig();
         //      CreateCurrentUser(c.login, c.password, throwException);
         //   }
         //   dsManager = null;
         //}
      }

      private static void CreateCurrentUser(NBTLViewer user)
      {
         Agent agent = new Agent();
         agent.id = "^!#$Manager";
         agent.name = user.name;
         agent.login = user.id;
         agent.password = user.password;

         DivisionList dl = DataModule.Get("Division") as DivisionList;
         if (dl.ContainsKey(user.division))
            CurrentUser.user = new Manager(agent, dl[user.division], dsManagerConfig, user.contracts);
         else
            throw new EUserNotFound();
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

      public Division Division { get { return division; } }

      internal virtual Agents GetAgents()
      {
         Agents a = new Agents(false);
         a[agent.id] = agent;
         return a;
      }

      public virtual bool HaveContract(string cid) { return false; }
   }

   public class Manager : Employee
   {
      private List<GRSoft.NapoleonManager.Division.DivisionAgent> agents;
      private List<Division> childs = new List<Division>();
      List<string> contracts = new List<string>();

      public Manager(Agent agent, Division division, DataSet<int, ManagerConfigObj> dsObjects, List<NBTLViewer.Item> contracts)
         : base(agent, division)
      {
         agents = division.agents;
         childs = division.Childs;
         
         //config.Load(dsObjects);
         foreach(NBTLViewer.Item i in contracts)
            this.contracts.Add(i.id);
      }

      public List<GRSoft.NapoleonManager.Division.DivisionAgent> Agents { get { return agents; } }
      public List<Division> Childs{get { return childs; } }

      public override bool HaveContract(string cid)
      {
         return contracts.Contains(cid);
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
