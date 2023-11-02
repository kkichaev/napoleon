using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   class FmManagerQuestionary : FmQuestionary, IQuestFactory
   {
      public FmManagerQuestionary()
      {
         Text = "Анкеты менеджера";
      }

      protected override void InitDataSet()
      {
         base.InitDataSet();
         dsQuestion = (DataSet<string, ManagerQuestion>)DataModule.Get(ManagerQuestion.OBJECT_NAME) ??
           new DataSet<string, ManagerQuestion>(ManagerQuestion.OBJECT_NAME);
         dsQuestion.Filter = "\"idquest\" is null or \"idquest\" is not null";
         dsDelQuest = new DataSet<string, ManagerQuestion>(ManagerQuestion.OBJECT_NAME, false);
      }

      Question IQuestFactory.Instance()
      {
         return new ManagerQuestion();
      }
   }
}
