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
      private DataSet<string, UserActivity> dsUserActivity = new DataSet<string, UserActivity>("UserActivity");
      protected DataSet<string, DivisionManager> dsManagers = new DataSet<string, DivisionManager>("DivisionManager");
      private DataSet<int, ServerConfig> dsServerConfig = new DataSet<int, ServerConfig>("ServerConfig");
      protected DataSet<int, ServerConfig> dsCommonConfig = new DataSet<int, ServerConfig>("ServerConfig");
      protected DataSet<int, Division> dsDivision = new DataSet<int, Division>(Division.OBJECT_NAME);
      DataSet<string, LinkedUsers> dsLinked = new DataSet<string, LinkedUsers>(LinkedUsers.OBJECT_NAME);

      Dictionary<string, ReqConnect> reqConnects = new Dictionary<string, ReqConnect>();
      
      public Config config;
      GRServerInfo serverInfo;


      public MainForm()
      {
         InitializeComponent();

         usersView.AutoGenerateColumns = false;
         cbUsers.SelectedIndex = 0;

         Init();

         //TestUUID();
      }

      //void TestUUID()
      //{
      //   DBConnection conn = new DBConnection("172.24.88.147", 3000);
      //   conn.uuid = "22";

      //   List<IDataSet> upd = new List<IDataSet>();
      //   upd.Add(dsAgents);

      //   DataModule.RefreshGiveSets(conn, upd, null).Join();
      //}

      public string Status(string userid)
      {
         string res = "";
         LinkedUsers lu;
         if (dsLinked.TryGetValue(userid, out lu))
         {
            res = "подключен к серверу";
         }
         else
         {
            ReqConnect rc;
            if (reqConnects.TryGetValue(userid, out rc))
            {
               DateTime dateTime = new DateTime(1970, 1, 1, 0, 0, 0, 0, DateTimeKind.Utc);
               dateTime = dateTime.AddSeconds(rc.till).ToLocalTime();
               res = String.Format("Код {0}, до {1}"
                  , rc.code
                  , dateTime.ToString("dd MMM HH:mm:ss"));
            }
         }
         return res;
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
         serverCode.Text = config.serverCode;
         if(config.serverCode.Length == 0)
            serverCode.Focus();
      }

      void RefreshData()
      {
         if (serverInfo == null || serverInfo.fail || config.serverCode != serverCode.Text)
         {
            config.serverCode = serverCode.Text;
            serverInfo = ConnectionHelper.GetServerInfo(config.serverCode);
            if (serverInfo.fail)
            {
               MessageBox.Show(serverInfo.error);
               Invoke(new Action(AfterUpdate));
               return;
            }
            else
            {
               config.Save();
            }
         }

         var con = config.GetConnection(serverInfo);
         if(con == null)
         {
            return;
         }

         reqConnects.Clear();
         List<ReqConnect>  rcs = Config.ServerReqConnects(config.serverCode);
         foreach(ReqConnect rq in rcs)
         {
            if (reqConnects.ContainsKey(rq.id) && reqConnects[rq.id].till < rq.till)
            {
               reqConnects[rq.id] = rq;
            }
            else
            {
               reqConnects.Add(rq.id, rq);
            }
         }

         DataModule.OnDataResponceError += DataModule_OnDataResponceError;
         DataModule.DataProcessed += DataModule_DataProcessed;

         List<IDataSet> sets = new List<IDataSet>();
         sets.Add(dsAgents);
         sets.Add(dsUserActivity);
         sets.Add(dsManagers);
         sets.Add(dsServerConfig);
         sets.Add(dsCommonConfig);
         sets.Add(dsDivision);
         sets.Add(dsLinked);

         DataModule.RefreshGiveSets(con, sets, null);
      }

      private void DataModule_DataProcessed(object sender, EventArgs e)
      {
         DataModule.OnDataResponceError -= DataModule_OnDataResponceError;
         DataModule.DataProcessed -= DataModule_DataProcessed;

         BeginInvoke(new Action(AfterUpdate));
      }

      void CreateFirstDivision()
      {
         List<IDataSet> upd = new List<IDataSet>();
         if (dsDivision.Count == 0)
         {
            Division d = new Division();
            d.id = 1;
            d.name = "Головное подразделение";
            d.description = "";

            SimpleDataSet<Division> dv = new SimpleDataSet<Division>(Division.OBJECT_NAME, false);
            dv.Add(d);
            upd.Add(dv);

            dsDivision.Add(d.id, d);
         }

         if(dsManagers.Count == 0)
         {
            DivisionManager dm = new DivisionManager();
            dm.id = Guid.NewGuid().ToString().Replace("-", "");
            dm.name = "Главный менеджер";
            foreach(Division d in dsDivision.Data)
            {
               if(d.parent == 0)
               {
                  dm.division = d.id;
                  break;
               }
            }

            SimpleDataSet<DivisionManager> dv = new SimpleDataSet<DivisionManager>(DivisionManager.OBJECT_NAME, false);
            dv.Add(dm);
            upd.Add(dv);
            dsManagers.Add(dm.id, dm);
         }

         if(upd.Count > 0)
         {
            var con = config.GetConnection(serverInfo);
            if (con == null)
            {
               return;
            }
            DataModule.UpdateDataSet(upd, null, null, con);
         }
      }

      private void DataModule_OnDataResponceError(EDataResponse e)
      {
         DataModule.OnDataResponceError -= DataModule_OnDataResponceError;
         DataModule.DataProcessed -= DataModule_DataProcessed;

         MessageBox.Show(e.Msg);
         BeginInvoke(new Action(AfterUpdate));
      }

      private void userUpdate_Click(object sender, EventArgs e)
      {
         userUpdate.Enabled = false;
         RefreshData();
      }

      void AfterUpdate()
      {
         if (dsAgents.Count > 0 && dsManagers.Count == 0)
         {
            CreateFirstDivision();
         }

         userUpdate.Enabled = true;

         RefreshUsers(cbUsers.SelectedIndex == 0);
      }

      void RefreshUsers(bool loadAgents)
      {
         List<DataItem> src = new List<DataItem>();
         if (loadAgents)
         {
            foreach (Agent a in dsAgents.Data)
            {
               DataItem di = new DataItem(a, dsUserActivity, this);
               src.Add(di);
            }
         } else
         {
            foreach(DivisionManager d in dsManagers.Data)
            {
               DataItem di = new DataItem(d, dsUserActivity, this);
               src.Add(di);
            }
         }

         usersView.DataSource = src;

      }

      private void linkUser_Click(object sender, EventArgs e)
      {
         if (usersView.CurrentRow == null)
            return;

         DataItem di = usersView.CurrentRow.DataBoundItem as DataItem;
         LinkUser lu = new LinkUser(di);

         ReqConnect rc = Config.LinkingUser(lu, config.serverCode);
         if (rc != null)
         {
            reqConnects[rc.id] = rc;
            usersView.InvalidateRow(usersView.CurrentRow.Index);
         }
      }

      private void cbUsers_SelectedIndexChanged(object sender, EventArgs e)
      {
         RefreshUsers(cbUsers.SelectedIndex == 0);
      }

      private void bindUser_Click(object sender, EventArgs e)
      {
         linkUser_Click(sender, e);
      }

      private void usersView_MouseDown(object sender, MouseEventArgs e)
      {
         if(e.Button == MouseButtons.Right)
         {
            DataGridView.HitTestInfo hi = usersView.HitTest(e.X, e.Y);
            if (hi.ColumnIndex < 0 || hi.RowIndex < 0)
               return;

            usersView.CurrentCell = usersView[hi.ColumnIndex, hi.RowIndex];
         }
      }

      private void unlinkUser_Click(object sender, EventArgs e)
      {
         if (usersView.CurrentRow == null)
            return;

         DataItem di = usersView.CurrentRow.DataBoundItem as DataItem;
         LinkedUsers lnkdu;
         if(dsLinked.TryGetValue(di.Id, out lnkdu))
         {
            SimpleDataSet<LinkedUsers> rmv = new SimpleDataSet<LinkedUsers>(LinkedUsers.OBJECT_NAME, false);
            List<IDataSet> rs = new List<IDataSet>();
            rmv.Add(lnkdu);
            rs.Add(rmv);

            if(DataModule.UpdateDataSet(null, rs, null, config.GetConnection(serverInfo)))
            {
               dsLinked.Remove(lnkdu.id);
               usersView.InvalidateRow(usersView.CurrentRow.Index);
            }
         } else if (reqConnects.ContainsKey(di.Id))
         {
            LinkUser lu = new LinkUser(di);

            ReqConnect rc = Config.UnlinkUser(lu, config.serverCode);
            if (rc != null)
            {
               reqConnects.Remove(rc.id);
               usersView.InvalidateRow(usersView.CurrentRow.Index);
            }
         }
      }
   }

   class AgentManager
   {
      Agent agent;
      DivisionManager mgr;

      public AgentManager(Agent a) { agent = a; }
      public AgentManager(DivisionManager m) { mgr = m; }

      public string id { get { return agent != null ? agent.id : mgr.id;  } }
      public string name {  get { return agent != null ? agent.name : mgr.name; } }

      public string type { get { return agent != null ? Agents.OBJECT_NAME : DivisionManager.OBJECT_NAME;  } }
   }

   public class DataItem
   {
      AgentManager agent;
      UserActivity activity;
      MainForm owner;

      public DataItem(Agent a, DataSet<string, UserActivity> ua, MainForm owner)
      {
         agent = new AgentManager(a);
         activity = null;
         ua.TryGetValue(a.id, out activity);
         this.owner = owner;
      }
      public DataItem(DivisionManager a, DataSet<string, UserActivity> ua, MainForm owner)
      {
         agent = new AgentManager(a);
         activity = null;
         ua.TryGetValue(a.id, out activity);
         this.owner = owner;
      }

      public string Id { get { return agent.id; } }
      public string Name {  get { return agent.name;  } }

      public string Type { get { return agent.type; } }

      public string Status { get { return owner.Status(Id); } }

      public string LastAccess { get { return activity == null ? "" : activity.date.ToString("dd.MM.yy HH:mm"); } }
      public string Version { get { return activity == null ? "" : activity.version; } }
   }
}
