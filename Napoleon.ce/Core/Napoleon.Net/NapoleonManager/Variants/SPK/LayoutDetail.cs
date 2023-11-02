using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public partial class LayoutDetail : UserControl, DataObjectViewer
   {
      //Dictionary<DateTime, LayoutApprove> approved = new Dictionary<DateTime, LayoutApprove>();
      //SimpleDataSet<LayoutActionCause> causes;

      FmDetailEx owner;

      //bool dirty = false;
      Font itemsBoldFont = null;
      int rowIndex = -1, gridRowIndex = -1;
      LayoutApprove refDoc = null;
      static ApproveLog approveLog = null;

      public LayoutDetail()
      {
         InitializeComponent();
         initNotEditingStatus(false);
         approveLog = null;
      }

      //public void RefreshApprove(DataSet<int, LayoutApprove> ds, SimpleDataSet<LayoutActionCause> causes)
      //{
      //   approved.Clear();

      //   foreach (LayoutApprove i in ds.Data)
      //      approved.Add(i.created, i);


      //   this.causes = causes;
      //}

      public void EnableWriteData(bool enable)
      {
         btnSave.Enabled = enable;
      }

      internal void SetOwner(FmDetailEx owner) { this.owner = owner; }

      public int RowIndex { get { return rowIndex; } set { rowIndex = value; } }
      public int GridRowIndex { set { gridRowIndex = value; } }

      public bool IsApproved(Layout doc) {
         return owner.Approved.ContainsKey(doc.created);
      }

      public void SetData(GRSoft.Network.DataObject dataObject )
      {
         GRSoft.NapoleonManager.Layout doc = dataObject as Layout;
         if (doc != null)
         {
            if (owner.Approved.ContainsKey(doc.created))
            {
               //btnSave.Enabled = false;
               //cbType.Enabled = false;
               //cbRemark.Enabled = false;

               refDoc = owner.Approved[doc.created];
            }
            else
            {
               //btnSave.Enabled = true;
               //cbType.Enabled = true;
               //cbRemark.Enabled = true;

               refDoc = new LayoutApprove();
               refDoc.id = doc.id;
               refDoc.created = doc.created;
               refDoc.date = DateTime.Now;
               refDoc.userid = doc.userid;

               doc.items.ForEach(x =>
               {
                  Layout.LayotItem di = new Layout.LayotItem();
                  di.SetFrom(x);
                  refDoc.items.Add(di);
               });
            }
            cbType.SelectedIndexChanged -= cbType_SelectedIndexChanged;
            cbType.SelectedIndex = refDoc.aprType;
            cbType.SelectedIndexChanged += cbType_SelectedIndexChanged;
            cbRemark.Text = refDoc.aprRemark;

            List<Item> data = new List<Item>();
            doc.items.Sort((x, y) => { return cmpLayoutItem(x, y); });

            GroupItem group = null;

            foreach (GRSoft.NapoleonManager.Layout.LayotItem i in doc.items)
            {
               if (group == null || !group.id.Equals(i.grid))
               {
                  group = new GroupItem(this);
                  group.name = i.grname;
                  group.id = i.grid;
                  data.Add(group);
               }

               Item item = new Item(i, refDoc, this);
               data.Add(item);
               group.qty += item.Qty;
               group.chQty += item.Changed;
               group.items.Add(item);
            }

            grid.DataSource = data;
            //dirty = false;
            rowIndex = gridRowIndex;
            Deattach();
         }
         else
         {
            grid.DataSource = new List<Item>();
         }
      }

      public void Deattach()
      {
         initNotEditingStatus(false);
         approveLog = null;
      }

      public bool IsDirty()
      {
         return btnSave.Enabled && IsEditMode(); // dirty;
      }

      public bool IsEditMode() 
      {
         return approveLog != null;
      }

      public void SaveChanges()
      {
         if(refDoc == null)
            return;
         refDoc.aprType = cbType.SelectedIndex;         
         refDoc.aprRemark = cbRemark.Text;
         refDoc.modify = DateTime.Now;

         List<IDataSet> upd = new List<IDataSet>();
         SimpleDataSet<ApproveLog> log = new SimpleDataSet<ApproveLog>(ApproveLog.OBJECT_NAME, false);
         approveLog.committed = DateTime.Now;
         log.Add(approveLog);
         upd.Add(log);

         SimpleDataSet < LayoutApprove > wr = new SimpleDataSet<LayoutApprove>(LayoutApprove.OBJECT_NAME_APPROVE, false);
         wr.Add(refDoc);
         
         ReplacedSet rs = new ReplacedSet(refDoc.userid, wr);
         rs.dontRemove = true;
         Config cfg = Config.GetConfig();

         bool res = DataModule.UpdateDataSet(upd, null, new List<ReplacedSet>(new ReplacedSet[] { rs }), cfg.GetConnection());
         if (res)
         {
            //dirty = false;
            //btnSave.Enabled = false;
            //cbType.Enabled = false;
            //cbRemark.Enabled = false;

            owner.Approved[refDoc.created] = refDoc;

            initNotEditingStatus(false);
            Deattach();
         }
      }

      private void initNotEditingStatus(bool edit)
      {
         btnSave.Text = edit? "Записать" : "Редактировать";
         cbType.Enabled = edit;
         cbRemark.Enabled = edit;
         grid.ReadOnly = !edit;
      }

      public bool CanChange
      {
         get
         {
            return btnSave.Enabled;
         }
      }

      public void ItemChanged()
      {
         bool changed = false;

         //dirty = true;

         List<Item> src = (List<Item>)grid.DataSource;
         foreach(Item i in src)
         {
            GroupItem gi = i as GroupItem;
            if( gi != null)
            {
               gi.Refresh();
               if (gi.QtyChanged)
                  changed = true;
               grid.InvalidateRow(src.IndexOf(i));
            }
         }

         cbType.SelectedIndex = (changed) ? 1 : 0;
      }

      protected override void OnSizeChanged(EventArgs e)
      {
         base.OnSizeChanged(e);
         const int GAP = 4;
         int w = Width - btnSave.Width - GAP * 2;
         cbType.Width = w / 3;
         cbRemark.Width = 2 * w / 3;
         cbType.Left = btnSave.Width + GAP;
         cbRemark.Left = cbType.Left + cbType.Width + GAP;
      }

      int cmpLayoutItem(GRSoft.NapoleonManager.Layout.LayotItem x, GRSoft.NapoleonManager.Layout.LayotItem y)
      {
         int result = x.grpos - y.grpos;

         if (result == 0)
            result = x.itname.CompareTo(y.itname);

         return result;
      }

      class Item
      {
         Layout.LayotItem aprvItem;
         public string name = string.Empty;
         public double qty = 0.0;
         public double chQty = 0.0;
         LayoutDetail owner;
         public string cause;

         public Item(Layout.LayotItem src, LayoutApprove refDoc, LayoutDetail owner)
         {
            this.owner = owner;
            if (src == null)
               return;

            name = src.itname;
            qty = src.qty;
            cause = string.Format("{0} {1}", src.cause, src.remark);

            foreach(Layout.LayotItem i in refDoc.items)
            {
               if(i.itid == src.itid)
               {
                  aprvItem = i;
                  break;
               }
            }
            if(aprvItem == null && owner.CanChange)
            {
               aprvItem = new Layout.LayotItem();
               aprvItem.SetFrom(src);
               refDoc.items.Add(aprvItem);
            }

            chQty = aprvItem.qty;
         }


         public string Name { get { return name; } }
         public double Qty { get { return qty; } }
         public string Cause { get { return cause;  } }
         protected virtual void SetValue(double value)
         {
            if (owner.CanChange)
            {
               aprvItem.qty = value;
               chQty = value;
               owner.ItemChanged();
            }
         }

         public double Changed { get { return chQty; } set { SetValue(value); } }
      }

      class GroupItem : Item
      {
         public string id = string.Empty;
         public List<Item> items = new List<Item>();

         public GroupItem(LayoutDetail owner)
            : base(null, null, owner)
         {

         }

         protected override void SetValue(double value) { }

         public void Refresh()
         {
            chQty = 0;
            foreach (Item i in items)
               chQty += i.Changed;
         }

         public bool QtyChanged 
         {
            get
            {
               foreach (Item i in items)
                  if (i.Changed != i.Qty)
                     return true;
               return false;
            } 
         }
      }

      private void grid_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         if (((DataGridView)sender).Rows[e.RowIndex].DataBoundItem is GroupItem)
         {
            if (itemsBoldFont == null)
               itemsBoldFont = new System.Drawing.Font(e.CellStyle.Font, FontStyle.Bold);
            e.CellStyle.Font = itemsBoldFont;
            e.CellStyle.BackColor = Color.LightGray;
         }
      }

      private void cbType_SelectedIndexChanged(object sender, EventArgs e)
      {
         if (!CanChange || owner.causes == null)
            return;

         //dirty = true;
         cbRemark.Items.Clear();

         int type = cbType.SelectedIndex <= 1 ? 0 : 1;
         foreach(LayoutActionCause lac in owner.causes.Data)
         {
            if (lac.action == type)
               cbRemark.Items.Add(lac.name);
         }
         cbRemark.Text = "";
         cbRemark.Enabled = cbType.SelectedIndex > 0;
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         if (refDoc == null)
            return;
         if (approveLog == null)
         {
            approveLog = new ApproveLog();
            approveLog.aprDate = DateTime.Now;
            approveLog.aprRemark = refDoc.aprRemark;
            approveLog.aprType = refDoc.aprType;
            approveLog.userid = CurrentUser.user.User.id;
            approveLog.id = refDoc.id;
            approveLog.created = refDoc.created;
            approveLog.agentid = refDoc.userid;
            initNotEditingStatus(true);
         }else if(MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes)
            SaveChanges();
      }

      private void cbRemark_TextChanged(object sender, EventArgs e)
      {
         //dirty = true;
      }
   }
}
