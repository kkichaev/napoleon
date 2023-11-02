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
   public partial class FmPdaPasw : Form
   {
      private DsCommonConfig dsCommonConfig;
      const string ADMPWD = "ADMPWD";

      public FmPdaPasw()
      {
         InitializeComponent();
         dsCommonConfig = (DsCommonConfig)DataModule.Get(CommonConfig.OBJECT_NAME) ?? new DsCommonConfig(true);
         dsCommonConfig.Filter = "(not (userid is null)) or userid is null";
         
      }

      private void FmPdaPasw_Load(object sender, EventArgs e)
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
                     tbPassw.Text = cfg.value;
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

      private void FmPdaPasw_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK)
         {
            DsCommonConfig dsCC = new DsCommonConfig(false);

            List<IDataSet> updList = null;
            List<IDataSet> delList = null;

            CommonConfig cc = new CommonConfig();
            cc.key = ADMPWD;
            cc.value = tbPassw.Text.Trim();
            dsCC.Add(1, cc);

            if (tbPassw.Text.Trim().Length > 0)
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
      
   }
}
