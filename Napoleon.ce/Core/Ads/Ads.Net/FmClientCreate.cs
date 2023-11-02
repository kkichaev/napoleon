using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.Ads
{
   public partial class FmClientCreate : Form
   {
      private Invoker okInvoke;
      private Client client;

      public FmClientCreate()
      {
         InitializeComponent();
         client = new Client();
      }

      private void btnKladr_Click(object sender, EventArgs e)
      {
         FmKladr fmKladr = new FmKladr();

         if (fmKladr.ShowDialog() == DialogResult.OK)
            tbAddress.Text = fmKladr.Address;
      }

      public static void ShowInstance(Invoker okInvoke)
      {
         FmClientCreate instance = new FmClientCreate();
         instance.okInvoke = okInvoke;
         instance.ShowDialog();
      }

      private bool isFormOK()
      {
         return tbAddress.Text.Trim().Length > 0 && tbName.Text.Trim().Length > 0;
      }

      private void FmClient2_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK)
         {
            if (!isFormOK())
            {
               if (tbName.Text.Trim().Length == 0)
                  tbName.Focus();
               else if (tbAddress.Text.Trim().Length == 0)
                  tbAddress.Focus();

               MessageBox.Show("Необходимо заполнить все поля",
                  "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);

               e.Cancel = true;
            }
            else
            {
               DsClient dsClient = new DsClient(false);
               List<IDataSet> updSet = new List<IDataSet>();
               updSet.Add(dsClient);

               if (client.id.Trim().Length == 0)
               {
                  string key = System.Guid.NewGuid().ToString();
                  client.id = key.Replace("-", ""); ;
               }

               client.name = tbName.Text.Trim();
               client.address = tbAddress.Text.Trim();

               dsClient.Add(client.id, client);

               if (!DataModule.UpdateDataSet(updSet, null, null, Config.GetConfig().GetConnection()))
               {
                  e.Cancel = true;
                  MessageBox.Show("Ошибка записи в базу данных");
               }
               else
                  okInvoke(client);
            }
         }
      }

      private void btnClear_Click(object sender, EventArgs e)
      {
         client.id = "";
         tbName.Text = string.Empty;

         if (client != null && client.contacts != null)
         {
            client.contacts.Clear();
            UpdateContact();
         }
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         if (FmClientContactEdit.ShowInstance(client, null))
            UpdateContact();
      }

      private void UpdateContact()
      {
         DataGridViewRow row = dgvContact.CurrentRow;
         ClientContact cc = row != null ? row.DataBoundItem as ClientContact : null;

         List<ClientContact> list = new List<ClientContact>();
         list.AddRange(client.contacts);
         dgvContact.DataSource = list;

         if (cc != null)
         {
            foreach (DataGridViewRow r in dgvContact.Rows)
            {
               if ((r.DataBoundItem is ClientContact) && r.DataBoundItem == cc)
                  dgvContact.CurrentCell = r.Cells[0];
            }
         }
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         DataGridViewRow row = dgvContact.CurrentRow;

         if (row != null)
         {
            ClientContact cc = row.DataBoundItem as ClientContact;

            if (cc != null)
            {
               if (FmClientContactEdit.ShowInstance(client, cc))
                  UpdateContact();
            }
         }
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         DataGridViewRow row = dgvContact.CurrentRow;

         if (row != null)
         {
            ClientContact contact = row.DataBoundItem as ClientContact;

            if (MessageBox.Show("Запись будет удалена. Удалить?",
               "Вопрос", MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
            {
               client.contacts.Remove(contact);
               UpdateContact();
            }
         }
      }

      private void btnFind_Click(object sender, EventArgs e)
      {
         if (tbAddress.Text.Trim().Length >  0)
            FmClientFind.ShowInstance(tbAddress.Text.Trim(),
               new Invoker(delegate(object param)
            {
               if (param != null)
               {
                  client = (Client)param;

                  tbAddress.Text = client.Address;
                  tbName.Text = client.Name;

                  UpdateContact();
               }
            }));
      }

      private void btnCopy_Click(object sender, EventArgs e)
      {
         tbName.Text = tbAddress.Text;
      }
   }
}
