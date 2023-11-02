using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.NapoleonManager.Utils;
using System.Windows.Forms;
using System.ComponentModel;
using System.Reflection;
using GRSoft.NapoleonManager.Reports;

namespace GRSoft.NapoleonManager
{
   class DecoratorFactory
   {
      public static IDecorator GetDecorator(Form form)
      {
         Type formType = form.GetType();
         if (formType == typeof(Divisions))
            return new DivisionDecorator(form);
         if (formType == typeof(FmDetail))
            return new DetailDecorator((FmDetail)form);
         return new EmptyDecorator();
      }
   }

   class DetailDecorator : IDecorator
   {
      ToolStripMenuItem salesHistoryReport;
      ToolStripMenuItem dailyReport;
      ToolStripMenuItem clientCardReport;

      FmDetail form;

      public DetailDecorator(FmDetail f)
      {
         form = f;

         ToolStripItemCollection ic = f.tsReportMenu.DropDownItems;

         salesHistoryReport = new ToolStripMenuItem("История продаж");
         salesHistoryReport.Click += new EventHandler(SalesHistoryReport);

         dailyReport = new ToolStripMenuItem("Ежедневный отчет");
         dailyReport.Click += new EventHandler(DailyReport);

         clientCardReport = new ToolStripMenuItem("Задачи");
         clientCardReport.Click += new EventHandler(ClientCardReport);

         ic.Clear();
         ic.AddRange(new System.Windows.Forms.ToolStripItem[] {
            f.tsbMakeHtml, salesHistoryReport, dailyReport, clientCardReport});
      }

      private void SalesHistoryReport(object sender, EventArgs e)
      {
         SalesHistory sh = new SalesHistory();
         ReportData data = sh.MakeData(form);
         if (data != null)
         {
            sh.Build(data);
            if( sh.IsPrepared )
               sh.Show();
         }
      }

      private void DailyReport(object sender, EventArgs e)
      {
         DailyReport dr = new DailyReport();
         ReportData data = dr.MakeData(form);
         if (data != null)
         {
            dr.Build(data);
            if (dr.IsPrepared)
               dr.Show();
         }
      }

      private void ClientCardReport(object sender, EventArgs e)
      {
         ClientCardReport sh = new ClientCardReport();
         ReportData data = sh.MakeData(form);
         if (data != null)
         {
            sh.Build(data);
            if (sh.IsPrepared)
               sh.Show();
         }
      }

      void IDecorator.AdjustForm()
      {
      }

      bool IDecorator.ExecFunction(FunctionArgsType args)
      {
         return false;
      }
   }

   class DivisionDecorator : IDecorator
   {
      public DivisionDecorator(Form f)
      {
         Divisions df = (Divisions)f;
         ToolStripButton tb = new ToolStripButton();
         tb.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         tb.Name = "tb";
         tb.Size = new System.Drawing.Size(101, 22);
         tb.Text = "Фокусные товары";
         tb.Click += new System.EventHandler(EditFocusedGroup);

         df.GetToolStrip().Items.Add(tb);
      }

      void EditFocusedGroup(object sender, EventArgs e)
      {
         ProvFocusedGroupEditor f = new ProvFocusedGroupEditor();
         f.Show();
      }

      public void AdjustForm()
      {
         
      }

      public bool ExecFunction(FunctionArgsType args)
      {
         return false;
      }
   }
}
