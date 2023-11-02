/*
 * Copyright (C), 2011, Гильдия разработчиков
 * 
 * Просмотр и редактирование планов
 * 
 * kki   29/03/2011   creating
 */

using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.UILib;
using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
using System.Collections;
using System.Threading;

namespace GRSoft.NapoleonManager
{
   public partial class FmAllPlans : Form
   {
      private static FmAllPlans instance;
      private DataSet<int, Plan> dsPlan;
      //private DataSet<string, Price> dsPrice;
      private Agents dsAgents = Agents.GetDataSet();
      private DivisionList dsDivision = DivisionList.GetDataSet();
      Size imageSize;

      public static void ShowForm()
      {
         if (instance == null)
         {
            instance = new FmAllPlans();
            instance.Show();
         }
         else
            instance.Focus();

         instance.RefreshData(instance.dtpDate.Value.Date);
      }

      protected FmAllPlans()
      {
         InitializeComponent();
         Init();
      }

      private void Init()
      {
         DateTime now = DateTime.Now;

         dtpDate.Value = new DateTime(now.Year, now.Month, now.Day);
         InitDataSets();

         Size sz = tgvPlans.DefaultCellStyle.Padding.Size;
         imageSize = new Size(tgvPlans.Columns[4].Width - sz.Width - 1, tgvPlans.RowTemplate.Height - sz.Height - 1);

         Column4.DefaultCellStyle.NullValue = emptyBitmap;
      }

      //Инициировать наборы данных
      private void InitDataSets()
      {
         dsPlan = (DataSet<int, Plan>)DataModule.Get(Plan.OBJECT_NAME) ?? new DataSet<int, Plan>(Plan.OBJECT_NAME);
         //dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ?? new DataSet<string, Price>(Price.OBJECT_NAME);
      }

      public void PlansLoad()
      {
         tgvPlans.SuspendLayout();

         if (CurrentUser.user == null)
         {
            Config c = Config.GetConfig();
            CurrentUser.CreateCurrentUser(c.login, c.password);
         }

         Manager manager = (CurrentUser.user as Manager);

         tgvPlans.Nodes.Clear();
         tgvPlans.Rows.Clear();

         TvgPlansLoad(manager.Division, null, null);
         CalculateSum();
         tgvPlans.ResumeLayout();
      }

      private void CalculateSum()
      {
         //foreach (TreeGridNode node in tgvPlans.Nodes)
         //   if(node.Nodes.Count > 0)
         //      CalculateSum(node);
      }

      //private List<double> CalculateSum(TreeGridNode parent)
      //{
      //   List<double> res = parent.Tag as List<double>;
      //   int count = 1;
      //   if( res == null )
      //   {
      //      res = new List<double>();
      //      parent.Tag = res;
      //   }
      //   if( res.Count == 0 )
      //      count = 0;

      //   foreach (TreeGridNode node in parent.Nodes)
      //   {
      //      if (node.Nodes.Count > 0)
      //      {
      //         List<double> childProgress = CalculateSum(node);
      //         if( childProgress != null && childProgress.Count > 0 )
      //         {
      //            while( res.Count < childProgress.Count )
      //               res.Add(0);

      //            // если число планов не совпадает - то будет косяк в общем прогрессе (будет считать не наззначенный план как 0)
      //            for( int i=0; i<res.Count && i < childProgress.Count; i++ )
      //               res[i] += childProgress[i];
      //            count++;
      //         }
      //      }
      //   }

      //   if (count > 1)
      //   {
      //      for (int i = 0; i < res.Count; i++)
      //         res[i] /= count;
      //   }

      //   for (int i = 3; i < parent.Cells.Count && i < res.Count + 3; i++)
      //   {
      //      parent.Cells[i].Value = ProgressImage.CreateProgressImage(res[i - 3], imageSize);
      //   }
      //   return res;
      //}

      class GroupPlanData : IComparable<GroupPlanData>
      {
         public Plan plan;
         public GroupPlanData(Plan p) { plan = p; }

         public void Add(Plan p)
         {
            plan.plan += p.plan;
            plan.fact += p.fact;
         }

         public int CompareTo(GroupPlanData other)
         {
            int cmp = plan.name.CompareTo(other.plan.name);
            if (cmp != 0) return cmp;
            return plan.date.CompareTo(other.plan.date);
         }
      }

      class GroupPlans : List<GroupPlanData>
      {
         public GroupPlans() { }

         private Plan findItem;
         bool Match(GroupPlanData src)
         {
            if (findItem == null) return false;
            if (findItem.name.Trim().CompareTo(src.plan.name.Trim()) != 0)
               return false;

            return (findItem.date.ToString("ddMMyyyy").CompareTo(src.plan.date.ToString("ddMMyyyy")) == 0);
         }

         public void Add(Plan p)
         {
            findItem = p;
            GroupPlanData g = Find(Match);
            if (g == null)
               base.Add(new GroupPlanData(p));
            else
               g.Add(p);
         }

         public static int Cmp(GroupPlanData x, GroupPlanData y)
         {
            int cmp;
            cmp = x.plan.date.ToString("ddMMyyyy").CompareTo(y.plan.date.ToString("ddMMyyyy"));
            if (cmp != 0)
               return cmp;

            return x.plan.name.Trim().CompareTo(y.plan.name.Trim());
         }

         public void AddNodes(TreeGridNode parent, Size imageSize)
         {
            if (Count > 0)
            {
               Sort(Cmp);

               int i = Count - 1;
               for (; i >= 0; i-- )
               {
                  GroupPlanData d = this[i];
                  TreeGridNode tn = new TreeGridNode();
                  parent.Nodes.Insert(0, tn);
                  //parent.InsertChildNode(0, tn);

                  tn.Cells[0].Value = d.plan.name;
                  tn.Cells[1].Value = d.plan.date.ToString("dd-MM-yyyy");
                  tn.Cells[2].Value = d.plan.plan.ToString();
                  tn.Cells[3].Value = d.plan.fact.ToString();

                  tn.Cells[4].Value = ProgressImage.CreateProgressImage((d.plan.fact * 100) / d.plan.plan, imageSize);
               }
            }
         }

         internal void AddPlans(GroupPlans groupData)
         {
            foreach (GroupPlanData d in groupData)
               Add(d.plan);
         }
      }

      private void TvgPlansLoad(Division cheifDivision, TreeGridNode node, GroupPlans plans)
      {
         TreeGridNodeCollection nodes = node == null ? tgvPlans.Nodes : node.Nodes;
         TreeGridNode divNode = nodes.Add(getPlanObjects(cheifDivision));
         divNode.DefaultCellStyle.Font = new Font(tgvPlans.DefaultCellStyle.Font, FontStyle.Bold);

         GroupPlans groupData = new GroupPlans();

         foreach (Division.DivisionAgent agent in cheifDivision.agents)
         {
            if (agent.agent == null)
               continue;
            AddAgentPlans(agent, divNode, groupData);
         }

         tgvPlans.ExpandNode(divNode);

         foreach (Division childDiv in cheifDivision.childs)
            TvgPlansLoad(childDiv, divNode, groupData);

         if (groupData.Count > 0)
         {
            if (plans != null)
               plans.AddPlans(groupData);
            groupData.AddNodes(divNode, imageSize);
         }
      }

      private object[] getPlanObjects(Division first)
      {
         return new object[] { first.DivisionName, string.Empty, string.Empty, string.Empty };
      }

      private object[] getPlanObjects(Division.DivisionAgent first)
      {
         return new object[] { first.AgentName, string.Empty, string.Empty, string.Empty };
      }

      private void AddAgentPlans(Division.DivisionAgent agent, TreeGridNode parent, GroupPlans groupData)
      {
         TreeGridNode aNode = parent.Nodes.Add(getPlanObjects(agent));
         List<Plan> plans = GetUserIdPlans(agent.id);

         GroupPlans gp = new GroupPlans();
         foreach (Plan p in plans)
         {
            gp.Add(p);
            //object[] data = new object[] { p.name, p.date.ToString("dd-MM-yyyy"), p.plan.ToString(), p.fact.ToString(), 
            //   ProgressImage.CreateProgressImage((p.fact * 100)/ p.plan, imageSize) };

            //aNode.Nodes.Add(data);
            groupData.Add(p);
         }

         if( gp.Count > 0 )
            gp.AddNodes(aNode, imageSize);

         if (aNode.Nodes.Count > 0)
         {
            parent.Expand();
            tgvPlans.ExpandNode(aNode);
         }
      }

      private List<Plan> GetUserIdPlans(string userid)
      {
         List<Plan> res = new List<Plan>();
         foreach (KeyValuePair<int, Plan> kv in dsPlan)
         {
            if (kv.Value.userid.Equals(userid))
               res.Add(kv.Value);
         }
         //foreach (Plan plan in dsPlan.Data)
         //{
         //   if (plan.userid.Equals(userid))
         //      res.Add(plan);
         //}
         return res;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         RefreshData(dtpDate.Value.Date);
      }

      //Обновить набры данных
      private void RefreshData(DateTime date)
      {

         DBConnection conn = Config.GetConfig().GetConnection();
         List<IDataSet> updList = new List<IDataSet>();

         if (dsAgents.Count == 0)
         {
            List<IDataSet> refreshDataSets = new List<IDataSet>();

            refreshDataSets.Add(dsAgents);
            refreshDataSets.Add(dsDivision);

            Thread refreshThread = DataModule.RefreshGiveSets(conn, refreshDataSets, null);
            refreshThread.Join();
         }

         dsPlan.Clear();
         updList.Add(dsPlan);
         dsPlan.Filter = DataUtils.MakeFilterFromAgents(null, dsAgents);

         if (dsDivision.Count == 0)
            updList.Add(dsDivision);

         //if (dsPrice.Count == 0)
         //   updList.Add(dsPrice);

         DataModule.SetDataRepsonceHandlers(DataLoaded, DataLoadError);
         FmWait.ShowForm(this, DataModule.RefreshGiveSets(conn, updList, FmWait.ProgressIndicator));
      }

      private void DataLoaded(object sender, EventArgs e)
      {
         EndOfDataRecieve();

         BeginInvoke(new EmptyParamHandler(delegate
         {
            PlansLoad();
         }));
      }

      private void DataLoadError(EDataResponse e)
      {
         EndOfDataRecieve();

         BeginInvoke(new EmptyParamHandler(delegate
         {
            const string TITLE_STR = "Ошибка";
            string MSG_STR = String.Format("Ошибка при приеме данных: {0}", e.Msg);
            MessageBox.Show(MSG_STR, TITLE_STR, MessageBoxButtons.OK, MessageBoxIcon.Error);
         }));
      }

      private void EndOfDataRecieve()
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
      }
   }
}