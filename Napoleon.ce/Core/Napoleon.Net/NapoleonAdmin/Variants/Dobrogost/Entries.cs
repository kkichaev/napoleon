/*
 * Copyright (C), 2010 - 2013, Гильдия Разработчиков
 *
 * Точки входа для форм
 * 
 * ert   15/11/2013   creating
 */

using GRSoft.Network;
using System.Collections.Generic;
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

      int GetConfigValue(string name, int defaulValue)
      {
         int ret = defaulValue;
         if (agent != null)
         {
            ServerConfig sc = GetConfig(agent.id, name);
            if (sc != null)
            {
               int value;
               if (int.TryParse(sc.value, out value))
                  ret = value;
            }
         }

         return ret;
      }

      void RemoveConfig(string userid, string key, bool fromRemove)
      {
         if (fromRemove)
         {
            foreach (KeyValuePair<int, ServerConfig> kv in container.dsRemoveConfig)
            {
               if (kv.Value.userid == userid && kv.Value.key == key)
               {
                  container.dsRemoveConfig.Remove(kv.Key);
                  break;
               }
            }
         }
         else
         {
            foreach (KeyValuePair<int, ServerConfig> kv in container.dsServerConfig)
            {
               if (kv.Value.userid == userid && kv.Value.key == key)
               {
                  container.dsServerConfig.Remove(kv.Key);
                  break;
               }
            }
         }
      }


      void ChangeConfigValue(string name, int value, int defaultValue)
      {
         if (agent == null)
            return;

         if (value == defaultValue)
         {
            ServerConfig sc = new ServerConfig();
            sc.key = name;
            sc.userid = agent.id;
            container.dsRemoveConfig.Add(sc);
            RemoveConfig(agent.id, name, false);
         }
         else
         {
            ServerConfig sc = GetConfig(agent.id, name);
            if (sc == null)
            {
               sc = new ServerConfig();
               sc.key = name;
               sc.userid = agent.id;
               DataSet<int, ServerConfig> ds = container.dsServerConfig;
               ds.Add(ds.Count + 1, sc);
            }
            sc.value = value.ToString();
            RemoveConfig(agent.id, name, true);
         }

         Resolver resolver = new Resolver(name, 0, value, null);
         container.FireChanging(resolver);
      }

      public int TryCount
      {
         get { return GetConfigValue("ПопытокПИН", MainFormEx.DEFAULT_TRY_COUNT); }
         set { ChangeConfigValue("ПопытокПИН", value, MainFormEx.DEFAULT_TRY_COUNT); }
      }

      UserPinData GetPinData(bool create)
      {
         UserPinData ret = MainFormEx.dsUserPins.ContainsKey(agent.id) ? MainFormEx.dsUserPins[agent.id] : null;
         if (ret == null && create)
         {
            ret = new UserPinData();
            ret.userid = agent.id;
            MainFormEx.dsUserPins.Add(agent.id, ret);
         }
         return ret;
      }

      public bool AuthByPin
      {
         get
         {
            UserPinData data = GetPinData(false);
            return data == null ? false : data.authByPin != 0;
         }

         set
         {
            UserPinData data = GetPinData(true);
            data.authByPin = (value) ? 1 : 0;
            Resolver resolver = new Resolver("AuthByPin", !value, value, null);
            container.FireChanging(resolver);
            container.AddWriteSet(MainFormEx.dsUserPins);
         }
      }

      public bool ResetPin
      {
         get
         {
            UserPinData data = GetPinData(false);
            return data == null ? false : data.resetPin != 0;
         }

         set
         {
            UserPinData data = GetPinData(true);
            data.resetPin = (value) ? 1 : 0;
            Resolver resolver = new Resolver("ResetPin", !value, value, null);
            container.FireChanging(resolver);
            container.AddWriteSet(MainFormEx.dsUserPins);
         }
      }
   }
}