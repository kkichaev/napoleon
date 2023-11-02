using Microsoft.Win32;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.IO;
using System.Reflection;
using System.Resources;
using System.Threading;
using System.Web.Script.Serialization;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Controls.Primitives;
using System.Windows.Input;

namespace Ads2017
{
    public partial class TaskWindow : Window, Update.IDataLoadProcess
    {
        private TaskQuery task = null;
        private Dictionary<string, TaskAttachment> update = new Dictionary<string, TaskAttachment>();
        private Dictionary<string, TaskAttachment> delete = new Dictionary<string, TaskAttachment>();
        private JavaScriptSerializer json = new JavaScriptSerializer();
        private List<TemplateData> templates = new List<TemplateData>();
        private List<TemplateAddress> addressList = new List<TemplateAddress>();
        private List<TemplateContact> contactList = new List<TemplateContact>();

        public TaskWindow()
        {
            InitializeComponent();

            attachments.ItemsSource = new ObservableCollection<TaskAttachment>();
            LoadTemplates();
            btnOK.IsEnabled = ManagerHelper.Instance.CurrentUser.CanWriteTask;
        }

        private void LoadTemplates()
        {
            templates.Clear();
            IEnumerable list = Update.GetList(AddressTemplate.OBJECT_NAME);
            foreach (AddressTemplate a in list)
            {
                TemplateData t = (TemplateData)json.Deserialize(a.template, typeof(TemplateData));
                templates.Add(t);
            }
        }

        public string Text
        {
            get { return tbText.Text; }
            set { tbText.Text = value; }
        }

        private void OK_Click(object sender, RoutedEventArgs e)
        {
            if (task != null && ManagerHelper.Instance.CanEdit(task.manager))
            {
                if (StartDateTime < FinishDateTime)
                {
                    Thread t = new Thread(Save);
                    t.Start();
                }
                else
                {
                    ((App)Application.Current).resource.GetString("check_interval");
                }
            }
        }

        private void Save(object obj)
        {
            UpdateCollection upd = new UpdateCollection();

            Dispatcher.Invoke(new Action(() =>
            {
                progressLayout.Visibility = System.Windows.Visibility.Visible;

             /*Поля объекта привязаны к форме*/
                upd.Add(Task.OBJECT_NAME).Add(Stored);
            }));

            upd.Add(TaskAttachment.OBJECT_NAME, update.Values);
            UpdateCollection remove = new UpdateCollection();
            remove.Add(TaskAttachment.OBJECT_NAME, delete.Values);
            bool result = Update.WriteObjects(upd, remove);

            Dispatcher.Invoke(new Action(() =>
               {
                   progressLayout.Visibility = System.Windows.Visibility.Hidden;

                   if (result)
                       DialogResult = result;
                   else
                       StdDialog.UpdateErrMsg(this);
               }));
        }

        public DateTime StartDateTime
        {
            get { return GetDateTime(startDate, startTime); }
            set { SetDateTime(startDate, startTime, value); }
        }

        public DateTime FinishDateTime
        {
            get
            {
                DateTime result = GetDateTime(finishDate, finishTime);
                return result;
            }
            set { SetDateTime(finishDate, finishTime, value); }
        }

        private void SetDateTime(DatePicker dp, TimeControl cp, DateTime date)
        {
            dp.SelectedDate = date.Date;
            cp.Time = date.TimeOfDay;
        }

        private DateTime GetDateTime(DatePicker dp, TimeControl cp)
        {
            TimeSpan t = cp.Time;
            DateTime? d = dp.SelectedDate;
            DateTime date = d ?? DateTime.MaxValue;
            DateTime result = date.Date + t;

            return result;
        }

        public string FIO
        {
            get { return tbFIO.Text.Trim(); }
            set { tbFIO.Text = value; }
        }

        public string Phone
        {
            get { return tbPhone.Text.Trim(); }
            set { tbPhone.Text = value; }
        }

        public string Client
        {
            get { return tbClient.Text.Trim(); }
            set { tbClient.Text = value; }
        }

        public string Address
        {
            get { return tbAddress.Text.Trim(); }
            set { tbAddress.Text = value; }
        }

        public TaskQuery Stored
        {
            get { return GetTask(); }
            set { SetTask(value); }
        }

        public int Notify
        {
            get { return GetNotify(); }
            set { SetNotify(value); }
        }

        private void SetNotify(int value)
        {
            if (value == 0)
                notify.Value = ((App)Application.Current).resource.GetString("not");
            else
                notify.Value = value;
        }

        private int GetNotify()
        {
            Int32.TryParse(notify.Value.ToString(), out int result);
            return result;
        }

        private void SetTask(TaskQuery value)
        {
            StartDateTime = value.start;
            FinishDateTime = value.finish;
            Phone = value.phone;
            Client = value.client;
            Address = value.address;
            FIO = value.fio;
            Text = value.text;
            Notify = value.notify;

            task = value;

            btnOK.IsEnabled = ManagerHelper.Instance.CanEdit(task.manager);
        }

        private TaskQuery GetTask()
        {
            task.start = StartDateTime;
            task.finish = FinishDateTime;
            task.phone = Phone;
            task.client = Client;
            task.address = Address;
            task.fio = FIO;
            task.text = Text;
            task.notify = Notify;

            return task;
        }

        private void Window_Loaded(object sender, RoutedEventArgs e)
        {
            List<TaskAttachment> list = TaskAttachmentHelper.Instance.GetAttach(task.taskid);
            ObservableCollection<TaskAttachment> data = (ObservableCollection<TaskAttachment>)attachments.ItemsSource;

            foreach (TaskAttachment i in list)
                data.Add(i);
        }

        private void AddAddachment_Click(object sender, RoutedEventArgs e)
        {
            OpenFileDialog dlg = new OpenFileDialog();

            if (dlg.ShowDialog() == true)
            {
                TaskAttachment a = new TaskAttachment()
                {
                    id = GRSoft.Network.DataObject.GenId(),
                    taskid = this.task.taskid,
                    name = dlg.SafeFileName,
                    data = File.ReadAllBytes(dlg.FileName)
                };

                ObservableCollection<TaskAttachment> data = (ObservableCollection<TaskAttachment>)attachments.ItemsSource;
                data.Add(a);

                update.Add(a.id, a);

                TaskAttachmentHelper.Instance.Add(task.taskid, a);
            }
        }

        private void DeleteExecuted(object sender, ExecutedRoutedEventArgs e)
        {
            if (attachments.SelectedItem is TaskAttachment i)
            {
                ObservableCollection<TaskAttachment> data = (ObservableCollection<TaskAttachment>)attachments.ItemsSource;
                data.Remove(i);

                delete.Add(i.id, i);
                TaskAttachmentHelper.Instance.Remove(task.taskid, i);
            }
        }

        private void Attachments_MouseDoubleClick(object sender, MouseButtonEventArgs e)
        {
            if (attachments.SelectedItem is TaskAttachment a)
            {
                if (a.data == null)
                {
                    Update.QueryList query = new Update.QueryList();
                    query.Add(TaskAttachment.OBJECT_NAME, string.Format("\"id\"='{0}'", a.id));
                    Update.StdDataRefresh(query, this);
                }
                else
                    OpenAttachment(a);
            }
        }

        public void DoLoadData(Update.UpdateResult data)
        {
            List<TaskAttachment> list = data.GetList<TaskAttachment>(TaskAttachment.OBJECT_NAME);

            if (list.Count > 0)
            {
                TaskAttachment a = list[0];
                OpenAttachment(a);
            }
        }

        private static void OpenAttachment(TaskAttachment a)
        {
            string name = Path.GetTempPath() + a.name;
            File.WriteAllBytes(name, a.data);
            System.Diagnostics.Process.Start(name);
        }

        public UIElement[] GetRefreshControls()
        {
            return new UIElement[0];
        }

        private void ListViewClient_PreviewMouseLeftButtonDown(object sender, MouseButtonEventArgs e)
        {
            if (sender is ListViewItem item)
            {
                TemplateData tmplData = (TemplateData)item.DataContext;
                Client = tmplData.Name;
                ppClient.IsOpen = false;

                addressList.Clear();
                addressList.AddRange(tmplData.Address);
            }
        }

        private void ListViewAddress_PreviewMouseLeftButtonDown(object sender, MouseButtonEventArgs e)
        {
            if (sender is ListViewItem item)
            {
                TemplateAddress tmplAddress = (TemplateAddress)item.DataContext;
                Address = tmplAddress.Name;
                ppAddress.IsOpen = false;

                contactList.Clear();
                contactList.AddRange(tmplAddress.Contact);
            }
        }

        private void OpenAddressList(string text)
        {
            lbAddress.Items.Clear();

            foreach (TemplateAddress t in addressList)
            {
                if (t.Name.ToUpper().Contains(text.ToUpper()))
                {
                    lbAddress.Items.Add(t);
                }
            }

            ppAddress.IsOpen = lbAddress.Items.Count > 0;
        }

        private void OpenFIOList(string text)
        {
            lbFIO.Items.Clear();

            foreach (TemplateContact t in contactList)
            {
                if (t.Name.ToUpper().Contains(text.ToUpper()))
                {
                    lbFIO.Items.Add(t);
                }
            }

            ppFIO.IsOpen = lbFIO.Items.Count > 0;
        }

        private void ListViewFIO_PreviewMouseLeftButtonDown(object sender, MouseButtonEventArgs e)
        {
            if (sender is ListViewItem item)
            {
                TemplateContact tmplContact = (TemplateContact)item.DataContext;
                FIO = tmplContact.Name;
                Phone = tmplContact.Phone;
                ppAddress.IsOpen = false;
            }
        }

        private void TextBoxAddress_GotFocus(object sender, RoutedEventArgs e)
        {
            TextBoxAddress_PreviewTextInput(sender, null);
        }

        private void TextBoxAddress_LostFocus(object sender, RoutedEventArgs e)
        {
            ppAddress.IsOpen = false;
        }

        private void TextBoxClient_PreviewTextInput(object sender, TextCompositionEventArgs e)
        {
            string text = ((TextBox)sender).Text.Trim() + e.Text.Trim();
            OpenClientList(text);
        }

        private void OpenClientList(string text)
        {
            addressList.Clear();
            contactList.Clear();

            lbClient.Items.Clear();

            foreach (TemplateData t in templates)
            {
                if (t.Name.ToUpper().Contains(text.ToUpper()))
                {
                    lbClient.Items.Add(t);
                }
            }

            ppClient.IsOpen = lbClient.Items.Count > 0;
        }

        private void TextBoxFIO_GotFocus(object sender, RoutedEventArgs e)
        {
            TextBoxFIO_PreviewTextInput(sender, null);
        }

        private void TextBoxFIO_LostFocus(object sender, RoutedEventArgs e)
        {
            ppFIO.IsOpen = false;
        }

        private void TextBoxPhone_LostFocus(object sender, RoutedEventArgs e)
        {
            ppFIO.IsOpen = false;
        }

        private void TextBoxPhone_GotFocus(object sender, RoutedEventArgs e)
        {
            TextBoxFIO_PreviewTextInput(sender, null);
        }

        private void TextBoxFIO_PreviewTextInput(object sender, TextCompositionEventArgs e)
        {
            string text = ((TextBox)sender).Text.Trim();
            OpenFIOList(text);
        }

        private void TextBoxAddress_PreviewTextInput(object sender, TextCompositionEventArgs e)
        {
            string text = ((TextBox)sender).Text.Trim();
            OpenAddressList(text);
        }

        delegate void OnEnterKeyDown(object sender);

        private void TextBoxClient_PreviewKeyDown(object sender, KeyEventArgs e)
        {
            e.Handled = UpdatePopup(ppClient, lbClient, e.Key, (s) =>
            {
                if (s is TemplateData t)
                {
                    tbClient.PreviewTextInput -= TextBoxClient_PreviewTextInput;
                    Client = t.Name;
                    tbClient.PreviewTextInput += TextBoxClient_PreviewTextInput;

                    addressList.Clear();
                    addressList.AddRange(t.Address);
                }
            });
        }

        private bool UpdatePopup(Popup poup, System.Windows.Controls.ListView list, Key k, OnEnterKeyDown hanler)
        {
            bool result = false;

            if (poup.IsOpen)
            {
                if (k == Key.Down)
                {
                    if (list.Items.Count > 0)
                        list.SelectedIndex += 1;

                    result = true;
                }
                else if (k == Key.Up)
                {
                    if (list.Items.Count > 0 && list.SelectedIndex > 0)
                        list.SelectedIndex -= 1;

                    result = true;
                }
                else if (k == Key.Enter)
                {
                    hanler(list.SelectedItem);


                    poup.IsOpen = false;
                    result = true;
                }
            }

            return result;
        }

        private void TextBoxAddress_PreviewKeyDown(object sender, KeyEventArgs e)
        {
            e.Handled = UpdatePopup(ppAddress, lbAddress, e.Key, (s) =>
            {
                if (s is TemplateAddress t)
                {
                    lbAddress.PreviewTextInput -= TextBoxAddress_PreviewTextInput;
                    Address = t.Name;
                    lbAddress.PreviewTextInput += TextBoxAddress_PreviewTextInput;

                    contactList.Clear();
                    contactList.AddRange(t.Contact);
                }
            });
        }

        private void TextBoxPhone_PreviewKeyDown(object sender, KeyEventArgs e)
        {
            e.Handled = UpdatePopup(ppFIO, lbFIO, e.Key, (s) =>
            {
                if (lbFIO.SelectedItem is TemplateContact t)
                {
                    lbFIO.PreviewTextInput -= TextBoxFIO_PreviewTextInput;
                    FIO = t.Name;
                    Phone = t.Phone;
                    lbFIO.PreviewTextInput += TextBoxFIO_PreviewTextInput;
                }
            });
        }

        private void Window_PreviewKeyDown(object sender, KeyEventArgs e)
        {
            if (e.Key == Key.Enter && (Keyboard.IsKeyDown(Key.LeftCtrl) || Keyboard.IsKeyDown(Key.RightCtrl)))
            {
                OK_Click(null, null);
            }
        }

        private void Cancel_Click(object sender, RoutedEventArgs e)
        {
            Close();
        }
    }
}
