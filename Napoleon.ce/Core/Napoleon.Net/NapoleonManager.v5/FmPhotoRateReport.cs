using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.IO;
using GRSoft.NapoleonManager.Utils;

namespace GRSoft.NapoleonManager
{
   public partial class FmPhotoRateReport : Form
   {
      static DateTime lastBegin = DateTime.Now;
      static DateTime lastEnd = DateTime.Now;

      DataSet<int, VisitInfo> dsVisits = new DataSet<int, VisitInfo>(VisitInfo.V_OBJECT_NAME, false);

      int CmpAgents(Division.DivisionAgent a, Division.DivisionAgent b)
      {
         return a.AgentName.CompareTo(b.AgentName);
      }

      public FmPhotoRateReport()
      {
         InitializeComponent();

         dtpBegin.Value = lastBegin;
         dtpEnd.Value = lastEnd;

         Manager m = CurrentUser.user as Manager;
         if (m != null)
         {
            foreach (Division.DivisionAgent a in m.Division.GetAllAgents())
               if (a.agent != null && cbAgents.Items.Contains(a) == false)
                  cbAgents.Items.Add(a);
         }
      }

      public void SetSelectedAgent(string id)
      {
         foreach (Division.DivisionAgent a in cbAgents.Items)
            if (a.agent.id == id)
               cbAgents.SelectedItem = a;
      }

      private void rbDivision_Click(object sender, EventArgs e)
      {
         cbAgents.Enabled = false;
      }

      private void rbAgents_Click(object sender, EventArgs e)
      {
         cbAgents.Enabled = true;
      }

      private void button1_Click(object sender, EventArgs e)
      {
         lastBegin = dtpBegin.Value;
         lastEnd = dtpEnd.Value;

         DoReport();
      }

      public static String selagentid = string.Empty;
      DataSet<string, Org> orgs;
      DataSet<string, PotenzialOrg> porgs;

      void DoReport()
      {
         string filter = "";
         Division.DivisionAgent sel = cbAgents.SelectedItem as Division.DivisionAgent;
         if (sel != null)
         {
            filter = "\"userid\" in ('" + sel.agent.id + "')";
            selagentid = sel.agent.id;
         }
         // сейчас отчет работает только по агенту

         //if (rbDivision.Checked)
         //{
         //   Division sel = (Division)cbDivisions.SelectedItem;
         //   if( sel != null )
         //      filter = FmMessageHistory.UserIdIsStr(sel.GetAllAgents());
         //}

         if (filter.Length == 0)
            return;

         porgs = new DataSet<string, PotenzialOrg>(PotenzialOrg.OBJECT_NAME, false);
         porgs.Filter = filter;

         string DATA_FILTER = String.Format("\"{0}\" >= ToDate('{1:dd/MM/yyyy}') and \"{0}\" < ToDate('{2:dd/MM/yyyy}')",
            "date", dtpBegin.Value.Date, dtpEnd.Value.AddDays(1).Date);
         filter += " and " + DATA_FILTER;

         DataModule.DataProcessed += new EventHandler(DataModule_DataProcessed);
         DataModule.OnDataResponceError += new EventDataResponseError(DataModule_OnDataResponceError);
         
         List<IDataSet> updSets = new List<IDataSet>();
         dsVisits.Filter = filter;
         updSets.Add(dsVisits);

         orgs = DataModule.GetUserDataSet(selagentid, "Org", typeof(DataSet<string, Org>)) as DataSet<string, Org>;
         if (orgs.Count == 0)
            updSets.Add(orgs);

         updSets.Add(porgs);

         FmWait.ShowForm(this, DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), updSets, FmWait.ProgressIndicator));
      }

      void DataModule_OnDataResponceError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         MessageBox.Show(e.Msg, "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Stop);
      }

      void DataModule_DataProcessed(object sender, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         PhotoRateRepData data = new PhotoRateRepData(orgs, porgs);
         foreach (VisitInfo v in dsVisits.Data)
            data.Add(v);

         MakeReport(data);

         BeginInvoke(new EmptyParamHandler(Close), null);
      }

      static int doc_count;
      private void MakeReport(PhotoRateRepData data)
      {
         StringBuilder html = new StringBuilder("<html><head>" +
            "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"></head>" +
            "<body><FONT FACE=\"Arial\">");
         
         foreach (KeyValuePair<Agent, AgentRepData> kva in data)
         {
            Dictionary<DateTime, Boolean> columns = kva.Value.columns;

            StringBuilder table = new StringBuilder();
            table.Append("<table cellpadding=5 CELLSPACING=0 border=1 BORDERCOLOR=\"#000000\"><tr BGCOLOR=\"#CCCCCC\">" +
               "<td align=\"center\"><FONT SIZE=\"2\"><b>Контрагент</b></td>");
            
            
            StringBuilder sb = new StringBuilder();
            foreach(KeyValuePair<DateTime,Boolean> ci in columns)
               sb.AppendFormat("<td align=\"center\"><FONT SIZE=\"2\"><b>{0}</b></td>", ci.Key.ToString("dd.MM"));

            sb.Append("<td align=\"center\"><FONT SIZE=\"2\"><b>Оценка</b></td></tr>");

            List<OrgInfo> orgs = new List<OrgInfo>();
            double value;
            orgs.AddRange(kva.Value.Keys);
            orgs.Sort();
            
            foreach (OrgInfo oi in orgs)
            {
               OrgRepData od = kva.Value[oi];
               sb.AppendFormat("<tr><td><FONT SIZE=\"2\">{0}</td>", oi.name);
               foreach (KeyValuePair<DateTime, Boolean> ci in columns)
               {
                  value = od.Res(ci.Key, oi);
                  sb.AppendFormat("<td><FONT SIZE=\"2\">{0}</td>", double.IsNaN(value) ? string.Empty : String.Format("{0:F1}", value));
               }

               value = od.Avg;
               sb.AppendFormat("<td><FONT SIZE=\"2\">{0:F1}</td></tr>", double.IsNaN(value) ? string.Empty : String.Format("{0:F1}", value));
            }

            sb.Append("<tr><td><FONT SIZE=\"2\"><b>Средняя оценка</b></td>");

            foreach (KeyValuePair<DateTime, Boolean> ci in columns)
            {
               value = kva.Value.Avg(ci.Key);
               sb.AppendFormat("<td><FONT SIZE=\"2\">{0}</td>", double.IsNaN(value) ? string.Empty : String.Format("{0:F1}", value));
            }

            double avg = kva.Value.Avg();
            sb.AppendFormat("<td><FONT SIZE=\"2\">{0}</td></tr>", double.IsNaN(avg) ? string.Empty : String.Format("{0:F1}", avg));

            table.Append(sb.ToString());
            table.Append("</table>");

            html.Append(String.Format("<H3>Отчет по агенту {0}</H3>Средняя оценка по агенту {1:F1}<p>", kva.Key.Name, avg));
            html.Append(table);
         }

         html.Append("<br><br><FONT SIZE=\"2\"> <SUB>Построен в системе \"Наполеон\" <a href=http://grsoft.ru/>http://grsoft.ru/</a></SUB>");
         html.Append("</body></html>");

         string fileName = System.IO.Path.GetTempPath() + String.Format("\\photo-rate{0}.html", ++doc_count);
         using (StreamWriter sw = new StreamWriter(fileName))
         {
            sw.Write(html.ToString());
            sw.Flush();
         }
         OpenLink.NewWindow(String.Format("\"{0}\"", fileName));
      }
   }

   class PhotoRateRepDocData
   {
      public string orgName;
      public double rate;

      public PhotoRateRepDocData(VisitInfo v)
      {
         orgName = v.OrgName;
         rate = v.rating;
      }
   }

   class OrgInfo : IComparable<OrgInfo>
   {
      public String name;
      public String id;
      
      public OrgInfo(VisitInfo v)
      {
         id = v.id;
         name = v.OrgName;
      }

      public OrgInfo(Org o)
      {
         id = o.id;
         name = o.Name;
      }

      #region Члены IComparable<OrgInfo>

      public int CompareTo(OrgInfo other)
      {
         return name.CompareTo(other.name);
      }

      #endregion
   }

   class RepData
   {
      double rate;
      int count;

      public RepData(VisitInfo v)
      {
         count = 1;
         rate = v.rating;
      }

      public void Add(VisitInfo v)
      {
         count++;
         rate += v.rating;
      }

      public double Avg { get { return rate / count; } }
   }

   class OrgRepData : Dictionary<DateTime, RepData>
   {
      public OrgRepData(VisitInfo v)
      {
         this.Add(v.date.Date, new RepData(v));
      }

      public OrgRepData()
      {
      }

      public void Add(VisitInfo v)
      {
         DateTime date = v.date.Date;
         if (ContainsKey(date))
            this[date].Add(v);
         else
            Add(date, new RepData(v));
      }

      public double Avg
      {
         get
         {
            if( Count == 0 )
               return double.NaN;
            double res = 0;
            foreach (KeyValuePair<DateTime, RepData> v in this)
               res += v.Value.Avg;

            res /= Count;
            return res;
         }
      }   

      public double Res(DateTime dt, OrgInfo oi)
      {
         double result = double.NaN;
         Org org = new Org();
         org.id = oi.id;

         if (ContainsKey(dt))
            result = this[dt].Avg;
         else if (FmDetailBase.IsCreatedBySelectedAgentRoute(org, FmPhotoRateReport.selagentid, dt))
         {
            VisitInfo vi = new VisitInfo();
            vi.rating = 0;
            Add(dt, new RepData(vi));
            result = 0;
         }

         return result;
      }
   }

   class AgentRepData : Dictionary<OrgInfo, OrgRepData>
   {
      public class OrgInfoEquals : IEqualityComparer<OrgInfo>
      {
         #region Члены IEqualityComparer<OrgInfo>

         public bool Equals(OrgInfo x, OrgInfo y)
         {
            return x.id.Equals(y.id);
         }

         public int GetHashCode(OrgInfo obj)
         {
            return obj.id.GetHashCode();
         }

         #endregion
      }

      public Dictionary<DateTime, Boolean> columns = new Dictionary<DateTime,bool>();

      public AgentRepData(VisitInfo v, DataSet<string, Org> orgs, DataSet<string, PotenzialOrg> porgs)
         : base(new OrgInfoEquals())
      {
         foreach (Org o in orgs.Data)
            Add(new OrgInfo(o), new OrgRepData());
         foreach (Org o in porgs.Data)
            Add(new OrgInfo(o), new OrgRepData());

         Add(v);
      }

      public void Add(VisitInfo v)
      {
         DateTime d = v.date.Date;
         if (columns.ContainsKey(d) == false)
            columns.Add(d, true);

         OrgInfo oi = new OrgInfo(v);
         if (!ContainsKey(oi))
            Add(oi, new OrgRepData(v));
         else
            this[oi].Add(v);
      }

      public double Res(OrgInfo o, DateTime dt)
      {
         double res = 0;
         if( ContainsKey(o) )
            res = this[o].Res(dt, o);
         return res;
      }

      public double Avg(DateTime dt)
      {
         double res = 0;
         int count = 0;
         foreach (KeyValuePair<OrgInfo, OrgRepData> v in this)
         {
            double val = v.Value.Res(dt, v.Key);

            if (!double.IsNaN(val))
            {
               res += val;
               count++;
            }
         }

         if (count > 0)
            res /= count;
         return res;
      }

      public double Avg()
      {
         if (Count == 0)
            return double.NaN;

         double res = 0;
         int count = 0;
         foreach (KeyValuePair<OrgInfo, OrgRepData> v in this)
         {
            double val = v.Value.Avg;
            if (!double.IsNaN(val))
            {
               res += val;
               count++;
            }
         }

         if( count > 0 )
            res /= count;
         return res;

      }
   }

   class PhotoRateRepData : Dictionary<Agent, AgentRepData>
   {
      DataSet<string, Org> orgs;
      DataSet<string, PotenzialOrg> porgs;
      public PhotoRateRepData(DataSet<string, Org> orgs, DataSet<string, PotenzialOrg> porgs)
      {
         this.orgs = orgs;
         this.porgs = porgs;
      }

      public void Add(VisitInfo v)
      {
         if( v.agent == null )
            return;

         if (ContainsKey(v.agent) == false)
            Add(v.agent, new AgentRepData(v, orgs, porgs));
         else
            this[v.agent].Add(v);
      }
   }
}
