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
   public partial class FmSchoolEdit : Form
   {
      SchoolEntity schoolEntity;

      public FmSchoolEdit(SchoolEntity schoolEntity)
      {
         InitializeComponent();
         this.schoolEntity = schoolEntity;
         tbNumber.Text = schoolEntity.number;
         tbAddress.Text = schoolEntity.address;

         if (schoolEntity.contacts != null)
         {
            foreach (Contact contact in schoolEntity.contacts)
            {
               DataGridViewRow row = new DataGridViewRow();
               row.CreateCells(dgvContacts, contact.name, contact.phone, contact.remark);
               dgvContacts.Rows.Add(row);
            }
         }
      }

      public static DialogResult ShowDialog(SchoolEntity schoolEntity)
      {
         return new FmSchoolEdit(schoolEntity).ShowDialog();
      }

      private void btnOK_Click(object sender, EventArgs e)
      {
         schoolEntity.number = tbNumber.Text;
         schoolEntity.address = tbAddress.Text;

         if (schoolEntity.contacts != null)
            schoolEntity.contacts.Clear();

         foreach (DataGridViewRow row in dgvContacts.Rows)
         {
            if (row.Cells[0].Value == null &&
               row.Cells[1].Value == null &&
               row.Cells[2].Value == null)
               continue;

            Contact contact = new Contact();
            contact.name = row.Cells[0].Value == null 
               ? String.Empty
               : row.Cells[0].Value.ToString();
            contact.phone = row.Cells[1].Value == null
               ? String.Empty
               : row.Cells[1].Value.ToString();
            contact.remark = row.Cells[2].Value == null
               ? String.Empty
               :row.Cells[2].Value.ToString();

            if (schoolEntity.contacts == null)
               schoolEntity.contacts = new List<Contact>();

            schoolEntity.contacts.Add(contact);
         }
      }

      private void FmSchoolEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK && tbNumber.Text.Trim().Length == 0)
         {
            e.Cancel = true;
            Dialogs.PleaseFillFieldsDlg();
         }
      }

      private void FmSchoolEdit_Activated(object sender, EventArgs e)
      {
         tbNumber.Focus();
      }
   }
}