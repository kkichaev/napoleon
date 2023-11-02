using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;

namespace GRSoft.NapoleonManager
{
   public partial class FmRouteApproval : Form
   {
      private DataSet<string, Agent> dsAgent;
      private DataSet<string, AgentRoute> dsAgentRoute;
      private DataSet<string, Org> dsOrg;
      
      public FmRouteApproval()
      {
         InitializeComponent();
         dsAgent = (DataSet<string, Agent>)DataModule.Get(Agent.OBJECT_NAME) ?? 
            new DataSet<string, Agent>(Agent.OBJECT_NAME);
         dsAgentRoute = (DataSet<string, AgentRoute>)DataModule.Get(AgentRoute.OBJECT_NAME) ??
            new DataSet<string, AgentRoute>(AgentRoute.OBJECT_NAME);
         dsOrg = (DataSet<string, Org>)DataModule.Get(Org.OBJECT_NAME) ??
            new DataSet<string, Org>(Org.OBJECT_NAME);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         btnRefresh.Enabled = false;
         DataModule.SetDataRepsonceHandlers(DataProcessed, DataConnectionError);

         List<IDataSet> updSets = new List<IDataSet>();
         updSets.Add(dsOrg);
         updSets.Add(dsAgent);
         updSets.Add(dsAgentRoute);

         DBConnection conn = Config.GetConfig().GetConnection();
         FmWait.ShowForm(this, DataModule.RefreshGiveSets(conn, updSets, FmWait.ProgressIndicator));
      }

      //Окончание выборки, заполняются внутренние наборы
      void DataProcessed(System.Object setnder, EventArgs e)
      {
         DataModule.ClearEvents();

         this.Invoke(new InvokeDelegate(delegate
         {
            FmWait.CloseForm();

            ReloadData();
            btnRefresh.Enabled = true;
         }));
      }

      protected void ReloadData()
      {
         lbAgents.Items.Clear();

         foreach (Agent a in dsAgent.Data)
            lbAgents.Items.Add(a);

         lbAgents.Sorted = true;
      }

      private void DataConnectionError(EDataResponse e)
      {
         DataModule.ClearEvents();

         Invoke(new EmptyParamHandler(delegate
         {
            FmWait.CloseForm();
            btnRefresh.Enabled = true;

            const string TITLE = "Ошибка";

            MessageBox.Show(e.Msg, TITLE, MessageBoxButtons.OK,
               MessageBoxIcon.Error);
         }));
      }

      class GridData
      {
         public Org org = null;
         public bool[] days = null;
      }

      private void lbAgents_SelectedIndexChanged(object sender, EventArgs e)
      {
         dgvRoute.Rows.Clear();
         string userid = ((Agent)lbAgents.SelectedItem).id;
         lbDate.Text = string.Empty;

         if(dsAgentRoute.ContainsKey(userid))
         {
            List<AgentRouteItem> list = dsAgentRoute[userid].items;
            Dictionary<Org, bool[]> data = new Dictionary<Org,bool[]>();

            foreach (AgentRouteItem i in list)
            {
               bool[] d = null;

               if (!data.ContainsKey(i.org))
               {
                  d = new bool[7];
                  data[i.org] = d;
               }
               else
                  d = data[i.org];

               d[i.day - 1] = true;
            }
            
            List<GridData> gd = new List<GridData>();

            foreach(KeyValuePair<Org, bool[]> k in data)
            {
               GridData d = new GridData();
               d.org = k.Key;
               d.days = k.Value;

               gd.Add(d);
            }

            gd.Sort((lhs, rhs) => { return lhs.org.CompareTo(rhs.org); });

            foreach (GridData g in gd)
               dgvRoute.Rows.Add(new object[] { g.org, g.days[0], g.days[1], g.days[2], g.days[3], g.days[4], g.days[5], g.days[6] });

            lbDate.Text = dsAgentRoute[userid].date.ToString("dd.MM.yyyy HH:mm");
         }
      }


      private void FmRouteApproval_Load(object sender, EventArgs e)
      {
         lbDate.Text = string.Empty;
      }

      private void btnApply_Click(object sender, EventArgs e)
      {
         //Agent a = lbAgents.SelectedItem as Agent;
         //FmApproveMsg dialog = new FmApproveMsg();
         //dialog.tbText.Text = "Маршрут утвержден.";

         //if(a != null && dsAgentRoute.ContainsKey(a.id) && dialog.ShowDialog() == DialogResult.OK )
         //{
         //   Dictionary<string, OrgFolder> dic = new Dictionary<string, OrgFolder>();

         //   foreach (AgentRouteItem i in dsAgentRoute[a.id].items)
         //   {
         //      isertFolder(i, dic, a);    
         //   }

         //   DataSet<int, OrgFolder> dsOrgFolder = new DataSet<int, OrgFolder>(OrgFolder.OBJECT_NAME);
         //   Dictionary<string, OrgFolder>.Enumerator enumer = dic.GetEnumerator();

         //   while (enumer.MoveNext())
         //      dsOrgFolder.Add(dsOrgFolder.Count, enumer.Current.Value);

         //   List<ReplacedSet> replaced = new List<ReplacedSet>();
         //   replaced.Add(new ReplacedSet(a.id, dsOrgFolder));

         //   if (DataModule.UpdateDataSet(null, null, replaced, Config.GetConfig().GetConnection()) == false)
         //   {
         //      MessageBox.Show("Ошибка при записи в базу данных.", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
         //   }else
         //      DataModule.SendMessage(dialog.tbText.Text.Trim(), a.id, Config.GetConfig().GetConnection());
         //}
      }

      private void isertFolder(AgentRouteItem i, Dictionary<int, RouteTemplate> dic, Agent agent)
      {
         int day = i.day;

         RouteTemplate f = null;
         if (dic.ContainsKey(day))
            f = dic[day];
         else
         {
            f = new RouteTemplate();
            f.dayOfWeek = day;
            f.agent = agent;
            f.items = new List<RouteTemplate.Item>();
            dic.Add(day, f);
         }

         bool r = false;

         foreach(RouteTemplate.Item fi in f.items)
            if(fi.id.Equals(i.id))
            {
               r = true;
               break;
            }

         if (!r)
         {
            RouteTemplate.Item fi = new RouteTemplate.Item();
            fi.id = i.id;
            fi.index = i.pos;
            f.items.Add(fi);
         }
      }

      private string days(int p)
      {
         return WeekDay.fullnames[p-1];
      }

      private void btnReject_Click(object sender, EventArgs e)
      {
         Agent a = lbAgents.SelectedItem as Agent;
         FmApproveMsg dialog = new FmApproveMsg();
         dialog.tbText.Text = "Маршрут отклонен.";

         if (a != null && dialog.ShowDialog() == DialogResult.OK)
         {
            DataModule.SendMessage(dialog.tbText.Text.Trim(), a.id, Config.GetConfig().GetConnection());
         }
      }
   }
}
