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
   public partial class TimeLine : UserControl
   {
      public delegate void TicketDblClickHandler(object source, Ticket ticket);
      public delegate void TimeDblClickHandler(object source, TicketGrid grid, TimeSpan time);
      public delegate void TicketDeleteHandler(object source, Ticket ticket, TicketDeleteHandlerParam arg);
      public delegate void TicketPastHandler(object source, TicketGrid grid, Ticket ticket, TimeCell where);

      public event TimeDblClickHandler TimeDblClick;
      public event TicketDblClickHandler TicketDblClick;
      public event TicketDeleteHandler TicketDelete;
      public event TicketPastHandler TicketPaste;

      private int hourRowHeight = 60;
      private CopyBuffer copyBuffer = CopyBuffer.Instance();
      private object selectedObject = null;

      public TimeLine()
      {
         InitializeComponent();
      }

      public static DependencyProperty DateProperty = DependencyProperty.Register(
         "Date", typeof(DateTime), typeof(TimeLine), new PropertyMetadata(DateTime.MinValue));

      public static DependencyProperty HourRowHeightProperty = DependencyProperty.Register(
         "HourRowHeight", typeof(int), typeof(TimeLine), new PropertyMetadata(60, ValueChanged));

      private static void ValueChanged(DependencyObject d, DependencyPropertyChangedEventArgs e)
      {
         TimeLine t = (TimeLine)d;

         if (e.Property.Name == "HourRowHeight")
         {
            t.hourRowHeight = (int)e.NewValue;
         }
      }

      private void svContent_ScrollChanged(object sender, ScrollChangedEventArgs e)
      {
         svTitle.ScrollToVerticalOffset(e.VerticalOffset);
         svHeader.ScrollToHorizontalOffset(e.HorizontalOffset);
      }

      public void AddTaskPanel(TicketGrid ticketGrid)
      {
         TimePanelHeaderCell lbl = new TimePanelHeaderCell();
         lbl.Caption = ticketGrid.Caption;
         headers.Children.Add(lbl);
         ticketGrid.HourRowHeight = HourRowHeight;
         taskPanel.Children.Add(ticketGrid);
      }

      public void ClearTaksPanel()
      {
         taskPanel.Children.Clear();
         headers.Children.Clear();
      }

      public int HourRowHeight
      {
         get { return (int)GetValue(HourRowHeightProperty); }
         set { SetValue(HourRowHeightProperty, value); }
      }

      public CopyBuffer CopyBuffer
      {
         get{ return copyBuffer; }
      }

      public TicketGrid CreateTicketGrid(string caption, object source)
      {
         TicketGrid result = new TicketGrid();

         result.Container = this;
         result.Caption = caption;
         result.SourceObject = source;
         result.TimeDblClick += TimeDblClickEvent;
         result.TicketDblClick += TicketDblClickEvent;
         result.TicketDelete += TicketDeleteEvent;
         result.TicketCut += TicketCutEvent;
         result.TicketCopy += TicketCopyEvent;
         result.TicketPast += TicketPastEvent;
         result.TicketMouseDown += TicketMouseDownEvent;
         result.TimeMouseDown += TimeMouseDownEvent;

         return result;
      }

      private void TimeMouseDownEvent(object source)
      {
         selectedObject = source;
         ResetSelection();
      }

      private void TicketMouseDownEvent(object source)
      {
         selectedObject = source;
         ResetSelection();
      }

      private void TicketPastEvent(object source, object where)
      {
         if (!copyBuffer.IsEmpty)
         {
            Ticket t = copyBuffer.Stored as Ticket;
            TicketGrid g = source as TicketGrid;
            TimeCell c = where as TimeCell;

            if(t != null && g != null)
            {
               if(copyBuffer.Command == Ads2017.CopyBuffer.CommandType.Cut)
                  t.Remove();

               FireTicketPast(g, t, c);

               copyBuffer.Clear();
               CommandManager.InvalidateRequerySuggested();
            }
         }
      }

      private void FireTicketPast(TicketGrid grid, Ticket ticket, TimeCell cell)
      {
         if (TicketPaste != null)
            TicketPaste(this, grid, ticket, cell);
      }

      private void TicketCopyEvent(object source)
      {
         copyBuffer.Command = CopyBuffer.CommandType.Copy;
         copyBuffer.Stored = source;
         CommandManager.InvalidateRequerySuggested();
      }

      private void TicketCutEvent(object source)
      {
         copyBuffer.Command = CopyBuffer.CommandType.Cut;
         copyBuffer.Stored = source;
         CommandManager.InvalidateRequerySuggested();
      }

      private void TicketDeleteEvent(object source, TicketDeleteHandlerParam arg)
      {
         FireDeleteTicket(source, arg);
      }

      private void FireDeleteTicket(object source, TicketDeleteHandlerParam arg)
      {
         if (TicketDelete != null)
            TicketDelete(this, (Ticket)source, arg);
      }

      private void TicketDblClickEvent(object source)
      {
         FireTicketDblClick(source);
      }

      private void FireTicketDblClick(object source)
      {
         if (TicketDblClick != null)
            TicketDblClick(this, (Ticket)source);
      }

      private void TimeDblClickEvent(object source, TimeSpan time)
      {
         FireTimeCellDblClick(source, time);
      }

      private void FireTimeCellDblClick(object source, TimeSpan time)
      {
         if (TimeDblClick != null)
            TimeDblClick(this, (TicketGrid)source, time);
      }

      private void ResetSelection()
      {
         ResetTimeSelection();
         ResetTicketSelection();
         CommandManager.InvalidateRequerySuggested();
      }

      private void ResetTimeSelection()
      {
         foreach (UIElement ui in taskPanel.Children)
         {
            TicketGrid g = (TicketGrid)ui;
            g.ResetTimeSelection();
         }
      }

      private void ResetTicketSelection()
      {
         foreach (UIElement ui in taskPanel.Children)
         {
            TicketGrid g = (TicketGrid)ui;
            g.ResetTicketSelection();
         }
      }

      public bool IsTicketSelected 
      { 
         get { return selectedObject is Ticket; } 
      }

      internal void ClearSelection()
      {
         selectedObject = null;
         CopyBuffer.Clear();
      }

      public DateTime Date
      {
         get { return (DateTime)GetValue(DateProperty); }
         set { SetValue(DateProperty, value); }
      }

      internal void PerformDelete()
      {
         Ticket t = selectedObject as Ticket;

         if (t != null)
         {
            TicketGrid g = t.Container as TicketGrid;

            if (g != null)
               g.DeleteTicket(t);
         }
      }

      internal void PerformCopy()
      {
         Ticket t = selectedObject as Ticket;

         if (t != null)
            TicketCopyEvent(t);
      }

      internal void PerformCut()
      {
         Ticket t = selectedObject as Ticket;

         if (t != null)
            TicketCutEvent(t);
      }

      internal void PerformPaste()
      {
         TimeCell c = selectedObject as TimeCell;

         if (c != null)
            c.PerformPaste();
      }

      internal Ticket GetTicket()
      {
         return selectedObject as Ticket;
      }
   }
}
