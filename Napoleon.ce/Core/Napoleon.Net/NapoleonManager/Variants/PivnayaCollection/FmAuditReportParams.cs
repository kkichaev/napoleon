using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmAuditReportParams : Form
   {
      public class AuditReportData : GRSoft.NapoleonManager.FmReportParams.ReportData
      {
         public string userid = string.Empty;
      }

      public AuditReportData data = null;

      public FmAuditReportParams(AuditReportData data)
      {
         InitializeComponent();
         this.data = data;
      }

      private void FmAuditReportParams_FormClosing(object sender, FormClosingEventArgs e)
      {
         if(DialogResult == DialogResult.OK)
         { 
            data.begin = datePeriodView1.Start;
            data.end = datePeriodView1.Finish;

            Agent a = cbAgents.SelectedItem as Agent;

            if(a != null)
               data.userid = a.id;
         }
      }

      private void FmAuditReportParams_Load(object sender, EventArgs e)
      {
         datePeriodView1.Start = data.begin;
         datePeriodView1.Finish = data.end;

         Manager mc = CurrentUser.user as Manager;
         if (mc != null)
         {
            object sa = null;
            foreach (Division.DivisionAgent da in mc.Division.GetAllAgents())
            {
               if (da.agent == null)
                  continue;

               cbAgents.Items.Add(da.agent);

               if (da.agent.id.Equals(data.userid))
                  sa = da.agent;
            }

            if (sa != null)
               cbAgents.SelectedItem = sa;
         }
      }
   }
}
