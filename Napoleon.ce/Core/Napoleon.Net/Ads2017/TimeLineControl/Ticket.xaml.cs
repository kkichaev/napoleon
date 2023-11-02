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
   /// Interaction logic for Ticket.xaml
   /// </summary>
   public partial class Ticket : UserControl
   {
      private object storedObject = null;
      private TicketGrid container;
      private string innerText = string.Empty;
      private bool isBold = false;

      public Ticket()
      {
         InitializeComponent();
      }

      private DateTime start = DateTime.MinValue;
      private DateTime finish = DateTime.MaxValue;

      public int TicketStartMinte()
      {
         int result = 0;

         if (start.Date.Equals(GetDate().Date))
            result = (int)start.TimeOfDay.TotalMinutes;

         return result;
      }

      private DateTime GetDate()
      {
         return GetContainerData() ?? DateTime.MinValue.Date;
      }

      private DateTime? GetContainerData()
      {
         DateTime? result = null;

         if (container != null)
         {
            TimeLine timeline = (TimeLine)container.Container;

            if (timeline != null)
               result = timeline.Date;
         }

         return result;
      }

      private int TicketFinishMinutes()
      { 
         int result = 24 * 60;

         if(finish.Date.Equals(GetDate().Date))
            result = (int)finish.TimeOfDay.TotalMinutes;

         return result;
      }

      public int TicketRangeTime()
      {
         return TicketFinishMinutes() - TicketStartMinte();
      }

      public static readonly DependencyProperty TextProperty = DependencyProperty.Register
        ("Text", typeof(string), typeof(Ticket), new PropertyMetadata(string.Empty));

      public static readonly DependencyProperty AddressProperty = DependencyProperty.Register
        ("Address", typeof(string), typeof(Ticket), new PropertyMetadata(string.Empty));

      public static readonly DependencyProperty IsSelectedProperty = DependencyProperty.Register
        ("IsSelected", typeof(bool), typeof(Ticket), new PropertyMetadata(false, ValueChanged));

      public static readonly DependencyProperty BorderColorProperty = DependencyProperty.Register
         ("BorderColor", typeof(Color), typeof(Ticket), new PropertyMetadata(Color.FromArgb(255, 0x00, 0x7c, 0xc5), BorderColorChanged));

      private static void BorderColorChanged(DependencyObject d, DependencyPropertyChangedEventArgs e)
      {
         Ticket t = (Ticket)d;
         t.selection.BorderBrush = new SolidColorBrush((Color)e.NewValue);
      }

      private static void ValueChanged(DependencyObject d, DependencyPropertyChangedEventArgs e)
      {
         Ticket t = (Ticket)d;

         //if (e.Property.Name == "Text")
         //{
         //   t.SetText((string)e.NewValue);
         //}
         //else if (e.Property.Name == "IsSelected")
         {
            SetTicketSelect(t, (bool)e.NewValue);
         }
      }

      private static void SetTicketSelect(Ticket t, bool v)
      {
         t.SetFocus(v);
      }

      private void SetFocus(bool f)
      {
         //selection.BorderBrush = f ? new SolidColorBrush(Colors.Red) : Background;
      }

      public string Text
      {
         get { return (string)GetValue(TextProperty); }
         set { SetValue(TextProperty, value); }
      }

      public object StoredObject
      {
         get { return storedObject; }
         set { storedObject = value; }
      }

      public DateTime StartDateTime
      {
         get { return start; }
         set { start = value; }
      }

      public DateTime FinishDateTime
      {
         get { return finish; }
         set { finish = value; }
      }

      public TicketGrid Container {
         get { return container; } 
         set{ container = value;} 
      }

      internal void Redraw()
      {
         Container.Redraw();
      }

      public bool IsSelected {
         get { return (bool)GetValue(IsSelectedProperty); }
         set { SetValue(IsSelectedProperty, value); }
      }

      private void DeleteExecuted(object sender, ExecutedRoutedEventArgs e)
      {
         container.DeleteTicket(sender);
      }

      private void CanDeleteExecuted(object sender, CanExecuteRoutedEventArgs e)
      {
         e.CanExecute = ManagerHelper.Instance.CanEdit(((Task)StoredObject).manager);
      }

      private void CopyExecuted(object sender, ExecutedRoutedEventArgs e)
      {
         container.CopyTicket(sender);
      }

      private void CanCopyExecuted(object sender, CanExecuteRoutedEventArgs e)
      {
         e.CanExecute = ManagerHelper.Instance.CanEdit(((Task)StoredObject).manager);
      }

      private void CutExecuted(object sender, ExecutedRoutedEventArgs e)
      {
         container.CutTicket(sender);
      }

      private void CanCutExecuted(object sender, CanExecuteRoutedEventArgs e)
      {
         e.CanExecute = ManagerHelper.Instance.CanEdit(((Task)StoredObject).manager);
      }

      public void Remove()
      {
         Container.ticketPanel.Children.Remove(this);
      }

      public Color BorderColor
      {
         get { return (Color) GetValue(BorderColorProperty); }
         set { SetValue(BorderColorProperty, value); }
      }

      public string Address
      {
         get { return (string)GetValue(AddressProperty); }
         set { SetValue(AddressProperty, value); }
      }

      public bool BoldFont
      {
         get { return isBold; }
         set { SetBold(value); } }

      public bool Attachments
      { set
         {
            attaches.BorderBrush = new SolidColorBrush(value? Colors.Green : Colors.Azure) ;
         }
      }

      private void SetBold(bool value)
      {
         tbText.FontWeight = value ? FontWeights.Bold : FontWeights.Normal;
      }
   }
}
