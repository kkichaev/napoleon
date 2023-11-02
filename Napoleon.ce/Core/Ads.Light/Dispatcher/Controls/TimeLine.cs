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
   [Designer(typeof(TimeLineDesigner))]
   public class TimeLine : Control
   {
      short timeStart = 7, timeEnd = 19;
      int step = 30;

      Font minuteFont;

      public class Item
      {
         public int hour;
         public int pos;
      }

      public TimeLine()
      {
         CalcMinuteFont();
         TabStop = false;
      }

      protected override void OnFontChanged(EventArgs e)
      {
         base.OnFontChanged(e);
         CalcMinuteFont();
         UpdateLayout();
      }

      void CalcMinuteFont()
      {
         minuteFont = new Font(Font.FontFamily, (float)(Font.Size * 0.9), Font.Style);
      }

      public List<Item> Items
      {
         get
         {
            List<Item> ret = new List<Item>();
            for (int i = timeStart, pos = step; i <= timeEnd; i++, pos += step)
            {
               Item item = new Item();
               item.hour = i;
               item.pos = pos;

               ret.Add(item);
            }
            return ret;
         }
      }

      public event EventHandler Changed;

      [Browsable(true)]
      public short HourStart
      {
         get { return timeStart; }
         set
         {
            if (timeStart != value)
            {
               timeStart = value;
               UpdateLayout();
            }
         }
      }

      [Browsable(true)]
      public short HourEnd
      {
         get { return timeEnd; }
         set
         {
            if (timeEnd != value)
            {
               timeEnd = value;
               UpdateLayout();
            }
         }
      }

      [Browsable(true)]
      public int Step
      {
         get { return step; }
         set
         {
            if (step != value && value > 0)
            {
               step = value;
               UpdateLayout();
               Height = MinimumSize.Height;
            }
         }
      }

      public int GetPosition(DateTime time)
      {
         int hour = time.Hour;
         if (hour < timeStart )
            return -1;
         if (hour > timeEnd)
            return -2;

         int pos = (int)((hour - timeStart) * step + step / 60.0 * time.Minute);
         return pos;
      }

      public int GetPosition(int h)
      {
         int hour = h;
         if (hour < timeStart)
            return -1;
         if (hour > timeEnd)
            return -2;

         int pos = (int)((hour - timeStart) * step + step);
         return pos;
      }

      public int GetHour(int y)
      {
         int result = -1;

         foreach(Item i in Items)
            if (i.pos <= y && i.pos + step > y)
            {
               result = i.hour;
               break;
            }

         return result;
      }

      protected override void OnHandleCreated(EventArgs e)
      {
         base.OnHandleCreated(e);
         CalcBounds();
      }

      protected override void OnHandleDestroyed(EventArgs e)
      {
         base.OnHandleDestroyed(e);
         if (minuteFont != null)
         {
            minuteFont.Dispose();
            minuteFont = null;
         }
      }

      private void UpdateLayout()
      {
         CalcBounds();

         if (Changed != null)
            Changed.Invoke(this, EventArgs.Empty);
      }

      protected override void OnPaint(PaintEventArgs e)
      {
         float hY, mY;
         float hX, mX;
         Graphics g = e.Graphics;

         Brush b = new SolidBrush(ForeColor);
         Pen p = new Pen(b);

         SizeF hw = g.MeasureString("24", Font);
         SizeF mw = g.MeasureString("00", minuteFont);

         float textWidth = (float)(hw.Width + mw.Width * 1.4);
         float offset = 0;
         if (textWidth < Width)
            offset = (Width - textWidth) / 2;

         mX = (float)(Width - mw.Width * 1.2 - offset);

         hY = (float)(hw.Height - mw.Height * 0.75);
         mY = (float)(0);

         foreach (Item i in Items)
         {
            String h = String.Format("{0:D2}", i.hour);
            
            hw = g.MeasureString(h, Font);
            hX = (float)(mX - hw.Width + mw.Width * 0.2);

            g.DrawString(h, Font, b, new PointF(hX, hY));
            g.DrawString("00", minuteFont, b, new PointF(mX, mY));
            g.DrawLine(p, 0, i.pos, Width, i.pos);

            hY += step;
            mY += step;
         }
         p.Dispose();
         b.Dispose();
      }

      private void CalcBounds()
      {
         if (minuteFont == null || Handle == null)
            return;

         int numSteps = timeEnd - timeStart + 1;
         Dock = DockStyle.Left;
         using (Graphics g = Graphics.FromHwnd(Handle))
         {
            SizeF hw = g.MeasureString("24", Font);
            SizeF mw = g.MeasureString("00", minuteFont);

            Size minSize = MinimumSize;
            MinimumSize = new Size((int)(hw.Width + mw.Width * 1.4), numSteps * step + step);
         }
      }
   }

   class TimeLineDesigner : ControlDesigner
   {
      public override void InitializeNewComponent(IDictionary defaultValues)
      {
         if (Component.Site == null)
            return;

         base.InitializeNewComponent(defaultValues);
         Control.Size = Control.MinimumSize;
      }

      protected override void PostFilterProperties(System.Collections.IDictionary properties)
      {
         base.PostFilterProperties(properties);

         List<String> allowed = new List<string>(new string[] { "ForeColor", "BackColor", "Font" });

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
}