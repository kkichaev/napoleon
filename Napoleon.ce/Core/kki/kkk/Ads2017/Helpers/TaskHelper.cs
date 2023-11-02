using System;
using System.Collections;
using System.Collections.Generic;
using System.Windows.Media;

namespace Ads2017
{
    class TaskHelper
    {
        private static readonly TaskHelper instance = new TaskHelper();
        public static TaskHelper Instance { get { return instance; } }
        private Dictionary<string, List<TaskQuery>> tasks = new Dictionary<string, List<TaskQuery>>();
        public Dictionary<string, TaskInfo> info;

        private TaskHelper()
        {
            info = new Dictionary<string, TaskInfo>
         {
            { string.Empty, new TaskInfo() }
         };
        }

        public static Color BkgItemColor(int val)
        {
            const byte a = 254;
            return BkgItemColor(val, a);
        }

        public static Color BkgItemColor(int val, byte a)
        {
            Color result = Color.FromArgb(a, 0x00, 0x7F, 0x0E);

            switch (val)
            {
                case Task.NEW:
                    result = Color.FromArgb(a, 0x00, 0x7C, 0xC5);
                    break;
                case Task.APPLY:
                    result = Color.FromArgb(a, 0x8F, 0xA3, 0x99);
                    break;
                case Task.REJECT:
                    result = Color.FromArgb(a, 0xFE, 0x00, 0x00);
                    break;
                case Task.INWORK:
                    result = Color.FromArgb(a, 0xFF, 0xD8, 0x00);
                    break;
                case Task.RESOLVED:
                    result = Color.FromArgb(a, 0x0d, 0x82, 0x00);
                    break;
            }

            return result;
        }

        public void CollectTasks(IEnumerable<TaskQuery> input)
        {
            tasks.Clear();
            info.Clear();

            info.Add(string.Empty, new TaskInfo());
            List<string> agents = ManagerHelper.Instance.Agents;

            foreach (TaskQuery t in input)
                if (agents.Contains(t.userid))
                    AppendTask(t);
        }


        public void AppendTask(TaskQuery task)
        {
            if (!tasks.ContainsKey(task.userid))
                tasks[task.userid] = new List<TaskQuery>();

            tasks[task.userid].Add(task);

            if (!info.ContainsKey(task.userid))
                info[task.userid] = new TaskInfo();

            TaskInfo t = info[task.userid];
            AddTaskInfo(task, t);
            t = info[string.Empty];
            AddTaskInfo(task, t);
        }

        private static void AddTaskInfo(TaskQuery task, TaskInfo t)
        {
            switch (task.solution)
            {
                case Task.APPLY:
                case Task.INWORK:
                    t.inwork += 1;
                    break;
                case Task.RESOLVED:
                    t.done += 1;
                    break;
                case Task.NEW:
                case Task.REJECT:
                    t.undone += 1;
                    break;
            }
        }

        public void RemoveTask(string userid, TaskQuery task)
        {
            if (tasks.ContainsKey(userid))
                tasks[userid].Remove(task);

            if (!info.ContainsKey(task.userid))
                info[task.userid] = new TaskInfo();

            TaskInfo t = info[task.userid];
            DecTaskInfo(task, t);
            t = info[string.Empty];
            DecTaskInfo(task, t);
        }

        private static void DecTaskInfo(TaskQuery task, TaskInfo t)
        {
            switch (task.solution)
            {
                case Task.APPLY:
                case Task.INWORK:
                    t.inwork -= 1;
                    break;
                case Task.RESOLVED:
                    t.done -= 1;
                    break;
                case Task.NEW:
                case Task.REJECT:
                    t.undone -= 1;
                    break;
            }
        }

        public IEnumerable<Task> GetUserTasks(string userid)
        {
            List<TaskQuery> result = new List<TaskQuery>();

            if (tasks.ContainsKey(userid))
                result.AddRange(tasks[userid]);

            return result;
        }

        public int GetDoneTask(string userid)
        {
            int result = 0;

            if (info.ContainsKey(userid))
                result = info[userid].done;

            return result;
        }

        public int GetUnDoneTask(string userid)
        {
            int result = 0;

            if (info.ContainsKey(userid))
                result = info[userid].undone;

            return result;
        }

        public int GetInworkTask(string userid)
        {
            int result = 0;

            if (info.ContainsKey(userid))
                result = info[userid].inwork;

            return result;
        }
    }
}
