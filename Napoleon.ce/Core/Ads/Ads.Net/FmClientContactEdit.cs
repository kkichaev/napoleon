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
   public partial class FmClientContactEdit : Form
   {
      public FmClientContactEdit()
      {
         InitializeComponent();
      }

      public static bool ShowInstance(Client client, ClientContact contact)
      {
         FmClientContactEdit instance = new FmClientContactEdit();

         if (contact != null)
         {
            instance.tbName.Text = contact.name;
            instance.tbPhone.Text = contact.phone;
            instance.Text = "Изменить";
         }
         else
         {
            instance.Text = "Добавить";
         }

         if (instance.ShowDialog() == DialogResult.OK)
         {
            DsClient dsClient = new DsClient(false);
            ClientContact newContact = contact ?? new ClientContact();
            newContact.name = instance.tbName.Text;
            newContact.phone = instance.tbPhone.Text;

            if (contact == null)
            {
               if (client.contacts == null)
                  client.contacts = new List<ClientContact>();

               client.contacts.Add(newContact);
            }

            dsClient.Add(client.id, client);
            List<IDataSet> updSet = new List<IDataSet>();
            updSet.Add(dsClient);

            return DataModule.UpdateDataSet(updSet, null, null, Config.GetConfig().GetConnection());
         }

         return false;
      }
   }
}
