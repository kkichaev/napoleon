using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class AgentChoose : Form
   {
      List<Agent> ret = null;
      public AgentChoose(List<Agent> check)
      {
         InitializeComponent();
         List<ChooseAgent> data = new List<ChooseAgent>();
         foreach (Agent agent in (CurrentUser.user as Manager).GetAgents().Data)
         {
            ChooseAgent ca = new ChooseAgent(agent, check.Contains(agent));
            data.Add(ca);
         }

         dgvAgents.AutoGenerateColumns = false;
         dgvAgents.DataSource = data;
      }

      private void tsbCancel_Click(object sender, EventArgs e)
      {
         DialogResult = DialogResult.Cancel;
      }

      private void tsbOK_Click(object sender, EventArgs e)
      {
         ret = new List<Agent>();
         foreach (ChooseAgent ca in (List<ChooseAgent>)dgvAgents.DataSource)
         {
            if (ca.Check)
               ret.Add(ca.Agent);
         }

         DialogResult = DialogResult.OK;
      }

      public List<Agent> SelectedAgents { get { return ret; } }

      private void dgvAgents_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         if (dgvAgents.CurrentCell.ColumnIndex == clmnCheck.Index)
            dgvAgents.CommitEdit(DataGridViewDataErrorContexts.Commit);
      }
   }

   class ChooseAgent
   {
      bool check;
      Agent a;

      public ChooseAgent(Agent a, bool check)
      {
         this.a = a;
         this.check = check;
      }

      public bool Check { get { return check; } set { check = value; } }

      public String Name { get { return a.Name; } }
      public Agent Agent { get { return a; } }
   }
}
