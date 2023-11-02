using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   public partial class OrgRemnantsItem : DataObject
   {
      public double shelf;

      public bool IsSklad { get { return qty != 0; } }
      public bool IsShelf { get { return shelf != 0; } } 
   }

   public partial class Org
   {
      [ItemType(typeof(OrgContact))]
      public List<OrgContact> contacts = new List<OrgContact>();

      public string Contact { get { return contacts.Count == 0 ? string.Empty : contacts[0].name; } 
         set
         {
            if (contacts.Count == 0)
               contacts.Add(new OrgContact());

            contacts[0].name = value;
         } 
      }

      public string Phone { get { return contacts.Count == 0 ? string.Empty : contacts[0].phone; } 
         set 
         {
            if (contacts.Count == 0)
               contacts.Add(new OrgContact());

            contacts[0].phone = value;
         } 
      }

      public string Userid { get { return userid; } set { userid = value; } }
      public string NName { get { return name; } set { name = value; } }
      public string AgentName { get { return agent != null ? agent.Name : string.Empty; } }
   }

   public class OrgContact : DataObject
   {
      public string name = string.Empty;
      public string phone = string.Empty;
   }

   public partial class PotenzialOrg
   {
      public bool converted = false;
   }

   public partial class OrgFolder
   {
      public DateTime date = DateTime.MinValue;
   }

   public partial class OrgFolderItem
   {
      public string comment = "";
   }
}
