using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using System.Drawing;
using System.Collections;

namespace GRSoft.NapoleonManager
{
   class FmPtnzlOrgEditEx : FmPtnzlOrgEdit
   {
      private ComboBox cbRegion = new ComboBox();
      private ComboBox cbRegionR1 = new ComboBox();
      private ComboBox cbRegionR2 = new ComboBox();
      public FmCensusEx census;

      public FmPtnzlOrgEditEx(FmCensusEx census)
      {
         this.census = census;

         Size = new Size(392, 350);
         
         Label label3 = new Label();
         Label label4 = new Label();
         Label label5 = new Label();
         
         // 
         // label3
         // 
         label3.AutoSize = true;
         label3.Location = new System.Drawing.Point(12, 200);
         label3.Name = "label3";
         label3.Size = new System.Drawing.Size(39, 14);
         label3.TabIndex = 5;
         label3.Text = "Населенный пункт";

         // cbRegion
         // 
         cbRegion.FormattingEnabled = true;
         cbRegion.Location = new System.Drawing.Point(12, 220);
         cbRegion.Name = "cbRegion";
         cbRegion.Size = new System.Drawing.Size(360, 20);
         cbRegion.TabIndex = 3;
         cbRegion.SelectionChangeCommitted += new System.EventHandler(cbRegion_SelectedIndexChanged);

         // 
         // label4
         // 
         label4.AutoSize = true;
         label4.Location = new System.Drawing.Point(12, 150);
         label4.Name = "label4";
         label4.Size = new System.Drawing.Size(39, 14);
         label4.TabIndex = 5;
         label4.Text = "Район *";

         // cbRegionR2
         // 
         cbRegionR1.FormattingEnabled = true;
         cbRegionR1.Location = new System.Drawing.Point(12, 170);
         cbRegionR1.Name = "cbRegionR2";
         cbRegionR1.Size = new System.Drawing.Size(360, 20);
         cbRegionR1.TabIndex = 3;
         cbRegionR1.SelectionChangeCommitted += new System.EventHandler(cbRegionR1_SelectedIndexChanged);

         // 
         // label5
         // 
         label5.AutoSize = true;
         label5.Location = new System.Drawing.Point(12, 100);
         label5.Name = "label5";
         label5.Size = new System.Drawing.Size(39, 14);
         label5.TabIndex = 5;
         label5.Text = "Область *";

         // 
         // cbRegionR1
         // 
         cbRegionR2.FormattingEnabled = true;
         cbRegionR2.Location = new System.Drawing.Point(12, 120);
         cbRegionR2.Name = "cbRegionR1";
         cbRegionR2.Size = new System.Drawing.Size(360, 20);
         cbRegionR2.TabIndex = 1;
         cbRegionR2.SelectionChangeCommitted += new System.EventHandler(cbRegionR2_SelectedIndexChanged);

         Controls.Add(cbRegionR2);
         Controls.Add(cbRegionR1);
         Controls.Add(cbRegion);

         Controls.Add(label5);
         Controls.Add(label4);
         Controls.Add(label3);
         

         UpdateDgvRegion();
      }

      private void cbRegion_SelectedIndexChanged(object sender, EventArgs e)
      {
         Region r = cbRegion.SelectedItem as Region;

         if (r != null)
         {
            cbRegionR1.SelectedItem = -1;
            cbRegionR2.SelectedItem = -1;

            foreach (Region1 r1 in cbRegionR1.Items)
            {
               if (r.region1.Equals(r1.id))
               {
                  cbRegionR1.SelectedItem = r1;
                  break;
               }
            }

            foreach (Region2 r2 in cbRegionR2.Items)
            {
               if (r.region2.Equals(r2.id))
               {
                  cbRegionR2.SelectedItem = r2;
                  break;
               }
            }
         }
      }

      private void cbRegionR1_SelectedIndexChanged(object sender, EventArgs e)
      {
         Region1 r1 = cbRegionR1.SelectedItem as Region1;

         if (r1 != null && r1.region2 != null && r1.region2.Length > 0)
         {
            foreach (Region2 r2 in cbRegionR2.Items)
            {
               if (r2.id.Equals(r1.region2))
               {
                  cbRegionR2.SelectedItem = r2;
                  break;
               }
            }

            List<Region> list = new List<Region>();
            foreach (Region r in census.dsRegion.Data)
            {
               if (r.region1.Equals(r1.Id))
                  list.Add(r);
            }

            cbRegion.DataSource = list;
            cbRegion.SelectedIndex = -1;
         }
      }

      private void cbRegionR2_SelectedIndexChanged(object sender, EventArgs e)
      {
         Region2 r2 = cbRegionR2.SelectedItem as Region2;

         if (r2 != null)
         {
            List<Region1> list = new List<Region1>();
            foreach (Region1 r1 in census.dsRegion1.Data)
            {
               if(r1.region2.Equals(r2.Id))
                  list.Add(r1);
            }

            cbRegionR1.DataSource = list;
            cbRegionR1.SelectedIndex = -1;
            cbRegion.SelectedIndex = -1;
         }
      }

      private void UpdateDgvRegion()
      {
         if (census.dsRegion != null)
         {
            List<Region> list = new List<Region>();
            list.AddRange(census.dsRegion.Values);
            list.Sort(new Comparison<Region>
               (delegate(Region r1, Region r2)
               { return r1.Name.CompareTo(r2.Name); }));

            cbRegion.DataSource = list;

            List<Region2> listR2 = new List<Region2>();
            listR2.AddRange(census.dsRegion2.Values);
            cbRegionR2.DataSource = listR2;

            List<Region1> listR1 = new List<Region1>();
            listR1.AddRange(census.dsRegion1.Values);
            cbRegionR1.DataSource = listR1;

            cbRegion .SelectedIndex = -1;
            cbRegionR1.SelectedIndex = -1;
            cbRegionR2.SelectedIndex = -1;
         }
      }

      private void UpdateCbControls(Region region)
      {
         if (region != null)
         {
            List<Region2> listR2 = new List<Region2>();
            listR2.AddRange(census.dsRegion2.Values);
            cbRegionR2.DataSource = listR2;

            List<Region1> listR1 = new List<Region1>();
            listR1.AddRange(census.dsRegion1.Values);
            cbRegionR1.DataSource = listR1;

            cbRegionR1.SelectedIndex = -1;
            cbRegionR2.SelectedIndex = -1;

            if (region.region1.Length > 0)
            {
               foreach (Region1 r1 in (List<Region1>)cbRegionR1.DataSource)
               {
                  if (r1.Id.Equals(region.region1))
                  {
                     cbRegionR1.SelectedItem = r1;
                     break;
                  }
               }
            }

            if (region.region2.Length > 0)
            {
               foreach (Region2 r2 in (List<Region2>)cbRegionR2.DataSource)
               {
                  if (r2.Id.Equals(region.region2))
                  {
                     cbRegionR2.SelectedItem = r2;
                     break;
                  }
               }
            }
         }
      }

      protected override void OnClosing(System.ComponentModel.CancelEventArgs e)
      {
         if (DialogResult == DialogResult.OK)
         {
            Region region = cbRegion.SelectedItem as Region;

            if (region == null)
            {
               e.Cancel = true;
               MessageBox.Show("Выберите населенный пункт");
               cbRegion.Focus();
            }
            else
            {
               Org.region = region;
            }
         }

         base.OnClosing(e);
      }

      public override PotenzialOrg Org
      {
         get
         {
            return base.Org;
         }
         set
         {
            base.Org = value;

            cbRegion.SelectedItem = Org.region;
         }
      }
   }
}
