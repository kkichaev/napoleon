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
   /// Interaction logic for TaskStopTicket.xaml
   /// </summary>
   public partial class TaskStopTicket : UserControl, ITaskTicket
   {
      object storedObject = null;

      public TaskStopTicket()
      {
         InitializeComponent();
      }

      public static readonly DependencyProperty NumberProperty = DependencyProperty.Register(
         "Number", typeof(int), typeof(TaskStopTicket), new PropertyMetadata(-1));

      public static readonly DependencyProperty TimeFactProperty = DependencyProperty.Register(
         "TimeFact", typeof(string), typeof(TaskStopTicket), new PropertyMetadata(string.Empty));

      public static readonly DependencyProperty TimeProperty = DependencyProperty.Register(
         "Time", typeof(string), typeof(TaskStopTicket), new PropertyMetadata(string.Empty));

      public static readonly DependencyProperty FactAddressProperty = DependencyProperty.Register(
         "FactAddress", typeof(string), typeof(TaskStopTicket), new PropertyMetadata(string.Empty));

      public object StoredObject
      {
         get { return storedObject; }
         set { storedObject = value; }
      }

      public int Number
      {
         get { return (int)GetValue(NumberProperty); }
         set { SetValue(NumberProperty, value); }
      }

      public string Time
      {
         get { return (string)GetValue(TimeProperty); }
         set { SetValue(TimeProperty, value); }
      }

      public string TimeFact
      {
         get { return (string)GetValue(TimeFactProperty); }
         set { SetValue(TimeFactProperty, value); }
      }

      public string FactAddress
      {
         get { return (string)GetValue(FactAddressProperty); }
         set { SetValue(FactAddressProperty, value); }
      }
   }
}
