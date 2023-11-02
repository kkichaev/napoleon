using GRSoft.NapoleonManager.Utils;
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
   public partial class FmDMPEdit : Form
   {
      private DataSet<string, DMPType> dsDMPType;
      private DataSet<string, DMPType> dsChanged;
      private SortableBindingList<DMPType> data;

      public FmDMPEdit()
      {
         InitializeComponent();

         dsDMPType = (DataSet<string, DMPType>) DataModule.Get(DMPType.OBJECT_NAME) ?? new DataSet<string, DMPType>(DMPType.OBJECT_NAME);
         dsChanged = new DataSet<string, DMPType>(DMPType.OBJECT_NAME, false);

         data = new SortableBindingList<DMPType>();
         data.AddingNew += data_AddingNew;

         grid.DataSource = data;
         btnSave.Enabled = false;

      }

      void data_AddingNew(object sender, AddingNewEventArgs e)
      {
         DMPType r = new DMPType();
         r.id = DMPType.GenId();
         r.text = "";
         e.NewObject = r;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsDMPType);

         FmWait.StdDataRefresh(this, list, DoLoadData);
      }

      private void DoLoadData()
      {
         dsChanged.Clear();
         data.Clear();
         
         foreach ( DMPType r in dsDMPType.Values)
            data.Add(r);

         grid.Sort(grid.Columns[0], ListSortDirection.Ascending);
      }

      private void grid_CellValueChanged(object sender, DataGridViewCellEventArgs e)
      {
         if (e.RowIndex != -1)
         {
            DMPType r = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as DMPType;

            if(r != null)
            {
               dsChanged[r.id] = r;
               btnSave.Enabled = true;
            }
         }
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         grid.CommitEdit(DataGridViewDataErrorContexts.Commit);

         DataSet<string, DMPType> toWrite = new DataSet<string, DMPType>(DMPType.OBJECT_NAME, false);

         foreach (DMPType r in dsChanged.Values)
            if (r.text.Trim().Length > 0)
               toWrite[r.id] = r;

         if (toWrite.Count > 0)
         {
            List<IDataSet> upd = new List<IDataSet>();
            upd.Add(toWrite);

            if (!DataModule.WriteDataSet(upd, Config.GetConfig().GetConnection()))
               DialogUtil.UpdateErrMsg(this);
            else
            {
               btnSave.Enabled = false;
               dsChanged.Clear();
            }
         }
      }

      private void FmReturnCauseEdit_Load(object sender, EventArgs e)
      {
         btnRefresh.PerformClick();
      }

      private void btnDelete_Click(object sender, EventArgs e)
      {
         DataGridViewCell c = grid.CurrentCell;

         if (c != null && DialogUtil.AskToDel(this))
         {
            DMPType r = grid.Rows[c.RowIndex].DataBoundItem as DMPType;

            if (r != null)
            {
               r.rem = 1;
               dsChanged[r.id] = r;
               data.Remove(r);
               btnSave.Enabled = true;
            }
         }
      }
   }
}
