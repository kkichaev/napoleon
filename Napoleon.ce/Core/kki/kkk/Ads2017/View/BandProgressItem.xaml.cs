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
   /// Interaction logic for BandProgressItem.xaml
   /// </summary>
   public partial class BandProgressItem : UserControl
   {
      public BandProgressItem()
      {
         InitializeComponent();
      }

      public static DependencyProperty BandWidthProperty = DependencyProperty.Register("BandWidth",
         typeof(double), typeof(BandProgressItem), new PropertyMetadata(0.0));

      public static DependencyProperty TitleProperty = DependencyProperty.Register("Title",
         typeof(string), typeof(BandProgressItem), new PropertyMetadata(string.Empty));

      public static DependencyProperty ProgressColorProperty = DependencyProperty.Register("ProgressColor",
         typeof(Brush), typeof(BandProgressItem), new PropertyMetadata(Brushes.White));

      public double BandWidth
      {
         get { return (double)GetValue(BandWidthProperty); }
         set { SetValue(BandWidthProperty, value); }
      }

      public string Title
      {
         get { return (string)GetValue(TitleProperty); }
         set { SetValue(TitleProperty, value); }
      }

      public Brush ProgressColor
      {
         get { return (Brush)GetValue(ProgressColorProperty); }
         set { SetValue(ProgressColorProperty, value); }
      }

      public double Progress { get; set; }
   }
}
