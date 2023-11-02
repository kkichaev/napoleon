using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmTaskReport : Form
   {
      public FmTaskReport()
      {
         InitializeComponent();

         dpv.Start = DateTime.Now;
         dpv.Finish = DateTime.Now;

         Manager m = CurrentUser.user as Manager;
         if (m != null)
         {
            foreach (Division.DivisionAgent a in m.Division.GetAllAgents())
               if (a.agent != null && cbAgents.Items.Contains(a.agent) == false)
                  cbAgents.Items.Add(a.agent);

            if (cbAgents.Items.Count > 0)
               cbAgents.SelectedIndex = 0;

            cbDivisions.Items.Add(m.Division);
            foreach (Division d in m.Childs)
               cbDivisions.Items.Add(d);

            if (cbDivisions.Items.Count > 0)
               cbDivisions.SelectedIndex = 0;
         }

         rbDivision_Click(rbDivision, EventArgs.Empty);
      }

      private void rbDivision_Click(object sender, EventArgs e)
      {
         cbAgents.Enabled = false;
         cbDivisions.Enabled = true;
      }

      private void rbAgents_Click(object sender, EventArgs e)
      {
         cbAgents.Enabled = true;
         cbDivisions.Enabled = false;
      }

      private void button1_Click(object sender, EventArgs e)
      {
         Param data = new Param();
         data.start = dpv.Start.Date;
         data.finish = dpv.Finish.Date.AddDays(1);
         if (cbAgents.Enabled)
         {
            Agent sel = cbAgents.SelectedItem as Agent;
            if (sel != null)
               data.agents.Add(new Param.Item(sel.id));
         }
         else
         {
            Division d = cbDivisions.SelectedItem as Division;
            if(d != null)
               foreach (Division.DivisionAgent da in d.GetAllAgents())
               {
                  data.agents.Add(new Param.Item(da.id));
               }
         }
         data.mode = "reportXLS";

         ReportResult.DoReport("orgtask", data, this);
      }

      class Param : GRSoft.Network.DataObject
      {
         public String mode = "";
         public DateTime start = DateTime.Now,
            finish = DateTime.Now;

         public class Item : GRSoft.Network.DataObject
         {
            public String id = "";

            public Item(String id) { this.id = id; }
            public Item() { }
         }
         public List<Item> agents = new List<Item>();
      }
   }
}
