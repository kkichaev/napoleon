using System;
using System.Collections.Generic;
using System.Drawing;
using System.Reflection;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public class Header
   {
      public List<Header> Children { get; set; }

      public string Name { get; set; }

      public int X { get; set; }

      public int Y { get; set; }

      public int Width { get; set; }

      public int Height { get; set; }

      public int ColumnId { get; set; }

      public bool Displayed { get; set; }

      public Rectangle Bounds { get; set; }

      public Header()
      {
         Name = string.Empty;
         Children = new List<Header>();
         ColumnId = -1;
      }

      public void SetBounds(DataGridViewCellPaintingEventArgs e, DataGridView dgv, System.Drawing.StringFormat format)
      {
         if (Children.Count != 0)
         {
            foreach (Header ch in Children)
               ch.SetBounds(e, dgv, format);

            return;
         }
         if (ColumnId == e.ColumnIndex)
         {
            Rectangle r = e.CellBounds;
            r.Y = Y;
            r.Height = Height;
            Bounds = r;

            
            e.PaintBackground(r, true);
            using (SolidBrush sb = new SolidBrush(dgv.ColumnHeadersDefaultCellStyle.ForeColor))
            {
               e.Graphics.DrawString(Name, dgv.ColumnHeadersDefaultCellStyle.Font, sb, Bounds, format);
            }
            e.Handled = true;
         }
      }

      public void SetDisplayed(bool val)
      {
         Displayed = val;
         foreach (Header ch in Children)
            ch.SetDisplayed(val);
      }

      public void Paint(PaintEventArgs e, DataGridView dgv, System.Drawing.StringFormat format)
      {
         if (Children.Count != 0 && Name.Length > 0)
         {
            Rectangle clip = Rectangle.Empty;
            int chW = 0;

            int firstId = Children[0].ColumnId;
            DataGridViewColumn clmn = dgv.Columns[firstId];
            bool firstFullVisible = clmn.Displayed && dgv.GetColumnDisplayRectangle(firstId, true).Width == clmn.Width;
            foreach (Header ch in Children)
            {
               clmn = dgv.Columns[ch.ColumnId];
               chW += clmn.Width;
               if (clmn.Displayed == false)
                  continue;

               Rectangle ri = dgv.GetColumnDisplayRectangle(ch.ColumnId, true);
               if (clip == Rectangle.Empty)
                  clip = ri;
               else
               {
                  clip.Width = ri.Right - clip.X;
               }
            }
            Rectangle r = new Rectangle((firstFullVisible) ? clip.Left : clip.Right - chW, Y, chW, Height);
            Bounds = r;
            e.Graphics.SetClip(clip);

            //using (SolidBrush sb = new SolidBrush(dgv.ColumnHeadersDefaultCellStyle.SelectionBackColor))
            //{
            //   e.Graphics.FillRectangle(sb, Bounds);
            //}
         
            using (SolidBrush sb = new SolidBrush(dgv.ColumnHeadersDefaultCellStyle.ForeColor))
            {
               e.Graphics.DrawString(Name, dgv.ColumnHeadersDefaultCellStyle.Font, sb, Bounds, format);
            }
            e.Graphics.ResetClip();
         }

         foreach (Header ch in Children)
            ch.Paint(e, dgv, format);
      }

      public void Measure(DataGridView objGrid, int iY, int iHeight)
      {
         Width = 0;
         if (Children.Count > 0)
         {
            int tempY = string.IsNullOrEmpty(Name.Trim()) ? iY : iY + iHeight;
            bool columnWidthSet = false;
            foreach (Header child in Children)
            {
               child.Measure(objGrid, tempY, iHeight);
               Width += child.Width;
               if (!columnWidthSet && Width > 0)
               {
                  ColumnId = child.ColumnId;
                  columnWidthSet = true;
               }
            }
         }
         else if (-1 != ColumnId && objGrid.Columns[ColumnId].Visible)
         {
            Width = objGrid.Columns[ColumnId].Width;
         }
         Y = iY;
         if (Children.Count == 0)
         {
            Height = objGrid.ColumnHeadersHeight - iY;
         }
         else
         {
            Height = iHeight;
         }
      }
   }

   public class StackedHeaderDecorator
   {
      private readonly IStackedHeaderGenerator objStackedHeaderGenerator = null;
      private readonly DataGridView objDataGrid;
      private Header objHeaderTree;
      private int iNoOfLevels;
      private readonly System.Drawing.StringFormat objFormat;

      public StackedHeaderDecorator(IStackedHeaderGenerator objStackedHeaderGenerator, DataGridView objDataGrid)
      {
         this.objStackedHeaderGenerator = objStackedHeaderGenerator;
         this.objDataGrid = objDataGrid;

         objFormat = new System.Drawing.StringFormat();
         objFormat.Alignment = StringAlignment.Center;
         objFormat.LineAlignment = StringAlignment.Center;

         Type dgvType = objDataGrid.GetType();
         PropertyInfo pi = dgvType.GetProperty("DoubleBuffered", BindingFlags.Instance | BindingFlags.NonPublic);
         pi.SetValue(objDataGrid, true, null);

         objDataGrid.Scroll += (objDataGrid_Scroll);
         objDataGrid.ColumnWidthChanged += objDataGrid_ColumnWidthChanged;
         objDataGrid.SizeChanged += objDataGrid_SizeChanged;

         objHeaderTree = objStackedHeaderGenerator.GenerateStackedHeader();

         objDataGrid.Paint += objDataGrid_Paint;
         objDataGrid.CellPainting += objDataGrid_CellPainting;

         RegenerateHeaders();
      }

      void objDataGrid_SizeChanged(object sender, EventArgs e)
      {
         Refresh();
      }

      public void Recreate()
      {
         RegenerateHeaders();
         Refresh();
      }

      void objDataGrid_ColumnWidthChanged(object sender, DataGridViewColumnEventArgs e)
      {
         Refresh();
      }

      void objDataGrid_CellPainting(object sender, DataGridViewCellPaintingEventArgs e)
      {
         if (e.RowIndex == -1)
            objHeaderTree.SetBounds(e, objDataGrid, objFormat);
      }

      void objDataGrid_Paint(object sender, PaintEventArgs e)
      {
         objHeaderTree.Paint(e, objDataGrid, objFormat);
      }

      void objDataGrid_Scroll(object sender, ScrollEventArgs e)
      {
         Refresh();
      }

      private void Refresh()
      {
         Rectangle rtHeader = objDataGrid.DisplayRectangle;
         rtHeader.Height = objDataGrid.ColumnHeadersHeight * (iNoOfLevels - 1) / iNoOfLevels;
         objDataGrid.Invalidate(rtHeader);
      }

      private void RegenerateHeaders()
      {
         objHeaderTree = objStackedHeaderGenerator.GenerateStackedHeader();

         iNoOfLevels = NoOfLevels(objHeaderTree) - 1;
         objDataGrid.ColumnHeadersHeightSizeMode = DataGridViewColumnHeadersHeightSizeMode.DisableResizing;
         objDataGrid.ColumnHeadersHeight = iNoOfLevels * 20;
         objHeaderTree.Measure(objDataGrid, 0, objDataGrid.ColumnHeadersHeight / iNoOfLevels);
      }

      private int NoOfLevels(Header header)
      {
         int level = 0;
         foreach (Header child in header.Children)
         {
            int temp = NoOfLevels(child);
            level = temp > level ? temp : level;
         }
         return level + 1;
      }
   }

   public interface IStackedHeaderGenerator
   {
      Header GenerateStackedHeader();
   }
}
