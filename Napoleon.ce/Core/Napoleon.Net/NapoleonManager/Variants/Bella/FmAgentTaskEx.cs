using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using System.Drawing;
using GRSoft.Network;
using System.Collections;
using System.Reflection;
using GRSoft.NapoleonManager.Utils;
using GRSoft.NapoleonManager.Properties;

namespace GRSoft.NapoleonManager
{
   class FmAgentTaskEx : FmAgentTask
   {
      public FmAgentTaskEx()
      {
         ToolStripButton btnCollectiveTask = new ToolStripButton();
         btnCollectiveTask.Image = Resources.quest_doc;
         btnCollectiveTask.Click += new EventHandler(btnCollectiveTask_Click);
         btnCollectiveTask.ToolTipText = "Создать задачи для всех контрагентов";
         btnCollectiveTask.Margin = new System.Windows.Forms.Padding(185, 0, 0, 0);

         toolStrip1.Items.Add(btnCollectiveTask);
      }

      void btnCollectiveTask_Click(object sender, EventArgs e)
      {
         OrgTask task = new OrgTask();
         task.start = dtpStart.Value.Date;
         task.finish = dtpFinish.Value.Date;

         task = FmAgentTaskEdit.EditTask(task);

         if (task != null && dgvTask.Rows.Count > 0)
         {
            DataSet<string, OrgTask> dsTask = new DataSet<string, OrgTask>(OrgTask.OBJECT_NAME, false);

            foreach (DataGridViewRow row in dgvTask.Rows)
            {
               OrgTaskInfo info = InflateDataBound(row.DataBoundItem) as OrgTaskInfo;
               OrgTask newTask = CloneTask(task);
               newTask.orgid = info.id;
               newTask.userid = ((Agent)cbAgent.SelectedItem).id;
               dsTask.Add(newTask.id, newTask);
            }

            List<IDataSet> updSet = new List<IDataSet>();
            updSet.Add(dsTask);
            if (!DataModule.UpdateDataSet(updSet, null, null, Config.GetConfig().GetConnection()))
               MessageBox.Show("Ошибка записи в базу данных");
         }
      }

      private OrgTask CloneTask(OrgTask src)
      {
         OrgTask result = new OrgTask();

         result.id = Task.GenId();
         result.orgid = src.orgid;
         result.userid = src.userid;
         result.start = src.start;
         result.finish = src.finish;
         result.text = src.text;

         return result;
      }
   }
}

