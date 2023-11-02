using System.Windows.Forms;

namespace GRSoft.NapoleonAdmin
{
   public class MainFormEx : MainForm
   {
      DataGridViewCheckBoxColumn clmnCanViewMinCost;

      public MainFormEx()
      {
         clmnCanViewMinCost = new DataGridViewCheckBoxColumn();
         clmnCanViewMinCost.DataPropertyName = "CanViewMinCost";
         clmnCanViewMinCost.HeaderText = "Видит минимальную цену";
         clmnCanViewMinCost.Name = "clmnSellWithoutRest";
         clmnCanViewMinCost.Visible = false;
         clmnCanViewMinCost.Width = 90;
         usersView.Columns.Add(clmnCanViewMinCost);
      }

      protected override void usersView_CurrentCellDirtyStateChanged(object sender, System.EventArgs e)
      {
         base.usersView_CurrentCellDirtyStateChanged(sender, e);
         if (usersView.CurrentCell.ColumnIndex == clmnCanViewMinCost.DisplayIndex)
            usersView.CommitEdit(DataGridViewDataErrorContexts.Commit);
      }

      protected override void PrepareViewComponents(bool agentView)
      {
         base.PrepareViewComponents(agentView);

         tracking.Visible = agentView;

         if (clmnCanViewMinCost != null)
            clmnCanViewMinCost.Visible = agentView;
      }

   }
}