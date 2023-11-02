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
using GRSoft.NapoleonManager.Maps;
using System.Globalization;
using System.Threading;

namespace GRSoft.NapoleonManager
{
   public partial class Config
   {
      public static string FILE_NAME = "NapoleonManager.cfg";
      public static string FOLDER = "\\GRSoft\\Manager\\";

      static readonly string KEY = "\x1st\x1ак\xbаs\xe\x2уе";

      public static string UUID = null;

      public string ip = "127.0.0.1";
      public int port = 8888;

      //public string login = "manager1";
      //public string password = "manager1";

      public string proxyLogin = string.Empty;
      public string proxyPassword = string.Empty;
      public string proxyDomen = string.Empty;

      public string mapSource = "Yandex";
      public bool isFullOrgName = false;

      public string culture = "ru-RU";
      public bool highliteOrderMissed = false;

      public string proxyIP = string.Empty;
      public int proxyPort = 0;

      public bool onlyInstance = false;
      public bool scriptErrorAllow = false;

      public string userid = "";
      public string uuid = "";
      public string serverCode = "";

      //// чтобы не сохранять в конфигурации сделаны функции для доступа
      //public bool rememberPassword = false;

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

      public static Config Reload()
      {
         instance = new Config();
         instance.Load();

         return instance;
      }

      static CultureInfo ci = null;
      static public CultureInfo GetCultureInfo()
      {
         if (ci != null)
            return ci;

         Config c = Config.GetConfig();
         ci = CultureInfo.CreateSpecificCulture(c.culture);
         return ci;
      }


      static GRServerInfo serverInfo;
      public DBConnection GetConnection(GRServerInfo si)
      {
         serverInfo = si;
         return GetConnection();
      }

      public DBConnection GetConnection()
      {
         DBConnection conn = (serverInfo == null) ? new DBConnection("", 0) :
            new DBConnection(serverInfo.address, serverInfo.port);
         conn.login = "";
         conn.password = "";

         conn.uuid = uuid;

#if DEBUG
         if (UUID != null)
            conn.uuid = UUID;
#endif

         conn.PDTFile = PDTFileName("pdt");

         return conn;
      }

      public string HrefBase { 
         get {
            if(serverInfo != null)
            {
               return String.Format("http://{0}:{1}/"
                  , serverInfo.address
                  , serverInfo.port);
            }
            if (GRPacket.GRJSHelper.IsJSAddress(ip))
            {
               return "http://" + GRPacket.GRJSHelper.ServerIP + ":" + GRPacket.GRJSHelper.ServerPort.ToString() + "/" + GRPacket.GRJSHelper.ServerID.ToString() + "/";  
            }
            return "http://" + ip + ":" + port.ToString() + "/";  
         } 
      }

      public static string PDTFileName(string name)
      {
         return "NapoleonManger" + name + ".pdt";
      }

      public static string AppFolder() 
      {
         string v = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData);
         v += FOLDER;
         Directory.CreateDirectory(v);
         return v;
      }

      public bool CheckLogin()
      {
         if (uuid.Length > 0 && serverCode.Length > 0)
         {
            return true;
         }
         return false;
      }

      private void Load()
      {
         if (!Load(FILE_NAME))
         {
            string v = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData);
            v += FOLDER + FILE_NAME;
            Load(v);
         }

         ci = null;
      }

      public bool Save()
      {
         if (File.Exists(FILE_NAME))
            return Save(FILE_NAME);

         string v = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData);
         v += FOLDER;
         Directory.CreateDirectory(v);
         return Save(v + FILE_NAME);
      }

      public bool Save(string fileName)
      {
         //return true; ;

         XmlSerializer s = new XmlSerializer(typeof(Config));
         bool ret = true;
         try
         {
            using (TextWriter w = new StreamWriter(fileName))
            {
               string tVal = serverCode;

               serverCode = Encrypt(tVal);

               s.Serialize(w, this);
               w.Close();
               serverCode = tVal;

               Route.FreeProxyInfo();
               ci = null;
               if (culture.Length > 0)
               {
                  Thread.CurrentThread.CurrentCulture = new CultureInfo(culture, true);
               }
            }
         }
         catch (Exception)
         {
            ret = false;
         }

         ci = null;
         return ret;
      }

      private bool Load(string fileName)
      {
         bool ret = false;
         if (File.Exists(fileName))
         {
            XmlSerializer s = new XmlSerializer(typeof(Config));
            using (FileStream fs = new FileStream(fileName, FileMode.Open, FileAccess.Read))
            {
               try
               {
                  instance = (Config)s.Deserialize(fs);
                  instance.serverCode = Decrypt(instance.serverCode);

                  ret = true;
                  Route.FreeProxyInfo();

                  ci = null;
                  if( instance.culture.Length > 0 )
                  {
                     Thread.CurrentThread.CurrentCulture = new CultureInfo(instance.culture, true);
                  }
               }
               catch
               {
               }
            }
         }
         return ret;
      }

      static byte[] GenerateKey(SymmetricAlgorithm key)
      {
         int ks = key.KeySize / 8;
         byte[] k = new byte[ks];
         int index = 0;
         foreach (byte b in Encoding.Unicode.GetBytes(KEY))
         {
            k[index] = b;
            index++;
            if (index >= ks) break;
         }

         return k;
      }

      public static string Encrypt(string PlainText)
      {
         SymmetricAlgorithm key = new DESCryptoServiceProvider();
         key.Key = GenerateKey(key);
         key.IV = GenerateKey(key);

         // Create a memory stream.
         MemoryStream ms = new MemoryStream();

         // Create a CryptoStream using the memory stream and the 
         // CSP DES key.  
         CryptoStream encStream = new CryptoStream(ms, key.CreateEncryptor(), CryptoStreamMode.Write);

         // Create a StreamWriter to write a string
         // to the stream.
         StreamWriter sw = new StreamWriter(encStream);

         // Write the plaintext to the stream.
         sw.WriteLine(PlainText);

         // Close the StreamWriter and CryptoStream.
         sw.Close();
         encStream.Close();

         // Get an array of bytes that represents
         // the memory stream.
         byte[] buffer = ms.ToArray();

         // Close the memory stream.
         ms.Close();

         StringBuilder sb = new StringBuilder();
         foreach (byte b in buffer)
            sb.Append(b.ToString("X2"));
         return sb.ToString();
      }

      public static string Decrypt(string text)
      {
         if (text == null || text.Length == 0) return "";

         SymmetricAlgorithm key = new DESCryptoServiceProvider();
         key.Key = GenerateKey(key);
         key.IV = GenerateKey(key);

         int len = text.Length / 2;
         byte[] CypherText = new byte[len];
         for (int off = 0; off < len; off++)
            CypherText[off] = byte.Parse(text.Substring(off * 2, 2), System.Globalization.NumberStyles.HexNumber);

         // Create a memory stream to the passed buffer.
         MemoryStream ms = new MemoryStream(CypherText);

         // Create a CryptoStream using the memory stream and the 
         // CSP DES key. 
         CryptoStream encStream = new CryptoStream(ms, key.CreateDecryptor(), CryptoStreamMode.Read);

         // Create a StreamReader for reading the stream.
         StreamReader sr = new StreamReader(encStream);

         // Read the stream as a string.
         string val = sr.ReadLine();

         // Close the streams.
         sr.Close();
         encStream.Close();
         ms.Close();

         return val;
      }

      public static string GetAppHomeDir()
      {
         string v = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData);
         v += FOLDER;

         return v;
      }
   }
}
