using System;
using GRSoft.Network;
using System.Reflection;
using System.Windows.Forms;
using System.Collections.Generic;

namespace GRSoft.NapoleonAdmin
{
   public class UserDataItem : CmpByField<UserDataItem>
   {
      protected string id;
      protected string progid;
      protected string name;
      protected string login;
      protected string passw;
      protected DateTime lastAccess;
      protected string version;
      protected LicensedUsers licenseType;
      //protected bool hasLicence;
      protected UserData container;
      protected string tracking;
      protected int division = 0;
      protected string oldLogin;
      public bool markToDel;

      protected Agent agent;
      protected DivisionManager manager;
      public object refObject = null;

      public static List<string> ManagerRights = new List<string>();


      public UserDataItem(UserData container)
      {
         this.container = container;
      }

      protected void SetCommonData(UserActivity ua, LicensedUsers licType, string tracking)
      {
         if (ua != null)
         {
            this.lastAccess = ua.date;
            this.version = ua.version;
         }
         this.tracking = tracking;
         this.licenseType = licType;
         this.oldLogin = login;
      }

      public virtual void Set(Agent a, UserActivity ua, LicensedUsers licType, string tracking)
      {
         this.agent = a;
         this.id = a.id;
         this.name = a.name;
         this.login = a.login;
         this.passw = a.password;
         this.progid = a.progid;

         SetCommonData(ua, licType, tracking);
      }

      public virtual void Set(DivisionManager m, DataSet<int, Division> dsDivision,
         UserActivity ua, LicensedUsers licType, string tracking)
      {
         this.manager = m;
         string name =  m.name.Length > 0 ? m.name : m.login;
         if (dsDivision.ContainsKey(m.division))
            name = dsDivision[m.division].name;

         this.id = m.login;
         this.name = name;
         this.login = m.login;
         this.passw = m.password;
         this.progid = "";
         this.division = m.division;

         SetCommonData(ua, licType, tracking);
      }

      public virtual void SetAgent(Agent a)
      {
         a.login = login;
         a.name = name;
         a.password = passw;
         a.progid = progid;
      }

      public virtual void SetManager(DivisionManager manager)
      {
         manager.division = division;
         manager.login = login;
         manager.password = passw;

         if (this.manager != null && this.manager != manager)
         {
            manager.AddRights(this.manager.rights);
            manager.RemvoeBadRights();
         }
      }

      public DivisionManager Manager { get { return manager; } }

      //public UserDataItem(UserData container, string id, string name, string login, string progid,
      //   string passw, DateTime lastAccess, string version, LicensedUsers licType,
      //   string tracking)
      //{
      //   this.container = container;
      //   this.id = id;
      //   this.name = name;
      //   this.login = login;
      //   this.passw = passw;
      //   this.progid = progid;
      //   this.lastAccess = lastAccess;
      //   this.version = version;
      //   //this.hasLicence = hasLicence;
      //   this.tracking = tracking;
      //   this.licenseType = licType;
      //   this.oldLogin = login;
      //}

      protected bool DoChanging(String propName, String fieldName, object value)
      {
         if (container == null)
            return true;

         bool ret = false;
         try
         {
            FieldInfo field = null;
            Type curType = GetType();
            do
            {
               field = curType.GetField(fieldName, BindingFlags.Instance | BindingFlags.NonPublic | BindingFlags.Public);
               if (field != null || curType == typeof(UserDataItem))
                  break;
               curType = curType.BaseType;
            } while (true);

            if (field != null)
            {
               Resolver resolver = new Resolver(propName, field.GetValue(this), value, this);
               container.FireChanging(resolver);
               if (resolver.Respond == Resolver.RespondType.OK)
               {
                  field.SetValue(this, value);
                  ret = true;
               }
            }
            else
            {
               MessageBox.Show("В классе UserDataItem нет поля с именем " + fieldName);
            }
         }
         catch (Exception)
         {
         }

         return ret;
      }

      public string OldLogin { get { return oldLogin; } set { oldLogin = value; } }
      public string Id
      {
         get { return id; }
         set
         {
#if EDIT_USER
            DoChanging("Id", "id", value);
#endif
         }
      }

      public string Name
      {
         get { return name; }
         set
         {
            DoChanging("Name", "name", value);
         }
      }

      public int Division { get { return division; } set { division = value; } }

      public Agent Agent { get { return agent; } }

      public LicensedUsers License
      {
         get { return licenseType; }
         set
         {
            if( DoChanging("License", "licenseType", value) )
               container.FireChanged();
         }
      }

      public bool DisablePwdChg
      {
         get { return container.DisableChangePwd(login); }
         set { container.SetDisableChangePwd(login, value); }
      }

      public string Login
      {
         get { return login ?? string.Empty; }
         set
         {
            DoChanging("Login", "login", value);
         }
      }

      public string Passw
      {
         get { return passw ?? string.Empty; }
         set
         {
            DoChanging("Passw", "passw", value);
         }
      }

      public string ProgID
      {
         get { return progid; }
         set
         {
            DoChanging("ProgID", "progid", value);
         }
      }

      public DateTime LastAccess { get { return lastAccess; } }
      public string Version { get { return version; } }
      //public bool HasLicence
      //{
      //   get { return hasLicence; }
      //   set
      //   {
      //      Resolver resolver = new Resolver("HasLicence", passw, value);
      //      container.FireChanging(resolver);
      //      if (resolver.Respond == Resolver.RespondType.OK)
      //      {
      //         hasLicence = value;
      //      }
      //      container.FireChanged();
      //   }
      //}

      public bool Tracking
      {
         get { return tracking != "none"; }
         set
         {
            if( DoChanging("Tracking", "tracking", value ? "GPSroute" : "none") )
               container.FireChanged();
         }
      }

      public string TrackingCode { get { return tracking; } }

      private string GetTrackCaption(string code)
      {
         if (code.Equals("none", StringComparison.CurrentCultureIgnoreCase))
            return "нет";
         else if (code.Equals("GSM"))
            return "GSM";
         else if (code.Equals("GPSpoint"))
            return "торг.точки GPS";
         else if (code.Equals("GPSroute"))
            return "маршрут GPS";
         else
            return "нет";
      }

      private string GetTrackingCode(string caption)
      {
         if (caption.Equals("нет"))
            return "none";
         else if (caption.Equals("GSM"))
            return "GSM";
         else if (caption.Equals("торг.точки GPS"))
            return "GPSpoint";
         else if (caption.Equals("маршрут GPS"))
            return "GPSroute";
         else
            return string.Empty;
      }

      [Compare]
      public static CompareCondition CC = new CompareCondition();


      bool HaveRightToWrite(string name)
      {
         if (manager != null)
            return manager.HaveRight(RightTokens.Get(name), RightActions.Write);
         return false;
      }

      protected virtual void UpdateRight(string name, bool value)
      {
         if (manager == null)
            return;

         manager.ChangeRight(RightTokens.Get(name), value ? RightActions.Write : RightActions.Read);

         Resolver resolver = new Resolver(name, !value, value, null);
         container.FireChanging(resolver);
      }

      public bool Right_0
      {
         get { return ManagerRights.Count > 0 ? HaveRightToWrite(ManagerRights[0]) : false; }
         set
         {
            if (ManagerRights.Count > 0)
               UpdateRight(ManagerRights[0], value);
         }
      }

      public bool Right_1
      {
         get { return ManagerRights.Count > 1 ? HaveRightToWrite(ManagerRights[1]) : false; }
         set
         {
            if (ManagerRights.Count > 1)
               UpdateRight(ManagerRights[1], value);
         }
      }

      public bool Right_2
      {
         get { return ManagerRights.Count > 2 ? HaveRightToWrite(ManagerRights[2]) : false; }
         set
         {
            if (ManagerRights.Count > 2)
               UpdateRight(ManagerRights[2], value);
         }
      }

      public bool Right_3
      {
         get { return ManagerRights.Count > 3 ? HaveRightToWrite(ManagerRights[3]) : false; }
         set
         {
            if (ManagerRights.Count > 3)
               UpdateRight(ManagerRights[3], value);
         }
      }

      public bool Right_4
      {
         get { return ManagerRights.Count > 4 ? HaveRightToWrite(ManagerRights[4]) : false; }
         set
         {
            if (ManagerRights.Count > 4)
               UpdateRight(ManagerRights[4], value);
         }
      }
   }
}