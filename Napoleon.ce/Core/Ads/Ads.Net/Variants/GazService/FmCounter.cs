using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.Ads
{
   public partial class FmCounter : Form
   {
      private DsCounter dsCounter;

      public FmCounter()
      {
         InitializeComponent();
         dsCounter = (DsCounter)DataModule.Get(Counter.OBJECT_NAME) ?? new DsCounter(true);
      }

      public static void ShowInstance()
      {
         FmCounter instance = new FmCounter();
         instance.Show();
      }

      private void bntAdd_Click(object sender, EventArgs e)
      {
         if (FmCounterEdit.ShowInstance())
            btnRefresh_Click(null, null);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsCounter);

         DataModule.OnDataResponceError += new EventDataResponseError(DataModule_OnDataResponceError);
         DataModule.DataProcessed += new EventHandler(DataModule_DataProcessed);
         FmWait.ShowForm(this,
            DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
            upd, FmWait.ProgressIndicator));
      }

      void DataModule_DataProcessed(object sender, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         Invoke(new InvokeDelegate(RefreshData));
      }

      void DataModule_OnDataResponceError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         MessageBox.Show(e.Msg);
      }

      void RefreshData()
      {
         List<Counter> list = new List<Counter>();
         list.AddRange(dsCounter.Values);
         list.Sort(new Comparison<Counter>(
            delegate(Counter c1, Counter c2) 
            {
               return c1.Name.CompareTo(c2.Name);
            }));

         dgvCounter.DataSource = list;
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         DataGridViewRow row = dgvCounter.CurrentRow;

         if (row == null)
            return;

         Counter del = (Counter)
                 dgvCounter.CurrentRow.DataBoundItem;

         if (MessageBox.Show("Запись будет удалена. Удалить?",
            "Вопрос", MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
         {
            DsCounter dsToDel = new DsCounter(false);
            dsToDel.Add(del.Name, del);
            List<IDataSet> delSet = new List<IDataSet>();
            delSet.Add(dsToDel);

            if (DataModule.UpdateDataSet(null, delSet, null, Config.GetConfig().GetConnection()))
            {
               btnRefresh_Click(null, null);
            }
            else
               MessageBox.Show("Ошибка при удалении записи");
         }
      }
   }
}
