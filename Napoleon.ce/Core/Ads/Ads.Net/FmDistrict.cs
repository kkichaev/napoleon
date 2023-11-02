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
   public partial class FmDistrict : Form
   {
      static FmDistrict instance;
      private DsDistrict dsDistrict;
      private SearchEngine searchEngine;
      private Invoker gridDoubleClick;

      public FmDistrict()
      {
         InitializeComponent();
         dsDistrict = (DsDistrict)DataModule.Get(District.OBJECT_NAME) ?? new DsDistrict(true);
         searchEngine = new SearchEngine(new FindDataGridObject(dgvDistrict, 0));
      }

      internal static void ShowInstance()
      {
         ShowInstance(null);
      }

      internal static void ShowInstance(Invoker gridDoubleClick)
      {
         if (instance != null)
            instance.Close();

         instance = new FmDistrict();
         instance.gridDoubleClick = gridDoubleClick;
         instance.Show();
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         if (FmDistrictEdit.ShowInstance(null))
            btnRefresh_Click(null, null);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsDistrict);

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
         List<District> list = new List<District>();
         foreach (District d in dsDistrict.Data)
            list.Add(d);
         dgvDistrict.DataSource = list;
         DataUtils.GridSort<District>(dgvDistrict, 0, districtComparer);
      }

      private void FmDistrict_Load(object sender, EventArgs e)
      {
         btnRefresh_Click(null, null);
      }

      DistrictGridComparer districtComparer = new DistrictGridComparer();

      class DistrictGridComparer : GridBoundedObjectComparer
      {
         //public override int Compare(District d1, District d2)
         //{
         //   if (ColumnIndex == 0)
         //      return d1.Id.CompareTo(d2.Id);
         //   else if (ColumnIndex == 1)
         //      return d1.Name.CompareTo(d2.Name);

         //   return 0;
         //}
      }

      private void dgvDistrict_ColumnHeaderMouseClick(object sender, DataGridViewCellMouseEventArgs e)
      {
         searchEngine = new SearchEngine(new FindDataGridObject(dgvDistrict, e.ColumnIndex));
         DataUtils.GridSort<District>(dgvDistrict, e.ColumnIndex, districtComparer);
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         DataGridViewRow row = dgvDistrict.CurrentRow;

         if (row == null)
            return;

         District edit = (District)
                 dgvDistrict.CurrentRow.DataBoundItem;
         if (FmDistrictEdit.ShowInstance(edit))
            dgvDistrict.Refresh();
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         DataGridViewRow row = dgvDistrict.CurrentRow;

         if (row == null)
            return;

         District del = (District)
                 dgvDistrict.CurrentRow.DataBoundItem;

         if (MessageBox.Show("Запись будет удалена. Удалить?",
            "Вопрос", MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
         {
            DsDistrict dsToDel = new DsDistrict(false);
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

      private void FmDistrict_FormClosed(object sender, FormClosedEventArgs e)
      {
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

      private void dgvDistrict_DoubleClick(object sender, EventArgs e)
      {
         District district = GetSelectedDistrict();

         if (district != null && gridDoubleClick != null)
            gridDoubleClick(district);
      }

      private District GetSelectedDistrict()
      {
         DataGridViewRow row = dgvDistrict.CurrentRow;

         if (row == null)
            return null;

         return (District)row.DataBoundItem;
      }

   }
}
