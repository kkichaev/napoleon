using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public static class ProgressImageEx
   {
      public static Image CreateProgressImage(double progress, Size imgSize)
      {
         int hgh = imgSize.Height;
         int wdh = imgSize.Width;
         Bitmap b = new Bitmap(wdh, hgh);

         using (Graphics G = Graphics.FromImage(b))
         {
            Brush back = Brushes.LightGray;
            Brush front = progress == 0 ? Brushes.White: (progress == 1) ? Brushes.Green : Brushes.Red;
            GraphicsUnit gu = GraphicsUnit.Pixel;

            RectangleF bounds = b.GetBounds(ref gu), r;
            r = bounds;
            SizeF s = r.Size;
            s.Width = r.Right;
            r.Size = s;

            G.FillRectangle(front, r);

            r = new RectangleF(r.Right, r.Top, bounds.Width - s.Width, r.Height);
            G.FillRectangle(back, r);

            using (Pen blackPen = new Pen(Color.Black, 1))
            {
               G.DrawRectangle(blackPen, new Rectangle((int)bounds.Left, (int)bounds.Top,
                  (int)bounds.Right - 1, (int)bounds.Height - 1));
            }
         }

         //b.Save("test" + iprogress.ToString() + ".bmp", ImageFormat.Bmp);
         b.Tag = progress;
         return b;
      }

      public static Image CreateProgressImage(double progress, DataGridViewColumn imageColumn)
      {
         Size sz = imageColumn.DefaultCellStyle.Padding.Size;
         int hgh = imageColumn.DataGridView.RowTemplate.Height - sz.Height - 1;
         int wdh = imageColumn.Width - sz.Width - 1;
         return CreateProgressImage(progress, new Size(wdh, hgh));
      }
   }
}
