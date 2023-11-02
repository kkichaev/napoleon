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

      ServerConfig GetConfig(string userid, string key)
      {
         foreach (ServerConfig sc in container.dsServerConfig.Data)
         {
            if (sc.userid == userid && sc.key == key)
               return sc;
         }
         return null;
      }

      bool GetConfigValue(string name, bool defaulValue)
      {
         bool ret = defaulValue;
         if (agent != null)
         {
            ServerConfig sc = GetConfig(agent.id, name);
            if (sc != null)
            {
               int value;
               ret = int.TryParse(sc.value, out value) && value != 0;
            }
         }

         return ret;
      }

      void ChangeConfigValue(string name, bool value)
      {
         if (agent == null)
            return;

         ServerConfig sc = GetConfig(agent.id, name);
         if( sc == null )
         {
            sc = new ServerConfig();
            sc.key = name;
            sc.userid = agent.id;
            DataSet<int, ServerConfig> ds = container.dsServerConfig;
            ds.Add(ds.Count + 1, sc);
         }
         sc.value = value ? "1" : "0";

         Resolver resolver = new Resolver(name, !value, value, null);
         container.FireChanging(resolver);
      }

      public bool CanViewMinCost
      {
         get { return GetConfigValue("ПоказыватьМинЦену", false); }
         set { ChangeConfigValue("ПоказыватьМинЦену", value); }
      }
   }
}