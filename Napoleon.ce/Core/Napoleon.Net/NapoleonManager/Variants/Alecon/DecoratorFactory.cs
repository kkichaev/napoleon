using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.NapoleonManager.Utils;
using System.Windows.Forms;
using System.ComponentModel;
using System.Reflection;
using GRSoft.Network;
using System.Threading;
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

         return new EmptyDecorator();
      }
   }

   class MainFormDecorator : IDecorator
   {
      MainForm form;

      public MainFormDecorator(MainForm form)
      {
         this.form = form;

         ToolStripButton button = new System.Windows.Forms.ToolStripButton();
         button.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         button.Image = Properties.Resources.accessorieseditor;
         button.ImageTransparentColor = System.Drawing.Color.Magenta;
         button.Name = "rttReport";
         button.Size = new System.Drawing.Size(23, 22);
         button.Text = "Матрицы контрагентов";
         button.Click += new System.EventHandler(rttReport_Click);

         form.tsbConfig.Items.Add(button);

         button = new System.Windows.Forms.ToolStripButton();
         button.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         button.Image = Properties.Resources.actgs;
         button.ImageTransparentColor = System.Drawing.Color.Magenta;
         button.Name = "exportPhoto";
         button.Size = new System.Drawing.Size(23, 22);
         button.Text = "Выгрузка фотографий";
         button.Click += new System.EventHandler(exportPhoto_Click);

         form.tsbConfig.Items.Add(button);
      }

      public void AdjustForm() { }

      public bool ExecFunction(FunctionArgsType args) { return false; }

      private void rttReport_Click(object sender, EventArgs e)
      {
         new FmOrgMatrix().Show();
      }

      private void exportPhoto_Click(object sender, EventArgs e)
      {
         if (form.CheckIsMainDataPresents(false))
            new FmExportPhoto().Show();
         else
            MessageBox.Show("Необходимо нажать кнопку обновить в главном окне");
      }
   }
}
