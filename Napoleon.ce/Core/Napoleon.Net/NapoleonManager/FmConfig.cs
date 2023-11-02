/*
 * Copyright (C), 2010, Гильдия разработчиков
 *
 * Форма для настройки программы
 * 
 * kki   01/09/2010   creating
 */

using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.NapoleonManager.Maps;
using GRSoft.Network;
using System.Collections;
using GRSoft.NapoleonManager.Utils;
using System.Globalization;
using System.Reflection;

namespace GRSoft.NapoleonManager
{
   //Настройки для текущего пользователя
   public partial class FmConfig : Form
   {
      public Config config = null;
      private DialogResult dialogResult = DialogResult.OK;

      //Серверный конфиг
      protected DataSet<int, CommonConfig> dsConfig;

      //Необходимые наборы данных
      private Agents dsAgents = Agents.GetDataSet();
      //private DataSet<>dsOrgs

      private const string COMMON_CONFIG_CAPTION = "Общие настройки";

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
         InitDataSets();

         config = Config.GetConfig();
         //config.Load();

         if( CurrentUser.user == null )
         {
            List<IDataSet> updSet = new List<IDataSet>();
            CurrentUser.InitCurrentUser(updSet);

            System.Threading.Thread t = DataModule.RefreshGiveSets(config.GetConnection(), updSet, null);
            t.Join();

            CurrentUser.SetCurrentUser(false);
         }

#if Zakroma
//         tbConfig.TabPages.Remove(tpUser);
//         tbConfig.TabPages.Remove(tpConnectionInfo);
#endif
      }

      //Настроить компоненты формы из класса Config
      protected virtual void SetControlsFromConfig()
      {
         tbLogin.Text      = config.login;
         tbPassword.Text   = config.password;
         tbIP.Text         = config.ip;
         tbPort.Text       = config.port.ToString();
         cbRememberPassword.Checked = config.rememberPassword;
         cbMapSource.Items.AddRange(MapEngine.GetNamesMaps());
         cbMapSource.Sorted = true;
         cbMapSource.SelectedIndex = cbMapSource.Items.IndexOf(config.mapSource);
         cbScriptErrorsAllow.Checked = config.scriptErrorAllow;
         
         proxyLogin.Text = config.proxyLogin;
         proxyPassword.Text = config.proxyPassword;
         proxyPort.Text = config.proxyPort.ToString();
         proxyIP.Text = config.proxyIP;
         proxyDomen.Text = config.proxyDomen;

         cbFullOrgName.Checked = config.isFullOrgName;

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
         cbHighliteOrderMissed.Checked = config.highliteOrderMissed;
         cbOnlyInstance.Checked = config.onlyInstance;

#if CONFIG_HISTORY
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
#else
         tbName.Visible = false;
         lbHistory.Visible = false;
         label16.Visible = false;
         label15.Visible = false;
#endif

      }

      //Заполнить Config из компоннтов формы
      protected virtual void FillConfigFromControls()
      {
         config.login      = tbLogin.Text;
         config.password   = tbPassword.Text;
         config.ip         = tbIP.Text;
         config.port       = Convert.ToInt32(tbPort.Text);
         config.rememberPassword = cbRememberPassword.Checked;
         config.mapSource = cbMapSource.Text;

         config.proxyLogin = proxyLogin.Text;
         config.proxyPassword = proxyPassword.Text;
         config.proxyDomen = proxyDomen.Text;

         config.proxyPort = (proxyPort.Text.Length > 0) ? Convert.ToInt32(proxyPort.Text) : 0;
         config.proxyIP = proxyIP.Text;

         config.isFullOrgName = cbFullOrgName.Checked;
         config.culture = (cbCultures.SelectedItem as CultureData).Code;
         config.highliteOrderMissed = cbHighliteOrderMissed.Checked;
         config.onlyInstance = cbOnlyInstance.Checked;
         config.name = tbName.Text.Trim();
         config.scriptErrorAllow = cbScriptErrorsAllow.Checked;
      }

      //Открыть форму
      public static DialogResult OpenConfig(Form owner)
      {
         Type tp = FormEntries.GetFormType(typeof(FmConfig));
         ConstructorInfo ci = tp.GetConstructor(Type.EmptyTypes);
         FmConfig form = (FmConfig)ci.Invoke(null);
         form.dialogResult = DialogResult.Cancel;
         form.SetControlsFromConfig();
         form.ShowDialog(owner);
         return form.dialogResult;
      }

      //OK
      private void btnOk_Click(object sender, EventArgs e)
      {
         FillConfigFromControls();
         config.Save();
         dialogResult = DialogResult.OK;
         Close();
      }

      //Cancel
      private void btnCancel_Click(object sender, EventArgs e)
      {
         dialogResult = DialogResult.Cancel;
         Close();
      }

      private void btnExit_Click(object sender, EventArgs e)
      {
         config.login = string.Empty;
         config.password = string.Empty;
         config.rememberPassword = false;
         config.Save();
         Close();
      }

      private void InitDataSets()
      {
         dsConfig = (DataSet<int, CommonConfig>) DataModule.Get(CommonConfig.OBJECT_NAME) ?? 
            new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME);

      }

      private List<Agent> CheckAndLoadAgents() // возвращает список агентов подчиненных данному
      {
         List<IDataSet> refreshDataSets = new List<IDataSet>();
         if (dsAgents.Count == 0)
            refreshDataSets.Add(dsAgents);
         DivisionList dsDivisions = DivisionList.GetDataSet();
         if (dsDivisions.Count == 0)
            refreshDataSets.Add(dsDivisions);

         Config c = Config.GetConfig();
         if (refreshDataSets.Count > 0)
            DataModule.RefreshGiveSets(c.GetConnection(), refreshDataSets, null).Join();

         Agent curAgent = dsAgents.Find(c.login, c.password);
         if (curAgent == null)
            return null;

         return dsDivisions.Subordinate(curAgent);
      }

      //Установить значение конфига по ключу
      private void setConfigValueByKey(ConfigKeyItems key, string value, string userid)
      {
         CommonConfig cc = ConfigUtils.GetConfig(dsConfig, ConfigKeyItems.VISIT_DUBLICATES_ORG, userid);

         if (cc == null)
         {
            cc = ConfigUtils.CreateConfig(dsConfig, ConfigKeyItems.VISIT_DUBLICATES_ORG, userid);
            dsConfig.Add(dsConfig.Count, cc);
         }

         cc.value = value;
      }

      //Сбросить текущее значение в ComboBOx
      private void ComboBoxResetValue(ComboBox comboBox)
      {
         comboBox.SelectedItem = null;
         comboBox.Text = string.Empty;
         comboBox.Refresh();
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
               foreach(Config c in history.config)
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

      private void miDel_Click(object sender, EventArgs e)
      {
         RemoveSetting();
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
#if CONFIG_HISTORY
            if (tbName.Text.Trim().Length == 0)
            {
               MessageBox.Show("Введите имя настройки", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
               e.Cancel = true;
            }
#endif
         }
      }
   }
}