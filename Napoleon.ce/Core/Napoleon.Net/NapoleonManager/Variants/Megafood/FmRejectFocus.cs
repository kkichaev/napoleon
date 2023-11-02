using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmRejectFocus : Form
   {
      Agent agent = null;
      public FmRejectFocus()
      {
         InitializeComponent();
      }

      public void SetData(DateTime s, DateTime e, Agent agent)
      {
         dpv.Start = s;
         dpv.Finish = e.AddDays(-1);
         this.agent = agent;
      }

      private void button1_Click(object sender, EventArgs e)
      {
         RepParam param = new RepParam();
         param.start = dpv.Start;
         param.finish = dpv.Finish;
         param.name = agent.Name;
         param.userid = agent.id;

         ReportResult.DoReport("focus_reject", param, this);
      }

      class RepParam : GRSoft.Network.DataObject
      {
         public DateTime start = DateTime.Now;
         public DateTime finish = DateTime.Now;
         public string name = "";
         public string userid = "";
      }
   }
}
