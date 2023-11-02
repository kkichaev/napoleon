using GRSoft.Network;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   public class OrgCash
   {
      private Dictionary<string, CashItem> items = new Dictionary<string, CashItem>();
      private Dictionary<string, AddressItem> address = new Dictionary<string, AddressItem>();

      public void Load()
      {
         items.Clear();
         address.Clear();

         DataSet<string, Org> dsOrg = DataModule.Get(Org.COMMON_OBJECT_NAME) as DataSet<string, Org>;
         DataSet<string, Cagent> dsCagent = DataModule.Get(Cagent.OBJECT_NAME) as DataSet<string, Cagent>;

         if (dsOrg != null && dsCagent != null)
         {
            foreach(Cagent c in dsCagent.Values)
            {
               CashItem ci = new CashItem();
               ci.cagent = c;

               items[c.id] = ci;
            }

            foreach (Org o in dsOrg.Values)
            {
               if (items.ContainsKey(o.ido))
               {
                  CashItem ci = items[o.ido];
                  AddressItem a = new AddressItem(ci, o);
                  address[o.id] = a;
                  ci.address.Add(a);
               }
            }
         }
      }

      public ICollection Items { get { return items.Values; } }

      public CashItem FindParent(string id)
      {
         CashItem result = null;

         if (address.ContainsKey(id))
            result = address[id].parent;

         return result;
      }

      internal AddressItem GetAddress(string id)
      {
         AddressItem result = null;

         if (address.ContainsKey(id))
            result = address[id];

         return result;
      }
   }

   public class CashItem 
   {
      public Cagent cagent;
      public List<AddressItem> address = new List<AddressItem>();

      public override string ToString()
      {
         return cagent != null ? cagent.name : string.Empty; 
      }
   }

   public class AddressItem
   {
      public Org org;
      public CashItem parent;

      public AddressItem(CashItem parent,  Org org)
      {
         this.org = org;
         this.parent = parent;
      }

      public override string ToString()
      {
         return org != null ? org.Address : string.Empty;
      }

      public string Id { get { return org != null ? org.id : string.Empty; } }

      public string CagentName { get { return parent != null && parent.cagent != null ? parent.cagent.name : string.Empty; } }
      public string Address { get { return ToString(); } }
   }
}
