using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using System.Windows.Forms.Design;
using System.Collections;

namespace GRSoft.NapoleonManager
{
   [Designer(typeof(TimeGridDesigner))]
   public partial class TimeGrid : UserControl
   {
      const int MIN_BAND_WIDTH = 10;

      Color gridColor = Color.Black;
      Color outWorkColor = Color.White;
      Color nowTimeColor = Color.Goldenrod;
      Color inWorkColor = Color.White;
      DateTime drawedDate = DateTime.Now.Date;

      TimeGridAdapter adapter = new TimeGridAdapter();
      Dictionary<Band, List<ItemDraw>> drawedTask = new Dictionary<Band, List<ItemDraw>>();

      Band sizedBand = null;
      Point startResizePos = new Point();
      int startWidth = 0, bandLeft;
      TaskContextMenuStrip itemMenu;
      ViewProp viewProp;
      EditBuffer buffer;
      SelectData selectData = null;

      public TimeGrid()
      {
         InitializeComponent();

         timeLine.Changed += new EventHandler(timeLine_Changed);
         adapter.SetOnBandsChanged(bands_Changed);
         Size sz = timeLine.MinimumSize;
         buffer = new EditBuffer(this);
      }

      protected override void OnResize(EventArgs e)
      {
         base.OnResize(e);
         BandAutoFit();
      }

      private void BandAutoFit()
      {
         if(header != null && header.Count > 1)
            for (int i = 1; i < header.Count; i++)
               header.SetWidth(i, (Width - timeLine.Width) / (header.Count - 1));
      }

      void Tracking(object sender, EventArgs e)
      {
         Refresh();
      }

      public event TaskColorEventHandler TaskColor;
      public event TaskEventHandler TaskClicked;
      public event TaskEventHandler TaskDblClicked;
      public event TaskHintEventHandler TaskHint;
      public event GridEventHandler GridDblClicked;
      public event GridItemDraw CustomItemDraw;
      public event GridPastHandler GridPastHandler;
      
      public DateTime DrawedDate
      {
         get { return drawedDate; }
         set
         {
            drawedDate = value.Date;
            Refresh();
         }
      }

      protected override void OnHandleCreated(EventArgs e)
      {
         base.OnHandleCreated(e);
         AutoScroll = true;
      }

      void bands_Changed(object sender, EventArgs e)
      {
         Size sz = timeLine.MinimumSize;

         if (Handle == IntPtr.Zero)
            return;

         if(header != null)
            header.ClearColumns();

         int count = adapter.GetBandCount();
         if (count > 0)
         {
            int bandWidth = (Width - timeLine.Width) / count;
            if (bandWidth < MIN_BAND_WIDTH)
               bandWidth = MIN_BAND_WIDTH;

            int index = 1;

            if (header != null)
               for (int i = 0; i < count; i++)
               {
                  String name = adapter.GetBandNameAt(i);
                  header.InsertColumn(index++, name, HorizontalAlignment.Left, bandWidth, HeaderSortMarker.None);
               }
         }
      }

      void timeLine_Changed(object sender, EventArgs e)
      {
         Refresh();
      }

      class HitTestData
      {
         public BandItem item;
         public Band band;
         public bool divider;
         public int hour;
      }

      HitTestData HitTest(Point pt)
      {
         Point check = PointToScreen(pt);
         HitTestData ht = new HitTestData();

         if (header != null)
         {
            check = header.PointToClient(check);
            check.Y = header.Height - 4;
            
            Win32.HDHITTESTINFO hti = header.HitTest(check);

            if (hti.iItem > 0 && hti.iItem <= adapter.GetBandCount())
               ht.band = adapter.GetBandAt(hti.iItem - 1);

            if ((hti.flags & Win32.HHT_ONDIVIDER) == Win32.HHT_ONDIVIDER)
            {
               ht.divider = true;
               return ht;
            }
         }

         if (ht.band != null)
         {
            Band b = ht.band;
            ht.hour = timeLine.GetHour(pt.Y);

            if (drawedTask.ContainsKey(b))
            {
               foreach (ItemDraw td in drawedTask[b])
               {
                  ItemDraw i = FindItem(td, pt);

                  if (i != null)
                  {
                     ht.item = i.Item;
                     break;
                  }
               }
            }
         }

         return ht;
      }

      private ItemDraw FindItem(ItemDraw i, Point p)
      {
         ItemDraw res = null;

         foreach (ItemDraw d in i.Childs)
         {
            res = FindItem(d, p);

            if (res != null)
               break;
         }

         if (i.Bounds.Contains(p))
            res = i;

         return res;
      }

      void BeginResizeBand(Band b)
      {
         sizedBand = b;
         startResizePos = Cursor.Position;

         int index = adapter.GetBandIndexOf(b) + 1;
         if (header != null)
         {
            startWidth = header.GetWidth(index);
            Win32.SetCapture(Handle);

            int i = 0;
            bandLeft = timeLine.Width;
            for (; i < index; i++)
               bandLeft += header.GetWidth(i);
         }
      }

      public void EndResizeBand()
      {
         if (sizedBand != null)
         {
            sizedBand = null;
            if (Win32.GetCapture() == Handle)
               Win32.ReleaseCapture();
         }
      }

      void ResizeBand()
      {
         int diff = Cursor.Position.X - startResizePos.X;
         int index = adapter.GetBandIndexOf(sizedBand);

         if(header != null)
            header.SetWidth(index + 1, startWidth + diff);

         Refresh();
      }

      protected override void WndProc(ref System.Windows.Forms.Message m)
      {
         if (m.Msg == Win32.WM_CANCELMODE || (m.Msg == Win32.WM_CAPTURECHANGED && Win32.GetCapture() != Handle))
            EndResizeBand();
         base.WndProc(ref m);
      }

      protected override void OnMouseDoubleClick(MouseEventArgs e)
      {
         base.OnMouseDoubleClick(e);
         if (TaskDblClicked != null)
         {
            HitTestData ht = HitTest(PointToClient(Cursor.Position));
            if (ht.item != null)
            {
               TaskEventArgs args = new TaskEventArgs(ht.item);
               TaskDblClicked.Invoke(this, args);
            }
            else
            {
               if (GridDblClicked != null)
               {
                  GridEventArgs args = new GridEventArgs(ht.band, ht.hour);
                  GridDblClicked(this, args);
               }
            }
         }
      }

      private Rectangle getSelectedBounds()
      {
         int pos = adapter.GetBandIndexOf(selectData.Band) + AutoScrollPosition.Y;
         int x = timeLine.Width;

         if (header != null)
            for (int i = 1; i <= pos; i++)
               x += header.GetWidth(i);

         int y1 = timeLine.GetPosition(selectData.Hour - 1) + AutoScrollPosition.Y;
         int y2 = timeLine.GetPosition(selectData.Hour) + AutoScrollPosition.Y;

         int left = header != null ?  header.GetWidth(pos + 1) - 4 : 0;
         return new Rectangle(x + 2, y1 + 2,left, y2 - y1 - 4); 
      }

      protected override void OnMouseDown(MouseEventArgs e)
      {
         base.OnMouseDown(e);
         HitTestData ht = HitTest(PointToClient(Cursor.Position));

         if (ht.divider)
         {
            if (ht.band != null)
               BeginResizeBand(ht.band);
         }
         else
         {
            selectData = new SelectData(ht.band, ht.hour, ht.item);
            Invalidate();
         }

         if(e.Button == MouseButtons.Right && itemMenu != null)
         {
            itemMenu.Task = ht.item;
            itemMenu.Show(PointToScreen(new Point(e.X, e.Y)));
         }
      }

      protected override void OnMouseUp(MouseEventArgs e)
      {
         base.OnMouseUp(e);
         EndResizeBand();
      }

      protected override void OnMouseClick(MouseEventArgs e)
      {
         base.OnMouseClick(e);
         if (TaskClicked != null)
         {
            HitTestData ht = HitTest(PointToClient(Cursor.Position));
            if (ht.item != null)
            {
               TaskEventArgs args = new TaskEventArgs(ht.item);
               TaskClicked.Invoke(this, args);
            }
            else
            {
               //if(GridDblClicked != null)
               //   GridDblClicked(this, 
            }
         }
      }

      protected override void OnMouseMove(MouseEventArgs e)
      {
         if (sizedBand != null)
         {
            ResizeBand();
            return;
         }

         base.OnMouseMove(e);
         if (e.Button == MouseButtons.None)
         {
            HitTestData ht = HitTest(PointToClient(Cursor.Position));
            if (ht.divider)
               Cursor = Cursors.VSplit;
            else
            {
               String hint = null;
               if (ht.item != null)
               {
                  TaskHintEventArgs arg = new TaskHintEventArgs(ht.item);
                  arg.Hint = string.Empty;

                  if (TaskHint != null)
                     TaskHint.Invoke(this, arg);
                  if (arg.Hint.Length > 0)
                     hint = arg.Hint;
               }
               if( toolTip.GetToolTip(this) != hint )
                  toolTip.SetToolTip(this, hint);
               Cursor = Cursors.Arrow;
            }
         }
      }

      protected override void OnMouseWheel(MouseEventArgs e)
      {
         if ((ModifierKeys & Keys.Control) == Keys.Control)
         {
            SuspendLayout();
            Refresh();
            
            ResumeLayout();
         } else
            base.OnMouseWheel(e);
      }

      protected override void OnMouseLeave(EventArgs e)
      {
         base.OnMouseLeave(e);
         Cursor = Cursors.Arrow;
      }

      protected override void OnPaintBackground(PaintEventArgs e)
      {
         using (Brush bgLost = new SolidBrush(Color.White))
         {
            e.Graphics.FillRectangle(bgLost, 0, 0, Width, Height);
            using (Brush bgWork = new SolidBrush(inWorkColor))
            {
               int top = timeLine.GetPosition(timeLine.Start-1) + AutoScrollPosition.Y;
               int bottom = timeLine.GetPosition(timeLine.Finish)+ AutoScrollPosition.Y;
               e.Graphics.FillRectangle(bgWork, 0, top, Width, bottom - top);
            }
         }

         if (drawedDate.Date.Equals(DateTime.Now.Date)) 
         {
            using (Brush bgTime = new SolidBrush(Color.FromArgb(25, nowTimeColor)))
            {
               int pos = timeLine.GetPosition(DateTime.Now.TimeOfDay) + AutoScrollPosition.Y;
               e.Graphics.FillRectangle(bgTime, 0, 0, Width, pos);
            }
         }
      }

      protected override void OnPaint(PaintEventArgs e)
      {
         base.OnPaint(e);

         Graphics g = e.Graphics;
         Pen p = new Pen(gridColor);

         int sz = Math.Abs(timeLine.Finish - timeLine.Start) + 1;
         for (int i = 0; i <= sz; i++)
         {
            int pos = i * timeLine.Step;
            int y = pos + AutoScrollPosition.Y;
            g.DrawLine(p, 0, y, Width, y);
         }

         int startX = timeLine.Width, index = 1;

         if (header != null)
            for (int i = 0; i < adapter.GetBandCount(); i++)
            {
               Band band = adapter.GetBandAt(i);
               int width = header.GetWidth(index++);
               DrawBand(g, band, startX, width);
               startX += width;
            }

         if (selectData != null && selectData.BandItem == null)
         {
            Rectangle r = getSelectedBounds();
            Pen sp = new Pen(Color.Blue, 1);
            sp.DashStyle = System.Drawing.Drawing2D.DashStyle.DashDot;
            g.DrawRectangle(sp, r);
         }
         
         p.Dispose();
      }

      internal RectangleF GetItemBounds(BandItem task, float startX, float width)
      {
         int sy = timeLine.GetPosition(task.Start.TimeOfDay) + AutoScrollPosition.Y + 2;
         int ey = timeLine.GetPosition(task.Finish.TimeOfDay) + AutoScrollPosition.Y - 1;

         if (ey < sy)
            ey = timeLine.GetPosition(task.Start.Hour + 1);

         return new RectangleF(startX + 2, sy, width, ey - sy);
      }

      internal Color GetTaskColor(BandItem task)
      {
         TaskColorEventArgs arg = new TaskColorEventArgs(task, Color.LightGreen);
         if (TaskColor != null)
            TaskColor.Invoke(this, arg);

         return arg.Color;
      }

      public class ViewProp
      {
         public bool addr = true;
         public bool name = true;
         public bool text = true;

         public virtual ViewProp Inflate()
         {
            return this;
         }
      }

      public enum SelectBorder {Default, Selection, Buffer }

      private SelectBorder GetBorderStyle(BandItem task)
      {
         SelectBorder result = SelectBorder.Default;

         if (buffer != null && buffer.BandItem == task)
            result = SelectBorder.Buffer;
         else if (selectData != null && selectData.BandItem == task)
            result = SelectBorder.Selection;

         return result;
      }

      private void ItemDrawProc(List<ItemDraw> list, float x, float w, ViewProp prop, Graphics g)
      {

         foreach (ItemDraw td in list)
         {
            const int CHILD_PADDING = 2;
            int count = td.Count;
            float tw = count != 0 ? ( w - (count - 1)  * CHILD_PADDING) / count : w;

            RectangleF r = td.Draw(g, this, x, tw, prop, GetBorderStyle(td.item));

            if (CustomItemDraw != null)
               CustomItemDraw(this, new GridItemDrawArgs(g, r, td.Item.Stored));

            if (td.Childs.Count > 0)
               ItemDrawProc(td.Childs, x + tw + CHILD_PADDING, w, prop, g);
         }
      }

      private void DrawBand(Graphics g, Band band, int startX, int width)
      {
         List<ItemDraw> list = CreateDrawList(band.Items);

         Pen p = new Pen(gridColor);

         int cx = startX;
         g.DrawLine(p, cx, 0, cx, Bottom);
         cx += width;
         g.DrawLine(p, cx, 0, cx, Bottom);

         const int CLICK_REGION = 20;
         width -= CLICK_REGION;
         ViewProp prop = ViewProperty.Inflate();
         
         ItemDrawProc(list, startX, width, prop, g);

         drawedTask[band] = list;
      }

      private void CollectItemSib(ItemDraw td, List<ItemDraw> res)
      {
         foreach (ItemDraw item in td.next)
         {
            CollectItemSib(item, res);
            res.Add(item);
         }
      }

      private List<ItemDraw> CreateDrawList(BandItemsCollection items)
      {
         List<ItemDraw> result = new List<ItemDraw>();
         List<BandItem> src = new List<BandItem>(items);
         src.Sort((lhs, rhs) => { return CmpItemDraw(lhs, rhs); });

         ItemDraw current = null;
         foreach (BandItem task in src)
         {
            ItemDraw check = new ItemDraw(task);
            if (current == null)
            {
               current = check;
               result.Add(current);

               continue;
            }

            ItemDraw ints = current.FindIntersect(check);

            if (ints == null)
            {
               current = check;
               result.Add(current);

               continue;
            }

            ints.Add(check);
         }

         return result;
      }

      private int CmpItemDraw(BandItem lhs, BandItem rhs)
      {
         int result = lhs.Start.CompareTo(rhs.Start);

         if (result == 0)
            result = lhs.Created.CompareTo(rhs.Created);

         return result;
      }

      [Browsable(true)]
      public Color GridColor
      { 
         get { return gridColor; }
         set
         {
            if (gridColor != value)
            {
               gridColor = value;
               Refresh();
            }
         } 
      }

      [Browsable(true)]
      public int Start
      {
         get { return timeLine.Start; }
         set { timeLine.Start = value; }
      }

      [Browsable(true)]
      public int Finish
      {
         get { return timeLine.Finish; }
         set 
         { 
            timeLine.Finish = value;
            AutoScrollMinSize = new Size(0, timeLine.Height - 10);
         }
      }

      [Browsable(true), DescriptionAttribute("Цвет фона когда время прошло")]
      public Color BackLostColor
      {
         get { return outWorkColor; }
         set { outWorkColor = value; }
      }

      //public BandCollection Bands { get { return bands; } }

      [Browsable(true)]
      public override ContextMenuStrip ContextMenuStrip 
      { 
         get { return base.ContextMenuStrip; }
         set { base.ContextMenuStrip = value;  }
      }

      [Browsable(true)]
      public TaskContextMenuStrip ItemContextMenuStrip
      {
         get { return itemMenu; }
         set { itemMenu = value; }
      }

      public ViewProp ViewProperty { get { return viewProp; } set { viewProp = value; } }
      public EditBuffer Buffer { get { return buffer; } set { buffer = value; } }
      public SelectData SelectionItem { get { return selectData; } }

      [Browsable(true)]
      public HeaderControl Header 
      {
         get { return header; }
         set
         {  
            header = value;
            if (header != null)
            {
               header.InsertColumn(0, string.Empty, HorizontalAlignment.Left, timeLine.Width, HeaderSortMarker.None, true);
               header.Tracking += new EventHandler(Tracking);
            }
         }
      }

      public SelectData SelectData
      {
         get { return selectData;  }
         set { selectData = value; }
      }

      protected override void OnScroll(ScrollEventArgs se)
      {
         base.OnScroll(se);
         Invalidate();
      }

      internal TimeGridAdapter Adapter { get { return adapter; } 
         set 
         { 
            adapter = value;
            adapter.SetOnBandsChanged(bands_Changed);
         } 
      }

      internal void FireGridPastHandler(EditBuffer buffer)
      {
         if (GridPastHandler != null && selectData != null)
         {
            GridPastArgs arg = new GridPastArgs(buffer, selectData.Band, selectData.Hour);
            GridPastHandler(this, arg);
         }
      }

      public bool BufferContainsItem() { return buffer.Contains();  }
   }

   class TimeGridDesigner : ControlDesigner
   {
      protected override void PostFilterProperties(System.Collections.IDictionary properties)
      {
         base.PostFilterProperties(properties);

         //List<String> allowed = new List<string>(new string[] { "ForeColor", "BackColor", "Font", "Dock" });

         //List<string> needUpdate = new List<string>();

         //foreach (DictionaryEntry de in properties)
         //{
         //   if (allowed.Contains(de.Key as string))
         //      continue;

         //   PropertyDescriptor prop = de.Value as PropertyDescriptor;
         //   if (prop.ComponentType == typeof(Control))
         //   {
         //      BrowsableAttribute ba = prop.Attributes[typeof(BrowsableAttribute)] as BrowsableAttribute;
         //      if (ba != null && ba.Browsable == false)
         //         continue;
               
         //      needUpdate.Add((string)de.Key);
         //   }
         //}

         //foreach (string key in needUpdate)
         //{
         //   PropertyDescriptor pd = (PropertyDescriptor)properties[key];
         //   properties[key] = TypeDescriptor.CreateProperty(Component.GetType(), pd, new Attribute[] { BrowsableAttribute.No });
         //}
      }
   }

   

   #region Events declarations

   public class GridItemDrawArgs : EventArgs
   {
      public RectangleF rect;
      public object stored;
      public Graphics g;

      public GridItemDrawArgs(Graphics g, RectangleF r, object stored)
      {
         this.rect = r;
         this.stored = stored;
         this.g = g;
      }
   }

   public class GridPastArgs : GridEventArgs
   {
      public object buffer;

      public GridPastArgs(object buffer, Band band, int hour)
         :base(band, hour)
      {
         this.buffer = buffer;
      }
   }

   public class GridEventArgs : EventArgs
   {
      public Band band;
      public int hour;

      public GridEventArgs(Band band, int hour)
      {
         this.band = band;
         this.hour = hour;
      }
   }

   public class TaskEventArgs : EventArgs
   {
      protected BandItem item;

      public TaskEventArgs(BandItem item)
      {
         this.item = item;
      }
   
      public BandItem Item { get { return item; } }
   }

   public class TaskColorEventArgs : TaskEventArgs
   {
      Color color;

      public TaskColorEventArgs(BandItem task, Color color)
         : base(task)
      {
         this.color = color;
      }

      public Color Color { get { return color; } set { color = value; } }
   }

   public class TaskHintEventArgs : TaskEventArgs
   {
      String hint;

      public TaskHintEventArgs(BandItem task)
         : base(task)
      {
      }

      public String Hint { get { return hint; } set { hint = value; } }
   }

   public delegate void TaskEventHandler(object sender, TaskEventArgs arg);
   public delegate void TaskColorEventHandler(object sender, TaskColorEventArgs arg);
   public delegate void TaskHintEventHandler(object sender, TaskHintEventArgs arg);
   public delegate void GridEventHandler(object sender, GridEventArgs arg);
   public delegate void GridItemDraw(object sender, GridItemDrawArgs arg);
   public delegate void GridPastHandler(object sender, GridPastArgs arg);

   #endregion
}
