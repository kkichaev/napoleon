using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public partial class FmPlanEdit : Form
   {
      private static FmPlanEdit instance;
      private Agent agent;

      public FmPlanEdit()
      {
         InitializeComponent();
      }

      public static Plan ShowInstance(Plan plan)
      {
         instance = new FmPlanEdit();

         if (plan == null)
         {
            plan = new Plan();
            plan.date = DateTime.Now;
            instance.Text = "Создать";
         }
         else
         {
            instance.Text = "Изменить";

            instance.agent = plan.Agent;
            instance.dtpBegin.Value = plan.from;
            instance.dtpEnd.Value = plan.till;
            instance.tbText.Text = plan.text;
            instance.tbName.Text = plan.name;
         }

         if (instance.ShowDialog() == DialogResult.OK)
         {
            plan.agent = (Agent)instance.cbAgents.SelectedItem;
            plan.from = instance.dtpBegin.Value;
            plan.till = instance.dtpEnd.Value;
            plan.name = instance.tbName.Text;
            plan.text = instance.tbText.Text;
            Double.TryParse(instance.tbPlan.Text, out plan.plan);
               
            return plan;
         }
         else
            return null;
      }

      private void FmPlanEdit_Load(object sender, EventArgs e)
      {
         DataSet<string, Agent> dsAgent = (DataSet<string, Agent>)DataModule.Get(Agent.OBJECT_NAME);

         if (dsAgent != null)
            foreach (Agent agent in dsAgent.Data)
            {
               cbAgents.Items.Add(agent);

               if (this.agent != null &&
                     agent.Equals(this.agent))
                  this.agent = agent;
            }

         cbAgents.SelectedItem = this.agent;
      }

   }
}
