using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.NapoleonManager.Reports.Excel;
using GRSoft.Network;
using System.IO;
using System.Threading;
using System.Runtime.InteropServices;
using System.Collections;
using System.Reflection;

namespace GRSoft.NapoleonManager
{
   public partial class FmReportPresent : Form
   {
      [DllImport("shell32.dll")]
      static extern IntPtr ShellExecute(IntPtr hwnd, string lpOperation, string lpFile, string lpParameters, string lpDirectory, int nShowCmd); 

      DataSet<string, Price> dsPrice;
      DataSet<int, Folder> dsFolder;
      DataSet<int, OrgRemnants> dsRemnants;
      DataSet<string, Org> dsOrg;
      DataSet<int, Visit> dsVisit;
      DataSet<string, OrgType> dsOrgType;
      DataSet<string, Dealer> dsDealer;
      protected const string COMMON_FILTER_STR = "\"created\" >= ToDate('{0:dd/MM/yyyy}') and \"created\" < ToDate('{1:dd/MM/yyyy} 23:59:59') and \"userid\" in({2})";
      static int count = 1;

      private const int AGENT_RPT = 1;
      private const int DIVISION_RPT = 1;
      private const int ORG_RPT = 1;

      public FmReportPresent()
      {
         InitializeComponent();
         dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ?? 
            new DataSet<string, Price>(Price.OBJECT_NAME);
         dsFolder = (DataSet<int, Folder>)DataModule.Get(Folder.OBJECT_NAME) ?? 
            new DataSet<int, Folder>(Folder.OBJECT_NAME);
         dsRemnants = (DataSet<int, OrgRemnants>)DataModule.Get(OrgRemnants.OBJECT_NAME) ?? 
            new DataSet<int, OrgRemnants>(OrgRemnants.OBJECT_NAME);
         dsVisit = (DataSet<int, Visit>)DataModule.Get(Visit.OBJECT_NAME) ?? 
            new DataSet<int, Visit>(Visit.OBJECT_NAME);
         dsOrg = (DataSet<string, Org>) DataModule.Get(Org.COMMON_OBJECT_NAME) ??
            new DataSet<string, Org>(Org.COMMON_OBJECT_NAME);
         dsOrgType = (DataSet<string, OrgType>)DataModule.Get(OrgType.OBJECT_NAME) ??
            new DataSet<string, OrgType>(OrgType.OBJECT_NAME);
         dsDealer = (DataSet<string, Dealer>)DataModule.Get(Dealer.OBJECT_NAME) ??
            new DataSet<string, Dealer>(Dealer.OBJECT_NAME);
      }

      class Result : GRSoft.Network.DataObject
      {
         public string name = "";
         public byte[] file = null;
      }

      public class Data : GRSoft.Network.DataObject
      {
         public DateTime start;
         public DateTime finish;
         public string filter = string.Empty;
         public int division = -1;
         public string agent = string.Empty;
      }

      private void button1_Click(object sender, EventArgs e)
      {
         const string REPORT_NAME = "present_report";

         Data data = new Data();

         if (rbDivision.Checked)
            data.division = ((Division)cbDivisions.SelectedItem).id;

         if(rbAgents.Checked)
            data.agent = ((Agent)cbAgents.SelectedItem).id;

         data.start = dtpStart.Value.Date;
         data.finish = dtpFinish.Value.Date;
         data.filter = tbName.Text.Trim();

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

      private String Quotes(String str)
      {
         StringBuilder result = new StringBuilder();

         result.Append("'").Append(str).Append("'");

         return result.ToString();
      }

      //Окончание выборки, заполняются внутренние наборы
      void DataProcessed(System.Object setnder, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         Invoke(new EmptyParamHandler(delegate()
         {
            
         }));
      }

      //Произошла ошибка в соединении
      private void DataConnectionError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         Invoke(new EmptyParamHandler(delegate
         {
            const string TITLE = "Ошибка";

            MessageBox.Show(e.Msg, TITLE, MessageBoxButtons.OK,
               MessageBoxIcon.Error);
         }));
      }

      private void FmReportPresent_Load(object sender, EventArgs e)
      {
         Manager m = CurrentUser.user as Manager;
         if (m != null)
         {
            foreach (Division.DivisionAgent a in m.Division.GetAllAgents())
               if (a.agent != null && cbAgents.Items.Contains(a.agent) == false)
                  cbAgents.Items.Add(a.agent);

            cbDivisions.Items.Add(m.Division);
            foreach (Division d in m.Childs)
               cbDivisions.Items.Add(d);
         }
      }

      private void rbDivision_CheckedChanged(object sender, EventArgs e)
      {
         cbAgents.Enabled = rbAgents.Checked;
         cbDivisions.Enabled = rbDivision.Checked;
      }
   }
}
