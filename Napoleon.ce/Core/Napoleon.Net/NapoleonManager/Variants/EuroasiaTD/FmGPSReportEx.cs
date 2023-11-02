using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class FmGPSReportEx : FmGPSReport
   {
      public FmGPSReportEx()
      {
         btnReport.Text = "Excel";
         btnReport.Click -= btnReport_Click;
         btnReport.Click += DoReport;
      }

      private void DoReport(object sender, EventArgs e)
      {
         Data data = new Data();
         data.start = dtpBegin.Value.Date;
         data.finish = dtpEnd.Value.Date;
         data.userid = CollectUserIDS();
         data.gsm = cbGSM.Checked ? 1 : 0;

         if (cbTime.Checked)
         {
            data.start += dtpTimeStart.Value.TimeOfDay;
            data.finish += dtpTimeEnd.Value.TimeOfDay;
         }

         ReportResult.DoReport("routelist_report", data, this);
      }

      private string CollectUserIDS()
      {
         string result = string.Empty;

         if (rbAgent.Checked && cbAgent.SelectedItem != null)
         {
            Agent agent = cbAgent.SelectedItem as Agent;

            if (agent != null)
               result = agent.id;
         }
         else if (rbDivision.Checked && cbDivision.SelectedItem != null)
         {
            Division division = cbDivision.SelectedItem as Division;

            if (division != null)
            {
               List<GRSoft.NapoleonManager.Division.DivisionAgent> agents = division.GetAllAgents();

               foreach (GRSoft.NapoleonManager.Division.DivisionAgent a in agents)
               {
                  if (result.Length > 0)
                     result += ",";

                  result += a.id;
               }
            }
         }

         return result;
      }

      class Data : GRSoft.Network.DataObject
      {
         public DateTime start = DateTime.MinValue;
         public DateTime finish = DateTime.MinValue;
         public string userid = string.Empty;
         public int gsm = 0;
      }
   }
}
