using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms.Design;
using System.Drawing;
using System.Drawing.Design;
using System.Runtime.InteropServices;
using System.Drawing.Imaging;
using System.IO;

namespace NFormEditor
{
   [Designer(typeof(PictureDesigner))]
   class Picture : Control, IScalable
   {
      private Scaler scaler;
      String bitmap;
      Image src;

      public Picture()
      {
         scaler = new Scaler(this);
      }

      public override ISite Site
      {
         get { return base.Site; }
         set
         {
            if (base.Site != null)
               scaler.UnSited();

            base.Site = value;

            if (value != null)
               scaler.Sited();
         }
      }

      [Category("Свойства")]
      [Editor(typeof(PictureEditor), typeof(System.Drawing.Design.UITypeEditor))]
      public string Image
      { 
         get { return bitmap; }
         set
         {
            bitmap = Path.GetFileName(value);
            src = null;
            Invalidate();
         }
      }

      //public string Src
      //{
      //   get { return ImageToString(); }
      //   set { }
      //}

      public Bitmap GetPicture()
      {
         if (bitmap == null)
            return null;

         String fileName = bitmap;
         if (File.Exists(fileName) == false)
         {
            if (!Path.IsPathRooted(fileName))
            {
               if (FormDeisgner.instance != null)
               {
                  string formName = FormDeisgner.instance.FormName;
                  string path = Path.GetDirectoryName(formName);
                  if (path != null && path != String.Empty)
                     fileName = path + "\\" + fileName;
               }

            }
         }
         if( src == null )
            src = System.Drawing.Image.FromFile(fileName);
         return new Bitmap(src, ClientRectangle.Size);
      }

      public string ImageToString()
      {
         string ret = "";
         Bitmap bmp = GetPicture();
         if (bmp != null)
         {
            StringBuilder sb = new StringBuilder();

            BitmapData data = bmp.LockBits(new Rectangle(0, 0, bmp.Width, bmp.Height), ImageLockMode.ReadOnly, PixelFormat.Format8bppIndexed);
            IntPtr ptr = data.Scan0;
            int bytes = Math.Abs(data.Stride) * bmp.Height;
            byte[] rgbValues = new byte[bytes];
            System.Runtime.InteropServices.Marshal.Copy(ptr, rgbValues, 0, bytes);
            for (int i = 0; i < bytes; i++)
               sb.AppendFormat("{0:X}", rgbValues[i]);

            ret = sb.ToString();
            bmp.UnlockBits(data);
         }
         return ret;
      }

      protected override void OnPaint(PaintEventArgs e)
      {
         base.OnPaint(e);
         
         Image i = GetPicture();
         if (i == null)
            e.Graphics.FillRectangle(Brushes.Transparent, ClientRectangle);
         else
            e.Graphics.DrawImage(i, new Point(0,0));
      }

      #region IScalable Members

      public void Scaling(float factor) { scaler.DoScaling(factor); }
      public System.Drawing.Size RealSize { get { return scaler.Size; } }
      public System.Drawing.Point RealLocation { get { return scaler.Location; } }

      #endregion
   }

   class PictureEditor : FileNameEditor
   {
      protected override void InitializeDialog(OpenFileDialog openFileDialog)
      {
         base.InitializeDialog(openFileDialog);
         
         string formName = FormDeisgner.instance.FormName;
         openFileDialog.InitialDirectory = Path.GetDirectoryName(formName);

         openFileDialog.Filter = "Image Files |*.jpeg;*.png;*.jpg;*.gif;*.bmp";
      }
   }

   class PictureDesigner : ControlDesigner
   {
      protected override void PreFilterProperties(System.Collections.IDictionary properties)
      {
         base.PreFilterProperties(properties);

         DictionaryEntry[] entiries = new DictionaryEntry[properties.Count];
         properties.CopyTo(entiries, 0);

         foreach (DictionaryEntry de in entiries)
         {
            PropertyDescriptor pd = de.Value as PropertyDescriptor;
            if (pd.IsBrowsable == false || pd.IsReadOnly) continue;

            if (pd.Category != "Свойства" && pd.Name != "Location" && pd.Name != "Size" && pd.Name != "Image" && pd.Name != "Src")
               properties.Remove(de.Key);
         }
      }
   }
}
