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

         ToolStripButton rttReport = new System.Windows.Forms.ToolStripButton();
         rttReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         rttReport.Image = Properties.Resources.plan_editor;
         rttReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         rttReport.Name = "rttPlans";
         rttReport.Size = new System.Drawing.Size(23, 22);
         rttReport.Text = "Планы";
         rttReport.Click += new System.EventHandler((o,e) => new FmAgentPlan().Show());

         form.tsbConfig.Items.Add(rttReport);
      }
   }
}
