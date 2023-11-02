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
         if (form.GetType() == typeof(MainForm))
            return new MainFormDecorator((MainForm)form);

         return new EmptyDecorator();
      }
   }

   class MainFormDecorator : IDecorator
   {
      MainForm form;

      public MainFormDecorator(MainForm form)
      {
         form.smiRoute.Click -= new EventHandler(form.smiRoute_Click);
         form.smiRoute.Click += new EventHandler(smiRoute_Click);
         this.form = form;

         form.tgvAgentsSummaryCount.Visible = false;
         form.tgvAgentsSummaryProgres.Visible = false;
         form.tgvAgentsSummarySum.Visible = false;
         form.btnOrderReport.Visible = false;
         form.btnPriceRemnants.Visible = false;
         form.tsbMakeHtml.Visible = false;

         ToolStripButton rttReport = new System.Windows.Forms.ToolStripButton();
         rttReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         rttReport.Image = Properties.Resources.excel;
         rttReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         rttReport.Name = "rttReport";
         rttReport.Size = new System.Drawing.Size(23, 22);
         rttReport.Text = "Маршрут ТП за день";
         rttReport.Click += new System.EventHandler(rttReport_Click);

         ToolStripButton rttClearBase = new System.Windows.Forms.ToolStripButton();
         rttClearBase.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         rttClearBase.Image = Properties.Resources.edit_clear_4;
         rttClearBase.ImageTransparentColor = System.Drawing.Color.Magenta;
         rttClearBase.Name = "rttClearBase";
         rttClearBase.Size = new System.Drawing.Size(23, 22);
         rttClearBase.Text = "Удалить документы";
         rttClearBase.Click += new System.EventHandler(rttClearBase_Click);

         form.tsbConfig.Items.Add(rttReport);
         //form.tsbConfig.Items.Add(rttClearBase);
      }

      public void AdjustForm() { }

      public bool ExecFunction(FunctionArgsType args)
      {
         throw new Exception("The method or operation is not implemented.");
      }

      private void smiRoute_Click(object sender, EventArgs e)
      {
         FmRegionRoute.Show(form.GetSelectedAgent());
      }

      private void rttReport_Click(object sender, EventArgs e)
      {
         new FmQuestionReport().Show();
      }

      private void rttClearBase_Click(object sender, EventArgs e)
      {
         new FmClearBase().Show();
      }
   }
}
