using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using System.Net;
using System.IO;
using System.Drawing.Drawing2D;
using System.Drawing.Imaging;
using System.Diagnostics;

namespace GRSoft.NapoleonManager
{
   [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public partial class VisitControl : UserControl, DocControl
   {
      private Visit src, dest;
      string baseName = "";

      List<TimeSpan> photoTS = new List<TimeSpan>();

      public VisitControl(Visit visit)
      {
         InitializeComponent();

         this.src = visit;
         dest = src.Clone();
         dest.created = DateTime.Now;
         dest.date = dest.created;

         dest.items.Clear();

         listView1.AllowDrop = true;

         DateTime dt = DateTime.MinValue;
         foreach (Visit.VisitItem vi in visit.items)
         {
            try
            {
               if(baseName == "")
               {
                  int idx = vi.name.LastIndexOf('/');
                  if(idx > 0)
                  {
                     baseName = vi.name.Substring(0, idx + 1);
                  }
               }

               if (dt == DateTime.MinValue)
                  dt = vi.date;
               else
               {
                  TimeSpan ts = (vi.date > dt) ? vi.date - dt : dt - vi.date;
                  photoTS.Add(ts);
                  dt = vi.date;
               }

               MemoryStream ms = new MemoryStream(vi.id);
               Image i = Image.FromStream(ms);
               ms.Close();

               AddImageToList(listView2, i, vi);
            }
            catch (Exception e)
            {

            }
         }
      }

      public Visit Src { get { return src; } }

      // интервал между фото
      public List<TimeSpan> PhotoTS { get { return photoTS; } }
      public GRSoft.Network.DataObject UpdateDoc()
      {
         dest.remark = tbRemark.Text.Trim();

         foreach(ListViewItem lvi in listView1.Items)
         {
            Visit.VisitItem vi = (Visit.VisitItem)lvi.Tag;
            if (vi == null)
               continue;
            dest.items.Add(vi);
         }
         return dest;
      }

      protected virtual void ShowPhoto(Image photo, string tag)
      {
         FmViewPhoto.ShowPhoto(photo, tag);
      }

      List<Image> AskImage()
      {
         List<Image> ret = new List<Image>();

         using (OpenFileDialog openFileDialog = new OpenFileDialog())
         {
            openFileDialog.Filter = "Изображения|*.jpg;*.jpeg";
            openFileDialog.RestoreDirectory = true;
            openFileDialog.Multiselect = true;

            if (openFileDialog.ShowDialog() == DialogResult.OK)
            {
               foreach (String file in openFileDialog.FileNames)
               {
                  ret.Add(Image.FromFile(file));
               }
            }
         }

         return ret;
      }

      void AddImageToList(ListView lv, Image img, Visit.VisitItem item)
      {
         ListViewItem lvi = new ListViewItem();
         lvi.ImageIndex = imageList1.Images.Count;
         lvi.Tag = item;

         imageList1.Images.Add(img);
         lv.Items.Add(lvi);
      }

      byte[] ImageToBytes(Image img)
      {
         using (MemoryStream ms = new MemoryStream())
         {
            img.Save(ms, System.Drawing.Imaging.ImageFormat.Jpeg);
            return ms.ToArray();
         }
      }

      private void btnAddPhoto_Click(object sender, EventArgs e)
      {
         List<Image> list = AskImage();
         foreach(Image img in list)
         {
            Visit.VisitItem vi = new Visit.VisitItem();
            vi.id = ImageToBytes(img);
            vi.date = DateTime.Now;

            Image small = ResizeImage(img);
            vi.smallPhoto = ImageToBytes(small);
            vi.smallSize = String.Format("{0}*{1}", small.Width, small.Height);


            vi.smallName = baseName + Guid.NewGuid().ToString().Replace("-", "") + ".jpeg";
            vi.name = baseName + Guid.NewGuid().ToString().Replace("-", "") + ".jpeg";

            AddImageToList(listView1, img, vi);
         }
      }

      public static Bitmap ResizeImage(Image image)
      {
         double coefH = 220.0 / image.Height;
         double coefW = 220.0 / image.Width;
         double coef = Math.Min(coefH, coefW);

         int width = (int)(image.Width * coef);
         int height = (int)(image.Height * coef);

         var destRect = new Rectangle(0, 0, width, height);
         var destImage = new Bitmap(width, height);

         destImage.SetResolution(image.HorizontalResolution, image.VerticalResolution);

         using (var graphics = Graphics.FromImage(destImage))
         {
            graphics.CompositingMode = CompositingMode.SourceCopy;
            graphics.CompositingQuality = CompositingQuality.HighQuality;
            graphics.InterpolationMode = InterpolationMode.HighQualityBicubic;
            graphics.SmoothingMode = SmoothingMode.HighQuality;
            graphics.PixelOffsetMode = PixelOffsetMode.HighQuality;

            using (var wrapMode = new ImageAttributes())
            {
               wrapMode.SetWrapMode(WrapMode.TileFlipXY);
               graphics.DrawImage(image, destRect, 0, 0, image.Width, image.Height, GraphicsUnit.Pixel, wrapMode);
            }
         }

         return destImage;
      }

      private void listView1_DragDrop(object sender, DragEventArgs e)
      {
         if (e.Data.GetDataPresent(typeof(ListViewItem)))
         {
            ListViewItem nearest = listView1.FindNearestItem(SearchDirectionHint.Right, e.X, e.Y);
            int index = nearest == null ? listView1.Items.Count : listView1.Items.IndexOf(nearest);
            ListViewItem lvi = (ListViewItem)e.Data.GetData(typeof(ListViewItem));
            Visit.VisitItem vi = (Visit.VisitItem)lvi.Tag;

            listView2.Items.Remove(lvi);
            src.items.Remove(vi);
            listView1.Items.Insert(index, lvi);
         }
      }

      private void listView1_DragEnter(object sender, DragEventArgs e)
      {
         if(e.Data.GetDataPresent(typeof(ListViewItem)))
         {
            e.Effect = DragDropEffects.Move;
         }
      }

      private void listView2_ItemDrag(object sender, ItemDragEventArgs e)
      {
         if (listView2.Items.Count > 1)
         {
            ListViewItem lvi = e.Item as ListViewItem;
            if (lvi != null)
            {
               listView2.DoDragDrop(lvi, DragDropEffects.Move);
            }
         }
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         List<int> indexes = new List<int>();
         foreach (int idx in listView1.SelectedIndices)
         {
            indexes.Add(idx);
         }

         indexes.ForEach(x => listView1.Items.RemoveAt(x));
      }
   }
}
