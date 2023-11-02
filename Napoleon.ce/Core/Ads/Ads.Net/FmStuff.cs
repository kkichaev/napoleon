using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.Ads.Utils;

namespace GRSoft.Ads
{
   public partial class FmStuff : Form
   {
      private static FmStuff instance;
      DsProfession dsProfession;
      DsStuff dsStuff;
      private SearchEngine searchEngine;
      private Invoker gridDoubleClick;

      public FmStuff()
      {
         InitializeComponent();
         dgvStuff.AutoGenerateColumns = true;
         dsStuff = (DsStuff)DataModule.Get(Stuff.OBJECT_NAME) ?? new DsStuff(true);
         dsProfession = (DsProfession)DataModule.Get(Profession.OBJECT_NAME) ?? new DsProfession(true);

         searchEngine = new SearchEngine(new FindDataGridObject(dgvStuff, 0));
      }

      public static void ShowInstance()
      {
         ShowInstance(null);
      }

      public static void ShowInstance(Invoker gridDoubleClick)
      {
         if (instance != null)
            instance.Close();

         instance = new FmStuff();
         instance.gridDoubleClick = gridDoubleClick;
         instance.Show();
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         if (FmStuffEdit.ShowInstance(null))
            btnRefresh_Click(null, null);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> updList = new List<IDataSet>();
         updList.Add(dsProfession);
         updList.Add(dsStuff);
         DataModule.SetDataRepsonceHandlers(DataModule_DataProcessed,
             DataModule_OnDataResponceError);

         FmWait.ShowForm(this,
            DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
               updList, FmWait.ProgressIndicator));
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
         List<Stuff> list = new List<Stuff>();

         foreach (Stuff stuff in dsStuff.Data)
            list.Add(stuff);

         dgvStuff.DataSource = list;

         DataUtils.GridSort<Stuff>(dgvStuff, 0, stuffComparer);
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         DataGridViewRow curRow = dgvStuff.CurrentRow;

         if (curRow == null)
            return;

         Stuff stuff = (Stuff)curRow.DataBoundItem;

         if (FmStuffEdit.ShowInstance(stuff))
            dgvStuff.Refresh();
      }

      private void FmStuff_Load(object sender, EventArgs e)
      {
         btnRefresh_Click(null, null);
      }

      private void FmStuff_FormClosed(object sender, FormClosedEventArgs e)
      {
         instance = null;
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         DataGridViewRow row = dgvStuff.CurrentRow;

         if (row == null)
            return;

         Stuff del = (Stuff)
                 dgvStuff.CurrentRow.DataBoundItem;

         if (MessageBox.Show("Запись будет удалена. Удалить?",
            "Вопрос", MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
         {
            DsStuff dsToDel = new DsStuff(false);
            dsToDel.Add(del.id, del);
            List<IDataSet> delSet = new List<IDataSet>();
            delSet.Add(dsToDel);

            if (DataModule.UpdateDataSet(null, delSet, null, Config.GetConfig().GetConnection()))
            {
               btnRefresh_Click(null, null);
            }
            else MessageBox.Show("Ошибка при удалении записи");
         }
      }

      private void dgvStuff_ColumnHeaderMouseClick(object sender, DataGridViewCellMouseEventArgs e)
      {
         searchEngine = new SearchEngine(new FindDataGridObject(dgvStuff, e.ColumnIndex));
         DataUtils.GridSort<Stuff>(dgvStuff, e.ColumnIndex, stuffComparer);
      }

      class StuffGridComparer : GridBoundedObjectComparer
      {
         //public override int Compare(Stuff x, Stuff y)
         //{
         //   switch (ColumnIndex)
         //   {
         //      case 0: return x.FIO.CompareTo(y.FIO);
         //      case 1: return x.Phone.CompareTo(y.Phone);
         //      case 2: return x.Address.CompareTo(y.Address);
         //      case 3: return x.Profession.ToString().CompareTo(x.Profession.ToString());
         //      case 4: return x.Rank.CompareTo(x.Rank);
         //      default: return 0;
         //   }
         //}
      }

      private StuffGridComparer stuffComparer = new StuffGridComparer();

      private void btnSearchBack_Click(object sender, EventArgs e)
      {
         searchEngine.find(tbFind.Text, Direction.UP);
      }

      private void btnSearchForward_Click(object sender, EventArgs e)
      {
         searchEngine.find(tbFind.Text, Direction.DOWN);
      }

      private void tbFind_KeyDown(object sender, KeyEventArgs e)
      {
         if (e.KeyCode == Keys.Enter)
            searchEngine.find(tbFind.Text, Direction.DOWN);
      }

      private void dgvStuff_DoubleClick(object sender, EventArgs e)
      {
         Stuff stuff = GetSelectedStuff();

         if (stuff != null && gridDoubleClick != null)
            gridDoubleClick(stuff);
      }

      private Stuff GetSelectedStuff()
      {
         DataGridViewRow row = dgvStuff.CurrentRow;

         if (row == null)
            return null;

         return (Stuff)row.DataBoundItem;
      }
   }
}
