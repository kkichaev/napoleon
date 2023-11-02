using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonAdmin
{
   public partial class SmtpSetting : UserControl
   {
      MainForm main;
      public GRSoft.Network.SimpleDataSet<ManagerConfigObj> dsManagerConfig = new Network.SimpleDataSet<ManagerConfigObj>(ManagerConfigObj.OBJECT_NAME, false);
      public const string MANAGER_LOGIN = "\x5A\x1O\x1fM\xeL\xdI\xcG\x23I\x1D";

      public SmtpSetting(MainForm main)
      {
         InitializeComponent();

         this.main = main;
      }

      private void SmtpSetting_Load(object sender, EventArgs e)
      {
         btnRefresh.PerformClick();
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         SimpleDataSet<ManagerConfigObj> ds = new SimpleDataSet<ManagerConfigObj>(ManagerConfigObj.OBJECT_NAME, false);

         ManagerConfigObj login = new ManagerConfigObj();
         login.manager = MANAGER_LOGIN;
         login.key = "login";
         login.value = tbLogin.Text.Trim();

         ds.Add(login);

         ManagerConfigObj pwd = new ManagerConfigObj();
         pwd.manager = MANAGER_LOGIN;
         pwd.key = "pwd";
         pwd.value = tbPass.Text.Trim();

         ds.Add(pwd);

         ManagerConfigObj server = new ManagerConfigObj();
         server.manager = MANAGER_LOGIN;
         server.key = "server";
         server.value = tbServer.Text.Trim();

         ds.Add(server);

         ManagerConfigObj port = new ManagerConfigObj();
         port.manager = MANAGER_LOGIN;
         port.key = "port";
         port.value = tbPort.Text.Trim();

         ds.Add(port);

         ManagerConfigObj from = new ManagerConfigObj();
         from.manager = MANAGER_LOGIN;
         from.key = "from";
         from.value = tbFrom.Text.Trim();

         ds.Add(from);

         ManagerConfigObj ssl = new ManagerConfigObj();
         ssl.manager = MANAGER_LOGIN;
         ssl.key = "ssl";
         ssl.value = cbSSL.Checked ? "1" : "0";

         ds.Add(ssl);

         ManagerConfigObj header = new ManagerConfigObj();
         header.manager = MANAGER_LOGIN;
         header.key = "header";
         header.value = tbHeader.Text.Trim();

         ds.Add(header);

         ManagerConfigObj body = new ManagerConfigObj();
         body.manager = MANAGER_LOGIN;
         body.key = "body";
         body.value = tbBody.Text.Trim();

         ds.Add(body);
         
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(ds);

         bool res = DataModule.UpdateDataSet(upd, null, null, main.config.GetConnection());

         if (res)
            MessageBox.Show("Изменения сохранены", "Информация", MessageBoxButtons.OK, MessageBoxIcon.Information);
         else
            MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Stop);
      }

      private void btnSendEmail_Click(object sender, EventArgs e)
      {

      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();
         dsManagerConfig.Filter = string.Format("\"manager\"='{0}'", MANAGER_LOGIN);
         upd.Add(dsManagerConfig);

         DataModule.DataProcessed += new EventHandler(DataLoaded);
         DataModule.OnDataResponceError += new EventDataResponseError(UpdateUserError);

         DataModule.RefreshGiveSets(main.config.GetConnection(), upd, null);
      }

      void UpdateUserError(EDataResponse e)
      {
         DataModule.ClearEvents();
         MessageBox.Show(e.Msg, "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Stop);
      }

      private void DataLoaded(object sender, EventArgs e)
      {
         DataModule.ClearEvents();

         if (dsManagerConfig.Count > 0)
         {
             BeginInvoke(new EmptyParamHandler(delegate
             {
               Dictionary<string, string> map = new Dictionary<string, string>();
               foreach (ManagerConfigObj m in dsManagerConfig.Values)
                  map[m.key] = m.value;

               if (map.ContainsKey("server"))
                  tbServer.Text = map["server"];

               if (map.ContainsKey("port"))
                  tbPort.Text = map["port"];

               if (map.ContainsKey("from"))
                  tbFrom.Text = map["from"];

               if (map.ContainsKey("pwd"))
                  tbPass.Text = map["pwd"];

               if (map.ContainsKey("login"))
                  tbLogin.Text = map["login"];

               if (map.ContainsKey("ssl"))
                  cbSSL.Checked = map["ssl"] == "1";

               if (map.ContainsKey("body"))
                  tbBody.Text = map["body"];

               if (map.ContainsKey("header"))
                  tbHeader.Text = map["header"];
             }));
         }
      }

      private void label3_Click(object sender, EventArgs e)
      {

      }
   }
}
