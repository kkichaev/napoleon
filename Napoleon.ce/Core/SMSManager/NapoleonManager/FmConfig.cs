/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Форма Настройки
 * 
 * kki   11/12/2010   creating
 */
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmConfig : Form
   {
      private Config config = Config.GetConfig();

      private FmConfig()
      {
         InitializeComponent();
      }

      public static void ShowConfig()
      {
         new FmConfig().ShowDialog();
      }

      private void FmConfig_Load(object sender, EventArgs e)
      {
         tbPort.Text = config.port.ToString();
         tbIP.Text = config.ip;
      }

      private void btnOK_Click(object sender, EventArgs e)
      {
         config.ip = tbIP.Text;
         config.port = Convert.ToInt32(tbPort.Text);
         config.Save();
      }
   }
}