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
   public partial class FmGroupTaskList : Form
   {
      //DataSet<string, DivisionManager> dsManager;
      public DataSet<string, GroupTask> dsOrgTask;
      public DataSet<string, GroupTask> dsDelOrgTask;

      protected OrgTaskInfo taskInfo = new OrgTaskInfo();
      protected string userid = string.Empty;

      Dictionary<string, List<GroupTask>> taskData = new Dictionary<string, List<GroupTask>>();

      public static void ShowForm(OrgTaskInfo data, DateTime start, DateTime finish, String userid)
      {
         Type agentTask = FormEntries.GetFormType(typeof(FmGroupTaskList));
         ConstructorInfo ci = agentTask.GetConstructor(Type.EmptyTypes);
         FmGroupTaskList form = (FmGroupTaskList)ci.Invoke(new object[] { });

         form.dtpStart.Value = start;
         form.dtpFinish.Value = finish;
         form.taskInfo = data;
         form.userid = userid;
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
         Manager m =  (CurrentUser.user as Manager);

         if (m != null)
         {
            foreach (Division.DivisionAgent a in m.Division.GetAllAgents())
               if (a.agent != null)
               {
                  list.Add((DataSet<string, Org>)DataModule.GetUserDataSet(a.id, Org.OBJECT_NAME, typeof(DataSet<string, Org>), true));
               }

            AdjustFilter();
            FmWait.StdDataRefresh(this, list, FillData);
         }
      }


      public FmGroupTaskList()
      {
         InitializeComponent();
         dgvTask.AutoGenerateColumns = false;

         dsOrgTask = new DataSet<string, GroupTask>(GroupTask.OBJECT_NAME, false);
         dsDelOrgTask = new DataSet<string, GroupTask>(GroupTask.OBJECT_NAME, false);

         //dsManager = (DataSet<string, DivisionManager>)DataModule.Get(DivisionManager.OBJECT_NAME) ??
         //   new DataSet<string, DivisionManager>(DivisionManager.OBJECT_NAME, false);
      }

      void FillData()
      {
         taskData.Clear();

         foreach (GroupTask o in dsOrgTask.Values)
         {
            if (!taskData.ContainsKey(o.groupid))
               taskData[o.groupid] = new List<GroupTask>();

            taskData[o.groupid].Add(o);
         }

         List<GroupTask> data = new List<GroupTask>();

         foreach (List<GroupTask> ts in taskData.Values)
         {
            if (ts.Count > 0)
               data.Add(ts[0]);
         }

         data.Sort(new Comparison<GroupTask>(delegate(GroupTask t1, GroupTask t2) { return t1.start.CompareTo(t2.start); }));

         dgvTask.DataSource = data;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         RefreshData();
      }

      protected virtual void AdjustFilter()
      {
         const string FILTER = "\"groupid\" is not null and \"finish\" >= ToDate('{2:dd/MM/yyyy}') and \"start\" < ToDate('{3:dd/MM/yyyy}')";
         dsOrgTask.Filter = string.Format(FILTER, taskInfo.id, userid, dtpStart.Value.Date, dtpFinish.Value.Date.AddDays(1));
      }

      private void dgvTask_CellMouseDoubleClick(object sender, DataGridViewCellMouseEventArgs e)
      {
        
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         List<GroupTask> task = FmGroupTaskEdit.EditTask(null);

         if(task != null && task.Count > 0)
         {
            foreach(GroupTask g in task)
               dsOrgTask.Add(g.id, g);

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
         {
            btnSave.Enabled = false;
            dsDelOrgTask.Clear();
         }
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
            GroupTask orgTask = row.DataBoundItem as GroupTask;

            if (orgTask != null && MessageBox.Show("Запись будет удалена. Удалить?",
               "Вопрос", MessageBoxButtons.OKCancel,
               MessageBoxIcon.Question) == DialogResult.OK)
            {
               foreach (GroupTask t in taskData[orgTask.groupid])
               {
                  dsOrgTask.Remove(t.id);
                  dsDelOrgTask.Add(t.id, t);
               }

               FillData();
               btnSave.Enabled = true;
               taskData.Remove(orgTask.groupid);
            }
         }
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         DataGridViewRow row = dgvTask.CurrentRow;

         if (row != null)
         {
            GroupTask orgTask = row.DataBoundItem as GroupTask;

            if (orgTask != null)
            {
               List<GroupTask> input = taskData[orgTask.groupid];
               List<GroupTask> res = FmGroupTaskEdit.EditTask(input);

               if (res != null)
               {
                  Dictionary<string, GroupTask> inids = new Dictionary<string, GroupTask>();
                  foreach (GroupTask t in input)
                     inids.Add(t.id, t);

                  Dictionary<string, GroupTask> outids = new Dictionary<string, GroupTask>();
                  foreach (GroupTask t in res)
                     outids.Add(t.id, t);

                  foreach(string id in inids.Keys)
                  {
                     if (!outids.ContainsKey(id))
                     {
                        input.Remove(inids[id]);
                        dsDelOrgTask.Add(id, inids[id]);
                        dsOrgTask.Remove(id);
                     }
                  }

                  foreach (string id in outids.Keys)
                  {
                     if (!inids.ContainsKey(id))
                     { 
                        input.Add(outids[id]);
                        dsOrgTask.Add(id, outids[id]);
                     }
                  }

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
         if (btnSave.Enabled == true &&
            MessageBox.Show("Сохранить изменения", "Вопрос", MessageBoxButtons.OKCancel
               , MessageBoxIcon.Question) == DialogResult.OK)
         {
            btnSave_Click(null, null);
         }
      }

      private void btnReport_Click(object sender, EventArgs e)
      {
         (new FmTaskReport()).Show();
      }
   }

   public class GroupTask : OrgTask
   {
      public string groupid = string.Empty;
   }

}
