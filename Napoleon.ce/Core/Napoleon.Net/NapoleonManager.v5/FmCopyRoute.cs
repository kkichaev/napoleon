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
   public partial class FmCopyRoute : Form
   {
      Agent srcAgent;

      public FmCopyRoute()
      {
         InitializeComponent();
      }

      public Agent SelectedAgent { get { return cbAgents.SelectedItem as Agent; } }

      public void SetCopiedAgent(Agent srcAgent)
      {
         this.srcAgent = srcAgent;

         Manager m = CurrentUser.user as Manager;
         foreach (Agent a in m.GetAgents().Data)
         {
            if (a.id != srcAgent.id)
               cbAgents.Items.Add(a);
         }
      }

      private void btnOK_Click(object sender, EventArgs e)
      {
         DialogResult = System.Windows.Forms.DialogResult.OK;
      }

      private void FmCopyRoute_KeyDown(object sender, KeyEventArgs e)
      {
         if (e.KeyCode == Keys.Escape)
            DialogResult = System.Windows.Forms.DialogResult.Cancel;
      }
   }
}
