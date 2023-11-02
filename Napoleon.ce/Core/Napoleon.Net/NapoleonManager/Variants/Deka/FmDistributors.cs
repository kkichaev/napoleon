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
   public partial class FmDistributors : Form
   {
      SimpleDataSet<Distributor> distribs;

      public FmDistributors()
      {
         InitializeComponent();
         dgvItems.AutoGenerateColumns = false;
         distribs = DataModule.Get(Distributor.OBJECT_NAME) as SimpleDataSet<Distributor> ?? 
            new SimpleDataSet<Distributor>(Distributor.OBJECT_NAME, false);
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
      }

      void RefreshData()
      {
         List<IDataSet> updSet = new List<IDataSet>();

         updSet.Add(distribs);
         FmWait.StdDataRefresh(this, updSet, DoLoadData);
      }

      void DoLoadData()
      {
         List<RowData> src = new List<RowData>();
         foreach (Distributor d in distribs.Data)
            src.Add(new RowData(d, this));

         src.Sort();
         dgvItems.DataSource = new SortableBindingList<RowData>(src);
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
         dgvItems.CommitEdit(DataGridViewDataErrorContexts.Commit);

         List<ReplacedSet> rpcSet = new List<ReplacedSet>();

         distribs.Clear();
         SortableBindingList<RowData> src = (SortableBindingList<RowData>)dgvItems.DataSource;
         foreach (RowData or in src)
         {
            Distributor o = or.Data;
            if (o.name.Trim().Length > 0)
               distribs.Add(o);
         }

         rpcSet.Add(new ReplacedSet(distribs));

         bool result = DataModule.UpdateDataSet(null, null, rpcSet, Config.GetConfig().GetConnection());
         if (!result && showDialog)
            MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);

         return result;
      }

      public class RowData : IComparable<RowData>
      {
         Distributor dstr;
         FmDistributors owner;

         public RowData(Distributor d, FmDistributors owner)
         {
            this.dstr = d;
            this.owner = owner;
         }

         public RowData()
         {
            dstr = new Distributor();
            dstr.id = Guid.NewGuid().ToString().Replace("-", "");
         }

         public void SetOwner(FmDistributors owner) { this.owner = owner; }

         public int CompareTo(RowData other)
         {
            return dstr.name.CompareTo(other.dstr.name);
         }

         public string Name
         {
            get { return dstr.name; }
            set
            {
               dstr.name = value;
               owner.SetDirty(true);
            }
         }

         public Distributor Data { get { return dstr; } }

         public double Disc 
         {
            get { return dstr.disc; }
            set
            {
               dstr.disc = value;
               owner.SetDirty(true);
            }
         }
      }

      internal void SetDirty(bool dirty)
      {
         btnSave.Enabled = dirty;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         RefreshData();
      }

      private void tsbAdd_Click(object sender, EventArgs e)
      {
         SortableBindingList<RowData> src = (SortableBindingList<RowData>)dgvItems.DataSource;
         RowData rd = src.AddNew();
         rd.SetOwner(this);

         int i = src.IndexOf(rd);
         if(i >= 0)
         {
            dgvItems.CurrentCell = dgvItems.Rows[i].Cells[clmnName.DisplayIndex];
            dgvItems.BeginEdit(true);
         }

         SetDirty(true);
      }

      private void tsbDel_Click(object sender, EventArgs e)
      {
         List<RowData> removed = new List<RowData>();
         foreach (DataGridViewCell c in dgvItems.SelectedCells)
         {
            RowData rd = dgvItems.Rows[c.RowIndex].DataBoundItem as RowData;
            if (!removed.Contains(rd))
               removed.Add(rd);
         }

         SortableBindingList<RowData> src = (SortableBindingList<RowData>)dgvItems.DataSource;
         removed.ForEach(x => src.Remove(x));

         SetDirty(true);
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         btnSave.Enabled = !SaveChanges(true);
      }
   }
}
