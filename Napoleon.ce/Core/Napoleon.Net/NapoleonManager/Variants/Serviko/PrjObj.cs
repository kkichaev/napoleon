using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   partial class AgentAssortMtx : DataObject
   {
      public static readonly string OBJECT_NAME = "AgentAssortMtx";

      [KeyField]
      public String userid;
      public String matrix;

      [Reference("Agents", "userid")]
      public Agent agent = null;
   }
}
