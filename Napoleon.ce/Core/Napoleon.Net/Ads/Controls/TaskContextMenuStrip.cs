using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public class TaskContextMenuStrip : ContextMenuStrip
   {
      private BandItem task;

      protected override void OnItemClicked(ToolStripItemClickedEventArgs e)
      {
         if(e.ClickedItem != null)
         {
            ToolStripItem i = e.ClickedItem;
            i.Tag = task;
         }

         base.OnItemClicked(e);
      }

      public BandItem Task
      {
         get { return task; }
         set { task = value; }
      }
   }
}
