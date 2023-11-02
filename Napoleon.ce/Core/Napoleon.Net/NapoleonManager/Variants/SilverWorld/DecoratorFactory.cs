using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.NapoleonManager.Utils;
using System.Windows.Forms;
using System.ComponentModel;
using System.Reflection;

namespace GRSoft.NapoleonManager
{
   class DecoratorFactory
   {
      public static IDecorator GetDecorator(Form form)
      {
         if (form.GetType() == typeof(MainForm))
            return new MainFormDecorator((MainForm)form);

         return new EmptyDecorator();
      }
   }

   class MainFormDecorator : IDecorator
   {
      MainForm form;

      public MainFormDecorator(MainForm form)
      {
         this.form = form;

         ToolStripButton btnTask = new System.Windows.Forms.ToolStripButton();
         btnTask.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnTask.Image = Properties.Resources.monitor_doc;
         btnTask.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnTask.Name = "btnMonitoringDBF";
         btnTask.Size = new System.Drawing.Size(23, 22);
         btnTask.Text = "Задачи";
         btnTask.Click += new System.EventHandler(rttTask_Click);

         form.tsbConfig.Items.Add(btnTask);
      }

      public void AdjustForm() { }

      public bool ExecFunction(FunctionArgsType args)
      {
         throw new Exception("The method or operation is not implemented.");
      }

      private void rttTask_Click(object sender, EventArgs e)
      {
         new FmAgentTask().Show();
      }
   }
}
