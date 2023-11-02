using System.Windows.Controls;
using System.Windows.Media;

namespace Ads2017
{
   /// <summary>
   /// Interaction logic for AgentItemView.xaml
   /// </summary>
   public partial class AgentItemView : UserControl, IListViewItem
   {
      private bool selected = false;
      private Brush selectColor = new SolidColorBrush(Color.FromArgb(254, 0xAE, 0xD7, 0xFF));

      public AgentItemView()
      {
         InitializeComponent();
      }

      public string AgentName { get; internal set; }
      public object StoredObject { get; set; }

      public bool Selected
      {
         get { return selected; }
         set
         {
            selected = value;
            Background = value ? selectColor : Brushes.Transparent;
         }
      }

      private void SendMessageCkick(object sender, System.Windows.RoutedEventArgs e)
      {
         MessageWindow w = new MessageWindow();
         w.Target = ((TreeData)StoredObject).Node.StoredObject;
         w.Show();
      }
   }
}
