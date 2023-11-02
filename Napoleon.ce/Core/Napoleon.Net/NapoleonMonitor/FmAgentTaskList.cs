using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Reflection;

namespace GRSoft.NapoleonManager
{
   public partial class FmAgentTaskList : Form
   {
      public DataSet<string, OrgTask> dsOrgTask;
      public DataSet<string, OrgTask> dsDelOrgTask;

      protected OrgTaskInfo taskInfo = new OrgTaskInfo();
      protected string userid = string.Empty;

      public static void ShowForm(OrgTaskInfo data, DateTime start, DateTime finish, String userid)
      {
         Type agentTask = FormEntries.GetFormType(typeof(FmAgentTaskList));
         ConstructorInfo ci = agentTask.GetConstructor(Type.EmptyTypes);
         FmAgentTaskList form = (FmAgentTaskList)ci.Invoke(new object[] { });

         form.dtpStart.Value = start;
         form.dtpFinish.Value = finish;
         form.taskInfo = data;
         form.userid = userid;
         form.Text += " " + data.name;
         form.Show();
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
      }

      void RefreshData()
      {
         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsOrgTask);
         AdjustFilter();
         FmWait.StdDataRefresh(this, list, FillData);
      }


      public FmAgentTaskList()
      {
         InitializeComponent();
         dsOrgTask = (DataSet<string, OrgTask>)DataModule.Get(OrgTask.OBJECT_NAME) ??
            new DataSet<string, OrgTask>(OrgTask.OBJECT_NAME);
         dsDelOrgTask = new DataSet<string, OrgTask>(OrgTask.OBJECT_NAME, false);
      }

      void FillData()
      {
         List<OrgTask> data = new List<OrgTask>();
         data.AddRange(dsOrgTask.Values);
         data.Sort(new Comparison<OrgTask>(delegate(OrgTask t1, OrgTask t2) { return t1.start.CompareTo(t2.start); }));

         dgvTask.DataSource = data;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         RefreshData();
      }

      protected virtual void AdjustFilter()
      {
         const string FILTER = "\"orgid\"='{0}' and \"userid\"='{1}' and \"start\" >= ToDate('{2:dd/MM/yyyy}') and \"start\" < ToDate('{3:dd/MM/yyyy}')";
         dsOrgTask.Filter = string.Format(FILTER, taskInfo.id, userid, dtpStart.Value.Date, dtpFinish.Value.Date.AddDays(1));
      }

      private void dgvTask_CellMouseDoubleClick(object sender, DataGridViewCellMouseEventArgs e)
      {
        
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         OrgTask task = FmAgentTaskEdit.EditTask(null);

         if(task != null)
         {
            task.orgid = taskInfo.id;
            task.userid = userid;
            dsOrgTask.Add(task.id, task);
            FillData();
            btnSave.Enabled = true;
         }
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         List<IDataSet> wrSet = new List<IDataSet>();
         List<IDataSet> rmvSet = new List<IDataSet>();

         if (dsOrgTask.Count > 0)
            wrSet.Add(UpdateTaskDataSet());
         
         if(dsDelOrgTask.Count > 0)
            rmvSet.Add(dsDelOrgTask);

         if (!DataModule.UpdateDataSet
            (wrSet, rmvSet, null, Config.GetConfig().GetConnection()))
            MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK,
               MessageBoxIcon.Error);
         else
            btnSave.Enabled = false;
      }

      protected virtual IDataSet UpdateTaskDataSet()
      {
         return dsOrgTask;
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         DataGridViewRow row = dgvTask.CurrentRow;

         if (row != null)
         {
            OrgTask orgTask = row.DataBoundItem as OrgTask;

            if (orgTask != null && MessageBox.Show("Запись будет удалена. Удалить?",
               "Вопрос", MessageBoxButtons.OKCancel,
               MessageBoxIcon.Question) == DialogResult.OK)
            {
               dsOrgTask.Remove(orgTask.id);
               dsDelOrgTask.Add(orgTask.id, orgTask);
               FillData();
               btnSave.Enabled = true;
            }
         }
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         DataGridViewRow row = dgvTask.CurrentRow;

         if (row != null)
         {
            OrgTask orgTask = row.DataBoundItem as OrgTask;

            if (orgTask != null)
            {
               OrgTask task = FmAgentTaskEdit.EditTask(orgTask);

               if (task != null)
               {
                  dgvTask.Refresh();
                  btnSave.Enabled = true;
               }
            }
         }
      }

      private void FmAgentTaskList_Load(object sender, EventArgs e)
      {
         btnSave.Enabled = false;
         dsOrgTask.Clear();
         dsDelOrgTask.Clear();
      }

      private void FmAgentTaskList_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK && btnSave.Enabled == true &&
            MessageBox.Show("Сохранить изменения", "Вопрос", MessageBoxButtons.OKCancel
               , MessageBoxIcon.Question) == DialogResult.OK)
         {
            btnSave_Click(null, null);
         }
      }
   }

   public class OrgTask : GRSoft.Network.DataObject
   {
      [KeyField]
      public string id = string.Empty;
      public string orgid = string.Empty;
      public string userid = string.Empty;
      public DateTime start;
      public DateTime finish;
      public string text = string.Empty;

      public static readonly string OBJECT_NAME = "OrgTask";

      public string Start { get { return start.ToString("dd:MM"); } }
      public string Finish { get { return finish.ToString("dd:MM"); } }
      public string Text { get { return text; } }
   }
}
