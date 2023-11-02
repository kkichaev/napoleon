/*
 * Copyright (C), 2010 - 2013, Гильдия Разработчиков
 *
 * Точки входа для форм
 * 
 * ert   15/11/2013   creating
 */

using GRSoft.Network;
using System.Windows.Forms;
namespace GRSoft.NapoleonAdmin
{
   class FormEntries
   {
      internal static System.Type GetObjectType(System.Type baseType)
      {
         if (baseType == typeof(MainForm))
            return typeof(MainFormEx);
         if (baseType == typeof(UserDataItem))
            return typeof(UserDataItemEx);

         return baseType;
      }

      internal static IFormDecorator GetFormDecorator(System.Type formType)
      {
         return new EmptyDecorator();
      }
   }

   class AdmRequestSync : GRSoft.Network.DataObject
   {
      public static string OBJECT_NAME = "AdmRequestSync";

      [KeyField]
      public string userid = "";
      public int sync = 0;
   }

   class UserDataItemEx : UserDataItem
   {
      bool reqSync = false;
      public UserDataItemEx(UserData container) : base(container) 
      {
      }

      bool GetConfig(string key)
      {
         ServerConfig c = container.GetConfig(agent.id, key, true);
         if(c != null)
         {
            int val = 0;
            int.TryParse(c.value, out val);
            return val != 0;
         }
         return false;
      }

      void SetConfig(string key, bool value)
      {
         ServerConfig c = container.GetConfig(agent.id, key, false);
         c.value = value ? "1" : "0";

         Resolver resolver = new Resolver(key, !value, value, this);
         container.FireChanging(resolver);
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

      public bool CopyOrderRight
      {
         get { return GetConfig("CanCopyOrders"); }
         set { SetConfig("CanCopyOrders", value); }
      }

      public bool CanDisableFirms
      {
         get { return GetRight("FmDisabledFirms"); }
         set { ChangeRight("FmDisabledFirms", value); }
      }

      public bool CanSendOrders
      {
         get { return GetRight("DailyAgentPlansCommit"); }
         set { ChangeRight("DailyAgentPlansCommit", value); }
      }

      public bool CanChangeOrder
      {
         get { return GetRight("CanChangeOrder"); }
         set { ChangeRight("CanChangeOrder", value); }
      }

      public bool CanChangeRoute
      {
         get { return GetRight("CanChangeRoute"); }
         set { ChangeRight("CanChangeRoute", value); }
      }

      public bool RequestSync
      {
         get { return reqSync; }
         set 
         { 
            reqSync = value;
            Resolver resolver = new Resolver("RequestSync", !value, value, this);
            container.FireChanging(resolver);
         }
      }

      //public bool ReturnEditRigth
      //{
      //   get { return GetRight("ReturnEditRigth"); }
      //   set { ChangeRight("ReturnEditRigth", value); }
      //}

      //public bool ReturnViewRigth
      //{
      //   get { return GetRight("ReturnViewRigth"); }
      //   set { ChangeRight("ReturnViewRigth", value); }
      //}
   }
}