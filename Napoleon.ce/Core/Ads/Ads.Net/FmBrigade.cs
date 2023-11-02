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
   public partial class FmBrigade : Form
   {
      private static FmBrigade instance;
      private static DsBrigade dsBrigade;
      private static DsDistrict dsDistrict;
      private static DsStuff dsStuff;
      private static DsProfession dsProfession;
      private Invoker gridDoubleClick;
      private SearchEngine searchEngine;
      private DsDivision dsDivision;
      private DsJobType dsJobType;

      public FmBrigade()
      {
         InitializeComponent();
         dsBrigade = (DsBrigade)DataModule.Get(Brigade.OBJECT_NAME) ?? new DsBrigade(true);
         dsDistrict = (DsDistrict)DataModule.Get(District.OBJECT_NAME) ?? new DsDistrict(true);
         dsStuff = (DsStuff)DataModule.Get(Stuff.OBJECT_NAME) ?? new DsStuff(true);
         dsProfession = (DsProfession)DataModule.Get(Profession.OBJECT_NAME) ?? new DsProfession(true);
         dsDivision = (DsDivision)DataModule.Get(Division.OBJECT_NAME) ?? new DsDivision(true);
         dsJobType = (DsJobType)DataModule.Get(JobType.OBJECT_NAME) ?? new DsJobType(true);

         dgvDistrict.AutoGenerateColumns = false;
         dgvStuff.AutoGenerateColumns = false;

         searchEngine = new SearchEngine(new FindDataGridObject(dgvBrigade, 0));
         dgvBrigade.AutoGenerateColumns = false;
      }

      public static void ShowInstance()
      {
         ShowInstance(null);
      }

      public static void ShowInstance(Invoker gridDoubleClick)
      {
         if (instance != null)
            instance.Close();

         instance = new FmBrigade();
         instance.gridDoubleClick = gridDoubleClick;
         instance.Show();
      }

      private void FmBrigade_FormClosed(object sender, FormClosedEventArgs e)
      {
         instance = null;
      }

      private void btnAddBrigade_Click(object sender, EventArgs e)
      {
         if (FmBrigadeEdit.ShowInstance(null))
            btnRefresh_Click(null, null);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsProfession);
         list.Add(dsStuff);
         list.Add(dsDistrict);
         list.Add(dsJobType);
         list.Add(dsBrigade);
         list.Add(dsDivision);

         DataModule.SetDataRepsonceHandlers(DataModule_DataProcessed,
            DataModule_OnDataResponceError);

         FmWait.ShowForm(this, 
            DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
               list, FmWait.ProgressIndicator));
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
         List<Brigade> list = new List<Brigade>();

         foreach (Brigade brigade in dsBrigade.Data)
            list.Add(brigade);

         dgvBrigade.DataSource = list;
         DataUtils.GridSort<Brigade>(dgvBrigade, 0, brigadeComparer);
         dgvBrigade_SelectionChanged(null, null);
      }

      private void btnEditBrigade_Click(object sender, EventArgs e)
      {
         DataGridViewRow row = dgvBrigade.CurrentRow;

         if (row == null)
            return;

         Brigade brigade = (Brigade)row.DataBoundItem;

         if (FmBrigadeEdit.ShowInstance(brigade))
            dgvBrigade.Refresh();
      }

      private void btnDeleteBrigade_Click(object sender, EventArgs e)
      {
         DataGridViewRow row = dgvBrigade.CurrentRow;

         if (row == null)
            return;

         Brigade del = (Brigade)row.DataBoundItem;

         if (MessageBox.Show("Запись будет удалена. Удалить?",
            "Вопрос", MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
         {
            DsBrigade dsToDel = new DsBrigade(false);
            dsToDel.Add(del.id, del);
            List<IDataSet> delSet = new List<IDataSet>();
            delSet.Add(dsToDel);

            List<IDataSet> updSet = new List<IDataSet>();
            
            foreach (Division division in dsDivision.Data)
            {
               foreach (Division.DivisionAgent agent in division.agents)
               {
                  if (agent.id.Equals(del.id))
                  {
                     division.agents.Remove(agent);
                     break;
                  }
               }
            }

            updSet.Add(dsDivision);

            if (DataModule.UpdateDataSet(updSet, delSet, null, Config.GetConfig().GetConnection()))
            {
               btnRefresh_Click(null, null);
            }
            else MessageBox.Show("Ошибка при удалении записи");
         }
      }

      private void FmBrigade_Load(object sender, EventArgs e)
      {
         btnRefresh_Click(null, null);
      }

      private BrigadeGridComparer brigadeComparer = new BrigadeGridComparer();

      class BrigadeGridComparer : GridBoundedObjectComparer
      {
         //public override int Compare(Brigade b1, Brigade b2)
         //{
         //   if (ColumnIndex == 0)
         //      return b1.Login.CompareTo(b2.Login);
         //   else if (ColumnIndex == 1)
         //      return b1.Password.CompareTo(b2.Password);
         //   else if (ColumnIndex == 2)
         //      return b1.Name.CompareTo(b2.Name);

         //   return 0;
         //}
      }

      private void dgvBrigade_ColumnHeaderMouseClick(object sender, DataGridViewCellMouseEventArgs e)
      {
         searchEngine = new SearchEngine(new FindDataGridObject(dgvBrigade, e.ColumnIndex));
         DataUtils.GridSort<Brigade>(dgvBrigade, e.ColumnIndex, brigadeComparer);
      }

      private void btnDistrict_Click(object sender, EventArgs e)
      {
         FmDistrict.ShowInstance(new Invoker(delegate(object param)
            {
               AddDistrict((District)param);
            }));
      }

      private void dgvDistrict_DragEnter(object sender, DragEventArgs e)
      {
         if (e.Data.GetData(typeof(District)) != null)
            e.Effect = DragDropEffects.Copy;
         else
            e.Effect = DragDropEffects.None;
      }

      private void dgvDistrict_DragDrop(object sender, DragEventArgs e)
      {
         District district = (District)e.Data.GetData(typeof(District));

         AddDistrict(district);
      }

      private void AddDistrict(District district)
      {
         DataGridViewRow brigadeRow = dgvBrigade.CurrentRow;

         if (brigadeRow != null && district != null)
         {
            Brigade brigade = (Brigade)brigadeRow.DataBoundItem;

            if (brigade.region == null)
               brigade.region = new List<BrigadeDistrict>();

            bool duplicates = false;

            foreach (BrigadeDistrict bd in brigade.region)
            {
               if (bd.id.Equals(district.id))
               {
                  duplicates = true;
                  break;
               }
            }

            if (!duplicates)
            {
               BrigadeDistrict brigadeDistrict = new BrigadeDistrict();
               brigadeDistrict.id = district.id;
               brigadeDistrict.district = district;

               brigade.region.Add(brigadeDistrict);
               Save();
               dgvBrigade_SelectionChanged(null, null);
            }
         }
      }

      private void Save()
      {
         Brigade brigade = GetSelectedBrigade();

         if (brigade != null)
         {
            DsBrigade dsBrigade = new DsBrigade(false);
            dsBrigade.Add(brigade.id, brigade);

            List<IDataSet> list = new List<IDataSet>();
            list.Add(dsBrigade);

            if (DataModule.UpdateDataSet(list, null, null, Config.GetConfig().GetConnection()) == false)
               MessageBox.Show("Ошибка при добавлении");
         }
      }

      private Brigade GetSelectedBrigade()
      {
         DataGridViewRow row = dgvBrigade.CurrentRow;

         if (row == null)
            return null;

         return (Brigade)row.DataBoundItem;
      }

      private void dgvBrigade_SelectionChanged(object sender, EventArgs e)
      {
         Brigade brigade = GetSelectedBrigade();
         List<District> districts = new List<District>();
         List<Stuff> stuff = new List<Stuff>();

         if (brigade != null)
         {
            foreach (BrigadeDistrict d in brigade.region)
               districts.Add(d.district);

            foreach (BrigadeStuff s in brigade.stuff)
               stuff.Add(s.stuff);
         }

         dgvDistrict.DataSource = districts;
         dgvStuff.DataSource = stuff;
      }

      private void btnStuff_Click(object sender, EventArgs e)
      {
         FmStuff.ShowInstance(new Invoker(delegate(object param) 
            {
               AddStuff((Stuff)param);
            }));
      }

      private void dgvStuff_DragEnter(object sender, DragEventArgs e)
      {
         if (e.Data.GetData(typeof(Stuff)) != null)
            e.Effect = DragDropEffects.Copy;
         else
            e.Effect = DragDropEffects.None;
      }

      private void dgvStuff_DragDrop(object sender, DragEventArgs e)
      {
         Stuff stuff = (Stuff)e.Data.GetData(typeof(Stuff));

         AddStuff(stuff);
      }

      private void AddStuff(Stuff stuff)
      {
         Brigade brigade = GetSelectedBrigade();

         if (brigade != null && stuff != null)
         {
            if (brigade.stuff == null)
               brigade.stuff = new List<BrigadeStuff>();

            bool duplicates = false;

            foreach (BrigadeStuff bs in brigade.stuff)
            {
               if (bs.id.Equals(stuff.id))
               {
                  duplicates = true;
                  break;
               }
            }

            if(!duplicates)
            {
               BrigadeStuff brigadeStuff = new BrigadeStuff();
               brigadeStuff.id = stuff.id;
               brigadeStuff.stuff = stuff;
               brigade.stuff.Add(brigadeStuff);
               Save();
               dgvBrigade_SelectionChanged(null, null);
            }
         }
      }

      private void btnDelDistrict_Click(object sender, EventArgs e)
      {
         DataGridViewRow row = dgvDistrict.CurrentRow;
         Brigade brigade = GetSelectedBrigade();

         if (row != null && brigade != null && MessageBox.Show("Запись будет удалена. Удалить?",
            "Вопрос", MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
         {
            District district = (District)row.DataBoundItem;

            foreach (BrigadeDistrict bd in brigade.region)
               if (bd.district == district)
               {
                  brigade.region.Remove(bd);
                  Save();
                  dgvBrigade_SelectionChanged(null, null);
                  break;
               }
         }
      }

      private void btnDelStuff_Click(object sender, EventArgs e)
      {
         DataGridViewRow row = dgvStuff.CurrentRow;
         Brigade brigade = GetSelectedBrigade();

         if (row != null && brigade != null && MessageBox.Show("Запись будет удалена. Удалить?",
            "Вопрос", MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
         {
            Stuff stuff = (Stuff)row.DataBoundItem;

            foreach (BrigadeStuff bs in brigade.stuff)
               if (bs.stuff == stuff)
               {
                  brigade.stuff.Remove(bs);
                  Save();
                  dgvBrigade_SelectionChanged(null, null);
                  break;
               }
         }
      }

      private void dgvBrigade_DoubleClick(object sender, EventArgs e)
      {
         Brigade brigade = GetSelectedBrigade();

         if (brigade != null && gridDoubleClick != null)
         {
            gridDoubleClick(brigade);
            Close();
         }
      }

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

      private void dgvBrigade_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         if (e.RowIndex != -1 && e.RowIndex < ((DataGridView)sender).Rows.Count)
         {
            DataGridViewRow row = ((DataGridView)sender).Rows[e.RowIndex];
            Brigade brigade = row.DataBoundItem as Brigade;

            if (brigade != null)
            {
               e.CellStyle.ForeColor = brigade.JobTypeColor;
            }
         }
      }

      private void Schedule_Click(object sender, EventArgs e)
      {
         Brigade brigade = GetSelectedBrigade();

         new FmSchedule(brigade).Show();
      }

      private void btnAddress_Click(object sender, EventArgs e)
      {
         new FmBrigadeAddress().Show();
      }
   }
}
