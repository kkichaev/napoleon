using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmSPKTaskRpt : Form
   {
      public FmSPKTaskRpt()
      {
         InitializeComponent();
         dpv.Start = DateTime.Now;
         dpv.Finish = DateTime.Now;
      }

      private class Param : GRSoft.Network.DataObject
      {
         public DateTime start = DateTime.MinValue;
         public DateTime finish = DateTime.MinValue;
         public string userid = string.Empty;
      }

      private void btnExcel_Click(object sender, EventArgs e)
      {
         Param arg = new Param();
         arg.start = dpv.Start.Date;
         arg.finish = dpv.Finish.Date;
         arg.userid = GetSelectedAgent();
         ReportResult.DoReport("spktaskrep", arg, this);
      }

      private string GetSelectedAgent()
      {
         string res = string.Empty;

         Agent a = cbAgents.SelectedItem as Agent;

         if (a != null)
            res = a.id;

         return res;
      }

      private void FmSPKTaskRpt_Load(object sender, EventArgs e)
      {
         Manager mc = CurrentUser.user as Manager;

         if (mc != null)
         {
            List<Agent> list = new List<Agent>(mc.GetAgents().Values);
            list.Sort((lhs, rhs) => { return lhs.Name.CompareTo(rhs.Name); });
            cbAgents.Items.AddRange(list.ToArray());

            if (cbAgents.Items.Count > 0)
               cbAgents.SelectedIndex = 0;
         }
      }
   }
}
