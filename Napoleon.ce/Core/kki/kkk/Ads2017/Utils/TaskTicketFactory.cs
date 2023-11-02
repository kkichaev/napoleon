using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;

namespace Ads2017
{
   class TaskTicketFactory
   {
      public UserControl CreateTicket(MapObj mapObj)
      {
         UserControl result = null;

         if (mapObj is TaskPoint tp)
            result = CreateTaskPointTicket(tp);
         else if (mapObj is StopPoint sp)
            result = CreateStopPointTicket(sp);

         return result;
      }

      private UserControl CreateStopPointTicket(StopPoint p)
      {
         TaskStopTicket ticket = new TaskStopTicket()
         {
            StoredObject = p,
            Number = p.Num,
            Time = string.Format("{0} {1}", (int)p.TimeRange, ((App)Application.Current).resource.GetString("minutes")),
            TimeFact = p.TimeFact,
            FactAddress = p.FactAddress
         };

         return ticket;
      }

      private UserControl CreateTaskPointTicket(TaskPoint p)
      {
         TaskPointTicket ticket = new TaskPointTicket()
         {
            StoredObject = p,
            Number = p.Num,
            TimePlan = p.TimePlan,
            TimeFact = p.TimeFact,
            Client = p.Client,
            Address = p.Address,
            FactAddress = p.FactAddress
         };

         ticket.address.Foreground = p.isNearest ? Brushes.Black : Brushes.Red;

         return ticket;
      }
   }
}
