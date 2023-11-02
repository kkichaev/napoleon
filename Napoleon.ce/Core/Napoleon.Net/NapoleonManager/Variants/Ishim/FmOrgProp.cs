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
   public partial class FmOrgProp : Form
   {
      protected DataSet<string, Org> dsOrg;
      private DataSet<int, ScriptDef> dsCommonScriptDefs;
      private DataSet<int, AgentScript> dsAgentScript;
      private System.Object lockThis = new System.Object();
      private List<OrgDataView> data = new List<OrgDataView>();
      private List<ScriptDef> scripts = new List<ScriptDef>();
      private DataSet<string, OrgProp> dsOrgProp;
      private DataSet<int, AgentMatrix> dsAgentMatrix;
      private List<String> mtxNames = new List<string>();
      private DataSet<int, CommonConfig> dsCommonConfig;

      private static readonly string DEFAULT_MATRIX = "DefaultMatrix";
      private static readonly string DEFAULT_SCRIPT = "DefaultScript";

      public FmOrgProp()
      {
         InitializeComponent();

         dsAgentMatrix = DataModule.Get(AgentMatrix.OBJECT_NAME) == null ? new DataSet<int, AgentMatrix>(AgentMatrix.OBJECT_NAME) :
            (DataSet<int, AgentMatrix>)DataModule.Get(AgentMatrix.OBJECT_NAME);
         dsAgentScript = (DataSet<int, AgentScript>)DataModule.Get(AgentScript.OBJECT_NAME) ?? new DataSet<int, AgentScript>(AgentScript.OBJECT_NAME);
         dsCommonScriptDefs = (DataSet<int, ScriptDef>)DataModule.Get(ScriptDef.OBJECT_NAME) ?? new DataSet<int, ScriptDef>(ScriptDef.OBJECT_NAME);
         dsCommonScriptDefs.Filter = "\"userid\" is null or \"userid\" is not null";
         dsOrgProp = (DataSet<string, OrgProp>)DataModule.Get(OrgProp.OBJECT_NAME) ?? new DataSet<string, OrgProp>(OrgProp.OBJECT_NAME, true);
         grid.DataError += grid_DataError;
         dsCommonConfig = DataModule.Get(CommonConfig.OBJECT_NAME) as DataSet<int, CommonConfig>;
      }

      void grid_DataError(object sender, DataGridViewDataErrorEventArgs e)
      {
         
      }

      private void FmOrgProp_Load(object sender, EventArgs e)
      {
         Manager m = CurrentUser.user as Manager;

         if (m != null)
         {
            List<Agent> list = new List<Agent>(m.GetAgents().Values);
            list.Sort((x, y) => { return x.Name.CompareTo(y.Name); });

            cbAgent.Items.AddRange(list.ToArray());

            if (cbAgent.Items.Count > 0)
            {
               cbAgent.SelectedIndex = 0;
               btnRefresh.PerformClick();
            }
         }
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         Agent a = cbAgent.SelectedItem as Agent;

         if (a != null)
         {
            data = new List<OrgDataView>();
            dsOrg = DataModule.GetUserDataSet(a.id, "Org", typeof(DataSet<string, Org>), true) as DataSet<string, Org>;

            List<IDataSet> upd = new List<IDataSet>();
            upd.Add(dsOrg);
            upd.Add(dsAgentMatrix);
            upd.Add(dsCommonScriptDefs);
            upd.Add(dsAgentScript);
            upd.Add(dsOrgProp);
            upd.Add(dsCommonConfig);

            dsAgentScript.Filter = string.Format("\"userid\"='{0}'", a.id);
            dsOrgProp.Filter = string.Format("\"userid\"='{0}'", a.id);
            dsAgentMatrix.Filter = string.Format("\"userid\"='{0}'", a.id);

            FmWait.StdDataRefresh(this, upd, DoLoadData);
         }
      }

      private void DoLoadData()
      {
         List<Org> orgs = new List<Org>(dsOrg.Values);
         orgs.Sort((Org x, Org y) => { return x.name.CompareTo(y.name); });

         foreach (Org o in orgs)
         {
            OrgDataView d = new OrgDataView(this);
            d.org = o;

            if (dsOrgProp.ContainsKey(o.id))
            {
               OrgProp p = dsOrgProp[o.id];
               d.Matrix = p.matrix;
               d.Script = p.script;
            }

            data.Add(d);
         }

         List<AgentMatrix> mtx = new List<AgentMatrix>();
         mtx.AddRange(dsAgentMatrix.Values);
         mtx.Sort(new Comparison<AgentMatrix>(delegate(AgentMatrix lhs, AgentMatrix rhs) { return lhs.name.CompareTo(rhs.name); }));
         mtx.RemoveAll((m) => { return m == null || m.name == null || m.name.Length == 0; });
         AgentMatrix em = new AgentMatrix();
         mtx.Insert(0, em);

         mtxNames.Clear();

         foreach (AgentMatrix m in mtx)
            mtxNames.Add(m.name);

         dgvOrgMatrix.DataSource = mtxNames;

         List<ScriptDef> scr = new List<ScriptDef>();

         foreach (AgentScript a in dsAgentScript.Values)
         {
            if (dsCommonScriptDefs.ContainsKey(a.script))
               scr.Add(dsCommonScriptDefs[a.script]);
         }

         scripts.Clear();
         ScriptDef es = new ScriptDef();
         es.name = "";

         scripts.Add(es);

         foreach (ScriptDef s in scr)
            scripts.Add(s);

         dgvOrgScript.DataSource = scripts;
         dgvOrgScript.DisplayMember = "Name";
         dgvOrgScript.ValueMember = "ID";

         grid.DataSource = data;

         cbMatrix.SelectedIndexChanged -= ComboBox_SelectedIndexChanged;
         cbScript.SelectedIndexChanged -= ComboBox_SelectedIndexChanged;

         cbMatrix.Items.Clear();
         cbScript.Items.Clear();

         cbMatrix.Items.AddRange(mtxNames.ToArray());
         cbScript.Items.AddRange(scripts.ToArray());

         Agent ag = cbAgent.SelectedItem as Agent;

         if (ag != null)
         {
            CommonConfig cc = ConfigUtils.GetConfig(dsCommonConfig, new ConfigKeyItems(DEFAULT_MATRIX), ag.id);
            if (cc != null)
            {
               cbMatrix.SelectedIndex = cbMatrix.Items.IndexOf(cc.value);
            }

            cc = ConfigUtils.GetConfig(dsCommonConfig, new ConfigKeyItems(DEFAULT_SCRIPT), ag.id);
            if (cc != null)
            {
               int id = 0;
               if(Int32.TryParse(cc.value, out id))
               {
                  foreach(ScriptDef sd in cbScript.Items)
                     if (sd.id == id)
                     {
                        cbScript.SelectedItem = sd;
                        break;
                     }
               }else
                  cbScript.SelectedIndex = 0;
            }
         }

         cbMatrix.SelectedIndexChanged += ComboBox_SelectedIndexChanged;
         cbScript.SelectedIndexChanged += ComboBox_SelectedIndexChanged;

         btnSave.Enabled = false;
      }

      private void grid_CellValueChanged(object sender, DataGridViewCellEventArgs e)
      {
         btnSave.Enabled = true;
      }

      private void grid_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         ((DataGridView)sender).CommitEdit(DataGridViewDataErrorContexts.Commit);
      }

      void TextChange(object sender, EventArgs e)
      {
         timer1.Stop();
         timer1.Start();
      }

      private void timer1_Tick(object sender, EventArgs e)
      {
         DoSearch();
      }

      private void DoSearch()
      {
         lock (lockThis)
         {
            timer1.Stop();

            string org = edOrg.Text.Trim().ToUpper();
            string matrix = edMatrix.Text.Trim().ToUpper();
            string script = edScript.Text.Trim().ToUpper();

            grid.SuspendLayout();

            List<OrgDataView> src = new List<OrgDataView>();

            foreach (OrgDataView d in data)
            {
               if (org.Length > 0 && !d.Name.ToUpper().Contains(org))
                  continue;

               if (matrix.Length > 0 && (d.Matrix == null || !d.Matrix.ToUpper().Contains(matrix)))
                  continue;

               if (script.Length > 0 && !GetScriptsByName(script).Contains(d.Script))
                  continue;

               src.Add(d);
            }

            grid.DataSource = src;
            grid.ResumeLayout();
         }
      }

      private List<int> GetScriptsByName(string script)
      {
         List<int> result = new List<int>();

         foreach (ScriptDef s in scripts)
         {
            if (s.Name.ToUpper().Contains(script))
               result.Add(s.ID);
         }

         return result;
      }

      private void btnClear_Click(object sender, EventArgs e)
      {
         edOrg.Text = string.Empty;
         edMatrix.Text = string.Empty;
         edScript.Text = string.Empty;
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         btnSave.Enabled = !SaveChanges(true);
      }

      private bool SaveChanges(bool showDialog)
      {
         bool ret = false;
         Agent a = cbAgent.SelectedItem as Agent;

         if (a != null)
         {
            grid.CommitEdit(DataGridViewDataErrorContexts.Commit);

            DataSet<int, OrgProp> ds = new DataSet<int, OrgProp>(OrgProp.OBJECT_NAME, false);

            foreach (OrgDataView d in data)
            {
               if (d.org != null)
               {
                  OrgProp p = new OrgProp();
                  p.id = d.org.id;
                  p.matrix = d.Matrix;
                  p.script = d.Script;
                  p.userid = a.id;

                  ds.Add(ds.Count, p);
               }
            }

            string selMatrix = cbMatrix.SelectedItem as string;

            if (selMatrix != null)
            {
               CommonConfig cc = ConfigUtils.GetConfig(dsCommonConfig, new ConfigKeyItems(DEFAULT_MATRIX), a.id);
               if (cc == null)
               {
                  cc = new CommonConfig();
                  cc.userid = a.id;
                  cc.key = DEFAULT_MATRIX;
                  dsCommonConfig.Add(dsCommonConfig.Count, cc);
               }

               cc.value = selMatrix;
            }

            ScriptDef selScript = cbScript.SelectedItem as ScriptDef;

            if (selScript != null)
            {
               CommonConfig cc = ConfigUtils.GetConfig(dsCommonConfig, new ConfigKeyItems(DEFAULT_SCRIPT), a.id);
               if (cc == null)
               {
                  cc = new CommonConfig();
                  cc.userid = a.id;
                  cc.key = DEFAULT_SCRIPT;
                  dsCommonConfig.Add(dsCommonConfig.Count, cc);
               }

               cc.value = selScript.ID.ToString();
            }

            List<IDataSet> wrs = new List<IDataSet>();
            wrs.Add(ds);
            wrs.Add(dsCommonConfig);

            ret = DataModule.UpdateDataSet(wrs, null, null, Config.GetConfig().GetConnection());

            if (showDialog)
            {
               MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");
            }
         }

         return ret;
      }

      private void FmOrgProp_FormClosing(object sender, FormClosingEventArgs e)
      {
         if(btnSave.Enabled && MessageBox.Show("Сохранить изменения?", 
            "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == System.Windows.Forms.DialogResult.OK)
          SaveChanges(false);
      }

      private void ComboBox_SelectedIndexChanged(object sender, EventArgs e)
      {
         btnSave.Enabled = true;
      }
   }

   class OrgDataView
   {
      public OrgDataView(FmOrgProp owner)
      {
         this.owner = owner;
      }

      public FmOrgProp owner;
      public Org org;
      public string Matrix { get; set; }
      public int Script { get; set; }
         
      public string Name { 
         get 
         {
            return org == null ? string.Empty : org.name;
         }
      }
   }
}
