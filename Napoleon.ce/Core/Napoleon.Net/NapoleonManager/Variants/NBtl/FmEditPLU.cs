using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmEditPLU : Form
   {
      DataSet<string, Price> dsPrice;
      DataSet<string, Slsnet> dsSlsnet;
      BindingList<PLU> datasource = new BindingList<PLU>();
      BindingList<Price> price = new BindingList<Price>();
      BindingList<Slsnet> slsnet = new BindingList<Slsnet>();
      SimpleDataSet<PLU> dsPLU = new SimpleDataSet<PLU>(PLU.OBJECT_NAME);
      SimpleDataSet<PLU> dsPLURem = new SimpleDataSet<PLU>(PLU.OBJECT_NAME);

      public FmEditPLU()
      {
         InitializeComponent();
         dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ?? new DataSet<string, Price>(Price.OBJECT_NAME);
         dsSlsnet = (DataSet<string, Slsnet>)DataModule.Get(Slsnet.OBJECT_NAME) ?? new DataSet<string, Slsnet>(Slsnet.OBJECT_NAME);

         dsPrice.Filter = "\"rem\"=0 and \"my\"=1 and \"fid\" in (select \"id\" from GroupGoods)";

         Column1.DisplayMember = "Name";
         Column1.ValueMember = "ID";
         Column1.DataPropertyName = "Item";
         Column1.DataSource = price;

         Column2.DisplayMember = "Name";
         Column2.ValueMember = "ID";
         Column2.DataPropertyName = "SLS";
         Column2.DataSource = slsnet;

         Column3.DataPropertyName = "Code";

         grid.DataSource = datasource;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsSlsnet);
         upd.Add(dsPrice);
         upd.Add(dsPLU);

         FmWait.StdDataRefresh(this, upd, DoLoadData, btnRefresh);
      }

      private void DoLoadData()
      {
         price.Clear();
         slsnet.Clear();

         List<Price> list = (List<Price>)dsPrice.ValueList;
         list.Sort((x,y)=>x.Name.CompareTo(y.Name));
         list.ForEach(p => price.Add(p));

         List<Slsnet> list2 = (List<Slsnet>)dsSlsnet.ValueList;
         list2.Sort((x, y) => x.Name.CompareTo(y.Name));
         list2.ForEach(p => slsnet.Add(p));

         datasource.Clear();

         foreach (PLU p in dsPLU.Values)
            datasource.Add(p);
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         grid.CommitEdit(DataGridViewDataErrorContexts.Commit);

         SimpleDataSet<PLU> ds = new SimpleDataSet<PLU>(PLU.OBJECT_NAME, false);

         foreach (PLU plu in datasource)
         {
            if (plu.Code.Trim().Length > 0 && plu.Item.Trim().Length > 0 && plu.SLS.Trim().Length > 0)
               ds.Add(plu);
         }

         List<IDataSet> wr = new List<IDataSet>();
   
         if (ds.Count > 0)
            wr.Add(ds);

         List<IDataSet> rem = new List<IDataSet>();

         if (dsPLURem.Count > 0)
            rem.Add(dsPLURem);

         bool res = DataModule.UpdateDataSet(wr, rem, null, Config.GetConfig().GetConnection());

         if (res)
         {
            dsPLURem.Clear();
         }
         else
            DialogUtil.UpdateErrMsg(this);
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         datasource.Add(new PLU());
      }

      private void FmEditPLU_Load(object sender, EventArgs e)
      {
         btnRefresh.PerformClick();
      }

      private void bntDel_Click(object sender, EventArgs e)
      {
         PLU obj = grid.CurrentRow?.DataBoundItem as PLU;

         if (obj != null)
         {
            datasource.Remove(obj);
            dsPLURem.Add(obj);
         }
      }

      private void grid_DataError(object sender, DataGridViewDataErrorEventArgs e)
      {

      }
   }
}
