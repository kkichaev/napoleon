using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.NapoleonManager;
using GRSoft.Network;

namespace NapoleonCfg
{
   public partial class FmMain : Form
   {
      private static readonly string LOGIN = "admin";
      private Agents agents = new Agents(false);
      private Config config = null;

      public FmMain()
      {
         InitializeComponent();

         config = Config.GetConfig();
         SetControlsFromConfig();

         gpSettings.Enabled = false;
      }

      private void btnConnect_Click(object sender, EventArgs e)
      {
         if (tbIPAdmin.Text.Trim().Length == 0)
         {
            MessageBox.Show("Введите IP");
            tbIPAdmin.Focus();
            return;
         }

         if (tbPortAdmin.Text.Trim().Length == 0)
         {
            MessageBox.Show("Введите порт");
            tbPortAdmin.Focus();
            return;
         }

         if (tbPasswAdmin.Text.Trim().Length == 0)
         {
            MessageBox.Show("Введите пароль");
            tbPasswAdmin.Focus();
            return;
         }

         int port = -1;

         if(Int32.TryParse(tbPortAdmin.Text.Trim(), out port))
         {
            btnConnect.Enabled = false;
            DataModule.SetDataRepsonceHandlers(DataProcessed, DataConnectionError);

            DBConnection conn = new DBConnection(tbIPAdmin.Text.Trim(), port);
            conn.login = LOGIN;
            conn.password = tbPasswAdmin.Text.Trim();
            List<IDataSet> updSets = new List<IDataSet>();
            updSets.Add(agents);
            FmWait.ShowForm(this, DataModule.RefreshGiveSets(conn, updSets, FmWait.ProgressIndicator));
         }
         else
         {
            MessageBox.Show("Ошибка при вводе порта");
            tbPortAdmin.Focus();
         }
      }

      //Окончание выборки, заполняются внутренние наборы
      void DataProcessed(System.Object setnder, EventArgs e)
      {
         DataModule.ClearEvents();
         Invoke(new EmptyParamHandler(delegate
         {
            FmWait.CloseForm();
            btnConnect.Enabled = true;
            gpSettings.Enabled = true;
         }));
      }

      //Произошла ошибка в соединении
      private void DataConnectionError(EDataResponse e)
      {
         DataModule.ClearEvents();

         Invoke(new EmptyParamHandler(delegate
         {
            FmWait.CloseForm();
            btnConnect.Enabled = true;
            gpSettings.Enabled = false;

            const string TITLE = "Ошибка";

            MessageBox.Show(e.Msg, TITLE, MessageBoxButtons.OK,
               MessageBoxIcon.Error);
         }));
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
                  SetNewConfig();
               }
            }
         }
      }

      private void SetNewConfig()
      {
         SetControlsFromConfig();
         Config.SetInstance(config);
         config.GetConnection().SetNewSession(Config.PDTFileName(config.name));
      }

      //Настроить компоненты формы из класса Config
      private void SetControlsFromConfig()
      {
         tbLogin.Text = config.login;
         tbPassword.Text = config.password;
         tbIP.Text = config.ip;
         tbPort.Text = config.port.ToString();

         proxyLogin.Text = config.proxyLogin;
         proxyPassword.Text = config.proxyPassword;
         proxyPort.Text = config.proxyPort.ToString();
         proxyIP.Text = config.proxyIP;
         
         List<Config> history = new List<Config>();
         ConfigHistory cfgHistory = ConfigHistory.Instance(true);
         history.AddRange(cfgHistory.config);
         history.Sort((c1, c2) => c1.name.CompareTo(c2.name));
         //history.Sort(new Comparison<Config>(delegate(Config c1, Config c2) { return c1.name.CompareTo(c2.name); }));
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

      private void lbHistory_MouseDown(object sender, MouseEventArgs e)
      {
         if (e.Button == MouseButtons.Right || e.Button == MouseButtons.Left)
         {
            FillConfigFromControls();
            config.Save();
            lbHistory.SelectedIndex = lbHistory.IndexFromPoint(e.X, e.Y);

            if (lbHistory.SelectedIndex != -1)
            {
               config = (Config)lbHistory.Items[lbHistory.SelectedIndex];
               SetNewConfig();
            }
         }
      }

      //Заполнить Config из компоннтов формы
      private void FillConfigFromControls()
      {
         config.login = tbLogin.Text;
         config.password = tbPassword.Text;
         config.ip = tbIP.Text;
         try
         {
            config.port = Convert.ToInt32(tbPort.Text);
         }
         catch (Exception) { }
         config.rememberPassword = true;
         config.name = tbName.Text.Trim();

         config.proxyLogin = proxyLogin.Text;
         config.proxyPassword = proxyPassword.Text;

         config.proxyPort = (proxyPort.Text.Length > 0) ? Convert.ToInt32(proxyPort.Text) : 0;
         config.proxyIP = proxyIP.Text;
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         FillConfigFromControls();
         config.Save();
         SetControlsFromConfig();
      }

      private void btnRem_Click(object sender, EventArgs e)
      {
         RemoveSetting();
      }
   }

   public delegate void InvokeDelegate();
}
