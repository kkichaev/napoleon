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

         if (formType == typeof(MainFormEx))
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

         ToolStripButton btn = new System.Windows.Forms.ToolStripButton();
         btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btn.Image = Properties.Resources.plan_editor;
         btn.ImageTransparentColor = System.Drawing.Color.Magenta;
         btn.Name = "btnAgentPlan";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "План";
         btn.Click += new System.EventHandler((o,e) => {new FmAgentPlan().Show();});

         form.tsbConfig.Items.Add(btn);
      }

      private static void BtnWorkReport2Click()
      {
         FmWorkReport f = new FmWorkReport();
         f.ReportName = "work_merch_report";
         f.Show();
      }
   }
}
