using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class MatrixNameDlg : Form
   {
      public MatrixNameDlg()
      {
         InitializeComponent();
      }

      private void MatrixNameDlg_FormClosing(object sender, FormClosingEventArgs e)
      {
         if(DialogResult == DialogResult.OK && textBox.Text.Trim().Length == 0)
         {
            MessageBox.Show("Введите название матрицы");
            e.Cancel = true;
         }
      }
   }

   
}
