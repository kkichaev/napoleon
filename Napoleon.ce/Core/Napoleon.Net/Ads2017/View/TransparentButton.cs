using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;

namespace Ads2017
{
   class TransparentButton : Button
   {
      public TransparentButton()
      {
         Background = new SolidColorBrush(Colors.Transparent);
         BorderThickness = new Thickness(0, 0, 0, 0);
      }
   }
}
