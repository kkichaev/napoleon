using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmScrAssign : Form
   {
      private DataSet<int, ScriptDef> dsScriptDef;
      private DataSet<string, ScriptAssign> dsScrAssign;
      private DataSet<string, Slsnet> dsSlsnet;

      public FmScrAssign()
      {
         InitializeComponent();
         dgvScript.AutoGenerateColumns = false;

         dsScriptDef = (DataSet<int, ScriptDef>)DataModule.Get(ScriptDef.OBJECT_NAME) ?? new DataSet<int, ScriptDef>(ScriptDef.OBJECT_NAME);
         dsScrAssign = (DataSet<string, ScriptAssign>)DataModule.Get(ScriptAssign.OBJECT_NAME) ?? new DataSet<string, ScriptAssign>(ScriptAssign.OBJECT_NAME);
         dsSlsnet = (DataSet<string, Slsnet>)DataModule.Get(Slsnet.OBJECT_NAME) ?? new DataSet<string, Slsnet>(Slsnet.OBJECT_NAME);

         btnSave.Enabled = false;
      }

      

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         edFilter.Text = string.Empty;
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsScriptDef);
         upd.Add(dsScrAssign);
         FmWait.StdDataRefresh(this, upd, DoLoadData, btnRefresh);
       }

      private void DoLoadData()
      {
         FilterScript(string.Empty);

         cbSls.Items.Clear();
         List<Slsnet> list = new List<Slsnet>();
         list.AddRange(dsSlsnet.Values);
         list.Sort((lhs, rhs) => { return lhs.Name.CompareTo(rhs.Name); });
         cbSls.Items.AddRange(list.ToArray());
      }

      private void FilterScript(string filter)
      {
         List<ScriptDef> list = new List<ScriptDef>();

         foreach (ScriptDef sd in dsScriptDef.Values)
         {
            if (filter.Length == 0 || sd.Name.ToUpper().Contains(filter.ToUpper()))
               list.Add(sd);
         }

         list.Sort((lhs, rhs) => { return lhs.Name.CompareTo(rhs.Name); });

         dgvScript.DataSource = list;
      }

      private void FmScrAssign_Load(object sender, EventArgs e)
      {
         btnRefresh.PerformClick();
      }

      private void cbSls_SelectedIndexChanged(object sender, EventArgs e)
      {
         lbScriptSlsOrgs.Items.Clear();

         ToolStripComboBox cb = (ToolStripComboBox)sender;
         ScriptAssign ass = GetCurAssignment();
         if (ass != null) 
         {
            if (ass.items != null)
            {
               List<ScriptDef> list = new List<ScriptDef>();
               foreach (ScriptAssignmentItem i in ass.items)
                  if(i.script != null)
                     list.Add(i.script);

               list.Sort((lhs, rhs) => { return lhs.Name.CompareTo(rhs.Name); });
               lbScriptSlsOrgs.Items.AddRange(list.ToArray());
            }
         }

         dgvScript.Refresh();
      }

      private ScriptAssign GetCurAssignment()
      {
         return GetCurAssignment(false);
      }

      private ScriptAssign GetCurAssignment(bool init)
      {
         ScriptAssign result = null;
         Slsnet selSls = cbSls.SelectedItem as Slsnet;

         if (selSls != null && dsScrAssign.ContainsKey(selSls.id))
            result = dsScrAssign[selSls.id];

         if (init && result == null && selSls != null) 
         {
            result = new ScriptAssign();
            result.id = selSls.id;
            result.slsnet = selSls;
            result.items = new List<ScriptAssignmentItem>();

            dsScrAssign.Add(result.id, result);
         }

         return result;
      }

      private void dgvScript_CellDoubleClick(object sender, DataGridViewCellEventArgs e)
      {
         
         if (e.RowIndex >= 0)
         {
            ScriptDef sd = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as ScriptDef;
            ScriptAssign sa = GetCurAssignment(true);
            
            if (sd != null && sa != null && !ScrIsAssign(sa, sd.id))
            {
               lbScriptSlsOrgs.Items.Add(sd);

               ScriptAssignmentItem i = new ScriptAssignmentItem();
               i.id = sd.id;
               i.script = sd;
               sa.items.Add(i);

               btnSave.Enabled = true;
            }
         }
      }

      private bool ScrIsAssign(ScriptAssign sa, int id)
      {
         bool result = false;
         foreach (ScriptAssignmentItem i in sa.items)
         {
            if (i.id == id)
            {
               result = true;
               break;
            }
         }

         return result;
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         if (Save())
            btnSave.Enabled = false;
         else
            DialogUtil.UpdateErrMsg(this);
      }

      private bool Save()
      {
         DataSet<string, ScriptAssign> dsRem = new DataSet<string, ScriptAssign>(ScriptAssign.OBJECT_NAME, false);
         DataSet<string, ScriptAssign> dsWr = new DataSet<string, ScriptAssign>(ScriptAssign.OBJECT_NAME, false);

         foreach (ScriptAssign sa in dsScrAssign.Values)
         {
            if (sa.items.Count > 0)
               dsWr.Add(sa.id, sa);
            else
               dsRem.Add(sa.id, sa);
         }

         List<IDataSet> wrSet = new List<IDataSet>();
         List<IDataSet> rmSet = new List<IDataSet>();

         if (dsWr.Count > 0)
            wrSet.Add(dsWr);

         if (dsRem.Count > 0)
            rmSet.Add(dsRem);

         return DataModule.UpdateDataSet(wrSet, rmSet, null, Config.GetConfig().GetConnection());
      }

      private void lbScriptSlsOrgs_DoubleClick(object sender, EventArgs e)
      {
         ScriptDef scriptDef = ((ListBox)sender).SelectedItem as ScriptDef;
         ScriptAssign sa = GetCurAssignment();

         if (scriptDef != null && sa != null)
         {
            foreach (ScriptAssignmentItem i in sa.items)
            {
               if (i.id.Equals(scriptDef.id))
               {
                  sa.items.Remove(i);
                  break;
               }
            }

            lbScriptSlsOrgs.Items.Remove(scriptDef);
            btnSave.Enabled = true;
         }
      }

      private void edFilter_TextChanged(object sender, EventArgs e)
      {
         FilterScript(((ToolStripTextBox)sender).Text.Trim()) ;
      }

      private void btnClearFilter_Click(object sender, EventArgs e)
      {
         edFilter.Text = string.Empty;
      }

      private void dgvOrg_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         ScriptDef sd = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as ScriptDef;
         ScriptAssign oa = GetCurAssignment();

         if (sd != null && oa != null && ScrIsAssign(oa, sd.id))
            e.CellStyle.BackColor = Color.Gray;
         else
            e.CellStyle.BackColor = Color.White;
      }

      private void btnOrg_Click(object sender, EventArgs e)
      {
         new FmOrg().Show();
      }
   }
}
