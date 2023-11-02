using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Drawing.Imaging;
using System.Drawing.Printing;
using System.Reflection;

namespace GRSoft.NapoleonManager
{
   public partial class FmViewPhoto : Form
   {
      Label lbDateTime;
      string comment = string.Empty;

      public FmViewPhoto()
      {
         InitializeComponent();

         lbDateTime = new Label();
         lbDateTime.BackColor = Color.Transparent;
         lbDateTime.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         lbDateTime.AutoSize = true;
         lbDateTime.BackColor = Color.Transparent;
         lbDateTime.ForeColor = System.Drawing.Color.Red;
         lbDateTime.Location = new System.Drawing.Point(340, 300);
         lbDateTime.Font = new System.Drawing.Font("Arial", 15.75F, ((System.Drawing.FontStyle)((System.Drawing.FontStyle.Bold | System.Drawing.FontStyle.Italic))), System.Drawing.GraphicsUnit.Point, ((byte)(204)));

         lbDateTime.BringToFront();

         this.pbPhoto.Controls.Add(lbDateTime);
#if VISIT_CHEATER
         this.toolStrip1.MouseClick += new System.Windows.Forms.MouseEventHandler(this.toolStrip1_MouseClick);
#endif
      }

      public static FmViewPhoto createInstance()
      {
         Type t = FormEntries.GetFormType(typeof(FmViewPhoto));
         ConstructorInfo ci = t.GetConstructor(Type.EmptyTypes);
         FmViewPhoto instance = (FmViewPhoto)ci.Invoke(new object[] { });

         return instance;
      }

      public static void ShowPhoto(Image photo, string label)
      {
         FmViewPhoto fmViewPhoto = createInstance();
         fmViewPhoto.captionPnl.Visible = false;
         fmViewPhoto.btnPrint.Visible = false;

         fmViewPhoto.pbPhoto.Image = photo;
         if (label == null)
            fmViewPhoto.lbDateTime.Hide();
         else
         {
            fmViewPhoto.lbDateTime.Visible = true;
            fmViewPhoto.lbDateTime.Text = label;
            fmViewPhoto.lbDateTime.BringToFront();
         }
         fmViewPhoto.Show();
      }

      public static void ShowPhoto(Image photo, string label, string orgname, string comment)
      {
         FmViewPhoto fmViewPhoto = createInstance();
         fmViewPhoto.captionPnl.Visible = true;
         fmViewPhoto.btnPrint.Visible = true;
         fmViewPhoto.pbPhoto.Image = photo;
         fmViewPhoto.lbOrg.Visible = true;
         fmViewPhoto.lbDate.Visible = true;
         fmViewPhoto.lbDateTime.Hide();
         fmViewPhoto.lbOrg.Text = orgname;
         fmViewPhoto.lbDate.Text = label;
         fmViewPhoto.comment = comment;

         fmViewPhoto.Show();
      }

      protected virtual void DrawImageNotPrint(Graphics g, Bitmap bmp){ }

      private void btnSave_Click(object sender, EventArgs e)
      {
         Bitmap copyImage = null;

         if (!btnPrint.Visible)
         {
            copyImage = new Bitmap(pbPhoto.Image);
            Graphics g = Graphics.FromImage(copyImage);
            SizeF textSz = g.MeasureString(lbDateTime.Text, lbDateTime.Font);
            SolidBrush drawBrush = new SolidBrush(Color.LightGreen);
            PointF drawPoint = new PointF(copyImage.Width - textSz.Width - 5, copyImage.Height - textSz.Height);
            g.DrawString(lbDateTime.Text, lbDateTime.Font, drawBrush, drawPoint);
            DrawImageNotPrint(g, copyImage);
         }
         else
         {
            copyImage = new Bitmap(pbPhoto.Image.Width, pbPhoto.Image.Height);
            DrawImage(Graphics.FromImage(copyImage), new Rectangle(new Point(), copyImage.Size));
         }

         if (saveFileDialog1.ShowDialog() == DialogResult.OK)
         {
            if(copyImage != null)
               copyImage.Save(saveFileDialog1.FileName);
         }
      }

      private void toolStrip1_MouseClick(object sender, MouseEventArgs e)
      {
         System.Console.Out.WriteLine(String.Format("x:{0} y:{1}", e.X, e.Y));

         if (e.X > 70 && e.X < 90)
         {
            new FmSelectDate(this).ShowDialog();
         }
      }

      void SetNewDateTime(DateTime date)
      {
         lbDateTime.Text = date.ToString("dd.MM.yy HH:mm");
         lbDateTime.ForeColor = Color.LightGreen;
      }

      class FmSelectDate : Form
      {
         FmViewPhoto fmViewPhoto;
         DateTimePicker date;
         DateTimePicker time;
         TextBox tbPassw;

         public FmSelectDate(FmViewPhoto fmViewPhoto)
         {
            this.fmViewPhoto = fmViewPhoto;
            Text = "Выберите дату и время";
            Icon = Properties.Resources.napoleon;
            Size = new Size(250, 190);

            date = new DateTimePicker();
            date.Name = "date";
            date.Format = DateTimePickerFormat.Long;
            date.Location = new Point(10, 10);
            date.Value = DateTime.Now;

            time = new DateTimePicker();
            time.Name = "date";
            time.Format = DateTimePickerFormat.Time;
            time.Location = new Point(10, 40);
            time.ShowUpDown = true;
            time.Value = DateTime.Now;

            Button btnOk = new Button();
            btnOk.Name = "btnOK";
            btnOk.Text = "ОК";
            btnOk.Location = new Point(10, 100);
            btnOk.DialogResult = DialogResult.OK;

            Button btnCancel = new Button();
            btnCancel.Text = "Отмена";
            btnCancel.Name = "btnCancel";
            btnCancel.Location = new Point(90, 100);
            btnCancel.DialogResult = DialogResult.Cancel;

            tbPassw = new TextBox();
            tbPassw.Name = "tbPassw";
            tbPassw.Location = new Point(10, 70);
            tbPassw.PasswordChar = '*';

            Controls.Add(date);
            Controls.Add(time);
            Controls.Add(btnOk);
            Controls.Add(btnCancel);
            Controls.Add(tbPassw);

            AcceptButton = btnOk;
            CancelButton = btnCancel;

            FormClosing += new FormClosingEventHandler(FmSelectDate_FormClosing);
         }

         void FmSelectDate_FormClosing(object sender, FormClosingEventArgs e)
         {
            if(DialogResult == DialogResult.OK)
            {
               Config cfg = Config.GetConfig();

               if (cfg.password.Equals(tbPassw.Text))
               {
                  fmViewPhoto.SetNewDateTime(date.Value.Date + time.Value.TimeOfDay);
               }
            }
         }
      }

      private void btnPrint_Click(object sender, EventArgs e)
      {
         System.Drawing.Printing.PrintDocument doc = new System.Drawing.Printing.PrintDocument();
         
         doc.PrintPage += new System.Drawing.Printing.PrintPageEventHandler(PrintPage);
         printDialog1.Document = doc;

         if (printDialog1.ShowDialog() == DialogResult.OK)
         {
            doc.Print();
         }
      }

      private void PrintPage(object o, PrintPageEventArgs e)
      {
         DrawImage(e.Graphics, e.PageBounds);
      }

      private void DrawImage(Graphics graphics, Rectangle rect)
      {
         const int bottomMargin = 90;
         const int PADDING = 20;
         Size sz = new Size(pbPhoto.Image.Width, pbPhoto.Image.Height + bottomMargin);
         Bitmap copyImage = new Bitmap(sz.Width, sz.Height);
         Image drw = BitmapUtil.ScaleImage(pbPhoto.Image, rect.Size);
         
         Rectangle bound = new Rectangle(rect.Size.Width / 2 - drw.Width / 2 + PADDING, 
            rect.Size.Height / 2 - drw.Height / 2, drw.Width - PADDING * 2, drw.Height);
         graphics.DrawImage(drw, bound.Left, bound.Top, bound.Width, bound.Height);

         SolidBrush drawBrush = new SolidBrush(Color.Red);
         Font fnt = new Font("Arial", 14);
         RectangleF rectF1 = new RectangleF(bound.Left, 
            bound.Top + drw.Height, drw.Width, bottomMargin);
         graphics.DrawString(lbDate.Text + "\n" + lbOrg.Text + "\n" + comment, fnt, drawBrush, rectF1);
      }

      private void bntRotRight_Click(object sender, EventArgs e)
      {
         RotateImage(RotateFlipType.Rotate270FlipNone);
      }

      private void toolStripButton2_Click(object sender, EventArgs e)
      {
         RotateImage(RotateFlipType.Rotate90FlipNone);
      }

      private void RotateImage(RotateFlipType rotate)
      {
         Image img = pbPhoto.Image;
         img.RotateFlip(rotate);
         pbPhoto.Image = img;
      }
   }
}