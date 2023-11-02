using GRSoft.NapoleonManager.Properties;
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
   public partial class AdminControl : Form
   {
      private DataSet<string, DivisionManager> dsManager;
      private DBConnection conn;

      public AdminControl(DBConnection conn)
      {
         InitializeComponent();
         this.conn = conn;
         dsManager = (DataSet<string, DivisionManager>)DataModule.Get(DivisionManager.OBJECT_NAME) ?? new DataSet<string, DivisionManager>(DivisionManager.OBJECT_NAME);
         btnSave.Enabled = false;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsManager);

         FmWait.StdDataRefresh(this, upd, DoLoadData, btnRefresh, conn);
      }

      private void DoLoadData()
      {
         BindingList<DivisionManager> list = new BindingList<DivisionManager>();
         foreach (DivisionManager m in dsManager.Data)
            list.Add(m);

         grid.DataSource = list;
      }

      private void AdminControl_Load(object sender, EventArgs e)
      {
         DoLoadData();
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         grid.CommitEdit(DataGridViewDataErrorContexts.Commit);

         DataError err = CheckValidData();
         if (err != DataError.None)
            ShowErrorMessage(err);
         else
         {
            if (Save())
            {
               DialogUtil.SavedGood(this);
               btnSave.Enabled = false;
               UpdateManagerPrefix();
            }
            else
               DialogUtil.UpdateErrMsg(this);
         }
      }

      private void UpdateManagerPrefix()
      {
         BindingList<DivisionManager> list = grid.DataSource as BindingList<DivisionManager>;

         if (list != null)
         {
            Config cfg = Config.GetConfig();
            foreach (DivisionManager d in list)
            {
               if (d.login.Equals(d.login))
                  FmMain.managerPrefix = d.prefix;
            }
         }
      }

      private void grid_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         btnSave.Enabled = true;
      }

      private void grid_UserDeletedRow(object sender, DataGridViewRowEventArgs e)
      {
         btnSave.Enabled = true;
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         DataGridViewRow row = grid.CurrentRow;

         if (row != null)
         {
            int idx = row.Index;

            if (idx >= 0 && DialogUtil.AskToDel(this))
            {
               grid.Rows.RemoveAt(idx);
               btnSave.Enabled = true;
            }
         }
      }

      private bool Save()
      {
         grid.CommitEdit(DataGridViewDataErrorContexts.Commit);

         DataSet<string, DivisionManager> saved = new DataSet<string, DivisionManager>(DivisionManager.OBJECT_NAME, false);

         foreach (DataGridViewRow row in grid.Rows)
         {
            DivisionManager m = row.DataBoundItem as DivisionManager;

            if (m != null)
            {
               int DIVISIONID = 1;
               m.division = DIVISIONID;
               saved.Add(m.login, m);
            }
         }

         DataSet<string, DivisionManager> removed = new DataSet<string, DivisionManager>(DivisionManager.OBJECT_NAME, false);

         foreach (DivisionManager m in dsManager.Data)
            if (!saved.ContainsKey(m.login))
               removed.Add(m.login, m);

         List<IDataSet> wrSet = new List<IDataSet>();

         if (saved.Count > 0)
            wrSet.Add(saved);

         List<IDataSet> rmvSet = new List<IDataSet>();

         if (removed.Count > 0)
            rmvSet.Add(removed);

         return DataModule.UpdateDataSet(wrSet, rmvSet, null, conn);
      }

      private void AdminControl_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (btnSave.Enabled && DialogUtil.AskToSave(this))
         {
            DataError err = CheckValidData();

            if (err == DataError.None)
               if(Save())
                  UpdateManagerPrefix();
            else
            {
               e.Cancel = true;
               ShowErrorMessage(err);
            }
         }
      }

      private void ShowErrorMessage(DataError err)
      {
         string text = string.Empty;

         if (err == DataError.PrfDub)
            text = Resources.prefixdub;
         else if (err == DataError.PrfMis)
            text = Resources.prefixmissing;

         MessageBox.Show(this, text, Resources.error, MessageBoxButtons.OK, MessageBoxIcon.Error);
      }

      private enum DataError { None, PrfMis, PrfDub }

      private DataError CheckValidData()
      {
         DataError result = DataError.None;

         List<string> prefixes = new List<string>();

         BindingList<DivisionManager> list = (BindingList<DivisionManager>)grid.DataSource;

         foreach (DivisionManager d in list)
         {
            if (d.Login.Trim().Length == 0 || d.Password.Trim().Length == 0)
               continue;

            if (d.Prefix.Trim().Length == 0)
            {
               result = DataError.PrfMis;
               break;
            }
            else if (prefixes.Contains(d.Prefix))
            {
               result = DataError.PrfDub;
               break;
            }
            else
               prefixes.Add(d.Prefix);
         }

         return result;
      }
   }
}
