using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmPriceSetting : Form
   {
      SettingFmPrice setting = null;

      public FmPriceSetting()
      {
         InitializeComponent();
      }

      public static void ShowInstance(SettingFmPrice setting)
      {
         FmPriceSetting instance = new FmPriceSetting();
         instance.setting = setting;
         instance.ShowDialog();
      }

      private void FmPriceSetting_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK && setting != null)
         {
            setting.szX = (int)upSizeX.Value;
            setting.szY = (int)upSizeY.Value;
         }
      }

      private void FmPriceSetting_Load(object sender, EventArgs e)
      {
         if (setting != null)
         {
            upSizeX.Value = setting.szX;
            upSizeY.Value = setting.szY;
         }
      }

   }
}
