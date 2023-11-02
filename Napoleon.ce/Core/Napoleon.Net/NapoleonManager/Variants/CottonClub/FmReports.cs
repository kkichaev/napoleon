using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Runtime.InteropServices;
using GRSoft.Network;
using System.Threading;
using System.IO;

namespace GRSoft.NapoleonManager
{
   public partial class FmReports : Form
   {

      [DllImport("shell32.dll")]
      static extern IntPtr ShellExecute(IntPtr hwnd, string lpOperation, string lpFile, string lpParameters, string lpDirectory, int nShowCmd);
      static int count = 1;

      public FmReports()
      {
         InitializeComponent();
      }

      public class Data : GRSoft.Network.DataObject
      {
         public bool city = false;
         public bool retailer = false;
         public DateTime start = DateTime.MinValue;
         public DateTime finish = DateTime.MinValue;
      }

      class Result : GRSoft.Network.DataObject
      {
         public string name = "";
         public byte[] file = null;
      }

      private void btnDistrib_Click(object sender, EventArgs e)
      {
         doReport("distrib");
      }

      private void doReport(string REPORT_NAME)
      {
         Data data = new Data();
         data.start = dtpStart.Value.Date;
         data.finish = dtpFinish.Value.Date;
         data.city = cbCities.Checked;
         data.retailer = cbRetailer.Checked;

         Result result = new Result();
         SimpleDataSet<Result> resultSet = new SimpleDataSet<Result>("Result", false);
         Report r = new Report(REPORT_NAME, data, resultSet);

         Thread th = DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), r, FmWait.ProgressIndicator);
         FmWait.ShowForm(this, th);
         th.Join();
         FmWait.CloseForm();

         if (resultSet.Count > 0)
         {
            Result res = resultSet[0];
            if (res.file.Length > 0)
            {
               string fileName = Path.GetTempPath() + "\\" + REPORT_NAME + count.ToString() + ".xlsx";
               while (File.Exists(fileName))
               {
                  count++;
                  fileName = Path.GetTempPath() + "\\" + REPORT_NAME + count.ToString() + ".xlsx";
               }
               File.WriteAllBytes(fileName, res.file);
               ShellExecute(IntPtr.Zero, "open", fileName, "", "", 1);
            }
         }
         else
            MessageBox.Show("Ошибка построения отчета");
      }

      private void btnMarketEnergy_Click(object sender, EventArgs e)
      {
         doReport("marketenergy");
      }

      private void btnMarketEnergyDetail_Click(object sender, EventArgs e)
      {
         doReport("marketenergydetail");
      }

      class ListReportParams : Network.DataObject
      {
         public string command = string.Empty;
      }

      class ModuleInfoType : Network.DataObject
      {
         public static readonly string OBJECT_NAME = "ModuleInfoType"; 
         public string name = string.Empty;
         public string kind = string.Empty;
         public string flavor = string.Empty;
         public string description = string.Empty;

         [ItemType(typeof(ModuleInfoTypeItem))]
         public List<ModuleInfoTypeItem> parameters = null;

         public override string ToString()
         {
            return description;
         }
      }

      class ModuleInfoTypeItem : Network.DataObject
      {
         public string name = string.Empty;
         public int type  = 0;
         public int flags = 0;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         lbReports.Items.Clear();
         ListReportParams prms = new ListReportParams();
         prms.command = "list";

         Result result = new Result();
         SimpleDataSet<ModuleInfoType> resultSet = new SimpleDataSet<ModuleInfoType>(ModuleInfoType.OBJECT_NAME, false);
         Report r = new Report("grsoft_reporter", prms, resultSet);

         Thread th = DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), r, FmWait.ProgressIndicator);
         FmWait.ShowForm(this, th);
         th.Join();
         FmWait.CloseForm();

         if (resultSet.Count > 0)
         {
            btnReport.Enabled = true;
            foreach (ModuleInfoType mit in resultSet.Values)
               lbReports.Items.Add(mit);

            if (lbReports.Items.Count > 0)
               lbReports.SelectedIndex = 0;
         }
      }

      private void btnReport_Click(object sender, EventArgs e)
      {
         if(lbReports.SelectedIndex != -1)
            doReport((((ModuleInfoType)lbReports.SelectedItem).name));
      }

      private void FmReports_Load(object sender, EventArgs e)
      {
         btnReport.Enabled = false;
      }
   }
}
