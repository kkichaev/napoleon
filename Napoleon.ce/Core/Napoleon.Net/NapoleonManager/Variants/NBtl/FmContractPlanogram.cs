using GRSoft.NapoleonManager.Utils;
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
   public partial class FmContractPlanogram : Form
   {
      public TSize imgSize;
      bool clearing = false;
      List<OrgData> allOrgs = new List<OrgData>();

      public FmContractPlanogram()
      {
         InitializeComponent();

         dgvOrgs.AutoGenerateColumns = false;
         dgvItems.AutoGenerateColumns = false;
      }

      private void toolStripButton1_Click(object sender, EventArgs e)
      {
         DialogResult = System.Windows.Forms.DialogResult.OK;
         Close();
      }

      private void dgvOrgs_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         OrgData od = dgvOrgs.Rows[e.RowIndex].DataBoundItem as OrgData;

         lvPhotos.Items.Clear();
         imageList1.Images.Clear();

         //od.PutToListView(lvPhotos, imageList1);
         dgvItems.DataSource = od.Photos;
      }

      public List<ContractOrgImg> OrgImg
      {
         set
         {
            allOrgs.Clear();

            DataSet<string, Org> orgs = (DataSet<string, Org>)DataModule.Get(Org.OBJECT_NAME);
            if( orgs != null )
            {
               Dictionary<Org, OrgData> tsrc = new Dictionary<Org, OrgData>();
               foreach (Org o in orgs.Data)
                  tsrc[o] = new OrgData(o);

               foreach(ContractOrgImg coi in value)
               {
                  if( orgs.ContainsKey(coi.id) )
                  {
                     Org o = orgs[coi.id];
                     if (tsrc.ContainsKey(o))
                        tsrc[o].Add(coi);
                  }
               }

               allOrgs.AddRange(tsrc.Values);
               allOrgs.Sort();
            }

            SortableBindingList<OrgData> dsSrc = new SortableBindingList<OrgData>(allOrgs);
            dgvOrgs.DataSource = dsSrc;

            lvPhotos.Items.Clear();
            imageList1.Images.Clear();
         }
         
         get
         {
            List<ContractOrgImg> ret = new List<ContractOrgImg>();
            
            foreach (OrgData od in allOrgs)
               od.PutToList(ret);

            return ret;
         }
      }

      class OrgData : IComparable<OrgData>
      {
         Org org;
         BindingList<ContractOrgImg> photos = new BindingList<ContractOrgImg>();

         public OrgData(Org o)
         {
            this.org = o;
         }

         public void Add(ContractOrgImg c) { photos.Add(c); }


         public string Name { get { return org.Name; } }
         public string Address { get { return org.Address; } }

         public string ID { get { return org.id; } }

         public int CompareTo(OrgData other)
         {
            return org.CompareTo(other.org);
         }

         public int Count { get { return photos.Count; } }

         internal void PutToListView(ListView lvPhotos, ImageList imageList1)
         {
            foreach(ContractOrgImg coi in photos)
            {
               MemoryStream stream = new MemoryStream(coi.photo);
               Image img = Image.FromStream(stream);

               imageList1.Images.Add(img);
               ListViewItem lvi = lvPhotos.Items.Add(coi.name, imageList1.Images.Count - 1);
               lvi.Tag = coi;
            }
         }

         public BindingList<ContractOrgImg> Photos { get { return photos; } }

         internal void PutToList(List<ContractOrgImg> ret)
         {
            foreach (ContractOrgImg coi in photos)
            {
               if (coi.href.Trim().Length > 0)
               {
                  coi.id = org.id;
                  ret.Add(coi);
               }
            }
         }

         internal void Remove(ContractOrgImg item)
         {
            photos.Remove(item);
         }
      }

      bool AddImage()
      {
         if (dgvOrgs.CurrentRow == null || imgSize == null)
            return false;

         OrgData od = dgvOrgs.CurrentRow.DataBoundItem as OrgData;

         OpenFileDialog dialog = new OpenFileDialog();
         if (dialog.ShowDialog() != DialogResult.OK)
            return false;

         new Thread(new ParameterizedThreadStart(delegate(object obj)
         {
            BeginInvoke(new EmptyParamHandler(delegate() { FmWait.ShowForm(this, true); }));

            Image img = FmContractEdit.resizeWithSelectedSize(dialog.FileName, (Size)obj);

            BeginInvoke(new EmptyParamHandler(delegate()
            {
               ContractOrgImg coi = new ContractOrgImg();
               coi.id = od.ID;
               coi.name = "Фото " + (od.Count + 1).ToString();

               using (MemoryStream writeStream = new MemoryStream())
               {
                  img.Save(writeStream, ImageFormat.Jpeg);
                  coi.photo = writeStream.ToArray();
               }

               od.Add(coi);
               imageList1.Images.Add(img);

               ListViewItem lvi = lvPhotos.Items.Add(coi.name, imageList1.Images.Count - 1);
               lvi.Tag = coi;

               FmWait.CloseForm();
            }));
         }
         )).Start(imgSize.size);

         return true;
      }

      bool DelImage()
      {
         if (dgvOrgs.CurrentRow == null)
            return false;

         OrgData od = dgvOrgs.CurrentRow.DataBoundItem as OrgData;

         foreach(ListViewItem lvi in lvPhotos.SelectedItems)
         {
            od.Remove(lvi.Tag as ContractOrgImg);
            lvPhotos.Items.Remove(lvi);
         }

         return true;
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         AddImage();
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         DelImage();
      }

      private void lvPhotos_AfterLabelEdit(object sender, LabelEditEventArgs e)
      {
         ListViewItem item = lvPhotos.Items[e.Item];
         ContractOrgImg coi = item.Tag as ContractOrgImg;
         coi.name = e.Label;
      }

      private void timer1_Tick(object sender, EventArgs e)
      {
         timer1.Stop();
         DoSearch(tbFind.Text);
      }

      private void DoSearch(string p)
      {
         p = p.ToUpper();

         List<OrgData> src = new List<OrgData>();
         foreach (OrgData mrd in allOrgs)
         {
            if (mrd.Name.ToUpper().Contains(p) || mrd.Address.ToUpper().Contains(p))
               src.Add(mrd);
         }

         dgvOrgs.DataSource = new SortableBindingList<OrgData>(src);
      }

      private void btnClearFind_Click(object sender, EventArgs e)
      {
         timer1.Stop();
         clearing = true;
         tbFind.Clear();

         dgvOrgs.DataSource = new SortableBindingList<OrgData>(allOrgs);

         clearing = false;

      }

      private void tbFind_TextChanged(object sender, EventArgs e)
      {
         timer1.Stop();

         if (tbFind.Text.Length > 0)
            timer1.Start();
         else if (!clearing)
            btnClearFind_Click(sender, e);
      }
   }
}
