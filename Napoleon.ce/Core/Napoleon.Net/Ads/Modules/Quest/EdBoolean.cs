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

      public List<string> GetValues()
      {
         List<string> result = new List<string>();

         result.Add(tbTrue.Text);
         result.Add(tbFalse.Text);

         return result;
      }

      public void SetValues(List<QuestionItemValue> list)
      {
         if (list.Count == 2)
         {
            QuestionItemValue trueVal = list[0] as QuestionItemValue;
            QuestionItemValue falseVal = list[1] as QuestionItemValue;

            tbTrue.Text = trueVal.value;
            tbFalse.Text = falseVal.value;
         }
      }
   }
}
