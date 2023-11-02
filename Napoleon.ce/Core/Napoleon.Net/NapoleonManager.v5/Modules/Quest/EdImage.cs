using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class EdImage : UserControl, IQuestItem
   {
      public EdImage()
      {
         InitializeComponent();
      }

      public List<QuestionItemValue> GetValues()
      {
         List<QuestionItemValue> result = new List<QuestionItemValue>();
         QuestionItemValue v = new QuestionItemValue();
         v.value = tbValue.Text.Trim();
         result.Add(v);
         return result;
      }

      public void SetValues(List<QuestionItemValue> list)
      {
         if (list.Count == 1)
            tbValue.Text = ((QuestionItemValue)list[0]).value;
      }

   }
}
