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
      public UserDataItemEx(UserData container) : base(container) { }

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

      public bool CanSendOrders
      {
         get { return GetRight("ShowADSReports"); }
         set { ChangeRight("ShowADSReports", value); }
      }
   }
}