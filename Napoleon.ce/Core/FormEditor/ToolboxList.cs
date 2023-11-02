using System.Drawing;
using System.Windows.Forms;
using System;
namespace NFormEditor
{
   #region ToolboxList class

   struct ItemDragEventArgs
   {
      public ToolboxItem item;

      public ItemDragEventArgs(ToolboxItem item)
      {
         this.item = item;
      }
   }

   internal delegate void ItemDragEventHandler(object sender, ItemDragEventArgs arg);

   /// <summary>
   /// List of the toolbox items
   /// </summary>
   class ToolboxList : System.Windows.Forms.ListBox
   {
      private int itemUnderMouse = -1, curSelected = -1;

      private Color umColor = SystemColors.InactiveCaptionText;
      private Color umSelColor = SystemColors.InactiveCaption;
      private Color selColor = Color.AliceBlue;
      private Color groupColor = SystemColors.ControlDark;
      private Color frameColor = Color.Black;

      private readonly int DragDistance = 3;
      private Point mouseClickOrigin;

      private int groupControlHeightDiff = 5;

      private ImageList imageList;

      public ToolboxList()
      {
      }

      public event ItemDragEventHandler ItemDrag;

      /// <summary>
      /// Gets or sets backgound color of the item under mouse cursor
      /// </summary>
      public Color ItemUnderMouseColor
      {
         get { return umColor; }
         set { umColor = value; }
      }


      public Color SelectedItemUnderMouseColor
      {
         get { return umSelColor; }
         set { umSelColor = value; }
      }

      public Color SelectedItemColor
      {
         get { return selColor; }
         set { selColor = value; }
      }

      /// <summary>
      /// Gets or sets frame color of the item under mouse cursor
      /// </summary>
      public Color FrameColor
      {
         get { return frameColor; }
         set { frameColor = value; }
      }

      /// <summary>
      /// Gets or sets backgound color of the group item
      /// </summary>
      public Color GroupColor
      {
         get { return groupColor; }
         set { groupColor = value; }
      }

      public ToolboxItem CurrentSelected
      {
         get { return (curSelected >= 0) ? Items[curSelected] as ToolboxItem : null; }
      }

      public int CurrentSelectedIndex
      {
         get { return curSelected; }
         set { ChangeSelection(value); }
      }

      /// <summary>
      /// 
      /// </summary>
      public ImageList Images
      {
         get { return imageList; }
         set { imageList = value; }
      }

      private int GetItemAt(Point pt)
      {
         int i = TopIndex, count = Items.Count;

         while (i < count)
         {
            Rectangle bounds = GetItemRectangle(i);

            if (bounds.Contains(pt))
               return i;

            i++;
         }
         return -1;
      }

      private int Distance(Point p1, Point p2)
      {
         int x = p1.X - p2.X;
         int y = p1.Y - p2.Y;

         return (int)Math.Sqrt(x * x + y * y);
      }

      protected override void OnMouseMove(MouseEventArgs e)
      {
         if ((e.Button & MouseButtons.Left) != 0)
         {
            if (Distance(mouseClickOrigin, Control.MousePosition) > DragDistance && curSelected >= 0)
            {
               if (ItemDrag != null)
               {
                  ToolboxItem dItem = Items[curSelected] as ToolboxItem;
                  ItemDrag(this, new ItemDragEventArgs(dItem));
               }
            }
            return;
         }

         Point mousePoint = PointToClient(Control.MousePosition);
         int item = GetItemAt(mousePoint);

         if (item != -1 && itemUnderMouse != item)
         {
            ToolboxItem tbItem = Items[item] as ToolboxItem;

            if (itemUnderMouse != -1)
               PaintItem(itemUnderMouse, false, null);

            itemUnderMouse = item;
            PaintItem(itemUnderMouse, true, null);
         }

         base.OnMouseMove(e);
      }

      protected override void OnMouseLeave(EventArgs e)
      {
         if (itemUnderMouse != -1)
         {
            PaintItem(itemUnderMouse, false, null);
            itemUnderMouse = -1;
         }
         base.OnMouseLeave(e);
      }


      protected void PaintItem(int item, bool underMouse, Graphics graphics)
      {
         Graphics g = (graphics == null) ? Graphics.FromHwnd(this.Handle) : graphics;

         Rectangle bounds = GetItemRectangle(item);

         bool itemSelected = (item == curSelected);
         ToolboxItem tbItem = Items[item] as ToolboxItem;
         string text = tbItem.text;

         underMouse = (underMouse && !tbItem.groupControl);

         Color backColor = this.BackColor;
         if (tbItem.groupControl && itemSelected)
            backColor = this.SelectedItemColor;
         else if (tbItem.groupControl)
            backColor = this.GroupColor;
         else if (underMouse && itemSelected)
            backColor = this.SelectedItemUnderMouseColor;
         else if (underMouse)
            backColor = this.ItemUnderMouseColor;
         else if (itemSelected)
            backColor = this.SelectedItemColor;

         Brush backBrush = new SolidBrush(backColor);
         Brush foreBrush = new SolidBrush(this.ForeColor);

         // draw background & frame
         g.FillRectangle(backBrush, bounds);
         if (underMouse || itemSelected)
         {
            Pen pen = new Pen(FrameColor, 1);

            bounds.Size = new Size(bounds.Size.Width - 1, bounds.Size.Height - 1);
            g.DrawRectangle(pen, bounds);

            pen.Dispose();
         }

         // restore bounds
         bounds = GetItemRectangle(item);

         // draw image
         int imageHeight = 0;
         int imageWidth = 0;

         if (imageList != null)
         {
            imageHeight = imageList.ImageSize.Height;
            imageWidth = imageList.ImageSize.Width;

            int offset = (tbItem.groupControl) ? 0 : 2;

            if (tbItem.image >= 0)
               imageList.Draw(g, bounds.Left + offset, bounds.Top + offset, imageWidth, imageHeight, tbItem.image);
         }

         // draw text
         bounds.Location = new Point(bounds.Left + imageWidth + 2, bounds.Top);

         SizeF size = g.MeasureString(text, this.Font);
         int vOffset = (bounds.Height - (int)size.Height) / 2;
         int hOffset = 1;

         Font textFont = this.Font;
         if (tbItem.groupControl)
            textFont = new Font(textFont.FontFamily, textFont.Size, FontStyle.Bold);

         bounds.Inflate(-hOffset, -vOffset);
         g.DrawString(text, textFont, foreBrush, bounds);

         foreBrush.Dispose();
         backBrush.Dispose();

         if (tbItem.groupControl)
            textFont.Dispose();

         if (graphics == null)
            g.Dispose();
      }

      protected override void OnDrawItem(DrawItemEventArgs e)
      {
         if (e.Index < 0 || e.Index >= Items.Count)
            return;

         Point mousePosition = this.PointToClient(Control.MousePosition);
         Rectangle bounds = GetItemRectangle(e.Index);

         bool underMouse = bounds.Contains(mousePosition) & (Control.MouseButtons == 0);
         PaintItem(e.Index, underMouse, e.Graphics);
      }

      protected override void OnMeasureItem(MeasureItemEventArgs e)
      {
         if (e.Index < 0 || e.Index >= Items.Count)
            return;

         ToolboxItem item = Items[e.Index] as ToolboxItem;
         e.ItemWidth = this.ClientSize.Width;
         e.ItemHeight = (item.groupControl) ? this.ItemHeight - groupControlHeightDiff : this.ItemHeight;
      }

      private void ChangeSelection(int newSelected)
      {
         if (newSelected == curSelected)
            return;

         if (curSelected >= 0)
         {
            int saveSelected = curSelected;

            curSelected = -1;
            PaintItem(saveSelected, false, null);
         }

         if (newSelected >= 0)
         {
            curSelected = newSelected;
            PaintItem(curSelected, false, null);
         }
      }

      protected override void OnMouseDown(MouseEventArgs e)
      {
         mouseClickOrigin = Control.MousePosition;
         int index = this.GetItemAt(this.PointToClient(Control.MousePosition));
         if (index >= 0)
            ChangeSelection(index);

         base.OnMouseDown(e);
      }
   }
   #endregion
}