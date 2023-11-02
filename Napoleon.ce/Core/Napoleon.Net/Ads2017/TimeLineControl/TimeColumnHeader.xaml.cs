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
   /// <summary>
   /// Interaction logic for TimeColumnHeader.xaml
   /// </summary>
   public partial class TimeColumnHeader : UserControl
   {
      public int hourRowHeight = 60;

      public TimeColumnHeader()
      {
         InitializeComponent();
      }

      public static DependencyProperty HourRowHeightProperty = DependencyProperty.Register(
         "HourRowHeight", typeof(int), typeof(TimeColumnHeader), new PropertyMetadata(60, ValueChanged));

      private static void ValueChanged(DependencyObject d, DependencyPropertyChangedEventArgs e)
      {
         TimeColumnHeader t = (TimeColumnHeader)d;

         if (e.Property.Name == "HourRowHeight")
         {
            t.hourRowHeight = (int)e.NewValue;
         }
      }

      public int HourRowHeight
      {
         get { return (int)GetValue(HourRowHeightProperty); }
         set { SetValue(HourRowHeightProperty, value); }
      }
   }
}
