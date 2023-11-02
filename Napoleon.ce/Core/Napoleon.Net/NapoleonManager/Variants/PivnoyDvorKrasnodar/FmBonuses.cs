using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using System.Collections;

namespace GRSoft.NapoleonManager
{
   public partial class FmBonuses : Form
   {
      const double DEFAULT_DAYS_SPAN = 7.0;
      static FmBonuses instance = null;

      SimpleDataSet<BonusDef> addedBonuses = new SimpleDataSet<BonusDef>(BonusDef.OBJECT_NAME, false, true);
      SimpleDataSet<BonusDef> bonuses = null;
      BindingList<BonusDef> bindedBonuses = null;

      public FmBonuses()
      {
         InitializeComponent();

         dgvBonuses.AutoGenerateColumns = false;
      }

      public static void Open()
      {
         DateTime defaultFrom = DateTime.Now - TimeSpan.FromDays(DEFAULT_DAYS_SPAN);
         DateTime defaultTill = DateTime.Now + TimeSpan.FromDays(DEFAULT_DAYS_SPAN);
         Open(defaultFrom, defaultTill);
      }

      public static void Open(DateTime from, DateTime till)
      {
         if (instance == null)
         {
            instance = new FmBonuses();
            instance.dtFromDate.Value = from;
            instance.dtTillDate.Value = till;
            instance.Show();
         }
         else
         {
            instance.dtFromDate.Value = from;
            instance.dtTillDate.Value = till;
            instance.RefreshData();
            instance.BringToFront();
         }
      }

      protected override void OnClosed(EventArgs e)
      {
         base.OnClosed(e);
         instance = null;
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
      }

      void RefreshData()
      {
         List<IDataSet> upd = new List<IDataSet>();

         DataSet<String, Price> price = (DataSet<String, Price>)DataModule.Get(Price.OBJECT_NAME) ??
            new DataSet<String, Price>(Price.OBJECT_NAME);

         if (price.Count == 0)
         {
            price.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
            upd.Add(price);
         }

         DateTime from = dtFromDate.Value.Date;
         DateTime till = dtTillDate.Value.Date;
         string query = String.Format("\"{0}\" <= ToDate('{3:dd/MM/yyyy}') or \"{1}\" >= ToDate('{2:dd/MM/yyyy}')",
               "start",
               "till",
               from.Date,
               till.Date);

         bonuses = new SimpleDataSet<BonusDef>(BonusDef.OBJECT_NAME, false, true);
         bonuses.Filter = query;
         upd.Add(bonuses);

         FmWait.StdDataRefresh(this, upd, DoLoadData, tsbRefresh);
      }

      void DoLoadData()
      {
         List<BonusDef> ds = new List<BonusDef>((IEnumerable<BonusDef> )bonuses.Data);
         ds.Sort();
         bindedBonuses = new BindingList<BonusDef>(ds);
         dgvBonuses.DataSource = bindedBonuses;
      }

      private void OpenBonusForm(BonusDef bonus)
      {
         FmBonus bonusForm = FmBonus.Open(bonus);
         bonusForm.FormClosing -= bonusForm_FormClosing;
         bonusForm.FormClosing += bonusForm_FormClosing;
      }

      private void tsbAdd_Click(object sender, EventArgs e)
      {
         if (null == bindedBonuses)
            return;

         BonusDef newBonus = bindedBonuses.AddNew();
         newBonus.id = GRSoft.Network.DataObject.GenId();
         newBonus.Start = DateTime.Now.Date;
         newBonus.Till = DateTime.Now.Date;
         OpenBonusForm(newBonus);
         addedBonuses.Add(newBonus);
      }

      private void tsbRemove_Click(object sender, EventArgs e)
      {
         SimpleDataSet<BonusDef> removingBonuses = new SimpleDataSet<BonusDef>(BonusDef.OBJECT_NAME, false, true);
         DataGridViewSelectedRowCollection rows = dgvBonuses.SelectedRows;
         foreach (DataGridViewRow row in rows)
         {
            if (row.DataBoundItem is BonusDef)
            {
               BonusDef removingBonus = row.DataBoundItem as BonusDef;
               removingBonuses.Add(removingBonus);
            }
         }

         DialogResult dr = MessageBox.Show("Удалить выбранные акции?", "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question);
         if (DialogResult.Yes == dr)
         {
            DataModule.UpdateDataSet(null
                                     , new List<IDataSet>() { removingBonuses }
                                     , null
                                     , Config.GetConfig().GetConnection());
            RefreshData();
         }
      }

      private void tsbRefresh_Click(object sender, EventArgs e)
      {
         RefreshData();
      }

      private void dgvBonuses_CellDoubleClick(object sender, DataGridViewCellEventArgs e)
      {
         if (0 > e.RowIndex)
            return;

         BonusDef selectedBonus = dgvBonuses.Rows[e.RowIndex].DataBoundItem as BonusDef;
         if (null != selectedBonus)
         {
            OpenBonusForm(selectedBonus);
         }
      }

      void bonusForm_FormClosing(object sender, FormClosingEventArgs e)
      {
         Form fmSender = sender as Form;
         if (null != fmSender)
         {
            fmSender.FormClosing -= bonusForm_FormClosing;
            RefreshData();
         }
      }
   }
}
