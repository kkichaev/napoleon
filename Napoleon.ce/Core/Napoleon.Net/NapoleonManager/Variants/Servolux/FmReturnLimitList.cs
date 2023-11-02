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
   public partial class FmReturnLimitList : Form
   {
      bool canSave = false;

      ManagerLogList mgrLog = new ManagerLogList();

      SimpleDataSet<ReturnLimit> dsLimits = new SimpleDataSet<ReturnLimit>(ReturnLimit.OBJECT_NAME, false);
      SimpleDataSet<ReturnLimit> removed = new SimpleDataSet<ReturnLimit>(ReturnLimit.OBJECT_NAME, false);
      DataSet<string, PriceType> dsTypes = new DataSet<string, PriceType>(PriceType.OBJECT_NAME, true);

      public FmReturnLimitList()
      {
         InitializeComponent();

         dgvItems.AutoGenerateColumns = false;
         dtpBegin.Value = DateTime.Now.AddDays(-7);
         dtpEnd.Value = DateTime.Now.AddDays(7);

         DataSet<string, PriceType> dsTypes = (DataSet<string, PriceType>)DataModule.Get(PriceType.OBJECT_NAME) ?? 
            new DataSet<string, PriceType>(PriceType.OBJECT_NAME, true);

         Manager m = CurrentUser.user as Manager;
         if (m != null)
         {
            canSave = true; // m.HaveRight(RightTokens.Get("ReturnEditRigth"), RightActions.Write);
            tsbAdd.Enabled = canSave;
            tsbEdit.Enabled = canSave;
            tsbRemove.Enabled = canSave;

            clmnOverLimit.ReadOnly = !canSave;
         }
      }
      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         LoadData();
      }

      private void LoadData()
      {
         List<IDataSet> upd = new List<IDataSet>();
         if (dsTypes.Count == 0)
            upd.Add(dsTypes);

         // всегда выбираем все от текущей даты и выше чтобы делать проверку интервалов.
         DateTime start = dtpBegin.Value.Date;
         if (start > DateTime.Now.Date)
            start = DateTime.Now.Date;
         dsLimits.Filter = String.Format("\"end\" >= ToDate('{0:dd/MM/yyyy}')", start);
         //dsLimits.Filter = String.Format("\"start\" <= ToDate('{1:dd/MM/yyyy}') and \"end\" >= ToDate('{0:dd/MM/yyyy}')", start, dtpEnd.Value.Date);
         upd.Add(dsLimits);

         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      void DoLoadData()
      {
         if (cbAgents.Items.Count != 0)
            return;

         List<Agent> agents = new List<Agent>();
         foreach (Agent a in (CurrentUser.user as Manager).GetAgents().Data)
            agents.Add(a);
         agents.Sort();
         agents.ForEach(x => cbAgents.Items.Add(x));
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
         List<IDataSet> wr = new List<IDataSet>();
         List<IDataSet> rmv = new List<IDataSet>();
         
         wr.Add(dsLimits);

         if (removed.Count > 0)
            rmv.Add(removed);

         if (mgrLog.Count > 0)
            wr.Add(mgrLog);

         bool ret = DataModule.UpdateDataSet(wr, rmv, null, Config.GetConfig().GetConnection());
         if (ret)
         {
            removed.Clear();
         }


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
         tsbSave.Enabled = canSave && true;
      }

      private void cbAgents_SelectedIndexChanged(object sender, EventArgs e)
      {
         Agent a = cbAgents.SelectedItem as Agent;
         if( a == null )
            return;
         ShowAgentLimits(a);
      }

      private void ShowAgentLimits(Agent a)
      {
         List<ReturnLimit> data = new List<ReturnLimit>();
         foreach(ReturnLimit rc in dsLimits.Data)
            if( rc.userid == a.id)
               data.Add(rc);

         SortableBindingList<ReturnLimit> src = new SortableBindingList<ReturnLimit>(data);
         dgvItems.DataSource = src;
      }

      private void dgvItems_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         if(e.ColumnIndex == clmnLimitMoney.DisplayIndex || e.ColumnIndex == clmnLimitWeight.DisplayIndex)
         {
            int value = (int)e.Value;
            if(value == 0)
            {
               e.Value = "";
               e.FormattingApplied = true;
            }
         }
      }

      ReturnLimit FindSameLimit(ReturnLimit ri)
      {
         foreach (ReturnLimit src in dsLimits.Data)
         {
            if (src.userid != ri.userid || src.priceType != ri.priceType || src.start != ri.start)
               continue;
            return src;
         }
         return null;
      }

      private void tsbAdd_Click(object sender, EventArgs e)
      {
         Agent a = cbAgents.SelectedItem as Agent;
         if (a == null)
            return;

         SortableBindingList<ReturnLimit> src = (SortableBindingList<ReturnLimit>)dgvItems.DataSource;
         ReturnLimit ri = src.AddNew();
         ri.userid = a.id;

         ri.start = DateTime.Now.Date;
         ri.end = DateTime.Now.AddDays(7);

         bool needRemove = true;

         if (FmEditAgentLimit.EditLimit(ri, dsTypes, dsLimits))
         {
            ReturnLimit check = FindSameLimit(ri);
            mgrLog.PutLog(ri.userid, ri.type.id, "", "ReturnLimit", ri.limit.ToString(), check == null ? "" : check.limit.ToString());
            if (check != null)
            {
               check.SetFrom(ri);
               dgvItems.InvalidateRow(src.IndexOf(check));
               SetDirty();
            }
            else
            {
               needRemove = false;
               dgvItems.InvalidateRow(src.IndexOf(ri));
               dsLimits.Add(ri);
               SetDirty();
            }
         }

         if(needRemove)
         {
            src.Remove(ri);
         }
      }

      private void tsbEdit_Click(object sender, EventArgs e)
      {
         if (dgvItems.CurrentRow == null)
            return;

         SortableBindingList<ReturnLimit> src = (SortableBindingList<ReturnLimit>)dgvItems.DataSource;
         ReturnLimit ri = (ReturnLimit)dgvItems.CurrentRow.DataBoundItem;
         if (FmEditAgentLimit.EditLimit(ri, dsTypes, dsLimits))
         {
            ReturnLimit check = FindSameLimit(ri);
            if(check != null && check != ri)
            {
               mgrLog.PutLog(check.userid, check.type.id, "", "ReturnLimit", check.limit.ToString(), ri.limit.ToString());
               check.SetFrom(ri);
               src.Remove(ri);
               ri = check;
            }
            SetDirty();
            dgvItems.InvalidateRow(src.IndexOf(ri));
         }
      }

      private void tsbRemove_Click(object sender, EventArgs e)
      {
         if (dgvItems.CurrentRow == null)
            return;

         ReturnLimit ri = (ReturnLimit)dgvItems.CurrentRow.DataBoundItem;
         SortableBindingList<ReturnLimit> src = (SortableBindingList<ReturnLimit>)dgvItems.DataSource;
         src.Remove(ri);
         removed.Add(ri);
         foreach(KeyValuePair<int, ReturnLimit> item in dsLimits)
            if (item.Value == ri)
            {
               dsLimits.Remove(item.Key);
               break;
            }

         mgrLog.PutLog(ri.userid, ri.type.id, "", "ReturnLimit", "", ri.limit.ToString());
         SetDirty();
      }

      private void dgvItems_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         if (dgvItems.CurrentCell.ColumnIndex == clmnOverLimit.Index)
         {
            dgvItems.CommitEdit(DataGridViewDataErrorContexts.Commit);
            SetDirty();
         }
      }
   }
}
