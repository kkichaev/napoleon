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
   /// Interaction logic for TicketStack.xaml
   /// </summary>
   public partial class TicketStack : StackPanel
   {
      const int MIN_CHILD_WIDTH = 400;
      public int hourRowHeight = 60;

      public TicketStack()
      {
         InitializeComponent();
      }

      protected override Size MeasureOverride(Size constraint)
      {
         const int MCW = 350;
         Size result = new Size();
         result.Width = 0;
         result.Height = 0;

         foreach (UIElement child in Children)
         {
            child.Measure(new Size(MCW, double.PositiveInfinity));

            if(result.Height < child.DesiredSize.Height)
               result.Height = child.DesiredSize.Height;

            result.Width += MCW;
         }

         return result;
      }

      protected override Size ArrangeOverride(Size arrangeSize)
      {
         if (arrangeSize.Width > MIN_CHILD_WIDTH * Children.Count)
         {
            double w = arrangeSize.Width / Children.Count;
            double top = 0;
            double left = 0;

            foreach (UIElement child in Children)
            {
               Rect r = new Rect(left, top, w, child.DesiredSize.Height);
               child.Arrange(r);
               left += w;
            }
         }
         else
            arrangeSize = base.ArrangeOverride(arrangeSize);

         return arrangeSize;
      }

      public static DependencyProperty HourRowHeightProperty = DependencyProperty.Register(
         "HourRowHeight", typeof(int), typeof(TicketStack), new PropertyMetadata(60, ValueChanged));

      private static void ValueChanged(DependencyObject d, DependencyPropertyChangedEventArgs e)
      {
         TicketStack t = (TicketStack)d;

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
