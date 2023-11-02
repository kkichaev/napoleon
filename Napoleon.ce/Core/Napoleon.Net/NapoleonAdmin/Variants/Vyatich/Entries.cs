/*
 * Copyright (C), 2010 - 2013, Гильдия Разработчиков
 *
 * Точки входа для форм
 * 
 * ert   15/11/2013   creating
 */

using System.Windows.Forms;
namespace GRSoft.NapoleonAdmin
{
   class FormEntries
   {
      internal static System.Type GetObjectType(System.Type baseType)
      {
         if( baseType == typeof(UserDataItem) )
            return typeof(UserDataItemEx);
         if (baseType == typeof(MainForm))
            return typeof(MainFormEx);
         return baseType;
      }

      internal static IFormDecorator GetFormDecorator(System.Type formType)
      {
         return new EmptyDecorator();
      }
   }

   class UserDataItemEx : UserDataItem
   {
      public string kisID;
      public string KISID
      { 
         get { return kisID; } 
         set { DoChanging("KISID", "kisID", value); } 
      }

      public UserDataItemEx(UserData container)
         : base(container)
      {
      }

      public override void Set(Agent a, UserActivity ua, GRSoft.Network.LicensedUsers licType, string tracking)
      {
         base.Set(a, ua, licType, tracking);
         kisID = a.kisID;

      }

      public override void SetAgent(Agent a)
      {
         base.SetAgent(a);
         a.kisID = kisID;
      }

      public bool DisableOldVersion
      {
         get
         {
            bool ret = false;
            Network.SimpleDataSet<NewVersionAction> va = (Network.SimpleDataSet<NewVersionAction>)Network.DataModule.Get(NewVersionAction.OBJECT_NAME);
            if(va != null)
            {
               foreach(NewVersionAction vi in va.Data)
               {
                  if(vi.userid == agent.id)
                  {
                     ret = vi.action == NewVersionAction.FORBIDDEN;
                     break;
                  }
               }
            }

            return ret;
         }

         set
         {
            int val = value ? NewVersionAction.FORBIDDEN : NewVersionAction.NONE;
            Network.SimpleDataSet<NewVersionAction> va = (Network.SimpleDataSet<NewVersionAction>)Network.DataModule.Get(NewVersionAction.OBJECT_NAME);
            if (va != null)
            {
               bool finded = false;
               foreach (NewVersionAction vi in va.Data)
               {
                  if (vi.userid == agent.id)
                  {
                     vi.action = val;
                     finded = true;
                     break;
                  }
               }

               if(!finded)
               {
                  NewVersionAction na = new NewVersionAction();
                  na.userid = agent.id;
                  na.action = val;
                  va.Add(na);
               }
               Resolver resolver = new Resolver("versionAction", !value, value, null);
               container.FireChanging(resolver);
            }
         }
      }
   }

   class NewVersionAction : GRSoft.Network.DataObject
   {
      public static readonly int WARNING = 0;
      public static readonly int FORBIDDEN = 1;
      public static readonly int NONE = 2;

      public static readonly string OBJECT_NAME = "NewVersionAction";

      public string userid = "";
      public int action = NONE;
   }
}