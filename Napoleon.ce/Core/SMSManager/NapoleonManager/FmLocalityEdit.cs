/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Форма для редактирования данных о городе
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
   public partial class FmLocalityEdit : Form
   {
      private Locality locality;

      public FmLocalityEdit(Locality locality)
      {
         InitializeComponent();
         this.locality = locality;
         tbName.Text = locality.name;
      }

      public static DialogResult ShowDialog(Locality locality)
      {
         return new FmLocalityEdit(locality).ShowDialog();
      }

      private void btnOK_Click(object sender, EventArgs e)
      {
         locality.name = tbName.Text;
      }

      private void FmLocalityEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK && tbName.Text.Trim().Length == 0)
         {
            e.Cancel = true;
            Dialogs.PleaseFillFieldsDlg();
         }
      }

      private void FmLocalityEdit_Activated(object sender, EventArgs e)
      {
         tbName.Focus();
      }
   }
}