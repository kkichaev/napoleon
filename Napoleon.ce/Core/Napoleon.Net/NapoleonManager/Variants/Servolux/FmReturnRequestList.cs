using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmReturnRequestList : Form
   {
      SimpleDataSet<ReturnCause> dsReturnCause = new SimpleDataSet<ReturnCause>(ReturnCause.OBJECT_NAME, false);
      SimpleDataSet<ReturnRequest> dsDocs = new SimpleDataSet<ReturnRequest>(ReturnRequest.OBJECT_NAME, false);

      Dictionary<string, Dictionary<DateTime, Visit>> visits = new Dictionary<string,Dictionary<DateTime,Visit>>();

      DataSet<string, Org> dsOrg;
      DataSet<string, Price> dsPrice;
      SimpleDataSet<Visit> loadedVisit;

      bool canWrite;

      public FmReturnRequestList()
      {
         InitializeComponent();
         dtpBegin.Value = DateTime.Now.Date;
         dtpEnd.Value = DateTime.Now.Date;

         dgvDocs.AutoGenerateColumns = false;
         dgvItems.AutoGenerateColumns = false;
         dsOrg = (DataSet<string, Org>)DataModule.Get(Org.COMMON_OBJECT_NAME) ?? new DataSet<string, Org>(Org.COMMON_OBJECT_NAME);
         dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ?? new DataSet<string, Price>(Price.OBJECT_NAME);
      
         Manager m = CurrentUser.user as Manager;
         canWrite = true; // m.HaveRight(RightTokens.Get("ReturnEditRigth"), RightActions.Write);
         tsbExport.Enabled = canWrite;
      
      }

      public static void Open()
      {
         if (MainForm.Instance.CheckIsMainDataPresents(true) == false)
            return;

         //Manager m = CurrentUser.user as Manager;
         //if (m == null || 
         //   (m.HaveRight(RightTokens.Get("ReturnEditRigth"), RightActions.Write) == false &&
         //     m.HaveRight(RightTokens.Get("ReturnViewRigth"), RightActions.Write) == false))
         //   return;

         FmReturnRequestList form = new FmReturnRequestList();
         form.Show();
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         LoadData();
      }

      private void LoadData()
      {
         List<IDataSet> upd = new List<IDataSet>();
         if (dsOrg.Count == 0)
            upd.Add(dsOrg);

         if (dsPrice.Count == 0)
         {
            dsPrice.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
            upd.Add(dsPrice);
         }

         if (dsReturnCause.Count == 0)
            upd.Add(dsReturnCause);

         dsDocs.Filter = String.Format("\"created\" >= ToDate('{0:dd/MM/yyyy}') and \"created\" <= ToDate('{1:dd/MM/yyyy 23:59:59}')", dtpBegin.Value.Date, dtpEnd.Value.Date);
         upd.Add(dsDocs);

         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      void DoLoadData()
      {
         List<ReturnCause> causeSrc = new List<ReturnCause>();
         foreach (ReturnCause rc in dsReturnCause.Data)
            causeSrc.Add(rc);

         clmnCause.DataSource = causeSrc;
         clmnCause.DisplayMember = "Name";
         clmnCause.ValueMember = "ID";

         clmnSvCause.DataSource = causeSrc;
         clmnSvCause.DisplayMember = "Name";
         clmnSvCause.ValueMember = "ID";

         if (cbAgents.Items.Count == 0)
         {
            List<Agent> agents = new List<Agent>();
            foreach (Agent a in (CurrentUser.user as Manager).GetAgents().Data)
               agents.Add(a);
            agents.Sort();
            cbAgents.Items.Add("<Все>");
            agents.ForEach(x => cbAgents.Items.Add(x));
            cbAgents.SelectedIndex = 0;

            //RefreshDocs(null);
         }
         else
            RefreshDocs(cbAgents.SelectedItem as Agent);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         LoadData();
      }

      void RefreshDocs(Agent a)
      {
         List<ReturnRequest> docs = new List<ReturnRequest>();
         foreach (ReturnRequest rr in dsDocs.Data)
            if (a == null || rr.userid == a.id)
               docs.Add(rr);

         dgvItems.DataSource = null;

         SortableBindingList<ReturnRequest> src = new SortableBindingList<ReturnRequest>(docs);
         dgvDocs.DataSource = src;
      }

      private void cbAgents_SelectedIndexChanged(object sender, EventArgs e)
      {
         RefreshDocs(cbAgents.SelectedItem as Agent);
      }

      Visit FindVisit(ReturnRequest doc)
      {
         Visit ret = null;

         if (visits.ContainsKey(doc.userid))
         {
            Dictionary<DateTime, Visit> docs = visits[doc.userid];
            if (docs.ContainsKey(doc.visitDoc))
               ret = docs[doc.visitDoc];
         }

         return ret;
      }

      private void dgvDocs_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         ReturnRequest doc = dgvDocs.Rows[e.RowIndex].DataBoundItem as ReturnRequest;

         List<RRDocItem> items = new List<RRDocItem>();
         foreach(ReturnRequest.RRItem item in doc.items)
            foreach(ReturnRequest.ReturnDlv dlv in item.items)
               items.Add(new RRDocItem(item, dlv, this, doc.accepted == 0 && canWrite));


         dgvItems.DataSource = new SortableBindingList<RRDocItem>(items);
         foreach (DataGridViewRow r in dgvItems.Rows)
         {
            RRDocItem i = r.DataBoundItem as RRDocItem;
            if(i.item != null && i.item.item != null)
            {
               string type = i.item.item.idType;
               List<ReturnCause> srcCell = new List<ReturnCause>();
               foreach (ReturnCause rc in dsReturnCause.Data)
                  if (rc.idType == type)
                     srcCell.Add(rc);

               DataGridViewComboBoxCell cellCause = (DataGridViewComboBoxCell)r.Cells[clmnCause.DisplayIndex];
               DataGridViewComboBoxCell cellSVCause = (DataGridViewComboBoxCell)r.Cells[clmnSvCause.DisplayIndex];
               cellCause.DataSource = srcCell;
               cellSVCause.DataSource = srcCell;
            }
         }

         Visit v = FindVisit(doc);
         if (v == null)
            LoadVisit(doc);
         else
            RefreshPhoto(v);
      }

      private void LoadVisit(ReturnRequest doc)
      {
         loadedVisit = new SimpleDataSet<Visit>(Visit.OBJECT_NAME, false);
         loadedVisit.Filter = String.Format("\"userid\"='{0}' and \"created\"=ToDate('{1:dd.MM.yyyy HH:mm:ss}')", doc.userid, doc.visitDoc);
         
         List<IDataSet> upd = new List<IDataSet>(new IDataSet[] {loadedVisit});
         FmWait.StdDataRefresh(this, upd, CheckVisit, btnRefresh);
      }

      void CheckVisit()
      {
         if(loadedVisit == null)
            return;

         Visit v = null;
         if (loadedVisit.Count != 0)
            v = loadedVisit[0];
         else
         {
            v = new Visit();
            v.items = new List<Visit.VisitItem>();
         }
         ReturnRequest doc = dgvDocs.CurrentRow.DataBoundItem as ReturnRequest;
         Dictionary<DateTime, Visit> vd = null;
         if (visits.ContainsKey(doc.userid))
            vd = visits[doc.userid];
         else
         {
            vd = new Dictionary<DateTime, Visit>();
            visits.Add(doc.userid, vd);
         }
         vd[doc.visitDoc] = v;
         loadedVisit = null;
         RefreshPhoto(v);
      }

      private void RefreshPhoto(Visit v)
      {
         imPhoto.Images.Clear();
         lvPhoto.Items.Clear();

         foreach (Visit.VisitItem item in v.items)
         {
            if (item.id == null)
               continue;
            try
            {
               MemoryStream stream = new MemoryStream(item.id);
               Image image = new Bitmap(stream);
               
               imPhoto.Images.Add(image);

               String name = item.tag;
               if (dsPrice.ContainsKey(name))
               {
                  Price p = dsPrice[name];
                  name = p.name + " " + p.thermalState + "/" + p.packName;
               }

               ListViewItem lvi = lvPhoto.Items.Add(name);
               lvi.Tag = image;
               lvi.ImageIndex = imPhoto.Images.Count-1;
            } catch(Exception)
            {
            }
         }
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         base.OnClosing(e);
         if (!CheckChanges())
            e.Cancel = true;
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

         return SaveChanges(false, false);
      }

      private bool SaveChanges(bool showDialog, bool forcePut)
      {
         Dictionary<string, SimpleDataSet<ReturnRequest>> sets = new Dictionary<string, SimpleDataSet<ReturnRequest>>();
         foreach (ReturnRequest rdoc in dsDocs.Data)
            if (rdoc.Modified || forcePut)
            {
               SimpleDataSet<ReturnRequest> wrdocs;
               if (sets.ContainsKey(rdoc.userid))
                  wrdocs = sets[rdoc.userid];
               else
               {
                  wrdocs = new SimpleDataSet<ReturnRequest>(ReturnRequest.OBJECT_WR_NAME, false);
                  sets[rdoc.userid] = wrdocs;
               }
               rdoc.accepted = 1;
               rdoc.svChanged = DateTime.Now;
               rdoc.svid = CurrentUser.user.User.id;
               wrdocs.Add(rdoc);
            }

         List<ReplacedSet> wr = new List<ReplacedSet>();
         foreach (KeyValuePair<String, SimpleDataSet<ReturnRequest>> kv in sets)
         {
            ReplacedSet rs = new ReplacedSet(kv.Key, kv.Value);
            rs.dontRemove = true;
            wr.Add(rs);
         }

         bool ret = DataModule.UpdateDataSet(null, null, wr, Config.GetConfig().GetConnection());
         if (ret)
         {
            foreach (KeyValuePair<String, SimpleDataSet<ReturnRequest>> kv in sets)
               foreach (ReturnRequest rdoc in kv.Value.Data)
                  rdoc.Modified = false;

            dgvDocs.Refresh();
         }


         if (showDialog)
         {
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");
         }
         return ret;
      }

      private void tsbSave_Click(object sender, EventArgs e)
      {
         tsbSave.Enabled = !SaveChanges(true, false);
      }

      private void lvPhoto_DoubleClick(object sender, EventArgs e)
      {
         if (lvPhoto.SelectedItems.Count == 0)
            return;
         ListViewItem lvi = lvPhoto.SelectedItems[0];
         Image i = lvi.Tag as Image;
         FmViewPhoto.ShowPhoto(i, "", lvi.Text, "");
      }

      void MarkDirty()
      {
         if( canWrite )
            tsbSave.Enabled = true;
      }

      public void ItemChanged(RRDocItem item)
      {
         // при изменении причины надо обновить все строки с тем же товаром.
         SortableBindingList<RRDocItem> items = (SortableBindingList<RRDocItem>)dgvItems.DataSource;
         for (int i = 0; i < items.Count; i++ )
         {
            if (items[i].item == item.item)
               dgvItems.InvalidateRow(i);
         }
         MarkDirty();
         
         ReturnRequest rdoc = dgvDocs.CurrentRow.DataBoundItem as ReturnRequest;
         rdoc.Modified = true;
         dgvDocs.InvalidateRow(dgvDocs.CurrentRow.Index);
      }

      private void dgvItems_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         int index = dgvItems.CurrentCell.ColumnIndex;
         if (index == clmnChecked.DisplayIndex || index == clmnSvCause.DisplayIndex )
            dgvItems.CommitEdit(DataGridViewDataErrorContexts.Commit);
      }

      private void dgvDocs_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         ReturnRequest rdoc = dgvDocs.Rows[e.RowIndex].DataBoundItem as ReturnRequest;
         DataGridViewCellStyle style = e.CellStyle;
         if (rdoc.Accepted)
            style.ForeColor = Color.DarkGreen;
         else
         {
            style.ForeColor = dgvDocs.DefaultCellStyle.ForeColor;
            style.BackColor = rdoc.Modified ? Color.Orange : dgvDocs.DefaultCellStyle.BackColor;
            style.SelectionBackColor = rdoc.Modified ? Color.DarkGreen : dgvDocs.DefaultCellStyle.SelectionBackColor;
         }
      }

      private void tsbPrint_Click(object sender, EventArgs e)
      {
         (new FmReturnRequestReport()).Show();
      }

      private void dgvItems_DataError(object sender, DataGridViewDataErrorEventArgs e)
      {

      }

      private void lvPhoto_DoubleClick_1(object sender, EventArgs e)
      {
         ListView lv = sender as ListView;
         if(lv != null && lv.SelectedItems.Count > 0)
         {
            ListViewItem lvi = lv.SelectedItems[0];
            Image photo = lvi.Tag as Image;
            if( photo != null)
            {
               FmViewPhoto.ShowPhoto(photo, lvi.Name);
            }
         }
      }

      private void tsbExport_Click(object sender, EventArgs e)
      {
         SaveChanges(true, true);
      }
   }

   public class RRDocItem
   {
      public ReturnRequest.RRItem item;
      ReturnRequest.ReturnDlv dlv;
      FmReturnRequestList owner;
      bool canEdit;

      public RRDocItem(ReturnRequest.RRItem item, ReturnRequest.ReturnDlv dlv, FmReturnRequestList owner, bool canEdit)
      {
         this.item = item;
         this.dlv = dlv;
         this.owner = owner;
         this.canEdit = canEdit;
      }

      public string Item { get { return item.item != null ? item.item.Name + " " + item.item.thermalState + "/" + item.item.packName : "товар с кодом <" + item.id + ">"; } }
      public string DocNumber { get { return dlv.number; } }
      public DateTime DocDate { get { return dlv.date; } }
      public double Qty { get { return dlv.qty; } }
      public string Cause { get { return item.cause; } }
      public string Remark 
      { 
         get { return dlv.remark; }
         set
         {
            if (canEdit)
            {
               dlv.remark = value;
               owner.ItemChanged(this);
            }
         }
      }

      public string CauseSV 
      { 
         get { return item.svCause; } 
         set 
         {
            if (canEdit)
            {
               item.svCause = value;
               owner.ItemChanged(this);
            }
         } 
      }
      public double QtySV
      { 
         get { return dlv.svQty; } 
         set 
         {
            if (canEdit)
            {
               dlv.svQty = value;
               owner.ItemChanged(this);
            }
         } 
      }

      public bool IsAccepted
      {
         get { return dlv.svQty > 0; }
         set
         {
            if (canEdit)
            {
               dlv.svQty = (value) ? dlv.qty : 0;
               owner.ItemChanged(this);
            }
         } 
      }
      
      public DateTime MfrDate { get { return item.mfrDate; } }

   }
}
