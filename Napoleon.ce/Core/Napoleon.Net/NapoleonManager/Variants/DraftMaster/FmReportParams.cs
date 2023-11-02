using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmReportParams : Form
   {
      public class ReportData : GRSoft.Network.DataObject
      {
         public DateTime begin = DateTime.Now;
         public DateTime end = DateTime.Now;
      }

      public ReportData data = null;

      public FmReportParams(ReportData data)
      {
         InitializeComponent();
         this.data = data;
      }

      private void FmVisitReportParams_FormClosing(object sender, FormClosingEventArgs e)
      {
         if(DialogResult == DialogResult.OK)
         { 
            data.begin = datePeriodView1.Start;
            data.end = datePeriodView1.Finish;
         }
      }

      private void FmVisitReportParams_Load(object sender, EventArgs e)
      {
         datePeriodView1.Start = data.begin;
         datePeriodView1.Finish = data.end;
      }
   }
}
