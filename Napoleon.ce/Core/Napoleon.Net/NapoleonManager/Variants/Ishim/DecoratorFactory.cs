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
         if (formType == typeof(Divisions))
            return new DivisionDecorator((Divisions)form);

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
         btn.Name = "rttPlans";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "Планы";
         btn.Click += new System.EventHandler(rttPlans_Click);

         form.tsbConfig.Items.Add(btn);

         btn = new System.Windows.Forms.ToolStripButton();
         btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btn.Image = Properties.Resources.remnants_report;
         btn.ImageTransparentColor = System.Drawing.Color.Magenta;
         btn.Name = "rttRpt";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "Отчет по присутсвию";
         btn.Click += new System.EventHandler(rttRpt_Click);

         form.tsbConfig.Items.Add(btn);
      }

      private void rttPlans_Click(object sender, EventArgs e)
      {
         new FmAgentPlans().Show();
      }

      private void rttRpt_Click(object sender, EventArgs e)
      {
         new FmRemnReport().Show();
      }
   }

   class DivisionDecorator : EmptyDecorator
   {
      Divisions form;

      public DivisionDecorator(Divisions form)
      {
         this.form = form;
         ToolStripButton btnOTE = new System.Windows.Forms.ToolStripButton();
         btnOTE.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         btnOTE.Name = "btnOTE";
         btnOTE.Text = "Редакторы";
         btnOTE.Click += new System.EventHandler((o, e) => { new FmEditors().Show(); });

         form.tb.Items.Add(btnOTE);

         form.btnQuestion.Visible = false;
         form.btnPriceMonitoring.Visible = false;
         form.btnStopList.Visible = false;
      }
   }
}
