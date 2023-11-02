using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Security.Cryptography;
using System.Text;
using System.Threading.Tasks;

namespace Ads2017
{
   class ConnectionHelper
   {
      static string LOGIN_TAG = "login";
      static string PROJECT_TAG = "project";
      static string DEBUG_MODE_TAG = "debugMode";
      static string CATEGORY_TAG = "category";
      static byte[] KEY = Encoding.UTF8.GetBytes("D3F$#lkJ:we@#_(g");

      public static bool FindServer(ref string ip, ref int port, string login, string project, bool debugMode)
      {
         KeyValueHolder kv = new KeyValueHolder();
         kv.Add(LOGIN_TAG, login).Add(PROJECT_TAG, project).Add(DEBUG_MODE_TAG, debugMode ? "1" : "0").Add(CATEGORY_TAG, ServerCommand.Category);
         //string data1 = "login:manager1;project:ADS2017\\\\Test\\,;debugMode:1;category:adsmanager;";
         string data = kv.ToString();

         //uint crc = CRC32.Checksum(Encoding.UTF8.GetBytes(data1));
         uint  crc = CRC32.Checksum(Encoding.UTF8.GetBytes(data));
         byte[] clearBytes = Encoding.UTF8.GetBytes(crc.ToString("X8") + data.Length.ToString("X8") + data);

         string outText = "";

         Aes aes = Aes.Create();
         aes.Key = KEY;
         aes.IV = new byte[16];
         aes.Mode = CipherMode.ECB;
         using (MemoryStream ms = new MemoryStream())
         {
            using (CryptoStream cs = new CryptoStream(ms, aes.CreateEncryptor(), CryptoStreamMode.Write))
            {
               cs.Write(clearBytes, 0, clearBytes.Length);
               cs.Close();
            }
            byte[] outArray = ms.ToArray();
            outText = WebUtility.UrlEncode(Convert.ToBase64String(outArray).Trim(new char[] { '=' }));
         }
         aes.Dispose();

         return false;
      }

      class CRC32
      {
         static uint[] table;

         public static uint Checksum(byte[] bytes)
         {
            if (table == null)
               Init();

            uint crc = 0xffffffff;
            for (int i = 0; i < bytes.Length; ++i)
            {
               byte index = (byte)(((crc) & 0xff) ^ bytes[i]);
               crc = (uint)((crc >> 8) ^ table[index]);
            }
            return ~crc;
         }

         static void Init()
         {
            uint poly = 0xedb88320;
            table = new uint[256];
            uint temp = 0;
            for (uint i = 0; i < table.Length; ++i)
            {
               temp = i;
               for (int j = 8; j > 0; --j)
               {
                  if ((temp & 1) == 1)
                  {
                     temp = (uint)((temp >> 1) ^ poly);
                  }
                  else
                  {
                     temp >>= 1;
                  }
               }
               table[i] = temp;
            }
         }
      }

      class KeyValueHolder
      {
         Dictionary<string, string> data = new Dictionary<string, string>();
         public KeyValueHolder()
         {
         }

         public KeyValueHolder Add(string key, object value)
         {
            data.Add(key, value.ToString());
            return this;
         }

         public override string ToString()
         {
            StringBuilder sb = new StringBuilder();
            foreach (KeyValuePair<string, string> kv in data)
            {
               sb.Append(kv.Key).Append(':').Append(kv.Value.Replace("\\", "\\\\").Replace(";", "\\,")).Append(";");
            }
            return sb.ToString();
         }
      }
   }
}
