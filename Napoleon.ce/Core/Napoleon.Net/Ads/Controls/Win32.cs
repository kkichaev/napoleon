/*
    HeaderControl/Win32.cs -- Win32 definitions
    Copyright © 2005 David A. Ferguson  <www.davidaferguson.com>.  Anyone may use this software in anyway they like.  NO WARRANTY.
*/
using System;
using System.Runtime.InteropServices;

namespace GRSoft.NapoleonManager
{
   public class Win32
   {
      public const int WM_NOTIFY = 0x004E;
      public const int WM_USER = 0x0400;

      public const int OCM__BASE = (WM_USER + 0x1c00);
      public const int OCM_NOTIFY = (OCM__BASE + WM_NOTIFY);

      public const int WS_CHILD = 0x40000000;
      public const int WS_VISIBLE = 0x10000000;

      public const int WM_CANCELMODE = 0x001F;
      public const int WM_CAPTURECHANGED = 0x0215;

      [StructLayout(LayoutKind.Sequential)]
      public struct NMHDR
      {
         public IntPtr hwndFrom;
         public IntPtr idFrom;
         public int code;
      }

      [DllImport("user32.dll")]
      public static extern int SendMessage(IntPtr hWnd, int Msg, int wParam, int lParam);

      public const int GWL_WNDPROC = -4;
      public const int GWL_HINSTANCE = -6;
      public const int GWL_HWNDPARENT = -8;
      public const int GWL_STYLE = -16;
      public const int GWL_EXSTYLE = -20;
      public const int GWL_USERDATA = -21;
      public const int GWL_ID = -12;

      [DllImport("user32.dll")]
      public static extern int GetWindowLong(IntPtr hWnd, int nIndex);

      [DllImport("user32.dll")]
      public static extern int SetWindowLong(IntPtr hWnd, int nIndex, int dwNewLong);

      [DllImport("user32.dll")]
      public static extern IntPtr SetCapture(IntPtr hWnd);

      [DllImport("user32.dll")]
      public static extern IntPtr GetCapture();

      [DllImport("user32.dll")]
      public static extern bool ReleaseCapture();

      #region Header Control ---------------------------------------------------------------------------------------------------
      public const string WC_HEADER = "SysHeader32";

      public const int HDM_FIRST = 0x1200;
      public const int HDM_INSERTITEM = HDM_FIRST + 10;
      public const int HDM_GETITEMCOUNT = HDM_FIRST + 0;
      public const int HDM_GETITEM = HDM_FIRST + 11;
      public const int HDM_SETITEM = HDM_FIRST + 12;
      public const int HDM_HITTEST = HDM_FIRST + 6;
      public const int HDM_DELETEITEM = HDM_FIRST + 2;


      public const int HDI_WIDTH = 0x0001;
      public const int HDI_HEIGHT = HDI_WIDTH;
      public const int HDI_TEXT = 0x0002;
      public const int HDI_FORMAT = 0x0004;
      public const int HDI_LPARAM = 0x0008;
      public const int HDI_BITMAP = 0x0010;
      public const int HDI_IMAGE = 0x0020;
      public const int HDI_DI_SETITEM = 0x0040;
      public const int HDI_ORDER = 0x0080;
      public const int HDI_FILTER = 0x0100;

      public const int HDF_LEFT = 0x0000;
      public const int HDF_RIGHT = 0x0001;
      public const int HDF_CENTER = 0x0002;
      public const int HDF_JUSTIFYMASK = 0x0003;
      public const int HDF_RTLREADING = 0x0004;
      public const int HDF_OWNERDRAW = 0x8000;
      public const int HDF_STRING = 0x4000;
      public const int HDF_BITMAP = 0x2000;
      public const int HDF_BITMAP_ON_RIGHT = 0x1000;
      public const int HDF_IMAGE = 0x0800;
      public const int HDF_SORTUP = 0x0400;
      public const int HDF_SORTDOWN = 0x0200;
      public const int HDF_FIXEDWIDTH = 0x0100;

      public const int HDS_HORZ = 0x0000;
      public const int HDS_BUTTONS = 0x0002;
      public const int HDS_HOTTRACK = 0x0004;
      public const int HDS_HIDDEN = 0x0008;
      public const int HDS_DRAGDROP = 0x0040;
      public const int HDS_FULLDRAG = 0x0080;
      public const int HDS_FILTERBAR = 0x0100;
      public const int HDS_FLAT = 0x0200;

      public const int HDN_FIRST = (0 - 300);
      public const int HDN_ITEMCHANGING = (HDN_FIRST - 20);
      public const int HDN_ITEMCHANGED = (HDN_FIRST - 21);
      public const int HDN_ITEMCLICK = (HDN_FIRST - 22);
      public const int HDN_ITEMDBLCLICK = (HDN_FIRST - 23);
      public const int HDN_DIVIDERDBLCLICK = (HDN_FIRST - 25);
      public const int HDN_BEGINTRACK = (HDN_FIRST - 26);
      public const int HDN_ENDTRACK = (HDN_FIRST - 27);
      public const int HDN_TRACK = (HDN_FIRST - 28);
      public const int HDN_GETDISPINFO = (HDN_FIRST - 29);
      public const int HDN_BEGINDRAG = (HDN_FIRST - 10);
      public const int HDN_ENDDRAG = (HDN_FIRST - 11);
      public const int HDN_FILTERCHANGE = (HDN_FIRST - 12);
      public const int HDN_FILTERBTNCLICK = (HDN_FIRST - 13);

      public const int HHT_NOWHERE = 0x0001;
      public const int HHT_ONHEADER = 0x0002;
      public const int HHT_ONDIVIDER = 0x0004;
      public const int HHT_ONDIVOPEN = 0x0008;
      public const int HHT_ONFILTER = 0x0010;
      public const int HHT_ONFILTERBUTTON = 0x0020;
      public const int HHT_ABOVE = 0x0100;
      public const int HHT_BELOW = 0x0200;
      public const int HHT_TORIGHT = 0x0400;
      public const int HHT_TOLEFT = 0x0800;


      [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Auto)]
      public struct HDITEM
      {
         public int mask;
         public int cxy;
         [MarshalAs(UnmanagedType.LPTStr)]
         public string pszText;        // LPSTR   pszText;
         public IntPtr hbm;            // HBITMAP hbm;
         public int cchTextMax;
         public int fmt;
         public int lParam;         // LPARAM  lParam;
         // #if (_WIN32_IE >= 0x0300)
         public int iImage;         // index of bitmap in ImageList
         public int iOrder;         // where to draw this item
         // #endif
         // #if (_WIN32_IE >= 0x0500)
         public int type;           // [in] filter type (defined what pvFilter is a pointer to)
         public IntPtr pvFilter;       // void*   pvFilter;       // [in] filter data see above
         // #endif
      }

      [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Auto)]
      public struct NMHEADER
      {
         public NMHDR hdr;
         public int iItem;
         public int iButton;
         public IntPtr pitem;
      }

      [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Auto)]
      public struct POINT
      {
         public int x;
         public int y;
      }

      [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Auto)]
      public struct HDHITTESTINFO
      {
         public POINT pt;
         public UInt32 flags;
         public int iItem;
      }

      [DllImport("user32.dll")]
      public static extern int SendMessage(IntPtr hWnd, int Msg, int wParam, ref HDITEM hdi);

      [DllImport("user32.dll")]
      public static extern int SendMessage(IntPtr hWnd, int Msg, int wParam, ref HDHITTESTINFO hdi);
      #endregion
   }
}
