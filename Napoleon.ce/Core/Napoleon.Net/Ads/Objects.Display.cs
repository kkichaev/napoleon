using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class Objects
   {
   }

   public partial class Note : BaseDocument
   {
      public string Number { get { return string.Empty; } }
      public string Address { get { return address; } }
      public string Client { get { return client; } }
   }

   public partial class Task
   {
      public const int NEW = 0;
      public const int RESOLVED = 1;
      public const int REJECT = 2;
      public const int APPLY = 4;
      public const int INWORK = 5;

      internal static string StatusToStr(int val)
      {
         string result = string.Empty;

         switch (val)
         {
            case NEW:
               result = "Непринята";
               break;
            case RESOLVED:
               result = "Выполнена";
               break;
            case REJECT:
               result = "Отклонена";
               break;
            case APPLY:
               result = "Принятая";
               break;
            case INWORK:
               result = "Выполняется";
               break;
         }

         return result;
      }

   }

   
}
