using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public partial class DivisionChief : Form
   {
      private Division division;
      protected DataSet<string, DivisionManager> dsManager = new DataSet<string,DivisionManager>(DivisionManager.OBJECT_NAME, false);

      // все менеджеры нужны для проверки логина
      private DataSet<string, DivisionManager> dsAllManagers = new DataSet<string, DivisionManager>(DivisionManager.OBJECT_NAME, false);
      //private DataSet<string, DivisionManager> dsRemoved = new DataSet<string, DivisionManager>(DivisionManager.OBJECT_NAME, false);

      DataSet<string, DivisionManager> GetDivisionManagers(int divisionID)
      {
         DataSet<string, DivisionManager> dsm = new DataSet<string, DivisionManager>(DivisionManager.OBJECT_NAME, false);

         foreach (DivisionManager m in dsAllManagers.Data)
         {
            if (m.division == divisionID)
            {
               DivisionManager dm = new DivisionManager(m);
               dsm.Add(dm.id, dm);
            }
         }

         return dsm;
      }

      public void __Initing(Division d)
      {
         division = d;
         System.Threading.Thread t = DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), 
            new object[] { dsAllManagers }, 
            null);
         t.Join();

         dsManager = GetDivisionManagers(d.id);
         
         dgvManagers.AutoGenerateColumns = false;
         BindingSource bs = new BindingSource();
         bs.DataSource = dsManager.Data;
         if (dsManager.Count == 0)
            bs.Clear();
         dgvManagers.DataSource = bs;
         btnSave.Enabled = false;

         Column1.Visible = true;
      }


      private void btnNew_Click(object sender, EventArgs e)
      {
         Agents a = Agents.GetDataSet();

         int i = dsManager.Count + 1;
         string name = "manager" + i.ToString();

         DivisionManager dm = new DivisionManager();
         dm.division = division.id;
         dm.id = Network.DataObject.GenId();
         dm.name = name;

         dsManager.Add(dm.id, dm);

         dgvManagers.AutoGenerateColumns = false;
         BindingSource bs = new BindingSource();
         bs.DataSource = dsManager.Data;
         dgvManagers.DataSource = bs;

         btnSave.Enabled = true;
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         if (dgvManagers.SelectedCells.Count == 1 && 
            MessageBox.Show("Удалить менеджера?", "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes)
         {
            //foreach (DataGridViewRow row in dgvManagers.SelectedRows)
            {
               DataGridViewRow row = dgvManagers.Rows[dgvManagers.SelectedCells[0].RowIndex];
               DivisionManager dm = row.DataBoundItem as DivisionManager;

               dsManager.Remove(dm.id);
            }

            dgvManagers.AutoGenerateColumns = false;
            BindingSource bs = new BindingSource();
            bs.DataSource = dsManager.Data;
            dgvManagers.DataSource = bs;

            btnSave.Enabled = true;
         }
      }

      private void dgvManagers_CellValueChanged(object sender, DataGridViewCellEventArgs e)
      {
         btnSave.Enabled = true;
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         List<IDataSet> wr = new List<IDataSet>();
         List<IDataSet> del = new List<IDataSet>();

         DataSet<string, DivisionManager> refDS = GetDivisionManagers(division.id);

         dgvManagers.CommitEdit(DataGridViewDataErrorContexts.Commit);
         foreach (DivisionManager dm in dsManager.Data)
         {
            refDS.Remove(dm.id);
         }

         if (dsManager.Count > 0)
            wr.Add(dsManager);

         if (refDS.Count > 0)
            del.Add(refDS);

         UpdateWriteSet(wr);

         if (DataModule.UpdateDataSet(wr, del, null, Config.GetConfig().GetConnection()))
         {
            foreach (DivisionManager m in refDS.Data)
               dsAllManagers.Remove(m.id);

            foreach (DivisionManager m in dsManager.Data)
               dsAllManagers[m.id] = m;

            btnSave.Enabled = false;
         }
      }

      protected virtual void UpdateWriteSet(List<IDataSet> wr)
      {
      }
   }
}
