using GRSoft.Network;
using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmSuppl : Form
   {
      static FmSuppl instance = null;

      SimpleDataSet<Supplier> suppliers = new SimpleDataSet<Supplier>(Supplier.OBJECT_NAME, false);

      public static void Open()
      {
         if (instance == null)
         {
            instance = new FmSuppl();
            instance.Show();
         }
         else
         {
            instance.BringToFront();
         }
      }

      public FmSuppl()
      {
         InitializeComponent();
         dgvItems.AutoGenerateColumns = false;
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         Loading();
      }

      void Loading()
      {
         List<IDataSet> src = new List<IDataSet>();
         src.Add(suppliers);
         FmWait.StdDataRefresh(this, src, LoadData);
      }

      class SupplierView : IComparable<SupplierView>
      {
         Supplier src;
         FmSuppl owner;

         public SupplierView(Supplier src, FmSuppl owner)
         {
            this.src = src;
            this.owner = owner;
         }

         public SupplierView()
         {
            src = new Supplier();
         }

         public Supplier Src { get { return src; } }

         public string Name { get { return src.name; } set { src.name = value; } }
         public bool Aikos
         {
            get { return src.aikos != 0; }
            set
            {
               int nw = value ? 1 : 0;
               if(src.aikos != nw)
               {
                  owner.UpdateAikos(src, nw);
               }
            }
         }

         public int CompareTo(SupplierView other)
         {
            return src.pos - other.src.pos;
         }
      }

      public void UpdateAikos(Supplier src, int val)
      {
         src.aikos = val;
         if (val != 0)
         {
            foreach(Supplier s in suppliers.Data)
            {
               if (s == src)
                  continue;
               s.aikos = 0;
            }
            dgvItems.InvalidateColumn(1);
            tsbSave.Enabled = true;
         }
      }

      private void LoadData()
      {
         List<SupplierView> src = new List<SupplierView>();
         foreach (Supplier i in suppliers.Data)
            src.Add(new SupplierView(i, this));

         src.Sort();

         dgvItems.DataSource = new BindingList<SupplierView>(src);
         tsbSave.Enabled = false;
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         base.OnClosing(e);
         if (!CheckChanges())
            e.Cancel = true;
         else
            instance = null;
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
         CommitGridChages();
         SimpleDataSet<Supplier> wrset = new SimpleDataSet<Supplier>(Supplier.OBJECT_NAME, false);

         int pos = 0;
         foreach (SupplierView ri in (BindingList<SupplierView>)dgvItems.DataSource)
         {
            ri.Src.pos = pos++;
            wrset.Add(ri.Src);
         }

         ReplacedSet rs = new ReplacedSet(wrset);
         List<ReplacedSet> rplSet = new List<ReplacedSet>();
         rplSet.Add(rs);


         bool ret = DataModule.UpdateDataSet(null, null, rplSet, Config.GetConfig().GetConnection());
         if (showDialog)
         {
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");
         }
         return ret;
      }

      public void SetDirty()
      {
         tsbSave.Enabled = true;
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         BindingList<SupplierView> src = (BindingList<SupplierView>)dgvItems.DataSource;
         SupplierView pt = src.AddNew();
         Supplier s = pt.Src;
         s.id = Guid.NewGuid().ToString().Replace("-", "");
         s.pos = src.Count;
         SetDirty();
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         BindingList<SupplierView> src = (BindingList<SupplierView>)dgvItems.DataSource;
         DataGridViewRow r = dgvItems.CurrentRow;
         if (r != null)
         {
            src.Remove((SupplierView)r.DataBoundItem);
            SetDirty();
         }
      }

      void ShiftCurItem(DataGridView gr, bool up)
      {
         IList src = (IList)gr.DataSource;
         DataGridViewRow cur = gr.CurrentRow;
         if (cur == null)
            return;

         int index = cur.Index;
         System.Object el = cur.DataBoundItem;

         if (up)
         {
            if (index == 0)
               return;
            index--;
         }
         else
         {
            if (index >= src.Count - 1)
               return;
            index++;
         }

         src.Remove(el);
         src.Insert(index, el);
         gr.CurrentCell = gr.Rows[index].Cells[0];

         SetDirty();
      }

      private void tsbUp_Click(object sender, EventArgs e)
      {
         ShiftCurItem(dgvItems, true);
      }

      private void tsbDn_Click(object sender, EventArgs e)
      {
         ShiftCurItem(dgvItems, false);
      }

      private void tsbSave_Click_1(object sender, EventArgs e)
      {
         if (SaveChanges(true))
            tsbSave.Enabled = false;
      }

      private void CommitGridChages()
      {
         dgvItems.CommitEdit(DataGridViewDataErrorContexts.Commit);
         dgvItems.EndEdit();
      }

      private void dgvItems_CellEndEdit(object sender, DataGridViewCellEventArgs e)
      {
         if(e.ColumnIndex == 0)
            tsbSave.Enabled = true;
      }
   }
}
