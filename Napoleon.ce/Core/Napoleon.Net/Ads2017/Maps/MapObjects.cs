using System;

namespace Ads2017
{
   class MapObj
   {
      public int idx = 0;
      public DateTime date = DateTime.Now;
      public double accuracy = 0.0;
      public double longitude = 0.0;
      public double latitude = 0.0;
      public double speed = 0.0;
      public bool isNearest = true;

      public int Num { get { return idx; } }
      public virtual string Client { get {return string.Empty;} }
      public virtual string TimePlan { get { return date.ToShortTimeString(); } }
      public virtual string TimeFact { get { return string.Empty; } }
      public virtual string Address { get { return string.Empty; } }
      public virtual string FactAddress { get { return string.Empty; } }
   }

   class StopPoint : MapObj
   {
      public DateTime endTime = DateTime.Now;
      public string address = string.Empty;
      public override string Client { get { return "Остановка"; } }
      public override string Address { get { return address; } }
      public override string FactAddress
      {
         get
         {
            return Address;
         }
      }

      public override string TimeFact
      {
         get
         {
            return FormatHelper.Instance.Range(date, endTime);
         }
      }

      public double TimeRange { get { return (endTime - date).TotalMinutes; } }
   }

   class TaskPoint : MapObj
   {
      public TaskQuery task = null;
      public string factAddress = string.Empty;

      public override string Client { get { return task.client; } }

      public TaskPoint(TaskQuery task)
      {
         this.task = task;
      }

      private String FmtDatePerion(DateTime d1, DateTime d2)
      {
         return FormatHelper.Instance.Range(d1, d2);
      }

      public override string TimePlan
      {
         get
         {
            return FmtDatePerion(task.start, task.finish);
         }
      }

      public override string TimeFact 
      { 
         get 
         {
            string result = string.Empty;

            if (task.solution == Task.RESOLVED)
               result = FmtDatePerion(task.startexec, task.finishexec);

            return result;
         }
      }

      public override string Address { get { return task.address; } }

      public override string FactAddress 
      { 
         get 
         {
            return factAddress;
         } 
      }

      private DateTime GetLastTime()
      {
         DateTime res = task.created;

         if (task is TaskQuery)
         { 
            TaskQuery tq = (TaskQuery)task;

            if (tq.startexec > res)
               res = tq.startexec;

            if (tq.finishexec > res)
               res = tq.finishexec;
         }

         return res;
      }
   }
}
