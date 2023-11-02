using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class FmScriptDesignerEx : FmScriptDesigner
   {
      DataGridViewTextBoxColumn clmnType;
      //private DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
      //private DataGridViewTextBoxColumn dataGridViewTextBoxColumn4;
      DataSet<string, Supplier> types;
      DataSet<int, UnusedScripts> unusedScripts = new DataSet<int, UnusedScripts>(UnusedScripts.OBJECT_NAME, false);

      public FmScriptDesignerEx()
      {
         types = (DataSet<string, Supplier>)DataModule.Get(Supplier.OBJECT_NAME) ??
            new DataSet<string, Supplier>(Supplier.OBJECT_NAME, true);

         clmnType = new DataGridViewTextBoxColumn();
         clmnType.DataPropertyName = "Suppl";
         clmnType.FillWeight = 30F;
         clmnType.HeaderText = "Производитель";
         clmnType.Name = "clmnType";


         dgvSrcipts.Columns.Insert(1, clmnType);
      }

      protected override void AddData(List<Network.IDataSet> upd)
      {
         base.AddData(upd);
         upd.Insert(0, types);
         upd.Add(unusedScripts);
      }

      protected override bool IsReadOnly(ScriptDef sd)
      {
         return !unusedScripts.ContainsKey(sd.id);
      }

      //private void InitializeComponent()
      //{
      //   this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
      //   this.dataGridViewTextBoxColumn4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
      //   this.SuspendLayout();
      //   // 
      //   // dataGridViewTextBoxColumn3
      //   // 
      //   this.dataGridViewTextBoxColumn3.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
      //   this.dataGridViewTextBoxColumn3.DataPropertyName = "Name";
      //   this.dataGridViewTextBoxColumn3.FillWeight = 30F;
      //   this.dataGridViewTextBoxColumn3.HeaderText = "Название";
      //   this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
      //   this.dataGridViewTextBoxColumn3.ReadOnly = true;
      //   // 
      //   // dataGridViewTextBoxColumn4
      //   // 
      //   this.dataGridViewTextBoxColumn4.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
      //   this.dataGridViewTextBoxColumn4.DataPropertyName = "DocsStr";
      //   this.dataGridViewTextBoxColumn4.HeaderText = "Документы";
      //   this.dataGridViewTextBoxColumn4.Name = "dataGridViewTextBoxColumn4";
      //   this.dataGridViewTextBoxColumn4.ReadOnly = true;
      //   // 
      //   // FmScriptDesignerEx
      //   // 
      //   this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
      //   this.ClientSize = new System.Drawing.Size(642, 391);
      //   this.Name = "FmScriptDesignerEx";
      //   this.ResumeLayout(false);
      //   this.PerformLayout();

      //}
   }
}
