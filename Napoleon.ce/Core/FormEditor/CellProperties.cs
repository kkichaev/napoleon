using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace NFormEditor
{
   public partial class CellProperties : Form
   {
      private Cell cell;

      internal CellProperties(ref Cell cell)
      {
         InitializeComponent();

         this.cell = cell;

         colSpan.Value = cell.ColSpan;
         rowSpan.Value = cell.RowSpan;
         text.Text = cell.Text;

         Borders b = cell.Border;
         if ((b & Borders.Left) != 0)
            borders.SetItemChecked(0, true);
         if ((b & Borders.Right) != 0)
            borders.SetItemChecked(1, true);
         if ((b & Borders.Top) != 0)
            borders.SetItemChecked(2, true);
         if ((b & Borders.Bottom) != 0)
            borders.SetItemChecked(3, true);

         switch (cell.TextAlign)
         {
            case ContentAlignment.BottomCenter:
               align.SelectedItem = "BottomCenter";
               break;
            case ContentAlignment.BottomLeft:
               align.SelectedItem = "BottomLeft";
               break;
            case ContentAlignment.BottomRight:
               align.SelectedItem = "BottomRight";
               break;
            case ContentAlignment.MiddleCenter:
               align.SelectedItem = "MiddleCenter";
               break;
            case ContentAlignment.MiddleLeft:
               align.SelectedItem = "MiddleLeft";
               break;
            case ContentAlignment.MiddleRight:
               align.SelectedItem = "MiddleRight";
               break;
            case ContentAlignment.TopCenter:
               align.SelectedItem = "TopCenter";
               break;
            case ContentAlignment.TopLeft:
               align.SelectedItem = "TopLeft";
               break;
            case ContentAlignment.TopRight:
               align.SelectedItem = "TopRight";
               break;
         }
      }

      private void ok_Click(object sender, EventArgs e)
      {
         cell.ColSpan = Decimal.ToInt32(colSpan.Value);
         cell.RowSpan = Decimal.ToInt32(rowSpan.Value);
         cell.Text = text.Text;

         if ((string)align.SelectedItem == "BottomCenter")
            cell.TextAlign = ContentAlignment.BottomCenter;
         else if ((string)align.SelectedItem == "BottomLeft")
            cell.TextAlign = ContentAlignment.BottomLeft;
         else if ((string)align.SelectedItem == "BottomRight")
            cell.TextAlign = ContentAlignment.BottomRight;
         else if ((string)align.SelectedItem == "MiddleCenter")
            cell.TextAlign = ContentAlignment.MiddleCenter;
         else if ((string)align.SelectedItem == "MiddleLeft")
            cell.TextAlign = ContentAlignment.MiddleLeft;
         else if ((string)align.SelectedItem == "MiddleRight")
            cell.TextAlign = ContentAlignment.MiddleRight;
         else if ((string)align.SelectedItem == "TopCenter")
            cell.TextAlign = ContentAlignment.TopCenter;
         else if ((string)align.SelectedItem == "TopLeft")
            cell.TextAlign = ContentAlignment.TopLeft;
         else if ((string)align.SelectedItem == "TopRight")
            cell.TextAlign = ContentAlignment.TopRight;

         Borders b = Borders.None;
         foreach (int index in borders.CheckedIndices)
         {
            if (index == 0)
               b |= Borders.Left;
            else if (index == 1)
               b |= Borders.Right;
            else if (index == 2)
               b |= Borders.Top;
            else if (index == 3)
               b |= Borders.Bottom;
         }
         cell.Border = b;
      }
   }
}