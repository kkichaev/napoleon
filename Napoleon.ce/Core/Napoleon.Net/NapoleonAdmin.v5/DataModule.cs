using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;
using System.ComponentModel;
using System.Threading;
using System.Net;
using System.Net.Sockets;

namespace GRSoft.NapoleonAdmin
{
   public class Agent : GRSoft.Network.DataObject
   {
      [KeyField]
      public string id = "";
      public string name = "";
      public string login = "";
      public string password = "";

      public string Name { get { return name; } }

      public override string ToString()
      {
         return name;
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
}
