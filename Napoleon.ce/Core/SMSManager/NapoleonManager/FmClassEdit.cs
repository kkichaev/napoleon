/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Форма редактирования данных класса
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
   public partial class FmClassEdit : Form
   {
      private SchoolEntity school;
      private SchoolEntity schoolClass;
      private Locality locality;

      public FmClassEdit(SchoolEntity schoolClass, SchoolEntity school, 
         Locality locality)
      {
         InitializeComponent();

         this.school = school;
         this.locality = locality;
         this.schoolClass = schoolClass;

         Init();
      }

      private void Init()
      {
         lblLocality.Text = locality.name;
         lblSchool.Text = school.number;
         lblAddress.Text = school.address;

         tbClass.Text = schoolClass.number;

         if (school.contacts != null)
         { 
            StringBuilder sb = new StringBuilder();

            foreach (Contact contact in school.contacts)
            {
               sb.Append(contact.name).Append(" ").
                  Append(contact.phone).Append(" ").
                  Append(contact.remark).Append("; ");
            }

            lblContacts.Text = sb.ToString();
         }

         if (schoolClass.contacts != null)
         {
            foreach (Contact contact in schoolClass.contacts)
            {
               DataGridViewRow row = new DataGridViewRow();
               row.CreateCells(dgvContacts, contact.name, contact.phone, contact.remark);
               dgvContacts.Rows.Add(row);
            }
         }
      }

      private void btnOK_Click(object sender, EventArgs e)
      {
         schoolClass.number = tbClass.Text;

         if (schoolClass.contacts != null)
            schoolClass.contacts.Clear();

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
               : row.Cells[2].Value.ToString();

            if (schoolClass.contacts == null)
               schoolClass.contacts = new List<Contact>();

            schoolClass.contacts.Add(contact);
         }
      }

      private void FmClassEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK &&
            tbClass.Text.Trim().Length == 0)
         {
            e.Cancel = true;
            Dialogs.PleaseFillFieldsDlg();
         }
      }

      private void FmClassEdit_Activated(object sender, EventArgs e)
      {
         tbClass.Focus();
      }
   }
}