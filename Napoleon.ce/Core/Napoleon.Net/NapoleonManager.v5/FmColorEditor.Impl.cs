using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmColorEditor : Form
   {
      public void __Initing()
      {
         
      }

      internal SysColors Colors
      {
         get
         {
            SysColors colors = new SysColors();
            colors.Clear();
            foreach (Color c in colorList.Items)
            {
               colors.Add(c);
            }
            return colors;
         }
         set
         {
            colorList.BeginUpdate();
            foreach (Color c in value)
               colorList.Items.Add(c);

            colorList.EndUpdate();
         }
      }

      private void tsDel_Click(object sender, EventArgs e)
      {
         int index = colorList.SelectedIndex;
         if (index >= 0)
            colorList.Items.RemoveAt(index);
      }

      private void tsEdit_Click(object sender, EventArgs e)
      {
         int index = colorList.SelectedIndex;
         if (index >= 0)
         {
            colorEditor.Color = (Color)colorList.Items[index];
            if (colorEditor.ShowDialog() == DialogResult.OK)
            {
               colorList.Items[index] = colorEditor.Color;
            }
         }
      }

      private void tsAdd_Click(object sender, EventArgs e)
      {
         if (colorEditor.ShowDialog() == DialogResult.OK)
         {
            colorList.Items.Add(colorEditor.Color);
         }
      }

      private void colorList_DrawItem(object sender, DrawItemEventArgs e)
      {
         if ((e.State & DrawItemState.Focus) != 0)
            e.DrawFocusRectangle();

         Color item = (Color)colorList.Items[e.Index];
         using (Brush b = new SolidBrush(((e.State & DrawItemState.Selected) != 0) ? Color.Blue : e.BackColor))
            e.Graphics.FillRectangle(b, e.Bounds);

         Rectangle bnds = e.Bounds;
         bnds.Inflate(new Size(-2, -2));
         using (Brush fb = new SolidBrush((Color)item))
            e.Graphics.FillRectangle(fb, bnds);
      }

      private void colorList_DoubleClick(object sender, EventArgs e)
      {
         tsEdit_Click(sender, e);
      }
   }
}
