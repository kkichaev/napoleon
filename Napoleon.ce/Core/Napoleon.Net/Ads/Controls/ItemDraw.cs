using System;
using System.Collections.Generic;
using System.Drawing;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class ItemDraw
   {
      public BandItem item;
      public ItemDraw prev;

      public List<ItemDraw> next = new List<ItemDraw>();
      RectangleF bounds = new RectangleF();

      public ItemDraw(BandItem task)
      {
         this.item = task;
      }

      public BandItem Item { get { return item; } }
      public int Count { get { return First.CountLargePath(); } }
      public List<ItemDraw> Childs { get { return next; } }
      public RectangleF Bounds { get { return bounds; } }

      public void Add(ItemDraw task)
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

      public RectangleF Draw(Graphics g, TimeGrid owner, float startX, float width,
         TimeGrid.ViewProp prop, TimeGrid.SelectBorder border)
      {
         bounds = owner.GetItemBounds(item, startX, width);
         Color fillColor = item.Color;
         Brush b = new SolidBrush(fillColor);
         g.FillRectangle(b, bounds);

         Brush textB = new SolidBrush(owner.ForeColor);
         StringFormat sf = new StringFormat();
         sf.LineAlignment = StringAlignment.Near;
         sf.Alignment = StringAlignment.Near;
         sf.Trimming = StringTrimming.EllipsisWord;

         RectangleF r = new RectangleF(bounds.X + 1, bounds.Y + 1, bounds.Width - 2, bounds.Height - 2);
         Pen sp = new Pen(MakeDarknes(fillColor), 2.0f);

         if (border == TimeGrid.SelectBorder.Selection)
            sp.DashStyle = System.Drawing.Drawing2D.DashStyle.DashDot;
         else if (border == TimeGrid.SelectBorder.Default)
            sp.DashStyle = System.Drawing.Drawing2D.DashStyle.Solid;

         g.DrawRectangle(sp, r.X, r.Y, r.Width, r.Height);

         b.Dispose();
         textB.Dispose();

         return bounds;
      }

      private Color MakeDarknes(Color color)
      {
         
         const float FACTOR = 0.7f;
         float red = FACTOR * color.R ;
         float green = FACTOR * color.G;
         float blue = FACTOR * color.B;
         return Color.FromArgb(color.A, (int)red, (int)green, (int)blue);
      }

      bool TimeInTaskTime(DateTime time)
      {
         return time.CompareTo(item.Start) >= 0 && time.CompareTo(item.Finish) < 0;
      }

      bool Intersects(ItemDraw check)
      {
         return TimeInTaskTime(check.item.Start) || TimeInTaskTime(check.item.Finish);
      }

      ItemDraw FindNextIntersect(ItemDraw check)
      {
         foreach (ItemDraw td in next)
         {
            ItemDraw fnd = td.FindNextIntersect(check);
            if (fnd != null)
               return fnd;
         }

         return Intersects(check) ? this : null;
      }

      public ItemDraw FindIntersect(ItemDraw check)
      {
         return First.FindNextIntersect(check);
      }

      int CountLargePath()
      {
         int count = 0;
         foreach (ItemDraw td in next)
         {
            int ct = td.CountLargePath();
            if (count < ct)
               count = ct;
         }
         return count + 1;
      }

      ItemDraw First
      {
         get
         {
            ItemDraw ct = this;
            while (true)
            {
               if (ct.prev == null)
                  return ct;
               ct = ct.prev;
            }
         }
      }
   }
}
