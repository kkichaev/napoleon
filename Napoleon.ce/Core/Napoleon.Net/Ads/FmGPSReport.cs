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

      private void btnReport_Click(object sender, EventArgs e)
      {
         StringBuilder filter = new StringBuilder();
         filter.Append(String.Format("\"date\" >= ToDate('{0:dd/MM/yyyy}') and \"date\" < ToDate('{1:dd/MM/yyyy}')" +
            " and \"isGSM\" = '0'",
            dtpBegin.Value.Date, dtpEnd.Value.Date.AddDays(1)));
        
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
                  path[pos.agent] += NapoleonManager.Coordutils.Distance(lat, lon, pos.latitude, pos.longitude);
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
         dtpTimeStart.Enabled = false;
         dtpTimeEnd.Enabled = false;
      }

      private void FmGsmReport_FormClosed(object sender, FormClosedEventArgs e)
      {
         instance = null;
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
