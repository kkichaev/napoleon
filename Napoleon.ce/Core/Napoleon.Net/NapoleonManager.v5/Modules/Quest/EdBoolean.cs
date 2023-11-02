using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class EdBoolean : UserControl, IQuestItem
   {
      public EdBoolean()
      {
         InitializeComponent();
      }

      public virtual List<QuestionItemValue> GetValues()
      {
         List<QuestionItemValue> result = new List<QuestionItemValue>();

         QuestionItemValue trueVal = new QuestionItemValue();
         trueVal.pos = 0;
         trueVal.value = tbTrue.Text.Trim();

         QuestionItemValue falseVal = new QuestionItemValue();
         falseVal.pos = 1;
         falseVal.value = tbFalse.Text.Trim();


         result.Add(trueVal);
         result.Add(falseVal);

         return result;
      }

      public virtual void SetValues(List<QuestionItemValue> list)
      {
         if (list.Count == 2)
         {
            list.Sort((x, y) => { return x.pos.CompareTo(y.pos); });

            QuestionItemValue trueVal = list[0] as QuestionItemValue;
            QuestionItemValue falseVal = list[1] as QuestionItemValue;

            tbTrue.Text = trueVal.value.Trim();
            tbFalse.Text = falseVal.value.Trim();
         }
      }
   }
}
