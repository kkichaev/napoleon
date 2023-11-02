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
   public partial class FmVisitPlanFact : Form
   {
      DataSet<string, Org> dsOrg;
      SimpleDataSet<VisitPlanFact> dsPlanFact;

      VisitPlanFact currentPlan;
      List<VisitPlanFact> changed;

      DateTime curDate;

      public FmVisitPlanFact()
      {
         InitializeComponent();

         dsOrg = (DataSet<string, Org>)DataModule.Get(Org.COMMON_OBJECT_NAME) ?? new DataSet<string, Org>(Org.COMMON_OBJECT_NAME);
         dsPlanFact = new SimpleDataSet<VisitPlanFact>(VisitPlanFact.OBJECT_NAME, false);
         changed = new List<VisitPlanFact>();

         dtpDate.Value = new DateTime(DateTime.Now.Year, DateTime.Now.Month, 1);
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         Manager mgr = CurrentUser.user as Manager;

         List<Agent> src = new List<Agent>();
         foreach(Agent a in mgr.GetAgents().Data)
         {
            src.Add(a);
         }
         src.Sort();
         src.ForEach(x => cbAgents.Items.Add(x));
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         base.OnClosing(e);
         if(!CheckChanges())
         {
            e.Cancel = true;
         }
      }

      DateTime StartMonth(DateTime date)
      {
         return new DateTime(date.Year, date.Month, 1);
      }

      void RefreshData()
      {
         if (!CheckChanges())
            return;

         List<IDataSet> upd = new List<IDataSet>();

         Manager mgr = CurrentUser.user as Manager;
         foreach (Agent a in mgr.GetAgents().Data)
         {
            DataSet<int, OrgFolder> dsOrgFolder = DataModule.GetUserDataSet(a.id, OrgFolder.OBJECT_NAME, typeof(DataSet<int, OrgFolder>), true) as DataSet<int, OrgFolder>;
            if (dsOrgFolder.Count == 0)
               upd.Add(dsOrgFolder);
         }

         if (dsOrg.Count == 0)
            upd.Add(dsOrg);

         if(curDate == null || StartMonth(dtpDate.Value) != curDate)
         {
            curDate = StartMonth(dtpDate.Value);
            dsPlanFact.Filter = String.Format("\"date\" = ToDate('{0:dd/MM/yyyy}')", curDate);
         }
         upd.Add(dsPlanFact);


         FmWait.StdDataRefresh(this, upd, LoadData);
      }

      void LoadData()
      {
         ClearDirty();
         OnAgentChanged();
      }

      void ClearDirty()
      {
         tsbSave.Enabled = false;
         changed.Clear();
         currentPlan = null;
      }

      void OnAgentChanged()
      {
         bool sv = tsbSave.Enabled;
         currentPlan = null;

         List<VisitPlanFact.Item> src = new List<VisitPlanFact.Item>();
         Agent a = cbAgents.SelectedItem as Agent;
         if (curDate != null && StartMonth(dtpDate.Value) == curDate && a != null)
         {
            foreach (VisitPlanFact i in dsPlanFact.Data)
            {
               if (i.userid == a.id)
               {
                  src = i.items;
                  currentPlan = i;
                  break;
               }
            }

            if(currentPlan == null)
            {
               VisitPlanFact doc = CreateNewPlan(a);
               dsPlanFact.Add(doc);
               currentPlan = doc;
               src = doc.items;
               SetDirty();
            }
         }
         dgvItems.DataSource = src;
         tsbSave.Enabled = sv;
      }

      int CountMonthDays(DateTime monthBegin, WeekDay wd)
      {
         DateTime cur = monthBegin;
         DateTime end = monthBegin.AddMonths(1);
         while (cur.DayOfWeek != wd.DayOfWeek)
            cur = cur.AddDays(1);

         int count = 1;

         while(true)
         {
            cur = cur.AddDays(7);
            if (cur >= end)
               break;

            count++;
         }
         return count;
      }

      private VisitPlanFact CreateNewPlan(Agent a)
      {
         VisitPlanFact doc = new VisitPlanFact();
         doc.userid = a.id;
         doc.agent = a;
         doc.date = curDate;

         Dictionary<string, VisitPlanFact.Item> items = new Dictionary<string, VisitPlanFact.Item>();

         DataSet<int, OrgFolder> dsOrgFolder = DataModule.GetUserDataSet(a.id, OrgFolder.OBJECT_NAME, typeof(DataSet<int, OrgFolder>), true) as DataSet<int, OrgFolder>;
         foreach (OrgFolder of in dsOrgFolder.Data)
         {
            int wd = CountMonthDays(curDate, of.WeekDay);
            foreach(OrgFolderItem ofi in of.items)
            {
               Org o;
               if(dsOrg.TryGetValue(ofi.name, out o))
               {

                  VisitPlanFact.Item item;
                  if (items.TryGetValue(ofi.name, out item) == false)
                  {
                     item = new VisitPlanFact.Item();
                     doc.items.Add(item);
                     items[ofi.name] = item;

                     item.org = o;
                     item.id = o.id;
                  }
                  item.plan += wd;

               }
            }
         }

         return doc;
      }

      bool CheckChanges()
      {
         if (!tsbSave.Enabled)
            return true;

         DialogResult dr = MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
         if (dr == DialogResult.No)
            return true;
         if (dr == DialogResult.Cancel)
            return false;

         return SaveChanges(false);
      }

      private bool SaveChanges(bool showDialog)
      {
         dgvItems.CommitEdit(DataGridViewDataErrorContexts.Commit);

         List<IDataSet> wr = new List<IDataSet>();
         SimpleDataSet<VisitPlanFact> ds = new SimpleDataSet<VisitPlanFact>(VisitPlanFact.OBJECT_NAME, false);
         changed.ForEach(x => ds.Add(x));
         wr.Add(ds);

         bool ret = DataModule.UpdateDataSet(wr, null, null, Config.GetConfig().GetConnection());
         if (ret)
         {
            changed.Clear();
         }


         tsbSave.Enabled = !ret;
         if (showDialog)
         {
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");
         }
         return ret;
      }

      private void tsbRefresh_Click(object sender, EventArgs e)
      {
         RefreshData();
      }

      private void tsbSave_Click(object sender, EventArgs e)
      {
         SaveChanges(true);
      }

      public void SetDirty()
      {
         tsbSave.Enabled = true;
      }

      private void cbAgents_SelectedIndexChanged(object sender, EventArgs e)
      {
         OnAgentChanged();
      }

      private void dgvItems_CurrentCellChanged(object sender, EventArgs e)
      {
         SetDirty();
         dgvItems.CommitEdit(DataGridViewDataErrorContexts.Commit);
         dgvItems.Refresh();

         if (currentPlan != null && !changed.Contains(currentPlan))
            changed.Add(currentPlan);
      }

      class PFReportParam : GRSoft.Network.DataObject
      {
         public string userid = "";
         public DateTime date = DateTime.Now;
      }

      private void btnExcel_Click(object sender, EventArgs e)
      {
         Agent a = cbAgents.SelectedItem as Agent;
         if (curDate == null || a == null)
         {
            MessageBox.Show("Не выбран агент или не приняты данные");
            return;
         }
         if (!CheckChanges())
            return;

         
         PFReportParam param = new PFReportParam();
         param.userid = a.id;
         param.date = curDate;
         ReportResult.DoReport("planfact_report", param, this);
      }
   }
}
