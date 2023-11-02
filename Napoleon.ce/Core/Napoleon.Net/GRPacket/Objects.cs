/*
 * Copyright (C), 2009 - 2010, Гильдия разработчиков
 *
 * Стандартные объекты
 * 
 * ert   26/11/2009   creating
 */
using System;
using System.Collections.Generic;
using System.Text;
using System.Collections;
using System.IO;
using ICSharpCode.SharpZipLib.Checksums;
using ICSharpCode.SharpZipLib.Zip.Compression.Streams;
using System.Reflection;
using System.ComponentModel;
using System.Globalization;
using System.Net;

namespace GRSoft.Network
{
   public class Commands
   {
      public static string LOAD_UPDATE = "LOAD UPDATE";
      public static string BYE_COMMAND = "BYE";
      public static string QUIT_COMMAND = "QUIT";
      public static string FORCE_PUT = "FORCE PUT";
      public static string RET_IDS = "RETURN IDS";
      public static string GET = "GET";
      public static string SELECT = "SELECT";
      public static string REMOVE = "REMOVE";
      public static string GET_REPORT = "Get Report";
      public static string DONE = "DONE";
      public static string PUT_NO_EXEC = "PUT NO EXEC";

      public static string Impersonate(string command, string id)
      {
         return (id == null) ? command + " AS NULL" : command + " AS '" + id + "'";
      }
   }

   public class UpdateCommand : ObjectList
   {
      const string OBJECT_NAME = "UpdateCommand";

      const string PACKET_TAG = "packet";
      const string PASSWORD_TAG = "password";
      const string LOGIN_TAG = "userid";

      public UpdateCommand(string login, string pwd, byte[] packet)
      {
         format = new Format(OBJECT_NAME);
         format.Add(new StringFormat(LOGIN_TAG));
         format.Add(new StringFormat(PASSWORD_TAG));
         format.Add(new BinaryFormat(PACKET_TAG));

         Object obj = new Object(format);
         Add(obj);

         SetMemberValue(PACKET_TAG, packet);
         SetMemberValue(PASSWORD_TAG, pwd);
         SetMemberValue(LOGIN_TAG, login);
      }
   }

   public class ServerCommand : ObjectList
   {
      public static string Category = "manager";

      const string OBJECT_NAME = "ServerCommand";

      public const string COMMAND_TAG = "command";
      public const string PARAM_TAG = "param";
      protected const string USER_ID_TAG = "userid";
      protected const string PASSWORD_TAG = "password";
      protected const string VERSION_TAG = "version";
      protected const string DURATION_TAG = "duration";
      protected const string CATEGORY_TAG = "category";
      protected const string UUID_TAG = "uuid";

      public ServerCommand(string command, string param)
      {
         MakeFormat();

         Object obj = new Object(format);
         Add(obj);

         SetMemberValue(COMMAND_TAG, command);
         SetMemberValue(PARAM_TAG, param);
         SetMemberValue(CATEGORY_TAG, Category);
         
         Assembly a = Assembly.GetEntryAssembly();
         if (a != null) 
         {
            object[] attrs = a.GetCustomAttributes(typeof(AssemblyFileVersionAttribute), false);
            if (attrs.Length > 0)
               SetMemberValue(VERSION_TAG, (attrs[0] as AssemblyFileVersionAttribute).Version);
         }
      }

      private void MakeFormat()
      {
         format = new Format(OBJECT_NAME);

         format.Add(new StringFormat(COMMAND_TAG));
         format.Add(new StringFormat(PARAM_TAG));
         format.Add(new StringFormat(USER_ID_TAG));
         format.Add(new StringFormat(PASSWORD_TAG));
         format.Add(new StringFormat(VERSION_TAG));
         format.Add(new NumberFormat(DURATION_TAG));
         format.Add(new StringFormat(CATEGORY_TAG));
         format.Add(new StringFormat(UUID_TAG));
      }

      public string UserID
      {
         get { return GetMemberValue(USER_ID_TAG) as string; }
         set { SetMemberValue(USER_ID_TAG, value); }
      }

      public string Password
      {
         get { return GetMemberValue(PASSWORD_TAG) as string; }
         set { SetMemberValue(PASSWORD_TAG, value); }
      }

      public string Version
      {
         get { return GetMemberValue(VERSION_TAG) as string; }
         set { SetMemberValue(VERSION_TAG, value); }
      }

      public uint Duration
      {
         get { return (uint)GetMemberValue(DURATION_TAG); }
         set { SetMemberValue(DURATION_TAG, value); }
      }

      public string UUID
      {
         get { return GetMemberValue(UUID_TAG) as string; }
         set { SetMemberValue(UUID_TAG, value); }
      }

      override public Object AddObject()
      {
         Object o = base.AddObject();

         o[USER_ID_TAG].Value = UserID;
         o[PASSWORD_TAG].Value = Password;
         o[UUID_TAG].Value = UUID;

         return o;
      }
   }

   public class GetCommand : ServerCommand
   {
      public GetCommand(string login, string password, string[] objects) : base(Commands.GET, "")
      {
         StringBuilder param = new StringBuilder();
         foreach (string o in objects)
         {
            if (param.Length > 0) param.Append(',');
            param.Append(o);
         }

         Object obj = this[0];
         obj[PARAM_TAG].Value = param.ToString();

         UserID = login;
         Password = password;
      }
   }

   public class SelectCommand : ServerCommand
   {
      public SelectCommand(string login, string password, string objName, string filter)
         : base(Commands.SELECT, "")
      {
         Object obj = this[0];
         obj[PARAM_TAG].Value = objName + ":" + filter;

         UserID = login;
         Password = password;
      }

      public void Add(string objName, string filter)
      {
         Object obj = AddObject();

         obj[USER_ID_TAG].Value = UserID;
         obj[PASSWORD_TAG].Value = Password;
         obj[COMMAND_TAG].Value = Commands.SELECT;
         obj[PARAM_TAG].Value = objName + ":" + filter;
      }
   }

   public class UpdatePacket : ObjectList
   {
      const string OBJECT_NAME = "UpdatePacket";

      const string PACKET_TAG = "packet";

      public UpdatePacket(byte[] packet)
      {
         format = new Format(OBJECT_NAME);

         format.Add(new BinaryFormat(PACKET_TAG));

         Object obj = new Object(format);
         Add(obj);

         SetMemberValue(PACKET_TAG, packet);
      }
   }

   public class LicensedUsers : ObjectList
   {
      //static public string PDA_TYPE = "pda";
      //static public string ADS_TYPE = "ads";
      //static public string EXCLUSIVE_MANAGER = "exclusiveManager";
      //static public string VAN_PDA_TYPE = "vanpda";
      //static public string MANAGER_TYPE = "manager";
      //static public string BTL_TYPE = "btl";

      static public readonly LicensedUsers NONE = new LicensedUsers("нет", "");
      static public readonly LicensedUsers PDA = new LicensedUsers("Pre-Selling", "pda");
      static public readonly LicensedUsers BTL = new LicensedUsers("BTL", "btl");
      static public readonly LicensedUsers ADS = new LicensedUsers("ADS", "ads");
      static public readonly LicensedUsers VAN = new LicensedUsers("Van-Selling", "vanpda");
      static public readonly LicensedUsers EXCLUSIVE_MANAGER = new LicensedUsers("ExclManager", "exclusiveManager");
      static public readonly LicensedUsers MONITOR_MANAGER = new LicensedUsers("Монитор", "monitor");
      static public readonly LicensedUsers MANAGER = new LicensedUsers("Manager", "manager");
      static public readonly LicensedUsers ADSLIGHT = new LicensedUsers("Ads Light", "adslight");
      static public readonly LicensedUsers VEND = new LicensedUsers("Vend лицензии", "vend");
      static public readonly LicensedUsers EXPEDITOR_PDA = new LicensedUsers("Доставка", "expeditorpda");
      private string title;
      private string type;
      
      public int licenseID = -1;

      private static List<LicensedUsers> license;

      public LicensedUsers(string title, string type)
      {
         this.type = type;
         this.title = title;

         format = new Format("LicensedUsers");
         format.Add(new StringFormat("id"));
         format.Add(new StringFormat("type"));

         if (license == null)
            license = new List<LicensedUsers>();
         license.Add(this);
      }

      public static LicensedUsers Find(string type)
      {
         foreach (LicensedUsers u in license)
            if (u.Type == type)
               return u;

         return NONE;
      }

      //public LicensedUsers()
      //{
      //   format = new Format("LicensedUsers");
      //   format.Add(new StringFormat("id"));
      //   format.Add(new StringFormat("type"));
      //}

      public override string ToString() { return title; }
      public string Title { get { return title; } }
      public LicensedUsers Value { get { return this; } }
      public string Type { get { return type; }  } 

      //static public string TypeToValue(string type)
      //{
      //   if (type == PDA_TYPE) return PDA_LIC;
      //   if (type == ADS_TYPE) return ADS_LIC;
      //   if (type == EXCLUSIVE_MANAGER) return EXCL_MGR_LIC;
      //   if (type == VAN_PDA_TYPE) return VAN_LIC;
      //   if (type == MANAGER_TYPE) return MGR_LIC;
      //   if (type == BTL_TYPE) return BTL_LIC;
      //   return NONE;
      //}

      //static public string ValueToType(string type)
      //{
      //   if (type == PDA_LIC) return PDA_TYPE;
      //   if (type == ADS_LIC) return ADS_TYPE;
      //   if (type == EXCL_MGR_LIC) return EXCLUSIVE_MANAGER;
      //   if (type == VAN_LIC) return VAN_PDA_TYPE;
      //   if (type == MGR_LIC) return MANAGER_TYPE;
      //   if (type == BTL_LIC) return BTL_TYPE;
      //   return NONE;
      //}
   }

   public class MessageObject : ObjectList
   {
      public static string OBJECT_NAME = "Message";
      static string MESSSGE_TAG = "message";

      public MessageObject(string message)   
      {
         format = new Format(OBJECT_NAME);
         format.Add(new StringFormat(MESSSGE_TAG));

         if( Format.Find(OBJECT_NAME) == null )
            Format.Add(format);

         Object obj = new Object(format);
         Add(obj);

         SetMemberValue(MESSSGE_TAG, message);
      }
   }

   public enum RightActions { None = 0, Read = 1, Write = 2 }

   public class RightToken
   {
      public string key;
      public string desc;

      public RightToken(string key, string desc)
      {
         this.key = key;
         this.desc = desc;
      }

      public static RightToken EMPTY = new RightToken("", "");
   }

   public class RightTokens
   {
      class RTList : List<RightToken>
      {
         public RTList()
         {
//#if Servolux
            Add(new RightToken("DailyAgentPlansCommit", "Отправить план"));
            Add(new RightToken("FmDisabledFirms", "Запрет редактирования заявок"));
            Add(new RightToken("ReturnEditRigth", "Может утверждать возвраты"));
            Add(new RightToken("ReturnViewRigth", "Может просматривать возвраты"));
            Add(new RightToken("ShowADSReports", "Просмотр отчетов АДС"));
            Add(new RightToken("TaskWrite", "Может редактировать чужие задачи"));
            Add(new RightToken("CanChangeOrder", "Разрешить подрезку"));
            Add(new RightToken("CanChangeRoute", "Разрешить корректировку маршрута"));

// СПК
            Add(new RightToken("DisplayChecker", "Оператор по выкладке"));
            Add(new RightToken("Stuff", "Сотрудник"));
// Classic
            Add(new RightToken("EditRouteRight", "Может сохранять маршрут"));
            Add(new RightToken("EnterToDivision", "Может заходить в подразделение"));
// ADS RSUQualitet
            Add(new RightToken("WriteADSTask", "Может сохранять задачи"));
            Add(new RightToken("LimitEditRoute", "Сохранение маршрута"));
            Add(new RightToken("DisableEditDivision", "Запрет редактирования"));
            Add(new RightToken("CanManageContracts", "Редактирование посещений"));
            Add(new RightToken("DisableCopy", "Запрет копировать в БД документы"));
            Add(new RightToken("DisableDelete", "Запрет удалять документы"));
            Add(new RightToken("DisableLook", "Запрет просмотр фото"));
         }

      }

      static RTList tokens = new RTList();

      public static RightToken Get(string key)
      {
         foreach (RightToken rt in tokens)
            if (rt.key == key)
               return rt;
         
         return RightToken.EMPTY;
      }

      public static List<RightToken> Tokens { get { return tokens; } }
   }

   public class AnswerPool
   {
      string error;
      Dictionary<string, List<Dictionary<string, object>>> data = new Dictionary<string, List<Dictionary<string, object>>>();
      public AnswerPool() { }

      public string Error { get { return error; } }

      public ServerAnswer Result
      {
         get
         {
            List<ServerAnswer> ret = Results;
            return ret.Count > 0 ? ret[0] : new ServerAnswer();
         }
      }

      public List<ServerAnswer> Results { get { return Read(new ServerAnswer()); } }

      public bool Read(string body)
      {
         var res = JSON.ParseJSON(body);
         object objs;
         if (!res.TryGetValue("array0", out objs) || !(objs is List<object>))
         {
            error = "no_json_values";
            return false;
         }

         foreach (object el in (List<object>)objs)
         {
            Dictionary<string, object> dataObj = el as Dictionary<string, object>;
            if (dataObj == null) continue;

            string name = null;
            List<Dictionary<string, object>> data = null;
            foreach (KeyValuePair<string, object> dkv in dataObj)
            {
               if (dkv.Key == "name")
               {
                  name = dkv.Value.ToString();
                  continue;
               }

               if (dkv.Key == "data")
               {
                  List<object> src = dkv.Value as List<object>;
                  foreach (object vi in src)
                  {
                     Dictionary<string, object> eli = vi as Dictionary<string, object>;
                     if (eli != null)
                     {
                        if (data == null) data = new List<Dictionary<string, object>>();
                        data.Add(eli);
                     }
                  }
               }
            }

            if (name != null & data != null)
            {
               this.data[name] = data;
            }
         }

         return true;
      }

      public bool Read(StreamReader stream)
      {
         string body = stream.ReadToEnd();
         stream.Close();

         int pos = body.IndexOf("\r\n\r\n");
         if (pos < 0)
         {
            return Read(body);
         }

         return Read(body.Substring(pos + 4));
      }

      public static string ObjectName(object el)
      {
         string name;
         Type t = el.GetType();
         FieldInfo fi = t.GetField("OBJECT_NAME", BindingFlags.Static | BindingFlags.Public);
         if (fi == null)
         {
            name = t.Name;
         }
         else
         {
            name = fi.GetValue(el).ToString();
         }

         return name;
      }

      public List<T> Read<T>(T el)
      {
         List<T> res = new List<T>();

         Type t = el.GetType();
         string name = ObjectName(el);

         List<Dictionary<string, object>> els;
         if (!data.TryGetValue(name, out els))
         {
            return res;
         }

         ConstructorInfo ci = t.GetConstructor(Type.EmptyTypes);
         foreach (Dictionary<string, object> vals in els)
         {
            T element = (T)ci.Invoke((object[])null);
            if (SetElement(element, vals))
            {
               res.Add(element);
            }
         }
         return res;
      }

      bool SetElement<T>(T element, Dictionary<string, object> src)
      {
         Type tp = element.GetType();
         FieldInfo[] fields = tp.GetFields(BindingFlags.Public | BindingFlags.Instance);
         foreach (FieldInfo fi in fields)
         {
            object elval;
            if (src.TryGetValue(fi.Name, out elval) == false)
               continue;


            String val = elval as string;
            if (val != null)
            {
               Type proptp = fi.FieldType;
               if (proptp == typeof(string))
               {
                  fi.SetValue(element, val);
               }
               else if (proptp == typeof(DateTime))
               {
                  DateTime dt;
                  if (DateTime.TryParseExact(val, "yyyyMMddHHmmss", CultureInfo.InvariantCulture,
                     DateTimeStyles.None, out dt))
                  {
                     fi.SetValue(element, dt);
                  }
               }
               else
               {
                  TypeConverter tc = TypeDescriptor.GetConverter(proptp);
                  if (tc.CanConvertFrom(typeof(string)))
                     fi.SetValue(element, tc.ConvertFrom(val));
               }
            }
         }
         return true;
      }

      public static string ToJson(object el)
      {
         Type t = el.GetType();

         FieldInfo[] fields = t.GetFields(BindingFlags.Public | BindingFlags.Instance);

         StringBuilder sb = new StringBuilder("{");
         foreach (FieldInfo fi in fields)
         {
            Type ft = fi.FieldType;
            string name = fi.Name;
            object val = fi.GetValue(el);
            if (val == null)
               continue;

            if (ft == typeof(string))
            {
               sb.AppendFormat("\"{0}\":\"{1}\",", name, val.ToString());
            }
            else if (ft == typeof(DateTime))
            {
               sb.AppendFormat("\"{0}\":\"{1}\",", name, ((DateTime)val).ToString("yyyyMMddHHmmss"));
            }
            else
            {
               TypeConverter tc = TypeDescriptor.GetConverter(el);
               if (tc.CanConvertTo(typeof(string)))
               {
                  sb.AppendFormat("\"{0}\":\"{1}\",", name, tc.ConvertTo(val, typeof(string)));
               }
            }
         }

         sb.Remove(sb.Length - 1, 1);
         sb.Append("}");

         return sb.ToString();
      }
   }

   public class GRServerInfo
   {
      public static readonly string OBJECT_NAME = "ServerInfo";
      public string address = "";
      public int port = 0;
      public string error = "";
      public bool fail = false;
   }

   public class ServerAnswer
   {
      public int response = 0;
      public string message = "";

      public bool Good { get { return response != 0; } }
   }

   public class LinkedUser
   {
      public static readonly string OBJECT_NAME = "LinkedUsers";
      public string code = "";
      public string server_code = "";
      public string id = "";

      public string error = "";
   }

   public class ConnectionHelper
   {
#if TEST
      public static string HOST = "https://dev.aceteam.app";
#else
#if LOCALHOST
      public static string ADDR = "172.25.211.121";
      public static int PORT = 3000;
      public static string HOST = "http://localhost";
#else
      public static string HOST = "https://napmobile.ru";
#endif

#endif

      static public LinkedUser RequestLink(string code)
      {
         LinkedUser lu = new LinkedUser();
         WebClient client = new WebClient();

         ServicePointManager.SecurityProtocol = SecurityProtocolType.Tls12 | SecurityProtocolType.Tls11 | SecurityProtocolType.Tls | SecurityProtocolType.Ssl3;

         client.Headers.Add("Content-Type", "application/json; charset=utf-8");

         string url = string.Format("{0}/api/link_user?code={1}&type=DivisionManager", HOST, code);

         try
         {
            Stream data = client.OpenRead(url);
            StreamReader sr = new StreamReader(data);
            AnswerPool ap = new AnswerPool();
            if (ap.Read(sr))
            {
               ServerAnswer result = ap.Result;
               if (result.Good)
               {
                  List<LinkedUser> reslu = ap.Read(lu);
                  if (reslu.Count > 0)
                  {
                     lu = reslu[0];
                  }
                  else
                  {
                     lu.error = "no_date";
                  }
               }
               else
               {
                  lu.error = result.message;
               }
            }
            else
            {
               lu.error = ap.Error;
            }
         } catch(Exception e)
         {
            lu.error = e.Message;
         }
         return lu;
      }

      static public GRServerInfo GetServerInfo(string serverCode)
      {
         WebClient client = new WebClient();

         ServicePointManager.SecurityProtocol = SecurityProtocolType.Tls12 | SecurityProtocolType.Tls11 | SecurityProtocolType.Tls | SecurityProtocolType.Ssl3;

         client.Headers.Add("Content-Type", "application/json; charset=utf-8");
         client.Headers.Add("Authorization", "Bearer " + serverCode);


         GRServerInfo si = new GRServerInfo();
#if LOCALHOST
         if(PORT != 0) 
         {
            si.address = ADDR;
            si.port = PORT;
            return si;
         }
#endif
         try
         {
            string url = HOST + "/api/server";
            Stream data = client.OpenRead(url);
            StreamReader sr = new StreamReader(data);
            AnswerPool ap = new AnswerPool();
            if (ap.Read(sr))
            {
               ServerAnswer result = ap.Result;
               if (result.Good)
               {
                  List<GRServerInfo> res = ap.Read(si);
                  if (res.Count > 0)
                  {
                     si = res[0];
                  }
                  else
                  {
                     si.fail = true;
                     si.error = "no_data";
                  }
               }
               else
               {
                  si.fail = true;
                  si.error = result.message;
               }
            }
            else
            {
               si.fail = true;
               si.error = ap.Error;
            }
         }
         catch (Exception e)
         {
            si.fail = true;
            si.error = e.ToString();
         }

         return si;
      }
   }
}
