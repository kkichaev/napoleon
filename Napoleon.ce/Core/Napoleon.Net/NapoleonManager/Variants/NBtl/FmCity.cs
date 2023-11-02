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
   public partial class FmCity : Form
   {
      public DataSet<string, City> dsCity;
      private BindingList<City> datasource = new BindingList<City>();
      private string lastupdateitem = string.Empty;
      private DataSet<string, City> dsRemSlsnet = new DataSet<string, City>(City.OBJECT_NAME, false);

      public FmCity()
      {
         InitializeComponent();
         btnSave.Enabled = false;

         dsCity = (DataSet<string, City>)DataModule.Get(City.OBJECT_NAME) ?? new DataSet<string, City>(City.OBJECT_NAME);
         grid.DataSource = datasource;
      }

      public delegate void CityRefresh(string id);

      public event CityRefresh OnCityRefresh;

      private void FmCity_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (btnSave.Enabled && DialogUtil.AskToSave(this))
            Save();
      }

      private bool Save()
      {
         List<IDataSet> wrSet = new List<IDataSet>();

         if(dsCity.Count > 0)
            wrSet.Add(dsCity);

         List<IDataSet> rmSet = new List<IDataSet>();

         if(dsRemSlsnet.Count > 0)
            rmSet.Add(dsRemSlsnet);

         FireCityRefresh();
         return DataModule.UpdateDataSet(wrSet, rmSet, null, Config.GetConfig().GetConnection());
      }

      private void FireCityRefresh()
      {
         if (lastupdateitem.Trim().Length > 0 && OnCityRefresh != null)
            OnCityRefresh(lastupdateitem);
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         FmCityEdit dialog = new FmCityEdit();

         if (dialog.ShowDialog() == DialogResult.OK) 
         {
            City sls = new City();
            sls.id = GRSoft.Network.DataObject.GenId();
            sls.name = dialog.City;

            datasource.Add(sls);
            dsCity.Add(sls.id, sls);

            lastupdateitem = sls.id;

            btnSave.Enabled = true;
         }
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         if (grid.CurrentRow != null)
         {
            FmCityEdit dialog = new FmCityEdit();
            City sls = grid.CurrentRow.DataBoundItem as City;

            if (sls != null)
            {
               dialog.City = sls.Name;

               if (dialog.ShowDialog() == DialogResult.OK)
               {
                  sls.name = dialog.City;
                  btnSave.Enabled = true;
                  lastupdateitem = sls.id;
               }
            }
         }
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         if (grid.CurrentRow != null)
         {
            City sls = grid.CurrentRow.DataBoundItem as City;

            if (sls != null && DialogUtil.AskToDel(this))
            {
               datasource.Remove(sls);
               dsCity.Remove(sls.id);

               btnSave.Enabled = true;
               dsRemSlsnet.Add(sls.id, sls);
            }
         }
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         if (Save())
         {
            dsRemSlsnet.Clear();
            btnSave.Enabled = false;
         }
         else
            DialogUtil.UpdateErrMsg(this);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsCity);

         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      private void DoLoadData()
      {
         datasource.Clear();
         List<City> list = new List<City>();
         list.AddRange(dsCity.Values);
         list.Sort((lhs, rhs) => { return lhs.Name.CompareTo(rhs.Name); });

         foreach (City s in list)
            datasource.Add(s);
      }

      private void FmCity_Load(object sender, EventArgs e)
      {
         btnRefresh.PerformClick();
      }
   }
}
