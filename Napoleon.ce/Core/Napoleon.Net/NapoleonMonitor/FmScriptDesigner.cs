using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Collections;

namespace GRSoft.NapoleonManager
{
   public partial class FmScriptDesigner : Form
   {
      private DataSet<int, ScriptDef> dsScriptDef;
      private int selectedScriptId = -1;
      private GRSoft.NapoleonManager.FmScriptEdit.PostProcess afterEdit;
         

      public FmScriptDesigner()
      {
         InitializeComponent();

         dsScriptDef = (DataSet<int, ScriptDef>) DataModule.Get(ScriptDef.OBJECT_NAME) ?? 
               new DataSet<int, ScriptDef>(ScriptDef.OBJECT_NAME);
         dsScriptDef.Filter = "\"userid\" is null or not \"userid\" is null";
         afterEdit = new FmScriptEdit.PostProcess(delegate(int id) { selectedScriptId = id; btnRefresh_Click(null, null); });

         Refreshing();
      }

      void Refreshing()
      {
         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsScriptDef);
         DataModule.SetDataRepsonceHandlers(DataModule_DataProcessed,
            DataModule_OnDataResponceError);

         FmWait.ShowForm(this,
            DataModule.RefreshGiveSets(Config.GetConfig().
            GetConnection(), list, FmWait.ProgressIndicator));
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         Refreshing();
      }

      void DataModule_DataProcessed(object sender, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         Invoke(new InvokeDelegate(delegate() { RefreshData();}));
      }

      private void RefreshData()
      {
         List<ScriptDef> list = new List<ScriptDef>();
         list.AddRange(dsScriptDef.Values);
         list.Sort(new Comparison<ScriptDef>(delegate(ScriptDef s1, ScriptDef s2) { return s1.Name.CompareTo(s2.Name); }));
         dgvSrcipts.DataSource = list;

         if (selectedScriptId != -1 &&
            dgvSrcipts.Rows.Count > 0)
         {
            foreach (DataGridViewRow r in dgvSrcipts.Rows)
            {
               if (r != null)
               {
                  ScriptDef sd = r.DataBoundItem as ScriptDef;
                  if (sd != null && sd.id == selectedScriptId)
                     dgvSrcipts.CurrentCell = dgvSrcipts[0, r.Index];
               }
            }
         }
      }

      void DataModule_OnDataResponceError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         MessageBox.Show(e.Msg);
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         FmScriptEdit.ShowModal(afterEdit);
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         ScriptDef scriptDef = GetSelectedScript();

         if (scriptDef != null)
            FmScriptEdit.ShowModal(scriptDef, afterEdit);
      }

      private ScriptDef GetSelectedScript()
      {
         ScriptDef result = null;
         DataGridViewRow row = dgvSrcipts.CurrentRow;

         if (row != null)
            result = row.DataBoundItem as ScriptDef;

         return result;
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         ScriptDef script = GetSelectedScript();

         if (script != null &&
            MessageBox.Show("Запись будет удалена", "Внимание", MessageBoxButtons.OKCancel,
            MessageBoxIcon.Warning) == DialogResult.OK)
         {
            List<IDataSet> rmList = new List<IDataSet>();
            DataSet<int, ScriptDef> rmSet = new DataSet<int, ScriptDef>(ScriptDef.OBJECT_NAME, false);
            rmSet.Add(script.id, script);
            rmList.Add(rmSet);

            if (DataModule.UpdateDataSet(null, rmList, null, Config.GetConfig().GetConnection()))
            {
               dsScriptDef.Remove(script.id);
               RefreshData();
            }
            else
               MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
         }
      }

      private void dgvSrcipts_CellDoubleClick(object sender, DataGridViewCellEventArgs e)
      {
         ScriptDef scriptDef = GetSelectedScript();

         if (scriptDef != null)
            FmScriptEdit.ShowModal(scriptDef, afterEdit);
      }
   }
}
