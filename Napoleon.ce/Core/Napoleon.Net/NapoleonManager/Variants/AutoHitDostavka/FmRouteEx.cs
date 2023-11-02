using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Drawing;
using System.IO;
using System.Net;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public class FmRouteEx : FmRoute
   {
      SimpleDataSet<DShipment> dsDShipment = null;
      SimpleDataSet<DReturn> dsDReturn = null;
      SimpleDataSet<DTask> dsDTask = null;

      WebBrowser wbPhoto = new WebBrowser();
      String assignedHtml = "";
      Config config;

      public FmRouteEx(string idAgent, DateTime date)
         :base(idAgent, date)
      {
         cbOrgRoute.Visible = false;
         btnExcel.Visible = false;
         clmnSum.Visible = false;
         clmnDuration.Visible = false;
         clmnAddress.Visible = false;
         clmnFactAdres.Visible = false;
         btnMessage.Visible = false;
         btnRefresh.Margin = new System.Windows.Forms.Padding(145, 1, 10, 2);
         lblFilter.Margin = new System.Windows.Forms.Padding(275, 1, 10, 2);

         SplitContainer sp = new SplitContainer();
         sp.Orientation = Orientation.Horizontal;

         while (splitContainer1.Panel2.Controls.Count > 0)
         {
            sp.Panel1.Controls.Add(splitContainer1.Panel2.Controls[0]);
         }
         splitContainer1.Panel2.Controls.Add(sp);
         sp.Dock = DockStyle.Fill;
         sp.SplitterDistance = splitContainer1.Height / 2;
         sp.SplitterWidth = 7;

         sp.Panel2.Controls.Add(wbPhoto);
         wbPhoto.Dock = DockStyle.Fill;
         wbPhoto.ObjectForScripting = this;
         wbPhoto.DocumentText = "<html></html>";
         wbPhoto.Visible = true;

         tabControl1.Controls.Remove(tpLog);

         config = Config.GetConfig();


         dsDShipment = (SimpleDataSet<DShipment>)DataModule.Get(DShipment.OBJECT_NAME) ?? new SimpleDataSet<DShipment>(DShipment.OBJECT_NAME, true);
         dsDReturn = (SimpleDataSet<DReturn>)DataModule.Get(DReturn.OBJECT_NAME) ?? new SimpleDataSet<DReturn>(DReturn.OBJECT_NAME, true);
         dsDTask = (SimpleDataSet<DTask>)DataModule.Get(DTask.OBJECT_NAME) ?? new SimpleDataSet<DTask>(DTask.OBJECT_NAME, true);

         documents = new List<DocumentInfo>();

         documents.Add(new DocumentInfo(dsDShipment, ObjType.TObjType.DShipment));
         documents.Add(new DocumentInfo(dsDReturn, ObjType.TObjType.DReturn));
         documents.Add(new DocumentInfo(dsDTask, ObjType.TObjType.Task));

         btnHtml.Margin = new Padding(timeEnd.Right - btnRefresh.Bounds.Right, // - toolStripLabel3.Width - 2 - btnHtml.Width , 
            btnHtml.Margin.Top, btnHtml.Margin.Right, btnHtml.Margin.Bottom);
      }

      protected override void OnRowEnter(VisitQueueItem i)
      {
         base.OnRowEnter(i);
         ShowPhotos(i);
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

#if VISIT_ITEM_DATE
         if (photoCreated.Year > 2010)
         {
            docTag = photoCreated.ToString("dd/MM/yyyy HH:mm") + " " + name.Replace("\"", "");
         }
#endif

         sb.AppendLine("<img ondblclick='window.external.ShowLargePicture(\"" + largHref + "\", \"" + docTag + "\")' src='" + config.HrefBase + smallImg + "' width='" +
            hw[0] + "px' height='" + (hw.Length > 1 ? hw[1] : "165") + "px' />");
#if VISIT_ITEM_DATE
         if(photoCreated.Year > 2010)
         {
            name += " " + photoCreated.ToString("dd/MM/yyyy HH:mm");
         }
#endif
         sb.AppendLine("<p class='nomargine'>" + name + "</div>");

         return true;
      }

      public bool IsSameDate(DateTime d1, DateTime d2)
      {
         return (d1.Year == d2.Year) && (d1.Month == d2.Month) && (d1.Day == d2.Day);
      }

      void ShowPhotos(VisitQueueItem vqi)
      {
         if (vqi.org != null)
         {
            StringBuilder sb = new StringBuilder();
            sb.Append("<html><head>\n<meta charset='utf-8'>\n<style type='text/css'>\ndiv.inline{\n    display:inline;\n   margin-right: 6px}" +
            "p.nomargine{\n    margin-top: 0px;\n    text-align: center;\n}\n</style>\n</head>\n<body>\n");

            int i = 0;
            foreach (Visit vis in dsVisit.Data)
            {
               if (IsSameDate(vis.date, vqi.startTime) && vis.org.id == vqi.org.id)
               {
                  string docDate = vis.created.ToString("dd.MM.yy HH:mm");

                  foreach (Visit.VisitItem vi in vis.items)
                  {
                     DateTime photoCr = DateTime.MinValue;
#if VISIT_ITEM_DATE
                        photoCr = vi.date;
#endif
                     AddPhotoToHtml(sb, (i + 1).ToString(), vi.name, vi.smallName, vi.smallSize, docDate, photoCr);
                     i++;
                  }
               }
            }

            sb.AppendLine("</body></html>");
            string text = sb.ToString();
            if (!text.Equals(assignedHtml))
            {
               // wbPhoto.Stop();
               wbPhoto.DocumentText = text;
               Debug.Print(text);
            }
         } else
            wbPhoto.DocumentText = "<html></html>";
      }

      public void ShowLargePicture(string url, string name)
      {
         try
         {
            WebClient wc = new WebClient();
            byte[] b = wc.DownloadData(url);
            MemoryStream ms = new MemoryStream(b);
            Image i = Image.FromStream(ms);
            //ms.Dispose();
            wc.Dispose();

            FmViewPhoto.ShowPhoto(i, name);
         }
         catch (Exception e)
         {
            MessageBox.Show(e.Message);
         }
      }
   }
}
