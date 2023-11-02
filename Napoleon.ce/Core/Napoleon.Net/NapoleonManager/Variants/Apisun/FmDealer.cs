using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;

namespace GRSoft.NapoleonManager
{
   public partial class FmDealer : Form
   {
      DataSet<string, Dealer> dsDealer;
      DataSet<string, Dealer> dsDelDealer;
      private SearchEngine searchEngine;

      public FmDealer()
      {
         InitializeComponent();

         dsDealer = (DataSet<string, Dealer>)DataModule.Get(Dealer.OBJECT_NAME) ??
            new DataSet<string, Dealer>(Dealer.OBJECT_NAME);
         dsDelDealer = new DataSet<string, Dealer>(Dealer.OBJECT_NAME);
         btnSave.Enabled = false;
         btnAdd.Enabled = false;
         btnEdit.Enabled = false;
         btnDel.Enabled = false;

         searchEngine = new SearchEngine(new FindDataGridObject(dgvDealer, 1)); 
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         DataModule.SetDataRepsonceHandlers(DataProcessed, DataConnectionError);

         List<IDataSet> updSet = new List<IDataSet>();
         updSet.Add(dsDealer);

         FmWait.ShowForm(this, DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
            updSet, FmWait.ProgressIndicator));

         btnAdd.Enabled = true;
         btnEdit.Enabled = true;
         btnDel.Enabled = true;
      }

      //Окончание выборки, заполняются внутренние наборы
      void DataProcessed(System.Object setnder, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         Invoke(new EmptyParamHandler(delegate()
         {
            List<Dealer> list = new List<Dealer>();
            list.AddRange(dsDealer.Values);
            list.Sort(new Comparison<Dealer>(delegate(Dealer d1, Dealer d2) { return d1.id.CompareTo(d2.id); }));

            BindingList<Dealer> blist = new BindingList<Dealer>(list);
            dgvDealer.DataSource = blist;
         }));
      }

      //Произошла ошибка в соединении
      private void DataConnectionError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         Invoke(new EmptyParamHandler(delegate
         {
            const string TITLE = "Ошибка";

            MessageBox.Show(e.Msg, TITLE, MessageBoxButtons.OK,
               MessageBoxIcon.Error);
         }));
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         Dealer d = FmDealerEdit.Edit(null);

         if (d != null)
         {
            if (!dsDealer.ContainsKey(d.id))
               dsDealer.Add(d.id, d);
            else
               dsDealer[d.id] = d;

            BindingList<Dealer> list = (BindingList<Dealer>)dgvDealer.DataSource;
            
            if(list == null)
            {
               list = new BindingList<Dealer>();
               dgvDealer.DataSource = list;
            }

            list.Add(d);

            btnSave.Enabled = true;
         }
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         List<IDataSet> wrSet = new List<IDataSet>();
         List<IDataSet> rmvSet = new List<IDataSet>();

         if (dsDealer.Count > 0)
            wrSet.Add(dsDealer);

         if (dsDelDealer.Count > 0)
            rmvSet.Add(dsDelDealer);
         
         if (!DataModule.UpdateDataSet
            (wrSet, rmvSet, null, Config.GetConfig().GetConnection()))
            MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK,
               MessageBoxIcon.Error);
         else
            btnSave.Enabled = false;
      }

      private void btnDel_Click(object sender, EventArgs e)
      {

         DataGridViewRow row = dgvDealer.CurrentRow;

         if (row != null && MessageBox.Show("Запись будет удалена, удалить?",
            "Вопрос", MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
         {
            Dealer d = (Dealer)row.DataBoundItem;
            dsDelDealer.Add(d.id, d);
            dgvDealer.Rows.Remove(row);
            btnSave.Enabled = true;
         }
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         DataGridViewRow row = dgvDealer.CurrentRow;

         if (row != null)
         {
            Dealer ot = (Dealer)row.DataBoundItem;

            Dealer orgType = FmDealerEdit.Edit(ot);

            if (orgType != null)
            {
               btnSave.Enabled = true;
               dgvDealer.Invalidate();
            }
         }
      }

      private void FmOrgType_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (btnSave.Enabled == true && MessageBox.Show("Сохранить изменения?", "Вопрос",
            MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
         {
            btnSave_Click(btnSave, EventArgs.Empty);
         }
      }

      private void btnFindUp_Click(object sender, EventArgs e)
      {
         searchEngine.find(tbFind.Text, Direction.UP);
      }

      private void btnFindDown_Click(object sender, EventArgs e)
      {
         searchEngine.find(tbFind.Text, Direction.DOWN);
      }

      private void tbFind_KeyDown(object sender, KeyEventArgs e)
      {
         if (e.KeyCode == Keys.Enter)
            searchEngine.find(tbFind.Text, Direction.DOWN);
      }

      private void dgvDealer_MouseDown(object sender, MouseEventArgs e)
      {
         DataGridView.HitTestInfo info = dgvDealer.HitTest(e.X, e.Y);

         if (e.Button == MouseButtons.Left && info != null)
         {
            DataGridViewRow row = dgvDealer.Rows[info.RowIndex];
            DoDragDrop(row.DataBoundItem, DragDropEffects.Copy);
         }
      }
   }

   public class Dealer : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "Dealer";

      [KeyField]
      public string id = string.Empty;
      public string name = string.Empty;

      public string Id { get { return id; } }
      public string Name { get { return name; } }

      public override string ToString()
      {
         return Name;
      }
   }
}
