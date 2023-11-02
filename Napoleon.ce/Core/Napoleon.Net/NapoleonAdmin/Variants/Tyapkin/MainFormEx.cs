using System.Windows.Forms;

namespace GRSoft.NapoleonAdmin
{
   public class MainFormEx : MainForm
   {
      DataGridViewCheckBoxColumn clmnGroupReportFilter;

      public MainFormEx()
      {
         clmnGroupReportFilter = new DataGridViewCheckBoxColumn();
         clmnGroupReportFilter.DataPropertyName = "GroupReportFilter";
         clmnGroupReportFilter.HeaderText = "Период отчета";
         clmnGroupReportFilter.Name = "clmnGroupReportFilter";
         clmnGroupReportFilter.Visible = false;
         clmnGroupReportFilter.Width = 90;
         usersView.Columns.Add(clmnGroupReportFilter);
      }

     
      protected override void usersView_CurrentCellDirtyStateChanged(object sender, System.EventArgs e)
      {
         base.usersView_CurrentCellDirtyStateChanged(sender, e);
         if (usersView.CurrentCell.ColumnIndex == clmnGroupReportFilter.DisplayIndex)
            usersView.CommitEdit(DataGridViewDataErrorContexts.Commit);
      }

      protected override void PrepareViewComponents(bool agentView)
      {
         base.PrepareViewComponents(agentView);

         tracking.Visible = agentView;

         if (clmnGroupReportFilter != null)
            clmnGroupReportFilter.Visible = agentView;
      }
   }
}