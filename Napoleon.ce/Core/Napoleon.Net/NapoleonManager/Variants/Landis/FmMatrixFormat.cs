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
   public partial class FmMatrixFormat : Form
   {
      public DataSet<int, OrderAddConfig> dsConfig = new DataSet<int, OrderAddConfig>(OrderAddConfig.OBJECT_NAME);
      public DataSet<string, MatrixFmt> dsMatrixFmt = new DataSet<string, MatrixFmt>(MatrixFmt.OBJECT_NAME);
      public DataSet<string, Matrix> dsMatrix = new DataSet<string, Matrix>(Matrix.OBJECT_NAME);

      public FmMatrixFormat()
      {
         InitializeComponent();
      }

      private void FmMatrixFormat_Load(object sender, EventArgs e)
      {
         btnRefresh.PerformClick();
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsConfig);
         upd.Add(dsMatrixFmt);
         upd.Add(dsMatrix);

         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      private void DoLoadData()
      {
         foreach (OrderAddConfig cc in dsConfig.Values)
         {
            if (cc.key.Equals("ФорматТТ"))
            {
               FillGrid(cc.value.Split(';'));
            }
         }
      }

      private void FillGrid(string[] fmt)
      {
         BindingList<MatrixFmt> data = new BindingList<MatrixFmt>();
         Array.Sort(fmt);

         foreach (string f in fmt){
            MatrixFmt m = new MatrixFmt();
            m.format = f;
            if (dsMatrixFmt.ContainsKey(f))
               m.matrix = dsMatrixFmt[f].matrix;

            data.Add(m);
         }

         dgvMatrix.Items.Clear();

         string[] mtx = new string[dsMatrix.Count + 1];
         mtx[0] = string.Empty;


         int idx = 1;
         foreach (string k in dsMatrix.Keys)
            mtx[idx++] = k;

         Array.Sort(mtx);

         dgvMatrix.Items.AddRange(mtx);

         grid.DataSource = data;
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         grid.CommitEdit(DataGridViewDataErrorContexts.Commit);
         Save();
      }

      private void Save()
      {
         DataSet<string, MatrixFmt> ds = new DataSet<string, MatrixFmt>(MatrixFmt.OBJECT_NAME, false);
         BindingList<MatrixFmt> data  = (BindingList<MatrixFmt>)grid.DataSource;

         foreach (MatrixFmt mf in data)
            ds.Add(mf.format, mf);

         List<IDataSet>  wr = new List<IDataSet>();
         wr.Add(ds);

         if(!DataModule.WriteDataSet(wr, Config.GetConfig().GetConnection()))
            DialogUtil.UpdateErrMsg(this);
      }
   }
}
