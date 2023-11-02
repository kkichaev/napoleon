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
   public partial class FmCateg : Form
   {
      private DataSet<string, Category> dsCategory;
      private DataSet<string, Category> dsDelCategory;
      private PostWorker postWorker;

      public FmCateg(PostWorker postWorker)
      {
         this.postWorker = postWorker;
         InitializeComponent();

         dsCategory = (DataSet<string, Category>)DataModule.Get(Category.OBJECT_NAME) ??
            new DataSet<string, Category>(Category.OBJECT_NAME);
         dsDelCategory = new DataSet<string, Category>(Category.OBJECT_NAME, false);
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         Category c = FmEditCateg.Open(null);

         if (c != null)
         {
            dsCategory.Add(c.id, c);
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
         dsDelCategory.Clear();
         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsCategory);

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
         List<Category> list = new List<Category>();
         list.AddRange(dsCategory.Values);
         list.Sort(new Comparison<Category>(delegate(Category c1, Category c2)
            { return c1.Name.CompareTo(c2.Name); }));
         grid.DataSource = list;
      }

      private Category GetSelectedCat()
      {
         Category result = null;
         DataGridViewRow row = grid.CurrentRow;

         if (row != null)
            result = row.DataBoundItem as Category;

         return result;
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         Category c = GetSelectedCat();

         if (c != null)
         {
            c = FmEditCateg.Open(c);
            btnSave.Enabled = true;
         }
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         Category c = GetSelectedCat();

         if (c != null && MessageBox.Show("Запись будет удалена, удалить?",
               "Вопрос", MessageBoxButtons.OKCancel,
               MessageBoxIcon.Question) == DialogResult.OK) 
         {
            if(!dsDelCategory.ContainsKey(c.id))
               dsDelCategory.Add(c.id, c);

            if (dsCategory.ContainsKey(c.id))
               dsCategory.Remove(c.id);

            DataProcessed();
            btnSave.Enabled = true;
         }
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         List<IDataSet> wrSet = new List<IDataSet>();
         List<IDataSet> rmvSet = new List<IDataSet>();

         if (dsCategory.Count > 0)
            wrSet.Add(dsCategory);

         if (dsDelCategory.Count > 0)
            rmvSet.Add(dsDelCategory);

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

      private void FmCateg_FormClosed(object sender, FormClosedEventArgs e)
      {
         if (postWorker != null)
            postWorker();
      }
   }
}
