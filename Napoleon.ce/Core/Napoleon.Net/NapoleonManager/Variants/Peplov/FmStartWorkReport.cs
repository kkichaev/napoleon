using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmStartWorkReport : Form
   {
      public static readonly string REPORT_NAME = "visit_report";

      public FmStartWorkReport()
      {
         InitializeComponent();
      }

      private void FmStartWorkReport_Load(object sender, EventArgs e)
      {
         date.Value = DateTime.Now.AddDays(-1);
      }

      public class Data : GRSoft.Network.DataObject
      {
         public string userids = string.Empty;
         public DateTime date = DateTime.MinValue;
      }

      private void btnReport_Click(object sender, EventArgs e)
      {
         Data data = new Data();

         Manager mc = CurrentUser.user as Manager;
         if (mc != null)
         {
            string userids = CollectUserids(mc.Division.GetAllAgents());

            data.date = date.Value.Date;
            data.userids = userids;

            ReportResult.DoReport(REPORT_NAME, data, this);
         }
      }

      public static string CollectUserids(List<GRSoft.NapoleonManager.Division.DivisionAgent> list)
      {
         string userids = string.Empty;
         List<Agent> al = new List<Agent>();
         foreach (Division.DivisionAgent da in list)
         {
            if (da.agent == null)
               continue;

            al.Add(da.agent);
         }

         const string AGENT_SEPARATOR = ",";
         foreach (Agent a in al)
         {
            if (userids.Length > 0)
               userids += AGENT_SEPARATOR;

            userids += a.id;
         }
         return userids;
      }
   }
}
