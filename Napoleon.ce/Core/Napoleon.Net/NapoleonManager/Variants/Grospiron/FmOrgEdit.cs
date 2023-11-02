using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmOrgEdit : Form
   {
      private Org org = null;

      public FmOrgEdit()
      {
         InitializeComponent();
      }

      private void FmOrgEdit_FormClosed(object sender, FormClosedEventArgs e)
      {
         if (DialogResult == DialogResult.OK)
         {
            org.name = tbName.Text.Trim();
            org.address2 = tbAddress.Text.Trim();
            org.formatTT = cbFormat.Text.Trim();
            org.city = cbCity.Text.Trim();
            org.brand = cbBrand.Text.Trim();
            org.address = org.city + ", " + org.address2;
         }
      }

      internal void SetOrg(Org org, ListItemSource brands, ListItemSource orgFormats, ListItemSource cities)
      {
         this.org = org;

         cbBrand.Items.AddRange(brands.Items);
         cbCity.Items.AddRange(cities.Items);
         cbFormat.Items.AddRange(orgFormats.Items);

         tbName.Text = org.name;
         tbAddress.Text = org.address2;
         cbCity.Text = org.city;
         cbFormat.Text = org.formatTT;
         cbBrand.Text = org.brand;
      }
   }
}
