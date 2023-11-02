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
   public partial class FmRegionParent : Form
   {
      private DataSet<string, Region1> dsRegion1;
      private DataSet<string, Region2> dsRegion2;

      private DataSet<string, Region1> dsDelRegion1;
      private DataSet<string, Region2> dsDelRegion2;

      public FmRegionParent()
      {
         InitializeComponent();

         dsRegion1 = (DataSet<string, Region1>)DataModule.Get(GRSoft.NapoleonManager.Region1.OBJECT_NAME) ??
           new DataSet<string, Region1>(GRSoft.NapoleonManager.Region1.OBJECT_NAME);
         dsRegion2 = (DataSet<string, Region2>)DataModule.Get(GRSoft.NapoleonManager.Region2.OBJECT_NAME) ??
            new DataSet<string, Region2>(GRSoft.NapoleonManager.Region2.OBJECT_NAME);

         dsDelRegion1 = new DataSet<string, Region1>(GRSoft.NapoleonManager.Region1.OBJECT_NAME, false);
         dsDelRegion2 = new DataSet<string, Region2>(GRSoft.NapoleonManager.Region2.OBJECT_NAME, false);

         dgvR1.AutoGenerateColumns = false;
         dgvR2.AutoGenerateColumns = false;
      }

      public static void Open()
      {
         FmRegionParent instance = new FmRegionParent();
         instance.Show();
      }

      private void ControlEnable(bool enable)
      {
         btnEditR1.Enabled = enable;
         btnAddR1.Enabled = enable;
         btnDelR1.Enabled = enable;
         btnAddR2.Enabled = enable;
         btnDelR2.Enabled = enable;
         btnEditR2.Enabled = enable;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         btnSave.Enabled = false;
         dsDelRegion1.Clear();
         dsDelRegion2.Clear();
         ControlEnable(true);
         DataModule.SetDataRepsonceHandlers(DataModule_DataProcessed, DataModule_OnDataResponceError);

         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsRegion1);
         list.Add(dsRegion2);

         FmWait.ShowForm(this,
            DataModule.RefreshGiveSets(Config.GetConfig().
            GetConnection(), list, FmWait.ProgressIndicator));
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
         List<Region2> listR2 = new List<Region2>();
         listR2.AddRange(dsRegion2.Values);
         listR2.Sort(new Comparison<Region2>(delegate(Region2 r1, Region2 r2){return r1.Name.CompareTo(r2.Name);}));

         List<Region1> listR1 = new List<Region1>();
         listR1.AddRange(dsRegion1.Values);
         listR1.Sort(new Comparison<Region1>(delegate(Region1 r1, Region1 r2) { return r1.region2.CompareTo(r2.region2); }));

         Dictionary<string, Region2> R2Dic = new Dictionary<string,Region2>();
         foreach (Region2 r2 in listR2)
            R2Dic.Add(r2.id, r2);

         foreach (Region1 r1 in listR1)
         {
            if (R2Dic.ContainsKey(r1.region2))
               R2Dic[r1.region2].childs.Add(r1);
         }

         dgvR2.DataSource = new BindingList<Region2>(listR2);
      }

      private void btnAddR2_Click(object sender, EventArgs e)
      {
         LiveArea r = FmRegionEdit.Open(null);
         
         if (r != null)
         {
            Region2 r2 = new Region2();
            r2.id = r.id;
            r2.code = r.code;
            r2.name = r.name;

            BindingList<Region2> list = (BindingList<Region2>)dgvR2.DataSource;
            list.Add(r2);
            dgvR2.Update();

            btnSave.Enabled = true;
         }
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         BindingList<Region2> list = (BindingList<Region2>)dgvR2.DataSource;

         if (list != null)
         {
            DataSet<string, Region2> dsR2 = new DataSet<string, Region2>(Region2.OBJECT_NAME, false);
            DataSet<string, Region1> dsR1 = new DataSet<string, Region1>(Region1.OBJECT_NAME, false);

            foreach (Region2 r2 in list)
            {
               dsR2.Add(r2.id, r2);
               foreach (Region1 r1 in r2.childs)
                  dsR1.Add(r1.id, r1);
            }

            List<IDataSet> wrSet = new List<IDataSet>();
            List<IDataSet> rmvSet = new List<IDataSet>();

            if (dsR2.Count > 0)
               wrSet.Add(dsR2);

            if (dsR1.Count > 0)
               wrSet.Add(dsR1);

            if (dsDelRegion1.Count > 0)
               rmvSet.Add(dsDelRegion1);

            if (dsDelRegion2.Count > 0)
               rmvSet.Add(dsDelRegion2);

            if (DataModule.UpdateDataSet(wrSet, rmvSet, null,
               Config.GetConfig().GetConnection()) == false)
            {
               MessageBox.Show("Ошибка при записи в базу данных.", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
            else
            {
               btnSave.Enabled = false;
               dsDelRegion1.Clear();
               dsDelRegion2.Clear();
            }
         }
      }

      private void btnAddR1_Click(object sender, EventArgs e)
      {
         Region2 r2 = getSelectedR2();

         if (r2 != null)
         {
            LiveArea r = FmRegionEdit.Open(null);
            if (r != null)
            {
               Region1 r1 = new Region1();
               r1.id = r.id;
               r1.name = r.name;
               r1.code = r.code;
               r1.region2 = r2.id;

               r2.childs.Add(r1);
               btnSave.Enabled = true;
            }
         }
      }

      private Region2 getSelectedR2()
      {
         DataGridViewRow row = dgvR2.CurrentRow;
         Region2 result = null;

         if (row != null)
            result = row.DataBoundItem as Region2;

         return result;

      }

      private Region1 getSelectedR1()
      {
         DataGridViewRow row = dgvR1.CurrentRow;
         Region1 result = null;

         if (row != null)
            result = row.DataBoundItem as Region1;

         return result;

      }

      private void dgvR2_SelectionChanged(object sender, EventArgs e)
      {
         Region2 r2 = getSelectedR2();

         if (r2 != null)
            dgvR1.DataSource = r2.childs;
      }

      private void FmRegionParent_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (btnSave.Enabled && MessageBox.Show("Сохранить измененим","Вопрос", MessageBoxButtons.OKCancel,
            MessageBoxIcon.Question) == DialogResult.OK)
         {
            btnSave_Click(null, null);
         }
      }

      private void FmRegionParent_Load(object sender, EventArgs e)
      {
         btnSave.Enabled = false;
         ControlEnable(false);
      }

      private void btnEditR2_Click(object sender, EventArgs e)
      {
         Region2 r2 = getSelectedR2();

         if (r2 != null)
         {
            LiveArea r = FmRegionEdit.Open(r2);

            if (r != null)
            {
               r2.code = r.code;
               r2.name = r.name;

               btnSave.Enabled = true;
            }
         }
      }

      private void btnDelR2_Click(object sender, EventArgs e)
      {
         Region2 r2 = getSelectedR2();

         if (r2 != null && MessageBox.Show("Запись будет удалена, удалить?",
            "Вопрос", MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
         {
            dsDelRegion2.Add(r2.id, r2);
            BindingList<Region2> list = (BindingList<Region2>)dgvR2.DataSource;
            list.Remove(r2);
            btnSave.Enabled = true;
         }
      }

      private void btnEditR1_Click(object sender, EventArgs e)
      {
         Region1 r1 = getSelectedR1();

         if (r1 != null)
         {
            LiveArea la = FmRegionEdit.Open(r1);

            if (la != null)
            {
               r1.code = la.code;
               r1.name = la.name;
               dgvR1.Update();
               btnSave.Enabled = true;
            }
         }
      }

      private void btnDelR1_Click(object sender, EventArgs e)
      {
         Region1 r1 = getSelectedR1();

         if (r1 != null && MessageBox.Show("Запись будет удалена, удалить?",
            "Вопрос", MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
         {
            dsDelRegion1.Add(r1.id, r1);
            BindingList<Region1> list = (BindingList<Region1>)dgvR1.DataSource;
            list.Remove(r1);
            btnSave.Enabled = true;
         }
      }
   }
}
