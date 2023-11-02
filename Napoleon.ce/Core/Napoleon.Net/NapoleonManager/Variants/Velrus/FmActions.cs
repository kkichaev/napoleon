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
   public partial class FmActions : Form
   {
      BindingList<OrderAction> data = new BindingList<OrderAction>();
      DataSet<int, OrderAction> dsAction = new DataSet<int, OrderAction>(OrderAction.OBJECT_NAME);
      DataSet<string, Price> dsPrice = new DataSet<string, Price>(Price.OBJECT_NAME);

      public FmActions()
      {
         InitializeComponent();
         grid.AutoGenerateColumns = false;
         grid.DataSource = data;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsPrice);
         upd.Add(dsAction);
         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      private void DoLoadData()
      {
         data.Clear();

         foreach (OrderAction a in dsAction.ValueList)
            data.Add(a);
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         FmActionEdit form = new FmActionEdit();

         if (form.ShowDialog() == DialogResult.OK)
         {
            List<IDataSet> write = new List<IDataSet>();
            DataSet<int, OrderAction> ds = new DataSet<int, OrderAction>(OrderAction.OBJECT_NAME, false);
            write.Add(ds);
            ds.Add(ds.Count, form.Action);

            if (DataModule.WriteDataSet(write, Config.GetConfig().GetConnection()))
               data.Add(form.Action);
            else
               DialogUtil.UpdateErrMsg(this);
         }
      }

      private void FmActions_Load(object sender, EventArgs e)
      {
         btnRefresh.PerformClick();
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         OrderAction action = grid.CurrentRow.DataBoundItem as OrderAction;

         if (action != null)
         {
            FmActionEdit form = new FmActionEdit();
            form.Action = action;

            if (form.ShowDialog() == DialogResult.OK)
            {
               grid.Refresh();

               List<IDataSet> write = new List<IDataSet>();
               DataSet<int, OrderAction> ds = new DataSet<int, OrderAction>(OrderAction.OBJECT_NAME, false);
               write.Add(ds);
               ds.Add(ds.Count, form.Action);

               if (!DataModule.WriteDataSet(write, Config.GetConfig().GetConnection()))
                  DialogUtil.UpdateErrMsg(this);
            }
         }
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         OrderAction action = grid.CurrentRow.DataBoundItem as OrderAction;

         if (action != null && DialogUtil.AskToDel(this))
         {
            action.rem = 1;
            List<IDataSet> write = new List<IDataSet>();
            DataSet<int, OrderAction> ds = new DataSet<int, OrderAction>(OrderAction.OBJECT_NAME, false);
            write.Add(ds);
            ds.Add(ds.Count, action);

            if (!DataModule.WriteDataSet(write, Config.GetConfig().GetConnection()))
               DialogUtil.UpdateErrMsg(this);
            else
               data.Remove(action);
         }
      }
   }
}
