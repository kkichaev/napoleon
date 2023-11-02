using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{ 
   public partial class FmAgentTaskListEx : FmAgentTaskList
   {
      public FmAgentTaskListEx()
      {
         Shown += new EventHandler(FmAgentTaskListEx_Shown);
      }

      protected override IDataSet UpdateTaskDataSet()
      {
         DataSet<string, OrgTaskEx> result = new DataSet<string, OrgTaskEx>(OrgTaskEx.OBJECT_NAME, false);

         foreach (OrgTask t in dsOrgTask.Values)
         {
            OrgTaskEx task = OrgTaskEx.Create(t);
            task.groupid = ((OrgTaskInfoEx)taskInfo).ido;
            result.Add(task.id, task);
         }

         return result;
      }

      void FmAgentTaskListEx_Shown(object sender, EventArgs e)
      {
         Text = taskInfo.Name + " " + ((OrgTaskInfoEx)taskInfo).ItemName;
      }

      protected override void AdjustFilter()
      {
         const string FILTER = "\"orgid\"='{0}' and \"userid\"='{1}' and \"start\" >= ToDate('{2:dd/MM/yyyy}') and \"start\" <= ToDate('{3:dd/MM/yyyy}')";
         dsOrgTask.Filter = string.Format(FILTER, taskInfo.id, userid, dtpStart.Value.Date, dtpFinish.Value.Date);
      }
   }
}
