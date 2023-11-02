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
         else if(formType == typeof(FmScriptEdit))
            return new FmScriptEditDecorator((FmScriptEdit)form);

         return new EmptyDecorator();
      }
   }

   class MainFormDecorator : IDecorator
   {
      MainForm form;

      public MainFormDecorator(MainForm form)
      {
         this.form = form;

         //form.tgvAgentsSummaryCount.Visible = false;
         //form.tgvAgentsSummaryProgres.Visible = false;
         //form.tgvAgentsSummarySum.Visible = false;
         //form.btnOrderReport.Visible = false;
         //form.btnPriceRemnants.Visible = false;
         //form.tsbMakeHtml.Visible = false;

         ToolStripButton rttReport = new System.Windows.Forms.ToolStripButton();
         rttReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         rttReport.Image = Properties.Resources.excel;
         rttReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         rttReport.Name = "rttReport";
         rttReport.Size = new System.Drawing.Size(23, 22);
         rttReport.Text = "Отчёт по анкетам";
         rttReport.Click += new System.EventHandler(rttReport_Click);

         //ToolStripButton rttGPSTrackReport = new System.Windows.Forms.ToolStripButton();
         //rttGPSTrackReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         //rttGPSTrackReport.Image = Properties.Resources.software_update_current;
         //rttGPSTrackReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         //rttGPSTrackReport.Name = "rttGPSTrackReport";
         //rttGPSTrackReport.Size = new System.Drawing.Size(23, 22);
         //rttGPSTrackReport.Text = "Трек";
         //rttGPSTrackReport.Click += new System.EventHandler(rttGpsReport_Click);

         //ToolStripButton rttClearBase = new System.Windows.Forms.ToolStripButton();
         //rttClearBase.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         //rttClearBase.Image = Properties.Resources.edit_clear_4;
         //rttClearBase.ImageTransparentColor = System.Drawing.Color.Magenta;
         //rttClearBase.Name = "rttClearBase";
         //rttClearBase.Size = new System.Drawing.Size(23, 22);
         //rttClearBase.Text = "Удалить документы";
         //rttClearBase.Click += new System.EventHandler(rttClearBase_Click);

         form.tsbConfig.Items.Add(rttReport);
         //form.tsbConfig.Items.Add(rttGPSTrackReport);
         //form.tsbConfig.Items.Add(rttClearBase);
      }

      public void AdjustForm() { }

      public bool ExecFunction(FunctionArgsType args) { return false; }

      private void rttReport_Click(object sender, EventArgs e)
      {
         FmQuestionReport form = new FmQuestionReport();
         form.shortAddr = true;
         form.Show();
      }

      private void rttGpsReport_Click(object sender, EventArgs e)
      {
         new FmGPSReport().Show();
      }
   }

   class FmScriptEditDecorator: IDecorator
   {
      DataSet<string, Question> dsQuestion = (DataSet<string, Question>)DataModule.Get(Question.OBJECT_NAME) ??
            new DataSet<string, Question>(Question.OBJECT_NAME);
      FmScriptEdit form;

      public FmScriptEditDecorator(FmScriptEdit form)
      {
         this.form = form;
         form.Visible = false;
         form.Load -= new EventHandler(form.FmScriptEdit_Load);
         Refreshing();
      }

      void Refreshing()
      {
         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsQuestion);
         DataModule.SetDataRepsonceHandlers(DataModule_DataProcessed,
            DataModule_OnDataResponceError);

         FmWait.ShowForm(form,
            DataModule.RefreshGiveSets(Config.GetConfig().
            GetConnection(), list, FmWait.ProgressIndicator));
      }

      void DataModule_DataProcessed(object sender, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         form.Invoke(new InvokeDelegate(delegate() { RefreshData();}));
      }

      void DataModule_OnDataResponceError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         MessageBox.Show(e.Msg);
      }


      private void RefreshData()
      {
         int idx = form.lvDocsAvail.Items.Count;
         const string QUEST_TYPE = "Answer";

         foreach (Question quest in dsQuestion.Data)
         {
            QuestionDoc doc = new QuestionDoc(quest.idquest);
            form.imageList1.Images.Add(Resources.quest_doc);
            ListViewItem item = form.lvDocsAvail.Items.Add(QUEST_TYPE, String.Format("Анкета {0}", quest.Name), idx);
            item.Tag = doc;
            idx++;
         }

         form.FmScriptEdit_Load(null, null);
      }

      public void AdjustForm() { }
      public bool ExecFunction(FunctionArgsType args) { return false; }
   }
}
