using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class PrjObj
   {
   }

   class InvFrgSt1 : Visit
   {
      public static readonly new string OBJECT_NAME = "InvFrgSt1";
   }

   class InvFrgSt2 : Visit
   {
      public static readonly new string OBJECT_NAME = "InvFrgSt2";
   }

   class InvFrgSt3 : Visit
   {
      public static readonly new string OBJECT_NAME = "InvFrgSt3";
   }

   class Entity : DataObject
   {
      public static readonly string OBJECT_NAME = "Entity";

      [KeyField]
      public string id = string.Empty;
      public string name = string.Empty;

      public override string ToString()
      {
         return name;
      }
   }

   partial class Org
   {
      public string entity = string.Empty;

      public string EntName
      {
         get
         {
            string result = entity;

            IDataSet ds = DataModule.Get(Entity.OBJECT_NAME);

            if (ds != null && ds.ContainsKey(entity))
            {
               result = ((DataSet<string, Entity>)ds)[entity].name;
            }

            return result; 
         }
      }

      public string OrderCreated
      {
         get 
         {
            string result = string.Empty;

            if (MainFormEx.orderDate.ContainsKey(id) && MainFormEx.orderDate[id].ContainsKey(userid))
               result = MainFormEx.orderDate[id][userid].ToString("dd.MM.yyyy");

            return result;
         }
      }
   }

   public class OrderCreated : DataObject
   {
      public static readonly string OBJECT_NAME = "OrderCreated";

      public string id = string.Empty;
      public string userid = string.Empty;
      public DateTime created = DateTime.MinValue;
   }
}
