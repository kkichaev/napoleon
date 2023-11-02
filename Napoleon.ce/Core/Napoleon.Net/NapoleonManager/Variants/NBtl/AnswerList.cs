using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class AnswerList : UserControl, IAnswerControl
   {
      private QuestionItem item;

      public AnswerList(QuestionItem item)
      {
         InitializeComponent();

         this.item = item;
         label1.Text = item.id;

         foreach (QuestionItemValue v in item.values)
         {
            checkedListBox1.Items.Add(v);
         }

         this.checkedListBox1.Height = (checkedListBox1.GetItemRectangle(0).Height + 3) * checkedListBox1.Items.Count;
         Height = label1.Height + checkedListBox1.Height + 10;
      }

      public void SetValue(List<AnswerItem> value)
      {
         foreach (AnswerItem a in value)
         {
            for (int i = 0; i < checkedListBox1.Items.Count; i++)
            {
               QuestionItemValue v = checkedListBox1.Items[i] as QuestionItemValue;

               if (v != null && v.value.Equals(a.answer))
               {
                  checkedListBox1.SetItemChecked(i, true);
               }
            }
         }
      }

      public List<AnswerItem> GetValue()
      {
         List<AnswerItem> res = new List<AnswerItem>();

         foreach (Object c in checkedListBox1.CheckedItems)
         {
            QuestionItemValue q = c as QuestionItemValue;

            if (q != null)
            {
               AnswerItem a = new AnswerItem();
               a.id = item.id;
               a.iditem = item.iditem;
               a.type = item.type;
               a.answer = q.value;
               res.Add(a);
            }
         }

         return res;
      }
   }
}
