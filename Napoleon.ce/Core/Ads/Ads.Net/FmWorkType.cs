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
   public partial class FmWorkType : Form
   {
      private DsWorkType dsWorkType;

      public FmWorkType()
      {
         InitializeComponent();

         dsWorkType = (DsWorkType)DataModule.Get(WorkType.OBJECT_NAME) ?? new DsWorkType(true);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> updSet = new List<IDataSet>();
         updSet.Add(dsWorkType);

         DataModule.SetDataRepsonceHandlers(DataModule_DataProcessed,
                     DataModule_OnDataResponceError);

         FmWait.ShowForm(this,
            DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
               updSet, FmWait.ProgressIndicator));
      }

      void DataModule_DataProcessed(object sender, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         Invoke(new InvokeDelegate(RefreshData));
      }

      void DataModule_OnDataResponceError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         MessageBox.Show(e.Msg);
      }

      private void RefreshData()
      {
         List<WorkType> list = new List<WorkType>();
         list.AddRange(dsWorkType.Values);
         dgvWorkType.DataSource = list;
      }

      private void FmWorkType_Load(object sender, EventArgs e)
      {
         btnRefresh_Click(null, null);
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         if (FmWorkTypeEdit.ShowInstance(null))
            btnRefresh_Click(null, null);
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         DataGridViewRow row = dgvWorkType.CurrentRow;

         if (row != null)
         {
            WorkType wt = row.DataBoundItem as WorkType;

            if (wt != null)
            {
               if (FmWorkTypeEdit.ShowInstance(wt))
                  btnRefresh_Click(null, null);
            }
         }
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         DataGridViewRow row = dgvWorkType.CurrentRow;

         if (row == null)
            return;

         WorkType wt = row.DataBoundItem as  WorkType;

         if (MessageBox.Show("Запись будет удалена. Удалить?",
            "Вопрос", MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
         {
            DsWorkType dsToDel = new DsWorkType(false);
            dsToDel.Add(wt.Id, wt);
            List<IDataSet> delSet = new List<IDataSet>();
            delSet.Add(dsToDel);

            if (DataModule.UpdateDataSet(null, delSet, null, Config.GetConfig().GetConnection()))
            {
               btnRefresh_Click(null, null);
            }
            else MessageBox.Show("Ошибка при удалении записи");
         }
      }
   }
}
