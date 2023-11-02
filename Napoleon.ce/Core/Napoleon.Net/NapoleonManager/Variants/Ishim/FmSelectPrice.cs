using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmSelectPrice : Form
   {
      private System.Object lockThis = new System.Object();
      List<PriceEx> allRecs = new List<PriceEx>();
      IsUsed used;

      public FmSelectPrice()
      {
         InitializeComponent();
         dgvItems.AutoGenerateColumns = false;
      }

      List<Price> Checked
      {
         get
         {
            List<Price> ret = new List<Price>();
            List<PriceEx> src = (List<PriceEx>)dgvItems.DataSource;

            foreach (PriceEx p in src)
               if (p.IsChecked)
                  ret.Add(p.Item);

            return ret;
         }
      }

      void SetPrice(DataSet<string, Price> dsPrice, List<Price> selected, IsUsed used)
      {
         allRecs = new List<PriceEx>();
         foreach(Price p in dsPrice.Data)
         {
            PriceEx pe = new PriceEx(p);
            pe.IsChecked = selected.Contains(p);

            allRecs.Add(pe);
         }
         allRecs.Sort();
         this.used = used;

         dgvItems.DataSource = allRecs;
      }

      public static List<Price> SelectPrice(DataSet<string, Price> dsPrice, List<Price> selected, IsUsed used)
      {
         FmSelectPrice form = new FmSelectPrice();
         form.SetPrice(dsPrice, selected, used);
         if (form.ShowDialog() == DialogResult.OK)
            return form.Checked;

         return null;
      }

      private void toolStripButton3_Click(object sender, EventArgs e)
      {
         dgvItems.CommitEdit(DataGridViewDataErrorContexts.Commit);
         DialogResult = System.Windows.Forms.DialogResult.OK;
         Close();
      }

      private void toolStripButton2_Click(object sender, EventArgs e)
      {
         DialogResult = System.Windows.Forms.DialogResult.Cancel;
         Close();
      }

      private void timer1_Tick(object sender, EventArgs e)
      {
         timer1.Stop();
         DoSearch(tsbFind.Text.ToUpper());
      }

      private void DoSearch(string str)
      {
         lock (lockThis)
         {
            if (str.Length == 0)
               dgvItems.DataSource = allRecs;
            else
            {
               List<PriceEx> src = new List<PriceEx>();
               foreach (PriceEx pe in allRecs)
                  if (pe.Name.ToUpper().Contains(str))
                     src.Add(pe);

               dgvItems.DataSource = src;

            }
         }
      }

      private void toolStripButton1_Click(object sender, EventArgs e)
      {
         tsbFind.Text = "";
      }


      class PriceEx : IComparable<PriceEx>
      {
         Price price;
         bool ischeck = false;

         public PriceEx(Price p)
         {
            this.price = p;
         }

         public bool IsChecked { get { return ischeck; } set { ischeck = value; } }
         public string Name { get { return price.Name; } }
         public Price Item { get { return price; } }

         public int CompareTo(PriceEx other)
         {
            return Name.CompareTo(other.Name);
         }
      }

      private void checkBox1_CheckedChanged(object sender, EventArgs e)
      {
            List<PriceEx> src = (List<PriceEx>)dgvItems.DataSource;
            foreach (PriceEx p in src)
               p.IsChecked = checkBox1.Checked;
            dgvItems.Invalidate();
      }

      private void toolStrip1_TextChanged(object sender, EventArgs e)
      {
         timer1.Stop();
         timer1.Start();
      }

      private void dgvItems_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         if(used != null)
         {
            PriceEx pe = dgvItems.Rows[e.RowIndex].DataBoundItem as PriceEx;
            e.CellStyle.BackColor = used(pe.Item) ? Color.LightGray : dgvItems.DefaultCellStyle.BackColor;
         }
      }
   }

   public delegate bool IsUsed(Price p);
}
