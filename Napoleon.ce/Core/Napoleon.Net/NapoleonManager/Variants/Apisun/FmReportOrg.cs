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
   public partial class FmReportOrg : Form
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

      public FmReportOrg()
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

         rbAgent.Tag = AGENT_RPT;
         rbDivision.Tag = DIVISION_RPT;
      }

      class Result : GRSoft.Network.DataObject
      {
         public string name = "";
         public byte[] file = null;
      }

      public class Data : GRSoft.Network.DataObject
      {
         public string divid = "";
         public string userid = "";
      }

      private void button1_Click(object sender, EventArgs e)
      {
         const string REPORT_NAME = "org_report";

         Data data = new Data();
         data.divid = GetSelelectedId(rbDivision, cbDivision, typeof(Division));
         data.userid = GetSelelectedId(rbAgent, cbAgent, typeof(Agent));

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

      private string GetSelelectedId(RadioButton rb, ComboBox cb, Type data)
      {
         string result = "";
         const string FIELD_NAME = "id";

         if (rb.Checked && cb.SelectedIndex >= 0)
         {
            FieldInfo info =  data.GetField(FIELD_NAME, BindingFlags.Instance | BindingFlags.Public);
            result = info.GetValue(cb.SelectedItem).ToString();
         }

         return result;
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

      private void FmReport_Load(object sender, EventArgs e)
      {
         List<Org> list = new List<Org>();

         foreach(Org org in dsOrg.Data)
            list.Add(org);

         Manager m = CurrentUser.user as Manager;
         if (m != null)
         {
            List<Agent> agentlist = new List<Agent>();

            foreach (Division.DivisionAgent a in m.Division.GetAllAgents())
               if (a.agent != null && cbAgent.Items.Contains(a.agent) == false)
                  agentlist.Add(a.agent);

            agentlist.Sort(new Comparison<Agent>(delegate(Agent lhs, Agent rhs) { return lhs.name.CompareTo(rhs.name); }));
            cbAgent.Items.AddRange(agentlist.ToArray());

            if (cbAgent.Items.Count > 0)
               cbAgent.SelectedIndex = 0;

            List<Division> divlist = new List<Division>();
            divlist.Add(m.Division);
            
            foreach (Division d in m.Childs)
               divlist.Add(d);

            divlist.Sort(new Comparison<Division>(delegate(Division lhs, Division rhs) { return lhs.name.CompareTo(rhs.name); }));
            cbDivision.Items.AddRange(divlist.ToArray());

            if (cbDivision.Items.Count > 0)
               cbDivision.SelectedIndex = 0;
         }
      }
      
   }
}
