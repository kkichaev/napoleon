using System.Windows.Forms;

namespace GRSoft.NapoleonAdmin
{
   public class MainFormEx : MainForm
   {
      DataGridViewCheckBoxColumn clmnCanSend;

      DataGridViewCheckBoxColumn[] rightColumns;

      public MainFormEx()
      {
         clmnCanSend = new DataGridViewCheckBoxColumn();
         clmnCanSend.DataPropertyName = "CanSendOrders";
         clmnCanSend.HeaderText = "огр. доступ";
         clmnCanSend.Name = "clmnCanSend";
         clmnCanSend.Visible = false;
         clmnCanSend.Width = 90;

         rightColumns = new DataGridViewCheckBoxColumn[] { clmnCanSend };
         usersView.Columns.AddRange(rightColumns);
      }

      protected override void usersView_CurrentCellDirtyStateChanged(object sender, System.EventArgs e)
      {
         base.usersView_CurrentCellDirtyStateChanged(sender, e);
         foreach(DataGridViewCheckBoxColumn c in rightColumns)
            if (usersView.CurrentCell.ColumnIndex == c.DisplayIndex)
            {
               usersView.CommitEdit(DataGridViewDataErrorContexts.Commit);
               break;
            }
      }

      protected override void PrepareViewComponents(bool agentView)
      {
         base.PrepareViewComponents(agentView);

         tracking.Visible = agentView;

         if (clmnCanSend != null)
            clmnCanSend.Visible = !agentView;
      }
   }
}