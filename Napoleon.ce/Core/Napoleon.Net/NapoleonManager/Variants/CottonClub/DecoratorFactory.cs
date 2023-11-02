using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.NapoleonManager.Utils;
using System.Windows.Forms;
using System.ComponentModel;
using System.Reflection;
using GRSoft.NapoleonManager.Properties;

namespace GRSoft.NapoleonManager
{
   class DecoratorFactory
   {
      public static IDecorator GetDecorator(Form form)
      {
         Type formType = form.GetType();

         if (formType == typeof(MainForm))
            return new MainFormDecorator((MainForm)form);
         else if (formType == typeof(Divisions))
            return new DivisionDecorator(form);

         return new EmptyDecorator();
      }
   }

   class MainFormDecorator : IDecorator
   {
      MainForm form;

      public MainFormDecorator(MainForm form)
      {
         this.form = form;

         ToolStripButton rttReport = new System.Windows.Forms.ToolStripButton();
         rttReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         rttReport.Image = Properties.Resources.accessorieseditor;
         rttReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         rttReport.Name = "rttReport";
         rttReport.Size = new System.Drawing.Size(23, 22);
         rttReport.Text = "Отчеты";
         rttReport.Click += new System.EventHandler(rttReport_Click);
         form.tsbConfig.Items.Add(rttReport);

         form.tsbMakeHtml.Visible = false;
         form.btnOrderReport.Visible = false;
      }

      public void AdjustForm() { }

      public bool ExecFunction(FunctionArgsType args) { return false; }

      private void rttReport_Click(object sender, EventArgs e)
      {
         new FmReports().Show();
      }
   }

   class DivisionDecorator : IDecorator
   {
      public DivisionDecorator(Form form)
      {
         Divisions divisions = (Divisions)form;
         
         ToolStripButton btnTypeEdit = new ToolStripButton();
         btnTypeEdit.Text = "Редактор типов акций";
         btnTypeEdit.Click += new EventHandler(btnTypeEdit_Click);

         //ToolStripButton btnActionEdit = new ToolStripButton();
         //btnActionEdit.Text = "Редактор акций";
         //btnActionEdit.Click += new EventHandler(btnActionEdit_Click);

         divisions.tb.Items.Add(btnTypeEdit);
         //divisions.tb.Items.Add(btnActionEdit);
      }

      void btnActionEdit_Click(object sender, EventArgs e)
      {
         new FmAction().Show();
      }

      void btnTypeEdit_Click(object sender, EventArgs e)
      {
         new FmTypeActionEdit().Show();
      }

      #region IDecorator Members

      public void AdjustForm() { }

      public bool ExecFunction(FunctionArgsType args) {return false; }

      #endregion
   }
}
