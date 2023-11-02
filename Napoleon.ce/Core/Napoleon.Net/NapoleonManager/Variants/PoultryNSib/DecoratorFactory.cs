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
         rttReport.Name = "rttReport";
         rttReport.Size = new System.Drawing.Size(23, 22);
         rttReport.Text = "Матрицы контрагентов";
         rttReport.Click += new System.EventHandler(rttReport_Click);

         ToolStripButton btnRetMtx = new System.Windows.Forms.ToolStripButton();
         btnRetMtx.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnRetMtx.Image = Properties.Resources.view_list_tree_2;
         btnRetMtx.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnRetMtx.Name = "btnRetMtx";
         btnRetMtx.Size = new System.Drawing.Size(23, 22);
         btnRetMtx.Text = "Матрицы возвратов";
         btnRetMtx.Click += new System.EventHandler((o, e) => { new FmRetMatrix().Show(); });

         ToolStripButton btnRouteApproval = new System.Windows.Forms.ToolStripButton();
         btnRouteApproval.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnRouteApproval.Image = Properties.Resources.preferences;
         btnRouteApproval.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnRouteApproval.Name = "btnRouteApproval";
         btnRouteApproval.Size = new System.Drawing.Size(23, 22);
         btnRouteApproval.Text = "Утверждение маршрута";
         btnRouteApproval.Click += new System.EventHandler(btnRouteApproval_Click);

         ToolStripButton btnAgentPlan = new System.Windows.Forms.ToolStripButton();
         btnAgentPlan.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnAgentPlan.Image = Properties.Resources.view_statistics;
         btnAgentPlan.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnAgentPlan.Name = "btnAgentPlan";
         btnAgentPlan.Size = new System.Drawing.Size(23, 22);
         btnAgentPlan.Text = "План";
         btnAgentPlan.Click += new System.EventHandler(btnAgentPlan_Click);

         ToolStripButton btnReturnReport = new System.Windows.Forms.ToolStripButton();
         btnReturnReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnReturnReport.Image = Properties.Resources.defects_doc;
         btnReturnReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnReturnReport.Name = "btnReturnReport";
         btnReturnReport.Size = new System.Drawing.Size(23, 22);
         btnReturnReport.Text = "Отчет по возвратам";
         btnReturnReport.Click += new System.EventHandler(btnReturnReport_Click);

         ToolStripButton btnForsakeReturn = new System.Windows.Forms.ToolStripButton();
         btnForsakeReturn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnForsakeReturn.Image = Properties.Resources.return_doc;
         btnForsakeReturn.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnForsakeReturn.Name = "btnForsakeReturn";
         btnForsakeReturn.Size = new System.Drawing.Size(23, 22);
         btnForsakeReturn.Text = "Отложенные возвраты";
         btnForsakeReturn.Click += new System.EventHandler(btnForsakeReturn_Click);

         ToolStripButton btnExport = new System.Windows.Forms.ToolStripButton();
         btnExport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnExport.Image = Properties.Resources.assort_ch;
         btnExport.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnExport.Name = "btnExport";
         btnExport.Size = new System.Drawing.Size(23, 22);
         btnExport.Text = "Выгрузка фотографий";
         btnExport.Click += new System.EventHandler(btnExport_Click);

         form.tsbConfig.Items.Add(rttReport);
         form.tsbConfig.Items.Add(btnRetMtx);
         form.tsbConfig.Items.Add(btnRouteApproval);
         form.tsbConfig.Items.Add(btnAgentPlan);
         form.tsbConfig.Items.Add(btnReturnReport);
         form.tsbConfig.Items.Add(btnForsakeReturn);
         form.tsbConfig.Items.Add(btnExport);
      }

      public void AdjustForm() { }

      public bool ExecFunction(FunctionArgsType args) { return false; }

      private void rttReport_Click(object sender, EventArgs e)
      {
         new FmOrgMatrix().Show();
      }

      private void btnRouteApproval_Click(object sender, EventArgs e)
      {
         new FmRouteApproval().Show();
      }

      private void btnAgentPlan_Click(object sender, EventArgs e)
      {
         new FmAgentPlan().Show();
      }

      private void btnReturnReport_Click(object sender, EventArgs e)
      {
         FmReturnsReport.ShowInstance(null);
      }

      private void btnForsakeReturn_Click(object sender, EventArgs e)
      {
         new FmForsakeReturns().Show();
      }

      private void btnExport_Click(object sender, EventArgs e)
      {
         if (form.CheckIsMainDataPresents(false))
            new FmExportPhoto().Show();
         else
            MessageBox.Show("Необходимо нажать кнопку обновить в ");
      }

   }
}
