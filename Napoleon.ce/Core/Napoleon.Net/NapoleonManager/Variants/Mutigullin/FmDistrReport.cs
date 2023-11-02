using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmDistrReport : Form
   {
      public FmDistrReport()
      {
         InitializeComponent();
      }

      private void button1_Click(object sender, EventArgs e)
      {
         Param arg = new Param();
         arg.userids = UserIDS;
         arg.start = datePeriodView1.Start.Date;
         arg.finish = datePeriodView1.Finish.Date.AddDays(1);

         NetOrg n = cbNetOrg.SelectedItem as NetOrg;

         if (n != null)
            arg.netorg = n.id;

         ReportResult.DoReport("distrib_report", arg, this);
      }

      class Param : GRSoft.Network.DataObject
      {
         public String userids = String.Empty;
         public DateTime start = DateTime.MinValue;
         public DateTime finish = DateTime.MinValue;
         public String netorg = String.Empty;
      }

      private void FmDistrReport_Load(object sender, EventArgs e)
      {
         Manager m = CurrentUser.user as Manager;

         if (m != null)
         {
            List<Agent> lsa = new List<Agent>();
            foreach (Division.DivisionAgent a in m.Division.GetAllAgents())
               if (a.agent != null && cbAgent.Items.Contains(a.agent) == false)
                  lsa.Add(a.agent);

            lsa.Sort((x, y) => { return x.name.CompareTo(y.name); });

            foreach (Agent a in lsa)
               cbAgent.Items.Add(a);

            List<Division> lsd = new List<Division>();
            lsd.Add(m.Division);

            foreach (Division d in m.Childs)
               lsd.Add(d);

            lsd.Sort((x, y) => { return x.name.CompareTo(y.name); });

            foreach (Division d in lsd)
               cbDivision.Items.Add(d);

            if (cbAgent.Items.Count > 0)
               cbAgent.SelectedIndex = 0;

            if (cbDivision.Items.Count > 0)
               cbDivision.SelectedIndex = 0;
         }

         DataSet<string, NetOrg> dsNetOrg = (DataSet<string, NetOrg>)DataModule.Get(NetOrg.OBJECT_NAME);

         List<NetOrg> list = new List<NetOrg>();
         list.AddRange(dsNetOrg.Values);
         list.Sort((x, y) => { return x.name.CompareTo(y.name); });

         foreach (NetOrg n in list)
            cbNetOrg.Items.Add(n);

         if (cbNetOrg.Items.Count > 0)
            cbNetOrg.SelectedIndex = 0;
      }

      private List<Agent> CollectAgents()
      {
         List<Agent> result = new List<Agent>();

         if (rbAgent.Checked && cbAgent.SelectedItem != null)
         {
            Agent agent = cbAgent.SelectedItem as Agent;
            result.Add(agent);
         }
         else if (rbDivision.Checked && cbDivision.SelectedItem != null)
         {
            Division division = cbDivision.SelectedItem as Division;

            if (division != null)
            {
               List<GRSoft.NapoleonManager.Division.DivisionAgent> agents = division.GetAllAgents();

               if (agents.Count > 0)
               {
                  List<GRSoft.NapoleonManager.Division.DivisionAgent>.Enumerator iter = agents.GetEnumerator();

                  while (iter.MoveNext())
                  {
                     GRSoft.NapoleonManager.Division.DivisionAgent agent = iter.Current;

                     if (agent != null && agent.agent != null)
                        result.Add(agent.agent);
                  };
               }
            }
         }
         else
         {
            Manager m = CurrentUser.user as Manager;

            if (m != null)
               result.AddRange(m.GetAgents().Values);
         }

         return result;
      }

      public string UserIDS
      {
         get
         {
            StringBuilder sb = new StringBuilder();

            foreach (Agent a in CollectAgents())
               if (a != null)
               {
                  if (sb.Length > 0)
                     sb.Append(",");

                  sb.Append(a.id);
               }

            return sb.ToString();
         }
      }
   }
}
