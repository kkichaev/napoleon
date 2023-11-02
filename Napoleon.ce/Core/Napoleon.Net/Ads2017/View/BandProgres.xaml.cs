using System;
using System.Collections.Generic;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;

namespace Ads2017
{
   public partial class BandProgres : UserControl
   {
      public class BandItem
      {
         public Color color;
         public string title;
      }

      public BandProgres()
      {
         InitializeComponent();
      }

      public List<BandItem> Items
      {
         get{ return CollectItems(); }
         set { SetItems(value); }
      }

      public double[] Values
      {
         get { return GetValues(); }
         set { SetValues(value); }
      }

      private void SetValues(double[] value)
      {
         for (int i = 0; i < panel.Children.Count; i++)
         {
            if (panel.Children[i] is BandProgressItem b)
            {
               if (i < value.Length)
                  b.Progress = value[i];
            }
         }
      }

      private double[] GetValues()
      {
         double[] result = new double[panel.Children.Count];

         for(int i = 0; i < result.Length; i++)
         {
            if (panel.Children[i] is BandProgressItem b)
            {
               result[i] = b.Progress; 
            }
         }

         return result;
      }

      private void SetItems(List<BandItem> value)
      {
         panel.Children.Clear();

         foreach(BandItem i in value)
         {
            panel.Children.Add(new BandProgressItem
            {
               ProgressColor = new SolidColorBrush(i.color),
               Title = i.title,
            });
         }
      }

      private List<BandItem> CollectItems()
      {
         List<BandItem> result = new List<BandItem>();

         panel.Children.ForEach<Rectangle>((r)=> 
         {
            if (r.Tag is BandItem i)
            {
               result.Add(i);
            }
         });
         
         return result;
      }

      public void StartAnimation()
      {
         panel.Children.ForEach<BandProgressItem>((r) =>
         {
            DoubleAnimation a = new DoubleAnimation();
            a.From = 0;
            a.To = ActualWidth / 100 * r.Progress;
            a.Duration = new Duration(new TimeSpan(3000000));
            r.BeginAnimation(BandProgressItem.BandWidthProperty, a);
         });
      }

      public string Title { get; set; }
   }
}
