using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   public interface IQuestItem
   {
      List<QuestionItemValue> GetValues();

      void SetValues(List<QuestionItemValue> list);
   }
}
