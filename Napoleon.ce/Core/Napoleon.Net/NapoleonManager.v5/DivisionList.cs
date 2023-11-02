/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Коллекция подразделений
 * 
 * ert   03/05/2010   creating
 */

using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   internal class DivisionList : DataSet<int, Division>
   {
      public static string ObjName { get { return "Division"; } }

      public DivisionList()
         : base(ObjName)
      {
         //if (Format.Find("Division") == null)
         //{
         //   Format ch = new Format("Division$agents");
         //   ch.Add(new StringFormat("id"));
         //   Format.Add(ch);

         //   Format f = new Format("Division");
         //   f.Add(new NumberFormat("id", 0));
         //   f.Add(new StringFormat("name"));
         //   f.Add(new StringFormat("description"));
         //   f.Add(new StringFormat("cheif"));
         //   f.Add(new ObjectFormat("agents", "Division"));
         //   f.Add(new ObjectFormat("folder", "Division"));
         //   f.Add(new NumberFormat("parent", 0));
         //   Format.Add(f);
         //}
      }

      public DivisionList(bool addToDataModule)
         : base(ObjName, addToDataModule)
      {
      }

      protected override void LoadComplete()
      {
         foreach (KeyValuePair<int, Division> element in this)
            element.Value.SetReferences(this);

         base.LoadComplete();
      }

      internal int NextID()
      {
         int i = 1;
         foreach (KeyValuePair<int, Division> value in this)
         {
            if (i <= value.Value.id)
               i = value.Value.id + 1;
         }

         return i;
      }

      internal void CheckAgents()
      {
         foreach (KeyValuePair<int, Division> kv in this)
            kv.Value.CheckAgents();
      }

      internal Division Root
      {
         get
         {
            if (CurrentUser.user == null)
            {
               foreach (KeyValuePair<int, Division> value in this)
                  if (value.Value.parent == 0)
                     return value.Value;
               return null;
            }

            Division d = CurrentUser.user.Division;
            //if (ContainsValue(d) == false)
            //   throw new Exception("No user division in list");
            return d;
         }
      }

      internal List<Division> RemoveAgents(Agent[] agents)
      {
         Dictionary<Agent, bool> da = new Dictionary<Agent, bool>();
         foreach (Agent a in agents)
            if (da.ContainsKey(a) == false)
               da.Add(a, true);

         List<Division> changed = new List<Division>();
         foreach (KeyValuePair<int, Division> value in this)
         {
            if (value.Value.Remove(da))
               changed.Add(value.Value);
         }
         return changed;
      }

      internal Division Find(Agent agent)
      {
         Division finded = null;

         foreach (KeyValuePair<int, Division> value in this)
         {
            if (value.Value.HaveAgent(agent))
            {
               finded = value.Value;
               break;
            }
         }

         return finded;
      }

      internal Division FindRelated(Agent agent)
      {
         Division finded = null;

         foreach (KeyValuePair<int, Division> value in this)
         {
            if (value.Value.cheif != null && value.Value.cheif.id == agent.id)
            {
               finded = value.Value;
               break;
            }
         }

         return finded;
      }

      // список всех подчиненных (в подразделении и в подчиненных подразделениях)
      internal List<Agent> Subordinate(Agent cheif)
      {
         List<Agent> ret = new List<Agent>();
         Division d = FindRelated(cheif);
         if (d != null)
         {
            List<Division.DivisionAgent> daList = d.GetAllAgents();
            foreach(Division.DivisionAgent da in daList)
            {
               if (da.agent != null && !ret.Contains(da.agent))
                  ret.Add(da.agent);
            }
         }

         return ret;
      }

      internal List<Agent> Managers()
      {
         List<Agent> managers = new List<Agent>();
         foreach (KeyValuePair<int, Division> value in this)
         {
            Division d = value.Value;
            if (d.cheif != null && !managers.Contains(d.cheif))
               managers.Add(d.cheif);
         }

         return managers;
      }

      internal List<Agent> UnusedAgents()
      {
         List<Agent> unused = new List<Agent>();
         Dictionary<Agent, bool> used = new Dictionary<Agent, bool>();

         foreach (KeyValuePair<int, Division> value in this)
         {
            Division d = value.Value;
            if (d.cheif != null && !used.ContainsKey(d.cheif))
               used[d.cheif] = true;

            if (d.agents != null)
            {
               foreach (Division.DivisionAgent agent in d.agents)
               {
                  if (agent.agent != null && !used.ContainsKey(agent.agent))
                     used[agent.agent] = true;
               }
            }
         }

         DataSet<string, Agent> agents = DataModule.Get("Agents") as DataSet<string, Agent>;
         if (agents != null)
         {
            foreach (KeyValuePair<string, Agent> av in agents)
            {
               Agent a = av.Value;
               if (!used.ContainsKey(a))
                  unused.Add(a);
            }
         }
         return unused;
      }

      private void RemoveChildList(List<Division> childs, List<Division> rc, Manager m)
      {
         foreach (Division d in childs)
         {
            RemoveChildList(d.Childs, rc, m);

            rc.Add(d);
            Remove(d.id);

            if (m != null && m.Division == d)
               m.Division = null;
         }
      }

      internal List<Division> RemoveTree(Division rd)
      {
         List<Division> rc = new List<Division>();

         Manager m = CurrentUser.user as Manager;
         if(m != null &&  m.Division == rd)
            m.Division = null;

         RemoveChildList(rd.Childs, rc, m);

         rc.Add(rd);
         Remove(rd.id);

         return rc;
      }

      //Если дата модуль содержит объект данных, то возвращаем его, иначе 
      //создаем новый
      internal static DivisionList GetDataSet()
      {
         DivisionList result = DataModule.Get(ObjName) as DivisionList;

         if (result == null)
         {
            result = new DivisionList();
         }

         return result;
      }
   }
}
