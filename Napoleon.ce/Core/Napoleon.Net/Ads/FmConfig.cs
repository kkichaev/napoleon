using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Collections;
using System.Globalization;

namespace GRSoft.NapoleonManager
{
   //Настройки для текущего пользователя
   public partial class FmConfig : Form
   {
      private Config config = null;
      private DataSet<string, DivisionManager> dsManager;
      private Agents dsAgents = Agents.GetDataSet();

      class CultureData
      {
         CultureInfo ci;
         public CultureData(CultureInfo ci)
         {
            this.ci = ci;
         }

         public override string ToString()
         {
            return ci.DisplayName;
         }

         public string Code { get { return ci.Name; } }
      }

      //FmConfig
      public FmConfig()
      {
         InitializeComponent();
         config = Config.GetConfig();
         SetControlsFromConfig();
         dsManager = (DataSet<string, DivisionManager>)DataModule.Get(DivisionManager.OBJECT_NAME) ?? new DataSet<string, DivisionManager>(DivisionManager.OBJECT_NAME);
      }

      //Настроить компоненты формы из класса Config
      private void SetControlsFromConfig()
      {
         tbLogin.Text      = config.login;
         tbPassword.Text   = config.password;
         tbIP.Text         = config.ip;
         tbPort.Text       = config.port.ToString();
         cbRememberPassword.Checked = config.rememberPassword;
         
         proxyLogin.Text = config.proxyLogin;
         proxyPassword.Text = config.proxyPassword;
         proxyPort.Text = config.proxyPort.ToString();
         proxyIP.Text = config.proxyIP;
         proxyDomen.Text = config.proxyDomen;

         CultureData selected = null;

         foreach (CultureInfo ci in CultureInfo.GetCultures(CultureTypes.AllCultures))
         {
            if (ci.IsNeutralCulture || ci.Name.Length == 0)
               continue;

            CultureData cd = new CultureData(ci);
            cbCultures.Items.Add(cd);

            if (ci.Name.Equals(config.culture))
               selected = cd;
         }

         cbCultures.SelectedItem = selected;
         cbOnlyInstance.Checked = config.onlyInstance;

         DateTime dt = DateTime.Now;
         dt = dt.Subtract(dt.TimeOfDay);
         dtpStartWT.Value = dt.Add(new TimeSpan(config.wtStart));
         dtpFinishWT.Value = dt.Add(new TimeSpan(config.wtFinish));
      }

      //Заполнить Config из компоннтов формы
      private void FillConfigFromControls()
      {
         config.login      = tbLogin.Text;
         config.password   = tbPassword.Text;
         config.ip         = tbIP.Text;

         try
         {
            config.port = Convert.ToInt32(tbPort.Text);
         }
         catch (Exception) { }

         config.rememberPassword = cbRememberPassword.Checked;

         config.proxyLogin = proxyLogin.Text;
         config.proxyPassword = proxyPassword.Text;
         config.proxyDomen = proxyDomen.Text;

         config.proxyPort = (proxyPort.Text.Length > 0) ? Convert.ToInt32(proxyPort.Text) : 0;
         config.proxyIP = proxyIP.Text;

         config.culture = (cbCultures.SelectedItem as CultureData).Code;
         config.onlyInstance = cbOnlyInstance.Checked;

         config.wtStart = dtpStartWT.Value.TimeOfDay.Ticks;
         config.wtFinish = dtpFinishWT.Value.TimeOfDay.Ticks;
      }

      private void btnOk_Click(object sender, EventArgs e)
      {
         FillConfigFromControls();
         config.Save();
      }

      private void btnExit_Click(object sender, EventArgs e)
      {
         config.login = string.Empty;
         config.password = string.Empty;
         config.rememberPassword = false;
         config.Save();
         Close();
      }

      private void tbConfig_Selected(object sender, TabControlEventArgs e)
      {
         
      }

      //Окончание выборки для конфига
      private void ConfigDataProcessed(System.Object setnder, EventArgs e)
      {
         ClearRegisterDataModuleEvents();
         FmWait.CloseForm();
      }

      //Произошла ошибка в соединении
      private void DataConnectionError(EDataResponse e)
      {
         ClearRegisterDataModuleEvents();
         FmWait.CloseForm();

         MessageBox.Show(e.Msg);
      }

      //Очистить события выборки данных
      private void ClearRegisterDataModuleEvents()
      {
         DataModule.OnDataResponceError -= DataConnectionError;
         DataModule.DataProcessed -= ConfigDataProcessed;
      }

      //Сбросить текущее значение в ComboBOx
      private void ComboBoxResetValue(ComboBox comboBox)
      {
         comboBox.SelectedItem = null;
         comboBox.Text = string.Empty;
         comboBox.Refresh();
      }

      private void SetNewConfig()
      {
         SetControlsFromConfig();
         Config.SetInstance(config);
         config.GetConnection().SetNewSession(Config.PDTFileName(config.name));
      }

      private void FmConfig_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK)
         {
            FillConfigFromControls();
            config.Save();
         }
      }

      private DBConnection adminConnect = new DBConnection();

      private void btnControl_Click(object sender, EventArgs e)
      {
         if (tbAdmPwd.Text.Trim().Length > 0)
         {
            String admpwd = tbAdmPwd.Text.Trim();
            
            List<IDataSet> upd = new List<IDataSet>();
            upd.Add(dsManager);
           
            adminConnect.ip = tbIP.Text.Trim();
            try
            {
               adminConnect.port = Int32.Parse(tbPort.Text.Trim());
            }
            catch (Exception) { }
            const string ADMIN = "admin";
            adminConnect.login = ADMIN;
            adminConnect.password = tbAdmPwd.Text.Trim();

            FmWait.StdDataRefresh(this, upd, AdminControl, btnControl, adminConnect);
         }

      }

      private void AdminControl()
      {
         AdminControl admin = new AdminControl(adminConnect);
         admin.Show();
      }
   }
}