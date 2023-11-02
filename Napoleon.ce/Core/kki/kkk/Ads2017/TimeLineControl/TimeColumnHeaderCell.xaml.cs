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
   public partial class TimeColumnHeaderCell : UserControl
   {
      public TimeColumnHeaderCell()
      {
         InitializeComponent();
      }

      public static readonly DependencyProperty TextProperty = DependencyProperty.Register
        ("Text", typeof(string), typeof(TimeColumnHeaderCell), new PropertyMetadata(string.Empty, ValueChanged));

      private static void ValueChanged(DependencyObject d, DependencyPropertyChangedEventArgs e)
      {
         TimeColumnHeaderCell t = (TimeColumnHeaderCell)d;

         if (e.Property.Name == "Text")
         {
            t.textBox.Text = (string)e.NewValue;
         }
      }

      public string Text
      {
         get { return (string)GetValue(TextProperty); }
         set { SetValue(TextProperty, value); }
      }
   }
}
