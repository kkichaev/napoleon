using GRSoft.Network;
using System.Windows.Forms;

namespace GRSoft.NapoleonAdmin
{
   public class MainFormEx : MainForm
   {
      DataGridViewCheckBoxColumn clmnWriteTask;

      DataGridViewCheckBoxColumn[] rightColumns;

      public MainFormEx()
      {
         clmnWriteTask = new DataGridViewCheckBoxColumn();
         clmnWriteTask.DataPropertyName = "CanSaveTask";
         clmnWriteTask.HeaderText = "Может сохранять задачи";
         clmnWriteTask.Name = "clmnCanSend";
         clmnWriteTask.Visible = false;
         clmnWriteTask.Width = 90;

         rightColumns = new DataGridViewCheckBoxColumn[] { clmnWriteTask };
         usersView.Columns.AddRange(rightColumns);

         Width += 100;
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

         if (clmnWriteTask != null)
         {
            clmnWriteTask.Visible = !agentView;
         }
      }
   }
}