using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmDaliyPlanReport : Form
   {
      public FmDaliyPlanReport()
      {
         InitializeComponent();
         
         dtpBegin.Value = DateTime.Now;

      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);

         Manager m = CurrentUser.user as Manager;
         if (m != null)
         {
            foreach (Division.DivisionAgent a in m.Division.GetAllAgents())
               if (a.agent != null && cbAgents.Items.Contains(a.agent) == false)
                  cbAgents.Items.Add(a.agent);

            cbDivisions.Items.Add(m.Division);
            foreach (Division d in m.Childs)
               cbDivisions.Items.Add(d);
         }

         if (cbAgents.Items.Count > 0)
            cbAgents.SelectedIndex = 0;

         if (cbDivisions.Items.Count > 0)
            cbDivisions.SelectedIndex = 0;
      }

      private void RadioButtonSelect(object sender, EventArgs e)
      {
         Control[] ctrl = new Control[] { cbDivisions, cbAgents };
         RadioButton rb = sender as RadioButton;

         if (rb != null)
         {
            int idx = int.Parse(rb.Tag.ToString());
            ctrl[idx].Enabled = rb.Checked;
         }
      }


      private void button1_Click(object sender, EventArgs e)
      {
         Params data = new Params();
         data.date = dtpBegin.Value;

         if (rbAll.Checked)
         {
            Manager m = CurrentUser.user as Manager;
            foreach (Division.DivisionAgent a in m.Division.GetAllAgents())
               data.items.Add(new Params.Item(a.id));
         }
         else if (rbDivision.Checked)
         {
            Division d = cbDivisions.SelectedItem as Division;
            foreach (Division.DivisionAgent a in d.GetAllAgents())
               data.items.Add(new Params.Item(a.id));
         }
         else
         {
            Agent a = cbAgents.SelectedItem as Agent;
            data.items.Add(new Params.Item(a.id));
         }

         ReportResult.DoReport("daily_plan", data, this);
      }

      public class Params : GRSoft.Network.DataObject
      {
         public DateTime date;
         public List<Item> items = new List<Item>();

         public class Item : GRSoft.Network.DataObject
         {
            public string id = "";
            public Item() { }
            public Item(string id)
            {
               this.id = id;
            }
         }
      }
   }
}
