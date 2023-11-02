/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Форма для редактировани данных о родителях
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
   public partial class FmParentsEdit : Form
   {
      private Parent parent;

      public FmParentsEdit(Parent parent)
      {
         InitializeComponent();
         this.parent = parent;
         Init();
      }

      private void Init()
      {
         tbName.Text = parent.name;

         if (parent.phones != null)
         {
            foreach (Phone phone in parent.phones)
            {
               DataGridViewRow row = new DataGridViewRow();
               row.CreateCells(dgvPhones, phone.phone, phone.remark);
               dgvPhones.Rows.Add(row);
            }
         }
      }

      private void btnOK_Click(object sender, EventArgs e)
      {
         parent.name = tbName.Text;

         if (parent.phones != null)
            parent.phones.Clear();

         foreach(DataGridViewRow row in dgvPhones.Rows)
         {
            if (row.Cells[0].Value == null && row.Cells[1].Value == null)
               continue;

            Phone phone = new Phone();

            phone.phone = row.Cells[0].Value == null 
               ? String.Empty
               : row.Cells[0].Value.ToString();

            phone.remark = row.Cells[1].Value == null
               ? String.Empty
               : row.Cells[1].Value.ToString();

            if (parent.phones == null)
               parent.phones = new List<Phone>();

            parent.phones.Add(phone);
         }
      }

      private void FmParentsEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK &&
            tbName.Text.Trim().Length == 0)
         {
            e.Cancel = true;
            Dialogs.PleaseFillFieldsDlg();
         }
      }

      private void FmParentsEdit_Activated(object sender, EventArgs e)
      {
         tbName.Focus();
      }
   }
}