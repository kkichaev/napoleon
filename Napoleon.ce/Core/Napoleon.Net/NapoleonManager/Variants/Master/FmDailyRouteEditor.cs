using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.UILib;
using System.Threading;
using System.IO;

namespace GRSoft.NapoleonManager
{
   public partial class FmDailyRouteEditor : Form
   {
      private string selID = null;
      bool loading = true;
      Agent curAgent;

      public FmDailyRouteEditor()
      {
         InitializeComponent();

         dgvOrgs.AutoGenerateColumns = false;
         dgvAgents.AutoGenerateColumns = false;
         
         dtpBeginDate.Value = DateTime.Now.AddDays(-7);
         dtpEndDate.Value = DateTime.Now.AddDays(14);

         MakeDateNodes();
      }

      public void SetCurrentAgent(String userid)
      {
         selID = userid;
      }

      protected override void  OnHandleCreated(EventArgs e)
      {
 	      base.OnHandleCreated(e);
         if (dgvAgents.DataSource == null)
            LoadAgents();

         if (selID != null)
         {
            foreach (DataGridViewRow row in dgvAgents.Rows)
            {
               Agent a = row.DataBoundItem as Agent;
               if (a.id.Equals(selID))
               {
                  row.Selected = true;
                  OnAgentChanged(a);
                  break;
               }
            }
         }
         else
         {
            if (dgvAgents.Rows.Count > 0)
               OnAgentChanged(dgvAgents.Rows[0].DataBoundItem as Agent);
         }
         loading = false;
      }

      private void LoadAgents()
      {
         Manager m = CurrentUser.user as Manager;
         List<Agent> agents = new List<Agent>();
         foreach(Division.DivisionAgent da in m.Division.GetAllAgents())
         {
            if( da.agent == null )
               continue;
            agents.Add(da.agent);
         }

         agents.Sort(CmpAgent);
         dgvAgents.DataSource = agents;
      }

      int CmpAgent(Agent a, Agent b)
      {
         return a.Name.CompareTo(b.Name);
      }

      private void dgvAgents_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         Agent a = dgvAgents.Rows[e.RowIndex].DataBoundItem as Agent;
         OnAgentChanged(a);
      }


      DataSet<string, Org> orgs;
      DataSet<int, DailyRoute> route;

      private void OnAgentChanged(Agent a)
      {
         if (loading)
            return;

         loading = true;

         curAgent = a;

         List<IDataSet> updSets = new List<IDataSet>();
         orgs = DataModule.GetUserDataSet(a.id, Org.OBJECT_NAME, typeof(DataSet<string, Org>)) as DataSet<string, Org>;
         orgs.Command = new ServerCommand(Commands.Impersonate(Commands.GET, a.id), orgs.Name);
         updSets.Add(orgs);

         route = new DataSet<int, DailyRoute>(DailyRoute.OBJECT_NAME, false);
         route.Filter = String.Format("userid in ('{0}') and date >= ToDate('{1:dd/MM/yyyy}') and date < ToDate('{2:dd/MM/yyyy}')", 
            a.id, dtpBeginDate.Value, dtpEndDate.Value.AddDays(1));
         updSets.Add(route);

         DBConnection conn = Config.GetConfig().GetConnection();
         DataModule.OnDataResponceError += new EventDataResponseError(DataModule_OnDataResponceError);
         DataModule.DataProcessed += new EventHandler(DataModule_DataProcessed);
         FmWait.ShowForm(this, DataModule.RefreshGiveSets(conn, updSets, FmWait.ProgressIndicator));
      }

      void DataModule_OnDataResponceError(EDataResponse e)
      {
         FmWait.CloseForm();
         DataModule.ClearEvents();
         loading = false;

         MessageBox.Show(e.Msg, "Ошибка подключения");
      }

      void DataModule_DataProcessed(object sender, EventArgs e)
      {
         FmWait.CloseForm();
         DataModule.ClearEvents();

         Invoke(new InvokeDelegate(delegate() { RefreshData(); } ));
      }

      void MakeDateNodes()
      {
         dgvRoute.SuspendLayout();
         TreeGridNodeCollection nodes = dgvRoute.Nodes;
         nodes.Clear();

         DateTime dt = dtpBeginDate.Value.Date;
         do
         {
            object[] data = { dt.ToString("dd/MM/yyyy") };
            TreeGridNode node = nodes.Add(data);
            node.Tag = dt;

            dt = new DateTime(dt.AddDays(1).Ticks);
         } while (dt <= dtpEndDate.Value.Date );

         dgvRoute.ResumeLayout();
      }

      TreeGridNode FindNode(TreeGridNodeCollection nodes, DateTime tag)
      {
         TreeGridNode node = null;

         foreach (TreeGridNode n in nodes)
         {
            if (tag == (DateTime)n.Tag)
            {
               node = n;
               break;
            }
         }
         return node;
      }

      void FillDateNodes()
      {
         dgvRoute.SuspendLayout();

         TreeGridNodeCollection nodes = dgvRoute.Nodes;
         foreach (TreeGridNode n in nodes)
            n.Nodes.Clear();

         foreach (DailyRoute dr in route.Data)
         {
            TreeGridNode node = FindNode(nodes, dr.date);
            if (node == null)
               continue; // не должно такого быть

            foreach (DailyRouteItem dri in dr.items)
            {
               if (dri.org == null)
                  continue;

               AddOrgToRote(node, dri.org);
            }
         }

         dgvRoute.ResumeLayout();
      }

      void AddOrgToRote(TreeGridNode node, Org o)
      {
         object[] chdata = { o.Name };
         TreeGridNode chnode = new TreeGridNode();
         chnode.CreateCells(dgvRoute, chdata);
         chnode.Tag = o;

         node.Nodes.Add(chnode);
         node.Expand();
      }

      private void RefreshData()
      {
         try
         {
            List<Org> orgList = new List<Org>();
            foreach (Org o in orgs.Data)
               orgList.Add(o);
            orgList.Sort();
            dgvOrgs.DataSource = orgList;

            FillDateNodes();
         }
         finally
         {
            loading = false;
         }
      }

      private void dgvRoute_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         DataGridViewRow r = dgvRoute.Rows[e.RowIndex];
         if (r.Tag is DateTime)
         {
            DateTime d = (DateTime)r.Tag;
            if (!r.Selected)
               e.CellStyle.BackColor = Color.LightGray;
            if (d.Equals(DateTime.Now.Date))
               e.CellStyle.ForeColor = Color.Red;
         }
      }

      Point startOrgPoint;
      Org clickedOrg;
      private void dgvOrgs_MouseDown(object sender, MouseEventArgs e)
      {
         if( e.Button == MouseButtons.Left )
         {
            DataGridView.HitTestInfo hti = dgvOrgs.HitTest(e.X, e.Y);
            int rowIndex = hti.RowIndex;
            if( rowIndex >= 0 && rowIndex < dgvOrgs.Rows.Count && hti.Type == DataGridViewHitTestType.Cell )
            {
               startOrgPoint = new Point(e.X, e.Y);
               clickedOrg = dgvOrgs.Rows[rowIndex].DataBoundItem as Org;
            }
         }
      }

      int Distance(Point start, Point end)
      {
         return (int)Math.Sqrt(Math.Pow(end.X - start.X, 2) + Math.Pow(end.Y - start.Y, 2));
      }

      private void dgvOrgs_MouseMove(object sender, MouseEventArgs e)
      {
         if (clickedOrg != null && Distance(startOrgPoint, e.Location) > 3)
         {
            dgvOrgs.DoDragDrop(new System.Windows.Forms.DataObject("Org", clickedOrg), DragDropEffects.Copy);
            clickedOrg = null;
         }
      }

      private void dgvRoute_DragEnter(object sender, DragEventArgs e)
      {
         if (e.Data.GetDataPresent("Org"))
            e.Effect = DragDropEffects.Copy;
      }

      private void dgvRoute_DragDrop(object sender, DragEventArgs e)
      {
         if (e.Data.GetDataPresent("Org"))
         {
            Org o = e.Data.GetData("Org") as Org;
            if (o != null)
            {
               Point cli = dgvRoute.PointToClient(new Point(e.X, e.Y));
               TreeGridNode node = GetDateNode(cli.X, cli.Y);
               if (node != null)
               {
                  AddOrgToRote(node, o);
                  Dirty = true;
               }
            }
         }
      }

      bool Dirty
      {
         set
         {
            tsSave.Enabled = value;
         }

         get { return tsSave.Enabled; }
      }

      private TreeGridNode GetDateNode(int x, int y)
      {
         TreeGridNode ret = null;
         
         DataGridView.HitTestInfo hti = dgvRoute.HitTest(x, y);
         int rowIndex = hti.RowIndex;
         if (hti.Type == DataGridViewHitTestType.Cell && rowIndex >= 0 && rowIndex < dgvRoute.Rows.Count)
         {
            ret = dgvRoute.Rows[rowIndex] as TreeGridNode;
            while (ret != null && !(ret.Tag is DateTime))
               ret = ret.Parent;
         }

         return ret;
      }

      private void dgvRoute_MouseDown(object sender, MouseEventArgs e)
      {
         if (e.Button == MouseButtons.Right)
         {
            DataGridView.HitTestInfo hti = dgvRoute.HitTest(e.X, e.Y);
            int rowIndex = hti.RowIndex;
            if (hti.Type == DataGridViewHitTestType.Cell && rowIndex >= 0 && rowIndex < dgvRoute.Rows.Count)
               dgvRoute.CurrentCell = dgvRoute.Rows[rowIndex].Cells[hti.ColumnIndex];
         }
      }

      private void tsmDel_Click(object sender, EventArgs e)
      {
         foreach (DataGridViewCell c in dgvRoute.SelectedCells)
         {
            DataGridViewRow r = dgvRoute.Rows[c.RowIndex];
            if (r.Tag is Org)
            {
               TreeGridNode n = (TreeGridNode)r;
               n.Parent.Nodes.Remove(n);
               Dirty = true;
            }
            else if (r.Tag is DateTime)
            {
               ((TreeGridNode)r).Nodes.Clear();
               Dirty = true;
            }
            break;
         }
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         if (Dirty)
         {
            DialogResult dr = MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
            if (dr == DialogResult.Yes)
               SaveChanges();
            else if (dr == DialogResult.Cancel)
               e.Cancel = true;
         }
         base.OnClosing(e);
      }

      private void SaveChanges()
      {
         if (curAgent == null)
         {
            return;
         }

         DataSet<int, DailyRoute> wr = new DataSet<int, DailyRoute>(DailyRoute.OBJECT_NAME, false);
         foreach (TreeGridNode node in dgvRoute.Nodes)
         {
            if (!node.HasChildren)
               continue;

            DailyRoute dr = new DailyRoute();
            dr.date = (DateTime)node.Tag;
            dr.userid = curAgent.id;
            dr.items = new List<DailyRouteItem>();
            
            foreach (TreeGridNode ch in node.Nodes)
            {
               DailyRouteItem dri = new DailyRouteItem();
               dri.id = (ch.Tag as Org).id;
               
               dr.items.Add(dri);
            }

            wr.Add(wr.Count, dr);
         }

         bool done = false;
         DBConnection conn = Config.GetConfig().GetConnection();
         if (wr.Count > 0)
         {
            ReplacedSet rs = new ReplacedSet(curAgent.id, wr);
            List<ReplacedSet> replSet = new List<ReplacedSet>(new ReplacedSet[] { rs });
            done = DataModule.UpdateDataSet(null, null, replSet, conn);
         }
         else
         {
            wr.Filter = String.Format("userid in ('{0}')", curAgent.id);
            done = DataModule.RemoveDataSet(wr, conn);
         }
         if (!done)
            MessageBox.Show("Ошибка при записи");

         Dirty = !done;
      }

      private void tsSave_Click(object sender, EventArgs e)
      {
         SaveChanges();
      }

      private void tsRefresh_Click(object sender, EventArgs e)
      {
         if (Dirty)
         {
            DialogResult dr = MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
            if (dr == DialogResult.Yes)
               SaveChanges();
            else if (dr == DialogResult.Cancel)
               return;
         }
         MakeDateNodes();
         OnAgentChanged(curAgent);
      }

      private void tsbMakeHtml_Click(object sender, EventArgs e)
      {
         DailyReportParams.Data data = new DailyReportParams.Data();
         data.date = dtpBeginDate.Value;
         data.dateEnd = dtpEndDate.Value;
         DailyReportParams dlg = new DailyReportParams(data);
         if( curAgent != null )
            dlg.SetSelectedAgent(curAgent.id);
         if (dlg.ShowDialog() == DialogResult.OK)
         {
            string filter = "";
            if (data.agent != null)
               filter = "userid in ('" + data.agent.id + "')";
            else if (data.division != null)
               filter = FmMessageHistory.UserIdIsStr(data.division.GetAllAgents());

            string DATA_FILTER = String.Format("{0} >= ToDate('{1:dd/MM/yyyy}') and {0} <= ToDate('{2:dd/MM/yyyy}')",
               "created", data.date.Date, data.dateEnd.Date);

            DataSet<int, Order> orders = new DataSet<int, Order>(Order.OBJECT_NAME, false);
            DataSet<int, VisitInfo> visits = new DataSet<int, VisitInfo>(Visit.V_OBJECT_NAME, false);
            DataSet<int, DailyRoute> route = new DataSet<int, DailyRoute>(DailyRoute.OBJECT_NAME, false);

            orders.Filter = filter + " and " + DATA_FILTER;
            visits.Filter = filter + " and " + DATA_FILTER;
            route.Filter = filter + " and " + String.Format("{0} >= ToDate('{1:dd/MM/yyyy}') and {0} <= ToDate('{2:dd/MM/yyyy}')",
               "date", data.date.Date, data.dateEnd.Date);

            List<IDataSet> upd = new List<IDataSet>(new IDataSet[] { orders, visits, route });

            Thread th = DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), upd, FmWait.ProgressIndicator);
            FmWait.ShowForm(this, th);
            th.Join();

            FmWait.CloseForm();

            DoReport(data, route, orders, visits);
         }
      }

      int RouteCmp(DailyRoute a, DailyRoute b)
      {
         return a.date.CompareTo(b.date);
      }

      List<DailyRoute> GetRoute(DataSet<int, DailyRoute> route, Agent a)
      {
         List<DailyRoute> ret = new List<DailyRoute>();
         
         foreach (DailyRoute dr in route.Data)
         {
            if (dr.userid.Equals(a.id))
               ret.Add(dr);
         }

         ret.Sort(RouteCmp);
         return ret;
      }

      private void DoReport(DailyReportParams.Data data, DataSet<int, DailyRoute> route, DataSet<int, Order> orders, DataSet<int, VisitInfo> visits)
      {
         StringBuilder html = new StringBuilder("<html><head><meta http-equiv='content-type' content='text/html; charset=utf-8'></head>");
         html.AppendFormat("<body><FONT FACE='Arial'><H2>Отчет за период с {0:dd/MM/yyyy} по {1:dd/MM/yyyy}</H2>", data.date, data.dateEnd);

         foreach (Agent a in data.Agents)
         {
            List<DailyRoute> r = GetRoute(route, a);
            html.AppendFormat("<H3>{0}</H3><table cellpadding=5 CELLSPACING=0 border=1 BORDERCOLOR=\"#000000\">", a.Name);
            foreach (DailyRoute dr in r)
            {
               html.AppendFormat("<tr BGCOLOR=\"#CCCCCC\"><td colspan='2' FONT SIZE=\"2\">{0:dd/MM/yyyy}</td></tr>", dr.date);
               foreach(DailyRouteItem dri in dr.items)
               {
                  if (dri.org != null)
                  {
                     string visited = ((IsVisited(a.id, dr.date.Date, dri.org.id, orders, visits)) ? "посетил" : "-");
                     html.AppendFormat("<tr><td FONT SIZE=\"2\">{0}</td><td FONT SIZE=\"2\">{1}</td></tr>", dri.org.Name, visited);
                  }
               }
            }
            html.Append("</table>");
         }
         html.Append("<p><SUB>Построен в системе \"Наполеон\" <a href=http://grsoft.ru/>http://grsoft.ru/</a></SUB>");
         html.Append("</body></html>");

         string fileName = String.Format("daily_route_{0}.html", ++doc_number);
         string result = System.IO.Path.GetTempPath() + fileName;
         StreamWriter sw = new StreamWriter(result);
         sw.Write(html.ToString());
         sw.Flush();
         sw.Close();

         OpenLink.NewWindow(String.Format("\"{0}\"", result));
      }

      static int doc_number = 0;

      private bool IsVisited(string agent, DateTime date, string org, DataSet<int, Order> orders, DataSet<int, VisitInfo> visits)
      {
         foreach(Order o in orders.Data )
            if(o.AgentID == agent && o.created.Date == date)
               return true;
            
         foreach(VisitInfo v in visits.Data )
            if(v.AgentID == agent && v.created.Date == date)
               return true;

         return false;
      }
   }
   public class DailyRouteItem : GRSoft.Network.DataObject
   {
      public string id = "";

      [Reference("Org", "id")]
      public Org org = null;
   }

   public class DailyRoute : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "DailyRoute";

      public string userid = "";
      [Reference("Agents", "userid")]
      public Agent agent = null;

      public DateTime date;

      [ItemType(typeof(DailyRouteItem))]
      public List<DailyRouteItem> items = null;

   }
}
