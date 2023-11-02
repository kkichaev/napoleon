using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Reports.Excel;
using System.Threading;
using System.Xml;
using System.Xml.Schema;
using System.Globalization;
using System.IO;
using System.Xml.Serialization;

namespace GRSoft.NapoleonManager
{
   public partial class FmGPSReport : Form
   {
      private DataSet<DateTime, GPSPos> dsGPS;
      
      enum OutType
      {
         Excel, Gpx
      }

      private OutType curOutType = OutType.Gpx;

      public FmGPSReport()
      {
         InitializeComponent();

         dsGPS = (DataSet<DateTime, GPSPos>)DataModule.Get(GPSPos.OBJECT_NAME) ??
            new DataSet<DateTime, GPSPos>(GPSPos.OBJECT_NAME);
      }

      class AllAgents : Agent
      {
         public override string ToString()
         {
            return "Все";
         }
      }

      private void FmAgentGPSReport_Load(object sender, EventArgs e)
      {
         Agents dsAgent = Agents.GetDataSet();

         if (dsAgent != null)
         {
            cbAgent.Items.Clear();
            cbAgent.Items.Add(new AllAgents());

            List<Agent> list = new List<Agent>();
            list.AddRange(dsAgent.Values);
            list.Sort(new Comparison<Agent>(delegate(Agent a1, Agent a2) { return a1.Name.CompareTo(a2.Name); }));

            cbAgent.Items.AddRange(list.ToArray());
            cbAgent.SelectedIndex = 0;
         }
      }

      private void btnExcel_Click(object sender, EventArgs e)
      {
         curOutType = OutType.Excel;
         UpdateDataSet();
      }

      private void UpdateDataSet()
      {
         Agent agent = (Agent)cbAgent.SelectedItem;

         if (agent != null)
         {
            dsGPS.Filter = String.Format("date >= ToDate('{0}') and date < ToDate('{1}')",
               dtpFrom.Value.Date.Date, dtpTill.Value.Date.Date.AddDays(1));

            if (!(agent is AllAgents))
               dsGPS.Filter += String.Format(" AND userid='{0}'", agent.id);

            List<IDataSet> list = new List<IDataSet>();
            list.Add(dsGPS);

            DataModule.SetDataRepsonceHandlers(DataModule_DataProcessed, DataModule_OnDataResponceError);

            FmWait.ShowForm(this,
               DataModule.RefreshGiveSets(
               Config.GetConfig().GetConnection(), list, FmWait.ProgressIndicator)
            );
         }
      }

      void DataModule_DataProcessed(object sender, EventArgs e)
      {
         FmWait.CloseForm();
         DataModule.ClearEvents();
         Invoke(new InvokeDelegate(RefreshData));
      }

      void DataModule_OnDataResponceError(EDataResponse e)
      {
         FmWait.CloseForm();
         DataModule.ClearEvents();
         MessageBox.Show(e.Msg);
      }

      void RefreshData()
      {
         List<GPSPos> list = new List<GPSPos>();
         list.AddRange(dsGPS.Values);
         list.Sort(new Comparison<GPSPos>(delegate(GPSPos p1, GPSPos p2)
            {
               int result = 0;

               if (p1.agent != null && p2.agent != null)
                  result = p1.agent.Name.CompareTo(p2.agent.Name);

               if (result == 0 && p1.date != null && p2.date != null)
                  result = p1.date.CompareTo(p2.date);

               return result;
            }
         ));

         if (curOutType == OutType.Excel)
         {
            if (dsGPS.Count < Excel.MAX_ROWS_V2003)
            {
               new Thread(new ParameterizedThreadStart(delegate(object o)
               {
                  BeginInvoke(new EmptyParamHandler(delegate() { FmWait.ShowForm(this, true); }));
                  GPSReport rpt = new GPSReport();
                  rpt.Build(list);
                  rpt.Show();
                  rpt.Dispose();
                  BeginInvoke(new EmptyParamHandler(delegate() { FmWait.CloseForm(); }));
               })).Start();


            }
            else
               MessageBox.Show("Слишком много данных для формирования отчета, уменьшите параметры выборки", "Ошибка",
                  MessageBoxButtons.OK, MessageBoxIcon.Error);
         }
         else
         {
            FolderBrowserDialog d = new FolderBrowserDialog();
            FmGPSReportSetting setting = FmGPSReportSetting.GetSetting(); 
            string path = setting.gpxfolder;
            d.SelectedPath = path;

            if (d.ShowDialog() == DialogResult.OK)
            {
               if (d.SelectedPath != path)
               {
                  setting.gpxfolder = d.SelectedPath;
                  setting.Save();
               }

               new Thread(new ParameterizedThreadStart(delegate(object o)
               {
                  BeginInvoke(new EmptyParamHandler(delegate() { FmWait.ShowForm(this, true); }));
                  Agent a = null;
                  List<GPSPos>.Enumerator iter = list.GetEnumerator();

                  while (iter.MoveNext())
                  {
                     a = iter.Current.agent;

                     if (a != null)
                     {
                        XmlDocument doc = new XmlDocument();
                        const string namespaceURI = @"http://www.topografix.com/GPX/1/1";

                        XmlElement gpx = (XmlElement)doc.AppendChild(doc.CreateElement("gpx", namespaceURI));
                        gpx.SetAttribute("version", "1.0");
                        gpx.SetAttribute("creator", "The Guild of Developers");
                        gpx.SetAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
                        gpx.SetAttribute("xsi:schemaLocation", "http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd");

                        NumberFormatInfo nfi = new CultureInfo("en-US", false).NumberFormat;
                        
                        XmlElement trk = (XmlElement)gpx.AppendChild(doc.CreateElement("trk", namespaceURI));
                        XmlElement name = (XmlElement)trk.AppendChild(doc.CreateElement("name", namespaceURI));
                        name.InnerText = a.Name;
                        XmlElement trkseg = (XmlElement)trk.AppendChild(doc.CreateElement("trkseg", namespaceURI));

                        while (iter.Current.agent == a)
                        {
                           XmlElement trkpt = (XmlElement)trkseg.AppendChild(doc.CreateElement("trkpt", namespaceURI));
                           trkpt.SetAttribute("lat", iter.Current.latitude.ToString(nfi));
                           trkpt.SetAttribute("lon", iter.Current.longitude.ToString(nfi));
                           trkpt.AppendChild(doc.CreateElement("time", namespaceURI)).InnerText = iter.Current.date.ToString("yyyy-MM-ddThh:mm:ssZ");

                           if (!iter.MoveNext())
                              break;
                        }

                        XmlWriter writer = XmlWriter.Create(d.SelectedPath + String.Format(@"\{0}.gpx", a.Name));
                        gpx.WriteTo(writer);
                        writer.Close();
                     }
                  }
                  BeginInvoke(new EmptyParamHandler(delegate() { FmWait.CloseForm(); }));
               })).Start();
            }
         }
      }

      private void btnGpx_Click(object sender, EventArgs e)
      {
         curOutType = OutType.Gpx;
         UpdateDataSet();
      }
   }

   class GPSReport : Excel
   {
      public void Build(List<GPSPos> gps)
      {
         SetValue(1, 1, "ТП");
         SetValue(1, 2, "Дата");
         SetValue(1, 3, "Широта");
         SetValue(1, 4, "Долгота");

         int row = 2;

         foreach (GPSPos pos in gps)
         {
            if (pos.agent != null && pos.date != null)
            {
               SetValue(row, 1, pos.agent.Name);
               SetValue(row, 2, pos.date.ToString());
               SetValue(row, 3, pos.latitude);
               SetValue(row, 4, pos.longitude);

               row++;
            }
         }
      }

      public void Show()
      {
         Visible = true;
      }
   }

   public class FmGPSReportSetting
   {
      static readonly string FILE_NAME = "FmGPSReportSetting.cfg";
      public string gpxfolder = string.Empty;
      private static FmGPSReportSetting instance = null;

      public static FmGPSReportSetting GetSetting()
      {
         if (instance == null)
         {
            instance = new FmGPSReportSetting();
            instance.Load();
         }

         return instance;
      }

      public void Load()
      {
         string v = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData);
         v += Config.FOLDER + FILE_NAME;
         Load(v);
      }

      public bool Save()
      {
         string v = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData);
         v += Config.FOLDER;
         Directory.CreateDirectory(v);
         return Save(v + FILE_NAME);
      }

      public bool Save(string fileName)
      {
         XmlSerializer s = new XmlSerializer(typeof(FmGPSReportSetting));
         bool ret = true;
         try
         {
            using (TextWriter w = new StreamWriter(fileName))
            {
               s.Serialize(w, this);
               w.Close();
            }
         }
         catch (Exception)
         {
            ret = false;
         }

         return ret;
      }

      public void Load(string fileName)
      {
         if (File.Exists(fileName))
         {
            XmlSerializer s = new XmlSerializer(typeof(Config));
            using (FileStream fs = new FileStream(fileName, FileMode.Open, FileAccess.Read))
            {
               try {instance = (FmGPSReportSetting)s.Deserialize(fs); }
               catch { }
            }
         }
      }
   }
}
