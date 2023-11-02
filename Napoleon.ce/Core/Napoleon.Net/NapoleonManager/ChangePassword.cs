using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class ChangePassword : Form
   {
      private String curPassword;
      private String newPassword;

      protected ChangePassword(string curPassword)
      {
         this.curPassword = curPassword;

         InitializeComponent();
      }

      static public string DoChangePassword(string curPassword)
      {
         string newPassword = null;

         ChangePassword cp = new ChangePassword(curPassword);
         if (cp.ShowDialog() == DialogResult.OK)
            newPassword = cp.newPassword;

         return newPassword;
      }

      private void ok_Click(object sender, EventArgs e)
      {
         if (currPwd.Text != curPassword)
         {
            MessageBox.Show("Не правильный текущий пароль", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Stop);
            return;
         }

         if (newPwd.Text != newPwdCheck.Text)
         {
            MessageBox.Show("Введенный пароль не совпадает", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Stop);
            return;
         }

         newPassword = newPwd.Text;
         DialogResult = DialogResult.OK;
      }
   }
}
