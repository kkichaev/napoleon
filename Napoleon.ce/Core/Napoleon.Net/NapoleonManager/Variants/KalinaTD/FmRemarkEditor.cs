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
   public partial class FmRemarkEditor : Form
   {
      private SimpleDataSet<OrderRemark> dsOrderRemark = new SimpleDataSet<OrderRemark>(OrderRemark.OBJECT_NAME);
      private DataSet<string, OrderRemark> dsToSave = new DataSet<string, OrderRemark>(OrderRemark.OBJECT_NAME);

      public FmRemarkEditor()
      {
         InitializeComponent();

         grid.DataSource = new BindingList<OrderRemark>();
         btnSave.Enabled = false;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsOrderRemark);

         FmWait.StdDataRefresh(this, list, DoLoadData);
      }

      private void DoLoadData()
      {
         grid.SuspendLayout();

         BindingList<OrderRemark> data = (BindingList<OrderRemark>)grid.DataSource;
         data.Clear();

         List<OrderRemark> list = new List<OrderRemark>();
         list.AddRange(dsOrderRemark.Values);
         list.Sort((x, y) => { return x.pos - y.pos; });

         foreach (OrderRemark o in list)
            data.Add(o);

         grid.ResumeLayout();

         btnSave.Enabled = false;
         dsToSave.Clear();
      }

      private void FmRemarkEditor_Load(object sender, EventArgs e)
      {
         btnRefresh.PerformClick();
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         OrderRemark item = new OrderRemark();
         item.id = GRSoft.Network.DataObject.GenId();

         dsToSave.Add(item.id, item);

        ((BindingList<OrderRemark>)grid.DataSource).Add(item);
        grid.CurrentCell = grid[0, grid.RowCount-1];
        btnSave.Enabled = true;
      }

      private void btnRem_Click(object sender, EventArgs e)
      {
         DataGridViewRow r = grid.CurrentRow;

         if (r != null)
         {
            OrderRemark o = r.DataBoundItem as OrderRemark;

            if (o != null)
            {
               o.rem = 1;
               dsToSave[o.id] = o;

               ((BindingList<OrderRemark>)grid.DataSource).Remove(o);
            }
         }

         btnSave.Enabled = true;
      }

      private void grid_CellValueChanged(object sender, DataGridViewCellEventArgs e)
      {
         if (e.RowIndex >= 0)
         {
            btnSave.Enabled = true;

            OrderRemark or = grid.Rows[e.RowIndex].DataBoundItem as OrderRemark;

            if (or != null)
            {
               dsToSave[or.id] = or;
            }
         }
      }

      private void FmRemarkEditor_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (btnSave.Enabled && DialogUtil.AskToSave(this))
            btnSave.PerformClick();
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         if (dsToSave.Count > 0)
         {
            List<IDataSet> wrSet = new List<IDataSet>();
            wrSet.Add(dsToSave);

            if (!DataModule.UpdateDataSet(wrSet, null, null, Config.GetConfig().GetConnection()))
               DialogUtil.UpdateErrMsg(this);
            else
            {
               btnSave.Enabled = false;
               dsToSave.Clear();
            }
         }
      }
   }
}
