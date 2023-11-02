using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;

namespace GRSoft.NapoleonManager
{
   public partial class MinCostEditor : Form
   {
      SimpleDataSet<MinCost> dsMinCost = new SimpleDataSet<MinCost>(MinCost.OBJECT_NAME, false);
      DataSet<string, Org> dsOrgs;
      DataSet<string, Price> dsPrice;
      DataSet<string, Folder> dsFolders;

      Org allMinCostOrg = new Org();

      List<OrgViewItem> allOrgs = new List<OrgViewItem>();
      BindingList<MinCost> source;

      bool clearing = false;

      static MinCostEditor instance = null;
      public MinCostEditor()
      {
         InitializeComponent();

         allMinCostOrg.id = "";
         allMinCostOrg.name = "<Общие мин.цены>";

         dgvOrgs.AutoGenerateColumns = false;
         dgvItems.AutoGenerateColumns = false;

         dsFolders = DataModule.Get(Folder.OBJECT_NAME) as DataSet<string, Folder> ??
            new DataSet<string, Folder>(Folder.OBJECT_NAME);

         dsPrice = DataModule.Get(Price.OBJECT_NAME) as DataSet<string, Price> ??
            new DataSet<string, Price>(Price.OBJECT_NAME);

         dsOrgs = DataModule.Get(Org.COMMON_OBJECT_NAME) as DataSet<string, Org> ??
            new DataSet<string, Org>(Org.COMMON_OBJECT_NAME);
      }

      static public void Open()
      {
         if (instance == null)
         {
            instance = new MinCostEditor();
            instance.Show();
         }
         else
         {
            instance.RefreshData();
            instance.BringToFront();
         }
      }

      protected override void OnClosed(EventArgs e)
      {
         base.OnClosed(e);
         instance = null;
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         base.OnClosing(e);
         if (!CheckChanges())
            e.Cancel = true;
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
      }

      void RefreshData()
      {
         List<IDataSet> upd = new List<IDataSet>();
         if (dsFolders.Count == 0 || dsFolders.Filter != DataUtils.USERID_IS_NULL_STR)
         {
            dsFolders.Filter = DataUtils.USERID_IS_NULL_STR;
            upd.Add(dsFolders);
         }

         if (dsPrice.Count == 0 || dsPrice.Filter != DataUtils.COMMON_PRICE_FILTER_STR)
         {
            dsPrice.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
            upd.Add(dsPrice);
         }

         if (dsOrgs.Count == 0)
            upd.Add(dsOrgs);

         upd.Add(dsMinCost);
         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      void DoLoadData()
      {
         allOrgs.Clear();
         allOrgs.Add(new OrgViewItem(allMinCostOrg, this));
         foreach(Org o in dsOrgs.Data)
            allOrgs.Add(new OrgViewItem(o, this));
         allOrgs.Sort();

         dgvOrgs.DataSource = allOrgs;
      }

      private void tbFind_TextChanged(object sender, EventArgs e)
      {
         timer1.Stop();

         if (tbFind.Text.Length > 0)
            timer1.Start();
         else if (!clearing)
            ClearFind(this, EventArgs.Empty);
      }

      private void ClearFind(object sender, EventArgs e)
      {
         clearing = true;
         tbFind.Clear();

         dgvOrgs.DataSource = allOrgs;

         clearing = false;
      }

      private void timer1_Tick(object sender, EventArgs e)
      {
         timer1.Stop();
         DoSearch();
      }

      void DoSearch()
      {
         String text = tbFind.Text.ToUpper();
         List<OrgViewItem> srch = new List<OrgViewItem>();
         foreach (OrgViewItem ovi in allOrgs)
            if (ovi.Org.ToUpper().Contains(text))
               srch.Add(ovi);

         dgvOrgs.DataSource = srch;
      }

      private void dgvOrgs_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         OrgViewItem ovi = dgvOrgs.Rows[e.RowIndex].DataBoundItem as OrgViewItem;
         if (ovi != null)
         {
            string id = ovi.Id;
            List<MinCost> items = new List<MinCost>();
            foreach (MinCost mc in dsMinCost.Data)
            {
               if (mc.id == id)
                  items.Add(mc);
            }

            source = new BindingList<MinCost>(items);
            dgvItems.DataSource = source;
         }
      }

      bool SourceHaveItem(string id)
      {
         foreach (MinCost mc in source)
            if (mc.id_i == id)
               return true;
         return false;
      }

      private void toolStripButton1_Click(object sender, EventArgs e)
      {
         List<Price> prc = new List<Price>();
         foreach(MinCost mc in source)
            prc.Add(mc.item);

         List<Price> ret = FmSelectSKU.SelectItems(this, prc, null, true);
         if (ret != null)
         {
            OrgViewItem ovi = dgvOrgs.CurrentRow.DataBoundItem as OrgViewItem;
            if (ovi == null)
               return;
            
            string id = ovi.Id;
            ret.ForEach((x) =>
            {
               if (SourceHaveItem(x.id) == false)
               {
                  MinCost mc = source.AddNew();
                  mc.id = id;
                  mc.id_i = x.id;
                  mc.item = x;
                  mc.minCost = 0;
               }
            });

            List<MinCost> rmv = new List<MinCost>();
            foreach (MinCost mc in source)
               if (ret.Contains(mc.item) == false)
                  rmv.Add(mc);
            rmv.ForEach(x => source.Remove(x));

            SyncItems();
         }
      }

      private void SyncItems()
      {
         OrgViewItem ovi = dgvOrgs.CurrentRow.DataBoundItem as OrgViewItem;
         if (ovi == null)
            return;

         string id = ovi.Id;
         List<int> removed = new List<int>();
         foreach (KeyValuePair<int, MinCost> kv in dsMinCost)
            if (kv.Value.id == id)
               removed.Add(kv.Key);

         removed.ForEach(x => dsMinCost.Remove(x));

         foreach(MinCost mc in source)
            dsMinCost.Add(mc);

         tsbSave.Enabled = true;
      }

      private void dgvItems_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         if (dgvItems.CurrentCell.ColumnIndex == clmnCost.DisplayIndex)
            tsbSave.Enabled = true;
      }

      bool CheckChanges()
      {
         if (!tsbSave.Enabled)
            return true;

         DialogResult dr = MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
         if (dr == DialogResult.No)
            return true;
         if (dr == DialogResult.Cancel)
            return false;

         return SaveChanges(false);
      }

      private bool SaveChanges(bool showDialog)
      {
         dgvItems.CommitEdit(DataGridViewDataErrorContexts.Commit);
         List<ReplacedSet> rpl = new List<ReplacedSet>(new ReplacedSet[] { new ReplacedSet(dsMinCost) });
         bool ret = DataModule.UpdateDataSet(null, null, rpl, Config.GetConfig().GetConnection());
         if (showDialog)
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");

         return ret;
      }

      private void tsbSave_Click(object sender, EventArgs e)
      {
         tsbSave.Enabled = !SaveChanges(true);
      }

      private void tsbRemove_Click(object sender, EventArgs e)
      {
         foreach (DataGridViewRow row in dgvItems.SelectedRows)
            source.Remove(row.DataBoundItem as MinCost);

         SyncItems();
      }

      private void dgvOrgs_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         OrgViewItem oi = dgvOrgs.Rows[e.RowIndex].DataBoundItem as OrgViewItem;
         if (oi != null)
         {
            Color fore = OrgHaveItems(oi.Id) ? Color.Red : dgvOrgs.DefaultCellStyle.ForeColor;
            e.CellStyle.ForeColor = fore;
         }
      }

      internal bool OrgHaveItems(string id)
      {
         foreach (MinCost mc in dsMinCost.Data)
            if (mc.id == id)
               return true;
         return false;
      }
   }

   class OrgViewItem : IComparable<OrgViewItem>
   {
      Org org;
      MinCostEditor owner;

      public OrgViewItem(Org o, MinCostEditor owner)
      {
         org = o;
         this.owner = owner;
      }

      public string Org { get { return org.Name; } }
      public string Id { get { return org.id; } } 

      #region IComparable<OrgViewItem> Members

      int IComparable<OrgViewItem>.CompareTo(OrgViewItem other)
      {
         bool iHaveItem = owner.OrgHaveItems(org.id);
         bool oHaveItem = owner.OrgHaveItems(other.Id);
         if (iHaveItem != oHaveItem)
            return iHaveItem ? -1 : 1;
         return org.name.CompareTo(other.org.name);
      }

      #endregion
   }

   class MinCost : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "MinCost";

      public string id = "";
      
      public string id_i = "";
      [Reference("ManagerPrice", "id_i")]
      public Price item;

      public double minCost = 0;

      public string Item { get { return item == null ? "Товар с кодом <" + id + ">" : item.Name; } }
      public double Cost { get { return minCost; } set { minCost = value; } }
   }
}
