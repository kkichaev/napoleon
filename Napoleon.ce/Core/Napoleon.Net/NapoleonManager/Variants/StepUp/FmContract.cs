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
   public partial class FmContract : Form
   {
      private DataSet<string, ContractDef> dsContract;
      private DataSet<string, ContractDef> dsRemContract = new DataSet<string,ContractDef>(ContractDef.OBJECT_NAME, false);
      private DataSet<string, Price> dsPrice;
      private BindingList<ContractDef> datasource = new BindingList<ContractDef>();
      private DataSet<string, Price> dsUpdPrice = new DataSet<string, Price>(Price.OBJECT_NAME, false);      

      public FmContract()
      {
         InitializeComponent();

         dsContract = (DataSet<string, ContractDef>)DataModule.Get(ContractDef.OBJECT_NAME) ?? new DataSet<string, ContractDef>(ContractDef.OBJECT_NAME);
         dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ?? new DataSet<string, Price>(Price.OBJECT_NAME);
         grid.DataSource = datasource;
         btnSave.Enabled = false;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         dsRemContract.Clear();
         dsUpdPrice.Clear();

         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsPrice);
         upd.Add(dsContract);
         const string PERIOD_FILTER_STR = "\"start\" < ToDate('{1:dd/MM/yyyy}') and \"finish\" >= ToDate('{0:dd/MM/yyyy}')";

         dsContract.Filter = string.Format(PERIOD_FILTER_STR, dpv.Start, dpv.Finish.AddDays(1));

         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      private void DoLoadData()
      {
         datasource.Clear();

         foreach (ContractDef c in dsContract.Values)
            datasource.Add(c);

         btnSave.Enabled = false;
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
            ContractDef c = new ContractDef();
            c.id = ContractDef.GenId();
            c.start = dialog.Start;
            c.finish = dialog.Finish;
            c.name = dialog.Contract;
            c.items = new List<ContractIDeftem>();

            foreach (Price p in dialog.Items)
            {
               ContractIDeftem ci = new ContractIDeftem();
               ci.id = p.id;
               ci.item = p;
               c.items.Add(ci);

               dsUpdPrice.Add(p.id, p);
               dsPrice[p.id] = p;
            }

            dsContract.Add(c.id, c);
            datasource.Add(c);
            btnSave.Enabled = true;
         }
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         if (Save())
         {
            dsUpdPrice.Clear();
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

         if (dsContract.Count > 0)
            wrSet.Add(dsContract);

         if (dsUpdPrice.Count > 0)
            wrSet.Add(dsUpdPrice);

         if (dsRemContract.Count > 0)
            rmSet.Add(dsRemContract);

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
               dsRemContract.Add(c.id, c);
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
               dialog.Start = c.start;
               dialog.Finish = c.finish;
               dialog.Contract = c.name;

               List<Price> list = new List<Price>();
               
               foreach (ContractIDeftem i in c.items)
                  list.Add(i.item);

               dialog.Items = list;

               if (dialog.ShowDialog() == DialogResult.OK)
               {
                  c.start = dialog.Start;
                  c.finish = dialog.Finish;
                  c.name = dialog.Contract;
                  c.items = new List<ContractIDeftem>();

                  foreach (Price p in dialog.Items)
                  {
                     ContractIDeftem ci = new ContractIDeftem();
                     ci.id = p.id;
                     ci.item = p;

                     c.items.Add(ci);

                     dsUpdPrice[p.id] = p;
                  }

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
   }
}
