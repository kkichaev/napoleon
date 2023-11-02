using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager.Utils
{
   [DefaultEvent("Click")]
   public partial class RichButton : UserControl
   {
      private enum RBState { normal, pressed}
      private RBState State { get; set; }
      private Color bkg = DefaultBackColor;
      private Color checkedColor = Color.Blue;
      private bool isCheck = false;

      public RichButton()
      {
         InitializeComponent();
         State = RBState.normal;

         label1.MouseDown += label1_MouseDown;
         label1.MouseUp += label1_MouseUp;
         label2.MouseDown += label1_MouseDown;
         label2.MouseUp += label1_MouseUp;
         pictureBox1.MouseDown += label1_MouseDown;
         pictureBox1.MouseUp += label1_MouseUp;
         panel1.MouseDown += label1_MouseDown;
         panel1.MouseUp += label1_MouseUp;
         panel2.MouseDown += label1_MouseDown;
         panel2.MouseUp += label1_MouseUp;
      }

      private void label1_MouseUp(object sender, MouseEventArgs e)
      {
         OnMouseUp(e);
         OnClick(EventArgs.Empty);
      }

      private void label1_MouseDown(object sender, MouseEventArgs e)
      {
         OnMouseDown(e);
      }

      [Browsable(true)]
      public Image Icon 
      {
         get
         {
            return pictureBox1.Image;
         }

         set 
         {
            pictureBox1.Image = value;
         }
      }

      [Browsable(true)]
      public string Caption 
      {
         get 
         {
            return label1.Text;
         }

         set
         {
            label1.Text = value;
         }
      }

      
      [Browsable(true)]
      public string Description
      {
         get
         {
            return label2.Text;
         }

         set
         {
            label2.Text = value;
         }
      }

      protected override void OnPaint(PaintEventArgs e)
      {
         base.OnPaint(e);
         DrawBorder(e);
      }

      private void DrawBorder(PaintEventArgs e)
      {
         Pen p = new Pen(State == RBState.pressed ? Color.FromArgb(0xff, 0xad, 0xf8, 0xe6) : Color.LightBlue, 1);
         e.Graphics.DrawRectangle(p, new Rectangle(0, 0, Width - 1, Height - 1));
      }

      protected override void OnMouseDown(MouseEventArgs e)
      {
         base.OnMouseDown(e);

         State = RBState.pressed;
         bkg = BackColor;
         BackColor = Color.LightBlue;
         Invalidate();
      }

      protected override void OnMouseUp(MouseEventArgs e)
      {
         base.OnMouseUp(e);
         
         State = RBState.normal;
         BackColor = bkg;
         Invalidate();
      }

      public bool Checked
      {
         get { return isCheck; }
         set
         {
            isCheck = value;
            label1.ForeColor = isCheck ? checkedColor : Color.Black;
            label1.Font = new Font(label1.Font, isCheck ? FontStyle.Bold : FontStyle.Regular);
         }
      }

      protected override void OnClick(EventArgs e)
      {
         base.OnClick(e);

         Checked = true;
      }

      public void PerformClick()
      {
         OnClick(EventArgs.Empty);
      }
   }
}
