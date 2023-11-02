/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Форма Школьные Предметы (редактирование)
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
using GRSoft.NapoleonManager.DataObjects;
using GRSoft.NapoleonManager.Utils;

namespace GRSoft.NapoleonManager
{
   public partial class FmSchoolSubjectEdit : Form
   {
      private SchoolSubject schoolSubject;

      public FmSchoolSubjectEdit(SchoolSubject schoolSubject)
      {
         InitializeComponent();
         this.schoolSubject = schoolSubject;

         tbName.Text = schoolSubject.name;
         tbSecondName.Text = schoolSubject.secondName;
      }

      private void btnOK_Click(object sender, EventArgs e)
      {
         schoolSubject.name = tbName.Text;
         schoolSubject.secondName = tbSecondName.Text;
      }

      private void FmSchoolSubjectEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK &&
               tbName.Text.Trim().Length == 0)
         {
            e.Cancel = true;
            Dialogs.PleaseFillFieldsDlg();
         }
      }

      private void FmSchoolSubjectEdit_Activated(object sender, EventArgs e)
      {
         tbName.Focus();
      }
   }
}