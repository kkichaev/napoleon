using GRSoft.NapoleonManager.Properties;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   partial class ScriptDocuments
   {
      static void InitDocuments()
      {
         docs = new List<ScriptDocument>();
         //docs.Add(new ChooseClientDoc());
         docs.Add(new PurchaseDoc());
         docs.Add(new SellingDoc());
         docs.Add(new BlackSellingDoc());
         docs.Add(new ScriptPropDoc());
      }
   }

   internal class PurchaseDoc : ScriptDocument
   {
      internal PurchaseDoc()
         : base("PurchaseDoc", "Закуп", Resources.order_doc)
      {
      }
   }

   //internal class ChooseClientDoc : ScriptDocument
   //{
   //   internal ChooseClientDoc()
   //      : base("ChooseClientDoc", "Выбор клиента", Resources.client_card)
   //   {
   //   }
   //}

   internal class SellingDoc : ScriptDocument
   {
      internal SellingDoc()
         : base("SellingDoc", "Продажа сопутствующих товаров", Resources.visit_doc)
      {
      }
   }

   internal class BlackSellingDoc : ScriptDocument
   {
      internal BlackSellingDoc()
         : base("BSellingDoc", "Продажа наличная", Resources.visit_doc)
      {
      }
   }
   internal class ScriptPropDoc : ScriptDocument
   {
      internal ScriptPropDoc()
         : base("ScriptPropDoc", "Проверка информации о закупуке", Resources.remnants_doc)
      {
      }
   }
}
