using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmReturnCauseEdit : Form
   {
      private DataSet<string, ReturnCause> dsReturnCause;
      private DataSet<string, ReturnCause> dsDelReturnCause;

      public static void Open()
      {
         new FmReturnCauseEdit().Show();
      }

      public FmReturnCauseEdit()
      {
         InitializeComponent();

         dsReturnCause = (DataSet<string, ReturnCause>)DataModule.Get(ReturnCause.OBJECT_NAME) ?? new DataSet<string, ReturnCause>(ReturnCause.OBJECT_NAME);
         dsDelReturnCause = new DataSet<string, ReturnCause>(ReturnCause.OBJECT_NAME);

         BindingList<ReturnCause> data = new BindingList<ReturnCause>();
         grid.DataSource = data;

         btnSave.Enabled = false;
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         bool ret = Save();         

         if (ret)
         {
            DialogUtil.SavedGood(this);
            dsDelReturnCause.Clear();
            btnSave.Enabled = false;
         }
         else
            DialogUtil.UpdateErrMsg(this);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsReturnCause);
         FmWait.StdDataRefresh(this, list, DoLoadData);
      }

      private void DoLoadData()
      {
         dsDelReturnCause.Clear();
         BindingList<ReturnCause> data = new BindingList<ReturnCause>();

         foreach (ReturnCause cause in dsReturnCause.Data)
            data.Add(cause);

         grid.DataSource = data;
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         DataGridViewRow row = grid.CurrentRow;
         if (row != null && DialogUtil.AskToDel(this))
         {
            ReturnCause cause = row.DataBoundItem as ReturnCause;

            if (cause != null)
            {
               if (cause.id.Length > 0)
                  dsDelReturnCause.Add(cause.id, cause);

               grid.Rows.Remove(row);

               btnSave.Enabled = true;
            }
         }
      }

      private void FmReturnCauseEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (btnSave.Enabled && DialogUtil.AskToSave(this))
            Save();
      }

      private bool Save()
      {
         bool result = false;
         BindingList<ReturnCause> list = grid.DataSource as BindingList<ReturnCause>;

         if (list != null)
         {
            DataSet<string, ReturnCause> upd = new DataSet<string, ReturnCause>(ReturnCause.OBJECT_NAME, false);

            foreach (ReturnCause cause in list)
            {
               if (cause.agent.Trim().Length == 0 && cause.report.Trim().Length == 0)
                  continue;

               if (cause.id.Length == 0)
                  cause.id = GRSoft.Network.DataObject.GenId();

               upd.Add(cause.id, cause);
            }

            List<IDataSet> wrSet = new List<IDataSet>();

            if (upd.Count > 0)
               wrSet.Add(upd);

            List<IDataSet> rmSet = new List<IDataSet>();

            if (dsDelReturnCause.Count > 0)
               rmSet.Add(dsDelReturnCause);

            result = DataModule.UpdateDataSet(wrSet, rmSet, null, Config.GetConfig().GetConnection());
         }

         return result;
      }

      private void grid_CellValueChanged(object sender, DataGridViewCellEventArgs e)
      {
         btnSave.Enabled = true;
      }

      private void grid_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         DataGridViewCell cell = grid.CurrentCell;
         if (cell != null && cell.ColumnIndex == 1)
         {
            grid.CommitEdit(DataGridViewDataErrorContexts.Commit);
            btnSave.Enabled = true;
         }
      }

      private void FmReturnCauseEdit_Load(object sender, EventArgs e)
      {
         btnRefresh.PerformClick();
      }
   }
}
