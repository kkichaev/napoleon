/*
 * Copyright (C), 2009 - 2010, Гильдия разработчиков
 *
 * Основная форма
 * 
 * ert   26/11/2009   creating
 */
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Net.Sockets;
using System.Net;
using System.Threading;
using System.Collections;
using System.IO;
using System.Reflection;

namespace GRSoft.NapoleonAdmin
{
   public partial class MainForm : Form
   {
      protected DataSet<string, Agent> dsAgents = Agents.GetDataSet();
      private DataSet<string, LicensedUser> dsLisensedUsers = new DataSet<string, LicensedUser>("LicensedUsers");
      private DataSet<int, LicenseCount> dsLicenseCount = new DataSet<int, LicenseCount>("LicenseCount");
      private DataSet<string, LicenseCountEx> dsLicenseCountEx = new DataSet<string, LicenseCountEx>(LicenseCountEx.OBEJCT_NAME);
      public DataSet<string, UserActivity> dsUserActivity = new DataSet<string, UserActivity>("UserActivity");
      protected DataSet<string, DivisionManager> dsManagers = new DataSet<string, DivisionManager>("DivisionManager");
      private DataSet<int, ServerConfig> dsServerConfig = new DataSet<int, ServerConfig>("ServerConfig");
      protected DataSet<int, ServerConfig> dsCommonConfig = new DataSet<int, ServerConfig>("ServerConfig");
      protected DataSet<int, Division> dsDivision = new DataSet<int, Division>(Division.OBJECT_NAME);
      private DataSet<int, ManagerConfigObj> dsMgrConfig = new DataSet<int, ManagerConfigObj>(ManagerConfigObj.OBJECT_NAME);
      private DataSet<int, SyncInfo> dsSyncInfo = new DataSet<int, SyncInfo>(SyncInfo.OBJECT_NAME);

      protected DataSet<int, LicenseProjectData> dsLicenseProjectData = new DataSet<int, LicenseProjectData>(LicenseProjectData.OBJECT_NAME);
      protected DataSet<string, LicenseType> dsLicenseTypes = new DataSet<string, LicenseType>(LicenseType.OBJECT_NAME);
      protected DataSet<string, LicensingUsersData> dsLicensingUsersData = new DataSet<string, LicensingUsersData>(LicensingUsersData.OBJECT_NAME);

      public UserData userData;
      private bool needCheck = false;

      public Config config;

      System.Windows.Forms.Timer tmrFind = new System.Windows.Forms.Timer();

      List<DataGridViewCheckBoxColumn> managerRightColumns = new List<DataGridViewCheckBoxColumn>();

      const int TIMEOUT = 60 * 1000;

      int pdaLicenseCount;
      int pdaVanLicenseCount;
      int mgrLicenseCount;
      int exMgrLicenseCount;
      int adsLicenseCount;
      int btlLicenseCount;
      int expdtrLicenseCount;

      List<string> licensedUsers = new List<string>();
      ObjectList loadedAgents = null;
      static readonly String GPSPERIOD = "gpstimecond";
      static readonly String PREZENTPATH = "prezentpath";

      static LicensedUsers dispLic = new LicensedUsers("Дипетчер", "dispatcher");

#if FastFood
      int ffPdaLicenseCount = 0;
      LicensedUsers FF_PDA;
#endif

#if VEND_LICENSE
      int vandLicenseCount = 0;
      static LicensedUsers VEND_LICENSE;
#endif

#if SKLAD_W_PDA
      int ffPdaLicenseCount = 0;
      static LicensedUsers FF_PDA = new LicensedUsers("Склад КПК", "skaldwspda");
#endif

#if AviaKos
      int expdPdaLicenseCount = 0;
      LicensedUsers EXPD_PDA;
#endif

      public MainForm()
      {
         InitializeComponent();

         userData = new UserData(dsServerConfig);

#if REMOVE_PHOTOS
#else
         tabControl1.TabPages.Remove(rmvScheduler);
#endif

         dgvActivity.AutoGenerateColumns = false;

         usersView.MultiSelect = true;

#if CHECK_LOGIN_PROGID
         ProgID.Visible = true;
#endif
#if !GPSTIMECOND
         tabControl1.TabPages.Remove(gps);
#endif
#if FastFood
         FF_PDA = new LicensedUsers("Bofrost", "fastfoodpda");
#endif
#if VEND_LICENSE
         VEND_LICENSE = LicensedUsers.VEND;
#endif
#if SKLAD_W_PDA
         FF_PDA = new LicensedUsers("Склад КПК", "skaldwspda");
#endif
#if AviaKos
         EXPD_PDA = new LicensedUsers("Экспедитор КПК", "expeditorpda");
#endif
#if !CONFIG_HISTORY_ADM
         label11.Visible = false;
         label12.Visible = false;
         tbName.Visible = false;
         lbHistory.Visible = false;
#endif

#if PREZENT_FOLDER_ADM
         toolStripLabel1.Visible = true;
         tbPresentFolder.Visible = true;
         btnFolder.Visible = true;
#else
         toolStripLabel1.Visible = false;
         tbPresentFolder.Visible = false;
         btnFolder.Visible = false;
#endif

#if Marshaev
         login.Visible = false;
         password.Visible = false;
         tracking.Visible = false;
         cbUserType.Visible = false;
#endif

         cbAgentSyncInfo.SelectedIndex = 0;
         dgvSyncInfo.AutoGenerateColumns = false;

         tmrFind.Interval = 500;
         tmrFind.Tick += TmrFind_Tick;

         Init();
      }

      private void TmrFind_Tick(object sender, EventArgs e)
      {
         tmrFind.Stop();

         UserData src = null;
         string text = tsFind.Text.Trim().ToUpper();
         if(text.Length == 0)
         {
            src = userData;
         }
         else
         {
            src = new UserData(dsServerConfig);
            foreach(UserDataItem udi in userData)
            {
               if(udi.Name.ToUpper().Contains(text))
               {
                  src.Add(udi);
               }
            }
         }

         makeDataSource(src);

      }

      private void Init()
      {
         Assembly a = Assembly.GetEntryAssembly();
         object[] attrs = a.GetCustomAttributes(typeof(AssemblyFileVersionAttribute), false);
         if (attrs.Length > 0)
         {
            version.Text = "версия: " + (attrs[0] as AssemblyFileVersionAttribute).Version;

            string f = a.GetModules()[0].FullyQualifiedName;
            version.Text += " / " + File.GetLastWriteTime(f).ToShortDateString();
         }
         else
            version.Text = "";

         config = Config.Load();

         cbUserType.SelectedIndex = 0;

         usersView.AutoGenerateColumns = false;
         
         userData.OnChangingData += new EventUserData(OnUserDataChanging);
         userData.OnChangedData += new EmptyParamHandler(OnUserDataChanged);

         if (!config.IsLoaded || !config.rememberPassword)
         {
            LoadSettings(config);
            needCheck = true;
            tabControl1.SelectedTab = settings;
         }

#if EDIT_USER
         clmnId.ReadOnly = false;
         btnAdd.Enabled = false;
         btnEdit.Enabled = false;
         btnDel.Enabled = false;
         user.ReadOnly = false;
#elif EDIT_USER_NAME
         btnAdd.Visible = false;
         btnDel.Visible = false;
         btnEdit.Enabled = false;
         user.ReadOnly = false;
#else
         btnAdd.Visible = false;
         btnEdit.Visible = false;
         btnDel.Visible = false;
#endif

         IFormDecorator dec = FormEntries.GetFormDecorator(this.GetType());
         if (dec != null)
            dec.Decorate(this);

         rmvScheduler1.Init(this.config);
      }

      protected void InitRightColumns(string[] rights, string[] header)
      {
         SuspendLayout();
         for(int i=0; i<rights.Length && i<header.Length; i++)
         {
            DataGridViewCheckBoxColumn clmn = new DataGridViewCheckBoxColumn();

            clmn.DataPropertyName = "Right_" + i.ToString();
            clmn.HeaderText = header[i];
            clmn.Name = clmn.DataPropertyName;
            clmn.Visible = false;
            clmn.Width = 70;

            usersView.Columns.Add(clmn);
            managerRightColumns.Add(clmn);
            UserDataItem.ManagerRights.Add(rights[i]);
         }
         ResumeLayout();
      }

      protected virtual void OnUserDataChanging(Resolver resolver)
      {
         userChangesSave.Enabled = true;

         if (resolver.Field.Equals("License"))
         {
            int count = 0;
            LicensedUsers licType = resolver.NewValue as LicensedUsers;
            if (licType == LicensedUsers.PDA)
               count = pdaLicenseCount;
            else if (licType == LicensedUsers.VAN)
               count = pdaVanLicenseCount;
            else if (licType == LicensedUsers.EXCLUSIVE_MANAGER)
               count = exMgrLicenseCount;
            else if (licType == LicensedUsers.ADS)
               count = adsLicenseCount;
            else if (licType == LicensedUsers.BTL)
               count = btlLicenseCount;
            else if (licType == LicensedUsers.EXPEDITOR_PDA)
               count = expdtrLicenseCount;
#if FastFood || SKLAD_W_PDA
            else if( licType == FF_PDA )
               count = ffPdaLicenseCount;
#endif
#if VEND_LICENSE
            else if (licType == VEND_LICENSE)
               count = vandLicenseCount;
#endif
            else
               return;

            int curCount = userData.GetInstalledLisences(licType);
            if (count - 1 < curCount)
            {
               String text = "Число назначенных лицензий - ";
               text += curCount.ToString();
               text += "\nбольше числа доступных лицензий.";
               MessageBox.Show(text, "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Information);
               resolver.Respond = Resolver.RespondType.CANCEL;
            }
         }
         //else if (resolver.Field.Equals("HasLicence"))
         //{
         //   //int count = ((cbUserType.SelectedIndex == 0) ? pdaLicenseCount : exMgrLicenseCount);
         //   //if (count - userData.GetInstalledLisences() - Convert.ToInt32(resolver.NewValue) < 0)
         //   //{
         //   //   MessageBox.Show("Число назначенных лицензий - " + 
         //   //      (userData.GetInstalledLisences() + Convert.ToInt32(resolver.NewValue)).ToString() 
         //   //      + "\nбольше числа доступных лицензий.",
         //   //      "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Information);
         //   //   resolver.Respond = Resolver.RespondType.CANCEL;
         //   //}
         //}
      }

      private void OnUserDataChanged()
      {
         SetStatusText();
      }

      public bool IsDirty
      {
         get { return userChangesSave.Enabled; }
         set
         {
            if (userChangesSave.Enabled != value)
            {
               BeginInvoke(new EnableControlHandler(EnableControl), new object[] { new object[] {userChangesSave}, value });
            }
         }
      }

      private delegate void EnableControlHandler(object[] buttons, bool value);
      private delegate void SetStatusTextHandler();

      void EnableControl(object[] buttons, bool value)
      {
         foreach (object button in buttons)
         {
            if (button is ToolStripButton)
               (button as ToolStripButton).Enabled = value;
            if (button is Control)
               (button as Control).Enabled = value;
         }
      }

      public static bool IsManagerLicType(string type)
      {
         return (type == LicensedUsers.EXCLUSIVE_MANAGER.Type) || (type == LicensedUsers.MONITOR_MANAGER.Type);
      }

      protected virtual void SetStatusText()
      {
         bool isAgents = cbUserType.SelectedIndex == 0;
         String text = "Всего ";
         if (isAgents)
         {
            foreach (LicenseCountEx lce in dsLicenseCountEx.Data)
            {
               LicensedUsers lu = FindLicense(lce.type);
               if( lu != LicensedUsers.NONE)
               {
                  text += lu.Title + ": (" + lce.count + "/" + (lce.count - CountLicense(lce.type)).ToString() + "); ";
               }
            }

            if (mgrLicenseCount > 0)
               text += " всего РМР: " + mgrLicenseCount;
         }
         else
         {
            text += "РМР: " + mgrLicenseCount;

            foreach (LicenseCountEx lce in dsLicenseCountEx.Data)
            {
               if (!IsManagerLicType(lce.type))
                  continue;
               LicensedUsers lu = FindLicense(lce.type);
               if (lu != LicensedUsers.NONE)
               {
                  
                  text += "; " + lu.Title + ": (" + lce.count + "/" + (lce.count - CountLicense(lce.type)).ToString() + ")";
               }
            }
         }

            List<LicenseProjectData> lda = new List<LicenseProjectData>();
            foreach(LicenseProjectData lpd in dsLicenseProjectData.Data)
            {
                lda.Add(lpd);
            }

            lda.Sort((x, y) => { return x.end.CompareTo(y.end); });

            String ct = "";
         foreach (LicenseProjectData lpd in lda) 
         {
            if( dsLicenseTypes.ContainsKey(lpd.type))
            {
               LicenseType lt = dsLicenseTypes[lpd.type];
                    String curTitle = "";
                    if (lt.title != ct)
                    {
                        curTitle = lt.title + " ";
                        ct = lt.title;
                    }
               if ((isAgents && lt.forAgents != 0) || (!isAgents && lt.forAgents == 0))
               {
                  text += "; " + curTitle + lpd.end.ToString("dd.MM.yy") + " - " + lpd.count.ToString();
               }
            }
         }
         licenseStatusText.Text = text + ".";
      }

      protected virtual void AddUpdDataSet(List<IDataSet> upd) { }

#region UIHandlers
      public void userUpdate_Click(object sender, EventArgs e)
      {
         userUpdate.Enabled = false;
         IsDirty = false;
         dsServerConfig.Filter = "not \"userid\" is null or \"userid\" is null";
         dsCommonConfig.Filter = "\"userid\" is null or \"userid\" = ''";

         DBConnection conn = config.GetConnection();
         DataModule.OnDataResponceError += new EventDataResponseError(UpdateUserError);
         DataModule.DataProcessed += new EventHandler(DataLoaded);

         List<IDataSet> upd = new List<IDataSet>(new IDataSet[] { dsServerConfig, dsAgents, dsLicenseCount, dsLicenseCountEx, dsLisensedUsers,
            dsUserActivity, dsManagers, dsDivision, dsCommonConfig, dsMgrConfig});

         upd.Add(dsLicenseProjectData);
         upd.Add(dsLicenseTypes);
         upd.Add(dsLicensingUsersData);

         rmvScheduler1.UpdateData(upd);
         AddUpdDataSet(upd);

         conn.ReceiveTimeout = 3 * 60 * 1000;
         DataModule.RefreshGiveSets(conn, upd, null);
      }

      public void UpdateUserError(EDataResponse e)
      {
         DataModule.ClearEvents();
         BeginInvoke(new EnableControlHandler(EnableControl), new object[] { new object[] {userUpdate}, true });
         MessageBox.Show(e.Msg, "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Stop);
      }

      protected virtual bool HaveLogin(string login)
      {
         return dsAgents.ContainsKey(login) || dsManagers.ContainsKey(login);
      }

      void ClearUnusedLoginLicense()
      {
         List<string> rmv = new List<string>();

         foreach (LicensingUsersData lud in dsLicensingUsersData.Data)
            if (!HaveLogin(lud.login) )
               rmv.Add(lud.login);

         rmv.ForEach(x => { dsLicensingUsersData.Remove(x); });
      }

      protected virtual void UpdateLoadedData() { }

      private void DataLoaded(object sender, EventArgs e)
      {
         DataModule.ClearEvents();

         ClearUnusedLoginLicense();
         UpdateLoadedData();

         BeginInvoke(new EnableControlHandler(EnableControl), new object[] { 
            new object[] { userUpdate, 
#if EDIT_USER
               btnAdd, btnEdit, btnDel 
#endif
            }, true });

         BeginInvoke(new EmptyParamHandler(CreateUserData));
      }

      //bool HasLicense(string id, string type)
      //{
      //   foreach (LicensedUser lu in dsLisensedUsers.Data)
      //   {
      //      if (lu.id == id && lu.type == type)
      //         return true;
      //   }

      //   return false;
      //}

      LicensedUsers License(string id)
      {
         LicensedUsers value = LicensedUsers.NONE;
         if (dsLisensedUsers.ContainsKey(id))
         {
            value = LicensedUsers.Find(dsLisensedUsers[id].type);
         }
         if (dsLicensingUsersData.ContainsKey(id))
         {
            LicensingUsersData lud = dsLicensingUsersData[id];
            if( dsLicenseProjectData.ContainsKey(lud.licenseID) )
            {
               LicenseProjectData lpd = dsLicenseProjectData[lud.licenseID];

               if (dsLicenseTypes.ContainsKey(lpd.type)) 
               {
                  LicensedUsers lu = new LicensedUsers(MakeLicenseTitle(lpd, dsLicenseTypes[lpd.type]), lpd.type);
                  lu.licenseID = lpd.id;
                  value = lu;

                  registred.Items.Add(lu);
               }
            }

         }

         return value;
      }

      private void AddAgent(Agent a)
      {
         UserActivity userActivity = null;
         if (dsUserActivity.ContainsKey(a.id))
            userActivity = dsUserActivity[a.id];

         Type stopType = FormEntries.GetObjectType(typeof(UserDataItem));
         ConstructorInfo ci = stopType.GetConstructor(new Type[] { typeof(UserData)});
         UserDataItem udi = (UserDataItem)ci.Invoke(new object[] { userData });

         udi.Set(a, userActivity, License(a.id), GetTrackingCode(a.id));

         userData.Add(udi);
      }

      protected UserDataItem AddManager(DivisionManager m)
      {
         UserActivity userActivity = null;
         if (dsUserActivity.ContainsKey(m.login))
            userActivity = dsUserActivity[m.login];

         Type stopType = FormEntries.GetObjectType(typeof(UserDataItem));
         ConstructorInfo ci = stopType.GetConstructor(new Type[] { typeof(UserData) });
         UserDataItem udi = (UserDataItem)ci.Invoke(new object[] { userData });

         udi.Set(m, dsDivision, userActivity, License(m.login), "none");
         return udi;
      }

      protected virtual void PrepareViewComponents(bool agentView)
      {
         clmnCheckPwd.Visible = !agentView;
         clmnId.Visible = agentView;

#if EDIT_USER
            btnAdd.Visible = agentView;
            btnEdit.Visible = agentView;
            btnDel.Visible = agentView;
#elif EDIT_USER_NAME
         btnEdit.Visible = agentView;
#endif

         foreach(DataGridViewCheckBoxColumn clmn in managerRightColumns)
         {
            clmn.Visible = !agentView;
         }

#if CHECK_LOGIN_PROGID
            ProgID.Visible = agentView;
#endif
      }

      protected virtual void RefreshUserData()
      {
         bool isAgentView = cbUserType.SelectedIndex == 0;
         usersView.SuspendLayout();
         userData.Clear();
         userData.dsMgrConfig = dsMgrConfig;

         PrepareViewComponents(cbUserType.SelectedIndex == 0);
         registred.Items.Clear();
         registred.Items.AddRange(MakeLicenseItems(cbUserType.SelectedIndex == 0, null));

         tracking.Visible = isAgentView;

         if (isAgentView)
         {
            if (registred.DisplayMember.Length == 0)
            {
               registred.DisplayMember = "Title";
               registred.ValueMember = "Value";
            }
            usersView.Columns[user.DisplayIndex].HeaderText = "Пользователь";
            usersView.Columns[tracking.DisplayIndex].ReadOnly = false;

            foreach (Agent a in dsAgents.Data)
               AddAgent(a);
         }
         else
         {
            usersView.Columns[user.DisplayIndex].HeaderText = "Подразделение";
            usersView.Columns[tracking.DisplayIndex].ReadOnly = true;

            foreach (DivisionManager m in dsManagers.Data)
            {
               UserDataItem udi = AddManager(m);
               userData.Add(udi);
            }
         }

         tsFind.Text = "";
         usersView.AutoGenerateColumns = false;
         makeDataSource(userData);

         sortUsersView(usersView.Columns[0]);
         usersView.ResumeLayout();
      }

      List<DivisionManager> GetManagers(int division)
      {
         List<DivisionManager> res = new List<DivisionManager>();
         foreach(DivisionManager dm in dsManagers.Data)
         {
            if (dm.division == division)
               res.Add(dm);
         }
         return res;
      }

      private void CreateUserData()
      {
         //CheckManagers();
         if (dsLicenseCountEx.Count > 0)
         {
            if (dsLicenseCountEx.ContainsKey(LicensedUsers.PDA.Type))
               pdaLicenseCount = dsLicenseCountEx[LicensedUsers.PDA.Type].count;
            if (dsLicenseCountEx.ContainsKey(LicensedUsers.MANAGER.Type))
               mgrLicenseCount = dsLicenseCountEx[LicensedUsers.MANAGER.Type].count;
            if (dsLicenseCountEx.ContainsKey(LicensedUsers.EXCLUSIVE_MANAGER.Type))
               exMgrLicenseCount = dsLicenseCountEx[LicensedUsers.EXCLUSIVE_MANAGER.Type].count;
            if (dsLicenseCountEx.ContainsKey(LicensedUsers.VAN.Type))
               pdaVanLicenseCount = dsLicenseCountEx[LicensedUsers.VAN.Type].count;
            if (dsLicenseCountEx.ContainsKey(LicensedUsers.ADS.Type))
               adsLicenseCount = dsLicenseCountEx[LicensedUsers.ADS.Type].count;
            if (dsLicenseCountEx.ContainsKey(LicensedUsers.BTL.Type))
               btlLicenseCount = dsLicenseCountEx[LicensedUsers.BTL.Type].count;
            if (dsLicenseCountEx.ContainsKey(LicensedUsers.EXPEDITOR_PDA.Type))
               expdtrLicenseCount = dsLicenseCountEx[LicensedUsers.EXPEDITOR_PDA.Type].count;
#if FastFood || SKLAD_W_PDA
            if (dsLicenseCountEx.ContainsKey(FF_PDA.Type))
               ffPdaLicenseCount = dsLicenseCountEx[FF_PDA.Type].count;
#endif
#if AviaKos
            if (dsLicenseCountEx.ContainsKey(EXPD_PDA.Type))
               expdPdaLicenseCount = dsLicenseCountEx[EXPD_PDA.Type].count;
#endif
#if VEND_LICENSE
            if (dsLicenseCountEx.ContainsKey(VEND_LICENSE.Type))
               vandLicenseCount = dsLicenseCountEx[VEND_LICENSE.Type].count;
#endif
         }
         else
         {
            LicenseCount lc = dsLicenseCount[0];
            pdaLicenseCount = lc.pda;
            if( pdaLicenseCount > 0 )
            {
               LicenseCountEx le = new LicenseCountEx();
               le.type = LicensedUsers.PDA.Type;
               le.count = lc.pda;
               dsLicenseCountEx[le.type] = le;
            }
            mgrLicenseCount = lc.manager;
            if (mgrLicenseCount > 0)
            {
               LicenseCountEx le = new LicenseCountEx();
               le.type = LicensedUsers.MANAGER.Type;
               le.count = lc.pda;
               dsLicenseCountEx[le.type] = le;
            }
            exMgrLicenseCount = lc.exclusiveManager;
            if (exMgrLicenseCount > 0)
            {
               LicenseCountEx le = new LicenseCountEx();
               le.type = LicensedUsers.EXCLUSIVE_MANAGER.Type;
               le.count = lc.pda;
               dsLicenseCountEx[le.type] = le;
            }
         }

         rmvScheduler1.OnDataLoaded();
         RefreshUserData();

         SetStatusText();
         SetGPSSetting();
         SetServerConfig();
         userChangesSave.Enabled = false;
      }

      private void SetGPSSetting()
      {
         ServerConfig cfg = null;

         foreach(ServerConfig s in dsCommonConfig.Data)
         {
            if (s.key.Equals(GPSPERIOD))
            {
               cfg = s;
               break;
            }
         }

         if (cfg != null && cfg.value.Trim().Length > 0)
         {
            String[] set = cfg.value.Split(';');

            cbD1.Checked = set[0].Contains("1");
            cbD2.Checked = set[0].Contains("2");
            cbD3.Checked = set[0].Contains("3");
            cbD4.Checked = set[0].Contains("4");
            cbD5.Checked = set[0].Contains("5");
            cbD6.Checked = set[0].Contains("6");
            cbD7.Checked = set[0].Contains("7");

            dtpBegin.Value = DateTime.Parse(set[1]);
            dtpEnd.Value = DateTime.Parse(set[2]);
         }
      }

      protected void makeDataSource(UserData ud)
      {
         BindingSource bs = new BindingSource();
         ////bs.DataSource = userData;

         Type t = FormEntries.GetObjectType(typeof(UserDataItem));
         ////IList param = (IList)typeof(List<>).MakeGenericType(t).GetConstructor(Type.EmptyTypes).Invoke((object[])null);
         ////foreach(object o in userData)
         ////   param.Add(o);

         object param;
         if (t != typeof(UserDataItem))
            param = ud.ConvertAll(c => Convert.ChangeType(c, t));
         else
            param = ud;
         bs.DataSource = param;
         usersView.DataSource = bs;

         //Type tl = typeof(BindingList<>);
         //Type ctr = tl.MakeGenericType(new Type[]{t});

         //Type listType = typeof(IList<>).MakeGenericType(t);
         //ConstructorInfo ci = ctr.GetConstructor(new Type[]{listType});
         //usersView.DataSource = ci.Invoke(new object[] { userData });

         ////usersView.DataSource = new SortableBindingList<TabAlignment> bs;
      }

      private void browseFile_Click(object sender, EventArgs e)
      {
         OpenFileDialog ofd = new OpenFileDialog();
         ofd.Filter = "All files (*.*)|*.*";
         ofd.FilterIndex = 1;
         ofd.Title = "Select file for upload";
         if (ofd.ShowDialog() == DialogResult.OK)
         {
            uploadFileName.Text = ofd.FileName;
         }
      }

      private void sendUpdate_Click(object sender, EventArgs e)
      {
         string fileName = uploadFileName.Text;
         if (File.Exists(fileName))
         {
            DBConnection conn = config.GetConnection();

            PacketObject po = new PacketObject(false);
            UpdateCommand updPacket = new UpdateCommand(config.login, config.password, File.ReadAllBytes(fileName));
            po.Add(updPacket);
            sendUpdate.Enabled = false;

            Application.UseWaitCursor = true;

            Thread t = conn.SendCommand(new SendParam(po, CheckUpdate, null));
            t.Join();
            sendUpdate.Enabled = true;

            Application.UseWaitCursor = false;
         }
      }

      void CheckUpdate(PacketObject result, List<IDataSet> sets)
      {
         bool loadedUpdate = false;
         string message = "";

         GRSoft.Network.Object ans = null;
         if (result != null)
         {
            if (result.Count == 1)
            {
               if (result[0].Count >= 1)
               {
                  if (result[0].Name == "ServerAnswer")
                  {
                     int idx = result[0].Count == 1 ? 0 : 1;
                     ans = result[0][idx];
                  }
               }
            }
            else if (result.Capacity > 1)
            {
               if (result[1].Name == "ServerAnswer")
                  ans = result[1][0];
            }
         }

         if( ans != null )
         {
            Member m = ans.GetMember("response");
            if (m != null)
               loadedUpdate = (((double)m.Value) != 0);

            if (!loadedUpdate)
            {
               m = ans.GetMember("message");
               message = (m==null) ? "Неправильный формат ответа сервера" : m.Value as string;
            }
         } else
            message = "Ошибка при загрузке обновления на сервер";
         if (!loadedUpdate)
            MessageBox.Show(message, "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Stop);
         else
            MessageBox.Show("Обновление загружено на сервер", "Иформация", MessageBoxButtons.OK, MessageBoxIcon.Information);
      }

      private bool CheckDirty(DataGridViewColumn column)
      {
         bool dirty = false;

         if (column.Name == "registred")
         {
            int index = column.DisplayIndex;
            int loginIndex = login.DisplayIndex;
            foreach (DataGridViewRow row in usersView.Rows)
            {
               object v = row.Cells[index].Value;
               GRSoft.Network.Object obj = (GRSoft.Network.Object)row.Tag;
               string lv = (string)obj["id"].Value;
               if (v == null || (bool)v == false)
               {
                  if (licensedUsers.Contains(lv))
                  {
                     dirty = true;
                     break;
                  }
               }
               else
               {
                  if (licensedUsers.Contains(lv) == false)
                  {
                     dirty = true;
                     break;
                  }
               }
            }
         }
         else
         {
            if (loadedAgents != null)
            {
               int aLogin = loadedAgents.FindField("login");
               int aPwd = loadedAgents.FindField("password");

               int uLogin = login.DisplayIndex;
               int uPwd = password.DisplayIndex;

               for (int i = usersView.Rows.Count - 1; i >= 0; i--)
               {
                  DataGridViewRow row = usersView.Rows[i];
                  GRSoft.Network.Object o = row.Tag as GRSoft.Network.Object;

                  string alv = o[aLogin].Value as string;
                  string apv = o[aPwd].Value as string;
                  string ulv = row.Cells[uLogin].Value as string;
                  string upv = row.Cells[uPwd].Value as string;

                  if (alv == null) alv = "";
                  if (ulv == null) ulv = "";
                  if (apv == null) apv = "";
                  if (upv == null) upv = "";

                  if (alv != ulv || apv != upv )
                  {
                     dirty = true;
                     break;
                  }
               }
            }
         }

         return dirty;
      }

      private void MainForm_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (IsDirty)
         {
            DialogResult res = MessageBox.Show("Данные изменились. Сохранить изменения", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
            if (res == DialogResult.Yes)
            {
               SaveChanges();
            }
            else if (res == DialogResult.Cancel)
            {
               e.Cancel = true;
            }
         }
      }

      //private void usersView_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      //{
      //   DataGridViewCell cell = usersView.CurrentCell;
      //   if (usersView.Columns[cell.ColumnIndex].Name == "registred" && usersView.Rows[cell.RowIndex].IsNewRow == false)
      //   {
      //      bool dirty = false;
      //      GRSoft.Network.Object obj = (GRSoft.Network.Object)usersView.Rows[cell.RowIndex].Tag;
      //      string lv = (string)obj["id"].Value;

      //      dirty = ((bool)cell.EditedFormattedValue && !licensedUsers.Contains(lv)) || 
      //         (!(bool)cell.EditedFormattedValue && licensedUsers.Contains(lv));

      //      IsDirty = dirty;
      //   }
      //}

#endregion

      void SaveUsersAnswer(PacketObject result)
      {
         IsDirty = false;
      }

      public virtual void BeforeUpdate(List<IDataSet> wr, List<IDataSet> rmv)
      {
      }

      public virtual void OnUpdate(Boolean res)
      {

      }

      protected virtual bool SaveChanges()
      {
         return userData.CommitEdit(config, (cbUserType.SelectedIndex == 0), dsLicenseTypes, dsLicensingUsersData, this);
      }

      private void userChangesSave_Click(object sender, EventArgs e)
      {
         usersView.CommitEdit(DataGridViewDataErrorContexts.Commit);

         bool r = SaveChanges();
         userChangesSave.Enabled = !r;
      }

      protected virtual void usersView_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         const string LICENCE_COLUMN_TEXT = "Лицензия";
         const string TRACKING_COLUMN_TEXT = "Слежение";

         DataGridViewColumn cur = usersView.Columns[usersView.CurrentCell.ColumnIndex];
         if (cur.HeaderText == LICENCE_COLUMN_TEXT ||
            cur.HeaderText == TRACKING_COLUMN_TEXT ||
            cur == clmnCheckPwd
            )
         {
            usersView.CommitEdit(DataGridViewDataErrorContexts.Commit);
         }


         int clmnIdx = usersView.CurrentCell.ColumnIndex;
         foreach(DataGridViewCheckBoxColumn clm in managerRightColumns)
            if(clm.Visible && clm.Index == clmnIdx)
            {
               usersView.CommitEdit(DataGridViewDataErrorContexts.Commit);
               break;
            }
      }

      /// <summary>
      /// Сохранить настройки с экрана в s
      /// </summary>
      /// <param name="s"></param>
      private void SetSettings(Config s)
      {
         s.ip = this.ip.Text;
         s.port = int.Parse(this.port.Text);
         s.login = log.Text;
         s.rememberPassword = savePwd.Checked;
         s.password = pwd.Text;
         s.name = tbName.Text.Trim();
      }

      private void lbHistory_KeyDown(object sender, KeyEventArgs e)
      {
         if (e.KeyCode == Keys.Delete)
            RemoveSetting();
      }

      private void RemoveSetting()
      {
         if (lbHistory.SelectedIndex != -1 && MessageBox.Show("Настройка будет удалена, удалить?",
            "Внимание", MessageBoxButtons.OKCancel,
            MessageBoxIcon.Warning) == DialogResult.OK)
         {
            Config cfgToDel = (Config)lbHistory.Items[lbHistory.SelectedIndex];

            if (cfgToDel != null)
            {
               ConfigHistory history = ConfigHistory.Instance(false);
               foreach (Config c in history.config)
                  if (c.name.Equals(cfgToDel.name))
                  {
                     history.config.Remove(c);
                     history.Save();
                     break;
                  }

               lbHistory.Items.RemoveAt(lbHistory.SelectedIndex);

               if (lbHistory.Items.Count > 0)
               {
                  lbHistory.SelectedIndex = 0;
                  config = (Config)lbHistory.Items[lbHistory.SelectedIndex];
               }
            }
         }
      }

      private void lbHistory_MouseDown(object sender, MouseEventArgs e)
      {
         if (e.Button == MouseButtons.Right || e.Button == MouseButtons.Left)
         {
            config.Save();
            lbHistory.SelectedIndex = lbHistory.IndexFromPoint(e.X, e.Y);

            if (lbHistory.SelectedIndex != -1)
            {
               config = (Config)lbHistory.Items[lbHistory.SelectedIndex];
               LoadSettings(config);
            }
         }
      }

      private void miDel_Click(object sender, EventArgs e)
      {
         RemoveSetting();
      }

      /// <summary>
      /// Загрузить настройки из s на экран
      /// </summary>
      /// <param name="s"></param>
      private void LoadSettings(Config s)
      {
         ip.Text = s.ip;
         port.Text = s.port.ToString();
         log.Text = s.login;
         pwd.Text = s.password;
         savePwd.Checked = s.rememberPassword;

#if CONFIG_HISTORY_ADM
         LoadCfgHistory();
#endif
      }

      private void SetServerConfig()
      {
         foreach (ServerConfig s in dsServerConfig.Values)
            if (s.key.Equals(PREZENTPATH))
            {
               tbPresentFolder.TextChanged -= tbPresentFolder_TextChanged;
               tbPresentFolder.Text = s.value;
               tbPresentFolder.TextChanged += tbPresentFolder_TextChanged;
               break;
            }
      }

      private void LoadCfgHistory()
      {
         List<Config> history = new List<Config>();
         ConfigHistory cfgHistory = ConfigHistory.Instance(true);
         history.AddRange(cfgHistory.config);
         history.Sort(new Comparison<Config>(delegate(Config c1, Config c2) { return c1.name.CompareTo(c2.name); }));
         lbHistory.Items.Clear();
         lbHistory.Items.AddRange(history.ToArray());

         for (int i = 0; i < lbHistory.Items.Count; i++)
         {
            if (((Config)lbHistory.Items[i]).name.Equals(config.name))
            {
               lbHistory.SelectedIndex = i;
               break;
            }
         }

         tbName.Text = config.name;
      }

      private void tabControl1_Selecting(object sender, TabControlCancelEventArgs e)
      {
         if (e.TabPage == settings)
         {
            LoadSettings(config);
            needCheck = true;
         }
         else
         {
            if (needCheck)
            {
               needCheck = false;

               Config s = new Config();
               SetSettings(s);
               if (s.IsEqual(config) == false)
               {
                  DialogResult dr = MessageBox.Show("Сохранить изменения?", "Вопрос",
                     MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
                  if (dr == DialogResult.Cancel)
                     e.Cancel = true;
                  else
                  {
                     config = s;
                     if (dr == DialogResult.Yes)
                        config.Save();
                  }
               }
            }
         }
      }

      private void save_Click(object sender, EventArgs e)
      {
         SetSettings(config);
         config.Save();
#if CONFIG_HISTORY_ADM
         LoadCfgHistory();
#endif
      }

      

      bool testPassed = false;
      private void test_Click(object sender, EventArgs e)
      {
         testPassed = false;

         Config c = new Config();
         SetSettings(c);

         DBConnection conn = c.GetConnection();
         DataModule.OnDataResponceError += new EventDataResponseError(CheckError);
         DataModule.DataProcessed += new EventHandler(CheckData);

         this.UseWaitCursor = true;
         //Application.UseWaitCursor = true;
         test.Enabled = false;

         try
         {
            Thread t = DataModule.RefreshGiveSets(conn, new object[] { dsAgents }, null);
            t.Join();
         }
         catch (Exception ex)
         {
            MessageBox.Show(ex.Message, "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Stop);
         }

         this.UseWaitCursor = false;

         test.Enabled = true;
         if (testPassed)
         {
            config = c;
            DialogResult dr = MessageBox.Show("Соединение с сервером успешно установлено!\nСохранить изменения?", "Информация", 
               MessageBoxButtons.YesNo, MessageBoxIcon.Question);

            if (dr == DialogResult.Yes)
               config.Save();
         }
      }

      void CheckError(EDataResponse e)
      {
         DataModule.OnDataResponceError -= new EventDataResponseError(CheckError);
         MessageBox.Show(e.Msg, "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Stop);
      }

      void CheckData(object sender, EventArgs e)
      {
         DataModule.DataProcessed -= new EventHandler(CheckData);
         testPassed = true;
      }

      private void update_DragEnter(object sender, DragEventArgs e)
      {
         if (e.Data.GetDataPresent(DataFormats.FileDrop, false))
            e.Effect = DragDropEffects.Copy;
      }

      private void update_DragDrop(object sender, DragEventArgs e)
      {
         if (e.Data.GetDataPresent(DataFormats.FileDrop, false))
         {
            string[] files = (string[])e.Data.GetData(DataFormats.FileDrop);

            // loop through the string array, adding each filename to the ListBox
            foreach (string file in files)
            {
               uploadFileName.Text = file;
               break;
            }
         }
      }

      //Сортировка по щелчку на заголовке таблицы
      private void usersView_ColumnHeaderMouseClick(object sender, DataGridViewCellMouseEventArgs e)
      {
         sortUsersView(usersView.Columns[e.ColumnIndex]);
      }

      private SortOrder sortUsersView(DataGridViewColumn column)
      {
         usersView.CurrentCell = null;

         SortOrder curOrder = column.HeaderCell.SortGlyphDirection;

         foreach (DataGridViewColumn c in usersView.Columns)
         {
            c.HeaderCell.SortGlyphDirection = SortOrder.None;
         }

         switch (curOrder)
         {
            case SortOrder.Ascending: curOrder = SortOrder.Descending; break;
            case SortOrder.Descending:
            case SortOrder.None: curOrder = SortOrder.Ascending; break;
         }

         userData.DoSort(column.DataPropertyName, curOrder);
         makeDataSource(userData);
         //usersView.Refresh();

         column.HeaderCell.SortGlyphDirection = curOrder;

         return curOrder;
      }

      public string GetTrackingCode(string userid)
      {
         foreach (ServerConfig serverConfig in dsServerConfig.Data)
            if (serverConfig.userid.Equals(userid) &&
                  serverConfig.key.Equals("Tracking"))
               return serverConfig.value;

         return "none";
      }

      protected void cbUserType_SelectedIndexChanged(object sender, EventArgs e)
      {
         if (userChangesSave.Enabled)
         {
            DialogResult res = MessageBox.Show("Сохранить изменения?", "Данные изменились", MessageBoxButtons.YesNo, MessageBoxIcon.Question);
            if (res == DialogResult.Yes)
               userChangesSave_Click(this, new EventArgs());
         }
         RefreshUserData();
         SetStatusText();
      }

      private void btnSaveGPSSetting_Click(object sender, EventArgs e)
      {
         ServerConfig srvCfg = null;
         foreach (ServerConfig s in dsCommonConfig.Data)
         {
            if (s.key.Equals(GPSPERIOD))
            {
               srvCfg = s;
               break;
            }
         }

         if (srvCfg == null)
         {
            srvCfg = new ServerConfig();
            srvCfg.key = GPSPERIOD;
            dsCommonConfig.Add(dsCommonConfig.Count, srvCfg);
         }

         srvCfg.value = PeriodStr();

         DBConnection conn = config.GetConnection();
         List<IDataSet> wrObj = new List<IDataSet>();
         wrObj.Add(dsCommonConfig);
         DataModule.UpdateDataSet(wrObj, null, null, conn);
      }

      private string PeriodStr()
      {
         StringBuilder sb = new StringBuilder();

         if (cbD1.Checked)
            sb.Append("1");
         if (cbD2.Checked)
            sb.Append("2");
         if (cbD3.Checked)
            sb.Append("3");
         if (cbD4.Checked)
            sb.Append("4");
         if (cbD5.Checked)
            sb.Append("5");
         if (cbD6.Checked)
            sb.Append("6");
         if (cbD7.Checked)
            sb.Append("7");

         if(sb.Length > 0)
         {
            sb.Append(";");
            sb.Append(dtpBegin.Value.TimeOfDay.ToString());
            sb.Append(";");
            sb.Append(dtpEnd.Value.TimeOfDay.ToString());
         }
         return sb.ToString();
      }

      private void btnUnsetGpsSetting_Click(object sender, EventArgs e)
      {
         cbD1.Checked = false;
         cbD2.Checked = false;
         cbD3.Checked = false;
         cbD4.Checked = false;
         cbD5.Checked = false;
         cbD6.Checked = false;
         cbD7.Checked = false;
      }

      private void btnSPGSetAllDays_Click(object sender, EventArgs e)
      {
         cbD1.Checked = true;
         cbD2.Checked = true;
         cbD3.Checked = true;
         cbD4.Checked = true;
         cbD5.Checked = true;
         cbD6.Checked = true;
         cbD7.Checked = true;
      }

      protected virtual void btnAdd_Click(object sender, EventArgs e)
      {
#if EDIT_USER
         int maxId = 0;

         foreach (UserDataItem udi in userData)
         {
            int id = 0;
            if (Int32.TryParse(udi.Id, out id) && id > maxId)
               maxId = id;
         }

         AgentEdit.AgentEditInfo input = new AgentEdit.AgentEditInfo();
         input.id = (++maxId).ToString();

         AgentEdit.AgentEditInfo info = AgentEdit.EditAgent(this, input);

         if (info != null)
         {
            AddAgent(info.CreateAgent());
            makeDataSource(userData);
            userChangesSave.Enabled = true;
         }
#endif
      }

      protected virtual void btnEdit_Click(object sender, EventArgs e)
      {
#if EDIT_USER || EDIT_USER_NAME
         DataGridViewRow row = usersView.CurrentRow;

         if (row != null)
         {
            UserDataItem udi = (UserDataItem)row.DataBoundItem;
            AgentEdit.AgentEditInfo info = AgentEdit.EditAgent(this, new AgentEdit.AgentEditInfo(udi));

            if (info != null)
            {
#if EDIT_USER
               udi.Id = info.id;
#endif
               udi.Name = info.name;
               userChangesSave.Enabled = true;
               usersView.Invalidate();
            }
         }
         else
            MessageBox.Show("Для редактирования пользователя выберите его!");
#endif
      }

      protected virtual void btnDel_Click(object sender, EventArgs e)
      {
         DataGridViewRow row = usersView.CurrentRow;

         if (row != null && MessageBox.Show("Пользователь будет удален! Удалить?", "Вопрос", 
            MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
         {
            UserDataItem udi = (UserDataItem)row.DataBoundItem;
            udi.markToDel = true;
            userChangesSave.Enabled = true;
            usersView.Invalidate();
         }
      }

      protected virtual void DrawCell(UserDataItem udi, DataGridViewCellFormattingEventArgs e)
      {
         if (udi.markToDel)
            e.CellStyle.BackColor = Color.Gray;
      }

      private void usersView_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         DataGridViewRow r = ((DataGridView)sender).Rows[e.RowIndex];
         if (r != null)
         {
            UserDataItem udi = r.DataBoundItem as UserDataItem;

            if (udi != null)
               DrawCell(udi, e);
         }
      }

      private void btnSelectPresentFolder_Click(object sender, EventArgs e)
      {
         if (folderBrowserDialog1.ShowDialog() == DialogResult.OK)
         {
            tbPresentFolder.Text = folderBrowserDialog1.SelectedPath;

           
            ServerConfig srvCfg = null;
            foreach (ServerConfig s in dsServerConfig.Data)
            {
               if (s.key.Equals(PREZENTPATH))
               {
                  srvCfg = s;
                  break;
               }
            }

            if (srvCfg == null)
            {
               srvCfg = new ServerConfig();
               srvCfg.key = PREZENTPATH;
               dsServerConfig.Add(dsServerConfig.Count, srvCfg);
            }

            srvCfg.value = tbPresentFolder.Text.Trim();
         }
      }

      private void tbPresentFolder_TextChanged(object sender, EventArgs e)
      {
         userChangesSave.Enabled = true;         
      }

      LicensedUsers[] staticLic = new LicensedUsers[] {
#if AviaKos
                     EXPD_PDA,
#endif
                     LicensedUsers.PDA,
#if FastFood || SKLAD_W_PDA
                     FF_PDA,
#endif
                     LicensedUsers.EXCLUSIVE_MANAGER,
                     LicensedUsers.MONITOR_MANAGER,
                     LicensedUsers.VAN,
                     LicensedUsers.ADS,
                     LicensedUsers.BTL,
                     LicensedUsers.EXPEDITOR_PDA,
#if VEND_LICENSE
                     LicensedUsers.VEND,
#endif
                     dispLic,
         };

      LicensedUsers FindLicense(string type)
      {
         foreach(LicensedUsers lu in staticLic)
            if( lu.Type == type)
               return lu;
      
         return LicensedUsers.NONE;
      }

      object[] MakeLicenseItems(bool agentsLicense, UserDataItem data)
      {
         List<LicensedUsers> ret = new List<LicensedUsers>();
         ret.Add(LicensedUsers.NONE);

         if (agentsLicense)
         {
            foreach(LicenseCountEx lce in dsLicenseCountEx.Data )
            {
               if (IsManagerLicType(lce.type))
                  continue;
               LicensedUsers lu = FindLicense(lce.type);
               if( lu != LicensedUsers.NONE)
               {
                  if (data == null)
                     ret.Add(lu);
                  else
                  {
                     if (data.License.Type == lce.type)
                        ret.Add(data.License);
                     else if (CountLicense(lce.type) < lce.count)
                        ret.Add(lu);
                  }
               }
            }
         }
         else
         {
            foreach (LicenseCountEx lce in dsLicenseCountEx.Data)
            {
               if (!IsManagerLicType(lce.type))
                  continue;
               LicensedUsers lu = FindLicense(lce.type);
               if (lu != LicensedUsers.NONE)
               {
                  if (data == null)
                     ret.Add(lu);
                  else
                  {
                     if (data.License.Type == lce.type)
                        ret.Add(data.License);
                     else if (CountLicense(lce.type) < lce.count)
                        ret.Add(lu);
                  }
               }
            }
         }

         foreach (LicenseProjectData lpd in dsLicenseProjectData.Data)
         {
            if (dsLicenseTypes.ContainsKey(lpd.type) == false)
               continue;
            LicenseType lt = dsLicenseTypes[lpd.type];
            if (agentsLicense ? lt.forAgents == 0 : lt.forAgents != 0)
               continue;

            LicensedUsers lu = new LicensedUsers(MakeLicenseTitle(lpd, lt), lpd.type);
            lu.licenseID = lpd.id;

            if (data == null)
            {
               ret.Add(lu);
            } else
            {
               if(data.License.Type == lpd.type && data.License.Title == MakeLicenseTitle(lpd, lt))
                  ret.Add(data.License);
               else if (HaveAvail(lpd))
                  ret.Add(lu);
            }
         }
         if (data != null && ret.Contains(data.License) == false)
            ret.Add(data.License);

         return ret.ToArray();
      }


      private string MakeLicenseTitle(LicenseProjectData lpd, LicenseType lt)
      {
         return String.Format("{0} с {1:dd.MM.yyyy} по {2:dd.MM.yyyy}", lt.title, lpd.start, lpd.end);
        }

      bool HaveAvail(LicenseProjectData lpd)
      {
         int ret = 0;
         string title = MakeLicenseTitle(lpd, dsLicenseTypes[lpd.type]);
         foreach (UserDataItem udi in userData)
         {
            if (udi.License.Type == lpd.type && udi.License.Title == title)
               ret++;
         }
         return ret < lpd.count;
      }

      private int CountLicense(string type)
      {
         int count = 0;
         foreach(UserDataItem udi in userData)
         {
            if (udi.License.Type == type)
               count++;
         }
         return count;
      }

      protected virtual void usersView_CellEnter(object sender, DataGridViewCellEventArgs e)
      {
         if(e.ColumnIndex == registred.Index)
         {
            UserDataItem di = usersView.Rows[e.RowIndex].DataBoundItem as UserDataItem;
            DataGridViewComboBoxCell cell = usersView.Rows[e.RowIndex].Cells[e.ColumnIndex] as DataGridViewComboBoxCell;
            cell.Items.Clear();
            cell.Items.AddRange(MakeLicenseItems(cbUserType.SelectedIndex == 0 , di));
         }
      }

      private void MainForm_KeyDown(object sender, KeyEventArgs e)
      {
         if (e.KeyCode == Keys.F5)
         {
            if (tabControl1.SelectedTab == users)
               userUpdate_Click(this, EventArgs.Empty);
            else if (tabControl1.SelectedTab == userActivity)
               toolStripButton1_Click(this, EventArgs.Empty);
         } else if (e.Modifiers == Keys.Control && e.KeyData == Keys.A && usersView.Visible)
         {
            usersView.SelectAll();
         }
      }

      private void usersView_DataError(object sender, DataGridViewDataErrorEventArgs e)
      {
         //e.Cancel = true;
      }

      private void btnRefreshSyncInfo_Click(object sender, EventArgs e)
      {
         btnRefreshSyncInfo.Enabled = false;

         dsSyncInfo.Filter = string.Format("\"created\" >= ToDate('{0}') and \"created\" < ToDate('{1}')", dtpDateSyncInfo.Value.Date, dtpDateSyncInfo.Value.Date.AddDays(1));
         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsAgents);
         list.Add(dsSyncInfo);

         DBConnection conn = config.GetConnection();

         DataModule.OnDataResponceError += new EventDataResponseError(ErrorSyncInfo);
         DataModule.DataProcessed += new EventHandler(UpdateSyncInfo);
         DataModule.RefreshGiveSets(conn, list, null);
      }

      void UpdateSyncInfo(object sender, EventArgs e)
      {
         BeginInvoke(new EmptyParamHandler(UpdateSyncInfoThread));
      }

      void UpdateSyncInfoThread()
      {
         btnRefreshSyncInfo.Enabled = true;

         if (cbAgentSyncInfo.Items.Count == 1)
         {
            List<Agent> agents = new List<Agent>();

            foreach (Agent a in dsAgents.Values)
               agents.Add(a);

            agents.Sort((x, y) => { return x.ToString().CompareTo(y.ToString()); });

            foreach (Agent a in agents)
               cbAgentSyncInfo.Items.Add(a);
         }

         FilterSyncInfoGrid(cbAgentSyncInfo);
      }
      
      void ErrorSyncInfo(EDataResponse e)
      {
         DataModule.OnDataResponceError -= new EventDataResponseError(ErrorSyncInfo);
         BeginInvoke(new EnableControlHandler(EnableControl), new object[] { new object[] { btnRefreshSyncInfo }, true });
         MessageBox.Show(e.Msg, "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Stop);
      }

      private void cbAgentSyncInfo_SelectedIndexChanged(object sender, EventArgs e)
      {
         FilterSyncInfoGrid((ToolStripComboBox)sender);
      }

      private void FilterSyncInfoGrid(ToolStripComboBox control)
      {
         List<SyncInfo> data = new List<SyncInfo>();
         foreach (SyncInfo s in dsSyncInfo.Values)
            if ((control.SelectedIndex == 0) || ((Agent)(control).SelectedItem).id.Equals(s.userid))
               data.Add(s);

         dgvSyncInfo.DataSource = data;
      }

      private void toolStripButton1_Click(object sender, EventArgs e)
      {
         SimpleDataSet<ManagerActivity> activity = DataModule.Get(ManagerActivity.OBJECT_NAME) as SimpleDataSet<ManagerActivity> ??
            new SimpleDataSet<ManagerActivity>(ManagerActivity.OBJECT_NAME);


         DBConnection conn = config.GetConnection();
         DataModule.OnDataResponceError += new EventDataResponseError(UpdateUserError);
         DataModule.DataProcessed += new EventHandler(UpdateManagerActivity);

         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(activity);
         if (dsManagers.Count == 0)
            upd.Add(dsManagers);

         DataModule.RefreshGiveSets(conn, upd, null);
      }

      private void UpdateManagerActivity(object sender, EventArgs e)
      {
         DataModule.ClearEvents();

         BeginInvoke(new EmptyParamHandler(RefreshManagerActivity));
      }

      void RefreshManagerActivity()
      {
         SimpleDataSet<ManagerActivity> activity = DataModule.Get(ManagerActivity.OBJECT_NAME) as SimpleDataSet<ManagerActivity>;

         List<ManagerActivityData> data = new List<ManagerActivityData>();
         foreach(ManagerActivity ma in activity.Data)
         {
            data.Add(new ManagerActivityData(ma, dsManagers));
         }

         data.Sort();
         dgvActivity.DataSource = data;

         userActivityTotals.Text = "Всего активно: " + data.Count.ToString();
      }

      class ManagerActivityData : IComparable<ManagerActivityData>
      {
         string manager;
         int duration;
         bool isExclusive;
         string ip;

         public ManagerActivityData(ManagerActivity data, DataSet<string, DivisionManager> managers)
         {
            duration = data.duration;
            manager = data.userid;
            isExclusive = data.isExclusive != 0;
            ip = data.ip;
         }

         public string Manager { get { return manager; } }
         public string Duration 
         {
            get
            {
               return String.Format("{0} мин {1} сек", duration / 60, duration % 60);
            }
         }

         public bool IsExclusive { get { return isExclusive; } }
         public string IP { get { return ip; } }

         public int CompareTo(ManagerActivityData other)
         {
            return duration - other.duration;
         }
      }

      private void tsFind_TextChanged(object sender, EventArgs e)
      {
         tmrFind.Stop();
         tmrFind.Start();
      }
   }
}
