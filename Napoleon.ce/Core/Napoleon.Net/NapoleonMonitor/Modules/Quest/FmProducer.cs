using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public partial class FmProducer : Form
   {
      private DataSet<string, Producer> dsProducer;
      private DataSet<string, Producer> dsDelProducer;
      private PostWorker postWorker;

      public FmProducer(PostWorker postWorker)
      {
         this.postWorker = postWorker;
         InitializeComponent();

         dsProducer = (DataSet<string, Producer>)DataModule.Get(Producer.OBJECT_NAME) ??
            new DataSet<string, Producer>(Producer.OBJECT_NAME);
         dsDelProducer = new DataSet<string, Producer>(Producer.OBJECT_NAME, false);
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         Producer p = FmEditProducer.Open(null);

         if (p != null)
         {
            dsProducer.Add(p.id, p);
            DataProcessed();
            btnSave.Enabled = true;
         }
      }

      private void SetEnableControls(bool enable)
      {
         btnAdd.Enabled = enable;
         btnEdit.Enabled = enable;
         btnDel.Enabled = enable;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         btnSave.Enabled = false;
         DataModule.SetDataRepsonceHandlers(DataModule_DataProcessed, DataModule_OnDataResponceError);
         dsDelProducer.Clear();
         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsProducer);

         FmWait.ShowForm(this,
            DataModule.RefreshGiveSets(Config.GetConfig().
            GetConnection(), list, FmWait.ProgressIndicator));

         SetEnableControls(true);
      }

      void DataModule_DataProcessed(object sender, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         BeginInvoke(new EmptyParamHandler(DataProcessed));
      }

      void DataModule_OnDataResponceError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         MessageBox.Show(e.Msg);
      }

      void DataProcessed()
      {
         List<Producer> list = new List<Producer>();
         list.AddRange(dsProducer.Values);
         list.Sort(new Comparison<Producer>(delegate(Producer p1, Producer p2)
            { return p1.Name.CompareTo(p2.Name); }));
         grid.DataSource = list;
      }

      private Producer GetSelectedProd()
      {
         Producer result = null;
         DataGridViewRow row = grid.CurrentRow;

         if (row != null)
            result = row.DataBoundItem as Producer;

         return result;
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         Producer p = GetSelectedProd();

         if (p != null)
         {
            p = FmEditProducer.Open(p);
            btnSave.Enabled = true;
         }
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         Producer p = GetSelectedProd();

         if (p != null && MessageBox.Show("Запись будет удалена, удалить?",
               "Вопрос", MessageBoxButtons.OKCancel,
               MessageBoxIcon.Question) == DialogResult.OK) 
         {
            if(!dsDelProducer.ContainsKey(p.id))
               dsDelProducer.Add(p.id, p);

            if (dsProducer.ContainsKey(p.id))
               dsProducer.Remove(p.id);

            DataProcessed();
            btnSave.Enabled = true;
         }
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         List<IDataSet> wrSet = new List<IDataSet>();
         List<IDataSet> rmvSet = new List<IDataSet>();

         if (dsProducer.Count > 0)
            wrSet.Add(dsProducer);

         if (dsDelProducer.Count > 0)
            rmvSet.Add(dsDelProducer);

         if (DataModule.UpdateDataSet(wrSet, rmvSet, null, Config.GetConfig().GetConnection()) == false)
         {
            MessageBox.Show("Ошибка при записи в базу данных.", 
               "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
         }
         else
            btnSave.Enabled = false;
      }

      private void FmCateg_Load(object sender, EventArgs e)
      {
         SetEnableControls(false);
         btnSave.Enabled = false;
      }

      private void FmProducer_FormClosed(object sender, FormClosedEventArgs e)
      {
         if (postWorker != null)
            postWorker();
      }
   }
}
