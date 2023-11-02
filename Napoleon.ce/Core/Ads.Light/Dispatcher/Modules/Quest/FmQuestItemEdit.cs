using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.Ads.Dispatcher
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

#if BTL
         cbNecessary.Visible = true;
#else
         cbNecessary.Visible = false;
#endif
      }

      private void cbType_SelectedIndexChanged(object sender, EventArgs e)
      {
         splitContainer1.Panel2.Controls.Clear();
         UserControl uc = (((ComboBox)sender).SelectedItem as QuestItemType).Editor;
         uc.Dock = DockStyle.Fill;
         splitContainer1.Panel2.Controls.Add(uc);
      }

      public QuestItemType Quest
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

      internal DialogResult ShowDialog(QuestionItem questionItem)
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
            if (tbShortQuest.Text.Trim().Length == 0)
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
                                            new QuestItemType(QuestionItem.BOOLEAN, new EdBoolean()),
                                            new QuestItemType(QuestionItem.DATASET, new EdDataSet())};
   }

   public class QuestItemType
   {
      public int code;
      public string text;
      private UserControl editor;
      private string name;
      private string[] values;
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

      public string[] Values
      {
         get
         {
            if (values != null)
            {
               string[] result = new string[values.Length];
               Array.Copy(values, result, result.Length);

               return result;
            }
            else
               return new string[0];
         }

         set
         {
            values = new string[value.Length];
            Array.Copy(value, values, values.Length);
         }
      }
   }
}
