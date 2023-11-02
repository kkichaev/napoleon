using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using System.Windows.Forms;
using System.Xml.Serialization;

namespace UpdateBase
{
   public class Config
   {
      static string FILE_NAME = "\\UpdateBase.cfg";

      public string IP = "127.0.0.1";
      public int port = 8888;

      public string login = "";
      public string password = "";

      static string comlogin = "\x2C\x3O\x4M\x5L\x6O\x7G\x7I\x6N";

      public static Config Load()
      {
         Config instance = new Config();
         
         string fileName = Application.StartupPath + FILE_NAME;
         if (File.Exists(fileName))
         {
            XmlSerializer s = new XmlSerializer(typeof(Config));
            using (FileStream fs = new FileStream(fileName, FileMode.Open, FileAccess.Read))
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

         return instance;
      }


      public bool Save()
      {
         string folder = Application.StartupPath;
         string fileName = folder + FILE_NAME;
         XmlSerializer s = new XmlSerializer(typeof(Config));
         bool ret = true;
         try
         {
            using (TextWriter w = new StreamWriter(fileName))
            {
               s.Serialize(w, this);
               w.Close();
            }
         }
         catch (Exception)
         {
            ret = false;
         }
         return ret;
      }

      public DBConnection Connection 
      { 
         get 
         {
            DBConnection conn = new DBConnection(IP, port);
            conn.login = login.Length == 0 ? comlogin : login;
            conn.password = password;
            conn.ReceiveTimeout = 10 * 60 * 1000;
            return conn;
         } 
      }
   }
}
