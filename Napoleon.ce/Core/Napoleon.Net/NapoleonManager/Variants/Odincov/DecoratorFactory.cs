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

         //if (formType == typeof(Divisions))
         //   return new DivisionsFormDecorator((Divisions)form);

         return new EmptyDecorator();
      }
   }

   class DivisionsFormDecorator : IDecorator
   {
      Divisions form;

      public DivisionsFormDecorator(Divisions form)
      {
         this.form = form;
      }

      public void AdjustForm()
      {
         ToolStripButton tsBonus = new ToolStripButton();
         tsBonus.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         tsBonus.Name = "tsBonus";
         tsBonus.Size = new System.Drawing.Size(101, 22);
         tsBonus.Text = "Акции";
         tsBonus.Click += new System.EventHandler(OpenBonuses);

         this.form.tb.Items.Add(tsBonus);
      }

      public bool ExecFunction(FunctionArgsType args) { return false; }

      void OpenBonuses(object sender, EventArgs e)
      {
         FmBonuses.Open();
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
