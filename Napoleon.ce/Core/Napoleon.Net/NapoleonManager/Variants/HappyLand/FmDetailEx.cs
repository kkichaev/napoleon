using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
  [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public
   class FmDetailEx : FmDetail
   {
      SimpleDataSet<TaskInfo> dsTaskInfo = new SimpleDataSet<TaskInfo>(TaskInfo.OBJECT_NAME);

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);

         dsTaskInfo.Clear();
         dsTaskInfo.Filter = string.Format("\"userid\"={0} and done=0",agentID);

         updSets.Add(dsTaskInfo);
      }

      protected override void PostSetScriptInfo(ScriptDoc sd)
      {
         TabPage tp = new TabPage("Задачи");
         TaskViewer tv = new TaskViewer();

         List<TaskInfo> list = new List<TaskInfo>();

         foreach(TaskInfo ti in dsTaskInfo.Values)
            if(ti.id.Equals(sd.id))
               list.Add(ti);

         tv.grid.DataSource = list;

         tp.Controls.Add(tv);
         scriptDetail.TabPages.Add(tp);
      }
   }

   class TaskInfo : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "TaskInfo";

      [Reference("Agents", "userid")]
      public Agent agent = null;

      public String id = string.Empty;
      public string text = string.Empty;
      public string idgr = string.Empty;
      public DateTime date = DateTime.MinValue;
      public int done = 0;


      public DateTime Date { get { return date; } }
      public string Idgr { get { return idgr; } }
      public string Text { get { return text; } }
   }
}
