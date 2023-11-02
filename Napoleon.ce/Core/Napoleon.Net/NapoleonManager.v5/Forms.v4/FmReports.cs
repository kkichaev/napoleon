using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmReports : Form
   {
      public readonly static string VISIT_REPORT = "rmr_visit_report";
      public readonly static string QUEST_REPORT = "quest_rep";
      public readonly static string QUEST_PIVOT_REPORT = "quest_pivot_rep";
      public readonly static string ROUTE_REPORT = "rmr_routelist_report|Маршрутный лист";
      public readonly static string SUMMARY_REPORT = "rmr_summary_report|Отчет о работе подразделений";
      public readonly static string DISTANCE_REPORT = "rmr_distance_report|Отчет по пробегу";
      public readonly static string ORDER_REPORT = "rmr_order_report|Отчет по заявкам";

      public class ReportParam : GRSoft.Network.DataObject
      {
         public class Item : GRSoft.Network.DataObject
         {
            public String id = "";
         }

         public DateTime start = DateTime.Now;
         public DateTime finish = DateTime.Now;
         public List<Item> userids = new List<Item>();
         public List<Item> quests = new List<Item>();
         public int divid = -1;
         public int param = 0;
         public string field = string.Empty;
         public string hrefBase = Config.GetConfig().HrefBase;
         public int gsm = 0;
         public int timeFromDocs = 0;
      }

      public virtual ReportParam CreateParamInstance() { return new ReportParam(); }

      public string selectedReport = string.Empty;

      public FmReports()
      {
         InitializeComponent();
      }

      public void LayoutControls()
      {
         int gapY = 6;
         int gapX = 6;
         int btnWidth = 240;
         int btnHeight = 55;

         int maxY = 0;
         Control[] panels = new Control[]
         {
            panel2, panel4, panel5, panel7, panel6
         };

         foreach(Control panel in panels)
         {
            int ctr = 0;
            foreach(Control c in panel.Controls)
            {
               int x = gapX + ctr % 3 * (btnWidth + gapX);
               int y = gapY + ctr / 3 * (btnHeight + gapY);
               c.Location = new Point(x, y);
               c.Size = new Size(btnWidth, btnHeight);
               if (panel.Width < y) panel.Width = y;
               ctr++;
            }

            if (maxY < panel.Bottom)
               maxY = panel.Bottom;
         }

         if (Height < maxY)
            Height = maxY;
      }

      private void FmReports_Load(object sender, EventArgs e)
      {
         dtpDateStart.Value = DateTime.Now.Date;
         dtpDateFinish.Value = DateTime.Now.Date;

         dtpTimeStart.Value = DateTime.Now.Date.AddHours(9);
         dtpTimeFinish.Value = DateTime.Now.Date.AddHours(18);

         splitContainer1.Panel2.Enabled = false;
         
         List<IDataSet> list = new List<IDataSet>();
         list.Add(DivisionList.GetDataSet());
         list.Add(Agents.GetDataSet());

         DataSet<string, Question> dsQuest = new DataSet<string, Question>(Question.OBJECT_NAME);
         dsQuest.Filter = "\"idquest\" is null or \"idquest\" is not null";
         list.Add(dsQuest);

         BeforeRefresh(list);

         FmWait.StdDataRefresh(this, list, DoLoadData);
      }

      protected virtual void BeforeRefresh(List<IDataSet> upd) { }
      protected virtual void DataLoaded(Manager m) { }

      private void DoLoadData()
      {
         Manager mc = CurrentUser.user as Manager;

         if (mc != null)
         {
            SetDivision(mc);
            SelectDivision(mc);
            SetAgent(mc);
            SetQuest();
            DataLoaded(mc);
         }
      }

      private void SetQuest()
      {
         DataSet<string, Question> dsQuest = (DataSet<string, Question>)DataModule.Get(Question.OBJECT_NAME);
         List<Question> list = new List<Question>(dsQuest.Values);
         list.Sort((x, y) => { return x.Name.CompareTo(y.Name); });
         list.ForEach((x) => { lbQuest.Items.Add(x); });
      }

      private void SetAgent(Manager mc)
      {
         List<Division.DivisionAgent> list = mc.Division.GetAllAgents();
         list.Sort((lhs, rhs) => { return lhs.AgentName.CompareTo(rhs.AgentName); });
         cbAgent.Items.AddRange(list.ToArray());
      }

      private void SetDivision(Manager mc)
      {
         List<Division> list = mc.AllDivisions;
         cbDivision.Items.AddRange(list.ToArray());
      }

      private void SelectDivision(Manager mc)
      {
         int sel = -1;
         for (int i = 0; i < cbDivision.Items.Count; i++)
         {
            Division d = (Division)cbDivision.Items[i];

            if (d.id.Equals(mc.Division.id))
            {
               cbDivision.SelectedIndex = i;
               break;
            }
         }

         if (sel != -1)
            cbDivision.SelectedIndex = sel;
      }

      public virtual void ResetPanel()
      {
         cbByDocs.Enabled = false;

         splitContainer1.Panel2.Enabled = true;

         foreach (Control c in splitContainer1.Panel2.Controls)
            c.Enabled = false;

         foreach (Control c in panel2.Controls)
            if (c is RichButton)
               ((RichButton)c).Checked = false;

         foreach (Control c in panel4.Controls)
            if (c is RichButton)
               ((RichButton)c).Checked = false;

         foreach (Control c in panel6.Controls)
            if (c is RichButton)
               ((RichButton)c).Checked = false;

         foreach (Control c in panel7.Controls)
            if (c is RichButton)
               ((RichButton)c).Checked = false;

         pnlQuest.Enabled = false;
      }

      private void rbDiv_CheckedChanged(object sender, EventArgs e)
      {
         cbDivision.Enabled = ((RadioButton)sender).Checked;
      }

      private void rbAgent_CheckedChanged(object sender, EventArgs e)
      {
         cbAgent.Enabled = ((RadioButton)sender).Checked;
      }

      protected virtual void btnVisit_Click(object sender, EventArgs e)
      {
         ResetPanel();
         gbDivision.Enabled = true;
         gbDate.Enabled = true;
         rbDiv.Enabled = false;
         rbAgent.Checked = true;
         btnDoReport.Enabled = true;
         selectedReport = VISIT_REPORT;
      }

      private void btnQuest_Click(object sender, EventArgs e)
      {
         ResetPanel();
         rbDiv.Enabled = true;
         rbDiv.Checked = true;
         gbDivision.Enabled = true;
         gbLayout.Enabled = true;
         gbDate.Enabled = true;
         btnDoReport.Enabled = true;
         pnlQuest.Enabled = true;
         selectedReport = QUEST_REPORT;
      }

      private void btnTask_Click(object sender, EventArgs e)
      {
         ResetPanel();
         rbDiv.Enabled = true;
         rbDiv.Checked = true;
         gbDivision.Enabled = true;
         gbDate.Enabled = true;
         btnDoReport.Enabled = true;
         selectedReport = "rmr_task_report";
      }

      private void btnRouteList_Click(object sender, EventArgs e)
      {
         ResetPanel();
         gbDivision.Enabled = true;
         rbAgent.Checked = true;
         rbDiv.Enabled = false;
         btnDoReport.Enabled = true;
         selectedReport = ROUTE_REPORT;
      }

      private void btnWorkReport_Click(object sender, EventArgs e)
      {
         ResetPanel();
         gbDivision.Enabled = true;
         rbDiv.Enabled = true;
         rbDiv.Checked = true;
         gbDate.Enabled = true;
         btnDoReport.Enabled = true;
         rbAgent.Enabled = false;
         cbAgent.Enabled = false;
         selectedReport = SUMMARY_REPORT;
      }

      private void btnDistance_Click(object sender, EventArgs e)
      {
         ResetPanel();
         gbDivision.Enabled = true;
         rbDiv.Enabled = true;
         rbDiv.Checked = true;
         gbDate.Enabled = true;
         gbTime.Enabled = true;
         gbGSM.Enabled = true;
         btnDoReport.Enabled = true;
         cbByDocs.Enabled = true;
         selectedReport = DISTANCE_REPORT;
      }

      protected virtual void btnOrder_Click(object sender, EventArgs e)
      {
         ResetPanel();
         gbDivision.Enabled = true;
         rbDiv.Enabled = true;
         rbDiv.Checked = true;
         gbDate.Enabled = true;
         gbField.Enabled = true;
         btnDoReport.Enabled = true;
         selectedReport = ORDER_REPORT;
      }

      private void btnMapClient_Click(object sender, EventArgs e)
      {
         ResetPanel();
         gbDivision.Enabled = true;
         rbDiv.Enabled = true;
         rbDiv.Checked = true;
         gbDate.Enabled = true;
         btnDoReport.Enabled = true;
         selectedReport = "rmr_mapclient_report";
      }

      private void btnDoReport_Click(object sender, EventArgs e)
      {
         string report = UpdateReportName();
         string file = report;
         string[] rn = report.Split('|');

         if (rn.Length > 1)
         {
            report = rn[0];
            file = rn[1];
         }

         bool ok = CheckInputData(report);

         if (ok)
            ReportResult.DoReport(report, file, CreateParam(report), this);
      }

      private bool CheckInputData(string report)
      {
         bool error = false;

         if (rbAgent.Checked && cbAgent.CheckedIndices.Count == 0)
         {
            MessageBox.Show("Выберите агента!");
            cbAgent.Focus();
            error = true;
         }

         if (report.Equals(QUEST_REPORT) || report.Equals(QUEST_PIVOT_REPORT))
         {
            if (lbQuest.CheckedItems.Count == 0)
            {
               MessageBox.Show("Выберите анкету!");
               lbQuest.Focus();
               error = true;
            }
         }

         return !error;
      }

      protected virtual string UpdateReportName()
      {
         string report = selectedReport;

         if (report.Equals(QUEST_REPORT))
         {
            if (!rbHor.Checked)
               report = QUEST_PIVOT_REPORT;
         }
         return report;
      }

      protected Network.DataObject SetParamObject(ReportParam res)
      {
         const string CREATED_FIELD = "created";
         const string DATE_FIELD = "date";

         res.start = dtpDateStart.Value.Date;
         res.finish = dtpDateFinish.Value.Date;
         res.userids = DoItems(CollectUserids());
         res.quests = DoItems(CollectQuests());
         res.divid = (cbDivision.SelectedItem as Division) == null ? -1 : (cbDivision.SelectedItem as Division).id;
         res.field = rbCreated.Checked ? CREATED_FIELD : DATE_FIELD;
         res.gsm = cbUseGSM.Checked ? 1 : 0;

         if (cbByDocs.Checked)
         {
            res.timeFromDocs = 1;
         }
         else if (cbTime.Checked)
         {
            res.start = res.start.AddHours(dtpTimeStart.Value.Hour).AddMinutes(dtpTimeStart.Value.Minute);
            res.finish = res.finish.AddHours(dtpTimeFinish.Value.Hour).AddMinutes(dtpTimeFinish.Value.Minute);
         }

         return res;
      }

      protected virtual Network.DataObject CreateParam(string selectedReport)
      {
         ReportParam res = CreateParamInstance();

         return SetParamObject(res);
      }

      private List<string> CollectUserids()
      {
         List<string> res = new List<string>();

         if (rbDiv.Checked)
         {
            Division d = cbDivision.SelectedItem as Division;

            if (d != null)
            {
               foreach (Division.DivisionAgent a in d.GetAllAgents())
                  if (!res.Contains(a.id))
                     res.Add(a.id);
            }
         }else foreach (System.Object i in cbAgent.CheckedItems)
         {
            Division.DivisionAgent a = i as Division.DivisionAgent;

            if (a != null)
            {
               res.Add(a.id);
            }
         }

         return res;
      }

      private void btnCheck_Click(object sender, EventArgs e)
      {
         QuestListCheck(true);
      }

      private void btnUncheck_Click(object sender, EventArgs e)
      {
         QuestListCheck(false);
      }

      private void QuestListCheck(bool val)
      {
         for (int i = 0; i < lbQuest.Items.Count; i++)
            lbQuest.SetItemChecked(i, val);
      }

      private List<ReportParam.Item> DoItems(List<string> list)
      {
         List<ReportParam.Item> res = new List<ReportParam.Item>();

         foreach (string id in list)
         {
            ReportParam.Item i = new ReportParam.Item();
            i.id = id;
            res.Add(i);
         }

         return res;
      }

      private List<string> CollectQuests()
      {
         List<string> res = new List<string>();

         CheckedListBox.CheckedIndexCollection list = lbQuest.CheckedIndices;

         for (int i = 0; i < list.Count; i++)
            res.Add(String.Format("'{0}'", ((Question)lbQuest.Items[list[i]]).idquest));

         return res;
      }

      private void cbByDocs_Click(object sender, EventArgs e)
      {
         cbTime.Checked = !cbByDocs.Checked;
      }

      private void cbTime_Click(object sender, EventArgs e)
      {
         cbByDocs.Checked = !cbTime.Checked;
      }
   }
}
