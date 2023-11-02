using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Drawing.Imaging;
using System.IO;
using System.Text;
using System.Threading;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmContractPhotos : Form
   {
      private DataSet<string, Org> dsOrg;
      public TSize imgSize;
      Dictionary<string, ListViewItem> orgMap = new Dictionary<string, ListViewItem>();
      Dictionary<string, Image> origImgMap = new Dictionary<string, Image>();

      public FmContractPhotos()
      {
         InitializeComponent();
         dsOrg = (DataSet<string, Org>)DataModule.Get(Org.OBJECT_NAME);
         
         if (dsOrg != null)
            DoLoadData();
      }

      private void DoLoadData()
      {
         List<Org> list = new List<Org>();
         list.AddRange(dsOrg.Values);
         list.Sort((lhs, rhs) => { return lhs.Name.CompareTo(rhs.Name); });

         foreach (Org o in list)
         {
            ListViewItem item = new ListViewItem();
            item.Text = o.Name;
            item.Tag = o;
            listView.Items.Add(item);

            orgMap[o.id] = item;
         }
      }

      private void listView_DoubleClick(object sender, EventArgs e)
      {
         if (imgSize != null && ((ListView)sender).SelectedItems.Count == 1)
         {
            ListViewItem item = ((ListView)sender).SelectedItems[0];

            OpenFileDialog dialog = new OpenFileDialog();

            if (dialog.ShowDialog() == DialogResult.OK)
            {
               new Thread(new ParameterizedThreadStart(delegate(object obj)
               {
                  BeginInvoke(new EmptyParamHandler(delegate() { FmWait.ShowForm(this, true); }));

                  string fileName = dialog.FileName;
                  Image img = FmContractEdit.resizeWithSelectedSize(fileName, (Size)obj);

                  BeginInvoke(new EmptyParamHandler(delegate()
                  {
                     imageList.Images.Add(img);
                     item.ImageIndex = imageList.Images.Count - 1;
                     listView.Refresh();
                     FmWait.CloseForm();

                     Org o = (Org)item.Tag;

                     if (o != null)
                        origImgMap[o.id] = img;
                  }));
               }
               )).Start(imgSize.size);
            }
         }
      }

      public List<ContractOrgImg> OrgImg 
      { 
         get 
         {
            List<ContractOrgImg> result = new List<ContractOrgImg>();

            foreach(KeyValuePair<string, Image> pair in origImgMap)
            {
               ContractOrgImg orgImg = new ContractOrgImg();
               orgImg.id = pair.Key;

               using (MemoryStream writeStream = new MemoryStream())
               {
                  pair.Value.Save(writeStream, ImageFormat.Jpeg);
                  orgImg.photo = writeStream.ToArray();
               }

               result.Add(orgImg);
            }

            return result;
         } 
         set 
         {
            imageList.Images.Clear();

            foreach (ContractOrgImg coi in value)
            {
               MemoryStream stream = new MemoryStream(coi.photo);
               Image img = Image.FromStream(stream);
               imageList.Images.Add(img);

               orgMap[coi.id].ImageIndex = imageList.Images.Count - 1;
               origImgMap[coi.id] = img;
            }
         } 
      }
   }
}
