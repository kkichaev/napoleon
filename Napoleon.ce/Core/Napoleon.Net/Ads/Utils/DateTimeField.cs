using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using System.Drawing;
using System.ComponentModel;
using System.Reflection;

namespace GRSoft.NapoleonManager.Dispatcher
{
   class DateTimeField : TransparentControl
   {
      Label label = new Label();
      DateTimePicker picker = new DateTimePicker();
      class TitleAttribute : Attribute
      {
         private string title = string.Empty;

         public TitleAttribute(string title)
         {
            this.title = title;
         }

         public string Title { get { return title; } } 
      }
      public enum TMode { [Title("с")]start, [Title("по")]finish }

      private TMode mode = TMode.start;

      public DateTimeField()
      {
         picker.Location = new Point(Width - picker.Width, 0);
         picker.Anchor = AnchorStyles.Top | AnchorStyles.Right | AnchorStyles.Left;
         Controls.Add(picker);
      }

      protected override Size DefaultSize
      {
         get
         {
            const int TEXT_FIELD_SIZE = 20;
            const int PICKER_WITDH = 120;

            picker.Width = PICKER_WITDH;
            return new Size(picker.Width + TEXT_FIELD_SIZE, picker.Height);
         }
      }


      protected override void OnPaint(PaintEventArgs e)
      {
         MemberInfo mi = mode.GetType().GetMember(mode.ToString())[0];
         TitleAttribute attr = (TitleAttribute)mi.GetCustomAttributes(typeof(TitleAttribute), false)[0];
         SolidBrush drawBrush = new SolidBrush(Color.Black);
         e.Graphics.DrawString(attr.Title, Font, drawBrush, new PointF(2, Height / 2 - Font.Height / 2));
         base.OnPaint(e);
      }

      [Browsable(true)]
      public TMode Mode { get { return mode; } set { mode = value; Invalidate(); } }
   }
}
