using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;
using System.IO;
using GRSoft.NapoleonManager.Utils;

namespace GRSoft.NapoleonManager.Reports.Html
{
   delegate void FetchDataCompleted(ReportData data);

   class HTMLIncassReport
   {
      private static int docNumer = 0;

      public void Build(ReportData data)
      {
         IncassReportData incassData = data as IncassReportData;

         if (incassData == null)
            return;

         incassData.DataFetchCompleted += new FetchDataCompleted(BuildCompelte);
         incassData.report = this;
         incassData.Refresh();
      }

      public void BuildCompelte(ReportData data)
      {
         IncassReportData incassData = data as IncassReportData;

         if (incassData == null)
            return;
      
         StringBuilder html = new StringBuilder("<html><head>" +
           "<meta http-equiv='content-type' content='text/html; charset=utf-8'></head>" +
           "<body><FONT FACE='Arial'>");

         const string DATA_MASK = "dd.MM.yy";
         html.Append("<H2>���� �� ����������.</H2><br>");
         html.Append("�����: ").Append(incassData.division.name).Append("<br>");
         html.Append("������: ").Append(incassData.begin.ToString(DATA_MASK)).
            Append(" - ").Append(incassData.end.ToString(DATA_MASK)).Append("<br>");

         List<Incass> incassList = new List<Incass>();
         incassList.AddRange((IEnumerable<Incass>)incassData.dsIncass.Data);

         incassList.Sort(new Comparison<Incass>(delegate(Incass x, Incass y)
         {
            int result = 0;
            result = x.agent.name.CompareTo(y.agent.name);

            if (result != 0)
               return result;

            result = x.OrgName.CompareTo(y.OrgName);

            if (result != 0)
               return result;

            result = x.date.CompareTo(y.date);

            return result;
         }));

         string agentId = string.Empty;
         double sum = 0;
         double fullSumm = 0;

         foreach (Incass incass in incassList)
         {
            if (!agentId.Equals(incass.AgentID))
            {
               if (agentId.Length != 0)
                  MakeTableFooter(html, sum);

               agentId = incass.AgentID;
               sum = 0;
               MakeTableHead(incass.agent, html);
            }

            html.Append("<tr>");
            html.Append("<td>").Append(incass.OrgName).Append("</td>");
            html.Append("<td>").Append(incass.date.ToString(DATA_MASK)).Append("</td>");
            html.Append("<td align='right'>").Append(incass.Sum().ToString()).Append("</td>");
            html.Append("</tr>");

            sum += incass.Sum();
            fullSumm += incass.Sum();
         }

         MakeTableFooter(html, sum);
         html.Append("�����: ").Append(fullSumm.ToString()).Append("<br>");

         html.Append("<FONT SIZE=\"2\"><SUB>�������� � ������� '��������' <a href=http://grsoft.ru/>http://grsoft.ru/</a></SUB></FONT></body></html>");

         string fileName = String.Format("incass_{0}.html", docNumer++);
         string path = System.IO.Path.GetTempPath() + fileName; //.GetTempFileName();
         using (StreamWriter sw = new StreamWriter(path))
         {
            sw.Write(html.ToString());
            sw.Flush();
         }

         OpenLink.NewWindow(path);
      }

      private void MakeTableFooter(StringBuilder html, double sum)
      {
         html.Append("</table><br>");
         html.Append("&nbsp;&nbsp;&nbsp;&nbsp;�����: ").Append(sum.ToString()).Append("<br>");
      }

      private void MakeTableHead(Agent agent, StringBuilder html)
      {
         html.Append("<br>");
         html.Append(agent.Name);
         html.Append("<table width='100%' cellpadding=5 CELLSPACING=0 border=1 BORDERCOLOR='#000000'>");
         html.Append("<tr BGCOLOR='#CCCCCC'>");
         html.Append("<td><font size='2'>������<font></td>");
         html.Append("<td><font size='2'>����<font></td>");
         html.Append("<td><font size='2'>�����<font></td>");
         html.Append("</tr>");
      }
   }

   class IncassReportData : ReportData
   {
      public FetchDataCompleted DataFetchCompleted;
      public DateTime begin;
      public DateTime end;
      public DataSet<int, Incass> dsIncass;
      private Form context;
      public HTMLIncassReport report;
      public Division division;

      public IncassReportData(Form context,
         DateTime dtBegin,
         DateTime dtEnd, Division division)
      {
         this.begin = dtBegin;
         this.end = dtEnd;
         this.context = context;
         this.division = division;

         dsIncass = (DataSet<int, Incass>)DataModule.Get(Incass.OBJECT_NAME) ?? 
            new DataSet<int, Incass>(Incass.OBJECT_NAME);
      }

      public void Refresh()
      {
         DataModule.SetDataRepsonceHandlers(DataLoaded, LoadError);
         dsIncass.Filter = String.Format(
            "date >= ToDate('{0:dd/MM/yyyy}') and date < ToDate('{1:dd/MM/yyyy}') and {2}",
            begin, end, DataUtils.MakeFilterFromAgents(null, division.GetAllAgents()));

         List<IDataSet> updSets = new List<IDataSet>();
         updSets.Add(dsIncass);

         DataSet<string, Org> dsOrg = (DataSet<string, Org>)DataModule.Get(Org.OBJECT_NAME);
         if (dsOrg.Data.Count == 0)
            updSets.Add(dsOrg);

         FmWait.ShowForm(context, DataModule.RefreshGiveSets(
            Config.GetConfig().GetConnection(), 
            updSets, FmWait.ProgressIndicator));
      }

      void DataLoaded(object sender, EventArgs e)
      {
         EndOfDataRecieve();

         if (report != null)
            report.BuildCompelte(this);
      }

      void LoadError(EDataResponse e)
      {
         EndOfDataRecieve();
      }

      private void EndOfDataRecieve()
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
      }

   }
}
