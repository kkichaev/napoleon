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

         ToolStripButton btnMonitoringDBF = new System.Windows.Forms.ToolStripButton();
         btnMonitoringDBF.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnMonitoringDBF.Image = Properties.Resources.monitor_doc;
         btnMonitoringDBF.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnMonitoringDBF.Name = "btnMonitoringDBF";
         btnMonitoringDBF.Size = new System.Drawing.Size(23, 22);
         btnMonitoringDBF.Text = "Выгрузка в DBF мониторинг";
         btnMonitoringDBF.Click += new System.EventHandler(rttReport_Click);

         form.tsbConfig.Items.Add(btnMonitoringDBF);
      }

      public void AdjustForm() { }

      public bool ExecFunction(FunctionArgsType args)
      {
         throw new Exception("The method or operation is not implemented.");
      }

      private void rttReport_Click(object sender, EventArgs e)
      {
         new FmMonitoringDBF().Show();
      }
   }

}
