using System;
using System.Collections.Generic;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.NapoleonManager;

namespace GRSoft.NapoleonManager
{
   public class FmViewPhotoEx : FmViewPhoto
   {
      private static readonly string ACTTILTE = "Акция золотая полка";
      Label label;
      public FmViewPhotoEx()
      {
         Load += FmViewPhotoEx_Load;
      }

      void FmViewPhotoEx_Load(object sender, EventArgs e)
      {
         VisitTag vt = pbPhoto.Image.Tag as VisitTag;
         if (vt != null && vt.visit != null && vt.visit.actgs > 0)
         {
            label = new Label();
            label.BackColor = Color.Transparent;
            label.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
            label.AutoSize = true;
            label.ForeColor = System.Drawing.Color.Red;
            label.Location = new System.Drawing.Point(0, 0);
            label.Font = new System.Drawing.Font("Arial", 15.75F, ((System.Drawing.FontStyle)((System.Drawing.FontStyle.Bold | System.Drawing.FontStyle.Italic))), System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            label.Text = ACTTILTE;
            this.pbPhoto.Controls.Add(label);
         }
      }

      protected override void DrawImageNotPrint(Graphics g, Bitmap bmp)
      {
         if (label != null)
         {
            SizeF textSz = g.MeasureString(ACTTILTE, label.Font);
            SolidBrush drawBrush = new SolidBrush(Color.LightGreen);
            PointF drawPoint = new PointF(0, 0);
            g.DrawString(ACTTILTE, label.Font, drawBrush, drawPoint);
         }
      }

   }
}
