using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public partial class FmSVTask : Form
   {
      public FmSVTask()
      {
         InitializeComponent();

         DataSet<string, TaskCategory> dsTaskCategory = (DataSet<string, TaskCategory>)DataModule.Get(TaskCategory.OBJECT_NAME);
         if (dsTaskCategory != null)
         {
            foreach (TaskCategory tc in dsTaskCategory.Data)
               category.Items.Add(tc.name);
         }
      }

      public SVTask Task
      {
         get
         {
            SVTask t = new SVTask();
            t.category = category.SelectedItem as string;
            t.flags = (int)AgentTask.Flags.SuperTask;
            t.date = DateTime.Now;
            t.execDate = DateTime.MinValue;
            t.text = task.Text;
            t.appointDate = date.Value;
            return t;
         }

         set
         {
            task.Text = value.text;
            date.Value = value.appointDate;
            category.SelectedItem = value.category;
         }
      }
   }
}
