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

         if (typeof(FmDetailBase).IsAssignableFrom(formType))
            return new DetailFormDecorator((FmDetailBase)form);

         return new EmptyDecorator();
      }
   }

   class DetailFormDecorator : EmptyDecorator
   {
      public DetailFormDecorator(FmDetailBase form)
      {
         DataGridViewTextBoxColumn clmn1 = new DataGridViewTextBoxColumn();
         clmn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmn1.DataPropertyName = "Number";
         clmn1.FillWeight = 100F;
         clmn1.HeaderText = "Номер";
         clmn1.Name = "dgvNotes";

         form.dgvDetail.Columns.Insert(3, clmn1);
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
         rttReport.Image = Properties.Resources.plan_editor;
         rttReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         rttReport.Name = "rttPlans";
         rttReport.Size = new System.Drawing.Size(23, 22);
         rttReport.Text = "Планы";
         rttReport.Click += new System.EventHandler(rttPlans_Click);

         form.tsbConfig.Items.Add(rttReport);
      }

      public void AdjustForm() { }

      public bool ExecFunction(FunctionArgsType args) { return false; }

      private void rttPlans_Click(object sender, EventArgs e)
      {
         new FmAgentPlan().Show();
      }
   }
}
