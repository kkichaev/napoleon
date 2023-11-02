using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.Ads.Utils;

namespace GRSoft.Ads
{
   public delegate void PostClientUpdate(Client client);

   public partial class FmClient : Form
   {
      private static FmClient instance;
      private DsClient dsClient;
      private Client selectedClient;
      private Invoker gridDoubleClick;
      private SearchEngine searchEngineClient;
      private SearchEngine searchEngineContact;

      public FmClient()
      {
         InitializeComponent();
         dsClient = (DsClient)DataModule.Get(Client.OBJECT_NAME) ?? new DsClient(true);
         searchEngineClient = new SearchEngine(new FindDataGridObject(dgvClient, 0));
         searchEngineContact = new SearchEngine(new FindDataGridObject(dgvContact, 0));
      }

      public static void ShowInstance()
      {
         ShowInstance(null);
      }

      public static void ShowInstance(Invoker gridDoubleClick)
      {
         bool restoreProp = instance != null;

         if (instance != null)
            instance.Close();

         instance = new FmClient();
         instance.gridDoubleClick = gridDoubleClick;
         instance.Show();
      }

      private void FmClient_FormClosed(object sender, FormClosedEventArgs e)
      {
         instance = null;
      }

      private void btnAddClient_Click(object sender, EventArgs e)
      {
         selectedClient = FmClientEdit.ShowInstance(null);
         if (selectedClient != null)
            RefreshData(false);
      }

      void RefreshData(bool rememberClient)
      {
         if( rememberClient )
            RememberClient();

         List<IDataSet> updSet = new List<IDataSet>();
         updSet.Add(dsClient);

         DataModule.SetDataRepsonceHandlers(DataModule_DataProcessed,
            DataModule_OnDataResponceError);
         DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
            updSet, FmWait.ProgressIndicator);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         RefreshData(true);
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

         foreach (Client client in dsClient.Data)
            list.Add(client);

         dgvClient.DataSource = list;
         DataUtils.GridSort<Client>(dgvClient, 0, clientComparer);
         DataUtils.GridSort<ClientContact>(dgvContact,0,contactComparer);
         SelectClient();
         dgvClient_SelectionChanged(null, null);
      }

      private void FmClient_Load(object sender, EventArgs e)
      {
         btnRefresh_Click(null, null);
      }

      private void btnAddContact_Click(object sender, EventArgs e)
      {
         DataGridViewRow clientRow = dgvClient.CurrentRow;

         if (clientRow == null)
            return;

         Client client = (Client)clientRow.DataBoundItem;

         if (FmClientContactEdit.ShowInstance(client, null))
            dgvClient_SelectionChanged(null, null);
      }

      private void dgvClient_SelectionChanged(object sender, EventArgs e)
      {
         DataGridViewRow row = dgvClient.CurrentRow;
         List<ClientContact> list = new List<ClientContact>();

         if (row != null)
         {
            Client client = (Client)row.DataBoundItem;

            if (client != null)
               list.AddRange(client.contacts);
         }

         dgvContact.DataSource = list;
         DataUtils.GridSort<ClientContact>(dgvContact, 0, contactComparer);
      }

      private void SelectClient()
      {
         if (selectedClient != null)
         {
            foreach (DataGridViewRow row in dgvClient.Rows)
            {
               Client client = (Client)row.DataBoundItem;

               if (client != null && client.id == selectedClient.id &&
                  dgvClient.CurrentCell != row.Cells[0])
               {
                  dgvClient.CurrentCell = row.Cells[0];
                  dgvClient_SelectionChanged(null, null);
               }

            }
         }
      }

      private void RememberClient()
      {
         if (dgvClient.CurrentRow != null)
            selectedClient = (Client)dgvClient.CurrentRow.DataBoundItem;
         else
            selectedClient = null;
      }

      private void btnEditClient_Click(object sender, EventArgs e)
      {
         DataGridViewRow row = dgvClient.CurrentRow;

         if (row == null)
            return;

         Client client = (Client)row.DataBoundItem;
         
         if (FmClientEdit.ShowInstance(client) != null)
            btnRefresh_Click(null, null);
      }

      private void btnDelClient_Click(object sender, EventArgs e)
      {
         DataGridViewRow row = dgvClient.CurrentRow;

         if (row == null)
            return;

         Client del = (Client)dgvClient.CurrentRow.DataBoundItem;

         if (MessageBox.Show("Запись будет удалена. Удалить?",
            "Вопрос", MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
         {
            DsClient dsToDel = new DsClient(false);
            dsToDel.Add(del.id, del);
            List<IDataSet> delSet = new List<IDataSet>();
            delSet.Add(dsToDel);

            if (DataModule.UpdateDataSet(null, delSet, null, Config.GetConfig().GetConnection()))
            {
               dsClient.Remove(del.id);
               RefreshData(false);
            }
            else MessageBox.Show("Ошибка при удалении записи");
         }
      }

      private void btnEditContact_Click(object sender, EventArgs e)
      {

         DataGridViewRow clientRow = dgvClient.CurrentRow;

         if (clientRow == null)
            return;

         DataGridViewRow contactRow = dgvContact.CurrentRow;

         if (contactRow == null)
            return;

         Client client = (Client)clientRow.DataBoundItem;
         ClientContact contact = (ClientContact)contactRow.DataBoundItem;

         if (FmClientContactEdit.ShowInstance(client, contact))
            dgvClient_SelectionChanged(null, null);
      }

      private void btnDelContact_Click(object sender, EventArgs e)
      {
         DataGridViewRow clientRow = dgvClient.CurrentRow;

         if (clientRow == null)
            return;

         DataGridViewRow contactRow = dgvContact.CurrentRow;

         if (contactRow == null)
            return;

         Client client = (Client)clientRow.DataBoundItem;
         ClientContact contact = (ClientContact)contactRow.DataBoundItem;


         if (MessageBox.Show("Запись будет удалена. Удалить?",
            "Вопрос", MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
         {
            DsClient dsClient = new DsClient(false);
            client.contacts.Remove(contact);
            dsClient.Add(client.id, client);

            List<IDataSet> updSet = new List<IDataSet>();
            updSet.Add(dsClient);

            if (DataModule.UpdateDataSet(updSet, null, null, Config.GetConfig().GetConnection()))
            {
               btnRefresh_Click(null, null);
            }
            else MessageBox.Show("Ошибка при удалении записи");
         }
      }

      private ClientGricComparer clientComparer = new ClientGricComparer();

      class ClientGricComparer : GridBoundedObjectComparer
      {
         //public override int Compare(Client c1, Client c2)
         //{
         //   if (ColumnIndex == 0)
         //      return c1.Name.CompareTo(c2.Name);
         //   else if (ColumnIndex == 1)
         //      return c1.Address.CompareTo(c2.Address);

         //   return 0;
         //}
      }

      private ContactGridComparer contactComparer = new ContactGridComparer();

      class ContactGridComparer : GridBoundedObjectComparer
      {
         //public override int Compare(ClientContact c1, ClientContact c2)
         //{
         //   if (ColumnIndex == 0)
         //      return c1.Name.CompareTo(c2.Name);
         //   else if (ColumnIndex == 1)
         //      return c1.Phone.CompareTo(c2.Phone);

         //   return 0;
         //}
      }

      private void dgvClient_ColumnHeaderMouseClick(object sender, DataGridViewCellMouseEventArgs e)
      {
         searchEngineClient = new SearchEngine(new FindDataGridObject(dgvClient, e.ColumnIndex));
         DataUtils.GridSort<Client>(dgvClient, e.ColumnIndex, clientComparer);
      }

      private void dgvContact_ColumnHeaderMouseClick(object sender, DataGridViewCellMouseEventArgs e)
      {
         searchEngineContact = new SearchEngine(new FindDataGridObject(dgvContact, e.ColumnIndex));
         DataUtils.GridSort<ClientContact>(dgvContact, e.ColumnIndex, contactComparer);
      }

      private void dgvClient_DoubleClick(object sender, EventArgs e)
      {
         if (gridDoubleClick != null)
         {
            DataGridViewRow row = dgvClient.CurrentRow;

            if (row != null)
            {
               Client client = (Client)row.DataBoundItem;
               gridDoubleClick(client);
               Close();
            }
         }
      }

      private void btnSearchBack_Click(object sender, EventArgs e)
      {
         searchEngineClient.find(tbFind.Text, Direction.UP);
      }

      private void btnSearchForward_Click(object sender, EventArgs e)
      {
         searchEngineClient.find(tbFind.Text, Direction.DOWN);
      }

      private void tbFind_KeyDown(object sender, KeyEventArgs e)
      {
         if (e.KeyCode == Keys.Enter)
            searchEngineClient.find(tbFind.Text, Direction.DOWN);
      }

      private void btnFindContactPrev_Click(object sender, EventArgs e)
      {
         searchEngineContact.find(tbFind.Text, Direction.UP);
      }

      private void btnFindContactNext_Click(object sender, EventArgs e)
      {
         searchEngineContact.find(tbFind.Text, Direction.DOWN);
      }

      private void tbFindContact_KeyDown(object sender, KeyEventArgs e)
      {
         if (e.KeyCode == Keys.Enter)
            searchEngineContact.find(tbFind.Text, Direction.DOWN);
      }
   }
}
