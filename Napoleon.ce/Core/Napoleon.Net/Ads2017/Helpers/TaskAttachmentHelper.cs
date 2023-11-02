using System.Collections.Generic;

namespace Ads2017
{
   class TaskAttachmentHelper
   {
      private static TaskAttachmentHelper instance = new TaskAttachmentHelper();
      public static TaskAttachmentHelper Instance { get { return instance; } }

      private Dictionary<string, List<TaskAttachment>> data = new Dictionary<string, List<TaskAttachment>>();

      private TaskAttachmentHelper()
      {
      }

      public void Load(List<TaskAttachment> list)
      {
         data.Clear();

         foreach (TaskAttachment i in list)
            Add(i.taskid, i);
      }

      public List<TaskAttachment> GetAttach(string taskid)
      {
         List<TaskAttachment> result = new List<TaskAttachment>();

         if (data.ContainsKey(taskid))
            result.AddRange(data[taskid]);

         return result;
      }

      internal void Add(string taskid, TaskAttachment a)
      {
         if (!data.ContainsKey(taskid))
            data[taskid] = new List<TaskAttachment>();

         data[taskid].Add(a);
      }

      internal void Remove(string taskid, TaskAttachment i)
      {
         if (data.ContainsKey(taskid))
            data[taskid].Remove(i);
      }

      internal bool HasAttach(string taskid)
      {
         return data.ContainsKey(taskid) && data[taskid].Count > 0;
      }
   }
}
