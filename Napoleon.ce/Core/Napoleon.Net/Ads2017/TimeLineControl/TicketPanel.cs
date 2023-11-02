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
   class TicketPanel : Panel
   {
      const int RIGHT_PADDING = 15;
      const int LEFT_PADDING = 5;
      private int hourRowHeight = 60;

      protected override Size MeasureOverride(Size availableSize)
      {
         var mySize = new Size();
         mySize.Height = 24 * hourRowHeight;
         mySize.Width = availableSize.Width;
         return mySize;
      }

      class UILocation
      {
         public Ticket el;
         public Rect rect;

         public UILocation(Ticket e, Rect r)
         {
            el = e;
            rect = r;
         }

         public int Weight { get { return el.TicketRangeTime(); } }
      }

      protected override Size ArrangeOverride(Size finalSize)
      {
         List<UILocation> pr = new List<UILocation>();
         
         for (int i = 0; i < InternalChildren.Count; i++)
         {
            Ticket t = (Ticket)InternalChildren[i];

            Rect r = new Rect(new Point(0, t.TicketStartMinte() * HourRowHeight / 60), new Size(finalSize.Width, (t.TicketRangeTime() - 1) * HourRowHeight / 60));
            pr.Add(new UILocation(t, r)); 
         }

         pr.Sort((x, y) => { return x.Weight.CompareTo(y.Weight) * -1; });

         for (int i = 0; i < pr.Count; i++)
         {
            SetZIndex(pr[i].el, i);
            List<UILocation> nb = FindNeighbors(pr[i], pr.GetRange(0, i));
            ComputePlacement(nb, (int)finalSize.Width);
         }

         foreach (UILocation u in pr)
            u.el.Arrange(u.rect);

         return finalSize;
      }

      private void ComputePlacement(List<UILocation> list, int width)
      {
         int full = width - RIGHT_PADDING;
         width = full;

         double part = width / list.Count;

         for (int i = 0; i < list.Count; i++ )
         {
            UILocation u = list[i];
            u.rect.X = part * i;
            u.rect.Width = full - u.rect.X;
         }
      }

      private List<UILocation> FindNeighbors(UILocation e, List<UILocation> list)
      {
         List<UILocation> result = new List<UILocation>();

         foreach (UILocation l in list)
         {
            
            if(e.rect.IntersectsWith(l.rect))
               result.Add(l);
         }

         result.Add(e);

         return result;
      }

      public static DependencyProperty HourRowHeightProperty = DependencyProperty.Register(
         "HourRowHeight", typeof(int), typeof(TicketPanel), new PropertyMetadata(60, ValueChanged));

      private static void ValueChanged(DependencyObject d, DependencyPropertyChangedEventArgs e)
      {
         TicketPanel t = (TicketPanel)d;

         if (e.Property.Name == "HourRowHeight")
         {
            t.hourRowHeight = (int)e.NewValue;
         }
      }

      public int HourRowHeight
      {
         get { return (int)GetValue(HourRowHeightProperty); }
         set { SetValue(HourRowHeightProperty, value); }
      }
   }
}
