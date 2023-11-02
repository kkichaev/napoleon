using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace Ads2017
{
   public partial class TimeCell : UserControl
   {
      public delegate void TimeCellPastHandler(object source);
      private event TimeCellPastHandler timeCellPast;

      private bool isSelected;

      public TimeCell()
      {
         InitializeComponent();
      }

      public TimeSpan StartTime
      {
         get { return (TimeSpan)GetValue(StartTimeProperty); }
         set { SetValue(StartTimeProperty, value); }
      }

      public static DependencyProperty HourRowHeightProperty = DependencyProperty.Register(
         "HourRowHeight", typeof(int), typeof(TimeCell), new PropertyMetadata(60));

      public static DependencyProperty StartTimeProperty = DependencyProperty.Register(
         "StartTime", typeof(TimeSpan), typeof(TimeCell), new PropertyMetadata(TimeSpan.MinValue));

      public int HourRowHeight
      {
         get { return (int)GetValue(HourRowHeightProperty); }
         set { SetValue(HourRowHeightProperty, value); }
      }

      private void CanPastExecuted(object sender, CanExecuteRoutedEventArgs e)
      {
         e.CanExecute = !CopyBuffer.Instance().IsEmpty;
      }

      private void PastExecuted(object sender, ExecutedRoutedEventArgs e)
      {
         FireTimeCellPast(sender);
      }

      private void FireTimeCellPast(object sender)
      {
         if (timeCellPast != null)
            timeCellPast(sender);
      }

      public event TimeCellPastHandler TimeCellPast
      {
         add { timeCellPast = value; }
         remove { timeCellPast = null; }
      }

      public bool IsSelected {
         get { return isSelected; }
         set { SetSelection(value); }
      }

      private void SetSelection(bool value)
      {
         isSelected = value;

         //if (value)
         //   label.Background = new SolidColorBrush(Colors.Red);
         //else
         //   label.Background = new SolidColorBrush(Colors.AliceBlue);
      }

      public void PerformPaste()
      {
         FireTimeCellPast(this);
      }
   }
}
