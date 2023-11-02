using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.Ads
{
   public partial class FmSetting : Form
   {
      private Config config;

      public FmSetting(Config config)
      {
         InitializeComponent();
         this.config = config;
      }

      public static void ShowInstance(EmptyInvoker okHandler)
      { 
         Config config = Config.GetConfig();
         FmSetting instance = new FmSetting(config);

         if (instance.ShowDialog() == DialogResult.OK)
         {
            config.ip = instance.tbIP.Text;
            config.password = instance.tbPassw.Text;
            config.login = instance.tbLogin.Text;
            config.port = Int32.Parse(instance.tbPort.Text);
            config.rememberPassword = instance.cbRememberPassword.Checked;
            config.mapSource = instance.cbMapSource.Text;
            config.prefix = instance.tbPrefix.Text;
            config.orderNumber = Int32.Parse(instance.tbOrderNumber.Text);
            config.refreshTime = (int)instance.udRefreshTime.Value;
            config.orderMissedInterval = (int)instance.udMissedOrderInterval.Value;
            config.alert = instance.cbAlert.Checked;
            config.Save();

            if (okHandler != null)
               okHandler();
         }
      }

      private void FmSetting_Load(object sender, EventArgs e)
      {
         tbIP.Text = config.ip;
         tbLogin.Text = config.login;
         tbPort.Text = config.port.ToString();
         tbPassw.Text = config.password;
         cbRememberPassword.Checked = config.rememberPassword;
         cbMapSource.Items.AddRange(MapEngine.GetNamesMaps());
         cbMapSource.Sorted = true;
         cbMapSource.SelectedIndex = cbMapSource.Items.IndexOf(config.mapSource);
         tbPrefix.Text = config.prefix;
         tbOrderNumber.Text = config.orderNumber.ToString();
         udRefreshTime.Value = config.refreshTime;
         udMissedOrderInterval.Value = config.orderMissedInterval;
         cbAlert.Checked = config.alert;
      }
   }
}
