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
            return new MainFormDecorator(form as MainForm);
         if (form is FmDetail)
            return new DetailDecorator(form as FmDetail);
         if (form is Divisions)
            return new DivisionDecorator(form as Divisions);
         return new EmptyDecorator();
      }
   }

   class MainFormDecorator : IDecorator
   {
      MainForm form;
      ToolStripMenuItem smiDailyRoute;

      public MainFormDecorator(MainForm form)
      {
         this.form = form;

         ToolStripButton rttReport = new System.Windows.Forms.ToolStripButton();
         rttReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         rttReport.Image = Properties.Resources.accessorieseditor;
         rttReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         rttReport.Name = "rttReport";
         rttReport.Size = new System.Drawing.Size(23, 22);
         rttReport.Text = "Отчет результативность посещений";
         rttReport.Click += new System.EventHandler((o, e) => { new FmRpt().Show(); });

         form.tsbConfig.Items.Add(rttReport);

      }

      #region Члены IDecorator

      public void AdjustForm()
      {
         smiDailyRoute = new ToolStripMenuItem();
         this.smiDailyRoute.Name = "smiRoute";
         this.smiDailyRoute.Size = new System.Drawing.Size(235, 22);
         this.smiDailyRoute.Text = "Редактирование доп.маршрута...";
         this.smiDailyRoute.Click += new System.EventHandler(this.smiDailyRoute_Click);

         int idx = form.menuAgentsSummary.Items.IndexOf(form.smiRoute);
         if (idx < 0)
            idx = form.menuAgentsSummary.Items.Count;
         else
            idx++;

         form.menuAgentsSummary.Items.Insert(idx, smiDailyRoute);
      }

      private void smiDailyRoute_Click(object sender, EventArgs e)
      {
         Agent a = form.GetSelectedAgent();
         FmDailyRouteEditor route = new FmDailyRouteEditor();
         if (a != null)
            route.SetCurrentAgent(a.id);
         route.Show();
      }

      public bool ExecFunction(FunctionArgsType args)
      {
         return false;
      }

      #endregion
   }

   class DetailDecorator : IDecorator
   {
      ToolStripMenuItem wtReport;
      ToolStripMenuItem pfReport;

      FmDetail form;

      public DetailDecorator(FmDetail f)
      {
         form = f;

         ToolStripItemCollection ic = f.tsReportMenu.DropDownItems;

         wtReport = new ToolStripMenuItem("Отчеты");
         wtReport.Click += new EventHandler(wtReport_Click);
         pfReport = new ToolStripMenuItem("План-Факт");
         pfReport.Click += new EventHandler(pfReport_Click);
         ic.Add(wtReport);
         ic.Add(pfReport);
      }

      void pfReport_Click(object sender, EventArgs e)
      {
         PlanFactReport.Do(form.GetDateForStartPeriod(), form.GetDateForEndPeriod(), form);
      }

      void wtReport_Click(object sender, EventArgs e)
      {
         WorkTimeReport.Do(form.GetDateForStartPeriod(), form.GetDateForEndPeriod(), form);
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
      Divisions form;

      public DivisionDecorator(Divisions form)
      {
         this.form = form;
         ToolStripButton btnOTE = new System.Windows.Forms.ToolStripButton();
         btnOTE.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         btnOTE.Name = "btnOTE";
         btnOTE.Text = "Редактор ТТ";
         btnOTE.Click += new System.EventHandler((o, e) => { new FmOrgTypeEditor().Show(); });

         form.tb.Items.Add(btnOTE);
      }

      #region Члены IDecorator

      public void AdjustForm()  {}

      public bool ExecFunction(FunctionArgsType args) { return false; }

      #endregion
   }
}

