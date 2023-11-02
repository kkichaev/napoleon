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

         form.tgvAgentsSummary.Columns[2].HeaderText = "Продажи";
         form.btnCensus.Visible = false;
         ToolStripButton rttReport = new System.Windows.Forms.ToolStripButton();
         rttReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         rttReport.Image = Properties.Resources.qty2report;
         rttReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         rttReport.Name = "rttReport";
         rttReport.Size = new System.Drawing.Size(23, 22);
         rttReport.Text = "Отчёт по продажам";
         rttReport.Click += (o, e) => {
            Form fm = new FmAvalonSalesReport();
            fm.Show();
         };
         form.tsbConfig.Items.Add(rttReport);

         rttReport = new System.Windows.Forms.ToolStripButton();
         rttReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         rttReport.Image = Properties.Resources.bonus_doc;
         rttReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         rttReport.Name = "rttReport";
         rttReport.Size = new System.Drawing.Size(23, 22);
         rttReport.Text = "Раппорт о неисправностях";
         rttReport.Click += (o, e) =>
         {
            FmDefectReport fm = new FmDefectReport();
            fm.Begin = form.GetBeginDateForSelection().Date;
            fm.End = form.GetRangeEndDate().Date;
            //fm.Agent = form.GetSelectedAgent();

            fm.Show();
         };
         form.tsbConfig.Items.Add(rttReport);
      }

   }
}
