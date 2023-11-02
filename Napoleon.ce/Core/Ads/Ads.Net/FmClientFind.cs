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
   public partial class FmClientFind : Form
   {
      private string address = string.Empty;
      private DsClient dsClient;
      private Invoker postWork;

      public FmClientFind()
      {
         InitializeComponent();
         dsClient = (DsClient)DataModule.Get(Client.OBJECT_NAME) ?? new DsClient(true);
      }

      public static void ShowInstance(String address, Invoker postWork)
      {
         FmClientFind instance = new FmClientFind();
         instance.address = address;
         instance.postWork = postWork;
         instance.ShowDialog();
      }

      private void FmClientFind_Load(object sender, EventArgs e)
      {
         DataModule.SetDataRepsonceHandlers(DataModule_DataProcessed,
            DataModule_OnDataResponceError);
         dsClient.Filter = String.Format("address = '{0}'", address);
         List<IDataSet> updList = new List<IDataSet>();
         updList.Add(dsClient);

         DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
           updList, FmWait.ProgressIndicator);
      }

      void DataModule_DataProcessed(object sender, EventArgs e)
      {
         DataModule.ClearEvents();

         Invoke(new InvokeDelegate(RefreshData));
      }

      void DataModule_OnDataResponceError(EDataResponse e)
      {
         DataModule.ClearEvents();
         MessageBox.Show(e.Msg);
      }

      void RefreshData()
      {
         List<Client> list = new List<Client>();
         list.AddRange(dsClient.Values);

         dgvContact.DataSource = list;
      }

      private void dgvContact_DoubleClick(object sender, EventArgs e)
      {
         DataGridViewRow row = dgvContact.CurrentRow;

         if (row != null && postWork != null && row.DataBoundItem is Client)
         {
            postWork(row.DataBoundItem as Client);
            Close();
         }
      }
   }
}
