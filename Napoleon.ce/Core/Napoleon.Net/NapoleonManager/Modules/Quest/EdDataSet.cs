using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class EdDataSet : UserControl, IQuestItem
   {
      public EdDataSet()
      {
         InitializeComponent();
         cbDataSet.SelectedIndex = 0;
      }

      public List<QuestionItemValue> GetValues()
      {
         List<QuestionItemValue> result = new List<QuestionItemValue>();
         QuestionItemValue v = new QuestionItemValue();
         v.value = cbDataSet.SelectedItem.ToString();
         result.Add(v);
         return result;
      }

      public void SetValues(List<QuestionItemValue> list)
      {
         if (list.Count == 1)
         {
            foreach (String s in cbDataSet.Items)
               if (s.Equals(((QuestionItemValue)list[0]).value))
               {
                  cbDataSet.SelectedItem = s;
                  break;
               }
         }
      }
   }
}
