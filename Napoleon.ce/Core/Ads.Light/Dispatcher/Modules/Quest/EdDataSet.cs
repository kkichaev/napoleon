using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.Ads.Dispatcher
{
   public partial class EdDataSet : UserControl, IQuestItem
   {
      public EdDataSet()
      {
         InitializeComponent();
         cbDataSet.SelectedIndex = 0;
      }

      public List<string> GetValues()
      {
         List<string> result = new List<string>();
         result.Add(cbDataSet.SelectedItem.ToString());
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
