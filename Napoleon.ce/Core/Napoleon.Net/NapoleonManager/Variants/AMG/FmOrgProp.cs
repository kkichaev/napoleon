using GRSoft.NapoleonManager.Utils;
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
      private System.Object lockThis = new System.Object();
      private SortableBindingList<OrgDataView> data = new SortableBindingList<OrgDataView>();
      private List<ScriptDef> scripts = new List<ScriptDef>();
      private DataSet<string, OrgProp> dsOrgProp;
      private List<String> mtxNames = new List<string>();

      private static readonly string DEFAULT_MATRIX = "DefaultMatrix";
      private static readonly string DEFAULT_SCRIPT = "DefaultScript";

      public FmOrgProp()
      {
         InitializeComponent();

         dsOrg = (DataSet<string, Org>)DataModule.Get(Org.COMMON_OBJECT_NAME) ?? new DataSet<string, Org>(Org.COMMON_OBJECT_NAME);
         dsCommonScriptDefs = (DataSet<int, ScriptDef>)DataModule.Get(ScriptDef.OBJECT_NAME) ?? new DataSet<int, ScriptDef>(ScriptDef.OBJECT_NAME);
         dsCommonScriptDefs.Filter = "\"userid\" is null or \"userid\" is not null";
         dsOrgProp = (DataSet<string, OrgProp>)DataModule.Get(OrgProp.OBJECT_NAME) ?? new DataSet<string, OrgProp>(OrgProp.OBJECT_NAME, true);
         grid.DataError += grid_DataError;
      }

      void grid_DataError(object sender, DataGridViewDataErrorEventArgs e)
      {
         
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         data = new SortableBindingList<OrgDataView>();

         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsOrg);
         upd.Add(dsCommonScriptDefs);
         upd.Add(dsOrgProp);

         FmWait.StdDataRefresh(this, upd, DoLoadData);
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
               d.Script = p.script;
            }

            data.Add(d);
         }

         List<ScriptDef> scr = new List<ScriptDef>();

         foreach (ScriptDef sd in dsCommonScriptDefs.Values)
         {
            scr.Add(sd);
         }

         scr.Sort((x, y) => { return x.Name.CompareTo(y.Name); });
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
            string script = edScript.Text.Trim().ToUpper();

            grid.SuspendLayout();

            List<OrgDataView> src = new List<OrgDataView>();

            foreach (OrgDataView d in data)
            {
               if (org.Length > 0 && !d.Name.ToUpper().Contains(org) && !d.Address.ToUpper().Contains(org))
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
         edScript.Text = string.Empty;
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         btnSave.Enabled = !SaveChanges(true);
      }

      private bool SaveChanges(bool showDialog)
      {
         bool ret = false;

         grid.CommitEdit(DataGridViewDataErrorContexts.Commit);

         DataSet<int, OrgProp> ds = new DataSet<int, OrgProp>(OrgProp.OBJECT_NAME, false);

         foreach (OrgDataView d in data)
         {
            if (d.org != null && d.Script != 0)
            {
               OrgProp p = new OrgProp();
               p.id = d.org.id;
               p.script = d.Script;
               p.userid = string.Empty;

               ds.Add(ds.Count, p);
            }
         }

         //List<IDataSet> wrs = new List<IDataSet>();
         //wrs.Add(ds);

         ReplacedSet rs = new ReplacedSet(ds);
         List<ReplacedSet> rls = new List<ReplacedSet>();
         rls.Add(rs);

         ret = DataModule.UpdateDataSet(null, null, rls, Config.GetConfig().GetConnection());

         if (showDialog)
         {
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");
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

      private void FmOrgProp_Load(object sender, EventArgs e)
      {
         btnRefresh.PerformClick();
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
      public int Script { get; set; }
         
      public string Name { 
         get 
         {
            return org == null ? string.Empty : org.name;
         }
      }

      public string Address { get { return org == null ? "" : org.Address; } }
   }
}
