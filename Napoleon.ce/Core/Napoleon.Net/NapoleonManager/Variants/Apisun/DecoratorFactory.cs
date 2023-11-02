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
         if (form.GetType() == typeof(MainFormEx))
            return new MainFormDecorator((MainForm)form);

         if (form.GetType() == typeof(FmCoverArea))
            return new FmCoverAreaDecorator((FmCoverArea)form);

         return new EmptyDecorator();
      }
   }

   class MainFormDecorator : IDecorator
   {
      MainForm form;

      public MainFormDecorator(MainForm form)
      {
         this.form = form;

#if MAIN_DEPARTMENT
         ToolStripButton btnOrg = new System.Windows.Forms.ToolStripButton();
         btnOrg.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnOrg.Image = Properties.Resources.monitor_doc;
         btnOrg.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnOrg.Name = "btnOrg";
         btnOrg.Size = new System.Drawing.Size(23, 22);
         btnOrg.Text = "Справочник организаций";
         btnOrg.Click += new System.EventHandler(btnOrg_Click);

         form.tsbConfig.Items.Add(btnOrg);
#endif

         ToolStripButton btnRep = new System.Windows.Forms.ToolStripButton();
         btnRep.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnRep.Image = Properties.Resources.emblem_documents;
         btnRep.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnRep.Name = "btnRep";
         btnRep.Size = new System.Drawing.Size(23, 22);
         btnRep.Text = "Отчет";
         btnRep.Click += new System.EventHandler(btnRep_Click);

         ToolStripButton btnRepRemn = new System.Windows.Forms.ToolStripButton();
         btnRepRemn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnRepRemn.Image = Properties.Resources.bookmarks_organize;
         btnRepRemn.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnRepRemn.Name = "btnRep";
         btnRepRemn.Size = new System.Drawing.Size(23, 22);
         btnRepRemn.Text = "Список организаций";
         btnRepRemn.Click += new System.EventHandler(btnRepRemn_Click);

         ToolStripButton btnRepPresent = new System.Windows.Forms.ToolStripButton();
         btnRepPresent.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnRepPresent.Image = Properties.Resources.draw_blend;
         btnRepPresent.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnRepPresent.Name = "btnRep";
         btnRepPresent.Size = new System.Drawing.Size(23, 22);
         btnRepPresent.Text = "Отчет по присутствию товара";
         btnRepPresent.Click += new System.EventHandler(btnRepPresent_Click);

         ToolStripButton btnRepAssortChange = new System.Windows.Forms.ToolStripButton();
         btnRepAssortChange.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnRepAssortChange.Image = Properties.Resources.assort_ch;
         btnRepAssortChange.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnRepAssortChange.Name = "btnRepAssortChange";
         btnRepAssortChange.Size = new System.Drawing.Size(23, 22);
         btnRepAssortChange.Text = "Отчет по изменению ассортимента";
         btnRepAssortChange.Click += new System.EventHandler(btnRepAssortChange_Click);

         ToolStripButton btnRepClientCard = new System.Windows.Forms.ToolStripButton();
         btnRepClientCard.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnRepClientCard.Image = Properties.Resources.client_card;
         btnRepClientCard.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnRepClientCard.Name = "btnRepClientCard";
         btnRepClientCard.Size = new System.Drawing.Size(23, 22);
         btnRepClientCard.Text = "Отчет карта клиента";
         btnRepClientCard.Click += new System.EventHandler(btnRepClientCard_Click);

         ToolStripButton btnPlanogramEdit = new System.Windows.Forms.ToolStripButton();
         btnPlanogramEdit.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnPlanogramEdit.Image = Properties.Resources.planogram_edit;
         btnPlanogramEdit.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnPlanogramEdit.Name = "btnPlanogramEdit";
         btnPlanogramEdit.Size = new System.Drawing.Size(23, 22);
         btnPlanogramEdit.Text = "Редактор планограмм";
         btnPlanogramEdit.Click += new System.EventHandler((o, e) => { new FmPlanogramEdit().Show(); });

         ToolStripButton btnVisitReport = new System.Windows.Forms.ToolStripButton();
         btnVisitReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnVisitReport.Image = Properties.Resources.visit_report;
         btnVisitReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnVisitReport.Name = "btnVisitReport";
         btnVisitReport.Size = new System.Drawing.Size(23, 22);
         btnVisitReport.Text = "Отчет о визитах";
         btnVisitReport.Click += new System.EventHandler((o, e) => { new FmVisitReport().Show(); });

         form.tsbConfig.Items.Add(btnRep);
         form.tsbConfig.Items.Add(btnRepRemn);
         form.tsbConfig.Items.Add(btnRepPresent);
         form.tsbConfig.Items.Add(btnRepAssortChange);
         form.tsbConfig.Items.Add(btnRepClientCard);
         form.tsbConfig.Items.Add(btnPlanogramEdit);
         form.tsbConfig.Items.Add(btnVisitReport);

         form.btnCensus.Visible = false;
         form.btnOrderReport.Visible = false;
         form.tgvAgentsSummaryCount.Visible = false;
         form.tgvAgentsSummarySum.Visible = false;
         form.tgvAgentsSummaryProgres.Visible = false;
         form.tsbMakeHtml.Visible = false;
      }

      private void btnRepClientCard_Click(object sender, EventArgs e)
      {
         ClientCardReport.Do(form);
      }

      public void AdjustForm() {
         form.menuAgentsSummary.Items.Remove(form.smiRoute);
      }

      public bool ExecFunction(FunctionArgsType args)
      {
         throw new Exception("The method or operation is not implemented.");
      }

      private void btnOrg_Click(object sender, EventArgs e)
      {
         new FmOrg().Show();
      }

      private void btnRep_Click(object sender, EventArgs e)
      {
         new FmReport().Show();
      }

      private void btnRepRemn_Click(object sender, EventArgs e)
      {
         new FmReportOrg().Show();
      }

      private void btnRepPresent_Click(object sender, EventArgs e)
      {
         new FmReportPresent().Show();
      }

      private void btnRepAssortChange_Click(object sender, EventArgs e)
      {
         new FmReportAssortChanges().Show();
      }
   }

   class FmCoverAreaDecorator : IDecorator
   {
      public FmCoverAreaDecorator(FmCoverArea form)
      {
         form.btnOrder.Visible = false;
         form.btnMove.Visible = false;
         form.btnSales.Visible = false;
         form.btnQuestion.Visible = false;
         form.btnMove.Visible = false;
         form.btnIncass.Visible = false;
         form.btnOnlyFromRoute.Visible = false;
         form.toolStripSeparator3.Visible = false;
      }

      #region IDecorator Members

      public void AdjustForm()
      {
         throw new NotImplementedException();
      }

      public bool ExecFunction(FunctionArgsType args)
      {
         throw new NotImplementedException();
      }

      #endregion
   }

}
