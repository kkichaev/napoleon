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
using System.Threading;

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

         tbCode.Focus();
         if(config.uuid.Length == 0)
         {
            btnOk.Enabled = false;
         }
         //if( CurrentUser.user == null )
         //{
         //   List<IDataSet> updSet = new List<IDataSet>();
         //   CurrentUser.InitCurrentUser(updSet);

         //   System.Threading.Thread t = DataModule.RefreshGiveSets(config.GetConnection(), updSet, null);
         //   t.Join();

         //   CurrentUser.SetCurrentUser(false);
         //}

      }

      //Настроить компоненты формы из класса Config
      protected virtual void SetControlsFromConfig()
      {
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
      }

      //Заполнить Config из компоннтов формы
      protected virtual void FillConfigFromControls()
      {
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
         config.Save();
         Close();
      }

      private void InitDataSets()
      {
         dsConfig = (DataSet<int, CommonConfig>) DataModule.Get(CommonConfig.OBJECT_NAME) ?? 
            new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME);

      }

      //private List<Agent> CheckAndLoadAgents() // возвращает список агентов подчиненных данному
      //{
      //   List<IDataSet> refreshDataSets = new List<IDataSet>();
      //   if (dsAgents.Count == 0)
      //      refreshDataSets.Add(dsAgents);
      //   DivisionList dsDivisions = DivisionList.GetDataSet();
      //   if (dsDivisions.Count == 0)
      //      refreshDataSets.Add(dsDivisions);

      //   Config c = Config.GetConfig();
      //   if (refreshDataSets.Count > 0)
      //      DataModule.RefreshGiveSets(c.GetConnection(), refreshDataSets, null).Join();

      //   Agent curAgent = dsAgents.Find(c.login, c.password);
      //   if (curAgent == null)
      //      return null;

      //   return dsDivisions.Subordinate(curAgent);
      //}

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

      private void miDel_Click(object sender, EventArgs e)
      {
      }


      private void FmConfig_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK)
         {
         }
      }

      void MakeConnection(string code)
      {
         LinkedUser lu = ConnectionHelper.RequestLink(code);
         if(lu.error.Length == 0)
         {
            config.uuid = lu.code;
            config.userid = lu.id;
            config.serverCode = lu.server_code;

            config.Save();

            BeginInvoke((Action)(() =>
            {
               DialogResult = DialogResult.OK;
               Close();
            }));

            return;
         }

         BeginInvoke((Action)(() =>
         {
            MessageBox.Show(lu.error);
         }));
      }

      private void btnConnect_Click(object sender, EventArgs e)
      {
         string code = tbCode.Text;
         if(code.Length == 0)
         {
            return;
         }

         Thread t = new Thread(() => MakeConnection(code));
         t.Start();
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         BeginInvoke((Action)(() => tbCode.Focus()));
      }

      private void tbConfig_SelectedIndexChanged(object sender, EventArgs e)
      {
         if (tbConfig.SelectedIndex == 0)
         {
            tbCode.Focus();
         }
      }
   }
}