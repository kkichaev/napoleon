using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Runtime.InteropServices;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmReport : Form
   {
      [DllImport("shell32.dll")]
      static extern IntPtr ShellExecute(IntPtr hwnd, string lpOperation, string lpFile, string lpParameters, string lpDirectory, int nShowCmd);
      static int count = 1;

      public class Data : GRSoft.Network.DataObject
      {
         public DateTime start;
         public DateTime finish;
         public string userid = string.Empty;
      }

      class Result : GRSoft.Network.DataObject
      {
         public string name = "";
         public byte[] file = null;
      }

      public FmReport()
      {
         InitializeComponent();
         rbAgents.Tag = cbAgents;
         rbDivision.Tag = cbDivisions;
      }

      private void FmReport_Load(object sender, EventArgs e)
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

         if (cbAgents.Items.Count > 0)
            cbAgents.SelectedIndex = 0;

         if (cbDivisions.Items.Count > 0)
            cbDivisions.SelectedIndex = 0;
      }

      private void rbSelected(object sender, EventArgs e)
      {
         foreach (ComboBox cb in new ComboBox[] { cbAgents, cbDivisions })
            cb.Enabled = false;

         ((ComboBox)((RadioButton)sender).Tag).Enabled = true;
      }

      private string CollectAgentIds()
      {
         StringBuilder result = new StringBuilder();
         Division d = cbDivisions.SelectedItem as Division;

         if (d != null)
         {
            foreach (Division.DivisionAgent da in d.agents)
            {
               if (result.Length > 0)
                  result.Append(",");

               result.Append(da.id);
            }
         }

         return result.ToString();
      }

      private void btnExcel_Click(object sender, EventArgs e)
      {
         Data data = new Data();
         data.start = dpPeriod.Start.Date;
         data.finish = dpPeriod.Finish.Date.AddDays(1);
         
         if(rbDivision.Checked)
            data.userid = CollectAgentIds();
         else
         {
            Agent a = cbAgents.SelectedItem as Agent;

            if (a != null)
               data.userid = a.id;
         }

         if (data.userid.Length == 0)
         {
            MessageBox.Show("Возможно, пользователь не менеджер или в программе отсутствуют подразделения");
            return;
         }

         SimpleDataSet<Result> resultSet = new SimpleDataSet<Result>("Result", false);
         const string REPORT_NAME = "ord_report";
         Report r = new Report(REPORT_NAME, data, resultSet);
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(r);

         FmWait.StdDataRefresh(this, upd, () =>
         {
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
         });
      }

   }
}
