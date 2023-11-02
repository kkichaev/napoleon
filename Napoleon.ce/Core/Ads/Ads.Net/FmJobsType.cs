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
   public partial class FmJobsType : Form
   {
      private static FmJobsType instance;
      private DsJobType dsJobType;
      private SearchEngine searchEngine;
      private Invoker gridDoubleClick;
      private JobType selectedJobType;

      public FmJobsType()
      {
         InitializeComponent();
         dsJobType = (DsJobType)DataModule.Get(JobType.OBJECT_NAME) ?? new DsJobType(true);
      }

      internal static void ShowInstance(JobType selectedJobType, Invoker gridDoubleClick)
      {
         if (instance != null)
            instance.Close();

         instance = new FmJobsType();
         instance.selectedJobType = selectedJobType;
         instance.gridDoubleClick = gridDoubleClick;
         instance.Show();
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsJobType);

         DataModule.OnDataResponceError += new EventDataResponseError(DataModule_OnDataResponceError);
         DataModule.DataProcessed += new EventHandler(DataModule_DataProcessed);
         FmWait.ShowForm(this,
            DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
            upd, FmWait.ProgressIndicator));
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

      TypeGridComparer typeComparer = new TypeGridComparer();

      class TypeGridComparer : GridBoundedObjectComparer
      {
         //public override int Compare(JobType x, JobType y)
         //{
         //   if (ColumnIndex == 0)
         //      return x.name.CompareTo(y.name);
         //   else if (ColumnIndex == 1)
         //      return x.color.CompareTo(y.color);

         //   return 0;
         //}
      }

      void RefreshData()
      {
         List<JobType> list = new List<JobType>();

         foreach (JobType d in dsJobType.Data)
            list.Add(d);

         dgvTypes.DataSource = list;

         if (searchEngine == null)
         {
            int index = dgvTypes.Columns[0].DisplayIndex;
            searchEngine = new SearchEngine(new FindDataGridObject(dgvTypes, index));
            DataUtils.GridSort<JobType>(dgvTypes, index, typeComparer);
         }

         if (selectedJobType != null)
            foreach (DataGridViewRow row in dgvTypes.Rows)
            {
               JobType jt = row.DataBoundItem as JobType;

               if (jt != null && jt.id == selectedJobType.id)
               {
                  dgvTypes.CurrentCell = dgvTypes.Rows[row.Index].Cells[0];
                  break;
               }
            }
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         if (FmJobsTypeEdit.ShowInstance(null))
            btnRefresh_Click(null, null);
      }

      private void FmJobsType_Load(object sender, EventArgs e)
      {
         btnRefresh_Click(null, null);
      }

      private void dgvTypes_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         if (e.ColumnIndex == 0 && e.RowIndex != -1)
         {
            e.CellStyle.BackColor = ((JobType)dgvTypes.Rows[e.RowIndex].DataBoundItem).Color;
            e.Value = "";
         }
         else
            e.CellStyle.BackColor = Color.White;
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         DataGridViewRow curRow = dgvTypes.CurrentRow;

         if (curRow == null)
            return;

         JobType jobType = (JobType)curRow.DataBoundItem;

         if (FmJobsTypeEdit.ShowInstance(jobType))
            dgvTypes.Refresh();
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         DataGridViewRow row = dgvTypes.CurrentRow;

         if (row == null)
            return;

         JobType del = (JobType)
                 dgvTypes.CurrentRow.DataBoundItem;

         if (MessageBox.Show("Запись будет удалена. Удалить?",
            "Вопрос", MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
         {
            DsJobType dsToDel = new DsJobType(false);
            dsToDel.Add(del.id, del);
            List<IDataSet> delSet = new List<IDataSet>();
            delSet.Add(dsToDel);

            if (DataModule.UpdateDataSet(null, delSet, null, Config.GetConfig().GetConnection()))
            {
               btnRefresh_Click(null, null);
            }
            else MessageBox.Show("Ошибка при удалении записи");
         }
      }

      private void dgvTypes_ColumnHeaderMouseClick(object sender, DataGridViewCellMouseEventArgs e)
      {
         int column = e.ColumnIndex;
         SelectColumnt(column);
      }

      private void SelectColumnt(int column)
      {
         searchEngine = new SearchEngine(new FindDataGridObject(dgvTypes, column));
         DataUtils.GridSort<JobType>(dgvTypes, column, typeComparer);
      }

      private void btnSearchBack_Click(object sender, EventArgs e)
      {
         searchEngine.find(tbFind.Text, Direction.UP);
      }

      private void btnSearchForward_Click(object sender, EventArgs e)
      {
         searchEngine.find(tbFind.Text, Direction.DOWN);
      }

      private void dgvTypes_DoubleClick(object sender, EventArgs e)
      {
         JobType jobType = dgvTypes.CurrentRow != null 
            ? (JobType)dgvTypes.CurrentRow.DataBoundItem : null;

         if (jobType != null && gridDoubleClick != null)
         {
            gridDoubleClick(jobType);
            Close();
         }
      }
   }
}
