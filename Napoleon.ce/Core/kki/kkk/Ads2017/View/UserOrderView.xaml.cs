using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;

namespace Ads2017
{
   public partial class UserOrderView : UserControl
   {
      public UserOrderView()
      {
         InitializeComponent();
      }

      public static readonly DependencyProperty ReadedProperty = DependencyProperty.Register(
        "Readed", typeof(bool), typeof(UserOrderView), new PropertyMetadata(false, ReadedChanged));

      private static void ReadedChanged(DependencyObject d, DependencyPropertyChangedEventArgs e)
      {
         ((UserOrderView)d).Background = new SolidColorBrush(GetReadedColor(e));
      }

      public static Color GetReadedColor(DependencyPropertyChangedEventArgs e)
      {
         return (bool)e.NewValue ? Colors.LightGray : Colors.White;
      }

      public string User { get; set; }
      public DateTime Created { get; set; }
      public string Client { get; set; }
      public string Address { get; set; }
      public string Remark { get; set; }
      public object StoredObject { get; set; }
      public string UserID { get; internal set; }
      public string FIO { get; set; }
      public string Phone { get; internal set; }

      public bool Readed
      {
         get { return (bool)GetValue(ReadedProperty); }
         set { SetValue(ReadedProperty, value); }
      }

      private void NewTask_Click(object sender, System.Windows.RoutedEventArgs e)
      {
         TaskWindow w = new TaskWindow();

         DateTime d = DateTime.Now.Date;

         TaskQuery t = new TaskQuery
         {
            taskid = Task.GenId(),
            userid = UserID,
            start = d,
            finish = d.AddHours(24),
            notify = 15,
            client = Client,
            address = Address,
            fio = FIO,
            phone = Phone
         };

         w.Stored = t;

         if (w.ShowDialog() ?? false)
            MainWindow.window.Refresh();
      }

      private void MarkAsReaded_Click(object sender, System.Windows.RoutedEventArgs e)
      {
         UserOrderRemark u = new UserOrderRemark()
         {
            userid = UserID,
            created = Created,
            readed = 1
         };

         UpdateCollection upd = new UpdateCollection();
         upd.Add(UserOrderRemark.OBJECT_NAME).Add(u);
         Update.WriteObjects(upd, null);

         Readed = true;
      }
   }
}
