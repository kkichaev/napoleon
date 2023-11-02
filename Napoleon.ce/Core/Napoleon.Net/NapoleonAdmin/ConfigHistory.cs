/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Конфигурация программы
 * 
 * ert   21/04/2010   creating
 */

using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
using System.IO;
using System.Security.Cryptography;
using GRSoft.Network;
using System.Globalization;

namespace GRSoft.NapoleonAdmin
{
   public class ConfigHistory
   {
      static readonly string FILE_NAME = "NapoleonAdminHistory.cfg";
      public static string FOLDER = "\\GRSoft\\Admin\\";

      public List<Config> config = new List<Config>();
      private static ConfigHistory instance = null;

      private ConfigHistory() 
      { 
      }

      public static ConfigHistory Instance(bool reload)
      {
         if (instance == null || reload)
         {
            instance = new ConfigHistory();
            instance.Load();
         }

         return instance;
      }

      private void Load()
      {
         string v = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData);
         v += FOLDER + FILE_NAME;
         if (File.Exists(v))
            Load(v);
         else
            Load(FILE_NAME);
      }

      public bool Save()
      {
         string v = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData);
         v += FOLDER;
         Directory.CreateDirectory(v);
         return Save(v + FILE_NAME);
      }

      public bool Save(string fileName)
      {
         XmlSerializer s = new XmlSerializer(typeof(ConfigHistory));
         bool ret = true;
         try
         {
            using (TextWriter w = new StreamWriter(fileName))
            {
               foreach (Config c in config)
               {
                  if (c.rememberPassword)
                     c.password = Config.Encrypt(c.password);
                  else
                     c.password = string.Empty;
               }

               s.Serialize(w, this);
               w.Close();
            }

            foreach (Config c in config)
            {
               if (c.rememberPassword)
                  c.password = Config.Decrypt(c.password);
               else
                  c.password = string.Empty;
            }
         }
         catch (Exception)
         {
            ret = false;
         }

         return ret;
      }

      private void Load(string fileName)
      {
         if (File.Exists(fileName))
         {
            XmlSerializer s = new XmlSerializer(typeof(ConfigHistory));
            using (FileStream fs = new FileStream(fileName, FileMode.Open, FileAccess.Read))
            {
               try
               {
                  instance = (ConfigHistory)s.Deserialize(fs);

                  foreach (Config c in instance.config)
                     c.password = Config.Decrypt(c.password);
               }
               catch
               {
               }
            }
         }
      }

      public static bool Exist()
      {
         string v = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData);
         v += FOLDER + FILE_NAME;
         if (File.Exists(v))
            return true;

         return File.Exists(FILE_NAME);
      }

      public static string GetAppHomeDir()
      {
         string v = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData);
         v += FOLDER;

         return v;
      }
   }
}
