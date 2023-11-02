using GRSoft.NapoleonManager.Properties;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
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
      public override string Client { get { return Resources.parking; } }
      public override string Address { get { return address; } }
      public override string FactAddress
      {
         get
         {
            return Address;
         }
      }
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

      public override string TimePlan
      {
         get
         {
            DateTime dt = task.start;
            string s1 =  dt.Year < 1900 ? string.Empty : dt.ToShortTimeString();
            dt = task.finish;
            string s2 = dt.Year < 1900 ? string.Empty : dt.ToShortTimeString();

            return string.Format("{0} - {1}", s1, s2);
         }
      }

      public override string TimeFact 
      { 
         get 
         {
            DateTime dt = task.finishexec;
            return dt.Year < 1900 || task.solution != Task.RESOLVED ? string.Empty : dt.ToShortTimeString();  
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
