/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Форма редактирования данных ученика
 * 
 * kki   01/12/2010   creating
 */
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.NapoleonManager.DataObjects;
using GRSoft.NapoleonManager.Utils;

namespace GRSoft.NapoleonManager
{
   public partial class FmStudentEdit : Form
   {
      private Student student;
 
      public FmStudentEdit(Student student)
      {
         InitializeComponent();
         this.student = student;
         tbName.Text = student.name;
      }

      private void btnOK_Click(object sender, EventArgs e)
      {
         student.name = tbName.Text;
      }

      private void FmStudentEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK &&
               tbName.Text.Trim().Length == 0)
         {
            e.Cancel = true;
            Dialogs.PleaseFillFieldsDlg();
         }
      }

      private void FmStudentEdit_Activated(object sender, EventArgs e)
      {
         tbName.Focus();
      }
   }
}