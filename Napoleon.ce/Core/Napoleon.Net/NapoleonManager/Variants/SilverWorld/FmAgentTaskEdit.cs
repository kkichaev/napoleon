using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmAgentTaskEdit : Form
   {
      public static OrgTask EditTask(OrgTask task)
      {
         OrgTask result = task;
         FmAgentTaskEdit form = new FmAgentTaskEdit();

         if (task != null)
         {
            form.dtpStart.Value = task.start;
            form.dtpFinish.Value = task.finish;
            form.tbTask.Text = task.text;
         }

         if (form.ShowDialog() == DialogResult.OK)
         {
            if (task == null)
            {
               result = new OrgTask();
               result.id = GRSoft.Network.DataObject.GenId();
            }

            result.start = form.dtpStart.Value.Date;
            result.finish = form.dtpFinish.Value.Date;
            result.text = form.tbTask.Text;
         }

         return result;
      }

      private FmAgentTaskEdit()
      {
         InitializeComponent();
      }
   }
}
