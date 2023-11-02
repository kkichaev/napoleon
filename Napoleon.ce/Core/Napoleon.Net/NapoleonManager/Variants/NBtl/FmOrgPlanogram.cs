using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmOrgPlanogram : Form
   {
      public FmOrgPlanogram()
      {
         InitializeComponent();
      }

      void PutData(ContractDef c, Org o)
      {
         Text += " " + c.Name;

         foreach(ContractOrgImg coi in c.orgImg)
         {
            if(coi.id == o.id)
            {
               using (MemoryStream ms = new MemoryStream(coi.photo))
               {
                  Image i = Image.FromStream(ms);
                  imageList1.Images.Add(i);
                  lvPhotos.Items.Add(coi.name, imageList1.Images.Count - 1).Tag = i;
               }
            }
         }

         if( lvPhotos.Items.Count == 0)
         {
            imageList1.Images.Add(c.Photo);
            lvPhotos.Items.Add("Базовая выкладка", imageList1.Images.Count - 1);
         }
      }

      public static void ShowPlanogram(ContractDef c, Org o)
      {
         FmOrgPlanogram form = new FmOrgPlanogram();
         form.PutData(c, o);
         form.Show();
      }

      private void lvPhotos_DoubleClick(object sender, EventArgs e)
      {
         if (lvPhotos.SelectedItems.Count == 0)
            return;

         ListViewItem lvi = lvPhotos.SelectedItems[0];
         Image photo = lvi.Tag as Image;
         FmViewPhoto.ShowPhoto(photo, lvi.Text);
      }
   }
}
