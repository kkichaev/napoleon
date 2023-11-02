using GRSoft.Network;
using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Text;
using System.Threading;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmContract : Form
   {
      private DataSet<string, ContractDef> dsContract;
      private DataSet<string, ContractDef> dsRemContract = new DataSet<string,ContractDef>(ContractDef.OBJECT_NAME, false);
      private DataSet<string, Price> dsPrice;
      private BindingList<ContractDef> datasource = new BindingList<ContractDef>();
      private DataSet<string, Price> dsUpdPrice = new DataSet<string, Price>(Price.OBJECT_NAME, false);
      private DataSet<string, Price> dsRemPrice = new DataSet<string, Price>(Price.OBJECT_NAME, false);
      private DataSet<string, ContractDef> dsUpdContractDef = new DataSet<string, ContractDef>(ContractDef.OBJECT_NAME, false);
      private DataSet<string, Org> dsOrg;

      public FmContract()
      {
         InitializeComponent();

         dsContract = (DataSet<string, ContractDef>)DataModule.Get(ContractDef.OBJECT_NAME) ?? new DataSet<string, ContractDef>(ContractDef.OBJECT_NAME);
         dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ?? new DataSet<string, Price>(Price.OBJECT_NAME);
         dsOrg = (DataSet<string, Org>)DataModule.Get(Org.OBJECT_NAME) ?? new DataSet<string, Org>(Org.OBJECT_NAME);
         dsOrg.Filter = "\"id\" is null or \"id\" is not null";

         grid.DataSource = datasource;
         btnSave.Enabled = false;
         dpv.Start = DateTime.Now;
         dpv.Finish = DateTime.Now;
      }

      public static readonly string PRICE_FILTER_STR = "\"cdef\" in (select \"id\" from contractdef where \"start\" < ToDate('{1:dd/MM/yyyy}') and \"finish\" >= ToDate('{0:dd/MM/yyyy}'))";
      public static readonly string PERIOD_FILTER_STR = "\"start\" < ToDate('{1:dd/MM/yyyy}') and \"finish\" >= ToDate('{0:dd/MM/yyyy}')";

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         dsUpdPrice.Clear();
         dsRemPrice.Clear();
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsOrg);
         upd.Add(dsPrice);
         upd.Add(dsContract);
         
         DateTime finish = dpv.Finish.AddDays(1);
         dsContract.Filter = string.Format(PERIOD_FILTER_STR, dpv.Start, finish);
         dsPrice.Filter = string.Format(PRICE_FILTER_STR, dpv.Start, finish);
         
         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      private void DoLoadData()
      {
         datasource.Clear();

         foreach (ContractDef c in dsContract.Values)
            datasource.Add(c);

         btnSave.Enabled = false;
         dsUpdContractDef.Clear();
         dsUpdPrice.Clear();
         dsRemContract.Clear();
         dsRemPrice.Clear();
      }

      private void FmContract_Load(object sender, EventArgs e)
      {
         btnRefresh.PerformClick();
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         FmContractEdit dialog  = new FmContractEdit();
         if (dialog.ShowDialog() == DialogResult.OK)
         {
            ContractDef c = dialog.Contract;// new ContractDef();
            //c.id = ContractDef.GenId();
            //c.start = dialog.Start;
            //c.finish = dialog.Finish;
            //c.name = dialog.Contract;
            //c.items = new List<ContractIDeftem>();
            //c.orgImg = dialog.OrgImg;

            foreach (Price p in dialog.Items)
            {
               //ContractIDeftem ci = new ContractIDeftem();
               //ci.id = p.id;
               //c.items.Add(ci);
               //ci.item = p;

               //p.cdef = c.id;

               dsUpdPrice.Add(p.id, p);
            }

            foreach (Price p in dialog.removed)
               dsRemPrice.Add(p.id, p);

            dsUpdContractDef[c.id] = c;
            datasource.Add(c);
            btnSave.Enabled = true;
         }
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         if (Save())
         {
            dsUpdContractDef.Clear();
            dsUpdPrice.Clear();
            dsRemPrice.Clear();
            dsRemContract.Clear();
            btnSave.Enabled = false;
         }
         else
            DialogUtil.UpdateErrMsg(this);
      }

      private bool Save()
      {
         List<IDataSet> wrSet = new List<IDataSet>();
         List<IDataSet> rmSet = new List<IDataSet>();

         if (dsUpdContractDef.Count > 0)
            wrSet.Add(dsUpdContractDef);

         if (dsUpdPrice.Count > 0)
            wrSet.Add(dsUpdPrice);

         if (dsRemContract.Count > 0)
            rmSet.Add(dsRemContract);

         if (dsRemPrice.Count > 0)
            rmSet.Add(dsRemPrice);

         return DataModule.UpdateDataSet(wrSet, rmSet, null, Config.GetConfig().GetConnection());
      }

      private void btnRem_Click(object sender, EventArgs e)
      {
         if (grid.CurrentRow != null)
         {
            ContractDef c = grid.CurrentRow.DataBoundItem as ContractDef;

            if (c != null && DialogUtil.AskToDel(this))
            {
               datasource.Remove(c);
               dsRemContract[c.id] = c;
               btnSave.Enabled = true;
            }
         }
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         if (grid.CurrentRow != null)
         {
            ContractDef c = grid.CurrentRow.DataBoundItem as ContractDef;

            if (c != null)
            {
               FmContractEdit dialog = new FmContractEdit();
               dialog.Contract = c;
               //dialog.Start = c.start;
               //dialog.Finish = c.finish;
               //dialog.Contract = c.name;
               //dialog.Photo = c.photo;
               //dialog.OrgImg = c.orgImg;

               //List<Price> list = new List<Price>();
               
               //foreach (ContractIDeftem i in c.items)
               //   list.Add(i.item);

               //dialog.Items = list;

               if (dialog.ShowDialog() == DialogResult.OK)
               {
                  //c.start = dialog.Start;
                  //c.finish = dialog.Finish;
                  //c.name = dialog.Contract;
                  //c.photo = dialog.Photo;
                  //c.items = new List<ContractIDeftem>();
                  //c.orgImg = dialog.OrgImg;

                  foreach (Price p in dialog.Items)
                  {
                     //ContractIDeftem ci = new ContractIDeftem();
                     //ci.id = p.id;
                     //ci.item = p;

                     //c.items.Add(ci);

                     //p.cdef = c.id;
                     dsUpdPrice[p.id] = p;
                  }

                  foreach (Price p in dialog.removed)
                     dsRemPrice.Add(p.id, p);

                  dsUpdContractDef[c.id] = c;
                  grid.Refresh();
                  btnSave.Enabled = true;
               }
            }
         }
      }

      private void FmContract_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (btnSave.Enabled && DialogUtil.AskToSave(this))
            Save();
      }

      private void grid_DoubleClick(object sender, EventArgs e)
      {
         btnEdit.PerformClick();
      }
   }
}
