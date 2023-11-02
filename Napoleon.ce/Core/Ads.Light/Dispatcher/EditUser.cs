using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Ads.Dispatcher.Properties;

namespace GRSoft.Ads.Dispatcher
{
   public partial class EditUser : Form
   {
      public EditUser()
      {
         InitializeComponent();
      }

      public string AgentName
      {
         get { return tbName.Text.Trim(); }
         set { tbName.Text = value; }
      }

      public string AgentLogin
      {
         get { return tbLogin.Text.Trim(); }
         set { tbLogin.Text = value; }
      }

      public string AgentPwd
      {
         get { return tbPassw.Text.Trim(); }
         set { tbPassw.Text = value; }
      }

      private bool CheckTBValue(TextBox tb, string msg)
      {
         bool result = true;

         if (tb.Text.Trim().Length == 0)
         {
            MessageBox.Show(msg, Resources.error,
               MessageBoxButtons.OK, MessageBoxIcon.Error);
            tb.Focus();
            result = false;
         }

         return result;
      }

      private void EditUser_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK)
         {
            e.Cancel = !CheckTBValue(tbName, Resources.nameCantEmpty) ||
               !CheckTBValue(tbLogin, Resources.loginCantEmpty) ||
               !CheckTBValue(tbPassw, Resources.pwdCantEmpty);
         }
      }
   }
}
