using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Threading;

namespace GRSoft.NapoleonManager
{
   public partial class FmTypeActionEdit : Form
   {
      public delegate void SelectActionType(ActionType type);

      public DataSet<string, ActionType> dsActionType;
      public DataSet<string, ActionType> dsRemActionType;
        
      private SelectActionType actionSelector;

      public FmTypeActionEdit()
      {
         InitializeComponent();

         dsActionType = (DataSet<string, ActionType>)DataModule.Get(ActionType.OBJECT_NAME) ??
            new DataSet<string, ActionType>(ActionType.OBJECT_NAME);
         dsRemActionType = new DataSet<string, ActionType>(ActionType.OBJECT_NAME, false);

         dgvQuestItems.AutoGenerateColumns = false;
         btnSave.Enabled = false;
      }

      public SelectActionType ActionTemplate { set { actionSelector = value; } }
      public void ClearSelector() { actionSelector = null; }

      private void btnAddType_Click(object sender, EventArgs e)
      {
         ActionType at = new ActionType();
         at.id = ActionType.GenId();
         at.name = "Новый тип акции";
         at.items = new List<QuestionItem>();

         lbTypes.Items.Add(at);

         lbTypes.SelectedItem = at;
         tbName.SelectAll();
         tbName.Focus();

         btnSave.Enabled = true;
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
      }

      void RefreshData()
      {
         DataModule.SetDataRepsonceHandlers(DataProcessed, DataConnectionError);

         List<IDataSet> updSet = new List<IDataSet>();
         updSet.Add(dsActionType);

         FmWait.ShowForm(this, DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
            updSet, FmWait.ProgressIndicator));
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         RefreshData();
      }

      //Окончание выборки, заполняются внутренние наборы
      void DataProcessed(System.Object setnder, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         Invoke(new EmptyParamHandler(delegate()
         {
            lbTypes.Items.Clear();

            foreach (ActionType at in dsActionType.Values)
               lbTypes.Items.Add(at);
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

      int lastSelection = -1;

      private void lbTypes_SelectedIndexChanged(object sender, EventArgs e)
      {
         ActionType at = lbTypes.SelectedItem as ActionType;

         if (at != null)
         {
            tbName.Text = at.name;

            List<QuestionItem> list = new List<QuestionItem>(at.items);
            dgvQuestItems.DataSource = list;
         }
         else
            ((ListBox)sender).SelectedIndex = lastSelection;
      }

      private void tbName_TextChanged(object sender, EventArgs e)
      {
         if (((TextBox)sender).ContainsFocus)
         {
            ActionType at = lbTypes.SelectedItem as ActionType;

            if (at != null)
            {
               at.name = ((TextBox)sender).Text;
               lbTypes.Items[lbTypes.SelectedIndex] = at;
               btnSave.Enabled = true;
            }
         }
      }

      private void btnAddItem_Click(object sender, EventArgs e)
      {
         ActionType at = lbTypes.SelectedItem as ActionType;

         if (at != null)
         {
            QuestionItem item = new QuestionItem();
            item.values = new List<QuestionItemValue>();
            FmQuestItemEdit form = new FmQuestItemEdit();
            if (form.ShowDialog(item) == DialogResult.OK)
            {
               QuestItemType qit = form.Quest;
               
               item.id = ActionType.GenId();
               item.number = at.items.Count + 1;
               UpdateItem(item, qit);
               at.items.Add(item);
               List<QuestionItem> list = new List<QuestionItem>(at.items);
               dgvQuestItems.DataSource = list;

               btnSave.Enabled = true;
            }
         }
      }

      private static void UpdateItem(QuestionItem item, QuestItemType qit)
      {
         item.text = qit.text;
         item.type = qit.code;
         item.optional = qit.optional ? 1 : 0;

         item.values.Clear();
         foreach (string val in qit.Values)
         {
            QuestionItemValue qiv = new QuestionItemValue();
            qiv.value = val;
            item.values.Add(qiv);
         }
      }

      private void btnEditItem_Click(object sender, EventArgs e)
      {
         if (dgvQuestItems.CurrentRow != null)
         {
            ActionType at = lbTypes.SelectedItem as ActionType;

            if (at != null)
            {
               FmQuestItemEdit form = new FmQuestItemEdit();
               QuestionItem item = dgvQuestItems.CurrentRow.DataBoundItem as QuestionItem;

               if (form.ShowDialog(item) == DialogResult.OK)
               {
                  QuestItemType qit = form.Quest;
                  UpdateItem(item, qit);
                  dgvQuestItems.Refresh();
                  btnSave.Enabled = true;
               }
            }
         }
      }

      private void btnDelItem_Click(object sender, EventArgs e)
      {
         if (dgvQuestItems.CurrentRow != null)
         {
             ActionType at = lbTypes.SelectedItem as ActionType;

             if (at != null)
             {
                at.items.Remove(dgvQuestItems.CurrentRow.DataBoundItem as QuestionItem);
                List<QuestionItem> list = new List<QuestionItem>(at.items);
                dgvQuestItems.DataSource = list;
                btnSave.Enabled = true;
             }
         }
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         SaveChanges();
      }

      private void btnDelType_Click(object sender, EventArgs e)
      {
         object o = lbTypes.SelectedItem;
         int idx = lbTypes.SelectedIndex;

         if (o != null && MessageBox.Show(this, "Тип акции будет удален, удалить?",
               "Вопрос", MessageBoxButtons.OKCancel) == DialogResult.OK)
         {
            btnSave.Enabled = true;
            ActionType at = (ActionType) o;
            dsRemActionType.Add(at.id, at);

            lbTypes.Items.Remove(o);

            if (idx != -1)
               if (idx < lbTypes.Items.Count)
                  lbTypes.SelectedIndex = idx;
               else if (lbTypes.Items.Count > 0)
                  lbTypes.SelectedIndex = 0;
         }
      }

      private void FmTypeActionEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (btnSave.Enabled == true && MessageBox.Show("Сохранить изменения", "Вопрос", 
            MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
         {
            btnSave_Click(btnSave, EventArgs.Empty);
         }
      }

      private void lbTypes_DoubleClick(object sender, EventArgs e)
      {
         if (actionSelector != null && ((ListBox)sender).SelectedItem != null)
         {
            if (!btnSave.Enabled || (btnSave.Enabled && 
               MessageBox.Show("Сохранить изменения", "Вопрос", MessageBoxButtons.OKCancel
                  , MessageBoxIcon.Question) == DialogResult.OK && SaveChanges()))
            {
               actionSelector((ActionType)((ListBox)sender).SelectedItem);
               Close();
            }
         }
      }

      private bool SaveChanges()
      {
         bool result = false;

         DataSet<string, ActionType> dataset = new DataSet<string, ActionType>(ActionType.OBJECT_NAME, false);
         List<IDataSet> wrSet = new List<IDataSet>();
         List<IDataSet> rmvSet = new List<IDataSet>();

         foreach (object o in lbTypes.Items)
         {
            ActionType at = (ActionType)o;
            dataset.Add(at.id, at);
         }

         wrSet.Add(dataset);
         rmvSet.Add(dsRemActionType);

         if (!DataModule.UpdateDataSet
            (wrSet, rmvSet, null, Config.GetConfig().GetConnection()))
            MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK,
               MessageBoxIcon.Error);
         else
         {
            dsRemActionType.Clear();
            btnSave.Enabled = false;
            result = true;
         }

         return result;
      }

      private void dgvQuestItems_CellDoubleClick(object sender, DataGridViewCellEventArgs e)
      {
         btnEditItem_Click(this, EventArgs.Empty);
      }

      class Data : GRSoft.Network.DataObject { };
      class Result : GRSoft.Network.DataObject
      {
         [ItemType(typeof(ResultItem))]
         public List<ResultItem> items = null;
      }

      class ResultItem : GRSoft.Network.DataObject
      {
         public string name = string.Empty;
      }

      private void btnCheck_Click(object sender, EventArgs e)
      {
         Data data = new Data();

         Result result = new Result();
         SimpleDataSet<Result> resultSet = new SimpleDataSet<Result>("Result", false);
         Report r = new Report("actchk", data, resultSet);

         Thread th = DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), r, FmWait.ProgressIndicator);
         FmWait.ShowForm(this, th);
         th.Join();
         FmWait.CloseForm();

         if (resultSet.Count > 0)
         {
            Result res = resultSet[0];
            if (res.items.Count == 0)
               MessageBox.Show("Акции проверены, все типы присутствуют в базе данных");
            else if (res.items.Count > 0 && MessageBox.Show("Найдены типы акциий, которых нет в базе, создать автоматически?",
               "Вопрос", MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
            {
               btnSave.Enabled = true;
               ActionType at = null;
               foreach (ResultItem item in res.items)
               {
                  at = new ActionType();
                  at.id = ActionType.GenId();
                  at.name = item.name;
                  at.items = new List<QuestionItem>();

                  lbTypes.Items.Add(at);
               }

               if (at != null)
                  lbTypes.SelectedItem = at;
            }
         }
         else
            MessageBox.Show("Ошибка получения данных");
      }
   }
}
