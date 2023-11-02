using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmApproveMsg : Form
   {
      public FmApproveMsg()
      {
         InitializeComponent();
      }

      private void FmApproveMsg_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK && tbText.Text.Trim().Length == 0)
         {
            e.Cancel = true;
            MessageBox.Show("Введите тексь сообщения", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
            tbText.Focus();
         }
      }
   }
}
