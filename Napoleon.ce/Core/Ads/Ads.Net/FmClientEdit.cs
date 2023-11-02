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
   public partial class FmClientEdit : Form
   {
      public FmClientEdit()
      {
         InitializeComponent();
      }

      public static Client ShowInstance(Client client)
      {
         bool addMode = client == null;
         FmClientEdit instance = new FmClientEdit();

         if (client != null)
         {
            instance.tbAddress.Text = client.address;
            instance.tbName.Text = client.name;
            instance.Text = "Изменить";
         }
         else
            instance.Text = "Добавить";

         if (instance.ShowDialog() == DialogResult.OK)
         {
            DsClient dsClient = new DsClient(false);
            Client result = client ?? new Client();
            result.name = instance.tbName.Text;
            result.address = instance.tbAddress.Text;

            DsClient dsFullClientSet = (DsClient)DataModule.Get(Client.OBJECT_NAME) ??
               new DsClient(true);


            if (client == null)
               result.id = dsFullClientSet.GetNextKey();

            dsClient.Add(result.id, result);
            List<IDataSet> updSet = new List<IDataSet>();
            updSet.Add(dsClient);

            bool insertOpStatus = false;

            /*
            if (addMode)
               insertOpStatus = DataModule.InsertDataSets(updSet, Config.GetConfig().GetConnection());
            else
             */
             
            insertOpStatus = DataModule.UpdateDataSet(updSet, null, null, Config.GetConfig().GetConnection());

            if(insertOpStatus)

            return result;
         }

         return null;
      }

      private void btnKladr_Click(object sender, EventArgs e)
      {
         FmKladr fmKladr = new FmKladr();

         if (fmKladr.ShowDialog() == DialogResult.OK)
            tbAddress.Text = fmKladr.Address;
      }

      private void FmClientEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK && tbName.Text.Trim().Length == 0)
         {
            tbName.Focus();
            MessageBox.Show("Введите наименование", "Ошибка", 
               MessageBoxButtons.OK, MessageBoxIcon.Error);
            e.Cancel = true;
         }
      }
   }
}
