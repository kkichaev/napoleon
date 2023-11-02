using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class PlanDateDialog : Form
   {
      public PlanDateDialog()
      {
         InitializeComponent();

         dtpDate.MinDate = FmAgentPlan.MonthStart(DateTime.Now);
         dtpDate.Value = dtpDate.MinDate;
         dtpDate.MaxDate = FmAgentPlan.MonthStart(DateTime.Now.AddMonths(12));
      }

      private void button2_Click(object sender, EventArgs e)
      {

      }

      public static bool AskDate(out DateTime dt)
      {
         PlanDateDialog pdd = new PlanDateDialog();
         DialogResult r = pdd.ShowDialog();
         dt = pdd.dtpDate.Value;
         return r == DialogResult.OK;
      }

      private void button1_Click(object sender, EventArgs e)
      {
         DialogResult = DialogResult.OK;
      }
   }
}
