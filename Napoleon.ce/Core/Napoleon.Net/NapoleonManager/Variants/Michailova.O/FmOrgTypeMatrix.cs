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
   public partial class FmOrgTypeMatrix : Form
   {
      protected DataSet<string, OrgType> dsOrgType;
      protected DataSet<string, OrgType> dsOrgTypeUpdated;
      protected DataSet<int, Matrix> dsMatrix;

      public FmOrgTypeMatrix()
      {
         InitializeComponent();

         dsOrgType = (DataSet<string, OrgType>)DataModule.Get(OrgType.OBJECT_NAME) ?? new DataSet<string, OrgType>(OrgType.OBJECT_NAME, true);
         dsOrgTypeUpdated = new DataSet<string, OrgType>(OrgType.OBJECT_NAME, false);
         dsMatrix = (DataSet<int, Matrix>)DataModule.Get(Matrix.OBJECT_NAME) ?? new DataSet<int, Matrix>(Matrix.OBJECT_NAME, true);

         grid.AutoGenerateColumns = false;
         grid.DataError += new DataGridViewDataErrorEventHandler((o, e) => { });

         btnSave.Enabled = false;
      }

      private void FmOrgTypeMatrix_Load(object sender, EventArgs e)
      {
         btnRefresh.PerformClick();
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> list = new List<IDataSet>();

         list.Add(dsOrgType);
         list.Add(dsMatrix);

         FmWait.StdDataRefresh(this, list, DoLoadData);
      }

      private void DoLoadData()
      {
         List<OrgType> data = new List<OrgType>();
         data.AddRange(dsOrgType.Values);
         data.Sort(new Comparison<OrgType>(delegate(OrgType lhs, OrgType rhs) { return lhs.name.CompareTo(rhs.name); }));
         grid.DataSource = data;

         List<Matrix> mtx = new List<Matrix>();
         mtx.AddRange(dsMatrix.Values);
         mtx.Sort(new Comparison<Matrix>(delegate(Matrix lhs, Matrix rhs) { return lhs.name.CompareTo(rhs.name); }));
         

         ColumnMatrix.Items.Clear();
         foreach (Matrix m in mtx)
            if (m != null && m.name != null && m.name.Length > 0 && m.items != null && m.items.Count > 0)
               ColumnMatrix.Items.Add(m.name);

         if(ColumnMatrix.Items.Count > 0)
            ColumnMatrix.Items.Insert(0, "");
      }

      private void grid_CellValueChanged(object sender, DataGridViewCellEventArgs e)
      {
         if (e.RowIndex > -1)
         {
            OrgType o = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as OrgType;

            if (o != null)
            {
               dsOrgTypeUpdated[o.id] = o;
               btnSave.Enabled = true;
            }
         }
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         List<IDataSet> list = new List<IDataSet>();

         if (dsOrgTypeUpdated.Count > 0)
            list.Add(dsOrgTypeUpdated);

         if (!DataModule.UpdateDataSet(list, null, null, Config.GetConfig().GetConnection()))
         {
            DialogUtil.UpdateErrMsg(this);
         }
         else
         {
            dsOrgTypeUpdated.Clear();
            btnSave.Enabled = false;
         }
      }
   }
}
