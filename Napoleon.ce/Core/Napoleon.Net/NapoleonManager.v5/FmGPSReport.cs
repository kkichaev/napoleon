using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.IO;

namespace GRSoft.NapoleonManager
{
   public partial class FmGPSReport : Form
   {
      private static FmGPSReport instance;
      private SimpleDataSet<GPSPos> dsGPSPos;
      private int doc_number;
      

      public FmGPSReport()
      {
         InitializeComponent();

         dsGPSPos = new SimpleDataSet<GPSPos>(GPSPos.OBJECT_NAME, false); 
      }

      protected void btnReport_Click(object sender, EventArgs e)
      {
         StringBuilder filter = new StringBuilder();
         filter.Append(String.Format("\"date\" >= ToDate('{0:dd/MM/yyyy}') and \"date\" < ToDate('{1:dd/MM/yyyy}')", dtpBegin.Value.Date, dtpEnd.Value.Date.AddDays(1)));
         if (!cbGSM.Checked)
            filter.Append(" and \"isGSM\" = '0'");
            
         String agentWhere = AgentWhere();

         if(agentWhere.Length > 0)
            filter.Append(" and ").Append(agentWhere);

         dsGPSPos.Filter = filter.ToString();

         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsGPSPos);

         DataModule.SetDataRepsonceHandlers(DataModule_DataProcessed, DataModule_OnDataResponceError);
         FmWait.ShowForm(this,
               DataModule.RefreshGiveSets(
                  Config.GetConfig().GetConnection(),
                  list, FmWait.ProgressIndicator)
            );
      }

      private String AgentWhere()
      {
         StringBuilder result = new StringBuilder();

         if (rbAgent.Checked && cbAgent.SelectedItem != null)
         {
            Agent agent = cbAgent.SelectedItem as Agent;

            if (agent != null && !(agent is AllAgents))
               result.Append("\"userid\" = '").Append(agent.id).Append("'");
         }
         else if (rbDivision.Checked && cbDivision.SelectedItem != null)
         { 
            Division division = cbDivision.SelectedItem as Division;

            if (division != null)
            {
               List<GRSoft.NapoleonManager.Division.DivisionAgent> agents = division.GetAllAgents();

               if (agents.Count > 0)
               {
                  result.Append("\"userid\" in (");

                  List<GRSoft.NapoleonManager.Division.DivisionAgent>.Enumerator  iter = agents.GetEnumerator();
                  List<string> ids = new List<string>();

                  while(iter.MoveNext())
                  {
                     GRSoft.NapoleonManager.Division.DivisionAgent agent = iter.Current;

                     if (agent != null)
                        ids.Add(String.Format("'{0}'", agent.id));
                  };

                  result.Append(String.Join(",", ids.ToArray()));
                  result.Append(")");
               }
            }
         }

         return result.ToString();
      }

      void DataModule_DataProcessed(object sender, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         Invoke(new EmptyParamHandler(delegate
         {
            Dictionary<int, GPSPos>.Enumerator iter = dsGPSPos.GetEnumerator();
            List<GPSPos> list = new List<GPSPos>();

            while(iter.MoveNext())
            {
               DateTime dt = iter.Current.Value.date;

               if (cbTime.Checked)
               {
                  if (dt.TimeOfDay >= dtpTimeStart.Value.TimeOfDay &&
                     dt.TimeOfDay <= dtpTimeEnd.Value.TimeOfDay &&
                     iter.Current.Value != null)
                     list.Add(iter.Current.Value);
               }
               else
               {
                  if (iter.Current.Value != null)
                     list.Add(iter.Current.Value);
               }

            }

            list.Sort(new Comparison<GPSPos>(delegate(GPSPos p1, GPSPos p2) 
            {
               int result = 0;
               if (p1.agent == null)
                  return p2.agent == null ? 0 : -1;
               if (p2.agent == null)
                  return 1;

               if (p1.agent != null && p2.agent != null)
                  result = p1.agent.id.CompareTo(p2.agent.id);

               if (result == 0)
                  result = p1.date.CompareTo(p2.date);

               return result;
            }));

            MakeReport(list);
         }
         ));
      }

      protected virtual void MakeReport(List<GPSPos> list)
      {
         Dictionary<Agent, double> path = new Dictionary<Agent,double>();
         Dictionary<Agent, int> test = new Dictionary<Agent, int>();

         double lat = 0;
         double lon = 0;

         foreach(GPSPos pos in list)
         {
            if (pos.agent != null)
            {
               if (path.ContainsKey(pos.agent))
                  path[pos.agent] += Utils.Coordutils.Distance(lat, lon, pos.latitude, pos.longitude);
               else
                  path.Add(pos.agent, 0);

               if (!test.ContainsKey(pos.agent))
                  test[pos.agent] = 1;
               else
                  test[pos.agent]++;

               lat = pos.latitude;
               lon = pos.longitude;
            }
         }

         string fileName = String.Format("gsmreport{0}.html", ++doc_number);
         string result = System.IO.Path.GetTempPath() + fileName;

         string html = "<html><head> " +
           "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">" +
           "</head><body>" +
              "<FONT FACE=\"Arial\">" +
              "<table cellpadding=5 CELLSPACING=0 border=1 BORDERCOLOR=\"#000000\">";
         html += String.Format("<H3>Отчет по километражу </H3><p>");
         html += String.Format("<FONT SIZE=\"2\">Период: <b>{0} - {1}</b><br><br>",
            dtpBegin.Value.Date.ToString("dd.MM.yyyy"), dtpEnd.Value.Date.ToString("dd.MM.yyyy"));

         html += String.Format("<tr BGCOLOR=\"#CCCCCC\" ><td><FONT SIZE=\"2\"><b>{0}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{1}</b></td></tr>",
            "Агент", "Пробег");

         double sumDistance = 0;
         Dictionary<Agent, double>.Enumerator iter = path.GetEnumerator();

         while(iter.MoveNext())
         {
            html += string.Format("<tr><td>{0}</td><td>{1}</td></tr>",
               iter.Current.Key.Name, DistanceHuman((int)iter.Current.Value));

            sumDistance += iter.Current.Value;
         }

         html += "</table>";
         html += "Итого:" + DistanceHuman((int)sumDistance) + "<br>";
         html += "<SUB>Построен в системе \"Наполеон\" <a href=http://grsoft.ru/>http://grsoft.ru/</a></SUB>";
         html += "</body></html>";

         using (StreamWriter sw = new StreamWriter(result))
         {
            sw.Write(html);
            sw.Flush();
         }

         OpenLink.NewWindow(String.Format("\"{0}\"", result));
      }

      private String DistanceHuman(int distance)
      {
         int km = distance / 1000;
         int met = distance % 1000;

         return km > 0 ? String.Format("{0}км {1}м", km, met) : String.Format("{0}м", met);
      }

      void DataModule_OnDataResponceError(EDataResponse e)
      {
         FmWait.CloseForm();
         DataModule.ClearEvents();
         MessageBox.Show(e.Msg);
      }

      public static void ShowInstance()
      {
         if (instance == null)
         {
            instance = new FmGPSReport();
            instance.Show();
         }
         else
            instance.Activate();
      }

      class AllAgents : Agent
      {
         public override string ToString()
         {
            return "Все";
         }
      }

      private void FmGsmReport_Load(object sender, EventArgs e)
      {
         dtpBegin.Value = DateTime.Now;
         dtpEnd.Value = DateTime.Now;

         Manager m = CurrentUser.user as Manager;

         if (m != null)
         {
            foreach (Division.DivisionAgent a in m.Division.GetAllAgents())
               if (a.agent != null && cbAgent.Items.Contains(a.agent) == false)
                  cbAgent.Items.Add(a.agent);

            cbDivision.Items.Add(m.Division);
            foreach (Division d in m.Childs)
               cbDivision.Items.Add(d);
         }

         if (cbAgent.Items.Count > 0)
            cbAgent.SelectedIndex = 0;

         if (cbDivision.Items.Count > 0)
            cbDivision.SelectedIndex = 0;

         //Agents dsAgent = CurrentUser.user.GetAgents();// Agents.GetDataSet();

         //if (dsAgent != null)
         //{
         //   cbAgent.Items.Clear();
         //   cbAgent.Items.Add(new AllAgents());

         //   List<Agent> alist = new List<Agent>();
         //   alist.AddRange(dsAgent.Values);
         //   alist.Sort(new Comparison<Agent>(delegate(Agent a1, Agent a2) { return a1.Name.CompareTo(a2.Name); }));

         //   cbAgent.Items.AddRange(alist.ToArray());
         //   cbAgent.SelectedIndex = 0;
         //}

         //DivisionList dsDivision = DivisionList.GetDataSet();
         //dsDivision.CheckAgents();

         //if (dsDivision.Count > 0)
         //{
         //   List<Division> dlist = new List<Division>();
         //   dlist.AddRange(dsDivision.Values);
         //   dlist.Sort(new Comparison<Division>(
         //      delegate(Division d1, Division d2) { 
         //         return d1.DivisionName.CompareTo(d2.DivisionName); }));
         //   cbDivision.Items.AddRange(dlist.ToArray());
         //   cbDivision.SelectedIndex = 0;
         //}

         cbAgent.Enabled = false;
         cbDivision.Enabled = false;
         dtpTimeStart.Enabled = false;
         dtpTimeEnd.Enabled = false;
         rbAgent.Checked = true;
      }

      private void FmGsmReport_FormClosed(object sender, FormClosedEventArgs e)
      {
         instance = null;
      }

      private void rbAgent_CheckedChanged(object sender, EventArgs e)
      {
         if (sender is RadioButton)
            cbAgent.Enabled = (sender as RadioButton).Checked;
      }

      private void rbDivision_CheckedChanged(object sender, EventArgs e)
      {
         if (sender is RadioButton)
            cbDivision.Enabled = (sender as RadioButton).Checked;
      }

      private void cbTime_CheckedChanged(object sender, EventArgs e)
      {
         if (sender is CheckBox)
         {
            bool chd = (sender as CheckBox).Checked;
            dtpTimeStart.Enabled = chd;
            dtpTimeEnd.Enabled = chd;
         }
      }
   }
}
