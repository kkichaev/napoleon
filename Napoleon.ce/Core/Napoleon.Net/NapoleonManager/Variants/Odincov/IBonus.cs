using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   interface IBonus
   {
      /**
       * Записать данные в бонус
       */ 
      bool save(BonusDef bonus);

      /**
       * Загрузить данные из бонуса
       */
      void load(BonusDef curBonus);

      event EventHandler ValueChanged;
   }
}
