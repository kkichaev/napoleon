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
   public partial class FmSpancopTable : Form
   {
      DataSet<string, Competitor> dsCompetitor = new DataSet<string, Competitor>(Competitor.OBJECT_NAME, false);
      DataSet<string, Competitor> dsCompetitorRmv = new DataSet<string, Competitor>(Competitor.OBJECT_NAME, false);
      DataSet<string, ClientLevel> dsClientLevel = new DataSet<string, ClientLevel>(ClientLevel.OBJECT_NAME, false);
      DataSet<string, ClientLevel> dsClientLevelRmv = new DataSet<string, ClientLevel>(ClientLevel.OBJECT_NAME, false);
      DataSet<string, Chance> dsChance = new DataSet<string, Chance>(Chance.OBJECT_NAME, false);
      DataSet<string, Chance> dsChanceRmv = new DataSet<string, Chance>(Chance.OBJECT_NAME, false);
      DataSet<string, CategoryProduct> dsCategory = new DataSet<string, CategoryProduct>(CategoryProduct.OBJECT_NAME, false);
      DataSet<string, CategoryProduct> dsCategoryRmv = new DataSet<string, CategoryProduct>(CategoryProduct.OBJECT_NAME, false);
      DataSet<string, Segment> dsSegment = new DataSet<string, Segment>(Segment.OBJECT_NAME, false);
      DataSet<string, Segment> dsSegmentRmv = new DataSet<string, Segment>(Segment.OBJECT_NAME, false);

      public FmSpancopTable()
      {
         InitializeComponent();
         lbTable.Items.Add(new TableItem("Конкуренты", dsCompetitor, dsCompetitorRmv));
         lbTable.Items.Add(new TableItem("Уровень клиента", dsClientLevel, dsClientLevelRmv));
         lbTable.Items.Add(new TableItem("Вероятность успеха", dsChance, dsChanceRmv));
         lbTable.Items.Add(new TableItem("Категория продукта", dsCategory, dsCategoryRmv));
         lbTable.Items.Add(new TableItem("Сегмент", dsSegment, dsSegmentRmv));

         btnSave.Enabled = false;
         EnablecControls(false);
      }

      private void EnablecControls(bool enable)
      {
         lbTable.Enabled = enable;
         tbText.Enabled = enable;
         toolStrip.Enabled = enable;
         lbTable.Enabled = enable;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         DataModule.SetDataRepsonceHandlers(DataProcessed, DataConnectionError);
         btnRefresh.Enabled = false;

         List<IDataSet> updSets = new List<IDataSet>();
         updSets.Add(dsCompetitor);
         updSets.Add(dsClientLevel);
         updSets.Add(dsChance);
         updSets.Add(dsCategory);
         updSets.Add(dsSegment);

         FmWait.ShowForm(this, DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), updSets, FmWait.ProgressIndicator));
      }

      void DataProcessed(System.Object setnder, EventArgs e)
      {
         DataModule.ClearEvents();
         this.Invoke(new InvokeDelegate(delegate
         {
            FmWait.CloseForm();
            btnRefresh.Enabled = true;
            EnablecControls(true);
         }));
      }

      private void DataConnectionError(EDataResponse e)
      {
         DataModule.ClearEvents();

         Invoke(new EmptyParamHandler(delegate
         {
            FmWait.CloseForm();
            btnRefresh.Enabled = true;

            const string TITLE = "Ошибка";

            MessageBox.Show(e.Msg, TITLE, MessageBoxButtons.OK,
               MessageBoxIcon.Error);
         }));
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         if (tbText.Text.Trim().Length > 0)
         {
            TableItem item = lbTable.SelectedItem as TableItem;

            if (item != null)
            {
               Type dataType = item.dataset.ElementType;
               TableValue tb = (TableValue)Activator.CreateInstance(dataType);
               tb.key = GRSoft.Network.DataObject.GenId();
               tb.value = tbText.Text.Trim();
               item.dataset.Add(tb.key, tb);

               lbValue.Items.Add(tb);
               btnSave.Enabled = true;
            }
         }
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         List<IDataSet> wrObj = new List<IDataSet>();
         List<IDataSet> rmvObj = new List<IDataSet>();

         foreach (object o in lbTable.Items)
         {
            TableItem item = (TableItem)o;

            if(item.dataset.Count > 0)
               wrObj.Add(item.dataset);

            if (item.rmv.Count > 0)
               rmvObj.Add(item.rmv);
         }

         bool done = DataModule.UpdateDataSet(wrObj, rmvObj, null, Config.GetConfig().GetConnection());

         if (!done)
         {
            MessageBox.Show("Ошибка при записи в базу данных.", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
            return;
         }
         else
         {
            btnSave.Enabled = false;
            foreach (object o in lbTable.Items)
            {
               TableItem item = (TableItem)o;

               if (item.rmv.Count > 0)
                  rmvObj.Clear();
            }
         }
      }

      private void lbTable_SelectedIndexChanged(object sender, EventArgs e)
      {
         tbText.Clear();
         lbValue.Items.Clear();
         TableItem item = (TableItem)lbTable.SelectedItem;

         if (item != null)
            foreach (TableValue val in item.dataset.Data)
               lbValue.Items.Add(val);
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         if (tbText.Text.Trim().Length > 0)
         {
            TableValue val = lbValue.SelectedItem as TableValue;

            if (val != null)
            {
               val.value = tbText.Text.Trim();
               lbValue.Items[lbValue.Items.IndexOf(val)] = val;
               btnSave.Enabled = true;
            }
         }
      }

      private void lbValue_SelectedIndexChanged(object sender, EventArgs e)
      {
         TableValue val = lbValue.SelectedItem as TableValue;

         if (val != null)
            tbText.Text = val.value;
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         TableValue val = lbValue.SelectedItem as TableValue;
         TableItem item = lbTable.SelectedItem as TableItem;
         tbText.Clear();

         if (val != null && item != null)
         {
            Type dataType = item.rmv.ElementType;
            TableValue tb = (TableValue)Activator.CreateInstance(dataType);
            tb.key = val.key;
            item.rmv.Add(tb.key, tb);
            btnSave.Enabled = true;
            lbValue.Items.Remove(val);
         }
      }

      private void FmSpancopTable_FormClosing(object sender, FormClosingEventArgs e)
      {
         if(btnSave.Enabled == true &&  
            MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
            btnSave_Click(btnSave, EventArgs.Empty);
      }
   }

   class TableItem
   {
      public string caption = string.Empty;
      public IDataSet dataset;
      public IDataSet rmv;

      public TableItem(string caption, IDataSet dataset, IDataSet rmv)
      {
         this.caption = caption;
         this.dataset = dataset;
         this.rmv = rmv;
      }

      public override string ToString()
      {
         return caption;
      }
   }

   class Competitor : TableValue
   {
      public static readonly string OBJECT_NAME = "Competitor";
   }

   class ClientLevel : TableValue
   {
      public static readonly string OBJECT_NAME = "ClientLevel";
   }

   class Chance : TableValue
   {
      public static readonly string OBJECT_NAME = "Chance";
   }

   class CategoryProduct : TableValue
   {
      public static readonly string OBJECT_NAME = "CategoryProduct";
   }

   class Segment : TableValue
   {
      public static readonly string OBJECT_NAME = "Segment";
   }

   class TableValue : GRSoft.Network.DataObject
   {
      [KeyField]
      public string key = string.Empty;
      public string value = string.Empty;

      public override string ToString()
      {
         return value;
      }
   }
}

