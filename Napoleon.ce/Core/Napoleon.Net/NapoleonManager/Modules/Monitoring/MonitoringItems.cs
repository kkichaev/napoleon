using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public partial class MonitoringItems : Form
   {
      protected List<MonitoringItem> items = new List<MonitoringItem>();
      public MonitoringItems()
      {
         InitializeComponent();

         dgvItems.AutoGenerateColumns = false;

         DataSet<string, MonitoringItem> dsItems;
         dsItems = DataModule.Get(MonitoringItem.OBJECT_NAME) as DataSet<string, MonitoringItem>;
         if( dsItems == null )
            dsItems = new DataSet<string,MonitoringItem>(MonitoringItem.OBJECT_NAME);

         DataModule.OnDataResponceError += new EventDataResponseError(DataModule_OnDataResponceError);
         DataModule.DataProcessed += new EventHandler(DataModule_DataProcessed);
         FmWait.ShowForm(this, DataModule.RefreshDataSet(dsItems, Config.GetConfig().GetConnection(), false, FmWait.ProgressIndicator));
      }

      protected virtual void LoadData()
      {
         DataSet<string, MonitoringItem> dsItems;
         dsItems = DataModule.Get(MonitoringItem.OBJECT_NAME) as DataSet<string, MonitoringItem>;

         items.Clear();
         foreach(MonitoringItem mi in dsItems.Data)
            items.Add(mi);

         SortItems();
         BindingSource bs = new BindingSource();
         bs.DataSource = items;
         dgvItems.DataSource = bs;
         
         ClearDirty();
      }

      protected virtual void SortItems()
      {
         items.Sort();
      }

      void DataModule_DataProcessed(object sender, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         Invoke(new InvokeDelegate(delegate { LoadData(); }));
      }

      void DataModule_OnDataResponceError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
      }

      private void tbDel_Click(object sender, EventArgs e)
      {
         DataGridViewSelectedCellCollection cells = dgvItems.SelectedCells;
         if (cells.Count > 0 && MessageBox.Show("Удалить записи?", "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes)
         {
            BindingSource bs = dgvItems.DataSource as BindingSource;
            List<MonitoringItem> removed = new List<MonitoringItem>();
            foreach (DataGridViewCell c in cells)
            {
               MonitoringItem mi = items[c.RowIndex];
               if (removed.Contains(mi) == false)
                  removed.Add(mi);
            }

            foreach (MonitoringItem m in removed)
               bs.Remove(m);

            MarkDirty();
         }
      }

      private void tbSave_Click(object sender, EventArgs e)
      {
         SaveChanges();
      }

      void MarkDirty()
      {
         tbSave.Enabled = true;
      }

      void ClearDirty()
      {
         tbSave.Enabled = false;
      }

      private void dgvItems_RowsAdded(object sender, DataGridViewRowsAddedEventArgs e)
      {
         MarkDirty();
      }

      void SaveChanges()
      {
         dgvItems.CommitEdit(DataGridViewDataErrorContexts.Commit);
         DataSet<int, MonitoringItem> wr = new DataSet<int, MonitoringItem>(MonitoringItem.OBJECT_NAME, false);
         long key = DateTime.Now.Ticks;

         for (int i = 0; i < items.Count; i++)
         {
            MonitoringItem mi = items[i];
            if (mi.name == null || 
               mi.name.Length == 0)
               continue;

            if(mi.id.Length == 0)
            {
               mi.id = key.ToString();
               key++;
            }

            wr[i] = mi;
            ProcessItem(mi);
         }

         List<ReplacedSet> rs = new List<ReplacedSet>();
         rs.Add(new ReplacedSet(wr));
         if( DataModule.UpdateDataSet(null, null, rs, Config.GetConfig().GetConnection()) )
            ClearDirty();
      }

      protected virtual void ProcessItem(MonitoringItem item){ }

      protected override void OnClosing(CancelEventArgs e)
      {
         if (tbSave.Enabled)
         {
            DialogResult dr = MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
            if (dr == DialogResult.Yes)
               SaveChanges();
            if (dr == DialogResult.Cancel)
               e.Cancel = true;
         }
         base.OnClosing(e);
      }

      private void dgvItems_CellBeginEdit(object sender, DataGridViewCellCancelEventArgs e)
      {
         MarkDirty();
      }

      private void dgvItems_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         if(dgvItems.Columns[dgvItems.CurrentCell.ColumnIndex].HeaderText == clmnOur.HeaderText)
            dgvItems.CommitEdit(DataGridViewDataErrorContexts.Commit);
      }
   }

   public partial class MonitoringItem : GRSoft.Network.DataObject, IComparable<MonitoringItem>
   {
      public static readonly string OBJECT_NAME="MonitoringItem";
      public static readonly int IS_OWN = 1;
      [KeyField]
      public String id = "";

      public String name = "";

      public int flags;

      public String Name { get { return name; } set { name = value; } }

      public bool IsOwn
      {
         get
         {
            return ((flags & IS_OWN) == IS_OWN);
         }
         set
         {
            if (value) flags |= IS_OWN;
            else flags &= (~IS_OWN);
         }
      }

      #region Члены IComparable<MonitoringItem>

      public int CompareTo(MonitoringItem other)
      {
         return name.CompareTo(other.name);
      }

      #endregion
   }

   public class MonitoringVolumeItem : GRSoft.Network.DataObject, IComparable<MonitoringVolumeItem>
   {
      [Precision(2)]
      public double cost = 0;
      [Precision(2)]
      public double volume = 0;

      public int qty = 0;

      #region Члены IComparable<MonitoringVolumeItem>

      public int CompareTo(MonitoringVolumeItem other)
      {
         return (volume < other.volume) ? -1 : (volume > other.volume) ? 1 : 0;
      }

      #endregion
   }

   public class MonitoringDocItem : GRSoft.Network.DataObject
   {
      [ItemType(typeof(MonitoringVolumeItem))]
      public List<MonitoringVolumeItem> items = new List<MonitoringVolumeItem>();

      public int uid;

      public String id;

      [Reference("MonitoringItem", "id")]
      public MonitoringItem mi;

      public int face;
      public int sku;
      public double cost;
   }

   public class Monitoring : BaseDocument
   {
      public static readonly string OBJECT_NAME = "Monitoring";

      public string idquest = string.Empty;

      public string number = "";
      public double sum = 0;

      public string OrgAddr { get { return org == null ? string.Empty : org.Address; } }

      public override double Sum() { return sum; }

      [ItemType(typeof(MonitoringDocItem))]
      public List<MonitoringDocItem> items = null;

      public MonitoringDocItem FindItem(MonitoringItem mi)
      {
         foreach (MonitoringDocItem i in items)
            if (i.id.CompareTo(mi.id) == 0)
               return i;
         return null;
      }
   }
}
