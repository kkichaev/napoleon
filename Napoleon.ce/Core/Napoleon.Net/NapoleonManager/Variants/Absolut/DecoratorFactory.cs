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
         if (form.GetType() == typeof(MainFormEx))
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

         ToolStripButton btnRep = new System.Windows.Forms.ToolStripButton();
         btnRep.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnRep.Image = Properties.Resources.accessorieseditor;
         btnRep.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnRep.Name = "btnRep";
         btnRep.Size = new System.Drawing.Size(23, 22);
         btnRep.Text = "Отчет";
         btnRep.Click += new System.EventHandler(rttRep_Click);

         form.tsbConfig.Items.Add(btnTask);
         form.tsbConfig.Items.Add(btnRep);

         btnTask = new System.Windows.Forms.ToolStripButton();
         btnTask.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnTask.Image = Properties.Resources.emblem_documents;
         btnTask.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnTask.Name = "btnMonitoringDBF";
         btnTask.Size = new System.Drawing.Size(23, 22);
         btnTask.Text = "Документы";
         btnTask.Click += new System.EventHandler((o, e) => FmDocsReport.Open());
         form.tsbConfig.Items.Add(btnTask);

         ToolStripButton btnFace = new System.Windows.Forms.ToolStripButton();
         btnFace.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnFace.Image = Properties.Resources.merch;
         btnFace.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnFace.Name = "btnFace";
         btnFace.Size = new System.Drawing.Size(23, 22);
         btnFace.Text = "Фейсинг";
         btnFace.Click += new System.EventHandler((o, e) => new FmFacing().Show());

         form.tsbConfig.Items.Add(btnFace);
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

      private void rttRep_Click(object sender, EventArgs e)
      {
         new FmRpt().Show();
      }
   }
}
