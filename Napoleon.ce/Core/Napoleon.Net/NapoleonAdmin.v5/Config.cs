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
using System.Threading.Tasks;
using System.Net;

namespace GRSoft.NapoleonAdmin
{
   public partial class Config
   {
      static string COM_LOGIN = "\x2C\x3O\x4M\x5L\x6O\x7G\x7I\x6N";

      static string FILE_NAME = "NapoleonAdmin.cfg";
      static string FOLDER = "\\GRSoft\\Admin\\";

      static string KEY = "\x1st\x1ак\xbаs\xe\x2уе";

      public string serverCode = "";

      public string name = string.Empty;

      public class ServerAnwer
      {
      }

      public static ReqConnect LinkingUser(LinkUser lu, string serverCode)
      {
         return LinkUnlink(lu, serverCode, true);
      }

      static ReqConnect LinkUnlink(LinkUser lu, string serverCode, bool link)
      {
         WebClient client = new WebClient();
         client.Headers.Add("Content-Type", "application/json; charset=utf-8");
         client.Headers.Add("Authorization", "Bearer " + serverCode);

         string data = AnswerPool.ToJson(lu);
         string url = ConnectionHelper.HOST + "/api/req_connect";
         string method = link ? "POST" : "DELETE";
         byte[] res = client.UploadData(url, method, Encoding.UTF8.GetBytes(data));
         string body = Encoding.UTF8.GetString(res);

         AnswerPool ap = new AnswerPool();
         ap.Read(body);

         List<ReqConnect> rq = ap.Read(new ReqConnect());
         if (rq.Count > 0)
         {
            ReqConnect rc = rq[0];
            rc.type = lu.type;
            rc.id = lu.id;
            return rc;
         }

         return null;
      }

      public static ReqConnect UnlinkUser(LinkUser lu, string serverCode)
      {
         return LinkUnlink(lu, serverCode, false);
      }

      public static List<ReqConnect> ServerReqConnects(string serverCode)
      {
         WebClient client = new WebClient();
         client.Headers.Add("Content-Type", "application/json; charset=utf-8");
         client.Headers.Add("Authorization", "Bearer " + serverCode);

         string url = ConnectionHelper.HOST + "/api/req_connect";
         Stream data = client.OpenRead(url);
         StreamReader sr = new StreamReader(data);
         AnswerPool ap = new AnswerPool();
         if (ap.Read(sr))
         {
            return ap.Read(new ReqConnect());
         }

         return new List<ReqConnect>();
      }

      public DBConnection GetConnection(GRServerInfo si)
      {
         if (si == null || si.fail)
            return null;

         DBConnection conn = new DBConnection(si.address, si.port);
         conn.login = COM_LOGIN;
         conn.password = serverCode;
         conn.PDTFile = "NapoleonAdmin.pdt";

         return conn;
      }

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
               string tVal = serverCode;

               serverCode = Encrypt(tVal);

               XmlSerializer s = new XmlSerializer(typeof(Config));
               s.Serialize(w, this);
               w.Close();
               serverCode = tVal;
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
         return serverCode.Equals(r.serverCode);
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
                  ret.serverCode = Decrypt(ret.serverCode);
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
