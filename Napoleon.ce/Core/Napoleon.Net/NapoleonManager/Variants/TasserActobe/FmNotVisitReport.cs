using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmNotVisitReport : Form
   {
      public class Data : Network.DataObject
      {
         public DateTime start = DateTime.Now;
         public DateTime finish = DateTime.Now;
         public string agents = string.Empty;
      }

      public FmNotVisitReport()
      {
         InitializeComponent();
      }

      private void FmWorkReport_Load(object sender, EventArgs e)
      {
         dtpBegin.Value = DateTime.Now.Date;
         dtpEnd.Value = DateTime.Now.Date;

         Manager m = CurrentUser.user as Manager;

         if (m != null)
         {
            cbDivision.Items.Add(m.Division);
            foreach (Division d in m.Childs)
               cbDivision.Items.Add(d);
         }

         if (cbDivision.Items.Count > 0)
            cbDivision.SelectedIndex = 0;
      }

      private void button1_Click(object sender, EventArgs e)
      {
         Data data = new Data();
         data.start = dtpBegin.Value.Date;
         data.finish = dtpEnd.Value.Date;
         data.agents = GetAgents();
         ReportResult.DoReport("notvisit_report", data, this);
      }

      private string GetAgents()
      {
         string res = string.Empty;

         Division d = cbDivision.SelectedItem as Division;

         if (d != null)
         {
            List<Division.DivisionAgent> list = d.GetAllAgents();

            if (list != null)
            {
               foreach (Division.DivisionAgent a in list)
               {
                  if (a != null)
                  {
                     if (res.Length > 0)
                        res += ",";

                     res += a.id;
                  }
               }
            }
         }

         return res;
      }
   }
}
