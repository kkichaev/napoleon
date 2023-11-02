using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class EdNumber : UserControl, IQuestItem
   {
      public EdNumber()
      {
         InitializeComponent();
      }

      public List<QuestionItemValue> GetValues()
      {
         List<QuestionItemValue> result = new List<QuestionItemValue>();
         double val = 0;

         if (Double.TryParse(tbValue.Text, out val))
         {
            QuestionItemValue v = new QuestionItemValue();
            v.value = tbValue.Text.Trim();
            result.Add(v);
         }

         return result;
      }

      public void SetValues(List<QuestionItemValue> list)
      {
         double val = 0.0;
         if (list.Count == 1 && Double.TryParse(list[0].ToString(), out val))
            tbValue.Text = val.ToString();
      }
   }
}
