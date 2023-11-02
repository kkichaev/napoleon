using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class AgentBand : Band
   {
      private Agent agent;
      public AgentBand(Agent agent)
         : base(agent.Name)
      {
         this.agent = agent;
      }

      public Agent Agent { get { return agent; } }
   }
}
