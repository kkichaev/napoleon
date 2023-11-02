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

namespace GRSoft.Ads
{
   public class Config
   {
      static readonly string FILE_NAME = "Config.cfg";
      static string FOLDER = "\\GRSoft\\Ads\\";

      static readonly string KEY = "\x1st\x1ак\xbаs\xe\x2уе";

      public string ip = "127.0.0.1";
      public int port = 8888;

      public string login = "disp1";
      public string password = "disp1";

      public string proxyLogin = "";
      public string proxyPassword = "";
      
      public string mapSource = "";
      public bool isFullOrgName = false;
      public string division = "disp1";
      public string prefix = "";
      public int orderNumber = 1;
      public int refreshTime = 1;
      public int orderMissedInterval = 10;
      public bool alert;

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

      // чтобы не сохранять в конфигурации сделаны функции для доступа
      public bool rememberPassword = false;

      public DBConnection GetConnection()
      {
         DBConnection conn = new DBConnection(ip, port);
         conn.login = login;
         conn.password = password;
         conn.PDTFile = "NapoleonManger.pdt";

         return conn;
      }

      public bool CheckLogin()
      {
         //Здесь мне не нравиться, если установлен пароль  и логин, то
         //мы думаешь что пользователь ввел необходимые данные
         if (login.Length > 0 && password.Length > 0)
         {
            return true;
         }

         if (login.Length == 0 || rememberPassword == false)
         {
            if (!Login.Ack(this))
               return false;
            Save();
         }
         return true;
      }

      public void Load()
      {
         string v = GetAppHomeDir() + FILE_NAME;
         if (File.Exists(v))
            Load(v);
         else
            Load(FILE_NAME);
      }

      public static string GetAppHomeDir()
      {
         string v = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData);
         v += FOLDER;

         return v;
      }

      public bool Save()
      {
         string v = GetAppHomeDir();
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
               string tVal = password;

               if (rememberPassword)
                  password = Encrypt(tVal);
               else
                  password = "";

               s.Serialize(w, this);
               w.Close();
               password = tVal;
            }
         }
         catch (Exception)
         {
            ret = false;
         }

         return ret;
      }

      public void Load(string fileName)
      {
         if (File.Exists(fileName))
         {
            XmlSerializer s = new XmlSerializer(typeof(Config));
            using (FileStream fs = new FileStream(fileName, FileMode.Open, FileAccess.Read))
            {
               try
               {
                  instance = (Config)s.Deserialize(fs);
                  if (!instance.rememberPassword)
                     instance.password = "";
                  else
                     instance.password = Decrypt(instance.password);
               }
               catch
               {
               }
            }
         }
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

      public static bool Exist()
      {
         string v = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData);
         v += FOLDER + FILE_NAME;
         if (File.Exists(v))
            return true;

         return File.Exists(FILE_NAME);
      }
   }
}
