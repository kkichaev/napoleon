using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class AgentSalesPlanEdit : Form
   {
      AgentPlanData plan = new AgentPlanData();
      AgentPlanData checkPlan;
      List<AgentPlanData> plans;

      public AgentSalesPlanEdit()
      {
         InitializeComponent();
      }

      protected override void OnValidating(CancelEventArgs e)
      {
         base.OnValidating(e);

         DateTime startDate = dtpStart.Value.Date;
         foreach (AgentPlanData pi in plans)
         {
            if (pi.dateStart.Equals(startDate) && pi != checkPlan)
            {
               MessageBox.Show("У агента уже есть план на эту дату", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Stop);
               e.Cancel = true;
               return;
            }
         }
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         plan.dateStart = dtpStart.Value.Date;
         plan.dateEnd = dtpEnd.Value.Date;
         plan.name = tbName.Text;

         base.OnClosing(e);
      }

      public List<AgentPlanData> Plans { set { plans = value; } }

      public AgentPlanData Plan
      {
         get { return plan; }
         set
         {
            checkPlan = value;
            plan = value.Copy();

            lblAgent.Text = (plan.agent == null) ? plan.userid : plan.agent.Name;
            tbName.Text = plan.name;
            dtpStart.Value = plan.dateStart;
            dtpEnd.Value = plan.dateEnd;
         }
      }
   }
}
