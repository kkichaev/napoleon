using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
  [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public
   class FmDetailEx : FmDetail
   {
      protected DataSet<int, TaskBegin> dsTaskBegin;
      protected DataSet<int, TaskEnd> dsTaskEnd;
      protected DataSet<int, ATask> dsATask;
      protected DataSet<int, MTask> dsMTask;
      protected DataSet<int, MerchEnd> dsMerchEnd;
      protected DataSet<int, Visit> dsActGS;
      protected DataSet<int, Facing> dsFacing;

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         dsTaskBegin = (DataSet<int, TaskBegin>)DataModule.Get(TaskBegin.OBJECT_NAME) ?? new DataSet<int, TaskBegin>(TaskBegin.OBJECT_NAME);
         dsTaskEnd = (DataSet<int, TaskEnd>)DataModule.Get(TaskEnd.OBJECT_NAME) ?? new DataSet<int, TaskEnd>(TaskEnd.OBJECT_NAME);
         dsATask = (DataSet<int, ATask>)DataModule.Get(ATask.OBJECT_NAME) ?? new DataSet<int, ATask>(ATask.OBJECT_NAME);
         dsMTask = (DataSet<int, MTask>)DataModule.Get(MTask.OBJECT_NAME) ?? new DataSet<int, MTask>(MTask.OBJECT_NAME);
         dsMerchEnd = (DataSet<int, MerchEnd>)DataModule.Get(MerchEnd.OBJECT_NAME) ?? new DataSet<int, MerchEnd>(MerchEnd.OBJECT_NAME);
         dsActGS = (DataSet<int, Visit>)DataModule.Get("ActGSDoc") ?? new DataSet<int, Visit>("ActGSDoc");
         dsFacing = (DataSet<int, Facing>)DataModule.Get(Facing.OBJECT_NAME) ?? new DataSet<int, Facing>(Facing.OBJECT_NAME);

         ToolStripMenuItem wtReport = new ToolStripMenuItem("Отчет о работе");
         wtReport.Click += new EventHandler(wtReport_Click);
         tsReportMenu.DropDownItems.Add(wtReport);

         ToolStripMenuItem mrchReport = new ToolStripMenuItem("Отчет мерчендайзинг");
         mrchReport.Click += mrchReport_Click;
         tsReportMenu.DropDownItems.Add(mrchReport);

         ToolStripMenuItem taskReport = new ToolStripMenuItem("Отчет по задачам");
         taskReport.Click += taskReport_Click;
         tsReportMenu.DropDownItems.Add(taskReport);

         documents.Add(new DocumentInfo(dsFacing, ObjType.TObjType.Facing));
      }

      private void taskReport_Click(object sender, EventArgs e)
      {
         TaskReport.Do(GetDateForStartPeriod(), dtpEnd.Value.Date, GetSelectedIdAgent(), this);
      }

      void mrchReport_Click(object sender, EventArgs e)
      {
         MerchReport.Do(GetDateForStartPeriod(), GetDateForEndPeriod(), this);
      }

      private void wtReport_Click(object sender, EventArgs e)
      {
         WorkTimeReport.Do(GetDateForStartPeriod(), GetDateForEndPeriod(), this);
      }


      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         string flt = string.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsTaskBegin.Filter = flt;
         dsTaskEnd.Filter = flt;
         dsMerchEnd.Filter = flt;
         dsFacing.Filter = flt;

         dsATask.Filter = "not taskid in (select taskid from taskanswer)";
         dsMTask.Filter = "not taskid in (select taskid from taskanswer)";

         updSets.Add(dsTaskBegin);
         updSets.Add(dsTaskEnd);
         updSets.Add(dsATask);
         updSets.Add(dsMTask);
         updSets.Add(dsMerchEnd);
         updSets.Add(dsFacing);

         base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);
      }

      internal override OrdersDetail CreateOrderDetail()
      {
         return new ScriptDetailEx(documents);
      }

      protected override FmDetail.DocView GetDocView(string docType)
      {
         if (docType.Equals("TaskBegin")) 
            return new DocView("TaskBegin", "Задачи", typeof(TaskControl));

         if (docType.Equals("MerchEnd"))
            return new DocView("MerchEnd", "Мерчендайзинг", typeof(MerchControl));

         if (docType.Equals("Facing"))
            return new DocView("Facing", "Файсинг", typeof(FacingControl));

         return base.GetDocView(docType);
      }

      protected override void PostSetScriptInfo(ScriptDoc sd)
      {
         foreach (TabPage p in scriptDetail.TabPages)
         {
            if (p.Text.Equals("Задачи начало"))
               p.Text = "Задачи";
            else if (p.Text.Equals("Мерч конец"))
               p.Text = "Мерчендайзинг";
         }
      }

      protected override void AfterRefreshData()
      {
         base.AfterRefreshData();

         IDataSet ds = DataModule.Get(Visit.OBJECT_NAME);
         if(ds != null)
            foreach (Visit v in ds.Data)
               if (v.actgs > 0)
                  dsActGS.Add(dsActGS.Count, v);
      }

      protected override bool IsVisitItem(ScriptDocItem i)
      {
         return base.IsVisitItem(i) || i.type == "ActGSDoc";
      }

      internal override Control RefreshDetail(OrderDetailRepresentation odr)
      {
         Control result = base.RefreshDetail(odr);

         if (odr.StoreObject is Facing)
         {
            Facing f = odr.StoreObject as Facing;
            tbVisitText.Text = FacingToString(f);
            result = tbVisitText;
         }

         return result;
      }

      public static string FacingToString(Facing f)
      {
         return string.Format("Общий фейсинг ЛВИ: {0}\nКомментарий: {1}", f.qty, f.remark);
      }
   }

   class ScriptDetailEx : ScriptDetail
   { 
      public ScriptDetailEx(List<DocumentInfo> documents) : base(documents) {}

      protected override bool isEmptyScript(List<ScriptDocItem> items)
      {
         bool result = true;

         if (items != null)
            foreach (ScriptDocItem s in items)
               if (s.Document != null || s.type == "TaskBegin" || s.type == "TaskEnd" || s.type == "ActGSDoc")
               {
                  result = false;
                  break;
               }

         return result;
      }
   }
}
