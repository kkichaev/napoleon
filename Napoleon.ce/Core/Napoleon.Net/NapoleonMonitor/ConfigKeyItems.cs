/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Классы ключей для конфигурации
 * 
 * kki   20/09/2010   creating
 */

using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   class ConfigKeyItems
   {
      private string key;

      public ConfigKeyItems(string key)
      {
         this.key = key;
      }

      public string Key { get { return key; } }

      public static ConfigKeyItems VISIT_DUBLICATES_ORG = new ConfigKeyItems("VISIT_DUBLICATES_ORG");
      public static ConfigKeyItems SHEDULE_START = new ConfigKeyItems("SheduleStart");
      public static ConfigKeyItems ALLOW_SCRIPTING = new ConfigKeyItems("AllowScripting");
      public static ConfigKeyItems ALLOW_CHANGE_COST = new ConfigKeyItems("МожноИзменятьЦену");
      public static ConfigKeyItems GPS_TRACKING = new ConfigKeyItems("Tracking");
   }

   class ConfigUtils
   {
      public static CommonConfig GetCommonConfig(DataSet<int, CommonConfig> configs, ConfigKeyItems item)
      {
         //Ищем общую конфигурацию
         foreach (CommonConfig cc in configs.Data)
         {
            if (cc.key.Equals(item.Key) &&
               (cc.userid == null || cc.userid.Equals(string.Empty)))
               return cc;
         }

         return null;
      }

      public static CommonConfig GetConfig(DataSet<int, CommonConfig> configs, ConfigKeyItems item, Agent agent)
      { 
         if (agent == null && agent.id == null && agent.id.Equals(string.Empty))
            throw new EDataCorrupted();

         return GetConfig(configs, item, agent.id);
      }

      public static CommonConfig GetConfig(DataSet<int, CommonConfig> configs, ConfigKeyItems item, string userid)
      {
         //Ищем конфигурацию для пользователя или общую если userid = null
         foreach (CommonConfig cc in configs.Data)
         {
            if (cc.key.Equals(item.Key) &&
               ((cc.userid != null && userid != null) 
                  ? cc.userid.Equals(userid) 
                  : cc.userid.Equals(string.Empty)))
               return cc;
         }

         return null;
      }

      public static CommonConfig CreateConfig(DataSet<int, CommonConfig> configs, ConfigKeyItems item)
      {
         return CreateConfig(configs, item, string.Empty);
      }

      public static CommonConfig CreateConfig(DataSet<int, CommonConfig> configs, ConfigKeyItems item, string userid)
      {
         //Если не нашли не одной конфигурации создаем новую
         CommonConfig result = new CommonConfig();
         result.key = item.Key;
         result.userid = userid;
         result.value = string.Empty;

         configs.Add(configs.Count, result);
         return result;
      }

      public static CommonConfig GetOrCreateCommonConfig(DataSet<int, CommonConfig> configs, ConfigKeyItems item)
      { 
         return GetCommonConfig(configs, item) ?? CreateConfig(configs, item);
      }

      public static CommonConfig GetOrCreateConfig(DataSet<int, CommonConfig> configs, ConfigKeyItems item, string userid)
      {
         return GetConfig(configs, item, userid) ?? CreateConfig(configs, item, userid);
      }

      public static CommonConfig GetOrCreateConfig(DataSet<int, CommonConfig> configs, ConfigKeyItems item, Agent agent)
      {
         if (agent == null && agent.id == null && agent.id.Equals(string.Empty))
            throw new EDataCorrupted();

         return GetConfig(configs, item, agent.id) ?? CreateConfig(configs, item, agent.id);
      }

      internal static void AddConfig(DataSet<int, CommonConfig> dsCommonConfig, CommonConfig c)
      {
         bool changed = false;
         int maxId = 0;
         foreach (KeyValuePair<int, CommonConfig> kv in dsCommonConfig)
         {
            if (maxId < kv.Key)
               maxId = kv.Key;

            if (kv.Value.userid.Equals(c.userid) && kv.Value.key.Equals(c.key))
            {
               changed = true;
               kv.Value.value = c.value;
            }
         }

         if (!changed)
            dsCommonConfig.Add(maxId + 1, c);
      }
   }
}
