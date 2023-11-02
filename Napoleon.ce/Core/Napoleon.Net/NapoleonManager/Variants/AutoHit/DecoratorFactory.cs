using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.NapoleonManager.Utils;
using System.Windows.Forms;
using System.ComponentModel;
using System.Reflection;

namespace GRSoft.NapoleonManager
{
   class DecoratorFactory
   {
      public static IDecorator GetDecorator(Form form)
      {
         if (form is MainForm)
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

         ToolStripButton btnMerchReport = new System.Windows.Forms.ToolStripButton();

         btnMerchReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnMerchReport.Image = Properties.Resources.taskdoc;
         btnMerchReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnMerchReport.Name = "btnMerchReport";
         btnMerchReport.Size = new System.Drawing.Size(23, 22);
         btnMerchReport.ToolTipText = "Отчёт по мерчендайзингу";
         btnMerchReport.Click += new System.EventHandler(btnMerchReport_Click);

         form.tsbConfig.Items.Add(btnMerchReport);
         
         ToolStripButton tsb = new ToolStripButton();
         tsb.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(MainForm));
         tsb.Image = ((System.Drawing.Image)(resources.GetObject("btnPriceRemnants.Image")));
         tsb.ImageTransparentColor = System.Drawing.Color.Magenta;
         tsb.Name = "btnPriceRemnants";
         tsb.Size = new System.Drawing.Size(23, 22);
         tsb.Text = "Прайс";
         tsb.Click += new EventHandler((o, e) =>
         {
            FmPriceViewAH f = new FmPriceViewAH();
            f.Show();
         });
         //form.tsbConfig.Items.Add(tsb);
      }

      private void btnMerchReport_Click(object sender, EventArgs e)
      {
         Division selectedDivision = form.GetSelectedDivision();
         if (selectedDivision != null)
            FmMerchReport.ShowInstance(selectedDivision);
      }
   }
}
