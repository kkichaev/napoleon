using System.Collections.Generic;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonAdmin
{
   public class MainFormEx : MainForm
   {
      DataGridViewCheckBoxColumn clmnPrevVersionBlock;
      SimpleDataSet<NewVersionAction> versions = new SimpleDataSet<NewVersionAction>(NewVersionAction.OBJECT_NAME);

      public MainFormEx()
      {
         clmnPrevVersionBlock = new DataGridViewCheckBoxColumn();
         clmnPrevVersionBlock.DataPropertyName = "DisableOldVersion";
         clmnPrevVersionBlock.HeaderText = "Блокировка старой версии";
         clmnPrevVersionBlock.Name = "clmnCanDisableFirms";
         clmnPrevVersionBlock.Visible = false;
         clmnPrevVersionBlock.Width = 90;

         usersView.Columns.Add(clmnPrevVersionBlock);

         DataGridViewTextBoxColumn clmnId = new System.Windows.Forms.DataGridViewTextBoxColumn();
         // 
         // clmnId
         // 
         clmnId.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmnId.DataPropertyName = "KISID";
         clmnId.HeaderText = "Код УС";
         clmnId.Name = "KISID";
         clmnId.Width = 150;

         usersView.Columns.Insert(1, clmnId);
      }

      public override void BeforeUpdate(List<IDataSet> wr, List<IDataSet> rmv)
      {
         base.BeforeUpdate(wr, rmv);
         wr.Add(versions);
      }

      protected override void usersView_CurrentCellDirtyStateChanged(object sender, System.EventArgs e)
      {
         base.usersView_CurrentCellDirtyStateChanged(sender, e);
         if (usersView.CurrentCell.ColumnIndex == clmnPrevVersionBlock.DisplayIndex)
         {
            usersView.CommitEdit(DataGridViewDataErrorContexts.Commit);
         }
      }

      protected override void PrepareViewComponents(bool agentView)
      {
         base.PrepareViewComponents(agentView);

         if (clmnPrevVersionBlock != null)
         {
            clmnPrevVersionBlock.Visible = agentView;
         }
      }

      protected override void AddUpdDataSet(List<IDataSet> upd)
      {
         upd.Add(versions);
      }

      protected override void UpdateLoadedData()
      {
         base.UpdateLoadedData();
         if (versions.Count == 0)
            foreach (Agent a in dsAgents.Data)
            {
               NewVersionAction el = new NewVersionAction();
               el.userid = a.id;
               versions.Add(el);
            }
      }
   }
}