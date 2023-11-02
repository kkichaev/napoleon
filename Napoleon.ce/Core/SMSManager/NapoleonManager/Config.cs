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

namespace GRSoft.NapoleonManager
{
   public class Config
   {
      static readonly string FILE_NAME = "NapoleonManager.cfg";

      public string ip = "127.0.0.1";
      public int port = 8888;

      static readonly string login = "1";
      static readonly string password = "1";
      
      private static Config instance = null;

      private Config() 
      { 
      }

      public static Config GetConfig()
      {
         if (instance == null)
         {
            instance = new Config();
            instance.Load();
         }

         return instance;
      }

      public static DBConnection Connection
      {
         get
         {
            Config cfg = GetConfig();
            return cfg.GetConnection();
         }
      }

      public DBConnection GetConnection()
      {
         DBConnection conn = new DBConnection(ip, port);
         conn.login = login;
         conn.password = password;
         conn.PDTFile = "GRManager.pdt";

         return conn;
      }

      public bool Save()
      {
         return Save(FILE_NAME);
      }

      public bool Save(string fileName)
      {
         XmlSerializer s = new XmlSerializer(typeof(Config));
         using (TextWriter w = new StreamWriter(fileName))
         {
            s.Serialize(w, this);
            w.Close();
         }

         return true;
      }

      public void Load()
      {
         Load(FILE_NAME);
      }

      public void Load(string fileName)
      {
         if (File.Exists(fileName))
         {
            XmlSerializer s = new XmlSerializer(typeof(Config));
            using (FileStream fs = new FileStream(fileName, FileMode.Open))
            {
               try
               {
                  instance = (Config)s.Deserialize(fs);
               }
               catch
               {
               }
            }
         }
      }

      public static bool Exist()
      {
         return File.Exists(FILE_NAME);
      }
   }
}
