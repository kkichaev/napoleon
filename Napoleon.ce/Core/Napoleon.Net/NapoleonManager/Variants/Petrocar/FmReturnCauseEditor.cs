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
   public partial class FmReturnCauseEditor : Form
   {
      static readonly string CAUSE_KEY = "ПричиныВозврата";

      public FmReturnCauseEditor()
      {
         InitializeComponent();
         dgvItems.AutoGenerateColumns = false;

      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         LoadData();
      }

      private void LoadData()
      {
         List<IDataSet> sets = new List<IDataSet>();
         SimpleDataSet<CommonConfig> cfg = new SimpleDataSet<CommonConfig>(CommonConfig.OBJECT_NAME, false);
         sets.Add(cfg);

         DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), sets, null).Join();

         List<DataItem> src = new List<DataItem>();
         foreach (CommonConfig cc in cfg.Data)
         {
            if (cc.key == CAUSE_KEY)
            {
               string[] vals = cc.value.Split(new char[] { ';' });
               foreach (string v in vals)
               {
                  src.Add(new DataItem(v));
               }
            }
         }

         dgvItems.DataSource = new BindingList<DataItem>(src);
         tsbSave.Enabled = false;
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

         return SaveChanges(false);
      }

      private bool SaveChanges(bool showDialog)
      {
         dgvItems.CommitEdit(DataGridViewDataErrorContexts.Commit);

         SimpleDataSet<CommonConfig> wrset = new SimpleDataSet<CommonConfig>(CommonConfig.OBJECT_NAME, false);
         CommonConfig val = new CommonConfig();
         val.key = CAUSE_KEY;
         foreach (DataItem di in (BindingList<DataItem>)dgvItems.DataSource)
         {
            val.value += di.Name + "\t" + di.ID + ";";
         }
         if (val.value.Length > 0)
            val.value = val.value.Substring(0, val.value.Length - 1);
         wrset.Add(val);

         List<IDataSet> wr = new List<IDataSet>();
         wr.Add(wrset);

         bool ret = DataModule.UpdateDataSet(wr, null, null, Config.GetConfig().GetConnection());


         if (showDialog)
         {
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");
         }
         return ret;
      }

      private void tsbSave_Click(object sender, EventArgs e)
      {
         tsbSave.Enabled = !SaveChanges(true);
      }

      public void SetDirty()
      {
         tsbSave.Enabled = true;
      }

      private void tsbAdd_Click(object sender, EventArgs e)
      {
         DataItem newItem = ((BindingList<DataItem>)dgvItems.DataSource).AddNew();
         newItem.ID = Guid.NewGuid().ToString().Replace("-", "");
         SetDirty();
      }

      private void tsbRemove_Click(object sender, EventArgs e)
      {
         if (dgvItems.CurrentRow == null)
            return;

         DataItem rc = (DataItem)dgvItems.CurrentRow.DataBoundItem;
         ((BindingList<DataItem>)dgvItems.DataSource).Remove(rc);
         SetDirty();
      }

      private void dgvItems_CellEndEdit(object sender, DataGridViewCellEventArgs e)
      {
         SetDirty();
      }

      class DataItem : IComparable<DataItem>
      {
         string key;
         string objvalue;

         public DataItem()
         {
            key = "";
            objvalue = "";
         }

         public DataItem(string src)
         {
            string[] vals = src.Split(new char[] { '\t' });
            objvalue = vals[0];
            key = vals.Length > 1 ? vals[1] : objvalue;
         }

         public string Name { get { return objvalue; } set { objvalue = value; } }
         public string ID { get { return key; } set { key = value; } }

         public int CompareTo(DataItem other)
         {
            return objvalue.CompareTo(other.objvalue);
         }
      }
   }
}
