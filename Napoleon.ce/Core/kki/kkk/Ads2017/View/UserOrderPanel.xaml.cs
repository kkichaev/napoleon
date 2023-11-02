using System.Collections;
using System.Windows.Controls;

namespace Ads2017
{
   public partial class UserOrderPanel : UserControl
   {
      public UserOrderPanel()
      {
         InitializeComponent();
      }

      object source = null;

      public object ItemsSource
      {
         get { return source; }
         set { SetItemsSource(value); }
      }

      private void SetItemsSource(object value)
      {
         AddToPanel(value);
         source = value;
      }

      private void AddToPanel(object value)
      {
         panel.Children.Clear();

         if (value is IEnumerable en)
         {
            foreach (object o in en)
            {
               if (o is UserOrder p)
               {
                  UserOrderView view = new UserOrderView()
                  {
                     StoredObject = p,
                     User = p.User,
                     Created = p.Created,
                     Address = p.Address,
                     Client = p.Client,
                     Remark = p.Remark,
                     UserID = p.userid,
                     Phone = p.phone,
                     FIO = p.fio,
                     Readed = p.readed == 1
                  };

                  panel.Children.Add(view);
               }
            }
         }
      }
   }
}
