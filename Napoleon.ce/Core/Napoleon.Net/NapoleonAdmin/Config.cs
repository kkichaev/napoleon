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

namespace GRSoft.NapoleonAdmin
{
   public partial class Config
   {
      static string FILE_NAME = "NapoleonAdmin.cfg";
      static string FOLDER = "\\GRSoft\\Admin\\";

      static string KEY = "\x1st\x1ак\xbаs\xe\x2уе";

      public string ip = "127.0.0.1";
      public int port = 8888;

      public string login = "admin";
      public string password = "admin";

      // чтобы не сохранять в конфигурации сделаны функции для доступа
      public bool rememberPassword = false;

      private bool loadedFromFile = false;
      public string name = string.Empty;

      public DBConnection GetConnection()
      {
         DBConnection conn = new DBConnection(ip, port);
         conn.login = login;
         conn.password = password;
         conn.PDTFile = "NapoleonAdmin.pdt";

         return conn;
      }

      public bool IsLoaded { get { return loadedFromFile; } }

      static public Config Load()
      {
         if( File.Exists(FILE_NAME) )
            return Load(FILE_NAME);

         string v = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData);
         v += FOLDER + FILE_NAME;
         if (File.Exists(v))
            return Load(v);

         return new Config();
      }

      private bool Save(StreamWriter w)
      {
         bool ret = true;
         try
         {
            using (w)
            {
               string tVal = password;

               if (rememberPassword)
                  password = Encrypt(tVal);
               else
                  password = "";

               XmlSerializer s = new XmlSerializer(typeof(Config));
               s.Serialize(w, this);
               w.Close();
               password = tVal;

#if CONFIG_HISTORY_ADM
               if (name.Length > 0)
               {
                  ConfigHistory history = ConfigHistory.Instance(false);
                  foreach (Config c in history.config)
                  {
                     if (c.name.Equals(name))
                     {
                        history.config.Remove(c);
                        break;
                     }
                  }

                  const int MAX_HISTORY_LEN = 10;

                  if (history.config.Count == MAX_HISTORY_LEN)
                     history.config.RemoveAt(MAX_HISTORY_LEN - 1);

                  history.config.Insert(0, (Config)MemberwiseClone());
                  history.Save();
               }
#endif
            }
         }
         catch (Exception)
         {
            ret = false;
         }

         return ret;
      }

      public bool Save()
      {
         if (Save(FILE_NAME))
            return true;

         string v = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData);
         v += FOLDER;
         Directory.CreateDirectory(v);
         return Save(v + FILE_NAME);
      }

      public bool Save(string fileName)
      {
         bool ret = false;
         try
         {
            ret = Save(new StreamWriter(fileName));
         }
         catch (Exception)
         {
         }
         return ret;
      }

      public bool IsEqual(Config r)
      {
         bool equal = (ip.CompareTo(r.ip) == 0 && port == r.port &&
            login.CompareTo(r.login) == 0 && password.CompareTo(r.password) == 0 && 
            rememberPassword == r.rememberPassword);

         return equal;
      }

      static public Config Load(string fileName)
      {
         Config ret = new Config();
         if (File.Exists(fileName))
         {
            XmlSerializer s = new XmlSerializer(typeof(Config));
            using (FileStream fs = new FileStream(fileName, FileMode.Open, FileAccess.Read))
            {
               try
               {
                  ret = (Config)s.Deserialize(fs);
                  if (!ret.rememberPassword)
                     ret.password = "";
                  else
                     ret.password = Decrypt(ret.password);

                  ret.loadedFromFile = true;
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

      public override string ToString()
      {
         return name.Length == 0 ? base.ToString() : name;
      }
   }
}
