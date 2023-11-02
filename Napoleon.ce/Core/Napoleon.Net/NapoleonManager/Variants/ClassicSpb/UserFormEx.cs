using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using System.Drawing;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   class UserFormEx : UserForm
   {
      public static readonly string CHECK_FOCUSED_ITEMS_KEY = "CheckFocusedItems";

      SimpleDataSet<Dogovor> dsDogovors;
      DataSet<string, AgentDogovors> dsAgentDogovors;

      List<DataGridViewCheckBoxColumn> addColumns = new List<DataGridViewCheckBoxColumn>();
      List<Dogovor> curDogovors = new List<Dogovor>();

      Org emptyOrg = new Org();

      CheckBox cbCheckFocusItems;
      public UserFormEx(Divisions owner):base(owner)
      {
         cbCheckFocusItems = new CheckBox();
         udScript.Controls.Add(cbCheckFocusItems);

         cbCheckFocusItems.Name = "btnEditRoute";
         cbCheckFocusItems.Location = new System.Drawing.Point(2, 6);
         cbCheckFocusItems.Size = new System.Drawing.Size(Size.Width - 4, 18);
         cbCheckFocusItems.Anchor = AnchorStyles.Left | AnchorStyles.Right | AnchorStyles.Top;
         cbCheckFocusItems.TabIndex = 1;
         cbCheckFocusItems.Text = "Проверка фокусного товара в заявках";
         cbCheckFocusItems.CheckStateChanged += new EventHandler(cbCheckFocusItems_CheckStateChanged);

         Size sz = tvScript.Size;
         Point l = tvScript.Location;
         tvScript.Dock = DockStyle.None;
         tvScript.Size = new Size(sz.Width, sz.Height - 25);
         tvScript.Location = new Point(l.X, l.Y + 25);
         tvScript.Anchor = AnchorStyles.Left | AnchorStyles.Right | AnchorStyles.Bottom | AnchorStyles.Top;

         dsDogovors = DataModule.Get(Dogovor.OBJECT_NAME) as SimpleDataSet<Dogovor> ?? 
            new SimpleDataSet<Dogovor>(Dogovor.OBJECT_NAME);

         dsAgentDogovors = new DataSet<string, AgentDogovors>(AgentDogovors.OBJECT_NAME, false);

         dgvOrgs.EditMode = DataGridViewEditMode.EditOnEnter;
         dgvOrgs.CurrentCellDirtyStateChanged += dgvOrgs_CurrentCellDirtyStateChanged;
         //ToolStripItem tsi = owner.tb.Items[Divisions.FOCUSED_ITEM_NAME];
         //if (tsi != null)
         //   tsi.Visible = false;
      }

      SimpleDataSet<AgentDogovors> GetAgentDogovors()
      {
         string userid = Agent.id;

         List<string> added = new List<string>();

         SimpleDataSet<AgentDogovors> ret = new SimpleDataSet<AgentDogovors>(AgentDogovors.OBJECT_NAME, false);
         for (int i = 1; i < dgvOrgs.Rows.Count; i++ )
         {
            DataGridViewRow r = dgvOrgs.Rows[i];
            Org o = r.DataBoundItem as Org;

            for(int j=1; j<dgvOrgs.Columns.Count; j++)
            {
               object val = r.Cells[j].Value;
               if (val != null && ((bool)val))
               {
                  FirmData fd = dgvOrgs.Columns[j].Tag as FirmData;
                  Dogovor dg = FindDogovor(o, fd);

                  if (added.Contains(dg.id))
                     continue;

                  added.Add(dg.id);

                  AgentDogovors adg = new AgentDogovors();
                  adg.id = dg.id;
                  adg.userid = userid;

                  ret.Add(adg);
               }
            }
         }
         return ret;
      }

      bool HaveDogovor(Org o, FirmData firm) { return FindDogovor(o, firm) != null; }

      private Dogovor FindDogovor(Org o, FirmData fd)
      {
         foreach (Dogovor dg in curDogovors)
            if ((dg.idOrg == o.id || dg.idOrg == o.ido) && fd.TestDogovot(dg))
               return dg;

         return null;
      }

      void dgvOrgs_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         DataGridViewCheckBoxCell c = dgvOrgs.CurrentCell as DataGridViewCheckBoxCell;
         if (c != null)
         {
            FirmData fd = dgvOrgs.Columns[dgvOrgs.CurrentCell.ColumnIndex].Tag as FirmData;
            if(dgvOrgs.CurrentRow.Index == 0)
            {
               dgvOrgs.CommitEdit(DataGridViewDataErrorContexts.Commit);
               UpdateDogovors(fd, (bool)c.Value, c.ColumnIndex);
            }
            owner.AddReplacedSet(Agent.id, GetAgentDogovors());
         }
      }

      private void UpdateDogovors(FirmData firm, bool isChecked, int column)
      {
         foreach(DataGridViewRow r in dgvOrgs.Rows)
         {
            Org o = r.DataBoundItem as Org;
            if (o.id.Length == 0)
               continue;

            DataGridViewCell c = r.Cells[column];
            if(!c.ReadOnly)
               c.Value = isChecked;
            //if (HaveDogovor(o, firm))
            //{
            //   r.Cells[column].Value = isChecked;
            //   dgvOrgs.InvalidateCell(column, r.Index);
            //}
         }
      }

      protected override void BeforeUpdateData(string userid, List<IDataSet> updSets)
      {
         if (dsDogovors.Count == 0)
            updSets.Add(dsDogovors);
         dsAgentDogovors.Filter = String.Format("\"userid\" = '{0}'", Agent.id);
         updSets.Add(dsAgentDogovors);
      }

      protected override void FillListOrgs()
      {
         List<FirmData> firms = new List<FirmData>();
         curDogovors.Clear();
         List<string> headOrgs = new List<string>();
         foreach (Org o in dsOrg.Data)
            if (headOrgs.Contains(o.ido) == false)
               headOrgs.Add(o.ido);

         foreach (Dogovor d in dsDogovors.Data)
         {
            if (dsOrg.ContainsKey(d.idOrg) || headOrgs.Contains(d.idOrg))
            {
               curDogovors.Add(d);
               FirmData fd = new FirmData(d.firm, d.bonus);
               if (firms.Contains(fd) == false)
                  firms.Add(fd);
            }
         }
         firms.Sort();

         addColumns.ForEach(x => dgvOrgs.Columns.Remove(x));
         addColumns.Clear();
         foreach (FirmData fd in firms)
         {
            DataGridViewCheckBoxColumn clmn = new DataGridViewCheckBoxColumn();
            clmn.HeaderText = fd.ToString();
            clmn.Width = 80;
            clmn.Tag = fd;
            addColumns.Add(clmn);
            dgvOrgs.Columns.Add(clmn);
         }
         dgvOrgs.Columns[0].Frozen = true;

         List<Org> orgs = new List<Org>();
         foreach (Org o in dsOrg.Data)
            orgs.Add(o);

         orgs.Sort();
         orgs.Insert(0, emptyOrg);
         dgvOrgs.DataSource = orgs;
         dgvOrgs.Rows[0].Frozen = true;

         for(int i=1; i<dgvOrgs.Rows.Count; i++)
         {
            DataGridViewRow r = dgvOrgs.Rows[i];
            Org o = r.DataBoundItem as Org;
            for(int j=0; j<firms.Count; j++)
            {
               DataGridViewCell c = r.Cells[j + 1];
               Dogovor dg = FindDogovor(o, firms[j]);
               if (dg == null)
               {
                  c.ReadOnly = true;
                  c.Style.BackColor = Color.LightGray;
               } else
               {
                  if (dsAgentDogovors.ContainsKey(dg.id))
                     c.Value = true;
               }
            }
         }
      }

      void cbCheckFocusItems_CheckStateChanged(object sender, EventArgs e)
      {
         DataSet<int, CommonConfig> addCfg = new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME, false);
         CommonConfig cfg = new CommonConfig();
         cfg.key = CHECK_FOCUSED_ITEMS_KEY;
         cfg.userid = Agent.id;
         cfg.value = (cbCheckFocusItems.Checked) ? "1" : "0";

         addCfg.Add(0, cfg);
         List<IDataSet> wr = new List<IDataSet>();
         wr.Add(addCfg);

         if (DataModule.UpdateDataSet(wr, null, null, Config.GetConfig().GetConnection()))
         {
            bool finded = false;
            foreach (CommonConfig serverConfig in owner.dsCommonConfig.Data)
            {
               if (serverConfig.userid.Equals(Agent.id) && serverConfig.key.Equals(cfg.key))
               {
                  serverConfig.value = cfg.value;
                  finded = true;
                  break;
               }
            }

            if (!finded)
               owner.dsCommonConfig.Add(owner.dsCommonConfig.Count, cfg);
         }
      }

      public override Agent Agent
      {
         get
         {
            return base.Agent;
         }
         set
         {
            base.Agent = value;

            cbCheckFocusItems.Checked = false;
            foreach (CommonConfig serverConfig in owner.dsCommonConfig.Data)
               if (serverConfig.userid.Equals(value.id) && serverConfig.key.Equals(CHECK_FOCUSED_ITEMS_KEY))
               {
                  int val = 0;
                  int.TryParse(serverConfig.value, out val);
                  cbCheckFocusItems.Checked = (val > 0);

                  break;
               }
         }
      }
   }

   class FirmData : IEquatable<FirmData>, IComparable<FirmData>
   {
      public string name = "";
      public int bonus = 0;

      public FirmData() { }
      public FirmData(string name, int bonus) { this.bonus = bonus; this.name = name; }

      public override string ToString()
      {
         return bonus > 0 ? name + " бонус" : name;
      }

      public bool Equals(FirmData other)
      {
         return name.Equals(other.name) && bonus == other.bonus;
      }

      public int CompareTo(FirmData other)
      {
         int cmp = name.CompareTo(other.name);
         if (cmp != 0)
            return cmp;
         return bonus - other.bonus;
      }

      public bool TestDogovot(Dogovor dog)
      {
         return dog.firm.Equals(name) && dog.bonus == bonus;
      }
   }
}
