using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class MainFormEx : MainForm
   {
      SimpleDataSet<NapoleonTask> dsTask;
      SimpleDataSet<NapoleonTaskResponse> dsResponce;

      System.Windows.Forms.ToolStripButton btnNplTask;

      public MainFormEx()
      {
         tsbMakeHtml.Visible = false;
         btnOrderReport.Visible = false;
         btnGpsReport.Visible = false;
         tgvAgentsSummaryCount.Visible = false;
         tgvAgentsSummarySum.Visible = false;

         Column1.HeaderText = "Задачи";

         dsResponce = new SimpleDataSet<NapoleonTaskResponse>(NapoleonTaskResponse.OBJECT_NAME);
         dsTask = new SimpleDataSet<NapoleonTask>(NapoleonTask.OBJECT_NAME);

         btnCensus.Text = "Задачи";

         smiRoute.Text = "Распределение задач...";

         btnNplTask = new System.Windows.Forms.ToolStripButton();
         btnNplTask.Image = btnCensus.Image;
         btnNplTask.Text = btnCensus.Text;
         btnNplTask.DisplayStyle = btnCensus.DisplayStyle;
         btnNplTask.Size = btnCensus.Size;
         btnNplTask.ImageTransparentColor = btnCensus.ImageTransparentColor;
         btnCensus.Visible = false;

         tsbConfig.Items.Insert(tsbConfig.Items.IndexOf(btnCensus), btnNplTask);

         btnNplTask.Click += ((o, e) =>
         {
            if (CheckIsMainDataPresents(true))
               FmTaskList.Open(GetBeginDateForSelection(), GetRangeEndDate().AddDays(-1));
         });
      }

      protected override DivisionSummary CreateDivisionSummary()
      {
         return new DivisionSummaryEx(dsConfig);
      }

      protected override void AddUpdateDataSet(List<IDataSet> list)
      {
         list.Add(dsResponce);
         list.Add(dsTask);
      }

      protected override void AdjustFilterForDS(DateTime dateBegin, DateTime dateEnd)
      {
         base.AdjustFilterForDS(dateBegin, dateEnd);

         string agentFilter = DataUtils.MakeFilterFromAgents(null, dsAgents);
         string filter = agentFilter + String.Format(" and \"end\" >= ToDate('{0:dd/MM/yyyy}') and \"end\" < ToDate('{1:dd/MM/yyyy}')", dateBegin, dateEnd);
         dsTask.Filter = filter;


         dsResponce.Filter = "\"id\" in (select \"id\" from \"NapoleonTask\" where " + filter + ")";
      }

      protected override void CellFormatting(System.Windows.Forms.DataGridViewCellFormattingEventArgs e)
      {
      }
   }

   class SummaryDataEx : SummaryData
   {
      public int answered = 0;

      public SummaryDataEx(NapoleonManager.Agent agent, DataSet<int, CommonConfig> config)
         : base(agent, config)
      {
      }

      public override void CountProgress(DateTime start, DateTime end, DataSet<int, OrgFolder> dsOrgFolder)
      {
         plan = Visits == 0 ? 0 : (double)answered / Visits * 100;
      }
   }

   class DivisionSummaryEx : DivisionSummary
   { 
      public DivisionSummaryEx(DataSet<int, CommonConfig> dsConfig):base(dsConfig)
      {
      }

      protected override void PostAddData()
      {
         IDataSet task = DataModule.Get(NapoleonTask.OBJECT_NAME);
         IDataSet response = DataModule.Get(NapoleonTaskResponse.OBJECT_NAME);
         if (task != null && response != null)
         {
            Dictionary<string, bool> answered = new Dictionary<string, bool>();
            foreach (NapoleonTaskResponse tr in response.Data)
               answered[tr.id] = true;

            foreach (NapoleonTask r in task.Data)
               this.Add(r, answered);
         }
      }

      protected override SummaryData CreateSummaryData(Agent agent, DataSet<int, CommonConfig> config)
      {
         return new SummaryDataEx(agent, config);
      }

      private void Add(NapoleonTask r, Dictionary<string, bool> answered)
      {
         if (ContainsKey(r.userid))
         {
            SummaryDataEx sd = (SummaryDataEx)this[r.userid];
            sd.AddOrg(r.id, r.userid, r.end);
            if (answered.ContainsKey(r.id))
               sd.answered++;
         }
      }
   }
}
