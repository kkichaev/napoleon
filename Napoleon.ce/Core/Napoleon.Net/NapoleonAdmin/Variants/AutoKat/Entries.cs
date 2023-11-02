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

   class UserDataItemEx : UserDataItem
   {
      const string DISABLE_SAVE = "DisableEditDivision";
      const string DISABLE_COPY = "DisableCopy";
      const string DISABLE_DELETE = "DisableDelete";
      const string DISABLE_LOOK = "DisableLook";

      public UserDataItemEx(UserData container) : base(container) { }

      public string kisID;
      public string KISID
      {
         get { return kisID; }
         set { DoChanging("KISID", "kisID", value); }
      }


      public override void Set(Agent a, UserActivity ua, LicensedUsers licType, string tracking)
      {
         base.Set(a, ua, licType, tracking);
         kisID = a.kisID;
      }

      public override void SetAgent(Agent a)
      {
         base.SetAgent(a);
         a.kisID = kisID;
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
           Resolver resolver = new Resolver(name, !value, value, null);
           container.FireChanging(resolver);
       }

       public bool DisableSave
       {
           get { return !GetRight(DISABLE_SAVE); }
           set { ChangeRight(DISABLE_SAVE, !value); }
       }

      public bool AllowCopy
      {
         get { return GetRight(DISABLE_COPY); }
         set { ChangeRight(DISABLE_COPY, value); }
      }

      public bool AllowDelete
      {
         get { return GetRight(DISABLE_DELETE); }
         set { ChangeRight(DISABLE_DELETE, value); }
      }

      public bool AllowLookPhoto
      {
         get { return GetRight(DISABLE_LOOK); }
         set { ChangeRight(DISABLE_LOOK, value); }
      }
   }

   public partial class Agent
   {
      public string kisID = "";
   }

}