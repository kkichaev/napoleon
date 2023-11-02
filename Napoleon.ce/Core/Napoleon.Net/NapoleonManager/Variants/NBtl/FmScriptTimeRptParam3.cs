using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Reports.Excel;

namespace GRSoft.NapoleonManager
{
   public partial class FmScriptTimeRptParam3 : Form
   {
      private DataSet<string, ContractDef> dsContract;
      public delegate void RequstReport(FmScriptTimeRptParam3 arg);

      public FmScriptTimeRptParam3()
      {
         InitializeComponent();

         dsContract = (DataSet<string, ContractDef>)DataModule.Get(ContractDef.OBJECT_NAME) ?? new DataSet<string, ContractDef>(ContractDef.OBJECT_NAME);

         Manager m = CurrentUser.user as Manager;
         if (m != null)
         {
            List<Agent> list = new List<Agent>();

            foreach (Division.DivisionAgent a in m.Division.GetAllAgents())
               if (a.agent != null && cbAgents.Items.Contains(a.agent) == false)
                  list.Add(a.agent);

            list.Sort(new Comparison<Agent>(delegate (Agent lhs, Agent rhs) {return lhs.name.CompareTo(rhs.name);}));
            foreach(Agent a in list)
               cbAgents.Items.Add(a);
         }

         if (cbAgents.Items.Count > 0)
            cbAgents.SelectedIndex = 0;

         cbAgents.Enabled = true;
      }

      public DateTime Start { get { return dtpStart.Value.Date; } set { dtpStart.Value = value; } }
      public DateTime Finish { get { return dtpFinish.Value.Date.AddDays(1); } set { dtpFinish.Value = value; } }
      public string UserIDS { get { return cbAgents.SelectedItem as Agent != null ? "'" + ((Agent)cbAgents.SelectedItem).id + "'" : string.Empty; } }
      public RequstReport DoReport { get; set; }
      public ContractDef CID { get { return cbContract.SelectedItem as ContractDef; } }
      

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         RefreshSync();
      }

      private void RefreshSync()
      {
         const string CONTRACT_FILTER = "\"start\" <= ToDate('{0:dd/MM/yyyy}') and \"finish\" >= ToDate('{1:dd/MM/yyyy}')";
         dsContract.Filter = string.Format(CONTRACT_FILTER, Finish, Start);

         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsContract);

         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      private void DoLoadData()
      {
         cbContract.Items.Clear();

         foreach (ContractDef c in dsContract.Data)
            cbContract.Items.Add(c);

         if (cbContract.Items.Count > 0)
            cbContract.SelectedIndex = 0;
      }

      private void FmScriptTimeRptParam3_Load(object sender, EventArgs e)
      {
         RefreshSync();
      }

      private void btnExcel_Click(object sender, EventArgs e)
      {
         if (CID != null && DoReport != null)
            DoReport(this);
      }
   }
}
