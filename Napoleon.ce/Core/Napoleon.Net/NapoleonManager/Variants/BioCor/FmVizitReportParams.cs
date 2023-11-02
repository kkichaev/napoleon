using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmVizitReportParams : Form
   {
      private VisitReport.Data data;

      public FmVizitReportParams(VisitReport.Data data)
      {
         InitializeComponent();
         this.data = data;
      }

      private void FmVisitReportParams_FormClosing(object sender, FormClosingEventArgs e)
      {
         if(DialogResult == DialogResult.OK)
         { 
            string userid = string.Empty;

            Agent a = cbAgent.SelectedItem as Agent;

            if (a != null)
               userid = a.id;

            data.userid = userid;
            data.begin = datePeriodView1.Start;
            data.end = datePeriodView1.Finish.AddDays(1);
            data.photo = cbPhoto.Checked ? 1 : 0;
         }
      }

      private void FmVisitReportParams_Load(object sender, EventArgs e)
      {
         Manager m = CurrentUser.user as Manager;

         if (m != null)
         {
            foreach (Division.DivisionAgent a in m.Division.GetAllAgents())
               if (a.agent != null)
                  cbAgent.Items.Add(a.agent);
         }

         int sel = cbAgent.Items.Count > 0 ? 0 : -1;

         if(data.userid != null && data.userid.Trim().Length > 0)
            for (int i = 0; i < cbAgent.Items.Count; i++)
            {
               Agent a = cbAgent.Items[i] as Agent;

               if (a != null && a.id.Equals(data.userid))
               {
                  sel = i;
                  break;
               }
            }

         cbAgent.SelectedIndex = sel;
         datePeriodView1.Start = data.begin;
         datePeriodView1.Finish = data.end;
         cbPhoto.Checked = data.photo != 0;
      }
   }
}
