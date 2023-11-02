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
   [Designer(typeof(TimeLineDesigner))]
   public class TimeLine : Control
   {
      const int MIN_SZ = 15;
      int start = 0;
      int finish = 24;
      
      int step = MIN_SZ;

      Font minuteFont;

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

      public event EventHandler Changed;

      [Browsable(true)]
      public int Start
      {
         get { return start; }
         set
         {
            //if (start != value)
            //{
            //   TimeSpan min = new TimeSpan(0, 0, 0);

            //   if (value < min)
            //      value = min;

            //   start = value;
            //   UpdateLayout();
            //}
         }
      }

      [Browsable(true)]
      public int Finish
      {
         get { return finish; }
         set
         {
            //if (finish != value)
            //{
            //   TimeSpan max = new TimeSpan(23,0,0);

            //   if (value > max)
            //      value = max;
            //   finish = value;
            //   CalcStep();
            //   UpdateLayout();
            //}
         }
      }

      public int GetPosition(TimeSpan time)
      {
         int hour = time.Hours;
         if (hour < start )
            return -1;
         if (hour > finish)
            return -2;

         int pos = (int)((hour - start) * step + step / 60.0 * time.Minutes);
         return pos;
      }

      public int GetPosition(int h)
      {
         int hour = h;
         if (hour < start)
            return -1;
         if (hour > finish)
            return -2;

         int pos = (int)((hour - start) * step + step);
         return pos;
      }

      public int GetHour(int y)
      {
         int result = -1;
         int sz = Math.Abs(start - finish);
         for (int i = 0; i <= sz; i++)
         {
            int pos = i * step;

            if (pos <= y && pos + step > y)
            {
               result = i + start;
               break;
            }
         }

         return result;
      }

      protected override void OnHandleCreated(EventArgs e)
      {
         base.OnHandleCreated(e);
         CalcBounds();
         CalcStep();
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

      protected override void OnResize(EventArgs e)
      {
         CalcStep();
      }

      private void CalcStep()
      {
         int sz = Math.Abs(start - finish) + 1;
         if (sz > 0)
         {
            int new_step = Height / sz;

            if (new_step > MIN_SZ)
               step = new_step;
         }
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

         int sz = Math.Abs(start - finish) + 1;
         for (int i = 0; i <= sz; i++)
         {
            int pos = i * step;
            String h = String.Format("{0:D2}", start + i);
            
            hX = (float)(mX - hw.Width + mw.Width * 0.2);

            float hfh = hw.Height / 2;
            g.DrawString(h, Font, b, new PointF(hX, hY + hfh));
            g.DrawString("00", minuteFont, b, new PointF(mX, mY + hfh));
            g.DrawLine(p, 0, pos, Width, pos);

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

         int numSteps = finish - start;
         Dock = DockStyle.Left;
         using (Graphics g = Graphics.FromHwnd(Handle))
         {
            SizeF hw = g.MeasureString("24", Font);
            SizeF mw = g.MeasureString("00", minuteFont);

            //Size minSize = MinimumSize;
            //MinimumSize = new Size((int)(hw.Width + mw.Width * 1.4), numSteps * MIN_SZ + MIN_SZ);
         }
      }

      public int Step { get {return step;} }
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