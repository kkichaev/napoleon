using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.IO;

namespace GRSoft.Ads
{
   public partial class FmGsmReport : Form
   {
      private static FmGsmReport instance;
      private DsWorkDay dsWorkDay;
      private int doc_number;

      public FmGsmReport()
      {
         InitializeComponent();

         dsWorkDay = (DsWorkDay)DataModule.Get(WorkDay.OBJECT_NAME) ?? new DsWorkDay(true);
      }

      private void btnReport_Click(object sender, EventArgs e)
      {
         Brigade brigade = (Brigade)cbBrigade.SelectedItem;

         if (brigade != null)
         {
            dsWorkDay.Filter = String.Format("[date] >= ToDate('{0}') and [date] < ToDate('{1}') and userid='{2}'", 
               dtpBegin.Value.Date, dtpEnd.Value.Date.AddDays(1), brigade.id);

            List<IDataSet> list = new List<IDataSet>();
            list.Add(dsWorkDay);

            DataModule.SetDataRepsonceHandlers(DataModule_DataProcessed, DataModule_OnDataResponceError);
            FmWait.ShowForm(this,
                  DataModule.RefreshGiveSets(
                     Config.GetConfig().GetConnection(), 
                     list, FmWait.ProgressIndicator)
               );
         }
      }

      void DataModule_DataProcessed(object sender, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         Invoke(new InvokeDelegate(MakeReport));
      }

      public void MakeReport()
      {
         string fileName = String.Format("gsmreport{0}.html", ++doc_number);
         string result = System.IO.Path.GetTempPath() + fileName;

         string html = "<html><head> " +
           "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">" +
           "</head><body>" +
              "<FONT FACE=\"Arial\">" +
              "<table cellpadding=5 CELLSPACING=0 border=1 BORDERCOLOR=\"#000000\">";
         html += String.Format("<H3>Отчет по ГСМ бригада: {0} </H3><p>", (Brigade)cbBrigade.SelectedItem);
         html += String.Format("<FONT SIZE=\"2\">Период: <b>{0} - {1}</b><br><br>",
            dtpBegin.Value.Date.ToString("dd.MM.yyyy"), dtpEnd.Value.Date.ToString("dd.MM.yyyy"));

         html += String.Format("<tr BGCOLOR=\"#CCCCCC\" ><td><FONT SIZE=\"2\"><b>{0}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{1}</b></td><td>" +
            "<FONT SIZE=\"2\"><b>{2}</b></td></tr>",
            "Дата", "Время", "Пробег");

         int sumDistance = 0;
         foreach (WorkDay workDay in dsWorkDay.Data)
         {
            html += string.Format("<tr><td>{0}</td><td>{1} - {2}</td><td>{3}</td></tr>",
               workDay.date.Date.ToShortDateString(), workDay.Begin, workDay.End, DistanceHuman(workDay.distance));

            sumDistance += workDay.distance;
         }

         html += "</table>";
         html += "Итого:" + DistanceHuman(sumDistance) + "<br>";
         html += "<SUB>Построен в системе \"Наполеон\" <a href=http://grsoft.ru/>http://grsoft.ru/</a></SUB>";
         html += "</body></html>";

         StreamWriter sw = new StreamWriter(result);
         sw.Write(html);
         sw.Flush();

         OpenLink.NewWindow(String.Format("\"{0}\"",result));
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
            instance = new FmGsmReport();
            instance.Show();
         }
         else
            instance.Activate();
      }

      private void FmGsmReport_Load(object sender, EventArgs e)
      {
         dtpBegin.Value = DateTime.Now;
         dtpEnd.Value = DateTime.Now;

         DsBrigade dsBrigade = (DsBrigade)DataModule.Get(Brigade.OBJECT_NAME);
            
         if (dsBrigade != null)
            foreach (Brigade brigade in dsBrigade.Data)
               cbBrigade.Items.Add(brigade);
      }

      private void FmGsmReport_FormClosed(object sender, FormClosedEventArgs e)
      {
         instance = null;
      }
   }
}
