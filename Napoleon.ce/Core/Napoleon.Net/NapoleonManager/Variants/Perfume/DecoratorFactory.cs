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

   class DivisionDecorator : IDecorator
   {
      public DivisionDecorator(Form form)
      {
         Divisions divisions = (Divisions)form;
         ToolStripButton btnPrice = new ToolStripButton();
         btnPrice.Image = Resources.view_list_tree_2;
         btnPrice.ToolTipText = "Редактор номенклатурных групп";
         btnPrice.Click += new EventHandler(btnPrice_Click);
         divisions.tb.Items.Add(btnPrice);
      }

      void btnPrice_Click(object sender, EventArgs e)
      {
         new FmPriceEdit().Show();
      }

      #region IDecorator Members

      public void AdjustForm() { }

      public bool ExecFunction(FunctionArgsType args) {return false; }

      #endregion
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
         new FmAgentPlans().Show();
      }
   }


}
