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
   /// Interaction logic for TimePanelHeader.xaml
   /// </summary>
   public partial class TimePanelHeaderCell : UserControl
   {
      public TimePanelHeaderCell()
      {
         InitializeComponent();
      }

      public string Caption
      {
         get
         {
            return text.Content.ToString();
         }

         set
         {
            text.Content = value;
         }
      }

      protected override Size MeasureOverride(Size constraint)
      {
         var size = new Size();
         size.Height = 35 ;
         size.Width = constraint.Width;

         return size;
      }

      protected override Size ArrangeOverride(Size arrangeBounds)
      {
         return base.ArrangeOverride(arrangeBounds);
      }
   }
}
