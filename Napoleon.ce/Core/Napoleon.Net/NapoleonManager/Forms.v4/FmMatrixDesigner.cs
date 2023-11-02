using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using System.Threading;

namespace GRSoft.NapoleonManager
{
   public partial class FmMatrixDesigner : Form
   {
      Pen border = new Pen(Color.FromArgb(109, 109, 109));

      public FmMatrixDesigner()
      {
         InitializeComponent();
         __Initing();

         this.tsbClearName.Click += TsbClearName_Click;
         this.tsbClearFind.Click += TsbClearFind_Click;
         toolStrip1.Paint += ToolStrip1_Paint;
      }

      private void ToolStrip1_Paint(object sender, PaintEventArgs e)
      {
         if (tbMatrixName.Visible)
            PaintBorder(e.Graphics, tbMatrixName);
         PaintBorder(e.Graphics, tstbFind);
      }

      void PaintBorder(Graphics g, ToolStripTextBox tb)
      {
         Rectangle r = new Rectangle(Point.Add(tb.TextBox.Location, new Size(-1, -1)), new Size(tb.TextBox.Size.Width + 2, tb.TextBox.Size.Height + 2));
         g.DrawRectangle(border, r);
      }

      private void TsbClearFind_Click(object sender, EventArgs e)
      {
         tstbFind.Text = "";
      }

      private void TsbClearName_Click(object sender, EventArgs e)
      {
         tbMatrixName.Text = "";
      }
   }
}