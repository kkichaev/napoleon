using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Ads;

namespace GRSoft.Ads
{
   public partial class Login : Form
   {
      private Config config;

      Login(Config config)
      {
         InitializeComponent();

         this.config = config;

         user.Text = config.login;
         password.Text = config.password;
         rememberPwd.Checked = config.rememberPassword;
      }

      static internal bool Ack(Config config)
      {
         bool res = false;
         Login l = new Login(config);

         if (l.ShowDialog() == DialogResult.OK)
         {
            config.login = l.user.Text;
            config.password = l.password.Text;
            config.rememberPassword = l.rememberPwd.Checked;

            res = true;
         }

         return res;
      }
   }
}
