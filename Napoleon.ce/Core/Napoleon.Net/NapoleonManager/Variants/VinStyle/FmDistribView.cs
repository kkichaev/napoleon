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
   public partial class FmDistribView : UserControl, DataObjectViewer
   {
      public FmDistribView()
      {
         InitializeComponent();
         grid.AutoGenerateColumns = false;
      }

      public void SetData(Network.DataObject dataObject, Dictionary<DateTime, Visit> visits)
      {
         Distrib dd = dataObject as Distrib;
         Dictionary<string, DistribItemView> items = new Dictionary<string, DistribItemView>();
         Config cfg = Config.GetConfig();

         if (dd != null)
         {
            Visit v = null;
            visits.TryGetValue(dd.created, out v);

            foreach (Distrib.DistribRemark i in dd.items)
            {
               if (!items.ContainsKey(i.id))
               {
                  items[i.id] = new DistribItemView();
                  items[i.id].Name = i.item.Name;

               }

               items[i.id].Remark = i.remark;
            }

            if (v != null)
            {
               DataSet<string, Price> prc = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME);
               foreach (Visit.VisitItem i in v.items)
               {
                  if (!items.ContainsKey(i.itemId))
                  {
                     DistribItemView dv = new DistribItemView();
                     items[i.itemId] = dv;
                     Price p = null;
                     prc.TryGetValue(i.itemId, out p);
                     dv.Name = p == null ? "" : p.Name;

                     dv.AgendID = dd.userid;
                     dv.DocCreated = dd.created;
                     dv.ID = dd.id;
                     dv.KEY = i.key;
                     dv.DMPID = i.itemId;
                     dv.PHOTOS = v.items.Count;
                  }

                  byte[] id = loadPic(cfg.HrefBase + i.smallName.Replace("\\", "/"));

                  if (id != null)
                  {
                     items[i.itemId].Photo = MakePic(id);
                     items[i.itemId].LargePhotoUrl = cfg.HrefBase + i.name.Replace("\\", "/");
                  }
               }
            }
         }

         List<DistribItemView> list = new List<DistribItemView>(items.Values);
         list.Sort((x, y) => { return x.Name.CompareTo(y.Name); });

         BindingList<DistribItemView> data = new BindingList<DistribItemView>();

         foreach (DistribItemView i in list)
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

      private class DistribItemView
      {
         public int Pos { get; set; }
         public string Name { get; set; }
         public string Remark { get; set; }
         public Image Photo { get; set; }

         public String LargePhotoUrl { get; set; }
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
            DistribItemView d = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as DistribItemView;

            if (d != null && d.Photo != null)
            {
               ShowLargePhoto(d);
               SetCheck(d);
            }
         }
      }

      private void SetCheck(DistribItemView d)
      {
         List<IDataSet> update = new List<IDataSet>();

         DataSet<int, DistrCheck> ds = new DataSet<int, DistrCheck>(DistrCheck.OBJECT_NAME, false);
         ds.Add(ds.Count, CreateCheck(d));
         update.Add(ds);

         DataModule.UpdateDataSet(update, null, null, Config.GetConfig().GetConnection());
      }

      private object CreateCheck(DistribItemView v)
      {
         DistrCheck d = new DistrCheck();
         d.agentid = v.AgendID;
         d.doccreated = v.DocCreated;
         d.created = DateTime.Now;
         d.id = v.ID;
         d.dmpid = v.DMPID;
         d.photos = v.PHOTOS;
         d.key = v.KEY;

         return d;
      }

      private void ShowLargePhoto(DistribItemView d)
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

      private void grid_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         DistribItemView d = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as DistribItemView;

         if ( d != null && (d.Remark == null  || d.Remark.Trim().Length == 0) && d.Photo == null)
            e.CellStyle.BackColor = Color.Gray;
      }
   }
}
