using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;

namespace Ads2017
{
   class TaskLabel : TextBlock
   {
      public int PeriodMin
      {
         get
         {
            return (int)(Finish.TotalMinutes - Start.TotalMinutes);
         }
      }

      public static readonly DependencyProperty StartProperty = DependencyProperty.Register
        ("Start", typeof(TimeSpan), typeof(TaskLabel), new PropertyMetadata(string.Empty));

      public static readonly DependencyProperty FinishProperty = DependencyProperty.Register
        ("Finish", typeof(TimeSpan), typeof(TaskLabel), new PropertyMetadata(string.Empty));

      public TimeSpan Start
      {
         get { return (TimeSpan)GetValue(StartProperty); }
         set { SetValue(StartProperty, value); }
      }

      public TimeSpan Finish
      {
         get { return (TimeSpan)GetValue(FinishProperty); }
         set { SetValue(FinishProperty, value); }
      }

      public new string Text
      {
         get { return (string)GetValue(TextProperty); }
         set { SetValue(TextProperty, value); }
      }
   }
}
