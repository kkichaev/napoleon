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
   public partial class FmVisitReport : Form
   {
      public DataSet<int, Order> dsOrder;
      public DataSet<int, Visit> dsVisit;
      protected DataSet<int, OrgFolder> dsOrgFolder;
      protected DataSet<string, Org> dsOrg;

      public FmVisitReport()
      {
         InitializeComponent();

         dsOrder = (DataSet<int, Order>)DataModule.Get(Order.OBJECT_NAME) ??
            new DataSet<int, Order>(Order.OBJECT_NAME);
         dsOrgFolder = (DataSet<int, OrgFolder>)DataModule.Get(OrgFolder.OBJECT_NAME) ??
            new DataSet<int, OrgFolder>(OrgFolder.OBJECT_NAME);
         dsVisit = (DataSet<int, Visit>)DataModule.Get(Visit.OBJECT_NAME) ??
            new DataSet<int, Visit>(Visit.OBJECT_NAME);
      }

      private void FmVisitReport_Load(object sender, EventArgs e)
      {
         Manager mc = CurrentUser.user as Manager;
         List<Agent> list = new List<Agent>();

         if (mc != null)
         {
            foreach (Division.DivisionAgent da in mc.Division.GetAllAgents())
            {
               if (da.agent == null)
                  continue;
               
               list.Add(da.agent);
            }
         }

         list.Sort(new Comparison<Agent>(
            delegate(Agent ai1, Agent ai2) 
               { return ai1.name.CompareTo(ai2.name); }));

         cbAgents.Items.Clear();
         cbAgents.Items.AddRange(list.ToArray());
      }

      private void btnReport_Click(object sender, EventArgs e)
      {
         Agent agent = cbAgents.SelectedItem as Agent;

         if(agent != null)
         {
            dsOrder.Filter = String.Format("created >= ToDate('{1:dd/MM/yyyy}') and created < ToDate('{2:dd/MM/yyyy}') and userid='{0}'",
               agent.id, dtpBegin.Value.Date, dtpEnd.Value.Date.AddDays(1));
            dsVisit.Filter = String.Format("date >= ToDate('{1:dd/MM/yyyy}') and date < ToDate('{2:dd/MM/yyyy}') and userid='{0}'",
               agent.id, dtpBegin.Value.Date, dtpEnd.Value.Date.AddDays(1));

            dsOrgFolder.Filter = String.Format("userid='{0}'", agent.id);
            dsOrg = DataModule.GetUserDataSet(agent.id, Org.OBJECT_NAME, 
               typeof(DataSet<string, Org>)) as DataSet<string, Org>;
            dsOrg.Command = new ServerCommand(Commands.Impersonate(
               Commands.GET, agent.id), dsOrg.Name);
            List<IDataSet> updSets = new List<IDataSet>();
            updSets.Add(dsOrg);
            updSets.Add(dsVisit);
            updSets.Add(dsOrder);
            updSets.Add(dsOrgFolder);

            DataModule.SetDataRepsonceHandlers(DataProcessed,
               DataConnectionError);

            FmWait.ShowForm(this, DataModule.RefreshGiveSets(
               Config.GetConfig().GetConnection(),
               updSets, FmWait.ProgressIndicator));
         }
      }

      private void EndOfDataReceive()
      {
         FmWait.CloseForm();
         DataModule.ClearEvents();
      }

      private void DataProcessed(object o, EventArgs e)
      {
         EndOfDataReceive();
         Invoke(new EmptyParamHandler(
            delegate
            {
               VisitReport report = new VisitReport("visit_{0}.html");
               report.Build(this);
               report.Show();

            }));
      }

      //Произошла ошибка в соединении
      private void DataConnectionError(EDataResponse e)
      {
         EndOfDataReceive();

         Invoke(new EmptyParamHandler(delegate
         {
            Visible = false;
            const string TITLE = "Ошибка";

            MessageBox.Show(e.Msg, TITLE, MessageBoxButtons.OK,
               MessageBoxIcon.Error);

            Close();
         }));
      }

      class VisitReport : HtmlReport
      {
         public VisitReport(string fileMask)
            : base(fileMask)
         { 
         }

         internal void Build(FmVisitReport form)
         {
            StringBuilder html = new StringBuilder("<html><head>" +
            "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"></head>" +
            "<body><FONT FACE=\"Arial\">");

            html.Append("Агент: ");

            Agent agent = form.cbAgents.SelectedItem as Agent;

            if (agent != null)
               html.Append(agent.name);

            html.Append("<br>");
            html.Append("период с ")
               .Append(form.dtpBegin.Value.Date.ToShortDateString())
               .Append(" по ")
               .Append(form.dtpEnd.Value.Date.ToShortDateString())
               .Append("<br>");

            html.Append("<table cellpadding=5 CELLSPACING=0 border=1 BORDERCOLOR=\"#000000\">\n");

            DateTime begin = form.dtpBegin.Value.Date;
            DateTime end = form.dtpEnd.Value.Date;

            List<DateTime> dates = new List<DateTime>();
            List<List<Org>> orgsPlan = new List<List<Org>>();
            List<List<Org>> orgsFact = new List<List<Org>>();

            while(begin < end.AddDays(1))
            {
               orgsPlan.Add(OrdersDetail.GetRoutePeriod(begin, begin.AddDays(1), Agents.GetDataSet()[agent.id]));
               List<Org> dayList = new List<Org>();
               orgsFact.Add(dayList);

               foreach (Order o in form.dsOrder.Data)
               {
                  if (o.created >= begin && o.created < begin.AddDays(1))
                  {
                     if (!dayList.Contains(o.org))
                        dayList.Add(o.org);
                  }
               }

               foreach (Visit v in form.dsVisit.Data)
               {
                  if (v.date >= begin && v.date < begin.AddDays(1))
                  {
                     if (!dayList.Contains(v.org))
                        dayList.Add(v.org);
                  }
               }

               dates.Add(begin);
               begin = begin.AddDays(1);
            }

            html.Append("<tr>");

            int cnt = 0;

            foreach (List<Org> o in orgsPlan)
               if (cnt < o.Count)
                  cnt = o.Count;

            foreach (List<Org> o in orgsFact)
               if (cnt < o.Count)
                  cnt = o.Count;

            foreach(DateTime d in dates)
            {
               html.Append("<td colspan=2>").Append(d.ToShortDateString())
                  .Append(" ").Append(new WeekDay(d.DayOfWeek).FullName).Append("</td>");
            }

            html.Append("</tr>");

            string[] capt = new string[]{"План","Факт"};

            html.Append("<tr>");

            for (int i = 0; i < dates.Count; i++)
            {
               html.Append("<td>").Append(capt[0]).Append("</td>")
                  .Append("<td>").Append(capt[1]).Append("</td>");
            }

            html.Append("</tr>");

            for (int i = 0; i < cnt; i++)
            {
               html.Append("<tr>");
               for (int a = 0; a < dates.Count; a++)
               {
                  html.Append("<td>").Append(orgsPlan.Count > a &&
                     orgsPlan[a].Count > i && orgsPlan[a][i] != null ? orgsPlan[a][i].Name : "").Append("</td>")
                     .Append("<td>").Append(orgsFact.Count > a &&
                     orgsFact[a].Count > i && orgsFact[a][i] != null ? orgsFact[a][i].Name : "").Append("</td>");

               }
               html.Append("</tr>");
            }

            html.Append("</table>\n");
            html.Append(GetFooter());
            html.Append("</body></html>");

            WriteToTmpFile(html.ToString());
         }

         internal void Show()
         {
            OpenReportInAssociationBrowser();
         }
      }
   }

   

}
