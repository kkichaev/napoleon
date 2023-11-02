using System.Windows;
using System.Windows.Input;

namespace Ads2017
{
   public partial class AdminLoginWindow : Window, Update.IDataLoadProcess
   {
      public AdminLoginWindow()
      {
         InitializeComponent();
      }

      public void DoLoadData(Update.UpdateResult data)
      {
#if MANAGE_DIVISIONS
            Properties.Settings.Default.Save();
            new DivisionsEdit().Show();
            Close();
#else
         if (data.GetList<Agent>(Agent.OBJECT_NAME).Count > 0)
         {
            Properties.Settings.Default.Save();
            new UsersWindow().Show();
            Close();
         }
         else
            MessageBox.Show("Нет данных о пользователях системы АДС");
#endif
        }

        public UIElement[] GetRefreshControls()
      {
         return new UIElement[] { btnOK };
      }

      private void OK_Click(object sender, RoutedEventArgs e)
      {
         string pwd = password.Password.Trim();

         Update.AdmPwd = pwd;
         Update.QueryList query = new Update.QueryList();
         query.Add(Agent.OBJECT_NAME);
         Update.StdDataRefresh(query, this, true);
      }

      private void Window_Closing(object sender, System.ComponentModel.CancelEventArgs e)
      {
         Update.CloseSession();
      }

      private void Window_PreviewKeyDown(object sender, System.Windows.Input.KeyEventArgs e)
      {
         if (e.Key == Key.Enter)
         {
            OK_Click(null, null);
         }
      }
   }
}
