using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public partial class TaskControl : UserControl, DataObjectViewer
   {
      public BaseDocument doc;

      public TaskControl()
      {
         InitializeComponent();

         grid.AutoGenerateColumns = false;
      }

      public void SetData(Network.DataObject dataObject)
      {
         doc = dataObject as BaseDocument;

         if (doc != null) { 
            DataSet<int, ATask> dsATask = (DataSet<int, ATask>)DataModule.Get(ATask.OBJECT_NAME);

           List<ATask> list = new List<ATask>();
            if(dsATask != null)
               foreach (ATask t in dsATask.Data)
                  if(t.id.Equals(doc.id))
                     list.Add(t);

            DataSet<int, MTask> dsMTask = (DataSet<int, MTask>)DataModule.Get(MTask.OBJECT_NAME);
            if (dsMTask != null)
               foreach (ATask t in dsMTask.Data)
                  if (t.id.Equals(doc.id))
                     list.Add(t);

            list.Sort((lhs, rhs) => lhs.created.CompareTo(rhs.created));

            BindingList<ATask> bl = new BindingList<ATask>();
            foreach (ATask t in list)
               bl.Add(t);

            grid.DataSource = bl;
         }
      }

      private void btnTask_Click(object sender, EventArgs e)
      {
         BindingList<ATask> list = grid.DataSource as BindingList<ATask>;
         FmTaskEdit te = new FmTaskEdit();

         if (doc != null && list != null && te.ShowDialog() == DialogResult.OK)
         {
            MTask task = new MTask();
            task.taskid = GRSoft.Network.DataObject.GenId();
            task.created = DateTime.Now;
            task.id = doc.id;
            task.remark = te.tbText.Text.Trim();

            DataSet<int, MTask> ds = new DataSet<int, MTask>(MTask.OBJECT_NAME, false);
            ds.Add(0, task);
            List<IDataSet> wr = new List<IDataSet>();
            wr.Add(ds);

            if (!DataModule.UpdateDataSet(wr, null, null, Config.GetConfig().GetConnection(), doc.userid))
               DialogUtil.UpdateErrMsg(this);
            else
               list.Add(task);
         }
      }

      private void grid_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         MTask t = grid.Rows[e.RowIndex].DataBoundItem as MTask;

         if (t != null)
            e.CellStyle.ForeColor = Color.Red;
      }

      private void grid_CellMouseDoubleClick(object sender, DataGridViewCellMouseEventArgs e)
      {
         if (e.Button == System.Windows.Forms.MouseButtons.Left)
         {
            MTask t = grid.Rows[e.RowIndex].DataBoundItem as MTask;

            EditTask(t);
         }
      }

      private void EditTask(MTask t)
      {
         if (t != null)
         {
            FmTaskEdit te = new FmTaskEdit();
            te.tbText.Text = t.remark;

            if (te.ShowDialog() == DialogResult.OK)
            {
               t.remark = te.tbText.Text.Trim();

               DataSet<int, MTask> ds = new DataSet<int, MTask>(MTask.OBJECT_NAME, false);
               ds.Add(0, t);
               List<IDataSet> wr = new List<IDataSet>();
               wr.Add(ds);

               if (!DataModule.UpdateDataSet(wr, null, null, Config.GetConfig().GetConnection(), doc.userid))
                  DialogUtil.UpdateErrMsg(this);
               else
                  grid.Refresh();
            }
         }
      }

      private void miEdit_Click(object sender, EventArgs e)
      {
         DataGridViewRow r = grid.CurrentRow;

         if(r != null)
         {
            MTask t = r.DataBoundItem as MTask;

            if (t != null)
               EditTask(t);
         }
      }

      private void miDel_Click(object sender, EventArgs e)
      {
         DataGridViewRow r = grid.CurrentRow;

         if (r != null)
         {
            MTask t = r.DataBoundItem as MTask;

            if (t != null && DialogUtil.AskToDel(this))
            {
               DataSet<int, MTask> ds = new DataSet<int, MTask>(MTask.OBJECT_NAME, false);
               ds.Add(0, t);
               List<IDataSet> rem = new List<IDataSet>();
               rem.Add(ds);

               if (!DataModule.UpdateDataSet(null, rem, null, Config.GetConfig().GetConnection(), doc.userid))
                  DialogUtil.UpdateErrMsg(this);
               else
               {
                  BindingList<ATask> list = grid.DataSource as BindingList<ATask>;
                  list.Remove(t);
               }
            }
         }
      }
   }

   public partial class ATask
   {
      public string Text { get { return remark; } }
   }
}
