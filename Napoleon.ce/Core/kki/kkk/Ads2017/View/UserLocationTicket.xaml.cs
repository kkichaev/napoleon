using System.Windows;
using System.Windows.Controls;

namespace Ads2017
{
    public partial class UserLocationTicket : UserControl
   {
      public UserLocationTicket()
      {
         InitializeComponent();
      }

      public static readonly DependencyProperty NumberProperty = DependencyProperty.Register(
         "Number", typeof(int), typeof(UserLocationTicket), new PropertyMetadata(-1));

      public static readonly DependencyProperty TimeProperty = DependencyProperty.Register(
         "Time", typeof(string), typeof(UserLocationTicket), new PropertyMetadata(string.Empty));

      public static readonly DependencyProperty ClientProperty = DependencyProperty.Register(
         "Client", typeof(string), typeof(UserLocationTicket), new PropertyMetadata(string.Empty));

      public object StoredObject { get; set; }

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

      public string Client
      {
         get { return (string)GetValue(ClientProperty); }
         set { SetValue(ClientProperty, value); }
      }

      public string UserId { get; set; }
   }
}
