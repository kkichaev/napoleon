using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.Ads.Dispatcher
{
   public partial class Setting : Form
   {
      public Setting()
      {
         InitializeComponent();
      }

      private void Setting_Load(object sender, EventArgs e)
      {
         Config cfg = Config.GetConfig();
         tbIP.Text = cfg.ip;
         tbPassw.Text = cfg.password;
         numHourStart.Value = cfg.hourStart;
         numHourEnd.Value = cfg.hourEnd;
         cbRememberPassw.Checked = cfg.rememberPassword;

         foreach (string s in cfg.cities)
            cbCity.Items.Add(s);

         if (cfg.city.Length > 0)
         {
            int idx = cfg.cities.IndexOf(cfg.city);

            if (idx != -1 && idx < cbCity.Items.Count)
               cbCity.SelectedIndex = idx;
         }

         cbMap.Items.AddRange(new object[] { "Google", "Openstreent" });

         if (cfg.mapSource.Length > 0)
         {
            int idx = cbMap.Items.IndexOf(cfg.mapSource);

            if (idx != -1 && idx < cbMap.Items.Count)
               cbMap.SelectedIndex = idx;
         }
      }

      private void btnOK_Click(object sender, EventArgs e)
      {
         Config cfg = Config.GetConfig();
         cfg.ip = tbIP.Text.Trim();
         cfg.password = tbPassw.Text.Trim();
         cfg.hourStart = (short)numHourStart.Value;
         cfg.hourEnd = (short)numHourEnd.Value;
         cfg.rememberPassword = cbRememberPassw.Checked;
         cfg.mapSource = cbMap.Text.Trim();
         cfg.city = cbCity.Text.Trim();

         if (cfg.city.Length > 0 && 
               !cfg.cities.Exists(c => c.Equals(cfg.city)))
            cfg.cities.Add(cfg.city);

         cfg.cities.Sort();

         if (settingOnChange != null)
            settingOnChange(cfg);

         cfg.Save();

         Close();
      }

      public delegate void SettingOnChange(Config cfg);
      public event SettingOnChange settingOnChange;
   }
}
