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

      override public Object AddObject()
      {
         Object o = base.AddObject();

         o[USER_ID_TAG].Value = UserID;
         o[PASSWORD_TAG].Value = Password;

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
}
