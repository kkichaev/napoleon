using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Xml.Serialization;
using System.IO;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public partial class EditTask : Form
   {
      private Task task;

      public EditTask(Task task)
      {
         InitializeComponent();
         this.task = task;
         InitView();
      }

      protected virtual void InitView()
      {
         if (task != null)
         {
            dtpStart.Value = task.start;
            dtpFinish.Value = task.finish;
            tbTask.Text = task.text;
            tbAgent.Text = task.agent != null ? task.agent.Name : string.Empty;
            tbFio.Text = task.fio;
            tbPhone.Text = task.phone;
            calendar.SelectionStart = task.start;
            calendar.SelectionEnd = task.finish;
            tbAddress.Text = task.address;
            tbClient.Text = task.client;
         }
      }

      public Task Task { get { return task; } }

      private void EditTask_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK)
            e.Cancel = !SaveTask();
      }

      private bool SaveTask()
      {
         bool result = true;

         if (task != null)
         {
            InitTask();

            DataSet<string, Task> ds = new DataSet<string, Task>(Task.OBJECT_NAME, false);
            ds.Add(task.taskid, task);

            List<IDataSet> wrSet = new List<IDataSet>();
            wrSet.Add(ds);

            result = DataModule.UpdateDataSet(wrSet, null, null, Config.GetConfig().GetConnection());

            if (!result)
               Utils.ErrorSaveDB();
         }

         return result;
      }

      protected virtual void InitTask()
      {
         DateTime date = calendar.SelectionStart.Date;

         task.text = tbTask.Text.Trim();
         task.start = new DateTime(date.Year, date.Month, date.Day, dtpStart.Value.Hour, dtpStart.Value.Minute, 0);
         task.finish = new DateTime(date.Year, date.Month, date.Day, dtpFinish.Value.Hour, dtpFinish.Value.Minute, 0);
         task.fio = tbFio.Text.Trim();
         task.phone = tbPhone.Text.Trim();
         task.client = tbClient.Text.Trim();
         task.address = tbAddress.Text.Trim();
      }

      protected virtual Task CreateTask()
      {
         return new Task();
      }
   }
}
