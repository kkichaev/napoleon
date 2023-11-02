using System.Windows;
using System.Windows.Controls;

namespace Ads2017
{
   public partial class TaskPointTicket : UserControl, ITaskTicket
   {
      object storedObject = null;

      public TaskPointTicket()
      {
         InitializeComponent();
      }

      public static readonly DependencyProperty NumberProperty = DependencyProperty.Register(
         "Number", typeof(int), typeof(TaskPointTicket), new PropertyMetadata(-1));

      public static readonly DependencyProperty TimePlanProperty = DependencyProperty.Register(
         "TimePlan", typeof(string), typeof(TaskPointTicket), new PropertyMetadata(string.Empty));

      public static readonly DependencyProperty TimeFactProperty = DependencyProperty.Register(
         "TimeFact", typeof(string), typeof(TaskPointTicket), new PropertyMetadata(string.Empty));

      public static readonly DependencyProperty ClientProperty = DependencyProperty.Register(
         "Client", typeof(string), typeof(TaskPointTicket), new PropertyMetadata(string.Empty));

      public static readonly DependencyProperty AddressProperty = DependencyProperty.Register(
         "Address", typeof(string), typeof(TaskPointTicket), new PropertyMetadata(string.Empty));

      public static readonly DependencyProperty FactAddressProperty = DependencyProperty.Register(
         "FactAddress", typeof(string), typeof(TaskPointTicket), new PropertyMetadata(string.Empty));

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

      public string TimePlan
      {
         get { return (string)GetValue(TimePlanProperty); }
         set { SetValue(TimePlanProperty, value); }
      }

      public string TimeFact
      {
         get { return (string)GetValue(TimeFactProperty); }
         set { SetValue(TimeFactProperty, value); }
      }

      public string Client
      {
         get { return (string)GetValue(ClientProperty); }
         set { SetValue(ClientProperty, value); }
      }

      public string Address
      {
         get { return (string)GetValue(AddressProperty); }
         set { SetValue(AddressProperty, value); }
      }

      public string FactAddress
      {
         get { return (string)GetValue(FactAddressProperty); }
         set { SetValue(FactAddressProperty, value); }
      }
   }
}
