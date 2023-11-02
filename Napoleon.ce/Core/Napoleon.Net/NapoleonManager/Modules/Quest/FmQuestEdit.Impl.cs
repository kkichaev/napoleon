using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Reflection;

namespace GRSoft.NapoleonManager
{
   public delegate void PostWorker();

   public partial class FmQuestEdit : Form
   {
      BindingList<QuestionItem> questItems;
      private List<QuestionAttach> questAttach;

#if BTL
      private DataSet<string, Category> dsCategory;
      private DataSet<string, Producer> dsProducer;
#endif

      protected void __Initing(Question question)
      {
         questItems = new BindingList<QuestionItem>();
         questAttach = new List<QuestionAttach>();

         gbPeriod.Enabled = false;
         dtpFrom.Value = DateTime.Now;
         dtpTill.Value = DateTime.Now.AddMonths(1);

#if BTL
         dsProducer = (DataSet<string, Producer>)DataModule.Get(Producer.OBJECT_NAME) ??
           new DataSet<string, Producer>(Producer.OBJECT_NAME);
         dsCategory = (DataSet<string, Category>)DataModule.Get(Category.OBJECT_NAME) ??
            new DataSet<string, Category>(Category.OBJECT_NAME);

         fillCategories();
         fillProducers();
#else
         gbDict.Visible = false;
         lblCat.Visible = false;
         lblProd.Visible = false;
         cbCat.Visible = false;
         cbProd.Visible = false;
         btnDelCat.Visible = false;
         btnCat.Visible = false;
         btnDelProd.Visible = false;
         btnProd.Visible = false;
#endif
         if (question != null)
         {
            tbName.Text = question.Name;
            tbText.Text = question.text;
            cbUsePeriod.Checked = question.IsUsePeriod();

            if (cbUsePeriod.Checked)
            {
               gbPeriod.Enabled = true;
               dtpFrom.Value = question.from;
               dtpTill.Value = question.till;
            }

            

            question.items.Sort((x, y) => { return x.number.CompareTo(y.number); });

            foreach (QuestionItem qi in question.items)
            {
               questItems.Add(qi);
            }

            int index = 1;
            if (question.items.Count > 0 && question.items[0].number == 0)
            {
               foreach (QuestionItem qi in question.items)
               {
                  qi.number = index++;
               }
            }

            foreach (QuestionAttach qa in question.attach)
            {
               questAttach.Add(qa);
            }
#if BTL
            if (question.category != null && question.category.Length> 0)
               foreach(Category c in cbCat.Items)
                  if (c.id.Equals(question.category))
                  {
                     cbCat.SelectedItem = c;
                     break;
                  }

            if (question.producer != null && question.producer.Length> 0)
               foreach(Producer p in cbProd.Items)
                  if (p.id.Equals(question.producer))
                  {
                     cbProd.SelectedItem = p;
                     break;
                  }
#endif
         }

         dgvQuestItem.DataSource = questItems;

#if QUESTION_REPORT_PYTHON
         btnAttach.Visible = true;
#endif
      }

#if BTL
      public void fillCategories()
      {
         cbCat.Items.Clear();

         List<Category> list = new List<Category>();
         list.AddRange(dsCategory.Values);
         list.Sort(new Comparison<Category>(delegate(Category c1, Category c2)
         { return c1.Name.CompareTo(c2.Name); }));

         cbCat.Items.AddRange(list.ToArray());
         
      }

      public void fillProducers()
      {
         cbProd.Items.Clear();

         List<Producer> list = new List<Producer>();
         list.AddRange(dsProducer.Values);
         list.Sort(new Comparison<Producer>(delegate(Producer p1, Producer p2)
         { return p1.Name.CompareTo(p2.Name); }));

         cbProd.Items.AddRange(list.ToArray());
      }
#endif
      public static Question ShowInstance(Question question, IQuestFactory factory)
      {
         Question result = null;

         Type formType = FormEntries.GetFormType(typeof(FmQuestEdit));
         ConstructorInfo ci = formType.GetConstructor(new Type[] {typeof(Question) });

         FmQuestEdit form = ci.Invoke(new object[]{question}) as FmQuestEdit;

         if (result == null)
            form.Text = "Добавить анкету";
         else
            form.Text = "Изменить анкету";

         if (form.ShowDialog() == DialogResult.OK)
         {
            result = question ??  factory.Instance();

            if (question == null)
               result.idquest = Question.GenId();

            result.name = form.tbName.Text;
            result.text = form.tbText.Text;
            result.html = MakeHtml();
            result.from = form.dtpFrom.Value.Date;
            result.till = form.dtpTill.Value.Date;
            result.items = form.GetQuestItems();
            result.attach = form.GetQuestAttach();
            
#if BTL
            Category c = form.cbCat.SelectedItem as Category;
            if (c != null)
               result.category = c.id;
            else
               result.category = string.Empty;

            Producer p = form.cbProd.SelectedItem as Producer;
            if (p != null)
               result.producer = p.id;
            else
               result.producer = string.Empty;
#endif

            form.UpdateQuest(result);
            result.SetUsePeriod(form.cbUsePeriod.Checked);
         }

         return result;
      }

      public virtual void UpdateQuest(Question quest)
      {
      }

      private List<QuestionAttach> GetQuestAttach()
      {
         List<QuestionAttach> result = new List<QuestionAttach>();
         result.AddRange(questAttach);
         return result;
      }

      private List<QuestionItem> GetQuestItems()
      {
         List<QuestionItem> result = new List<QuestionItem>();
         result.AddRange(questItems);
         return result;
      }

      private static string MakeHtml()
      {
         return string.Empty;
      }

      private Form CreateFmQuestItemEditForm()
      {
         Type formType = FormEntries.GetFormType(typeof(FmQuestItemEdit));
         ConstructorInfo ci = formType.GetConstructor(Type.EmptyTypes);
         return (Form)ci.Invoke(null);
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {

         FmQuestItemEdit form = CreateFmQuestItemEditForm() as FmQuestItemEdit;

         form.checkIdQuesItem = new FmQuestItemEdit.CheckIdQuestItem(CheckIdQuestItem);
         if (form.ShowDialog() == DialogResult.OK)
         {
            QuestItemType qyt = form.Quest;

            if (qyt != null)
            {
               QuestionItem qi = new QuestionItem();
               qi.iditem = Question.GenId();
               UpdateItem(qyt, qi);
               qi.id = form.ItemId;
               qi.number = questItems.Count + 1;
               questItems.Add(qi);
            }
         }
      }

      protected virtual void UpdateItem(QuestItemType qyt, QuestionItem qi)
      {
         qi.type = qyt.code;
         qi.text = qyt.text;
         qi.values = new List<QuestionItemValue>();
         qi.optional = qyt.optional ? 1 : 0;

         foreach (QuestionItemValue val in qyt.Values)
         {
            qi.values.Add(val.Copy());
         }
      }

      private void cbPeriod_CheckedChanged(object sender, EventArgs e)
      {
         gbPeriod.Enabled = ((CheckBox)sender).Checked;
      }

      private QuestionItem GetSelectedQuestItem()
      {
         DataGridViewRow row = dgvQuestItem.CurrentRow;

         if (row != null)
            return row.DataBoundItem as QuestionItem;
         else
            return null;
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         FmQuestItemEdit form = CreateFmQuestItemEditForm() as FmQuestItemEdit;
         
         QuestionItem questionItem = GetSelectedQuestItem();

         if (questionItem != null)
         {
            form.tbShortQuest.Text = questionItem.id;
            form.tbShortQuest.Enabled = false;
            if (form.ShowDialog(questionItem) == DialogResult.OK)
            {
               QuestItemType qit = form.Quest;

               if (qit != null)
               {
                  UpdateItem(qit, questionItem);
                  dgvQuestItem.Refresh();
               }
            }
         }
      }

      public bool CheckIdQuestItem(string id)
      {
         bool result = true;

         foreach (QuestionItem qi in questItems)
         {
            if (qi.id.Trim().ToUpper().Equals(id.Trim().ToUpper()))
            {
               result = false;
               break;
            }
         }
         return result;
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         DataGridViewRow row = dgvQuestItem.CurrentRow;

         if (row != null)
         {
            QuestionItem qi = row.DataBoundItem as QuestionItem;

            if (qi != null && MessageBox.Show("Запись будет удалена. Удалить?",
               "Вопрос", MessageBoxButtons.OKCancel,
               MessageBoxIcon.Question) == DialogResult.OK)
            {
               questItems.Remove(qi);
               SetItemsNumber();
            }
         }
      }

      private void SetItemsNumber()
      {
         int i = 1;
         foreach (DataGridViewRow row in dgvQuestItem.Rows)
         {
            if (row != null && (row.DataBoundItem as QuestionItem) != null)
            {
               QuestionItem qi = (QuestionItem)row.DataBoundItem;
               qi.number = i++;
            }
         }
      }

      private void btnUp_Click(object sender, EventArgs e)
      {
         QuestionItem selectedItem = GetSelectedQuestItem();

         if (selectedItem != null)
         {
            int pos = questItems.IndexOf(selectedItem);

            if (pos > 0)
            {
               selectedItem.number--;
               questItems.RemoveAt(pos);

               if (--pos >= 0)
                  questItems[pos].number++;

               questItems.Insert(pos, selectedItem);
               dgvQuestItem.Refresh();
               dgvQuestItem.CurrentCell = dgvQuestItem.Rows[pos].Cells[0];
            }
         }
      }

      private void btnDown_Click(object sender, EventArgs e)
      {
         QuestionItem selectedItem = GetSelectedQuestItem();

         if (selectedItem != null)
         {
            int pos = questItems.IndexOf(selectedItem);

            if (pos < questItems.Count-1)
            {
               selectedItem.number++;
               questItems.RemoveAt(pos);

               if (pos <= questItems.Count - 1)
                  questItems[pos].number--;

               questItems.Insert(++pos, selectedItem);
               dgvQuestItem.Refresh();
               dgvQuestItem.CurrentCell = dgvQuestItem.Rows[pos].Cells[0];
            }
         }
      }

      private void btnCat_Click(object sender, EventArgs e)
      {
#if BTL
         new FmCateg(new PostWorker(fillCategories)).Show();
#endif
      }

      private void btnProd_Click(object sender, EventArgs e)
      {
#if BTL
         new FmProducer(new PostWorker(fillProducers)).Show();
#endif
      }

      private void btnDelCat_Click(object sender, EventArgs e)
      {
         cbCat.SelectedIndex = -1;
      }

      private void btnDelProd_Click(object sender, EventArgs e)
      {
         cbProd.SelectedIndex = -1;
      }

      private void btnAttach_Click(object sender, EventArgs e)
      {
         FmQuestAttach form = new FmQuestAttach();
         form.Attach = questAttach;
         form.Show();
      }
   }
}
