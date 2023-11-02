using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonAdmin
{
   public class UserDataItemEx : UserDataItem
   {
      DataSet<int, Division> dsDivision;

      public UserDataItemEx(UserData data)
         : base(data)
      {
      }

      bool GetRight(string name)
      {
         if (manager != null)
            return manager.HaveRight(RightTokens.Get(name), RightActions.Write);
         return false;
      }

      void ChangeRight(string name, bool value)
      {
         if (manager == null)
            return;

         manager.ChangeRight(RightTokens.Get(name), value ? RightActions.Write : RightActions.Read);

         Resolver resolver = new Resolver(name, !value, value, this);
         container.FireChanging(resolver);
      }

      public bool CanManageContracts
      {
         get { return GetRight("CanManageContracts"); }
         set { ChangeRight("CanManageContracts", value); }
      }


      public override void Set(DivisionManager m, DataSet<int, Division> dsDivision, UserActivity ua, LicensedUsers licType, string tracking)
      {
         base.Set(m, dsDivision, ua, licType, tracking);
         this.dsDivision = dsDivision;
         if( division <= 0 )
            foreach(Division d in dsDivision.Data)
               if(d.parent == 0)
               {
                  division = d.id;
                  break;
               }
      }

      public Division DivisionNative
      {
         get { return dsDivision != null && dsDivision.ContainsKey(division) ? dsDivision[division] : GRSoft.NapoleonAdmin.Division.Empty; }
         set
         {
            foreach(Division d in dsDivision.Data)
               if(d == value)
               {
                  if (DoChanging("Division", "division", d.id))
                     container.FireChanged();

                  break;
               }
         }
      }

   }

   public class Contracts : DataObject
   {
      public static readonly string OBJECT_NAME = "ContractDef";

      [KeyField]
      public string id = "";

      public string name = "";
      public DateTime start = DateTime.Now;
      public DateTime finish = DateTime.Now;
   }

   public class NBTLViewer : DataObject
   {
      public static readonly string OBJECT_NAME = "NBTLViewer";

      [KeyField]
      public string id = "";
      public string name = "";
      public string password = "";
      public int division = 0;

      public class Item : DataObject
      {
         public string id = "";
         public Item() { }
         public Item(string id) { this.id = id; }
      }

      public List<Item> contracts = new List<Item>();
   }
}
