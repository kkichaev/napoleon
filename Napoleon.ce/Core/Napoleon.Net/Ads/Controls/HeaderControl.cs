/*
    HeaderControl/HeaderControl.cs -- Wraps the windows header control
    Copyright © 2005 David A. Ferguson  <www.davidaferguson.com>.  Anyone may use this software in anyway they like.
*/
using System;
using System.Windows.Forms;
using System.Runtime.InteropServices;
using System.ComponentModel;
using System.Drawing;

namespace GRSoft.NapoleonManager
{
   // Wrap the Win32 Header Control
   // ms-help://MS.VSCC.2003/MS.MSDNQTR.2003FEB.1033/shellcc/platform/commctls/header/header.htm

   // Using v6 common controls
   // http://msdn.microsoft.com/library/en-us/dv_vstechart/html/vbtchUsingWindowsXPVisualStylesWithControlsOnWindowsForms.asp



   public enum HeaderSortMarker
   {
      None = 0,
      Up = Win32.HDF_SORTUP,
      Down = Win32.HDF_SORTDOWN,
   }



   public class HeaderControlEventArgs : EventArgs
   {
      int _index;
      MouseButtons _button;

      public HeaderControlEventArgs(int index, MouseButtons button)
      {
         _index = index;
         _button = button;
      }

      public int Index { get { return _index; } }
      public MouseButtons Button { get { return _button; } }
   }

   public delegate void HeaderControlEventHandler(object sender, HeaderControlEventArgs ea);



   public class HeaderControl : Control
   {
      int _style = Win32.WS_CHILD | Win32.WS_VISIBLE;
      int columns = 0;

      public HeaderControl()
      {
         Dock = DockStyle.Top;
         TabStop = false;
      }

      #region Events -----------------------------------------------------------------------------------------------------------
      public new event HeaderControlEventHandler Click;
      protected virtual void OnClick(HeaderControlEventArgs e) { if (Click != null) Click(this, e); }

      public new event HeaderControlEventHandler DoubleClick;
      protected virtual void OnDoubleClick(HeaderControlEventArgs e) { if (DoubleClick != null)  DoubleClick(this, e); }
      #endregion

      #region Properties -------------------------------------------------------------------------------------------------------
      [Category("Behavior"),
      Description("If true you will receive click events from the header."),
      DefaultValue(false)]
      public bool ButtonStyle
      {
         get { return GetWin32Style(Win32.HDS_BUTTONS); }
         set { SetWin32Style(Win32.HDS_BUTTONS, value); }
      }
      #endregion

      bool GetWin32Style(int mask)
      {
         return (_style & mask) == 0 ? false : true;
      }

      void SetWin32Style(int mask, bool state)
      {
         if (GetWin32Style(mask) != state)
         {
            if (state)
               _style |= mask;
            else
               _style &= ~mask;

            if (Handle.ToInt32() != 0)
               Win32.SetWindowLong(Handle, Win32.GWL_STYLE, _style);
         }
      }

      protected override CreateParams CreateParams
      {
         get
         {
            SetStyle(ControlStyles.UserPaint, false);
            CreateParams cp = base.CreateParams;
            cp.ClassName = Win32.WC_HEADER;
            cp.Style |= _style;
            return cp;
         }
      }

      public int InsertColumn(int index, string text, HorizontalAlignment align, int width, HeaderSortMarker sortMarker)
      {
         return InsertColumn(index, text, align, width, sortMarker, false);
      }

      public int InsertColumn(int index, string text, HorizontalAlignment align, int width, HeaderSortMarker sortMarker, bool fixedWidth)
      {
         int result = -1;

         Win32.HDITEM hdi = new Win32.HDITEM();

         hdi.mask |= Win32.HDI_TEXT;
         hdi.pszText = text;

         hdi.mask |= Win32.HDI_WIDTH;
         hdi.cxy = width;

         hdi.mask |= Win32.HDI_FORMAT;
         switch (align)
         {
            case HorizontalAlignment.Left: hdi.fmt |= Win32.HDF_LEFT; break;
            case HorizontalAlignment.Right: hdi.fmt |= Win32.HDF_RIGHT; break;
            case HorizontalAlignment.Center: hdi.fmt |= Win32.HDF_CENTER; break;
         }

         hdi.fmt |= Win32.HDF_STRING;  // currently we always display item as string
         hdi.fmt |= (int)sortMarker;

         if (fixedWidth)
            hdi.fmt |= Win32.HDF_FIXEDWIDTH;

         IntPtr w = this.Handle;
         result = Win32.SendMessage(w, Win32.HDM_INSERTITEM, index, ref hdi);

         if (result == index)
            columns++;

         return result;
      }

      public void ClearColumns()
      {
         IntPtr w = this.Handle;

         for (int i = columns; i > 0; i--)
            Win32.SendMessage(w, Win32.HDM_DELETEITEM, i, 0);
      }

      HeaderControlEventArgs MakeArgs(ref System.Windows.Forms.Message m)
      {
         Win32.NMHEADER nmh;
         nmh = (Win32.NMHEADER)m.GetLParam(typeof(Win32.NMHEADER));
         MouseButtons mb = MouseButtons.None;
         switch (nmh.iButton)
         {
            case 0: mb = MouseButtons.Left; break;
            case 1: mb = MouseButtons.Right; break;
            case 2: mb = MouseButtons.Middle; break;
         }
         return new HeaderControlEventArgs(nmh.iItem, mb);
      }

      public int Count
      { 
         get { return Win32.SendMessage(Handle, Win32.HDM_GETITEMCOUNT, 0, 0); } 
      }

      public Win32.HDITEM GetItem(int pos, int mask)
      {
         Win32.HDITEM hdi = new Win32.HDITEM();
         hdi.mask = mask;
         Win32.SendMessage(Handle, Win32.HDM_GETITEM, pos, ref hdi);
         return hdi;
      }

      public int GetWidth(int item)
      {
         if (item >= Count)
            return 0;

         Win32.HDITEM i = GetItem(item, Win32.HDI_WIDTH);
         return i.cxy;
      }

      public bool SetWidth(int index, int width)
      {
         Win32.HDITEM item = new Win32.HDITEM();
         item.mask = Win32.HDI_WIDTH;
         item.cxy = width;
         return Win32.SendMessage(Handle, Win32.HDM_SETITEM, index, ref item) > 0;
      }

      public Win32.HDHITTESTINFO HitTest(Point pt)
      {
         Win32.HDHITTESTINFO ht = new Win32.HDHITTESTINFO();
         ht.pt.x = pt.X;
         ht.pt.y = pt.Y;
         Win32.SendMessage(Handle, Win32.HDM_HITTEST, 0, ref ht);
         return ht;
      }

      protected override void OnMouseMove(MouseEventArgs e)
      {
         base.OnMouseMove(e);
         if (e.Button == MouseButtons.None)
         {
            Point pt = PointToClient(Cursor.Position);
            Win32.HDHITTESTINFO ht = HitTest(pt);
            if ((ht.flags & Win32.HHT_ONDIVIDER) == Win32.HHT_ONDIVIDER)
               Cursor = Cursors.VSplit;
            else
               Cursor = Cursors.Arrow;
         }
      }

      protected override void OnMouseLeave(EventArgs e)
      {
         base.OnMouseLeave(e);
         Cursor = Cursors.Arrow;
      }

      public event EventHandler Tracking;

      protected override void WndProc(ref System.Windows.Forms.Message m)
      {
         if (m.Msg == Win32.OCM_NOTIFY)
         {
            Win32.NMHDR nmBase = (Win32.NMHDR)m.GetLParam(typeof(Win32.NMHDR));
            switch (nmBase.code)
            {
               case Win32.HDN_ITEMCLICK:
                  OnClick(MakeArgs(ref m));
                  return;

               case Win32.HDN_ITEMDBLCLICK:
                  OnDoubleClick(MakeArgs(ref m));
                  return;
               case Win32.HDN_ITEMCHANGED://.HDN_ITEMCHANGING:
                  if (Tracking != null)
                     Tracking(this, EventArgs.Empty);
                  return;
            }
         }
         base.WndProc(ref m);
      }
   }
}