using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   internal interface IUnitItemType: IItemType
   {
   }

   internal class UnitItemFactory
   {
      public enum UnitItemCode { INVALID_UNIT_ID = -1, KILO_ID = 0, 
         PIECE_ID = 1, ROUBLE_ID = 2}

      public static IUnitItemType CreateUnitItem(UnitItemCode id)
      {
         switch (id)
         {
            case UnitItemCode.KILO_ID: return new KiloUnit();
            case UnitItemCode.PIECE_ID: return new PieceUnit();
            case UnitItemCode.ROUBLE_ID: return new RoubleUnit();
            default: return new UnitItemStub();
         }
      }

      public static List<IUnitItemType> CreateItemList()
      {
         List<IUnitItemType> result = new List<IUnitItemType>();

         foreach (UnitItemCode id in Enum.GetValues(typeof(UnitItemCode)))
            result.Add(CreateUnitItem(id));

         return result;
      }
   }

   abstract class UnitItemType : PItemType, IUnitItemType
   {
      public UnitItemType(string name, int code)
         :base(name, code)
      {
      }
   }

   class KiloUnit : UnitItemType
   {
      private const string NAME = "кг.";

      public KiloUnit()
         :base(NAME, (int)UnitItemFactory.UnitItemCode.KILO_ID)
      { 
      }
   }

   class PieceUnit : UnitItemType
   {
      private const string NAME = "шт.";

      public PieceUnit()
         : base(NAME, (int)UnitItemFactory.UnitItemCode.PIECE_ID)
      {
      }
   }

   class RoubleUnit : UnitItemType
   {
      private const string NAME = "руб.";

      public RoubleUnit()
         : base(NAME, (int)UnitItemFactory.UnitItemCode.ROUBLE_ID)
      { 
      }
   }

   class UnitItemStub : UnitItemType
   {
      private const string NAME = "Тип для единиц измерния не реализован, ошибка!";

      public UnitItemStub()
         : base(NAME, (int)UnitItemFactory.UnitItemCode.INVALID_UNIT_ID)
      { 
      }
   }
}
