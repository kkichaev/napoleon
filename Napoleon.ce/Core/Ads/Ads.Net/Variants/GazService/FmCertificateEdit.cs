using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.Ads
{
   public partial class FmCertificateEdit : Form
   {
      public FmCertificateEdit()
      {
         InitializeComponent();
      }

      public static Certificate ShowInstance(Brigade brigade)
      {
         Certificate result = null;
         FmCertificateEdit form = new FmCertificateEdit();

         if (form.ShowDialog() == DialogResult.OK)
         {
            result = new Certificate();
            result.assigned = DateTime.Now;
            result.number = form.tbNumber.Text;
            result.brigade = brigade;
         }

         return result;
      }

      private void FmSertificateEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK)
         {
            if (tbNumber.Text.Trim().Length == 0)
            {
               e.Cancel = true;
               MessageBox.Show("Введите номер свидетельства", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
               tbNumber.Focus();
            }
         }
      }
   }
}
