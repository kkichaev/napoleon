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
         Type formType = form.GetType();

         if (formType == typeof(Divisions))
            return new DivisionsDecorator((Divisions)form);
         else if (formType == typeof(MainForm))
            return new MainFormDecorator((MainForm)form);

         return new EmptyDecorator();
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
            btnMonitoringDBF.Text = "Отчет по остаткам";
            btnMonitoringDBF.Click += new System.EventHandler(rttReport_Click);

            form.tsbConfig.Items.Add(btnMonitoringDBF);
            form.tsbMakeHtml.Visible = false;
            form.btnOrderReport.Visible = false;
         }

         public void AdjustForm() { }

         public bool ExecFunction(FunctionArgsType args)
         {
            throw new Exception("The method or operation is not implemented.");
         }

         private void rttReport_Click(object sender, EventArgs e)
         {
            new FmFaceReport().Show();
         }
      }


      class DivisionsDecorator : IDecorator
      {
         Divisions form;

         public DivisionsDecorator(Divisions form)
         {
            this.form = form;

            ToolStripButton btnOrgMatrix = new System.Windows.Forms.ToolStripButton();
            btnOrgMatrix.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            btnOrgMatrix.Image = Properties.Resources.accessorieseditor;
            btnOrgMatrix.ImageTransparentColor = System.Drawing.Color.Magenta;
            btnOrgMatrix.Name = "btnOrgMatrix";
            btnOrgMatrix.Size = new System.Drawing.Size(23, 22);
            btnOrgMatrix.Text = "Матрицы контрагентов";
            btnOrgMatrix.Click += new System.EventHandler(rttReport_Click);

            form.tb.Items.Add(btnOrgMatrix);

            form.tsbMatrixDesigner.Visible = false;
         }

         public void AdjustForm() { }

         public bool ExecFunction(FunctionArgsType args) { return false; }

         private void rttReport_Click(object sender, EventArgs e)
         {
            new FmOrgMatrixDesigner().Show();
         }
      }

   }
}
