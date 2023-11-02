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
   public partial class FmTasktemplateEdit : Form
   {
      static FmTasktemplateEdit instance = null;

      DataSet<string, NapoleonTaskTemplate> dsTask = new DataSet<string, NapoleonTaskTemplate>(NapoleonTaskTemplate.OBJECT_NAME, false);
      DataSet<string, NapoleonTaskTemplate> dsTaskDel = new DataSet<string, NapoleonTaskTemplate>(NapoleonTaskTemplate.OBJECT_NAME, false);

      public FmTasktemplateEdit()
      {
         InitializeComponent();
         dgvItems.AutoGenerateColumns = false;
      }

      public static void Open()
      {
         if (instance == null)
         {
            instance = new FmTasktemplateEdit();
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
         List<IDataSet> updSets = new List<IDataSet>();
         updSets.Add(dsTask);
         FmWait.StdDataRefresh(this, updSets, DoLoadData, btnRefresh);
      }

      void DoLoadData()
      {
         List<NapoleonTaskTemplate> src = new List<NapoleonTaskTemplate>();
         src.AddRange((IEnumerable<NapoleonTaskTemplate>)dsTask.Data);
         src.Sort();
         dgvItems.DataSource = new BindingList<NapoleonTaskTemplate>(src);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
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
         List<IDataSet> del = new List<IDataSet>();

         if (dsTask.Count > 0)
            wr.Add(dsTask);

         if (dsTaskDel.Count > 0)
            del.Add(dsTaskDel);

         bool ret = DataModule.UpdateDataSet(wr, del, null, Config.GetConfig().GetConnection());
         if (showDialog)
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");

         dsTaskDel.Clear();
         return ret;
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         btnSave.Enabled = !SaveChanges(true);
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         if(dgvItems.CurrentRow != null)
         {
            NapoleonTaskTemplate ntt = dgvItems.CurrentRow.DataBoundItem as NapoleonTaskTemplate;

            if (ntt != null && DialogUtil.AskToDel(this))
            {
               dsTaskDel[ntt.id] = ntt;
               dsTask.Remove(ntt.id);
               ((BindingList<NapoleonTaskTemplate>)dgvItems.DataSource).Remove(ntt);
               btnSave.Enabled = true;
            }
         }
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         NapoleonTaskTemplate ntt = new NapoleonTaskTemplate();
         ntt.id = GRSoft.Network.DataObject.GenId();
         ntt.task = "";

         DoEdit(ntt);
      }

      private void DoEdit(NapoleonTaskTemplate ntt)
      {
         FmTaskTextEdit fm = new FmTaskTextEdit();
         fm.Task = ntt.task;

         if( fm.ShowDialog() == System.Windows.Forms.DialogResult.OK)
         {
            ntt.task = fm.Task;
            dsTaskDel.Remove(ntt.id);
            dsTask[ntt.id] = ntt;
            btnSave.Enabled = true;

            BindingList<NapoleonTaskTemplate> src = (BindingList<NapoleonTaskTemplate>)dgvItems.DataSource;
            int idx = src.IndexOf(ntt);
            if (idx < 0)
               src.Add(ntt);
            else
               src.ResetItem(idx);
         }
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         if(dgvItems.CurrentRow != null)
         {
            NapoleonTaskTemplate ntt = dgvItems.CurrentRow.DataBoundItem as NapoleonTaskTemplate;
            if (ntt != null)
               DoEdit(ntt);
         }
      }

      private void dgvItems_CellDoubleClick(object sender, DataGridViewCellEventArgs e)
      {
         NapoleonTaskTemplate ntt = dgvItems.Rows[e.RowIndex].DataBoundItem as NapoleonTaskTemplate;
         if (ntt != null)
            DoEdit(ntt);
      }
   }
}
