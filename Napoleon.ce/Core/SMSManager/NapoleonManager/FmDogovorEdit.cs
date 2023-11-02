/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Форма редактирование договора
 * 
 * kki   07/12/2010   creating
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
   public partial class FmDogovorEdit : Form
   {
      private Dogovor dogovor;

      public FmDogovorEdit(Dogovor dogovor)
      {
         InitializeComponent();
         this.dogovor = dogovor;
         Init();
      }

      private void Init()
      {
         cbType.Items.AddRange(Dogovor.Types);
         tbNumber.Text = dogovor.number;
         dtpBegin.Value = dogovor.start == DateTime.MinValue 
            ? DateTime.Now
            : dogovor.start;
         dtpEnd.Value = dogovor.end == DateTime.MinValue
            ? DateTime.Now.AddMonths(1)
            : dogovor.end;
         cbType.SelectedIndex = dogovor.type;
      }

      private void btnOK_Click(object sender, EventArgs e)
      {
         dogovor.number = tbNumber.Text;
         dogovor.start = dtpBegin.Value.Date;
         dogovor.end = dtpEnd.Value.Date;
         dogovor.type = cbType.SelectedIndex;
      }

      private void FmDogovorEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK &&
               tbNumber.Text.Trim().Length == 0)
         {
            e.Cancel = true;
            Dialogs.PleaseFillFieldsDlg();
         }
      }

      private void FmDogovorEdit_Activated(object sender, EventArgs e)
      {
         tbNumber.Focus();
      }

      
   }
}