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
   public partial class FmFocusMatrix : Form
   {
      DataSet<string, OrgType> types;
      DataSet<string, Price> dsPrice;
      DataSet<string, ManagerFolder> dsFolders;
      DataSet<string, FocusMatrix> dsMatrix;

      public FmFocusMatrix()
      {
         InitializeComponent();

         dgvTypes.AutoGenerateColumns = false;
         dgvItems.AutoGenerateColumns = false;

         dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ??
            new DataSet<string, Price>(Price.OBJECT_NAME);

         dsFolders = (DataSet<string, ManagerFolder>)DataModule.Get(ManagerFolder.OBJECT_NAME) ??
            new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME);

         types = (DataSet<string, OrgType>)DataModule.Get(OrgType.OBJECT_NAME) ??
            new DataSet<string, OrgType>(OrgType.OBJECT_NAME, true);

         dsMatrix = new DataSet<string, FocusMatrix>(FocusMatrix.OBJECT_NAME, false);
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
      }

      void RefreshData()
      {
         List<IDataSet> upd = new List<IDataSet>();
         if(dsPrice.Count == 0)
         {
            dsPrice.Filter = Utils.DataUtils.COMMON_PRICE_FILTER_STR;
            upd.Add(dsPrice);
         }

         if(dsFolders.Count == 0)
         {
            dsFolders.Filter = Utils.DataUtils.COMMON_PRICE_FILTER_STR;
            upd.Add(dsFolders);
         }
         upd.Add(types);
         upd.Add(dsMatrix);

         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      void DoLoadData()
      {
         List<FocusMatrix> src = new List<FocusMatrix>();
         foreach(OrgType ot in types.Data)
         {
            if(dsMatrix.ContainsKey(ot.id))
            {
               src.Add(dsMatrix[ot.id]);
            }
            else
            {
               FocusMatrix fm = new FocusMatrix();
               fm.type = ot.id;
               fm.orgType = ot;
               dsMatrix.Add(ot.id, fm);
               src.Add(fm);
            }
         }

         dgvTypes.DataSource = new SortableBindingList<FocusMatrix>(src);
         //dgvItems.DataSource = null;
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

      private void dgvTypes_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         FocusMatrix fm = dgvTypes.Rows[e.RowIndex].DataBoundItem as FocusMatrix;
         dgvItems.DataSource = fm.items;
      }

      bool SaveChanges(bool showDialog)
      {
         List<ReplacedSet> rpl = new List<ReplacedSet>();
         SimpleDataSet<FocusMatrix> upd = new SimpleDataSet<FocusMatrix>(FocusMatrix.OBJECT_NAME, false);
         SortableBindingList<FocusMatrix> src = (SortableBindingList<FocusMatrix>)dgvTypes.DataSource;
         foreach (FocusMatrix i in src)
         {
            if(i.items.Count > 0)
               upd.Add(i);
         }

         rpl.Add(new ReplacedSet(upd));
         bool ret = DataModule.UpdateDataSet(null, null, rpl, Config.GetConfig().GetConnection());
         if (showDialog)
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи");

         return ret;
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         btnSave.Enabled = !SaveChanges(true);
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         if (dgvItems.CurrentRow != null)
         {
            FocusMatrix fm = dgvTypes.CurrentRow.DataBoundItem as FocusMatrix;
            FocusMatrix.Item fi = dgvItems.CurrentRow.DataBoundItem as FocusMatrix.Item;
            fm.items.Remove(fi);
            dgvItems.DataSource = null;
            dgvItems.DataSource = fm.items;

            btnSave.Enabled = true;
         }
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         if (dgvTypes.CurrentRow == null)
            return;

         FocusMatrix fm = dgvTypes.CurrentRow.DataBoundItem as FocusMatrix;
         List<Price> selected = new List<Price>();
         foreach (FocusMatrix.Item fi in fm.items)
            if (fi.price != null)
               selected.Add(fi.price);

         selected = FmSelectSKU.SelectItems(this, selected, null, true);
         if (selected == null)
            return;

         List<FocusMatrix.Item> items = new List<FocusMatrix.Item>();
         foreach(Price p in selected)
         {
            FocusMatrix.Item i = new FocusMatrix.Item();
            i.id = p.id;
            i.price = p;
            items.Add(i);
         }
         fm.items = items;
         dgvItems.DataSource = items;

         btnSave.Enabled = true;
      }
   }
}
