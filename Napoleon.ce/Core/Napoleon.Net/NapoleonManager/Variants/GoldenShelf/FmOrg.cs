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
   public partial class FmOrg : Form
   {
      private DataSet<string, Org> dsOrg;
      private DataSet<string, Org> dsRemOrg = new DataSet<string, Org>(Org.OBJECT_NAME, false);
      private DataSet<string, Slsnet> dsSlsnet;
      private DataSet<string, City> dsCity;

      private BindingList<Org> datasource = new BindingList<Org>();

      public FmOrg()
      {
         InitializeComponent();
         btnSave.Enabled = false;
         grid.AutoGenerateColumns = false;
         dsOrg = (DataSet<string, Org>)DataModule.Get(Org.OBJECT_NAME) ?? new DataSet<string, Org>(Org.OBJECT_NAME);
         dsSlsnet = (DataSet<string, Slsnet>)DataModule.Get(Slsnet.OBJECT_NAME) ?? new DataSet<string, Slsnet>(Slsnet.OBJECT_NAME);
         dsCity = (DataSet<string, City>)DataModule.Get(City.OBJECT_NAME) ?? new DataSet<string, City>(City.OBJECT_NAME);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsSlsnet);
         upd.Add(dsCity);
         upd.Add(dsOrg);

         dsOrg.Filter = "\"id\" is null or \"id\" is not null";
         FmWait.StdDataRefresh(this, upd, DoLoadData, btnRefresh);
      }

      private void DoLoadData()
      {
         datasource.Clear();
         foreach(Org o in dsOrg.Values)
            datasource.Add(o);
         grid.DataSource = datasource;
         btnSave.Enabled = false;
      }

      private bool Save()
      {
         List<IDataSet> wrSet = new List<IDataSet>();

         if(dsOrg.Count > 0)
            wrSet.Add(dsOrg);

         List<IDataSet> rmSet = new List<IDataSet>();

         if(dsRemOrg.Count > 0)
            rmSet.Add(dsRemOrg);

         return DataModule.UpdateDataSet(wrSet, rmSet, null, Config.GetConfig().GetConnection());
      }

      private void FmOrg_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (btnSave.Enabled && DialogUtil.AskToSave(this))
            Save();
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         FmOrgEdit dialog = new FmOrgEdit();

         if (dialog.ShowDialog() == DialogResult.OK)
         {
            Org org = new Org();
            org.id = Org.GenId();
            org.name = dialog.Org;
            org.slsnet = dialog.Slsnet;
            org.cid = dialog.CityName;
            org.address = dialog.Address;

            dsOrg.Add(org.id, org);
            datasource.Add(org);
            btnSave.Enabled = true;
         }
      }

      private void FmOrg_Load(object sender, EventArgs e)
      {
         if (dsOrg.Count == 0)
            btnRefresh.PerformClick();
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         if (Save())
         {
            dsRemOrg.Clear();
            btnSave.Enabled = false;
         }
         else
            DialogUtil.UpdateErrMsg(this);
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         if (grid.CurrentRow != null)
         {
            Org org = grid.CurrentRow.DataBoundItem as Org;

            if (org != null)
            {
               FmOrgEdit dialog = new FmOrgEdit();
               dialog.CityName = org.cid;
               dialog.Org = org.name;
               dialog.Address = org.address;
               dialog.Slsnet = org.slsnet;

               if (dialog.ShowDialog() == DialogResult.OK)
               {
                  org.cid = dialog.CityName;
                  org.slsnet = dialog.Slsnet;
                  org.name = dialog.Org;
                  org.address = dialog.Address;

                  btnSave.Enabled = true;
                  grid.Refresh();
               }
            }
         }
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         if (grid.CurrentRow != null)
         {
            Org org = grid.CurrentRow.DataBoundItem as Org;

            if (org != null && DialogUtil.AskToDel(this))
            {
               dsRemOrg.Add(org.id, org);
               dsOrg.Remove(org.id);
               datasource.Remove(org);
               btnSave.Enabled = true;
            }
         }
      }
   }
}
