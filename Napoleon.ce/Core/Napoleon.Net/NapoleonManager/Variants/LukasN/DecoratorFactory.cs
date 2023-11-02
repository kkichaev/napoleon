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

   class MainFormDecorator : EmptyDecorator
   {
      MainForm form;

      public MainFormDecorator(MainForm form)
      {
         this.form = form;

         

         ToolStripButton btnAgentPlan = new System.Windows.Forms.ToolStripButton();
         btnAgentPlan.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnAgentPlan.Image = Properties.Resources.view_statistics;
         btnAgentPlan.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnAgentPlan.Name = "btnAgentPlan";
         btnAgentPlan.Size = new System.Drawing.Size(23, 22);
         btnAgentPlan.Text = "План";
         btnAgentPlan.Click += new System.EventHandler((o,e) => new FmAgentPlan().Show());
         
         form.tsbConfig.Items.Add(btnAgentPlan);

         ToolStripButton rttReport = new System.Windows.Forms.ToolStripButton();
         rttReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         rttReport.Image = Properties.Resources.accessorieseditor;
         rttReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         rttReport.Name = "rttReport";
         rttReport.Size = new System.Drawing.Size(23, 22);
         rttReport.Text = "Выгрузка фотографий";
         rttReport.Click += new System.EventHandler(rttReport_Click);

         form.tsbConfig.Items.Add(rttReport);

      }

      private void rttReport_Click(object sender, EventArgs e)
      {
         if (form.CheckIsMainDataPresents(false))
            new FmExportPhoto().Show();
         else
            MessageBox.Show("Необходимо нажать кнопку обновить в ");
      }
   }
}
