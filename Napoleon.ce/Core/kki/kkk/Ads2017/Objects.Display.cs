using System;
using System.Collections.Generic;
using System.Text;
using System.Windows;

namespace Ads2017
{
   class Objects
   {
   }

   public partial class UserOrder : BaseDocument
   {
      public int marked = 0;

      public string User { get { return agent != null ? agent.Name : userid; } }
      public string Number { get { return string.Empty; } }
      public string Address { get { return address; } }
      public string Client { get { return client; } }
      public string Phone { get { return phone; } }
      public int Marked { get { return marked; } }
      public string Report { get { return remark; } }
      public string FIO {  get { return fio; } }
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
               result = ((App)Application.Current).resource.GetString("new_task_status");
               break;
            case RESOLVED:
               result = ((App)Application.Current).resource.GetString("resolved_task_status");
               break;
            case REJECT:
               result = ((App)Application.Current).resource.GetString("reject_task_status");
               break;
            case APPLY:
               result = ((App)Application.Current).resource.GetString("apply_task_status");
               break;
            case INWORK:
               result = ((App)Application.Current).resource.GetString("inwork_task_status");
               break;
         }

         return result;
      }

   }

   public partial class MessageArchive
   {
      public DateTime Date
      {
         get { return date; }
         set { date = value; }
      }

      public string Message
      {
         get { return message; }
         set { message = value; }
      }
   }
}
