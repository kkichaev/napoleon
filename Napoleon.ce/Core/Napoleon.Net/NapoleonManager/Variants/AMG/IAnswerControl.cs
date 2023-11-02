using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   interface IAnswerControl
   {
      void SetValue(List<AnswerItem> value);
      List<AnswerItem> GetValue();
   }
}
