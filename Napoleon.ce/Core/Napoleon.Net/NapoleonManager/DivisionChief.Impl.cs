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
               dsm.Add(dm.login, dm);
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

      private void dgvManagers_CellValidating(object sender, DataGridViewCellValidatingEventArgs e)
      {
         if (e.ColumnIndex == login.DisplayIndex)
         {
            string newLogin = (string)e.FormattedValue;
            DivisionManager mgr = dgvManagers.Rows[e.RowIndex].DataBoundItem as DivisionManager;
            if( mgr != null && mgr.login != newLogin && dsAllManagers.ContainsKey(newLogin) != false)
            {
               MessageBox.Show("Логин совпадает с введенным ранее");
               e.Cancel = true;
            }

            
         }
      }

      private void btnNew_Click(object sender, EventArgs e)
      {
         Agents a = Agents.GetDataSet();

         string login;
         int i = dsManager.Count + 1;
         for ( ; ; i++)
         {
            login = "manager" + i.ToString();
            if (dsAllManagers.ContainsKey(login) == false && dsManager.ContainsKey(login) == false && a.ContainsKey(login) == false )
               break;
         }

         DivisionManager dm = new DivisionManager();
         dm.division = division.id;
         dm.login = login;
         dm.password = dm.login;
#if EuroasiaTD
         dm.guid = Guid.NewGuid().ToString().Replace("-", "");
#endif
         dsManager.Add(dm.login, dm);

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

               dsManager.Remove(dm.login);
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
         Agents ads = Agents.GetDataSet();

         foreach (DivisionManager m in dsManager.Data)
         {
            string login = m.login;
            foreach (Agent a in ads.Data)
            {
               if (a.login.ToLower().Trim().Equals(login.ToLower().Trim()))
               {
                  MessageBox.Show("Логин менеджера " + login + " совпадает с логином агента, "
                   + "сохранить невозможно, исправьте и повторие попытку");
                  return;
               }
            }
         }

         List<IDataSet> wr = new List<IDataSet>();
         List<IDataSet> del = new List<IDataSet>();

         DataSet<string, DivisionManager> refDS = GetDivisionManagers(division.id);

         dgvManagers.CommitEdit(DataGridViewDataErrorContexts.Commit);
         foreach (DivisionManager dm in dsManager.Data)
         {
            refDS.Remove(dm.login);
         }

         if (dsManager.Count > 0)
            wr.Add(dsManager);

         if (refDS.Count > 0)
            del.Add(refDS);

         UpdateWriteSet(wr);

         if (DataModule.UpdateDataSet(wr, del, null, Config.GetConfig().GetConnection()))
         {
            foreach (DivisionManager m in refDS.Data)
               dsAllManagers.Remove(m.login);

            foreach (DivisionManager m in dsManager.Data)
               dsAllManagers[m.login] = m;

            btnSave.Enabled = false;
         }
      }

      protected virtual void UpdateWriteSet(List<IDataSet> wr)
      {
      }
   }
}
