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
   /// Interaction logic for TicketGrid.xaml
   /// </summary>
   public partial class TicketGrid : UserControl
   {
      private object sourceObject;
      public int hourRowHeight = 60;
      

      public delegate void TimeDblClickHandler(object source, TimeSpan time);
      public delegate void TimeMouseDownHandler(object source);
      public delegate void TicketDblClickHandler(object source);
      public delegate void TicketDeleteHandler(object source, TicketDeleteHandlerParam arg);
      public delegate void TicketCopyHandler(object source);
      public delegate void TicketCutHandler(object source);
      public delegate void TickerPastHandler(object source, object where);
      public delegate void TicketMouseDownHandler(object source);

      public event TimeDblClickHandler TimeDblClick;
      public event TimeMouseDownHandler TimeMouseDown;
      public event TicketDblClickHandler TicketDblClick;
      public event TicketDeleteHandler TicketDelete;
      public event TicketCopyHandler TicketCopy;
      public event TicketCutHandler TicketCut;
      public event TickerPastHandler TicketPast;
      public event TicketMouseDownHandler TicketMouseDown; 

      private string caption = string.Empty;
      private object container = null;

      public TicketGrid()
      {
         InitializeComponent();

         foreach (UIElement ui in timePanel.Children)
         {
            if (ui is TimeCell)
            {
               TimeCell tc = (TimeCell)ui;
               tc.MouseDoubleClick += CellDoubleClick;
               tc.TimeCellPast += PastToCell;
               tc.MouseDown += CellMouseDown;
            }
         }
      }

      void CellMouseDown(object sender, MouseButtonEventArgs e)
      {
         FireTimeMouseDown(sender);
         ((TimeCell)sender).IsSelected = true;
      }

      private void FireTimeMouseDown(object sender)
      {
         if (TimeMouseDown != null)
            TimeMouseDown(sender);
      }

      public void ResetTimeSelection()
      {
         foreach (UIElement ui in timePanel.Children)
         {
            if (ui is TimeCell)
            {
               TimeCell tc = (TimeCell)ui;
               tc.IsSelected = false;
            }
         }
      }

      public string Caption
      {
         get { return caption; }
         set { caption = value; }
      }

      public object SourceObject
      {
         get { return sourceObject; }
         set { sourceObject = value; }
      }

      private void CellDoubleClick(object sender, MouseButtonEventArgs e)
      {
         if (e.ChangedButton == MouseButton.Left)
         {
            TimeCell timeCell = ((TimeCell)sender);
            TimeSpan time = timeCell.StartTime;
            FireTimeDblClickHandler(time);
         }
      }

      private void FireTimeDblClickHandler(TimeSpan time)
      {
         if (TimeDblClick != null)
            TimeDblClick(this, time);
      }

      internal void AddTicket(Ticket t)
      {
         t.Container = this;
         t.MouseDoubleClick += Ticket_Double_Click;
         t.MouseDown += TicketSelect;
         ticketPanel.Children.Add(t);
      }

      private void TicketSelect(object sender, MouseButtonEventArgs e)
      {
         FireTicketMouseDown(sender);
         ((Ticket)sender).IsSelected = true;
      }

      private void FireTicketMouseDown(object sender)
      {
         if (TicketMouseDown != null)
            TicketMouseDown(sender);
      }

      private void Ticket_Double_Click(object sender, MouseButtonEventArgs e)
      {
         FireTicketDblClick(sender);
      }

      private void FireTicketDblClick(object sender)
      {
         if (TicketDblClick != null)
            TicketDblClick(sender);
      }

      public static DependencyProperty HourRowHeightProperty = DependencyProperty.Register(
         "HourRowHeight", typeof(int), typeof(TicketGrid), new PropertyMetadata(60, ValueChanged));

      private static void ValueChanged(DependencyObject d, DependencyPropertyChangedEventArgs e)
      {
         TicketGrid t = (TicketGrid)d;

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

      public void Redraw()
      {
         ticketPanel.InvalidateVisual();
      }

      internal void DeleteTicket(object sender)
      {
         TicketDeleteHandlerParam arg = new TicketDeleteHandlerParam();
         FireDeleteTicket(sender, arg);

         if (arg.canDelete)
         {
            ticketPanel.Children.Remove((UIElement)sender);
         }
      }

      private void FireDeleteTicket(object sender, TicketDeleteHandlerParam arg)
      {
         if (TicketDelete != null)
            TicketDelete(sender, arg);
      }

      internal void CopyTicket(object sender)
      {
         FireCopyTicket(sender);
      }

      private void FireCopyTicket(object sender)
      {
         if (TicketCopy != null)
            TicketCopy(sender);
      }

      internal void CutTicket(object sender)
      {
         FireCutTicket(sender);
      }

      private void FireCutTicket(object sender)
      {
         if (TicketCut != null)
            TicketCut(sender);
      }

      internal void PastToCell(object sender)
      {
         FireTicketPast(sender);
      }

      private void FireTicketPast(object sender)
      {
         if (TicketPast != null)
            TicketPast(this, sender);
      }

      internal void ResetTicketSelection()
      {
         foreach (UIElement ui in ticketPanel.Children)
         {
            Ticket tk = (Ticket)ui;
            tk.IsSelected = false;
         }
      }

      public object Container { get { return container; } set { container = value; } }
   }
}
