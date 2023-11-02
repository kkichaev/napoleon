using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using System.Xml.Serialization;

namespace SyncDocs
{
   public class Config
   {
      static string FILE_NAME = "SyncDocs.cfg";
      public static string FOLDER = "\\GRSoft\\SyncDocs\\";

      public string IPAvalon = "94.141.176.186";
      public int portAvalon = 8888;

      public string IPDydo = "service.k-cloud.ru"; //"37.230.155.211"; 
      public int portDydo = 20102;

      string login = "\x2C\x3O\x4M\x5L\x6O\x7G\x7I\x6N";

      public static Config Load()
      {
         Config instance = new Config();
         
         string fileName = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData);
         fileName += FOLDER + FILE_NAME;
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
         string folder = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + FOLDER;
         Directory.CreateDirectory(folder);
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

      public DBConnection DydoConnection 
      { 
         get 
         {
            DBConnection conn = new DBConnection(IPDydo, portDydo);
            conn.login = login;
            conn.password = "";
            conn.PDTFile = "ddcnct.pdt";
            return conn;
         } 
      }

      public DBConnection AvalonConnection
      {
         get
         {
            DBConnection conn = new DBConnection(IPAvalon, portAvalon);
            conn.login = login;
            conn.password = "";
            conn.PDTFile = "vlncnct.pdt";
            return conn;
         }
      }
   }
}
