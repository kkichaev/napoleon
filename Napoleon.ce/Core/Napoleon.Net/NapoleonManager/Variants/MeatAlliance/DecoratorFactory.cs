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
         btnAgentPlan.Click += new System.EventHandler((o,e) => {new FmAgentPlan().Show();});

         ToolStripButton btn = new System.Windows.Forms.ToolStripButton();
         btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btn.Image = Properties.Resources.wsorder_doc;
         btn.ImageTransparentColor = System.Drawing.Color.Magenta;
         btn.Name = "btnOrderAnalize";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "Анализ продаж";
         btn.Click += new System.EventHandler((o, e) => { OrderAnalize.Open(); });
         
         ToolStripButton btnReturnReport = new System.Windows.Forms.ToolStripButton();
         btnReturnReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnReturnReport.Image = Properties.Resources.defects_doc;
         btnReturnReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnReturnReport.Name = "btnReturnReport";
         btnReturnReport.Size = new System.Drawing.Size(23, 22);
         btnReturnReport.Text = "Отчет по возвратам";
         btnReturnReport.Click += new System.EventHandler(btnReturnReport_Click);

         form.tsbConfig.Items.Add(btnAgentPlan);
         form.tsbConfig.Items.Add(btn);
         form.tsbConfig.Items.Add(btnReturnReport);
      }

      private void btnReturnReport_Click(object sender, EventArgs e)
      {
         FmReturnsReport.ShowInstance(null);
      }
   }
}
