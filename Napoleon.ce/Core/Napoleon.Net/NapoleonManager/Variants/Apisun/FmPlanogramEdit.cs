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
   public partial class FmPlanogramEdit : Form
   {
      bool clearing = false;
      List<Org> allOrgs = new List<Org>();
      public DataSet<string, PlanogramDef> dsPlanogram;
      public DataSet<string, Org> dsOrg;

      public FmPlanogramEdit()
      {
         InitializeComponent();
         dsPlanogram = new DataSet<string, PlanogramDef>(PlanogramDef.OBJECT_NAME, false);
         dsOrg = (DataSet<string, Org>)DataModule.Get(Org.OBJECT_NAME) ?? new DataSet<string, Org>(Org.OBJECT_NAME);
         dsOrg.Filter = "id not null";
         dsPlanogram.Filter = "id not null";
         dgvOrgs.AutoGenerateColumns = false;

         cbSizes.Items.Add(new TSize(240, 320));
         cbSizes.Items.Add(new TSize(320, 480));
         cbSizes.Items.Add(new TSize(800, 600));
         cbSizes.SelectedIndex = 1;
      }

      private void toolStripButton1_Click(object sender, EventArgs e)
      {
         DialogResult = System.Windows.Forms.DialogResult.OK;
         Close();
      }

      private void dgvOrgs_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         Org org = dgvOrgs.Rows[e.RowIndex].DataBoundItem as Org;

         lvPhotos.Items.Clear();
         imageList1.Images.Clear();

         if (dsPlanogram.ContainsKey(org.id))
         {
            PlanogramDef def = dsPlanogram[org.id];

            foreach (PlanogramDef.PlanogramDefItem pdi in def.items)
            {
               MemoryStream stream = new MemoryStream(pdi.photo);
               Image img = Image.FromStream(stream);

               imageList1.Images.Add(img);
               ListViewItem lvi = lvPhotos.Items.Add(pdi.name, imageList1.Images.Count - 1);
               lvi.Tag = pdi;
            }
         }
      }

      bool AddImage()
      {
         OpenFileDialog dialog = new OpenFileDialog();
         if (dialog.ShowDialog() != DialogResult.OK)
            return false;

         bool imgListAdded = false;

         foreach (DataGridViewRow row in dgvOrgs.SelectedRows)
         {
            Org org = row.DataBoundItem as Org;

            PlanogramDef pd = null;

            if (!dsPlanogram.ContainsKey(org.id))
            {
               dsPlanogram[org.id] = new PlanogramDef();
               dsPlanogram[org.id].id = org.id;
            }

            pd = dsPlanogram[org.id];

            Image img = resizeWithSelectedSize(dialog.FileName, ((TSize)cbSizes.SelectedItem).size);

            PlanogramDef.PlanogramDefItem pdi = new PlanogramDef.PlanogramDefItem();
            pdi.id = PlanogramDef.GenId();
            pdi.name = "Фото " + (pd.items.Count + 1).ToString();

            using (MemoryStream writeStream = new MemoryStream())
            {
               img.Save(writeStream, ImageFormat.Jpeg);
               pdi.photo = writeStream.ToArray();
            }

            pd.items.Add(pdi);

            if (!imgListAdded)
            {
               imageList1.Images.Add(img);
               imgListAdded = true;
               ListViewItem lvi = lvPhotos.Items.Add(pdi.name, imageList1.Images.Count - 1);
               lvi.Tag = pdi;
            }
         }

         btnSave.Enabled = true;
         return true;
      }

      public static Image resizeWithSelectedSize(String path, Size size)
      {
         Image result = null;

         if (path != null)
         {
            Stream stream = null;

            if (path.Length > 0)
               stream = new FileStream(path, FileMode.Open, FileAccess.Read);

            if (stream != null)
            {
               using (stream)
                  result = new Bitmap(stream);

               result = FmPrice.resizeImage(result, size);
            }
         }

         return result;
      }

      bool DelImage()
      {
         if (dgvOrgs.CurrentRow == null)
            return false;

         Org org = dgvOrgs.CurrentRow.DataBoundItem as Org;

         if (org != null && dsPlanogram.ContainsKey(org.id))
         {
            PlanogramDef df = dsPlanogram[org.id];
            foreach (ListViewItem lvi in lvPhotos.SelectedItems)
            {
               df.items.Remove(lvi.Tag as PlanogramDef.PlanogramDefItem);
               lvPhotos.Items.Remove(lvi);
            }
         }

         btnSave.Enabled = true;

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
         PlanogramDef.PlanogramDefItem di = item.Tag as PlanogramDef.PlanogramDefItem;
         di.name = e.Label;

         btnSave.Enabled = true;
      }

      private void timer1_Tick(object sender, EventArgs e)
      {
         timer1.Stop();
         DoSearch(tbFind.Text);
      }

      private void DoSearch(string p)
      {
         p = p.ToUpper();

         List<Org> src = new List<Org>();
         foreach (Org mrd in allOrgs)
         {
            if (mrd.Name.ToUpper().Contains(p) || mrd.Address.ToUpper().Contains(p))
               src.Add(mrd);
         }

         dgvOrgs.DataSource = new SortableBindingList<Org>(src);
      }

      private void btnClearFind_Click(object sender, EventArgs e)
      {
         timer1.Stop();
         clearing = true;
         tbFind.Clear();

         dgvOrgs.DataSource = new SortableBindingList<Org>(allOrgs);

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

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> updSet = new List<IDataSet>();
         updSet.Add(dsOrg);
         updSet.Add(dsPlanogram);

         FmWait.StdDataRefresh(this, updSet, DoLoadData);
      }

      private void DoLoadData()
      {
         allOrgs.Clear();
         allOrgs.AddRange(dsOrg.Values);
         allOrgs.Sort((x, y) => { return x.Name.CompareTo(y.Name); });

         dgvOrgs.DataSource = new SortableBindingList<Org>(allOrgs);
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         List<IDataSet> wrSet = new List<IDataSet>();
         wrSet.Add(dsPlanogram);
         if (DataModule.UpdateDataSet(wrSet, null, null, Config.GetConfig().GetConnection()))
         {
            btnSave.Enabled = false;
            DialogUtil.SavedGood(this);
         }
         else
            DialogUtil.UpdateErrMsg(this);
      }

      private void FmPlanogramEdit_Load(object sender, EventArgs e)
      {
         btnRefresh.PerformClick();
      }

      private void btnCheck_Click(object sender, EventArgs e)
      {
         foreach(DataGridViewRow row in dgvOrgs.Rows)
         {
            row.Selected = true;
         }
      }

      private void btnUncheck_Click(object sender, EventArgs e)
      {
         foreach (DataGridViewRow row in dgvOrgs.Rows)
         {
            row.Selected = false;
         }
      }
   }
}
