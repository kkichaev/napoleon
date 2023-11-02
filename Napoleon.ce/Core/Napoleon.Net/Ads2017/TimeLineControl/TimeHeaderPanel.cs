using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;

namespace Ads2017
{
   class TimeHeaderPanel : TicketStack
   {
      protected override System.Windows.Size MeasureOverride(Size constraint)
      {
         Size result =  base.MeasureOverride(constraint);

         return result;
      }
   }
}
