using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Reflection;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmQuestItemEdit : Form
   {
      public delegate bool CheckIdQuestItem(string id);
      public  CheckIdQuestItem checkIdQuesItem;

      public FmQuestItemEdit()
      {
         InitializeComponent();

         foreach (QuestItemType type in types)
            cbType.Items.Add(type);

         cbType.SelectedIndex = 0;

#if BTL || TDLider || Birsnek
         cbNecessary.Visible = true;
#elif CottonClub
         cbNecessary.Visible = true;
         // Ни кто не знает зачем мы закрыли для них тему, когда то....
         //label3.Visible = false;
         //tbShortQuest.Visible = false;
#else
         //cbNecessary.Visible = false;
#endif
      }

      private void cbType_SelectedIndexChanged(object sender, EventArgs e)
      {
         splitContainer1.Panel2.Controls.Clear();
         UserControl uc = (((ComboBox)sender).SelectedItem as QuestItemType).Editor;
         uc.Dock = DockStyle.Fill;
         splitContainer1.Panel2.Controls.Add(uc);
      }

      public virtual QuestItemType Quest
      {
         get
         {
            QuestItemType result = cbType.SelectedItem as QuestItemType;

            if (result != null && splitContainer1.Panel2.Controls.Count == 1)
            {
               IQuestItem iqi = splitContainer1.Panel2.Controls[0] as IQuestItem;
               result.Values = iqi.GetValues().ToArray();
            }

            result.text = tbText.Text;
            result.optional = !cbNecessary.Checked;
            return result;
         }
      }

      internal virtual DialogResult ShowDialog(QuestionItem questionItem)
      {
         tbText.Text = questionItem.Text;
         cbNecessary.Checked = questionItem.optional == 0;
         foreach (QuestItemType qit in cbType.Items)
            if (qit.code.Equals(questionItem.type))
            {
               cbType.SelectedItem = qit;
               break;
            }

         if (splitContainer1.Panel2.Controls.Count == 1)
         {
            IQuestItem iqi = splitContainer1.Panel2.Controls[0] as IQuestItem;

            if (iqi != null)
               iqi.SetValues(questionItem.values);
         }

         return ShowDialog();
      }

      private void ShowMessageShortQuestErr(string msg, FormClosingEventArgs e)
      {
         tbShortQuest.Focus();
         MessageBox.Show(msg, "Ошибка",
            MessageBoxButtons.OK, MessageBoxIcon.Error);
         e.Cancel = true;
      }

      private void FmQuestItemEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK)
         {
            if (tbShortQuest.Visible && tbShortQuest.Text.Trim().Length == 0)
               ShowMessageShortQuestErr("Поле \"Тема вопроса\" не может быть пустым.", e);
            else if (checkIdQuesItem != null && !checkIdQuesItem(tbShortQuest.Text))
               ShowMessageShortQuestErr("Ошибка в \"Тема вопроса\",  значение не должно повторяться в анкете.", e);
            else if (tbShortQuest.Text.Contains("\""))
               ShowMessageShortQuestErr("\"Тема вопроса\" содержит недопустимый симовол (\") - ковычки", e);
         }
      }

      public string ItemId { get { return tbShortQuest.Text; } }

      public QuestItemType[] types = { new QuestItemType(QuestionItem.TEXT, new EdTxt()),
                                            new QuestItemType(QuestionItem.NUMBER, new EdNumber()),
                                            new QuestItemType(QuestionItem.LIST, new EdList()),
                                            new QuestItemType(QuestionItem.SET, new EdList()),
                                            new QuestItemType(QuestionItem.BOOLEAN, CreateControlByType(typeof(EdBoolean)))
#if !CottonClub
                                     
#endif
                                            ,new QuestItemType(QuestionItem.DATASET, new EdDataSet())
                                            
#if QUESTION_REPORT_PYTHON
                                             ,new QuestItemType(QuestionItem.IMAGE, new EdImage())
                                             ,new QuestItemType(QuestionItem.SPINNER, new EdSpinner())
                                             ,new QuestItemType(QuestionItem.NUMBER_LIST, new EdNumberList())
#endif
                                            };

      public static UserControl CreateControlByType(Type controlType)
      {
         Type type = FormEntries.GetFormType(controlType);
         ConstructorInfo ci = type.GetConstructor(Type.EmptyTypes);
         return (UserControl)ci.Invoke(new object[] { });
      }
   }

   
   public partial class QuestItemType
   {
      public int code;
      public string text;
      private UserControl editor;
      private string name;
      private QuestionItemValue[] values;
      public bool optional = false;

      public QuestItemType(int code, UserControl editor)
      {
         this.code = code;
         this.editor = editor;
         name = QuestionItem.TypeToStr(code);
      }

      public override string ToString()
      {
         return name;
      }

      public UserControl Editor { get { return editor; } }

      public QuestionItemValue[] Values
      {
         get
         {
            if (values != null)
            {
               QuestionItemValue[] result = new QuestionItemValue[values.Length];
               Array.Copy(values, result, result.Length);

               return result;
            }
            else
               return new QuestionItemValue[0];
         }

         set
         {
            values = new QuestionItemValue[value.Length];
            Array.Copy(value, values, values.Length);
         }
      }
   }
}
