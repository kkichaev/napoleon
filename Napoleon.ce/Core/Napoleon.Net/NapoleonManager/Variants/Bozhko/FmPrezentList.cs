using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.IO;
using System.Runtime.InteropServices;
using System.Xml;

namespace GRSoft.NapoleonManager
{
   public partial class FmPrezentList : Form
   {
      const int WIDTH = 120;
      static Size size = new Size(WIDTH, WIDTH / 3 * 4);
      static Size cell = new Size(size.Width + 20, size.Height + 20);

      public FmPrezentList()
      {
         InitializeComponent();
         
         grid.AutoSizeRowsMode = DataGridViewAutoSizeRowsMode.None;
         grid.RowTemplate.Height = cell.Height;

         const int START_COL = 3;
         const int START_ROW = 4;

         numCol.Value = START_COL;
         numRow.Value = START_ROW;

         lbList.Items.Add(new PItem(1, START_COL, START_ROW));
         lbList.SelectedIndex = 0;

         btnSave.Enabled = false;
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         InsertImage();
      }

      private void InsertImage()
      {
         if (openFileDialog.ShowDialog() == DialogResult.OK)
         {
            btnSave.Enabled = true;
            Stream stream = null;
            if ((stream = openFileDialog.OpenFile()) != null)
            {
               using (stream)
               {
                  DataGridViewCell cell = grid.CurrentCell;
                  cell.Value = MakeImage(Image.FromStream(stream), "");
                  cell.Tag = openFileDialog.FileName;

                  PItem item = lbList.SelectedItem as PItem;

                  if (item != null)
                     item.addItem(cell.ColumnIndex, cell.RowIndex, openFileDialog.FileName);
               }
            }
         }
      }

      private Bitmap MakeImage(Image pic, string desc)
      {
         Bitmap bitmap = new Bitmap(size.Width, size.Height);

         using (Graphics g = Graphics.FromImage(bitmap))
         {
            g.FillRectangle(new SolidBrush(Color.White), 0, 0, bitmap.Width, bitmap.Height);
            RectangleF rectF1 = new RectangleF(5, 5, bitmap.Width - 10, cell.Height / 3 - 10);
            g.DrawString(desc, new Font("Arial", 8), new SolidBrush(Color.Black), rectF1); ;
            Size sz = new Size(size.Width - 10, size.Height / 3 * 2 - 10);
            Image scale = FmPricePhoto.ScaleImage(pic, sz);

            g.DrawImage(scale, (bitmap.Width - scale.Width) / 2, cell.Height / 3);
         }

         return bitmap;
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         ClearCell();
         btnSave.Enabled = true;
      }

      private void ClearCell()
      {
         ((DataGridViewImageCell)grid.CurrentCell).Value = null;
      }

      private void numCol_ValueChanged(object sender, EventArgs e)
      {
         btnSave.Enabled = true;

         int col = (int)((NumericUpDown)sender).Value;

         if (col == grid.Columns.Count)
            return;

         if (col > grid.Columns.Count)
            for (int i = grid.Columns.Count; i < col; i++)
            {
               DataGridViewImageColumn column = new DataGridViewImageColumn();
               column.Width = cell.Width;
               grid.Columns.Add(column);
            }
         else if (col < grid.Columns.Count)
            while (col < grid.Columns.Count)
               grid.Columns.RemoveAt(grid.Columns.Count - 1);

         PItem item = lbList.SelectedItem as PItem;

         if (item != null)
            item.col = col;
      }

      private void numRow_ValueChanged(object sender, EventArgs e)
      {
         btnSave.Enabled = true;

         int row = (int)((NumericUpDown)sender).Value;

         if (row == grid.Rows.Count)
            return;

         if (row > grid.Rows.Count)
            for (int i = grid.Rows.Count; i < row; i++)
               grid.Rows.Add();
         else if (row < grid.Rows.Count)
            while (row < grid.Rows.Count)
               grid.Rows.RemoveAt(grid.Rows.Count - 1);

         PItem item = lbList.SelectedItem as PItem;

         if (item != null)
            item.row = row;
      }

      private void grid_CellDoubleClick(object sender, DataGridViewCellEventArgs e)
      {
         InsertImage();
      }

      private void grid_MouseDown(object sender, MouseEventArgs e)
      {
         if (e.Button == MouseButtons.Right)
         {
            DataGridView.HitTestInfo info = ((DataGridView)sender).HitTest(e.X, e.Y);
            ((DataGridView)sender).CurrentCell = ((DataGridView)sender)[info.ColumnIndex, info.RowIndex];
         }
      }

      private void itDelete_Click(object sender, EventArgs e)
      {
         ClearCell();
         btnSave.Enabled = true;
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         if (saveXmlDialog.ShowDialog() == DialogResult.OK)
         {
            btnSave.Enabled = false;
            const string PHOTO = "photo";
            string path = System.IO.Path.GetDirectoryName(saveXmlDialog.FileName);
            

            XmlWriterSettings settings = new XmlWriterSettings();
            settings.Indent = true;
            using (XmlWriter writer = XmlWriter.Create(saveXmlDialog.FileName, settings))
            {
               writer.WriteStartDocument();
               writer.WriteStartElement("prezentation");

               foreach(object o in lbList.Items)
               {
                  PItem item = (PItem)o;

                  writer.WriteStartElement("list");
                  writer.WriteAttributeString("col", item.col.ToString());
                  writer.WriteAttributeString("row", item.row.ToString());

                  foreach(Pic p in item.items) 
                  {
                     string from = p.fileName;
                     string filename = System.IO.Path.GetFileName(from);
                     string to = path + "\\" + PHOTO + "\\" + filename;
                     System.IO.Directory.CreateDirectory(path + "\\" + PHOTO);

                     if (from != to && !File.Exists(to))
                     {
                        Size sz = new Size(240, 320);
                        Image image = Image.FromFile(from);
                        Image output = FmPrice.resizeImage(image, sz);
                        output.Save(to);
                     }

                     writer.WriteStartElement("item");
                     writer.WriteAttributeString("col", p.col.ToString());
                     writer.WriteAttributeString("row", p.row.ToString());
                     writer.WriteAttributeString("path", PHOTO + "\\" + filename);
                     writer.WriteAttributeString("desc", p.desc);

                     foreach (Price price in p.items)
                     {
                        writer.WriteStartElement("price");
                        writer.WriteAttributeString("id", price.id);
                        writer.WriteAttributeString("name", price.name);
                        writer.WriteEndElement();
                     }
                     writer.WriteEndElement();
                  }

                  writer.WriteEndElement();
               }

               writer.WriteEndElement();
               writer.WriteEndDocument();
            }
         }
      }

      private void btnLoad_Click(object sender, EventArgs e)
      {
         if (openXmlDialog.ShowDialog() == DialogResult.OK)
         {
            btnSave.Enabled = false;
            lbList.Items.Clear();
            string path = System.IO.Path.GetDirectoryName(openXmlDialog.FileName);

            using (XmlReader reader = XmlReader.Create(openXmlDialog.FileName, new XmlReaderSettings()))
            {
               int num = 1;
               while (reader.Read())
               {
                  if (reader.NodeType == XmlNodeType.Element)
                  {
                     if(reader.Name == "list")
                     {
                        int col = 1;
                        int row = 1;

                        try
                        {
                           col = Int32.Parse(reader.GetAttribute("col"));
                           row = Int32.Parse(reader.GetAttribute("row"));
                           PItem item = new PItem(num++, col, row);
                           lbList.Items.Add(item);
                        }
                        catch (Exception) { }
                     }
                     else if (reader.Name == "item")
                     {
                        PItem item = (PItem)lbList.Items[lbList.Items.Count - 1];
                        item.addItem(Int32.Parse(reader.GetAttribute("col")),
                              Int32.Parse(reader.GetAttribute("row")),
                              path + "\\" + reader.GetAttribute("path"))
                              .desc = reader.GetAttribute("desc"); ;
                     }
                     else if (reader.Name == "price")
                     {
                        PItem item = (PItem)lbList.Items[lbList.Items.Count - 1];
                        Pic pic = item.items[item.items.Count - 1];
                        Price p = new Price();
                        p.id = reader.GetAttribute("id");
                        p.name = reader.GetAttribute("name");
                        pic.items.Add(p);
                     }
                  }
               }
            }

            if (lbList.Items.Count > 0)
            {
               lbList.SelectedIndex = 0;
               grid_CellEnter(grid, null);
            }
         }
      }

      int lastSelection = -1;

      private void lbList_SelectedIndexChanged(object sender, EventArgs e)
      {
         PItem item = lbList.SelectedItem as PItem;

         if (item != null)
         {
            lastSelection = ((ListBox)sender).SelectedIndex;
            //tbDesc.Text = string.Empty;
            lbPrice.Items.Clear();

            for (int r = 0; r < grid.Rows.Count; r++)
               for (int c = 0; c < grid.Columns.Count; c++)
                  grid[c, r].Value = null;


            numCol.Value = item.col;
            numRow.Value = item.row;

            foreach (Pic p in item.items)
            {
               DataGridViewCell cell = grid[p.col, p.row];
               cell.Value = MakeImage(Image.FromFile(p.fileName), p.desc);
               cell.Tag = p.fileName;
            }

            DataGridViewCell curcel = grid.CurrentCell;

            if (curcel != null)
            {
               Pic pic = item.getItem(curcel.ColumnIndex, curcel.RowIndex);

               if (pic != null)
               {
                  lbPrice.Items.AddRange(pic.items.ToArray());
                  //tbDesc.Text = pic.desc;
               }
            }
         }
         else
            ((ListBox)sender).SelectedIndex = lastSelection;
      }

      private void btnAddList_Click(object sender, EventArgs e)
      {
         PItem item = lbList.SelectedItem as PItem;
         btnSave.Enabled = true;
         if (item != null)
         {
            lbList.Items.Add(item.Copy(lbList.Items.Count + 1));
            lbList.SelectedIndex = lbList.Items.Count - 1;
            
         }
         else
         {
            lbList.Items.Add(new PItem(lbList.Items.Count + 1, 3, 3));
            lbList.SelectedIndex = lbList.Items.Count - 1;
         }
      }

      private void btnPrice_Click(object sender, EventArgs e)
      {
         PItem item = lbList.SelectedItem as PItem;

         if(item != null)
         {
            btnSave.Enabled = true;

            DataGridViewCell cell = grid.CurrentCell;
            Pic pic = item.getItem(cell.ColumnIndex, cell.RowIndex);

            if (pic != null)
            {
               List<Price> output = FmSelectSKU.SelectItems(this, pic.items, null);

               if (output != null)
               {
                  pic.items = output;
                  lbPrice.Items.Clear();
                  lbPrice.Items.AddRange(pic.items.ToArray());
               }
            }
         }
      }

      private void grid_CellEnter(object sender, DataGridViewCellEventArgs e)
      {
         lbPrice.Items.Clear();
         //tbDesc.Text = string.Empty;
         PItem item = lbList.SelectedItem as PItem;

         if (item != null)
         {
            DataGridViewCell cell = grid.CurrentCell;

            if (cell != null)
            {
               Pic pic = item.getItem(cell.ColumnIndex, cell.RowIndex);

               if (pic != null)
               {
                  lbPrice.Items.AddRange(pic.items.ToArray());
                  //tbDesc.Text = pic.desc;
               }
            }
         }
      }

      //private void tbDesc_TextChanged(object sender, EventArgs e)
      //{
      //   if (((TextBox)sender).ContainsFocus)
      //   {
      //      btnSave.Enabled = true;
      //      PItem item = lbList.SelectedItem as PItem;

      //      if (item != null)
      //      {
      //         DataGridViewCell cell = grid.CurrentCell;
      //         Pic pic = item.getItem(cell.ColumnIndex, cell.RowIndex);

      //         if (pic != null)
      //         {
      //            pic.desc = ((TextBox)sender).Text;

      //            Bitmap value = cell.Value as Bitmap;

      //            using (Graphics g = Graphics.FromImage(value))
      //            {
      //               g.FillRectangle(new SolidBrush(Color.White), 0, 0, value.Width, size.Height / 3);
      //               RectangleF rectF1 = new RectangleF(5, 5, value.Width - 10, value.Height / 3 - 10);
      //               g.DrawString(pic.desc, new Font("Arial", 8), new SolidBrush(Color.Black), rectF1); ;
      //            }

      //            grid.Refresh();
      //         }
      //      }
      //   }

      //}

      private void itListDelete_Click(object sender, EventArgs e)
      {
         if (MessageBox.Show(this, "Выбранный лист будет удален, удалить?",
               "Вопрос", MessageBoxButtons.OKCancel) == DialogResult.OK)
         {
            int sel = lbList.SelectedIndex;
            lbList.Items.Remove(lbList.SelectedItem);
            btnSave.Enabled = true;
         }
      }

      private void listContextMenu_Opening(object sender, CancelEventArgs e)
      {
         if (lbList.Items.Count <= 1)
            e.Cancel = true;
      }

      private void itPriceDelete_Click(object sender, EventArgs e)
      {
         if (lbPrice.SelectedIndex >= 0 && 
            MessageBox.Show(this, "Выбранный товар будет удален, удалить?",
               "Вопрос", MessageBoxButtons.OKCancel) == DialogResult.OK)
         {
            PItem item = lbList.SelectedItem as PItem;

            if (item != null)
            {
               DataGridViewCell cell = grid.CurrentCell;
               Pic pic = item.getItem(cell.ColumnIndex, cell.RowIndex);

               if (pic != null)
                  pic.items.Remove(lbPrice.SelectedItem as Price);
            }

            lbPrice.Items.Remove(lbPrice.SelectedItem);
            btnSave.Enabled = true;
         }
      }

      private void lbPrice_MouseDown(object sender, MouseEventArgs e)
      {
         ((ListBox)sender).SelectedIndex = ((ListBox)sender).IndexFromPoint(e.X, e.Y);
      }

      private void lbList_MouseDown(object sender, MouseEventArgs e)
      {
         ((ListBox)sender).SelectedIndex = ((ListBox)sender).IndexFromPoint(e.X, e.Y);
      }

      private void FmPrezentList_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (btnSave.Enabled && MessageBox.Show(this, "Сохранить изменения?",
               "Вопрос", MessageBoxButtons.OKCancel) == DialogResult.OK)
            btnSave.PerformClick();
      }
   }

   class PItem
   {
      public int col;
      public int row;
      public int num;
      public List<Pic> items = new List<Pic>();

      public PItem(int num, int col, int row)
      {
         this.num = num;
         this.col = col;
         this.row = row;
      }

      public override string ToString()
      {
         return string.Format("Лист №{0}", num);
      }

      public PItem Copy(int num)
      {
         return new PItem(num, col, row);
      }

      public Pic addItem(int c, int r, string p)
      {
         deleteItem(c, r);
         Pic result = new Pic();
         result.col = c;
         result.row = r;
         result.fileName = p;
         items.Add(result);

         return result;
      }

      private void deleteItem(int c, int r)
      {
         foreach(Pic pic in items)
            if(c == pic.col && r == pic.row)
            {
               items.Remove(pic);
               return;
            }
      }

      public Pic getItem(int c, int r)
      {
         foreach (Pic pic in items)
            if (c == pic.col && r == pic.row)
            {
               return pic;
            }

         return null;
      }
   }

   class Pic
   {
      public int col = 0;
      public int row = 0;
      public string fileName = string.Empty;
      public string desc = string.Empty;

      public List<Price> items = new List<Price>();
   }
}
