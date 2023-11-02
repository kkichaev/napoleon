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
using System.Runtime.InteropServices;
using System.IO;

namespace GRSoft.NapoleonManager
{
   class DecoratorFactory
   {
      public static IDecorator GetDecorator(Form form)
      { 
         Type formType = form.GetType();

         if (formType == typeof(MainForm))
            return new MainFormDecorator((MainForm)form);
         else 
         if (formType == typeof(FmScriptEdit))
            return new FmScriptEditDecorator((FmScriptEdit)form);

         return new EmptyDecorator();
      }
   }

   class MainFormDecorator : IDecorator
   {
      [DllImport("shell32.dll")]
      static extern IntPtr ShellExecute(IntPtr hwnd, string lpOperation, string lpFile, string lpParameters, string lpDirectory, int nShowCmd);

      MainForm form;

      public MainFormDecorator(MainForm form)
      {
         this.form = form;

         ToolStripButton rttReport = new System.Windows.Forms.ToolStripButton();
         rttReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         rttReport.Image = Properties.Resources.qty2report;
         rttReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         rttReport.Name = "qty2report";
         rttReport.Size = new System.Drawing.Size(23, 22);
         rttReport.Text = "Товар под заказ производителю";
         rttReport.Click += new System.EventHandler(qty2Report_Click);

         ToolStripButton dlvrpt = new System.Windows.Forms.ToolStripButton();
         dlvrpt.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         dlvrpt.Image = Properties.Resources.debt_doc;
         dlvrpt.ImageTransparentColor = System.Drawing.Color.Magenta;
         dlvrpt.Name = "dlvrpt";
         dlvrpt.Size = new System.Drawing.Size(23, 22);
         dlvrpt.Text = "Отчет по накладным";
         dlvrpt.Click += new System.EventHandler(dlvrpt_Click);

         form.tsbConfig.Items.Add(rttReport);
         form.tsbConfig.Items.Add(dlvrpt);
      }

      private void dlvrpt_Click(object sender, EventArgs e)
      {
         Param param = new Param();
         Manager dm = CurrentUser.user as Manager;

         if (dm != null)
         {
            param.uid = DataUtils.MakeFilterFromAgents(null, dm.GetAgents());
            const string MODULE_NAME = "dlvrpt";
            Result result = new Result();
            SimpleDataSet<Result> resultSet = new SimpleDataSet<Result>("Result", false);
            Report r = new Report(MODULE_NAME, param, resultSet);

            Thread th = DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), r, FmWait.ProgressIndicator);
            FmWait.ShowForm(form, th);
            th.Join();
            FmWait.CloseForm();

            if (resultSet.Count > 0)
            {
               try
               {
                  Result res = resultSet[0];
                  if (res.file.Length > 0)
                  {
                     string fileName = Path.GetTempPath() + "\\" + MODULE_NAME + ".xlsx";
                     File.WriteAllBytes(fileName, res.file);

                     ShellExecute(IntPtr.Zero, "open", fileName, "", "", 1);
                  }
               }
               catch (Exception excetion)
               {
                  MainForm.Instance.Invoke(new EmptyParamHandler(delegate()
                  {
                     ViewException ve = new ViewException();
                     ve.Exception = excetion;
                     ve.Show(MainForm.Instance);
                  }));
               }
            }
            else
               MessageBox.Show("Ошибка построения отчета");
         }
      }

      public void AdjustForm() { }

      public bool ExecFunction(FunctionArgsType args) { return false; }

      private void qty2Report_Click(object sender, EventArgs e)
      {
         new FmQty2Report().Show();
      }

      class Result : GRSoft.Network.DataObject
      {
         public string name = "";
         public byte[] file = null;
      }

      class Param : GRSoft.Network.DataObject
      {
         public string uid = string.Empty;
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
