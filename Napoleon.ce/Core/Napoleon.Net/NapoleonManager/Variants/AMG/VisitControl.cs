using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using System.Net;
using System.IO;

namespace GRSoft.NapoleonManager
{
   [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public partial class VisitControl : UserControl, DocControl
   {
      private Config config;
      private Visit visit;
      public List<VisitItemDoc> addItems = new List<VisitItemDoc>();

      public VisitControl(Visit visit)
      {
         InitializeComponent();
         config = Config.GetConfig();
         this.visit = visit;

         LoadPhoto();
         addItems.ForEach((i) => listBox1.Items.Add(i.__nameBase));
         webBrowser1.ObjectForScripting = this;
      }

      protected void StartPhotoHTML(StringBuilder sb)
      {
         sb.Append("<html><head>\n<meta charset='utf-8'>\n<style type='text/css'>\ndiv.inline{\n    display:inline;\n   margin-right: 6px}" +
            "p.nomargine{\n    margin-top: 0px;\n    text-align: center;\n}\n</style>\n</head>\n<body>\n");
      }

      public void LoadPhoto()
      {
         StringBuilder sb = new StringBuilder();

         if (visit != null)
         {
            StartPhotoHTML(sb);
            int i = 0;
            string docDate = visit.created.ToString("dd.MM.yy HH:mm");

            foreach (Visit.VisitItem vi in visit.items)
            {
               i++;

               DateTime photoCr = DateTime.MinValue;
               AddPhotoToHtml(sb, (i).ToString(), vi.name, vi.smallName, vi.smallSize, docDate, photoCr);
               checkedListBox1.Items.Add(i, true);
            }

            if (sb.Length > 0)
            {
               sb.AppendLine("</body></html>");
               webBrowser1.Stop();
               webBrowser1.DocumentText = sb.ToString();
            }
            else
               webBrowser1.DocumentText = "<html></html>";
         }

         tbRemark.Text = visit.Remark;
      }

      protected bool AddPhotoToHtml(StringBuilder sb, string name, string img, string smallImg, string smallSize, string docDate, DateTime photoCreated)
      {
         if (smallImg.Length == 0)
            return false;

         string[] hw = smallSize.Split(new char[] { '*' });

         sb.AppendLine("<div class='inline' style='width: " + hw[0] + "px;'>");
         smallImg = smallImg.Replace("\\", "/");
         if (smallImg.StartsWith("/"))
            smallImg = smallImg.Substring(1);

         img = img.Replace("\\", "/");
         if (img.StartsWith("/"))
            img = img.Substring(1);
         string largHref = config.HrefBase + img;
         string docTag = docDate + " " + name.Replace("\"", "");

         sb.AppendLine("<img ondblclick='window.external.ShowLargePicture(\"" + largHref + "\", \"" + docTag + "\")' src='" + config.HrefBase + smallImg + "' width='" +
            hw[0] + "px' height='" + (hw.Length > 1 ? hw[1] : "165") + "px' />");

         sb.AppendLine("<p class='nomargine'>" + name + "</div>");

         return true;
      }

      public GRSoft.Network.DataObject UpdateDoc()
      {
         List<GRSoft.NapoleonManager.Visit.VisitItem> items = new List<GRSoft.NapoleonManager.Visit.VisitItem>();

         foreach (int idx in checkedListBox1.CheckedIndices)
         {
            if (idx < visit.items.Count)
               items.Add(visit.items[idx]);
         }

         visit.items = items;
         visit.remark = tbRemark.Text.Trim();
         visit.created = DateTime.Now;
         visit.date = visit.created;

         return visit;
      }

      public void ShowLargePicture(string url, string name)
      {
         try
         {
            WebClient wc = new WebClient();
            byte[] b = wc.DownloadData(url);
            MemoryStream ms = new MemoryStream(b);
            Image i = Image.FromStream(ms);
            wc.Dispose();

            ShowPhoto(i, name);
         }
         catch (Exception e)
         {
            MessageBox.Show(e.Message);
         }
      }

      protected virtual void ShowPhoto(Image photo, string tag)
      {
         FmViewPhoto.ShowPhoto(photo, tag);
      }

      private void checkedListBox1_MouseClick(object sender, MouseEventArgs e)
      {
         if ((e.Button == MouseButtons.Left) & (e.X > 13))
         {
            this.checkedListBox1.SetItemChecked(
               this.checkedListBox1.SelectedIndex, !this.checkedListBox1.GetItemChecked(this.checkedListBox1.SelectedIndex));
         }
      }

      private void btnAddPhoto_Click(object sender, EventArgs e)
      {
         var filePath = string.Empty;

         using (OpenFileDialog openFileDialog = new OpenFileDialog())
         {
            openFileDialog.InitialDirectory = "c:\\";
            openFileDialog.Filter = "Изображения|*.jpg;*.jpeg;*.png";
            openFileDialog.RestoreDirectory = true;

            if (openFileDialog.ShowDialog() == DialogResult.OK)
            {
               filePath = openFileDialog.FileName;

               Image img = Image.FromFile(filePath);
               byte[] arr;

               using (MemoryStream ms = new MemoryStream())
               {
                  img.Save(ms, System.Drawing.Imaging.ImageFormat.Jpeg);
                  arr = ms.ToArray();

                  VisitItemDoc item = new VisitItemDoc();
                  item.id = arr;
                  item.__nameBase = Path.GetFileNameWithoutExtension(filePath) + "\\" + visit.created.ToString("yyyyMMddHHmmss") + "_" + (addItems.Count + 1);
                  item.__date = visit.created;
                  addItems.Add(item);

                  listBox1.Items.Add(item.__nameBase);
               }
            }
         }
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         if (listBox1.SelectedIndex != -1 && listBox1.SelectedIndex < addItems.Count)
         {
            addItems.RemoveAt(listBox1.SelectedIndex);
            listBox1.Items.RemoveAt(listBox1.SelectedIndex);
         }
      }
   }
}
