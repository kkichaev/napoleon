using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.Ads.Dispatcher
{
   public interface IQuestItem
   {
      List<string> GetValues();

      void SetValues(List<QuestionItemValue> list);
   }
}
