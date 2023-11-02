using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using System.Threading;
using GRSoft.NapoleonManager.Reports.Excel;

namespace GRSoft.NapoleonManager
{
   public partial class ActionReport : Form
   {
      ReportParams rp = new ReportParams();
      public ActionReport()
      {
         InitializeComponent();

         dtStart.Value = DateTime.Now.Date.AddDays(-7);
         dtEnd.Value = DateTime.Now.Date;
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         rp.end = dtEnd.Value;
         rp.start = dtStart.Value;

         base.OnClosing(e);
      }

      ReportParams Params { get { return rp; } }

      public static void Do(Form owner)
      {
         ActionReport form = new ActionReport();
         if (form.ShowDialog() != DialogResult.OK)
            return;

         ReportParams prm = form.Params;
         DateTime start = DateTime.MaxValue, end = DateTime.MinValue;

         DataSet<string, OrgActions> dsOrgActions = (DataSet<string, OrgActions>)DataModule.Get(OrgActions.OBJECT_NAME) ??
            new DataSet<string, OrgActions>(OrgActions.OBJECT_NAME);
         if (dsOrgActions.Count == 0)
         {
            Thread th = DataModule.RefreshDataSet(dsOrgActions, Config.GetConfig().GetConnection(), false, null);
            th.Join();
         }
         foreach (OrgActions oa in dsOrgActions.Data)
         {
            if (oa.end >= prm.start && oa.start <= prm.end)
            {
               if (start > oa.start)
                  start = oa.start;
               if (end < oa.end)
                  end = oa.end;
            }
         }
         if (start == DateTime.MaxValue)
         {
            MessageBox.Show("В указанный интервал нет доступных акций", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Stop);
            return;
         }

         prm.dsActions = new SimpleDataSet<ActiveOrgActions>(ActiveOrgActions.OBJECT_NAME, false);
         prm.dsActions.Filter = String.Format("\"{0}\" >= ToDate('{1:dd/MM/yyyy}') and \"{0}\" < ToDate('{2:dd/MM/yyyy} 23:59:59') and {3}",
            "created", start, end, DataUtils.MakeFilterFromAgents(null, ((Manager)CurrentUser.user).GetAgents()));

         DataSet<string, Org> commonOrgs = (DataSet<string, Org>)DataModule.Get(Org.COMMON_OBJECT_NAME) ??
            new DataSet<string, Org>(Org.COMMON_OBJECT_NAME);

         List<IDataSet> upd = new List<IDataSet>();
         if (commonOrgs.Count == 0)
            upd.Add(commonOrgs);
         upd.Add(prm.dsActions);

         FmWait.StdDataRefresh(owner, upd, () => {
            Thread th = new Thread(DoReport);
            th.Start(prm);
            FmWait.ShowForm(owner, th);
         }); 
      }

      static void DoReport(object prm)
      {
         ReportBuilder rpb = new ReportBuilder(prm as ReportParams);
         try
         {
            rpb.Do();
         }
         catch (Exception e)
         {
            MessageBox.Show("Ошибка при создании отчета: " + e.Message);
         }
         FmWait.CloseForm();
      }

      class ReportBuilder : Excel
      {
         ReportParams param;
         public ReportBuilder(ReportParams param)
         {
            this.param = param;
         }

         class ActionKey
         {
            Org org;
            OrgActions action;
            public ActionKey(Org o, OrgActions a)
            {
               org = o;
               action = a;
            }

            public class Comparer : EqualityComparer<ActionKey>
            {

               public override bool Equals(ActionKey x, ActionKey y)
               {
                  return x.org.id == y.org.id && x.action.id == y.action.id;
               }

               public override int GetHashCode(ActionKey obj)
               {
                  return obj.org.id.GetHashCode() ^ obj.action.id.GetHashCode();
               }
            }

         }

        class EmptyOrg : Org
         {
            public EmptyOrg(string id)
            {
               this.id = id;
               name = "Контрагент с кодом <" + id + ">";
            }
         }

         class OrgActiionData
         {
            public OrgActions action;
            public DateTime created;

            public OrgActiionData(OrgActions a, DateTime created)
            {
               this.action = a;
               this.created = created;
            }
         }

         class OrgData : Dictionary<Org, List<OrgActiionData>>
         {
            public OrgData() : base(new OrgComparer()) { }

            public List<OrgActiionData> Data(Org org)
            {
               List<OrgActiionData> actions;
               if (ContainsKey(org))
                  actions = this[org];
               else
               {
                  actions = new List<OrgActiionData>();
                  Add(org, actions);
               }

               return actions;
            }

            class OrgComparer : EqualityComparer<Org>
            {
               public override bool Equals(Org x, Org y)
               {
                  return x.id == y.id;
               }

               public override int GetHashCode(Org obj)
               {
                  return obj.id.GetHashCode();
               }
            }

         }

         class AgentData : Dictionary<Agent, OrgData>
         {
            public OrgData Data(Agent agent)
            {
               OrgData od;
               if (ContainsKey(agent))
                  od = this[agent];
               else
               {
                  od = new OrgData();
                  Add(agent, od);
               }

               return od;
            }
         }

         class DivisionData : Dictionary<Division, AgentData>
         {
            public AgentData Data(Division div)
            {
               AgentData ad;
               if (ContainsKey(div))
                  ad = this[div];
               else
               {
                  ad = new AgentData();
                  Add(div, ad);
               }
               return ad;
            }
         }

         DivisionData PrepareData(DivisionList divisions, Dictionary<ActionKey, DateTime> actionsStart)
         {
            DivisionData data = new DivisionData();
            foreach (ActiveOrgActions oa in param.dsActions.Data)
            {
               if (oa.agent == null)
                  continue;
               Division div = divisions.Find(oa.agent);
               if (div == null)
                  continue;

               if (oa.org == null)
                  oa.org = new EmptyOrg(oa.id);

               AgentData ad = data.Data(div);
               OrgData od = ad.Data(oa.agent);
               List<OrgActiionData> actions = od.Data(oa.org);

               foreach (ActiveOrgActions.Item item in oa.items)
               {
                  if (item.action == null)
                     continue;

                  actions.Add(new OrgActiionData(item.action, oa.created));
                  ActionKey ak = new ActionKey(oa.org, item.action);
                  if (actionsStart.ContainsKey(ak))
                  {
                     DateTime dt = actionsStart[ak];
                     if (dt > oa.created)
                        actionsStart[ak] = oa.created;
                  }
                  else
                     actionsStart.Add(ak, oa.created);
               }
            }

            return data;
         }

         public void Do()
         {
            DivisionList divisions = DivisionList.GetDataSet();
            Dictionary<ActionKey, DateTime> actionsStart = new Dictionary<ActionKey, DateTime>(new ActionKey.Comparer());

            DivisionData data = PrepareData(divisions, actionsStart);

            SetValue(1, 1, "Группы агентов");
            SetValue(1, 2, "Агенты");
            SetValue(1, 3, "Торговые точки");
            SetValue(1, 4, "Акции");
            SetValue(1, 5, "Количество акций");

            int curRow = 2;
            foreach (KeyValuePair<Division, AgentData> dd in data)
            {
               foreach (KeyValuePair<Agent, OrgData> ad in dd.Value)
               {
                  foreach (KeyValuePair<Org, List<OrgActiionData>> od in ad.Value)
                  {
                     foreach (OrgActiionData act in od.Value)
                     {
                        ActionKey ak = new ActionKey(od.Key, act.action);
                        DateTime check = actionsStart[ak];
                        if (act.created > check)
                           continue;

                        SetValue(curRow, 1, dd.Key.name);
                        SetValue(curRow, 2, ad.Key.Name);
                        SetValue(curRow, 3, od.Key.Name);
                        SetValue(curRow, 4, act.action.name);
                        SetValue(curRow, 5, 1);

                        curRow++;
                     }
                  }
               }
            }

            const int xlSum = -4157;
            //Subtotal GroupBy:=1, Function:=xlSum, TotalList:=Array(5), Replace:=False, PageBreaks:=False, SummaryBelowData:=True
            object cell = GetCell(1,1);
            InvokeMethod(cell, "Subtotal", new object[] { 1, xlSum, new object[] { 5 }, false, false, true});
            InvokeMethod(cell, "Subtotal", new object[] { 2, xlSum, new object[] { 5 }, false, false, true });

            object clmns = GetProperty(ActiveSheet, COLUMNS_STR, new object[] { "A:E" });
            InvokeMethod(GetProperty(clmns, "EntireColumn"), "AutoFit", (object[])null);

            Visible = true;
         }
      }

      class ReportParams
      {
         public DateTime start;
         public DateTime end;

         public SimpleDataSet<ActiveOrgActions> dsActions;
      }
   }
}
