using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.IO;

namespace GRSoft.NapoleonManager
{
   public interface IQuestFactory
   {
      Question Instance();
   }

   public partial class FmQuestionary : Form, IQuestFactory
   {
      protected IDataSet dsQuestion;
      protected IDataSet dsDelQuest;
      protected DataSet<string, Category> dsCategory;
      protected DataSet<string, Producer> dsProducer;
      protected DataSet<string, AgentQuest> dsAgentQuest;

      public FmQuestionary()
      {
         InitializeComponent();
         InitDataSet();
      }

      protected virtual void InitDataSet()
      {
         dsQuestion = (DataSet<string, Question>)DataModule.Get(Question.OBJECT_NAME) ??
            new DataSet<string, Question>(Question.OBJECT_NAME);
         dsQuestion.Filter = "\"idquest\" is null or \"idquest\" is not null";
         dsDelQuest = new DataSet<string, Question>(Question.OBJECT_NAME, false);
         dsProducer = (DataSet<string, Producer>)DataModule.Get(Producer.OBJECT_NAME) ??
            new DataSet<string, Producer>(Producer.OBJECT_NAME);
         dsCategory = (DataSet<string, Category>)DataModule.Get(Category.OBJECT_NAME) ??
            new DataSet<string, Category>(Category.OBJECT_NAME);
         dsAgentQuest = (DataSet<string, AgentQuest>)DataModule.Get(AgentQuest.OBJECT_NAME) ??
            new DataSet<string, AgentQuest>(AgentQuest.OBJECT_NAME);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         if (btnSave.Enabled && MessageBox.Show("Данные были изменены, но не записаны в базу данных. Записать?",
            "Вопрос", MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
            btnSave_Click(null, null);

         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsQuestion);
         list.Add(dsProducer);
         list.Add(dsCategory);
         list.Add(dsAgentQuest);

         dsDelQuest.Clear();
         btnSave.Enabled = false;
         
         DataModule.SetDataRepsonceHandlers(DataModule_DataProcessed, DataModule_OnDataResponceError);
         
         FmWait.ShowForm(this,
            DataModule.RefreshGiveSets(
            Config.GetConfig().GetConnection(), list, FmWait.ProgressIndicator)
         );
      }

      void DataModule_DataProcessed(object sender, EventArgs e)
      {
         FmWait.CloseForm();
         DataModule.ClearEvents();
         Invoke(new InvokeDelegate(RefreshData));
      }

      void DataModule_OnDataResponceError(EDataResponse e)
      {
         FmWait.CloseForm();
         DataModule.ClearEvents();
         MessageBox.Show(e.Msg);
      }

      void RefreshData()
      {
         List<Question> list = new List<Question>();
         foreach (Question o in dsQuestion.Data)
            list.Add(o);

         if (list.Count > 0 && list[0].Number == 0)
         {
            int index = 1;
            foreach (Question q in list)
               q.number = index++;

            MessageBox.Show("Выполнена автоматическая нумерация анкет, сохраните изменеия в базе", "Внимание",
               MessageBoxButtons.OK, MessageBoxIcon.Warning);
            btnSave.Enabled = true;
         }

         list.Sort(new Comparison<Question>(delegate(Question q1, Question q2) { return q1.Number.CompareTo(q2.Number); }));
         dgvQuestion.DataSource = list;
      }

      private void recreateNumber()
      {
         List<Question> list = (List<Question>)dgvQuestion.DataSource;
         
         int index = 1;
         foreach (Question q in list)
            q.number = index++;

         dgvQuestion.Refresh();
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         Question quest = FmQuestEdit.ShowInstance(null, this);

         if (quest != null)
         {
            quest.number = dgvQuestion.RowCount + 1;
            dsQuestion.Add(quest.idquest, quest);
            quest.InvalidateHtml();
            btnSave.Enabled = true;
            RefreshData();
            dgvQuestion.CurrentCell = dgvQuestion.Rows[dgvQuestion.RowCount - 1].Cells[0];
         }
      }

      private void FmQuestionary_Load(object sender, EventArgs e)
      {
         btnSave.Enabled = false;
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         List<IDataSet> wrSet = new List<IDataSet>();
         List<IDataSet> rmvSet = new List<IDataSet>();

         if (dsQuestion.Count > 0)
            wrSet.Add(dsQuestion);

         if (dsDelQuest.Count > 0)
            rmvSet.Add(dsDelQuest);

         if (!DataModule.UpdateDataSet
            (wrSet, rmvSet, null, Config.GetConfig().GetConnection()))
            MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK,
               MessageBoxIcon.Error);
         else
            btnSave.Enabled = false;
      }

      private Question GetSelectedQuest()
      {
         DataGridViewRow dgv = dgvQuestion.CurrentRow;

         if (dgv != null)
            return dgv.DataBoundItem as Question;
         else
            return null;
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         Question quest = GetSelectedQuest();

         if (quest != null && FmQuestEdit.ShowInstance(quest, this) != null)
         {
            dgvQuestion.Update();
            quest.InvalidateHtml();
            btnSave.Enabled = true;
         }
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         Question quest = GetSelectedQuest();

         if (quest != null &&
            MessageBox.Show("Запись будет удалена, удалить?", "Вопрос", MessageBoxButtons.OKCancel,
            MessageBoxIcon.Question) == DialogResult.OK)
         {
            if (!dsDelQuest.ContainsKey(quest.idquest))
               dsDelQuest.Add(quest.idquest, quest);

            if (dsQuestion.ContainsKey(quest.idquest))
            {
               dsQuestion.Remove(quest.idquest);
               RefreshData();
            }

            recreateNumber();
            btnSave.Enabled = true;
         }

      }

      private void FmQuestionary_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK && btnSave.Enabled == true &&
            MessageBox.Show("Сохранить изменения", "Вопрос", MessageBoxButtons.OKCancel
               , MessageBoxIcon.Question) == DialogResult.OK)
         {
            btnSave_Click(null, null);
         }
      }

      private void btnUp_Click(object sender, EventArgs e)
      {
         Question q = GetSelectedQuest();

         if (q != null && q.number > 1)
         {
            List<Question> list = (List<Question>) dgvQuestion.DataSource;
            q.number--;
            list[q.number-1].number++;
            list.Sort(new Comparison<Question>(delegate(Question q1, Question q2) { return q1.Number.CompareTo(q2.Number); }));
            dgvQuestion.Refresh();
            dgvQuestion.CurrentCell = dgvQuestion.Rows[q.number-1].Cells[0];
            btnSave.Enabled = true;
         }
      }

      private void btnDown_Click(object sender, EventArgs e)
      {
         Question q = GetSelectedQuest();
         List<Question> list = (List<Question>)dgvQuestion.DataSource;

         if (q != null && q.number < list.Count)
         {
            list[q.number].number--;
            q.number++;
            list.Sort(new Comparison<Question>(delegate(Question q1, Question q2) { return q1.Number.CompareTo(q2.Number); }));
            dgvQuestion.Refresh();
            dgvQuestion.CurrentCell = dgvQuestion.Rows[q.number - 1].Cells[0];
            btnSave.Enabled = true;
         }
      }

      private void dgvQuestion_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         Question quest = dgvQuestion.Rows[e.RowIndex].DataBoundItem as Question;

         if (quest != null)
            btnEdit.Enabled = !dsAgentQuest.ContainsKey(quest.idquest);
      }

      private void btnCopy_Click(object sender, EventArgs e)
      {
         Question quest = GetSelectedQuest();

         if (quest != null)
         {
            Question copy = quest.Copy();
            copy.number = dgvQuestion.RowCount + 1;
            dsQuestion.Add(copy.idquest, copy);
            btnSave.Enabled = true;
            RefreshData();
            dgvQuestion.CurrentCell = dgvQuestion.Rows[dgvQuestion.RowCount - 1].Cells[0];
         }
      }

      private void btnHtmlView_Click(object sender, EventArgs e)
      {
         Question quest = GetSelectedQuest();

         if (quest != null)
         {
            string html = System.IO.Path.GetTempPath() + @"\tmp.html";
            using (StreamWriter sw = new StreamWriter(html))
            {
               sw.Write(quest.html);
               sw.Flush();
               sw.Close();
               OpenLink.NewWindow(String.Format("\"{0}\"", html));
            }
         }
      }

      public Question Instance()
      {
         return new Question();
      }
   }
}
