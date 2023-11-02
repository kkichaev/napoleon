using System.Windows;
using System.Windows.Input;

namespace Ads2017
{
   public partial class AboutWindow : Window
   {
      public AboutWindow()
      {
         InitializeComponent();
      }

      private void OpenWep_Click(object sender, MouseButtonEventArgs e)
      {
         System.Diagnostics.Process.Start((string)Application.Current.Resources["webpage"]);
      }

      private void OpenEmail_Click(object sender, MouseButtonEventArgs e)
      {
         System.Diagnostics.Process.Start((string)Application.Current.Resources["emaillink"]);
      }

      private void Close_Click(object sender, RoutedEventArgs e)
      {
         Close();
      }
   }
}
