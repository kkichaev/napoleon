using System;
using System.Collections.Generic;
using System.Text;
using System.Data;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{

   /// <summary>
   /// Интерфейс для планов
   /// </summary>
   internal interface IPlanItemType : IItemType
   {
      List<IUnitItemType> GetUnits();
      IUnitItemType Unit { get; set; }
      double Value { get; set; }
      double Current { get; set; }
      double PlanValue { get; set; } 
      string UnitName { get; }
      string PlanName { get; }
      string Text { get; set; }
      //void Load(PlanItem plan);
      string DisplayData { get; }
      int Progress { get; }
      //PlanItem ToData();
      PlanItemFactory.PlanItemCode PlanItemCode { get; }
   }

   internal interface ISKUPlanItem : IPlanItemType
   {
      List<Price> SKU{get;}
   }

   /// <summary>
   /// Фабрика по созданию объектов IPlanItemType
   /// </summary>
   class PlanItemFactory
   {
      public enum PlanItemCode { INVALID_PLAN_ID = -1, SALES_VOLUME_ID = 0 , 
         COUNT_OUTLET_ID = 1, SKU_PLAN_ID = 2};

      //public static IPlanItemType CreatePlanItem(PlanItem planItem)
      //{
      //   IPlanItemType result = CreatePlanItem((PlanItemCode)
      //      Enum.ToObject(typeof(PlanItemCode), planItem.type));
      //   result.Load(planItem);

      //   return result;
      //}

      public static IPlanItemType CreatePlanItem(PlanItemCode code)
      {
         switch (code)
         {
            case PlanItemCode.SALES_VOLUME_ID: return new SalesVolume();
            case PlanItemCode.COUNT_OUTLET_ID: return new CountOutlet();
            case PlanItemCode.SKU_PLAN_ID: return new SKUPlan();
            default: return new PlanItemStub();
         }
      }

      public static List<IPlanItemType> CreateItemList(Plan plan)
      {
         return null;
         //List<IPlanItemType> result = new List<IPlanItemType>();

         //foreach (PlanItem planItem in plan.items)
         //{
         //   IPlanItemType planItemType = CreatePlanItem((PlanItemCode)
         //      Enum.ToObject(typeof(PlanItemCode), planItem.type));
         //   planItemType.Load(planItem);
         //   result.Add(planItemType);
         //}

         //return result;
      }

      public static List<IPlanItemType> CreateItemList()
      {
         List<IPlanItemType> result = new List<IPlanItemType>();

         foreach (PlanItemCode id in Enum.GetValues(typeof(PlanItemCode)))
         {
            if (id != PlanItemCode.INVALID_PLAN_ID)
               result.Add(CreatePlanItem(id));
         }

         return result;
      }
   }

   /// <summary>
   /// Класс представляет типы планов
   /// </summary>
   abstract class PlanItemType: PItemType, IPlanItemType
   {

      #region Public

      public PlanItemType(string name, int code)
         :base(name, code)
      {
      }

      public IUnitItemType Unit { get { return unit; } set { unit = value; } }
      #endregion

      protected List<IUnitItemType> units = new List<IUnitItemType>();
      public List<IUnitItemType> GetUnits() { return units; }
      public double Value { get { return value; } set { this.value = value; } }
      public double Current { get { return current; } set { this.current = value; } }
      public double PlanValue { get { return planValue; } set { planValue = value; } } 
      public string UnitName { get { return Unit == null ? string.Empty : Unit.Name; } }
      public string PlanName { get { return Name; } }
      public virtual string Text { get { return string.Empty; } set { } }
      public string DisplayData { get { return String.Format("{0}% ", 
         GetPercentValue()); } }
      //public string DisplayData { get { return String.Format(" {0}{1}  {2}  {3}% ", 
      //   Value, Unit, PlanValue, GetPercentValue()); } }

      public int Progress
      {
         get
         {
            if (Value == 0)
               return 0;
            else
               return (int)((100 * Current) / Value);
         }
      }

      private object GetPercentValue()
      {
         return Progress;
      }

      private IUnitItemType unit;
      private double value;
      private double planValue;
      private double current;

      //public virtual void Load(PlanItem planItem)
      //{
      //   Value = planItem.value;
      //   PlanValue = (planItem.value != 0) ? (planItem.current / planItem.value) * 100 : 0;
      //   Current = planItem.current;

      //   Unit = UnitItemFactory.CreateUnitItem((UnitItemFactory.UnitItemCode)
      //      Enum.ToObject(typeof(UnitItemFactory.UnitItemCode), planItem.unit));
      //}

      //public virtual PlanItem ToData()
      //{
      //   PlanItem planItem = new PlanItem();
      //   planItem.value = Value;
      //   planItem.current = Current;
      //   planItem.type = Code;
      //   planItem.unit = Unit.Code;

      //   return planItem;
      //}

      public PlanItemFactory.PlanItemCode PlanItemCode 
      { 
         get
         {
            return (PlanItemFactory.PlanItemCode)(Enum.ToObject(typeof(PlanItemFactory.PlanItemCode), Code));
         }
      }
   }

   class SalesVolume : PlanItemType
   {
      private const string NAME = "Объем продаж";

      public SalesVolume()
         :
         base(NAME, (int)PlanItemFactory.PlanItemCode.SALES_VOLUME_ID)
      {
         units.Add(new KiloUnit());
         units.Add(new PieceUnit());
         units.Add(new RoubleUnit());
      }

   }

   class CountOutlet : PlanItemType
   {
      private const string NAME = "Количество торговых точек";

      public CountOutlet()
         : base(NAME, (int)PlanItemFactory.PlanItemCode.COUNT_OUTLET_ID)
      {
         units.Add(new PieceUnit());
      }

   }

   class SKUPlan : PlanItemType, ISKUPlanItem
   {
      private const string NAME = "План по SKU";
      private List<Price> sku = new List<Price>();
      private string text = "";

      public SKUPlan()
         : base(NAME, (int)PlanItemFactory.PlanItemCode.SKU_PLAN_ID)
      {
         units.Add(new KiloUnit());
         units.Add(new PieceUnit());
         units.Add(new RoubleUnit());
      }

      public override string Text 
      { 
         get 
         { 
            return text; 
         }
         set { text = value; }
      }
      #region ISKUPlanItem Members

      public List<Price> SKU
      {
         get
         {
            return sku;
         }
      }

      #endregion

      //public override void Load(PlanItem planItem)
      //{
      //   //Мы думаем что прайс в этот момент уже кем-то получен...
      //   DataSet<string, Price> dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME);

      //   if (planItem.skuItems != null)
      //      foreach (PlanItemSKU planItemSku in planItem.skuItems)
      //         if (dsPrice.ContainsKey(planItemSku.id))
      //            SKU.Add(dsPrice[planItemSku.id]);

      //   text = planItem.text;
      //   Name = "SKU " + text;
      //   base.Load(planItem);
      //}

      //public override PlanItem ToData()
      //{
      //   PlanItem result = base.ToData();

      //   foreach (Price price in SKU)
      //   {
      //      PlanItemSKU sku = new PlanItemSKU();
      //      sku.id = price.id;
      //      result.skuItems.Add(sku);
      //   }
      //   result.text = text;

      //   return result;
      //}
   }

   class PlanItemStub : PlanItemType
   {
      private const string NAME = "Тип плана не реализован, ошибка!";

      public PlanItemStub()
         : base(NAME, (int)PlanItemFactory.PlanItemCode.INVALID_PLAN_ID)
      {
      }

   }

   
}
