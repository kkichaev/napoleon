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
         if (form is MainForm)
            return new MainFormDecorator((MainForm)form);
         if (form is Divisions)
            return new DivisionDecorator(form as Divisions);

         return new EmptyDecorator();
      }
   }

   class DivisionDecorator : EmptyDecorator
   {
      public DivisionDecorator(Divisions form)
      {
         ToolStripMenuItem tsDistrib = new ToolStripMenuItem("Матрица фокусного ассортимента");
         tsDistrib.Click += new EventHandler((o, e) => { Form f = new FmFocusMtx(); f.Show(); });
         form.tb.Items.Add(tsDistrib);
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

      }
   }
}
