using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmSelectAgent : Form
   {
      Agent selected = null;
      public FmSelectAgent()
      {
         InitializeComponent();

         List<Agent> src = new List<Agent>();
         foreach(Agent a in (CurrentUser.user as Manager).GetAgents().Data)
            src.Add(a);

         src.Sort();
         lbAgents.Items.AddRange(src.ToArray());
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         if (DialogResult == System.Windows.Forms.DialogResult.OK)
            selected = lbAgents.SelectedItem as Agent;

         base.OnClosing(e);
      }

      public static Agent DoSelect()
      {
         Agent sel = null;
         FmSelectAgent form = new FmSelectAgent();
         if (form.ShowDialog() == DialogResult.OK)
            sel = form.selected;
         return sel;
      }

      private void lbAgents_MouseDoubleClick(object sender, MouseEventArgs e)
      {
         if (lbAgents.SelectedItem != null)
            DialogResult = System.Windows.Forms.DialogResult.OK;
      }
   }
}
