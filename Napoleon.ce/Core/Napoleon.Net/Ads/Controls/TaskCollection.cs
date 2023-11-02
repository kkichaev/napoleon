using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   //public class TaskCollection : List<TaskHead>
   //{
   //   public event EventHandler Changed;

   //   Band owner;
   //   public TaskCollection(Band owner)
   //   {
   //      this.owner = owner;
   //   }

   //   new public void Add(TaskHead task)
   //   {
   //      base.Add(task);

   //      task.Band = owner;
   //      task.Changed += new EventHandler(task_Changed);

   //      if (Changed != null)
   //         Changed.Invoke(this, EventArgs.Empty);
   //   }

   //   new public void AddRange(IEnumerable<TaskHead> items)
   //   {
   //      base.AddRange(items);
   //      foreach (TaskHead task in items)
   //         task.Changed += new EventHandler(task_Changed);

   //      if (Changed != null)
   //         Changed.Invoke(this, EventArgs.Empty);
   //   }

   //   public void Replace(IEnumerable<TaskHead> items)
   //   {
   //      Clear(false);
   //      AddRange(items);
   //   }

   //   public void Clear(bool fireEvent)
   //   {
   //      foreach (TaskHead task in this)
   //         task.Changed -= new EventHandler(task_Changed);

   //      base.Clear();

   //      if (fireEvent && Changed != null)
   //         Changed.Invoke(this, EventArgs.Empty);
   //   }

   //   new public void Clear()
   //   {
   //      Clear(true);
   //   }

   //   new public void Remove(TaskHead task)
   //   {
   //      task.Changed -= new EventHandler(task_Changed);
   //      if (base.Remove(task) && Changed != null)
   //         Changed.Invoke(this, EventArgs.Empty);
   //   }

   //   void task_Changed(object sender, EventArgs e)
   //   {
   //      if (Changed != null)
   //         Changed.Invoke(this, EventArgs.Empty);
   //   }
   //}
}
