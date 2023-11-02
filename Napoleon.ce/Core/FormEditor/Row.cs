using System.Windows.Forms;
using System.Windows.Forms.Design;
using System.Drawing;
using System.ComponentModel;

namespace NFormEditor
{
   class Row : ContainerControl
   {
      private string obj;

      public string Object
      {
         get { return obj; }
         set { obj = value; }
      }

      protected override void OnPaint(PaintEventArgs e)
      {
         base.OnPaint(e);

         Brush b = new SolidBrush(SystemColors.GrayText);
         e.Graphics.FillRectangle(b, 0, 0, Width, Height);
      }
   }
}
