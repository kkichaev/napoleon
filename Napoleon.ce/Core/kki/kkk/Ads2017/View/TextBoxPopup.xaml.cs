using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
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
   public partial class TextBoxPopup : UserControl
    {
      public TextBoxPopup()
      {
         InitializeComponent();
      }

      public void SetDataContext(object context)
      {
         listBox.DataContext = context;
      }

      public static DependencyProperty ValueProperty = DependencyProperty.Register(
         "Value", typeof(object), typeof(TextBoxPopup), new PropertyMetadata(null, ValueChanged));

      public static DependencyProperty ItemsProperty = DependencyProperty.Register(
         "Items", typeof(string), typeof(TextBoxPopup), new PropertyMetadata(string.Empty, ItemsChanged));

      private static void ValueChanged(DependencyObject d, DependencyPropertyChangedEventArgs e)
      {
         ((TextBoxPopup)d).textBox.Text = string.Format("{0}", e.NewValue);
      }

      private static void ItemsChanged(DependencyObject d, DependencyPropertyChangedEventArgs e)
      {
         ((TextBoxPopup)d).listBox.DataContext = null;
         ((TextBoxPopup)d).SetDataContext(e.NewValue.ToString().Split(','));
      }

      private void TextBoxPreviewMouseUp(object sender, MouseButtonEventArgs e)
      {
         Popup1.IsOpen = !Popup1.IsOpen;
      }

      public object Value
      {
         get { return (object)GetValue(ValueProperty); }
         set { SetValue(ValueProperty, value); }
      }

      public string Items
      {
         get { return (string)GetValue(ItemsProperty); }
         set { SetValue(ItemsProperty, value); }
      }

      private void Popup1_Opened(object sender, EventArgs e)
      {
         listBox.SelectedItem = Value;

         //if (listBox.SelectedIndex >= 0 && listBox.SelectedIndex < listBox.Items.Count)
         //{
         //   int add = Mode == TimeControlMode.Start ? 1 : 3;

         //   if ((add + listBox.SelectedIndex) > listBox.Items.Count - 1)
         //      add = listBox.Items.Count - 1 - listBox.SelectedIndex;

         //   listBox.ScrollIntoView(listBox.Items[listBox.SelectedIndex + add]);
         //}
      }

      private void ListViewItem_PreviewMouseLeftButtonDown(object sender, MouseButtonEventArgs e)
      {
         var item = sender as ListViewItem;

         if (item != null)
         {
            Value = item.DataContext;
            Popup1.IsOpen = false;
         }
      }
   }
}
