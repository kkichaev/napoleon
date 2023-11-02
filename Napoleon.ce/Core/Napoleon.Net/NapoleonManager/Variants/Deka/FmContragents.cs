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
   public partial class FmContragents : Form
   {
      DataSet<string, Org> orgs;
      DataSet<string, Org> oldOrgs = new DataSet<string, Org>("OldOrgs");
      DataSet<string, Org> changed = new DataSet<string,Org>(Org.COMMON_OBJECT_NAME, false);
      DataSet<string, Org> removed = new DataSet<string, Org>(Org.COMMON_OBJECT_NAME, false);
      List<OrgRow> allOrgs = new List<OrgRow>();

      public FmContragents()
      {
         InitializeComponent();

         dgvOrgs.AutoGenerateColumns = false;

         orgs = DataModule.Get(Org.COMMON_OBJECT_NAME) as DataSet<string, Org> ??
            new DataSet<string, Org>(Org.COMMON_OBJECT_NAME);
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
      }

      void RefreshData()
      {
         List<IDataSet> updSet = new List<IDataSet>();

         updSet.Add(orgs);
         FmWait.StdDataRefresh(this, updSet, DoLoadData);
      }

      void DoLoadData()
      {
         changed.Clear();
         removed.Clear();
         btnSave.Enabled = false;

         allOrgs = new List<OrgRow>();
         foreach(Org o in orgs.Data)
            allOrgs.Add(new OrgRow(o, this));

         allOrgs.Sort();
         dgvOrgs.DataSource = new SortableBindingList<OrgRow>(allOrgs);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         RefreshData();
      }

      public void SetDirty(Org org)
      {
         if (!changed.ContainsKey(org.id))
            changed.Add(org.id, org);

         btnSave.Enabled = true;
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         base.OnClosing(e);
         if (!CheckChanges())
            e.Cancel = true;
      }

      bool CheckChanges()
      {
         if (!btnSave.Enabled)
            return true;

         DialogResult dr = MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
         if (dr == DialogResult.No)
            return true;
         if (dr == DialogResult.Cancel)
            return false;

         return SaveChanges(false);
      }

      bool SaveChanges(bool showDialog)
      {
         dgvOrgs.CommitEdit(DataGridViewDataErrorContexts.Commit);

         List<IDataSet> wrSet = new List<IDataSet>();

         if(changed.Count > 0)
            wrSet.Add(changed);

         //List<IDataSet> rmvSet = new List<IDataSet>();
         //if(removed.Count > 0)
         //   rmvSet.Add(removed);
         //bool result = DataModule.UpdateDataSet(wrSet, rmvSet, null, Config.GetConfig().GetConnection());

         bool result = DataModule.UpdateDataSet(wrSet, null, null, Config.GetConfig().GetConnection());
         if (!result && showDialog)
            MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);

         if(result)
         {
            changed.Clear();
            removed.Clear();
         }

         return result;
      }

      class OrgRow : IComparable<OrgRow>
      {
         public Org org;
         FmContragents owner;

         public OrgRow(Org o, FmContragents owner)
         {
            this.org = o;
            this.owner = owner;
         }

         public OrgRow()
         {
            org = new Org();
            org.id = Guid.NewGuid().ToString().Replace("-", "");
         }

         public Org Org { get { return org; } }

         public void SetOwner(FmContragents owner)
         {
            this.owner = owner;
         }

         public string Name
         {
            get { return org.name; }
            set
            {
               org.name = value;
               owner.SetDirty(org);
            }
         }

         public string Address
         {
            get { return org.address; }
            set
            {
               org.address = value;
               owner.SetDirty(org);
            }
         }

         public string FIO
         {
            get
            {
               return org.contacts.Count == 0 ? "" : org.contacts[0].name;
            }

            set
            {
               if (org.contacts.Count == 0)
                  org.contacts.Add(new Org.Contact(org.id));
               org.contacts[0].name = value;
               owner.SetDirty(org);
            }
         }

         public string Phone
         {
            get
            {
               return org.contacts.Count == 0 ? "" : org.contacts[0].phone;
            }

            set
            {
               if (org.contacts.Count == 0)
                  org.contacts.Add(new Org.Contact(org.id));
               org.contacts[0].phone = value;
               owner.SetDirty(org);
            }
         }

         public int CompareTo(OrgRow other)
         {
            return Name.CompareTo(other.Name);
         }

         public string Agent
         {
            get
            {
               return org.userid;
            }

            set
            {
               org.userid = value;
               owner.SetDirty(org);
            }
         }
      }

      private void tsbAdd_Click(object sender, EventArgs e)
      {
         SortableBindingList<OrgRow> src = dgvOrgs.DataSource as SortableBindingList<OrgRow>;
         
         OrgRow or = src.AddNew();
         or.SetOwner(this);
         int i = src.IndexOf(or);
         if(i >= 0)
         {
            dgvOrgs.CurrentCell = dgvOrgs.Rows[i].Cells[clmnName.DisplayIndex];
            dgvOrgs.BeginEdit(true);
         }
         SetDirty(or.org);
      }

      private void tsbDel_Click(object sender, EventArgs e)
      {
         //SortableBindingList<OrgRow> src = dgvOrgs.DataSource as SortableBindingList<OrgRow>;

         //List<OrgRow> rows = new List<OrgRow>();
         //foreach (DataGridViewCell c in dgvOrgs.SelectedCells)
         //{
         //   OrgRow rd = dgvOrgs.Rows[c.RowIndex].DataBoundItem as OrgRow;
         //   if (!rows.Contains(rd))
         //   {
         //      if(!removed.ContainsKey(rd.org.id))
         //         removed.Add(rd.org.id, rd.org);

         //      rows.Add(rd);
         //   }
         //}

         //rows.ForEach(x => src.Remove(x));

         //btnSave.Enabled = true;
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         btnSave.Enabled = !SaveChanges(true);
      }

      private void FmContragents_Load(object sender, EventArgs e)
      {
         List<Agent> list = new List<Agent>();
         Manager dm = CurrentUser.user as Manager;
         list.Add(new Agent());
         foreach (Agent a in dm.GetAgents().Data)
            list.Add(a);

         clmnAgent.DataSource = list;
         clmnAgent.DisplayMember = "Name";
         clmnAgent.ValueMember = "ID";
      }

      private void btnLoadOldOrgs_Click(object sender, EventArgs e)
      {
         List<IDataSet> updSet = new List<IDataSet>();

         updSet.Add(oldOrgs);
         FmWait.StdDataRefresh(this, updSet, DoLoadOldOrgs);
      }

      private void DoLoadOldOrgs()
      {
         bool added = false;
         foreach (Org o in oldOrgs.Values)
         {
            if (!orgs.ContainsKey(o.id))
            {
               orgs[o.id] = o;

               if (!added)
                  added = true;
            }
         }

         if (added)
         {
            DoLoadData();
            btnSave.Enabled = true;
         }
      }

      bool clearing = false;
      private void tbFind_TextChanged(object sender, EventArgs e)
      {
         timer1.Stop();

         if (tbFind.Text.Length > 0)
            timer1.Start();
         else if (!clearing)
            ClearFind();
      }

      private void ClearFind()
      {
         clearing = true;
         tbFind.Text = "";

         dgvOrgs.DataSource = allOrgs;

         clearing = false;
      }

      private void timer1_Tick(object sender, EventArgs e)
      {
         timer1.Stop();
         DoSearch(tbFind.Text);
      }

      private void DoSearch(string str)
      {
         str = str.ToUpper();

         List<OrgRow> src = new List<OrgRow>();
         foreach (OrgRow o in allOrgs)
            if (o.Name.ToUpper().Contains(str) || o.Address.ToUpper().Contains(str))
               src.Add(o);

         dgvOrgs.DataSource = new SortableBindingList<OrgRow>(src);
      }

      private void tbClearFind_Click(object sender, EventArgs e)
      {
         ClearFind();
      }

      private void btnCopy_Click(object sender, EventArgs e)
      {
         dgvOrgs.SelectAll();
         System.Windows.Forms.DataObject dobj = dgvOrgs.GetClipboardContent();
         System.Windows.Forms.DataObject dest = new System.Windows.Forms.DataObject("UnicodeText", dobj.GetData("UnicodeText"));

         Clipboard.SetDataObject(dest);
      }

      private void dgvOrgs_DataError(object sender, DataGridViewDataErrorEventArgs e)
      {

      }
   }
}
