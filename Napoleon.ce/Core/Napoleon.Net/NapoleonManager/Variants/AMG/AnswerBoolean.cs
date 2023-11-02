using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class AnswerBoolean : UserControl, IAnswerControl
   {
      QuestionItem item;

      public AnswerBoolean(QuestionItem item)
      {
         InitializeComponent();

         this.item = item;

         label1.Text = item.id;
         int height = 3;

         for (int i = 0; i < item.values.Count; i++)
         {
            RadioButton rb = new RadioButton();
            rb.Text = item.values[i].value;
            rb.Location = new Point(i, height);
            height += rb.Height;
            groupBox1.Controls.Add(rb);
         }

         groupBox1.Height = height + 3;
         Height = label1.Height + groupBox1.Height + 10;
      }

      public void SetValue(List<AnswerItem> value)
      {
         foreach (AnswerItem i in value)
         {
            foreach (Control c in groupBox1.Controls)
            {
               RadioButton rb = c as RadioButton;

               if (rb != null && rb.Text.Equals(i.answer))
               {
                  rb.Checked = true;
                  return;
               }
            }
         }
      }

      public List<AnswerItem> GetValue()
      {
         List<AnswerItem> res = new List<AnswerItem>();

         foreach (Control c in groupBox1.Controls)
         {
            RadioButton rb = c as RadioButton;

            if (rb != null && rb.Checked)
            {
               AnswerItem a = new AnswerItem();
               a.id = item.id;
               a.iditem = item.iditem;
               a.type = item.type;
               a.answer = rb.Text.Trim();
               res.Add(a);

               break;
            }
         }

         return res;
      }
   }
}
