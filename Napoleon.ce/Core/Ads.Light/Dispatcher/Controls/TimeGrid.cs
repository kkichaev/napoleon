using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using System.Windows.Forms.Design;
using System.Collections;

namespace GRSoft.Ads.Dispatcher
{
   [Designer(typeof(TimeGridDesigner))]
   public partial class TimeGrid : UserControl
   {
      const int MIN_BAND_WIDTH = 10;

      Color gridColor = Color.Black;
      Color backLostColor = Color.DarkGray;

      DateTime drawedDate = DateTime.Now.Date;
      BandCollection bands = new BandCollection();

      Dictionary<Band, TaskList> drawedTask = new Dictionary<Band, TaskList>();

      Band sizedBand = null;
      Point startResizePos = new Point();
      int startWidth = 0, bandLeft;
      TaskContextMenuStrip taskContextMenuStrip;
      ViewProp viewProp;
      EditBuffer buffer;

      public TimeGrid()
      {
         InitializeComponent();

         timeLine.Changed += new EventHandler(timeLine_Changed);
         bands.Changed += new EventHandler(bands_Changed);

         header.InsertColumn(0, "", HorizontalAlignment.Left, timeLine.Width, HeaderSortMarker.None, true);
         header.Tracking += new EventHandler(Tracking);
         
         Size sz = timeLine.MinimumSize;
         MinimumSize = new Size(sz.Width + bands.Count * MIN_BAND_WIDTH, sz.Height);

         buffer = new EditBuffer(this);
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
         RefreshAutoScroll();
      }

      void bands_Changed(object sender, EventArgs e)
      {
         Size sz = timeLine.MinimumSize;
         MinimumSize = new Size(sz.Width + bands.Count * MIN_BAND_WIDTH, sz.Height);
         RefreshAutoScroll();

         if (Handle == IntPtr.Zero)
            return;

         header.ClearColumns();

         int count = bands.Count;
         if (count == 0) count = 1;
         int bandWidth = (Width - timeLine.Width) / count;
         if (bandWidth < MIN_BAND_WIDTH)
            bandWidth = MIN_BAND_WIDTH;

         int index = 1;

         foreach (Band b in bands)
            header.InsertColumn(index++, b.name, HorizontalAlignment.Left, bandWidth, HeaderSortMarker.None);
      }

      void timeLine_Changed(object sender, EventArgs e)
      {
         RefreshAutoScroll();
         Refresh();
      }

      void RefreshAutoScroll()
      {
         AutoScrollMinSize = new Size(timeLine.MinimumSize.Width, timeLine.MinimumSize.Height);
         AutoScrollMargin = new Size(10, 10);
      }

      class HitTestData
      {
         public TaskHead task;
         public Band band;
         public bool onTaskDivider;
         public int hour;
      }

      HitTestData HitTest(Point pt)
      {
         Point check = PointToScreen(pt);
         check = header.PointToClient(check);
         check.Y = header.Height - 4;

         HitTestData ht = new HitTestData();
         Win32.HDHITTESTINFO hti = header.HitTest(check);

         if (hti.iItem > 0 && hti.iItem <= bands.Count)
            ht.band = bands[hti.iItem - 1];

         if ((hti.flags & Win32.HHT_ONDIVIDER) == Win32.HHT_ONDIVIDER)
         {
            ht.onTaskDivider = true;
            return ht;
         }

         if (ht.band != null)
         {
            Band b = ht.band;
            ht.hour = timeLine.GetHour(pt.Y);
            if (drawedTask.ContainsKey(b))
            {
               foreach (TaskDraw td in drawedTask[b])
               {
                  if (td.Bounds.Contains(pt))
                  {
                     ht.task = td.Task;
                     break;
                  }

                  bool finded = false;
                  foreach (TaskDraw tdc in td.Childs)
                  {
                     if (tdc.Bounds.Contains(pt))
                     {
                        ht.task = tdc.Task;
                        finded = true;
                        break;
                     }
                  }
                  if (finded) break;
               }
            }
         }
         return ht;
      }

      void BeginResizeBand(Band b)
      {
         sizedBand = b;
         startResizePos = Cursor.Position;

         int index = bands.IndexOf(b) + 1;
         startWidth = header.GetWidth(index);
         Win32.SetCapture(Handle);

         int i = 0;
         bandLeft = timeLine.Width;
         for (; i < index; i++)
            bandLeft += header.GetWidth(i);
      }

      void EndResizeBand()
      {
         if (sizedBand != null)
         {
            sizedBand = null;
            if (Win32.GetCapture() == Handle)
               Win32.ReleaseCapture();

            RefreshAutoScroll();
         }
      }

      void ResizeBand()
      {
         int diff = Cursor.Position.X - startResizePos.X;
         int index = bands.IndexOf(sizedBand);

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
            if (ht.task != null)
            {
               TaskEventArgs args = new TaskEventArgs(ht.task);
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

      public class SelectData
      {
         private Band band;
         private int hour;
         private TaskHead task;

         public SelectData(Band band, int hour, TaskHead task)
         {
            this.band = band;
            this.hour = hour;
            this.task = task;
         }

         public Band Band { get { return band; } set { band = value;} }
         public int Hour { get { return hour; } set { hour = value; } }
         public TaskHead Task { get { return task; } set { task = value; } }
      }

      SelectData selectData = null;

      private Rectangle getSelectedBounds()
      {
         int pos = bands.IndexOf(selectData.Band);
         int x = timeLine.Width;

         for (int i = 1; i <= pos; i++)
            x += header.GetWidth(i);

         int y1 = timeLine.GetPosition(selectData.Hour - 1);
         int y2 = timeLine.GetPosition(selectData.Hour);

         return new Rectangle(x + 2, y1 + header.Bottom + 2, header.GetWidth(pos + 1) - 4, y2 - y1 - 4); 
      }

      protected override void OnMouseDown(MouseEventArgs e)
      {
         base.OnMouseDown(e);
         HitTestData ht = HitTest(PointToClient(Cursor.Position));

         if (ht.onTaskDivider)
         {
            if (ht.band != null)
               BeginResizeBand(ht.band);
         }
         else
         {
            selectData = new SelectData(ht.band, ht.hour, ht.task);
            Invalidate();
         }

         if(ht.task != null && e.Button == MouseButtons.Right && taskContextMenuStrip != null)
         {
            taskContextMenuStrip.Task = ht.task;
            taskContextMenuStrip.Show(PointToScreen(new Point(e.X, e.Y)));
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
            if (ht.task != null)
            {
               TaskEventArgs args = new TaskEventArgs(ht.task);
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
            if (ht.onTaskDivider)
               Cursor = Cursors.VSplit;
            else
            {
               String hint = null;
               if (ht.task != null)
               {
                  TaskHintEventArgs arg = new TaskHintEventArgs(ht.task);
                  arg.Hint = ht.task.text;

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

            if (e.Delta > 0)
            {
               timeLine.Step+=2;
            }
            else
            {
               timeLine.Step-=2;
            }
            RefreshAutoScroll();
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
         DateTime now = DateTime.Now;
         int cmp = drawedDate.CompareTo(now.Date);
         if (cmp == 0)
         {
            int pos = timeLine.GetPosition(now);
            if (pos >= 0)
            {
               pos += header.Bottom;
               Brush b = new SolidBrush(backLostColor);
               e.Graphics.FillRectangle(b, 0, 0, Width, pos);
               b.Dispose();

               b = new SolidBrush(BackColor);
               e.Graphics.FillRectangle(b, 0, pos, Width, Height - pos);
               b.Dispose();
            }
            else
               cmp = (pos == -1) ? -1 : 1;
         }
         if( cmp != 0 )
         {
            int pos1 = timeLine.GetPosition(timeLine.HourStart + 1);
            int pos2 = timeLine.GetPosition(timeLine.HourEnd - 2);
            Color back = (cmp < 0) ? backLostColor : BackColor;
            Brush b = new SolidBrush(back);
            Brush bd = new SolidBrush(Color.White);
            e.Graphics.FillRectangle(b, 0, 0, Width, pos1+header.Bottom);
            e.Graphics.FillRectangle(bd, 0, pos1 + header.Bottom, Width, pos2 - pos1);
            e.Graphics.FillRectangle(b, 0, pos2 + header.Bottom, Width, Height - pos2 - header.Bottom);
            b.Dispose();
            bd.Dispose();
         }
         //base.OnPaintBackground(e);
      }

      public class EditBuffer
      {
         private SelectData buffer;
         private TimeGrid grid;
         public enum Operation { Cut, Copy }
         Operation oper = Operation.Copy;

         public EditBuffer(TimeGrid grid)
         {
            this.grid = grid;
         }

         public virtual void Copy()
         {
            if (grid.selectData != null)
            {
               buffer = grid.selectData;
               oper = Operation.Copy;
               grid.Invalidate();
            }
         }

         public virtual void Cut()
         {
            if (grid.selectData != null)
            {
               buffer = grid.selectData;
               oper = Operation.Cut;
               grid.Invalidate();
            }
         }

         public virtual Operation Past(DateTime date)
         {
            TaskHead copy = (TaskHead)buffer.Task.Clone();

            copy.start = date.Date.AddHours(grid.selectData.Hour);
            copy.finish = date.Date.AddHours(grid.selectData.Hour + (buffer.Task.finish-buffer.Task.start).Hours);
            grid.selectData.Band.Tasks.Add(copy);

            if (oper == Operation.Cut)
               buffer.Band.Tasks.Remove(buffer.Task);

            grid.selectData.Task = copy;
            grid.Invalidate();

            return oper;
         }

         public TaskHead Task { get { return buffer != null ? buffer.Task : null; } }
      }

      protected override void OnPaint(PaintEventArgs e)
      {
         base.OnPaint(e);

         Graphics g = e.Graphics;
         Pen p = new Pen(gridColor);
         foreach (TimeLine.Item item in timeLine.Items)
         {
            int y = item.pos + timeLine.Top;
            g.DrawLine(p, 0, y, Width, y);
         }

         int startX = timeLine.Width, index = 1;
         foreach (Band band in bands)
         {
            int width = header.GetWidth(index++);
            DrawBand(g, band, startX, width);
            startX += width;
         }

         if (selectData != null && selectData.Task == null)
         {
            Rectangle r = getSelectedBounds();
            Pen sp = new Pen(Color.Blue, 1);
            sp.DashStyle = System.Drawing.Drawing2D.DashStyle.DashDot;
            g.DrawRectangle(sp, r);
         }
         
         p.Dispose();
      }

      internal Rectangle GetTaskBounds(TaskHead task, int startX, int width)
      {
         int sy = timeLine.GetPosition(task.start);
         int ey = timeLine.GetPosition(task.finish);

         if (ey < 0)
            ey = timeLine.GetPosition(timeLine.HourEnd);

         return new Rectangle(startX, sy + header.Bottom, width, ey - sy);
      }

      internal Color GetTaskColor(TaskHead task)
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

      public enum SelectBorder {None, Selection, Buffer }

      private SelectBorder GetBorderStyle(TaskHead task)
      {
         SelectBorder result = SelectBorder.None;

         if (buffer != null && buffer.Task == task)
            result = SelectBorder.Buffer;
         else if (selectData != null && selectData.Task == task)
            result = SelectBorder.Selection;

         return result;
      }

      private void DrawBand(Graphics g, Band band, int startX, int width)
      {
         TaskList list = new TaskList(band.Tasks);

         Pen p = new Pen(gridColor);

         int cx = startX;
         g.DrawLine(p, cx, header.Bottom, cx, Bottom);
         cx += width;
         g.DrawLine(p, cx, header.Bottom, cx, Bottom);
         ViewProp prop = ViewProperty.Inflate();

         foreach (TaskDraw td in list)
         {
            int count = td.Count;
            int taskWidth = width / count;

            td.Draw(g, this, startX, taskWidth, prop, GetBorderStyle(td.task));
         }

         drawedTask[band] = list;
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
      public short HourStart
      {
         get { return timeLine.HourStart; }
         set { timeLine.HourStart = value; }
      }

      [Browsable(true)]
      public short HourEnd
      {
         get { return timeLine.HourEnd; }
         set { timeLine.HourEnd = value; }
      }

      [Browsable(true),
      DescriptionAttribute("Цвет фона когда время прошло")
      ]
      public Color BackLostColor
      {
         get { return backLostColor; }
         set { backLostColor = value; }
      }

      [Browsable(true)]
      int Step
      {
         get { return timeLine.Step; }
         set { timeLine.Step = value; }
      }

      public BandCollection Bands { get { return bands; } }

      [Browsable(true)]
      public override ContextMenuStrip ContextMenuStrip
      {
         get
         {
            return base.ContextMenuStrip;
         }
         set
         {
            base.ContextMenuStrip = value;
         }
      }

      [Browsable(true)]
      public TaskContextMenuStrip TaskContextMenuStrip
      {
         get { return taskContextMenuStrip; }
         set { taskContextMenuStrip = value; }
      }

      public ViewProp ViewProperty { get { return viewProp; } set { viewProp = value; } }
      public EditBuffer Buffer { get { return buffer; } set { buffer = value; } }
      public SelectData SelectionItem { get { return selectData; } }
   }

   class TimeGridDesigner : ControlDesigner
   {
      protected override void PostFilterProperties(System.Collections.IDictionary properties)
      {
         base.PostFilterProperties(properties);

         List<String> allowed = new List<string>(new string[] { "ForeColor", "BackColor", "Font", "Dock" });

         List<PropertyDescriptor> newProps = new List<PropertyDescriptor>();

         foreach (DictionaryEntry de in properties)
         {
            if (allowed.Contains(de.Key as string))
               continue;

            PropertyDescriptor prop = de.Value as PropertyDescriptor;
            if (prop.ComponentType == typeof(Control))
            {
               bool isHidden = false;
               AttributeCollection runtimeAttributes = prop.Attributes;
               foreach (Attribute a in runtimeAttributes)
               {
                  BrowsableAttribute ba = a as BrowsableAttribute;
                  if (ba != null)
                  {
                     isHidden = !ba.Browsable;
                     break;
                  }
               }
               if (isHidden)
                  continue;

               Attribute[] attrs = new Attribute[runtimeAttributes.Count + 1];
               runtimeAttributes.CopyTo(attrs, 0);
               attrs[runtimeAttributes.Count] = BrowsableAttribute.No;
               prop = TypeDescriptor.CreateProperty(prop.ComponentType, (string)de.Key, prop.PropertyType, attrs);
               newProps.Add(prop);
            }
         }

         foreach (PropertyDescriptor pd in newProps)
         {
            properties[pd.Name] = pd;
         }
      }
   }

   class TaskDraw
   {
      public TaskHead task;
      public TaskDraw prev;
      
      List<TaskDraw> next = new List<TaskDraw>();
      Rectangle bounds = new Rectangle();

      public TaskDraw(TaskHead task)
      {
         this.task = task;
      }

      public TaskHead Task { get { return task; } }
      public int Count { get { return First.CountLargePath(); } }
      public List<TaskDraw> Childs { get { return next; } }
      public Rectangle Bounds { get { return bounds; } }

      public void Add(TaskDraw task)
      {
         task.prev = this;
         next.Add(task);
      }

      private void fixBounds(RectangleF origin, ref RectangleF fixing)
      {
         const int PADDING = 5;

         if (origin.Bottom < fixing.Bottom)
            fixing.Height = origin.Bottom - fixing.Y - PADDING;

      }

      public void Draw(Graphics g, TimeGrid owner, int startX, int width,
         TimeGrid.ViewProp prop, TimeGrid.SelectBorder border)
      {
         bounds = owner.GetTaskBounds(task, startX, width);
         Color color = owner.GetTaskColor(task);

         Color fillColor = Color.FromArgb(180, color);
         Brush b = new SolidBrush(fillColor);
         g.FillRectangle(b, bounds);

         Brush textB = new SolidBrush(owner.ForeColor);
         StringFormat sf = new StringFormat();
         sf.LineAlignment = StringAlignment.Near;
         sf.Alignment = StringAlignment.Near;
         sf.Trimming = StringTrimming.EllipsisWord;

         float height = 0;

         if (prop.addr)
         {
            Font addressFont = new Font(owner.Font, FontStyle.Bold);
            string address = task.city + " " + task.street+" " + task.house;
            SizeF sz = g.MeasureString(address, addressFont, width);
            RectangleF addressBounds = new RectangleF(bounds.X, bounds.Y, sz.Width, sz.Height);
            g.DrawString(address, addressFont, textB, addressBounds, sf);
            height += sz.Height;
         }

         if (prop.name)
         {
            string client = task.clientname + " " + task.clientphone;
            SizeF sz = g.MeasureString(client, owner.Font, width, sf);
            RectangleF clientBounds = new RectangleF(bounds.X, bounds.Y + height,
               sz.Width, sz.Height);
            fixBounds(bounds, ref clientBounds);
            g.DrawString(client, owner.Font, textB, clientBounds, sf);
            height += sz.Height;
         }

         if (prop.text)
         {
            Font textFont = new Font(owner.Font, FontStyle.Italic);
            SizeF sz = g.MeasureString(task.text, textFont, width);
            RectangleF textBounds = new RectangleF(bounds.X, bounds.Y + height,
               sz.Width, sz.Height);
            fixBounds(bounds, ref textBounds);
            g.DrawString(task.text, textFont, textB, textBounds, sf);
            height += sz.Height;
         }

         if (border != TimeGrid.SelectBorder.None)
         {
            Rectangle r = new Rectangle(bounds.X + 2, bounds.Y + 2, bounds.Width - 4, bounds.Height - 4);
            Pen sp = new Pen(Color.Blue, 1);

            if (border == TimeGrid.SelectBorder.Selection)
               sp.DashStyle = System.Drawing.Drawing2D.DashStyle.DashDot;
            else if (border == TimeGrid.SelectBorder.Buffer)
               sp.DashStyle = System.Drawing.Drawing2D.DashStyle.Solid;

            g.DrawRectangle(sp, r);
         }

         b.Dispose();
         textB.Dispose();

         startX += width;
         foreach (TaskDraw td in next)
            td.Draw(g, owner, startX, width, prop, border);
      }

      bool TimeInTaskTime(DateTime time)
      {
         return time.CompareTo(task.start) >= 0 && time.CompareTo(task.finish) <= 0;
      }

      bool Intersects(TaskDraw check)
      {
         return TimeInTaskTime(check.task.start) || TimeInTaskTime(check.task.finish);
      }

      TaskDraw FindNextIntersect(TaskDraw check)
      {
         foreach (TaskDraw td in next)
         {
            TaskDraw fnd = td.FindNextIntersect(check);
            if (fnd != null)
               return fnd;
         }

         return Intersects(check) ? this : null;
      }

      public TaskDraw FindIntersect(TaskDraw check)
      {
         return First.FindNextIntersect(check);
      }

      int CountLargePath()
      {
         int count = 0;
         foreach (TaskDraw td in next)
         {
            int ct = td.CountLargePath();
            if (count < ct)
               count = ct;
         }
         return count + 1;
      }

      TaskDraw First
      {
         get
         {
            TaskDraw ct = this;
            while (true)
            {
               if (ct.prev == null)
                  return ct;
               ct = ct.prev;
            }
         }
      }
   }

   class TaskList : List<TaskDraw>
   {
      public TaskList(TaskCollection tasks)
      {
         List<TaskHead> src = new List<TaskHead>(tasks);
         src.Sort(CompareTask);

         TaskDraw current = null;
         foreach (TaskHead task in src)
         {
            TaskDraw check = new TaskDraw(task);
            if (current == null)
            {
               current = check;
               Add(current);

               continue;
            }
            TaskDraw ints = current.FindIntersect(check);
            if (ints == null)
            {
               current = check;
               Add(current);

               continue;
            }

            ints.Add(check);
         }
      }

      int CompareTask(TaskHead t1, TaskHead t2)
      {
         return t1.start.CompareTo(t2.start);
      }
   }

   public class Band
   {
      public string name;
      TaskCollection tasks;

      public event EventHandler Changed;

      public Band()
         : this("")
      {
      }

      public Band(string name)
      {
         this.name = name;

         tasks = new TaskCollection(this);
         tasks.Changed += new EventHandler(tasks_Changed);
      }

      void tasks_Changed(object sender, EventArgs e)
      {
         if (Changed != null)
            Changed.Invoke(this, EventArgs.Empty);
      }

      public TaskCollection Tasks { get { return tasks; } }
   }

   public class BandCollection : List<Band>
   {
      public BandCollection()
      {
      }

      public event EventHandler Changed;

      new public void Add(Band band)
      {
         base.Add(band);
         band.Changed += new EventHandler(band_Changed);

         if (Changed != null)
            Changed.Invoke(this, EventArgs.Empty);
      }

      new public void AddRange(IEnumerable<Band> items)
      {
         base.AddRange(items);
         foreach (Band band in items)
            band.Changed += new EventHandler(band_Changed);

         if (Changed != null)
            Changed.Invoke(this, EventArgs.Empty);
      }

      new public void Clear()
      {
         foreach(Band band in this)
            band.Changed -= new EventHandler(band_Changed);

         base.Clear();

         if (Changed != null)
            Changed.Invoke(this, EventArgs.Empty);
      }

      void band_Changed(object sender, EventArgs e)
      {
         if (Changed != null)
            Changed.Invoke(this, EventArgs.Empty);
      }

      new public void Remove(Band band)
      {
         if (base.Remove(band) && Changed != null)
            Changed.Invoke(this, EventArgs.Empty);
      }
   }

   public class TaskCollection : List<TaskHead>
   {
      public event EventHandler Changed;

      Band owner;
      public TaskCollection(Band owner)
      {
         this.owner = owner;
      }

      new public void Add(TaskHead task)
      {
         base.Add(task);

         task.Band = owner;
         task.Changed += new EventHandler(task_Changed);

         if (Changed != null)
            Changed.Invoke(this, EventArgs.Empty);
      }

      new public void AddRange(IEnumerable<TaskHead> items)
      {
         base.AddRange(items);
         foreach (TaskHead task in items)
            task.Changed += new EventHandler(task_Changed);

         if (Changed != null)
            Changed.Invoke(this, EventArgs.Empty);
      }

      public void Replace(IEnumerable<TaskHead> items)
      {
         Clear(false);
         AddRange(items);
      }

      public void Clear(bool fireEvent)
      {
         foreach(TaskHead task in this)
            task.Changed -= new EventHandler(task_Changed);

         base.Clear();

         if (fireEvent && Changed != null)
            Changed.Invoke(this, EventArgs.Empty);
      }

      new public void Clear()
      {
         Clear(true);
      }

      new public void Remove(TaskHead task)
      {
         task.Changed -= new EventHandler(task_Changed);
         if (base.Remove(task) && Changed != null)
            Changed.Invoke(this, EventArgs.Empty);
      }

      void task_Changed(object sender, EventArgs e)
      {
         if (Changed != null)
            Changed.Invoke(this, EventArgs.Empty);
      }
   }

   #region Events declarations

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
      protected TaskHead task;

      public TaskEventArgs(TaskHead task)
      {
         this.task = task;
      }
   
      public TaskHead Task { get { return task; } }
   }

   public class TaskColorEventArgs : TaskEventArgs
   {
      Color color;

      public TaskColorEventArgs(TaskHead task, Color color)
         : base(task)
      {
         this.color = color;
      }

      public Color Color { get { return color; } set { color = value; } }
   }

   public class TaskHintEventArgs : TaskEventArgs
   {
      String hint;

      public TaskHintEventArgs(TaskHead task)
         : base(task)
      {
      }

      public String Hint { get { return hint; } set { hint = value; } }
   }

   public delegate void TaskEventHandler(object sender, TaskEventArgs arg);
   public delegate void TaskColorEventHandler(object sender, TaskColorEventArgs arg);
   public delegate void TaskHintEventHandler(object sender, TaskHintEventArgs arg);
   public delegate void GridEventHandler(object sender, GridEventArgs arg);

   #endregion
}
