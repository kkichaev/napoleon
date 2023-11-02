using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using System.Text;

namespace GRPacket
{
   public class GRJSHelper
   {
      static string JS_PACKET_TAG = "GRJS";
      static string CLIENT_CONNECT_CMD = "CLTC";
      static string OK_CMD = "OKCM";
      static string REJECT_CMD = "CLTC";

      static byte[] UInt32ToBigEndianBytes(UInt32 x)
      {
         return new byte[] {
            (byte)((x >> 24) & 0xff),
            (byte)((x >> 16) & 0xff),
            (byte)((x >> 8) & 0xff),
            (byte)(x & 0xff)
            };
      }

      static UInt32 FromBytes(byte[] buf, int offset)
      {
         return ((uint)buf[offset] << 24) | ((uint)buf[offset + 1] << 16) | ((uint)buf[offset + 2] << 8) | ((uint)buf[offset + 3]);
      }

      public static bool IsJSAddress(string addr)
      {
         return addr.ToUpper().StartsWith("GRJS.");
      }

      static bool SendCommand(NetworkStream stream, String cmd, UInt32 id, out string error)
      {
         bool ret = true;
         byte[] data = new byte[4 * 4];

         error = "";

         int cp = 0;
         Buffer.BlockCopy(Encoding.ASCII.GetBytes(JS_PACKET_TAG), 0, data, cp, 4);
         cp += 4;
         Buffer.BlockCopy(Encoding.ASCII.GetBytes(cmd), 0, data, cp, 4);
         cp += 4;
         Buffer.BlockCopy(UInt32ToBigEndianBytes(id), 0, data, cp, 4);
         //cp += 4;
         //Buffer.BlockCopy(UInt32ToBigEndianBytes(0), 0, data, cp, 4);

         stream.Write(data, 0, data.Length);

         stream.Read(data, 0, data.Length);
         string answ = Encoding.ASCII.GetString(data, 4, 4);
         if (answ == REJECT_CMD)
         {
            ret = false;
            UInt32 len = FromBytes(data, 12);
            if (len > 0)
            {
               data = new byte[len];
               stream.Read(data, 0, (int)len);
               error = Encoding.ASCII.GetString(data);
            }
         }

         return ret;
      }

   
      static bool GetAddress(out string addr, out int port, string serverID)
      {
         bool ret = false;
         addr = "";
         port = 0;

         String url = "https://grsoft.ru/grjs/grjs.php?server=" + serverID.Substring(5);
         using (WebClient client = new WebClient())
         {
            string s = client.DownloadString(url);
            Dictionary<string, object> val = JSONParser.Parse(s) as Dictionary<string, object>;
            if(val != null)
            {
               object param;
               if(val.TryGetValue("addr", out param))
               {
                  addr = param.ToString();
                  if(val.TryGetValue("port", out param))
                  {
                     port = (int)param;
                     ret = true;
                  }
               }
            }
         }

         return ret;
      }

      //public static string ServerIP = "127.0.0.1";
      public static string ServerIP = "51.68.175.104";
      public static int ServerPort = 9595;
      public static UInt32 ServerID = 0;

      public static bool GRJSConnect(string addr, TcpClient client, out string error)
      {
         bool ret = true;
         error = "";

         try
         {
            string[] parts = addr.Split(new char[] { '.' });
            if (parts.Length != 5)
               return false;

            UInt32 id = 0;
            for (int i = 1; i < 5; i++)
            {
               id <<= 8;

               UInt32 val;
               if (UInt32.TryParse(parts[i], out val))
               {
                  id |= val;
               }
            }

            ServerID = id;
            if(ServerIP.Length == 0)
            {
               string ip;
               int port;
               if (!GetAddress(out ip, out port, addr))
                  return false;

               //ip = "127.0.0.1";
               ServerIP = ip;
               ServerPort = port;
            }

            client.Connect(ServerIP, ServerPort);
            NetworkStream stream = client.GetStream();
            ret = SendCommand(stream, CLIENT_CONNECT_CMD, id, out error);
         }
         catch(Exception e)
         {
            ServerIP = "";
            ret = false;
         }
         return ret;
      }

      class JSONParser
      {
         static Stack<List<string>> splitArrayPool;
         static StringBuilder stringBuilder;

         static int AppendUntilStringEnd(bool appendEscapeCharacter, int startIdx, string json)
         {
            stringBuilder.Append(json[startIdx]);
            for (int i = startIdx + 1; i < json.Length; i++)
            {
               if (json[i] == '\\')
               {
                  if (appendEscapeCharacter)
                     stringBuilder.Append(json[i]);
                  stringBuilder.Append(json[i + 1]);
                  i++;//Skip next character as it is escaped
               }
               else if (json[i] == '"')
               {
                  stringBuilder.Append(json[i]);
                  return i;
               }
               else
                  stringBuilder.Append(json[i]);
            }
            return json.Length - 1;
         }

         static List<string> Split(string json)
         {
            List<string> splitArray = splitArrayPool.Count > 0 ? splitArrayPool.Pop() : new List<string>();
            splitArray.Clear();
            if (json.Length == 2)
               return splitArray;
            int parseDepth = 0;
            stringBuilder.Length = 0;
            for (int i = 1; i < json.Length - 1; i++)
            {
               switch (json[i])
               {
                  case '[':
                  case '{':
                     parseDepth++;
                     break;
                  case ']':
                  case '}':
                     parseDepth--;
                     break;
                  case '"':
                     i = AppendUntilStringEnd(true, i, json);
                     continue;
                  case ',':
                  case ':':
                     if (parseDepth == 0)
                     {
                        splitArray.Add(stringBuilder.ToString());
                        stringBuilder.Length = 0;
                        continue;
                     }
                     break;
               }

               stringBuilder.Append(json[i]);
            }

            splitArray.Add(stringBuilder.ToString());

            return splitArray;
         }

         static internal object Parse(string json)
         {
            if (json.Length == 0)
               return null;
            
            splitArrayPool = new Stack<List<string>>();
            stringBuilder = new StringBuilder();

            if (json[0] == '{' && json[json.Length - 1] == '}')
            {
               List<string> elems = Split(json);
               if (elems.Count % 2 != 0)
                  return null;
               var dict = new Dictionary<string, object>(elems.Count / 2);
               for (int i = 0; i < elems.Count; i += 2)
                  dict.Add(elems[i].Substring(1, elems[i].Length - 2), Parse(elems[i + 1]));
               return dict;
            }
            if (json[0] == '[' && json[json.Length - 1] == ']')
            {
               List<string> items = Split(json);
               var finalList = new List<object>(items.Count);
               for (int i = 0; i < items.Count; i++)
                  finalList.Add(Parse(items[i]));
               return finalList;
            }
            if (json[0] == '"' && json[json.Length - 1] == '"')
            {
               string str = json.Substring(1, json.Length - 2);
               return str.Replace("\\", string.Empty);
            }
            if (char.IsDigit(json[0]) || json[0] == '-')
            {
               if (json.Contains("."))
               {
                  double result;
                  double.TryParse(json, System.Globalization.NumberStyles.Float, System.Globalization.CultureInfo.InvariantCulture, out result);
                  return result;
               }
               else
               {
                  int result;
                  int.TryParse(json, out result);
                  return result;
               }
            }
            if (json == "true")
               return true;
            if (json == "false")
               return false;
            // handles json == "null" as well as invalid JSON
            return null;
         }

      }
   }
}
