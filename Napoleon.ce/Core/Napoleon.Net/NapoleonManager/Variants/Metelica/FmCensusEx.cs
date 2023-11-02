using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;

namespace GRSoft.NapoleonManager
{
   class FmCensusEx:FmCensus
   {
      private DataSet<string, Org> orgs;
      public FmCensusEx()
      {
         orgs = (DataSet<string, Org>)DataModule.Get(Org.OBJECT_NAME) ?? new DataSet<string, Org>(Org.OBJECT_NAME);
      }

      protected override List<IDataSet> CreateUpdateList()
      {
         List <IDataSet> result = base.CreateUpdateList();
         Manager m = CurrentUser.user as Manager;

         string filter = DataUtils.MakeFilterFromAgents(null, m.Division.GetAllAgents());
         orgs.Filter = filter;
         result.Add(orgs);

         return result;
      }

      protected override void RefreshDataEx(Dictionary<string, AgentData> agents)
      {
         foreach (Org o in orgs.Data)
            if (agents.ContainsKey(o.agent.id))
               agents[o.agent.id].AddOrg(o);

         foreach (AgentOrgTask t in tasks.Data)
            if (agents.ContainsKey(t.userid) && orgs.ContainsKey(t.id))
               agents[t.userid].AddTask(orgs[t.id], t);
      }

   }
}
