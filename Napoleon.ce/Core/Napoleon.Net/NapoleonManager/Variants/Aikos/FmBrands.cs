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
   public partial class FmBrands : Form
   {
      static FmBrands instance = null;

      SimpleDataSet<Brand> brands = new SimpleDataSet<Brand>(Brand.OBJECT_NAME, false);

      public static void Open()
      {
         if(instance == null)
         {
            instance = new FmBrands();
            instance.Show();
         }
         else
         {
            instance.BringToFront();
         }
      }

      public FmBrands()
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
         src.Add(brands);
         FmWait.StdDataRefresh(this, src, LoadData);
      }

      private void LoadData()
      {
         List<Brand> src = new List<Brand>();
         foreach (Brand i in brands.Data)
            src.Add(i);

         src.Sort();

         dgvItems.DataSource = new BindingList<Brand>(src);
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
         SimpleDataSet<Brand> wrset = new SimpleDataSet<Brand>(Brand.OBJECT_NAME, false);

         int pos = 0;
         foreach (Brand ri in (BindingList<Brand>)dgvItems.DataSource)
         {
            ri.pos = pos++;
            wrset.Add(ri);
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
         BindingList<Brand> src = (BindingList<Brand>)dgvItems.DataSource;
         Brand pt = src.AddNew();
         pt.id = Guid.NewGuid().ToString().Replace("-", "");
         pt.pos = src.Count;
         SetDirty();
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         BindingList<Brand> src = (BindingList<Brand>)dgvItems.DataSource;
         DataGridViewRow r = dgvItems.CurrentRow;
         if (r != null)
         {
            src.Remove((Brand)r.DataBoundItem);
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
         tsbSave.Enabled = true;
      }
   }
}
