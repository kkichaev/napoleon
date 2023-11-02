using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;

namespace Ads2017
{
   public partial class TimeControl : UserControl
   {
      public enum TimeControlMode { Start, Finish };

      public TimeControl()
      {
         InitializeComponent();
         InitControl();
      }

      public void SetDataContext(object context)
      {
         listBox.DataContext = context;
      }

      public static DependencyProperty ModeProperty = DependencyProperty.Register(
         "Mode", typeof(TimeControlMode), typeof(TimeControl), new PropertyMetadata(TimeControlMode.Start, ModeChanged));

      private static void ModeChanged(DependencyObject d, DependencyPropertyChangedEventArgs e)
      {
         ((TimeControl)d).InitControl();
      }

      public static DependencyProperty TimeProperty = DependencyProperty.Register(
         "Time", typeof(TimeSpan), typeof(TimeControl), new PropertyMetadata(TimeSpan.MinValue, TimeChanged));

      private static void TimeChanged(DependencyObject d, DependencyPropertyChangedEventArgs e)
      {
         ((TimeControl)d).textBox.Text = string.Format("{0:hh}:{0:mm}", e.NewValue);
      }

      private void TextBoxPreviewMouseUp(object sender, MouseButtonEventArgs e)
      {
         Popup1.IsOpen = !Popup1.IsOpen;
      }

      public TimeSpan Time
      {
         get { return (TimeSpan)GetValue(TimeProperty);}
         set { SetValue(TimeProperty,value);}
      }

      private void Popup1_Opened(object sender, EventArgs e)
      {
         listBox.SelectedItem = Time;

         if (listBox.SelectedIndex >= 0 && listBox.SelectedIndex < listBox.Items.Count)
         {
            int add = Mode == TimeControlMode.Start ? 1 : 3;

            if ((add + listBox.SelectedIndex) > listBox.Items.Count - 1)
               add = listBox.Items.Count - 1 - listBox.SelectedIndex;

            listBox.ScrollIntoView(listBox.Items[listBox.SelectedIndex + add]);
         }
      }

      private void ListViewItem_PreviewMouseLeftButtonDown(object sender, MouseButtonEventArgs e)
      {
         var item = sender as ListViewItem;

         if (item != null)
         {
            Time = (TimeSpan)item.DataContext;
            Popup1.IsOpen = false;
         }
      }

      public TimeControlMode Mode
      {
         get { return (TimeControlMode)GetValue(ModeProperty); }
         set { SetValue(ModeProperty, value); }
      }

      private void InitControl()
      {
         if (Mode == TimeControlMode.Start)
            SetDataContext(CreateStartDataItems());
         else
            SetDataContext(CreateFinishDataItems());
      }

      private object CreateStartDataItems()
      {
         IList<TimeSpan> list = new ObservableCollection<TimeSpan>();

         for (int i = 0; i < 24; i++)
         {
            TimeSpan ts = new TimeSpan(i, 0, 0);
            list.Add(ts);
            ts = new TimeSpan(i, 30, 0);
            list.Add(ts);
         }

         return list;
      }

      private object CreateFinishDataItems()
      {
         IList<TimeSpan> list = new ObservableCollection<TimeSpan>();

         for (int i = 0; i < 24; i++)
         {

            TimeSpan ts = new TimeSpan(i, 30, 0);
            list.Add(ts);
            ts = new TimeSpan(i + 1, 0, 0);
            list.Add(ts);
         }

         return list;
      }

      class TimeSpanEx
      {
      }
   }
}
