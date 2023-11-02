using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class AnswerText : UserControl, IAnswerControl
   {
      QuestionItem item;

      public AnswerText(QuestionItem item)
      {
         InitializeComponent();
         this.item = item;
         label1.Text = item.id;
      }


      public void SetValue(List<AnswerItem> value)
      {
         if (value.Count > 0)
         {
            textBox1.Text = value[0].answer;
         }
      }


      public List<AnswerItem> GetValue()
      {
         List<AnswerItem> res = new List<AnswerItem>();

         AnswerItem a = new AnswerItem();
         a.id = item.id;
         a.iditem = item.iditem;
         a.type = item.type;
         a.answer = textBox1.Text.Trim();

         res.Add(a);

         return res;
      }
   }
}
