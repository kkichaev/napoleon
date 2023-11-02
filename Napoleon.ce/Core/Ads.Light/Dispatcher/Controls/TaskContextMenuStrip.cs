using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.Ads.Dispatcher
{
   public class TaskContextMenuStrip : ContextMenuStrip
   {
      private TaskHead task;

      protected override void OnItemClicked(ToolStripItemClickedEventArgs e)
      {
         if(e.ClickedItem != null)
         {
            ToolStripItem i = e.ClickedItem;
            i.Tag = task;
         }

         base.OnItemClicked(e);
      }

      public TaskHead Task
      {
         get { return task; }
         set { task = value; }
      }
   }
}
