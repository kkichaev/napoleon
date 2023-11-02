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
   public partial class FmSCGoodsList : Form
   {
      static FmSCGoodsList instance = null;

      SimpleDataSet<StorcheckGoods> docs = new SimpleDataSet<StorcheckGoods>(StorcheckGoods.OBJECT_NAME, false);
      SimpleDataSet<StorcheckGoods> rmvd = new SimpleDataSet<StorcheckGoods>(StorcheckGoods.OBJECT_NAME, false);

      DataSet<string, Price> price;
      DataSet<string, ManagerFolder> folders;

      public FmSCGoodsList()
      {
         InitializeComponent();

         dgvItems.AutoGenerateColumns = false;
         dtpStart.Value = DateTime.Now.Date;
         dtpFinish.Value = DateTime.Now.Date.AddMonths(1);
      }

      public static void Open()
      {
         if (instance == null)
         {
            instance = new FmSCGoodsList();
            instance.Show();
         }
         else
            instance.BringToFront();
      }

      protected override void OnClosed(EventArgs e)
      {
         instance = null;
         base.OnClosed(e);
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
      }

      private void RefreshData()
      {
         List<IDataSet> upd = new List<IDataSet>();
         if (price == null)
            price = DataModule.Get(Price.OBJECT_NAME) as DataSet<string, Price> ?? new DataSet<string, Price>(Price.OBJECT_NAME);

         if (folders == null)
            folders = DataModule.Get(ManagerFolder.OBJECT_NAME) as DataSet<string, ManagerFolder> ?? new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME);

         if(price.Count == 0)
         {
            price.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
            upd.Add(price);
         }

         if(folders.Count == 0)
         {
            folders.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
            upd.Add(folders);
         }

         string where = String.Format("\"date\" >= (select ifnull(max(\"date\"),0) from \"StorcheckGoods\" where \"date\" <= ToDate('{1:dd/MM/yyyy}')) and \"{0}\" < ToDate('{2:dd/MM/yyyy}')", "date", dtpStart.Value.Date, dtpFinish.Value.Date.AddDays(1));
         docs.Filter = where;
         upd.Add(docs);

         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         base.OnClosing(e);
         if (!CheckChanges())
            e.Cancel = true;
      }

      private void DoLoadData()
      {
         List<StorcheckGoods> src = new List<StorcheckGoods>();
         foreach(StorcheckGoods doc in docs.Data)
            src.Add(doc);
         src.Sort();

         SortableBindingList<StorcheckGoods> srcS = new SortableBindingList<StorcheckGoods>(src);
         dgvItems.DataSource = srcS;
      }

      private void dgvItems_CellDoubleClick(object sender, DataGridViewCellEventArgs e)
      {
         EditCurrent();
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         if (CheckChanges())
            RefreshData();
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

      private bool SaveChanges(bool showDialog)
      {
         List<IDataSet> wr = new List<IDataSet>();
         wr.Add(docs);

         List<IDataSet> rm = new List<IDataSet>();
         rm.Add(rmvd);

         bool ret = DataModule.UpdateDataSet(wr, rm, null, Config.GetConfig().GetConnection());
         if (showDialog)
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");
         if (ret)
            rmvd.Clear();
         return ret;
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         if (dgvItems.CurrentRow == null)
            return;

         StorcheckGoods doc = dgvItems.CurrentRow.DataBoundItem as StorcheckGoods;
         if(MessageBox.Show("Удалить документ?", "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == System.Windows.Forms.DialogResult.Yes)
         {
            rmvd.Add(doc);
            
            dgvItems.Rows.Remove(dgvItems.CurrentRow);
            btnSave.Enabled = true;
         }
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         SortableBindingList<StorcheckGoods> srcS = (SortableBindingList<StorcheckGoods>)dgvItems.DataSource;
         StorcheckGoods doc = srcS.AddNew();

         FmSCGoodsEdit edit = new FmSCGoodsEdit();
         edit.SetDoc(doc);

         if (edit.ShowDialog() == System.Windows.Forms.DialogResult.OK)
         {
            docs.Add(doc);
            dgvItems.InvalidateRow(srcS.IndexOf(doc));
            btnSave.Enabled = true;
         }
         else
            srcS.Remove(doc);
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         EditCurrent();
      }

      private void EditCurrent()
      {
         if (dgvItems.CurrentRow == null)
            return;

         StorcheckGoods doc = dgvItems.CurrentRow.DataBoundItem as StorcheckGoods;
         FmSCGoodsEdit edit = new FmSCGoodsEdit();
         edit.SetDoc(doc);

         if (edit.ShowDialog() == System.Windows.Forms.DialogResult.OK)
         {
            dgvItems.InvalidateRow(dgvItems.CurrentRow.Index);
            btnSave.Enabled = true;
         }

      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         btnSave.Enabled = !SaveChanges(true);
      }
   }
}
