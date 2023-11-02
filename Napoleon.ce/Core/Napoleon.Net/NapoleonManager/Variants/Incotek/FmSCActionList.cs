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
   public partial class FmSCActionList : Form
   {
      static FmSCActionList instance = null;

      SimpleDataSet<StorcheckActions> docs = new SimpleDataSet<StorcheckActions>(StorcheckActions.OBJECT_NAME, false);
      SimpleDataSet<StorcheckActions> rmvd = new SimpleDataSet<StorcheckActions>(StorcheckActions.OBJECT_NAME, false);

      public FmSCActionList()
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
            instance = new FmSCActionList();
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
         string where = String.Format("\"date\" >= (select ifnull(max(\"date\"),0) from \"StorcheckActions\" where \"date\" <= ToDate('{0:dd/MM/yyyy}')) and \"date\" < ToDate('{1:dd/MM/yyyy}')", dtpStart.Value.Date, dtpFinish.Value.Date.AddDays(1));
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
         List<StorcheckActions> src = new List<StorcheckActions>();
         foreach (StorcheckActions doc in docs.Data)
            src.Add(doc);
         src.Sort();

         SortableBindingList<StorcheckActions> srcS = new SortableBindingList<StorcheckActions>(src);
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

         StorcheckActions doc = dgvItems.CurrentRow.DataBoundItem as StorcheckActions;
         if (MessageBox.Show("Удалить документ?", "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == System.Windows.Forms.DialogResult.Yes)
         {
            rmvd.Add(doc);

            dgvItems.Rows.Remove(dgvItems.CurrentRow);
            btnSave.Enabled = true;
         }
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         SortableBindingList<StorcheckActions> srcS = (SortableBindingList<StorcheckActions>)dgvItems.DataSource;
         StorcheckActions doc = srcS.AddNew();

         FmSCActionEdit edit = new FmSCActionEdit();
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

         StorcheckActions doc = dgvItems.CurrentRow.DataBoundItem as StorcheckActions;
         FmSCActionEdit edit = new FmSCActionEdit();
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
