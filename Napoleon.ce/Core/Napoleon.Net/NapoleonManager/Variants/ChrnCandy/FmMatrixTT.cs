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
   public partial class FmMatrixTT : Form
   {
      private DataSet<string, OrgMatrix> dsOrgMatrix;
      private DataSet<string, OrgType> dsType;
      private DataSet<int, Matrix> dsMatrix;

      public FmMatrixTT()
      {
         InitializeComponent();

         dsOrgMatrix = (DataSet<string, OrgMatrix>)DataModule.Get(OrgMatrix.OBJECT_NAME) ?? new DataSet<string, OrgMatrix>(OrgMatrix.OBJECT_NAME);
         dsType = (DataSet<string, OrgType>)DataModule.Get(OrgType.OBJECT_NAME) ?? new DataSet<string, OrgType>(OrgType.OBJECT_NAME);
         dsMatrix = (DataSet<int, Matrix>)DataModule.Get(Matrix.OBJECT_NAME) ?? new DataSet<int, Matrix>(Matrix.OBJECT_NAME);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         btnSave.Enabled = false;

         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsType);
         list.Add(dsOrgMatrix);
         list.Add(dsMatrix);

         FmWait.StdDataRefresh(this, list, DoLoadData);
      }

      private void DoLoadData()
      {
         List<OrgMatrix> list = new List<OrgMatrix>();
         foreach(OrgType t in dsType.Values)
            if (!dsOrgMatrix.ContainsKey(t.id))
            {
               OrgMatrix om = new OrgMatrix();
               om.id = t.id;
               om.type = t;
               dsOrgMatrix[t.id] = om;
            }

         list.AddRange(dsOrgMatrix.Values);
         list.Sort((x, y) => { return x.Name.CompareTo(y.Name); });

         List<Matrix> mtx = new List<Matrix>();
         mtx.AddRange(dsMatrix.Values);
         mtx.Sort(new Comparison<Matrix>(delegate(Matrix lhs, Matrix rhs) { return lhs.name.CompareTo(rhs.name); }));
         Matrix empty = new Matrix();
         mtx.Insert(0, empty);

         Column2.Items.Clear();
         foreach (Matrix m in mtx)
            if (m != null && m.name != null)
               Column2.Items.Add(m.name);

         grid.DataSource = list;
      }

      private void FmMatrixTT_Shown(object sender, EventArgs e)
      {
         btnRefresh.PerformClick();
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         Save();
      }

      private bool Save()
      {
         List<IDataSet> wr = new List<IDataSet>();
         wr.Add(dsOrgMatrix);

         bool result = DataModule.UpdateDataSet(wr, null, null, Config.GetConfig().GetConnection());
         if (result)
            btnSave.Enabled = false;
         else
            DialogUtil.UpdateErrMsg(this);

         return result;
      }

      private void grid_CellEndEdit(object sender, DataGridViewCellEventArgs e)
      {
         btnSave.Enabled = true;
      }

      private void FmMatrixTT_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (btnSave.Enabled)
            e.Cancel = !Save();
      }
   }
}
