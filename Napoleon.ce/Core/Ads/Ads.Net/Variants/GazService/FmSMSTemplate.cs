using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.Ads
{
   public partial class FmSMSTemplate : Form
   {
      private DsCommonConfig dsCommonConfig;
      const string ADMPWD = "SMSTEMP";
      const int MAX_LEN = 70;

      public FmSMSTemplate()
      {
         InitializeComponent();
         dsCommonConfig = (DsCommonConfig)DataModule.Get(CommonConfig.OBJECT_NAME) ?? new DsCommonConfig(true);
         dsCommonConfig.Filter = "(not (userid is null)) or userid is null";
         
      }

      private void FmSMSTemplate_Load(object sender, EventArgs e)
      {
         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsCommonConfig);

         DataModule.SetDataRepsonceHandlers(DataModule_DataProcessed, DataModule_OnDataResponceError); 
         FmWait.ShowForm(this,
            DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), list, FmWait.ProgressIndicator));
      }

      void DataModule_DataProcessed(object sender, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         Invoke(new InvokeDelegate(delegate() 
            {
               foreach (CommonConfig cfg in dsCommonConfig.Data)
               {
                  if (cfg.key.Equals(ADMPWD))
                  {
                     tbText.Text = cfg.value;
                     break;
                  }
               }
            }));
      }

      void DataModule_OnDataResponceError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         MessageBox.Show(e.Msg);
      }

      private void FmSMSTemplate_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK)
         {
            DsCommonConfig dsCC = new DsCommonConfig(false);

            List<IDataSet> updList = null;
            List<IDataSet> delList = null;

            CommonConfig cc = new CommonConfig();
            cc.key = ADMPWD;
            cc.value = tbText.Text.Trim();
            dsCC.Add(1, cc);

            if (tbText.Text.Trim().Length > 0)
            {
               updList = new List<IDataSet>();
               updList.Add(dsCC);
            }
            else
            {
               delList = new List<IDataSet>();
               delList.Add(dsCC);
            }

            bool res = DataModule.UpdateDataSet(updList, delList, null, Config.GetConfig().GetConnection());

            if (!res)
            {
               MessageBox.Show(this, "Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
               e.Cancel = true;
            }
         }
      }

      private void tbText_TextChanged(object sender, EventArgs e)
      {
         lblCount.Text = (MAX_LEN - tbText.Text.Length).ToString();
      }

      private void tbText_KeyDown(object sender, KeyEventArgs e)
      {
         if (e.KeyCode != Keys.Back && MAX_LEN - tbText.Text.Length <= 0)
            e.SuppressKeyPress = true;
      }
   }
}
