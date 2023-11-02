
using GRSoft.Network;
using System.Collections.Generic;
using System.Drawing;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public class FmScriptDesignerEx : FmScriptDesigner
   {
      readonly DataGridViewCheckBoxColumn clmnActive = new DataGridViewCheckBoxColumn();
      DataSet<int, UnusedScripts> unusedScripts = new DataSet<int, UnusedScripts>(UnusedScripts.OBJECT_NAME, false);

      public FmScriptDesignerEx()
      {
         clmnActive = new DataGridViewCheckBoxColumn();
         clmnActive.DataPropertyName = "Active";
         clmnActive.FillWeight = 20;
         clmnActive.HeaderText = "Активный";
         clmnActive.Name = "Active";

         dgvSrcipts.Columns.Insert(0, clmnActive);

         dgvSrcipts.CurrentCellDirtyStateChanged += DgvSrcipts_CurrentCellDirtyStateChanged;
      }

      protected override void AddData(List<IDataSet> upd)
      {
         base.AddData(upd);
         upd.Add(unusedScripts);
      }

      protected override bool IsReadOnly(ScriptDef sd)
      {
         return !unusedScripts.ContainsKey(sd.id);
      }

      private void radioButtonChanged()
      {
         if (dgvSrcipts.CurrentCell.ColumnIndex == clmnActive.Index)
         {
            SimpleDataSet<ScriptDefActive> changed = new SimpleDataSet<ScriptDefActive>(ScriptDefActive.OBJECT_NAME, false);
            foreach (DataGridViewRow row in dgvSrcipts.Rows)
            {
               ScriptDef sd = row.DataBoundItem as ScriptDef;
               bool isActive = row.Index == dgvSrcipts.CurrentCell.RowIndex;
               if( sd.Active != isActive)
               {
                  sd.Active = isActive;
                  changed.Add(new ScriptDefActive(sd, isActive));
               }
            }
            dgvSrcipts.InvalidateColumn(clmnActive.DisplayIndex);

            if(changed.Count > 0)
            {
               List<IDataSet> wr = new List<IDataSet>();
               wr.Add(changed);
               DataModule.UpdateDataSet(wr, null, null, Config.GetConfig().GetConnection());
            }
         }
      }

      private void DgvSrcipts_CurrentCellDirtyStateChanged(object sender, System.EventArgs e)
      {
         radioButtonChanged();
      }
   }
}