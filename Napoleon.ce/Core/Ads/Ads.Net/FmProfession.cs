using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Collections;
using GRSoft.Ads.Utils;

namespace GRSoft.Ads
{
   public partial class FmProfession : Form
   {
      DsProfession dsProfession;
      EmptyInvoker postWorker;
      private SearchEngine searchEngine;

      private static FmProfession instance;

      public FmProfession()
      {
         InitializeComponent();
         dsProfession = (DsProfession)DataModule.Get(Profession.OBJECT_NAME) 
            ?? new DsProfession(true);

         searchEngine = new SearchEngine(new FindDataGridObject(dgvProfession, 0));
      }

      public static void ShowInstance()
      {
         ShowInstance(null);
      }

      public static void ShowInstance(EmptyInvoker postWorker)
      {
         if (instance == null)
         {
            instance = new FmProfession();
            instance.postWorker = postWorker;
            instance.Show();
         }
         else
            instance.Activate();
      }



      private void btnAdd_Click(object sender, EventArgs e)
      {
         if (FmProfessionEdit.ShowInstance())
            btnRefresh_Click(null, null);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsProfession);

         DataModule.OnDataResponceError += new EventDataResponseError(DataModule_OnDataResponceError);
         DataModule.DataProcessed += new EventHandler(DataModule_DataProcessed);
         DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), upd, FmWait.ProgressIndicator);
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
         List<Profession> list = new List<Profession>();
         foreach (Profession p in dsProfession.Data)
            list.Add(p);
         dgvProfession.DataSource = list;
         DataUtils.GridSort<Profession>(dgvProfession, 0, professionGridComparer);
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         DataGridViewRow row = dgvProfession.CurrentRow;

         if (row == null)
            return;

         Profession edit = (Profession)
                 dgvProfession.CurrentRow.DataBoundItem;
         if (FmProfessionEdit.ShowInstance(edit))
            dgvProfession.Refresh();
      }

      private void dgvProfession_ColumnHeaderMouseClick(object sender, DataGridViewCellMouseEventArgs e)
      {
         searchEngine = new SearchEngine(new FindDataGridObject(dgvProfession, e.ColumnIndex));
         DataUtils.GridSort<Profession>(dgvProfession, e.ColumnIndex, professionGridComparer);
      }

      ProfessionGridComparer professionGridComparer = new ProfessionGridComparer();
 
      class ProfessionGridComparer : GridBoundedObjectComparer
      {
         //public override int Compare(Profession p1, Profession p2)
         //{
         //   if (ColumnIndex == 0)
         //      return p1.Id.CompareTo(p2.Id);
         //   else if (ColumnIndex == 1)
         //      return p1.Name.CompareTo(p2.Name);

         //   return 0;
         //}
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         DataGridViewRow row = dgvProfession.CurrentRow;

         if (row == null)
            return;

         Profession del = (Profession)
                 dgvProfession.CurrentRow.DataBoundItem;

         if (MessageBox.Show("Запись будет удалена. Удалить?",
            "Вопрос", MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
         {
            DsProfession dsToDel = new DsProfession(false);
            dsToDel.Add(del.Id, del);
            List<IDataSet> delSet = new List<IDataSet>();
            delSet.Add(dsToDel);

            if (DataModule.UpdateDataSet(null, delSet, null, Config.GetConfig().GetConnection()))
            {
               btnRefresh_Click(null, null);
            }
            else MessageBox.Show("Ошибка при удалении записи");
         }
      }

      private void FmProfession_Load(object sender, EventArgs e)
      {
         btnRefresh_Click(null, null);
      }

      private void FmProfession_FormClosed(object sender, FormClosedEventArgs e)
      {
         if (postWorker != null)
            postWorker();

         instance = null;
      }

      private void btnSearchBack_Click(object sender, EventArgs e)
      {
         searchEngine.find(tbFind.Text, Direction.UP);
      }

      private void btnSearchForward_Click(object sender, EventArgs e)
      {
         searchEngine.find(tbFind.Text, Direction.DOWN);
      }

      private void tbFind_KeyDown(object sender, KeyEventArgs e)
      {
         if (e.KeyCode == Keys.Enter)
            searchEngine.find(tbFind.Text, Direction.DOWN);
      }
   }
}
