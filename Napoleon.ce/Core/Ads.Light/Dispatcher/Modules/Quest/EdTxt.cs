using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.Ads.Dispatcher
{
   public partial class EdTxt : UserControl, IQuestItem
   {
      public EdTxt()
      {
         InitializeComponent();
      }

      public List<string> GetValues()
      {
         List<string> result = new List<string>();
         result.Add(tbValue.Text);
         return result;
      }

      public void SetValues(List<QuestionItemValue> list)
      {
         if (list.Count == 1)
            tbValue.Text = ((QuestionItemValue)list[0]).value;
      }

   }
}
