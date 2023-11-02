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
using System.Windows.Shapes;

namespace Ads2017
{
    /// <summary>
    /// Interaction logic for Setting.xaml
    /// </summary>
    public partial class SettingWindow : Window, Update.IDataLoadProcess
    {
        public SettingWindow()
        {
            InitializeComponent();
        }

        private void OK_Window(object sender, RoutedEventArgs e)
        {
            Refresh();
        }

        private void Refresh()
        {
            ManagerHelper.Instance.CurrentUser = null;

            Update.QueryList list = new Update.QueryList();
            string where = string.Format("\"login\" = '{0}' and \"password\" = '{1}'", login.Text.Trim(), password.Text.Trim());
            list.Add(DivisionManager.OBJECT_NAME, where);
            list.Add(Division.OBJECT_NAME, "");

            Update.StdDataRefresh(list, this);
        }

        private void Cancel_Click(object sender, RoutedEventArgs e)
        {
            DialogResult = false;
            Close();
        }

        public void DoLoadData(Update.UpdateResult data)
        {
            if (ManagerHelper.Instance.SetCurrentUserData(data))
            {
                Properties.Settings.Default.Save();
                DialogResult = true;
                Close();
            }
        }

        public UIElement[] GetRefreshControls()
        {
            return new UIElement[] { btnOK, btnCancel };
        }

        private string OldLogin { get; set; }
        private string OldPassword { get; set; }
        private string OldIP { get; set; }
        private int OldPort { get; set; }

        private void Window_Loaded(object sender, RoutedEventArgs e)
        {
            OldLogin = Properties.Settings.Default.Login;
            OldPassword = Properties.Settings.Default.Password;
            OldIP = Properties.Settings.Default.IP;
            OldPort = Properties.Settings.Default.Port;
        }

        private void Window_Closed(object sender, EventArgs e)
        {
            if (!DialogResult.Value)
            {
                Properties.Settings.Default.Login = OldLogin;
                Properties.Settings.Default.Password = OldPassword;
                Properties.Settings.Default.IP = OldIP;
                Properties.Settings.Default.Port = OldPort;
            }
        }

        private void Window_PreviewKeyDown(object sender, KeyEventArgs e)
        {
            if (e.Key == Key.Enter)
                OK_Window(btnOK, null);
            else if (e.Key == Key.Escape)
                Cancel_Click(btnCancel, null);
        }
    }
}
