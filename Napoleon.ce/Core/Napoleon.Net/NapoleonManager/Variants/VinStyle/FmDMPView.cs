using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using System.IO;
using GRSoft.Network;
using System.Net;

namespace GRSoft.NapoleonManager
{
   public partial class FmDMPView : UserControl, DataObjectViewer
   {
      public FmDMPView()
      {
         InitializeComponent();
         grid.AutoGenerateColumns = false;
      }

      public void SetData(Network.DataObject dataObject, Dictionary<DateTime, Visit> visits)
      {
         DMP dd = dataObject as DMP;
         List<DMPItemView> items = new List<DMPItemView>();
         Config cfg = Config.GetConfig();

         if (dd != null)
         {
            Visit v = null;
            visits.TryGetValue(dd.created, out v);

            if (v != null)
            {
               DataSet<string, Price> prc = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME);
               DataSet<string, DMPType> types = (DataSet<string, DMPType>)DataModule.Get(DMPType.OBJECT_NAME);

               foreach (Visit.VisitItem i in v.items)
               {
                  DMPItemView dv = new DMPItemView();
                  Price p = null;
                  prc.TryGetValue(i.itemId, out p);
                  dv.Name = p == null ? "" : p.Name;
                  dv.AgendID = dd.userid;
                  dv.DocCreated = dd.created;
                  dv.ID = dd.id;
                  dv.KEY = i.key;
                  dv.DMPID = i.dmpId;
                  dv.PHOTOS = v.items.Count;

                  DMPType d = null;
                  types.TryGetValue(i.dmpId, out d);
                  dv.DMP = p == null ? "" : d.text;

                  byte[] id = loadPic(cfg.HrefBase + i.smallName.Replace("\\", "/"));

                  if (id != null)
                  {
                     dv.Photo = MakePic(id);
                     dv.LargePhotoUrl = cfg.HrefBase + i.name.Replace("\\", "/");
                  }

                  items.Add(dv);
               }
            }
         }

         items.Sort(DmpCompare);

         BindingList<DMPItemView> data = new BindingList<DMPItemView>();

         foreach (DMPItemView i in items)
            data.Add(i);

         for (int i = 0; i < data.Count; i++)
            data[i].Pos = i + 1;

         grid.DataSource = data;
      }

      private byte[] loadPic(string url)
      {
         byte[] result = null;

         try
         {
            WebClient wc = new WebClient();
            result = wc.DownloadData(url);
         }
         catch (Exception)
         {
         }

         return result;
      }

      private int DmpCompare(DMPItemView x, DMPItemView y)
      {
         int res = x.Name.CompareTo(y.Name);

         if (res == 0)
            res = x.DMP.CompareTo(y.DMP);

         return res;
      }

      public void SetData(Network.DataObject dataObject)
      {

      }

      private Image MakePic(byte[] p)
      {
         Image result = null;
         
         using (Stream stream = new MemoryStream(p))
         {
            try
            {
               result = Image.FromStream(stream);
            }
            catch (Exception) { }
         }

         return result;
      }

      private class DMPItemView
      {
         public int Pos { get; set; }
         public string Name { get; set; }
         public string DMP { get; set; }
         public Image Photo { get; set; }
         public string LargePhotoUrl { get; set; }
         public string AgendID { get; set; }
         public DateTime DocCreated { get; set; }
         public string ID { get; set; }
         public string DMPID { get; set; }
         public int PHOTOS { get; set; }
         public string KEY { get; set; }
      }

      private void grid_CellContentClick(object sender, DataGridViewCellEventArgs e)
      {
         if (e.ColumnIndex == 3)
         {
            DMPItemView d = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as DMPItemView;

            if (d != null && d.Photo != null)
            {
               ShowLargePhoto(d);
            }
         }
      }

      private void ShowLargePhoto(DMPItemView d)
      {
         try
         {
            WebClient wc = new WebClient();
            byte[] b = wc.DownloadData(d.LargePhotoUrl);
            MemoryStream ms = new MemoryStream(b);
            Image i = Image.FromStream(ms);
            wc.Dispose();

            FmViewPhoto.ShowPhoto(i, null);
         }
         catch (Exception e)
         {
            MessageBox.Show(e.Message);
         }
      }
   }
}
