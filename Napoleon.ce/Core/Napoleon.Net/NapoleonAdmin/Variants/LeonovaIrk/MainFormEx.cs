using System.Windows.Forms;

namespace GRSoft.NapoleonAdmin
{
   public class MainFormEx : MainForm
   {
      
      DataGridViewCheckBoxColumn clmnDisableSave;

      DataGridViewCheckBoxColumn[] rightColumns;

      public MainFormEx()
      {
         clmnDisableSave = new DataGridViewCheckBoxColumn();
         clmnDisableSave.DataPropertyName = "DisableSave";
         clmnDisableSave.HeaderText = "Лимит изменения маршрутов";
         clmnDisableSave.Name = "clmnCanDisableFirms";
         clmnDisableSave.Visible = false;
         clmnDisableSave.Width = 90;

         rightColumns = new DataGridViewCheckBoxColumn[] { clmnDisableSave };
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

         if (clmnDisableSave != null)
         {
            clmnDisableSave.Visible = !agentView;
         }
      }
   }
}