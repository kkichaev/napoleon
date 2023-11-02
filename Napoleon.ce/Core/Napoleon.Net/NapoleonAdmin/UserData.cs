using System.Collections.Generic;
using System;
using GRSoft.Network;
using System.Windows.Forms;
namespace GRSoft.NapoleonAdmin
{
   public class UserData : List<UserDataItem>
   {
      public static readonly string CAN_CHANGE_KEY = "CanChangePassword";
      public DataSet<int, ManagerConfigObj> dsMgrConfig;
      public DataSet<int, ServerConfig> dsServerConfig;
      public SimpleDataSet<ServerConfig> dsRemoveConfig = new SimpleDataSet<ServerConfig>("ServerConfig", false);

      List<IDataSet> addWrSets = new List<IDataSet>();
      List<IDataSet> rmvSet = new List<IDataSet>();

      public UserData(DataSet<int, ServerConfig> dsServerConfig)
      {
         this.dsServerConfig = dsServerConfig;
      }

      public bool DisableChangePwd(string login)
      {
         bool ret = false;
         if (dsMgrConfig == null)
            return ret;

         foreach (ManagerConfigObj obj in dsMgrConfig.Data)
         {
            if (obj.manager == login && obj.key == CAN_CHANGE_KEY)
            {
               int res = 0;
               Int32.TryParse(obj.value, out res);
               ret = (res == 0);
               break;
            }
         }
         return ret;
      }

      public void SetDisableChangePwd(string login, bool disable)
      {
         if (dsMgrConfig == null)
            return;

         Resolver r = new Resolver("DisableChangePwd", !disable, disable, null);
         FireChanging(r);

         int maxI = 0;
         string value = (!disable) ? "1" : "0";
         bool updated = false;
         foreach (KeyValuePair<int, ManagerConfigObj> kv in dsMgrConfig)
         {
            if (maxI < kv.Key)
               maxI = kv.Key;

            ManagerConfigObj obj = kv.Value;
            if (obj.manager == login && obj.key == CAN_CHANGE_KEY)
            {
               obj.value = value;
               updated = true; ;
               break;
            }
         }

         if (!updated)
         {
            ManagerConfigObj mo = new ManagerConfigObj();
            mo.manager = login;
            mo.key = CAN_CHANGE_KEY;
            mo.value = value;
            dsMgrConfig.Add(maxI + 1, mo);
         }
      }

      public void FireChanging(Resolver resolver)
      {
         if (OnChangingData != null)
         {
            OnChangingData(resolver);
         }
      }

      public void FireChanged()
      {
         if (OnChangedData != null)
         {
            OnChangedData();
         }
      }

      public int GetInstalledLisences(LicensedUsers type)
      {
         int result = 0;

         foreach (UserDataItem udi in this)
         {
            if (udi.License == type)
            {
               result++;
            }
         }

         return result;
      }

      public void AddWriteSet(IDataSet dataSet)
      {
         if (!addWrSets.Contains(dataSet))
            addWrSets.Add(dataSet);
      }

      public void AddRemoveSet(IDataSet dataSet)
      {
         if (!rmvSet.Contains(dataSet))
            rmvSet.Add(dataSet);
      }

      public bool CommitEdit(Config config, bool saveAgents, DataSet<string, LicenseType> dsLicenseTypes, 
         DataSet<string, LicensingUsersData> dsLicensingUsersData, MainForm owner)
      {
         DataSet<String, DivisionManager> managers = new DataSet<string, DivisionManager>("DivisionManager", false);
         DataSet<String, DivisionManager> rmvManagers = new DataSet<string, DivisionManager>("DivisionManager", false);
         DataSet<string, Agent> agents = new DataSet<string, Agent>("Agents", false, true);
         DataSet<String, LicensedUser> users = (DataSet<String, LicensedUser>)DataModule.Get("LicensedUsers");

         Dictionary<String, String> licensed = new Dictionary<string, String>();

         DataSet<String, DivisionManager> dsManagers = (DataSet<string, DivisionManager>)DataModule.Get("DivisionManager");
         DataSet<string, Agent> dsAgents = (DataSet<string, Agent>)DataModule.Get("Agents");
         DataSet<string, Agent> rmAgents = new DataSet<string, Agent>("Agents", false);

         List<LicensingUsersData> licUsers = new List<LicensingUsersData>();
         bool agentAdded = false;

         foreach (UserDataItem udi in this)
         {
            dsLicensingUsersData.Remove(udi.Login);
            if( dsLicenseTypes.ContainsKey(udi.License.Type) )
            {
               LicensingUsersData lud = new LicensingUsersData();
               lud.login = udi.Login;
               lud.licenseID = udi.License.licenseID;
               licUsers.Add(lud);
            }

            if (saveAgents)
            {
               if (udi.Id == null || udi.Login == null)
               {
                  MessageBox.Show("Не заполнены поля id или логин а агента");
                  return false;
               }
               foreach (DivisionManager m in dsManagers.Data)
               {
                  if (m.login.Trim().ToLower().Equals(udi.Login.Trim().ToLower()))
                  {
                     MessageBox.Show("Логин агента \"" + udi.Login +
                        "\" совпадает с логином менеджера, сохранить данные невозможно",
                        "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
                     return false;
                  }
               }

               Agent a = null;

               if (dsAgents.ContainsKey(udi.Id))
                  a = dsAgents[udi.Id];
               else
               {
                  a = new Agent();
                  a.id = udi.Id;

                  if (!agentAdded)
                     agentAdded = true;
               }

               udi.SetAgent(a);

               if (udi.markToDel)
               {
                  if (!rmAgents.ContainsKey(udi.Id))
                     rmAgents.Add(a.id, a);
               }
               else
                  agents.Add(a.id, a);

               ServerConfig serverConfig = GetTrackingConfig(a.id);
               serverConfig.value = udi.TrackingCode;

               if (udi.License != LicensedUsers.NONE && dsLicenseTypes.ContainsKey(udi.License.Type) == false)
                  licensed.Add(a.id, udi.License.Type);


            }
            else
            {
               foreach (Agent a in dsAgents.Data)
               {
                  if (a.login.Trim().ToLower().Equals(udi.Login.Trim().ToLower()))
                  {
                     MessageBox.Show("Логин менеджера \"" + udi.Login +
                        "\" совпадает с логином агента, сохранить данные невозможно",
                        "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
                     return false;
                  }
               }

               DivisionManager dm = udi.Manager;

               if (dm == null)
               {
                  dm = new DivisionManager();
               }
               udi.SetManager(dm);

               if (udi.OldLogin != udi.Login)
               {
                  DivisionManager rmv = new DivisionManager();
                  udi.SetManager(rmv);
                  rmv.login = udi.OldLogin;
                  rmvManagers.Add(rmv.login, rmv);
                  dsManagers.Remove(udi.OldLogin);

                  udi.OldLogin = udi.Login;
                  dm.login = udi.Login;
               }
               managers.Add(dm.login, dm);
               dsManagers[dm.login] = dm;

               if (udi.License != LicensedUsers.NONE && dsLicenseTypes.ContainsKey(udi.License.Type) == false)
                  licensed.Add(dm.login, udi.License.Type);
            }
         }

         List<String> remove = new List<String>();
         foreach (KeyValuePair<String, LicensedUser> lu in users)
         {
            if (saveAgents && MainForm.IsManagerLicType(lu.Value.type) || !saveAgents && !MainForm.IsManagerLicType(lu.Value.type))
            {
               continue;
            }
            if (!licensed.ContainsKey(lu.Value.id))
               remove.Add(lu.Key);
            else if (licensed[lu.Key] == lu.Value.type)
               licensed.Remove(lu.Value.id);
         }

         bool licenseChanged = (remove.Count > 0 || licensed.Count > 0 || agentAdded);

         foreach (string iv in remove)
            users.Remove(iv);

         foreach (KeyValuePair<String, String> kv in licensed)
         {
            LicensedUser lu = new LicensedUser();
            lu.agent = (agents.ContainsKey(kv.Key)) ? agents[kv.Key] : null;
            lu.id = kv.Key;
            lu.type = kv.Value; 
            if (users.ContainsKey(lu.id))
               users.Remove(lu.id);
            users.Add(lu.id, lu);
         }

         //Config c = Config.Load();
         DBConnection conn = config.GetConnection();
         List<IDataSet> wrObj = new List<IDataSet>();
#if DONT_EDIT_USERS
         if (saveAgents) {
         }
         else
#else
         if (saveAgents)
         {
            if (agents.Count > 0)
               wrObj.Add(agents);
         }
         else
#endif
         {
            if(managers.Count > 0)
               wrObj.Add(managers);
            if (dsMgrConfig != null && dsMgrConfig.Count > 0)
               wrObj.Add(dsMgrConfig);
         }

         if (licenseChanged)
            wrObj.Add(users);
         if(dsServerConfig.Count > 0)
            wrObj.Add(dsServerConfig);
         wrObj.AddRange(addWrSets);
         addWrSets.Clear();

         foreach (LicensingUsersData lud in licUsers)
            dsLicensingUsersData[lud.login] = lud;
         if (dsLicensingUsersData.Count > 0)
            wrObj.Add(dsLicensingUsersData);

         List<IDataSet> rmvObj = new List<IDataSet>();
         if (rmvManagers.Count > 0)
            rmvObj.Add(rmvManagers);
         if (rmAgents.Count > 0)
            rmvObj.Add(rmAgents);
         if (dsRemoveConfig.Count > 0)
            rmvObj.Add(dsRemoveConfig);

         rmvObj.AddRange(rmvSet);

         owner.BeforeUpdate(wrObj, rmvObj);
         bool res = DataModule.UpdateDataSet(wrObj, rmvObj, null, conn);
         owner.OnUpdate(res);

         if(res)
         {
            rmvManagers.Clear();
            rmAgents.Clear();
            dsRemoveConfig.Clear();

            rmvSet.Clear();
            addWrSets.Clear();
         }
         return res;
      }

      public ServerConfig GetConfig(string userid, string key, bool onlyFind)
      {
         foreach (ServerConfig sc in dsServerConfig.Data)
         {
            if (sc.userid == userid && sc.key == key)
               return sc;
         }
         if (onlyFind)
            return null;

         ServerConfig result = new ServerConfig();
         result.userid = userid;
         result.key = key;

         dsServerConfig.Add(dsServerConfig.Count + 1, result);
         return result;
      }

      private ServerConfig GetTrackingConfig(string userid)
      {
         return GetConfig(userid, "Tracking", false);
         //foreach (ServerConfig sc in dsServerConfig.Data)
         //{
         //   if (sc.userid.Equals(userid) &&
         //      sc.key.Equals("Tracking"))
         //      return sc;
         //}

         //ServerConfig result = new ServerConfig();
         //result.userid = userid;
         //result.key = "Tracking";

         //dsServerConfig.Add(dsServerConfig.Count + 1, result);
         //return result;
      }

      public void DoSort(string cmpField, SortOrder sortOrder)
      {
         UserDataItem.CC.SetCompareCondition(cmpField, sortOrder == SortOrder.Ascending);
         Sort();
      }

      public event EventUserData OnChangingData;
      public event EmptyParamHandler OnChangedData;

   }

   public class Resolver
   {
      public enum RespondType { OK, CANCEL }

      private string field;
      private object oldValue;
      private object newValue;
      private RespondType answer = RespondType.OK;
      public UserDataItem item;

      public Resolver(string field, object oldValue, object newValue, UserDataItem item)
      {
         this.field = field;
         this.oldValue = oldValue;
         this.newValue = newValue;
         this.item = item;
      }

      public string Field { get { return field; } }
      public object OldValue { get { return oldValue; } }
      public object NewValue { get { return newValue; } }
      public RespondType Respond { get { return answer; } set { answer = value; } }
   }

   public delegate void EventUserData(Resolver listener);
}