using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class AnswerSpinner : UserControl, IAnswerControl
   {
      QuestionItem item;

      public AnswerSpinner(QuestionItem item)
      {
         InitializeComponent();
         this.item = item;

         label1.Text = item.id;
         comboBox1.Items.Add("");

         item.values.ForEach((x) => comboBox1.Items.Add(x.value));
      }

      public void SetValue(List<AnswerItem> value)
      {
         if (value.Count > 0)
         {
            AnswerItem a = value[0];

            comboBox1.SelectedItem = a.answer;
         }
      }

      public List<AnswerItem> GetValue()
      {
         List<AnswerItem> res = new List<AnswerItem>();

         AnswerItem a = new AnswerItem();
         a.id = item.id;
         a.iditem = item.iditem;
         a.type = item.type;
         a.answer = comboBox1.Text;
         res.Add(a);

         return res;
      }
   }
}
