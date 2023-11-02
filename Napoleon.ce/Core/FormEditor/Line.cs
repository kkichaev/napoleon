using System.Windows.Forms;
using System.Windows.Forms.Design;
using System.Drawing;
using System.ComponentModel;
using System.Windows.Forms.Design.Behavior;
using System.Collections;
using System.ComponentModel.Design;

namespace NFormEditor
{
   [Designer(typeof(LineDesigner))]
   class Line : Control, IScalable
   {
      protected bool horiz = true;
      private string text;

      private Scaler scaler;

      public Line()
      {
         scaler = new Scaler(this);

         SetStyle(ControlStyles.SupportsTransparentBackColor, true);
         BackColor = Color.Transparent;
      }

      protected override void OnPaint(PaintEventArgs e)
      {
         base.OnPaint(e);

         Pen p = new Pen(SystemColors.WindowText);
         if (horiz)
         {
            e.Graphics.DrawLine(p, 0, 0, Width, 0);
            if (text != null && text.Length > 0)
            {
               RectangleF bounds = new RectangleF(0, 0, Width, Height);
               StringFormat sf = StringFormat.GenericDefault;
               sf.Alignment = StringAlignment.Center;
               sf.LineAlignment = StringAlignment.Near;

               using( Font font = new Font("Microsoft Sans Serif", 11.0F * Program.ScaleFactor) )
                  e.Graphics.DrawString(text, font, SystemBrushes.WindowText, bounds, sf);
            }
         }
         else
            e.Graphics.DrawLine(p, 0, 0, 0, Height);
      }

      [Category("Свойства")]
      public string LineText
      {
         get { return text; }
         set { text = value; Invalidate(); Update(); }
      }

      [Category("Свойства")]
      public bool Horizontal
      {
         get { return horiz; }
         set
         {
            if (horiz != value)
            {
               horiz = value;
               Invalidate();
            }
         }
      }

      public override ISite Site
      {
         get
         {
            return base.Site;
         }
         set
         {
            if (base.Site != null)
               scaler.UnSited();

            base.Site = value;

            if (value != null)
               scaler.Sited();
         }
      }

      #region IScalable Members

      public void Scaling(float factor)
      {
         scaler.DoScaling(factor);
      }

      public Size RealSize
      {
         get { return scaler.Size; }
      }

      public Point RealLocation
      {
         get { return scaler.Location; }
      }

      #endregion
   }

   class LineDesigner : ControlDesigner
   {
      public override System.Collections.IList SnapLines
      {
         get
         {
            Line line = Component as Line;
            bool horiz = line.Horizontal;
            if (horiz)
            {
               return new SnapLine[] { 
                  new SnapLine(SnapLineType.Top, 0),
                  new SnapLine(SnapLineType.Bottom, 0),
                  new SnapLine(SnapLineType.Right, 0),
                  new SnapLine(SnapLineType.Right, line.Width),
                  new SnapLine(SnapLineType.Left, 0),
                  new SnapLine(SnapLineType.Left, line.Width),
               };
            }
            else
            {
               return new SnapLine[] { 
                  new SnapLine(SnapLineType.Left, 0),
                  new SnapLine(SnapLineType.Right, 0),
                  new SnapLine(SnapLineType.Top, 0),
                  new SnapLine(SnapLineType.Top, line.Height),
                  new SnapLine(SnapLineType.Bottom, 0),
                  new SnapLine(SnapLineType.Bottom, line.Height),
               };
            }
         }
      }

      protected override void OnCreateHandle()
      {
         base.OnCreateHandle();
         (Component as Line).SendToBack();
      }

      protected override void PreFilterProperties(System.Collections.IDictionary properties)
      {
         base.PreFilterProperties(properties);

         DictionaryEntry[] entiries = new DictionaryEntry[properties.Count];
         properties.CopyTo(entiries, 0);

         foreach (DictionaryEntry de in entiries)
         {
            PropertyDescriptor pd = de.Value as PropertyDescriptor;
            if (pd.IsBrowsable == false || pd.IsReadOnly) continue;

            if (pd.Category != "Свойства" && pd.Name != "Location" && pd.Name != "Size" )
               properties.Remove(de.Key);
         }
      }
      //protected override void OnPaintAdornments(PaintEventArgs e)
      //{
      //   base.OnPaintAdornments(e);

      //   Pen p = new Pen(SystemColors.WindowText);

      //   Control ctrl = Component as Control;
      //   e.Graphics.DrawLine(p, ctrl.Left, ctrl.Top, ctrl.Right, ctrl.Bottom);
      //} 
   }
}