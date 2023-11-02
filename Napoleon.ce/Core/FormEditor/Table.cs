using System.Windows.Forms;
using System.Windows.Forms.Design;
using System.Drawing;
using System.ComponentModel;
using System.Collections.ObjectModel;
using System;
using System.ComponentModel.Design;
using System.Windows.Forms.Design.Behavior;
using System.Collections;
using System.Diagnostics;

namespace NFormEditor
{
   class IntCollection : Collection<int>
   {
      private Collection<int> realCollection;

      public IntCollection(Collection<int> realCollection)
      {
         this.realCollection = realCollection;
      }

      protected override void InsertItem(int index, int item)
      {
         base.InsertItem(index, item);
         realCollection.Insert(index, (int)(item / Program.ScaleFactor));
      }

      protected override void RemoveItem(int index)
      {
         base.RemoveItem(index);
         realCollection.RemoveAt(index);
      }

      protected override void ClearItems()
      {
         base.ClearItems();
         realCollection.Clear();
      }
   }

   [Designer(typeof(TableDesigner))]
   class Table : Control, IScalable, IFontSize
   {
      // высоту и ширину храним нарастающим итогом
      private Collection<TableRow> rows = new Collection<TableRow>();
      private Collection<int> width;
      private Collection<int> height;

      private Collection<int> realWidth = new Collection<int>();
      private Collection<int> realHeight = new Collection<int>();

      private int variableRow = -1;
      private string obj;

      private int fontSize = 6;

      private Scaler scaler;

      private int selectedCell = -1, selectedRow = -1;

      const int SnapArea = 5;
      const int CRowHeight = 40;
      const int CellWidth = 80;

      public Table()
      {
         width = new IntCollection(realWidth);
         height = new IntCollection(realHeight);

         scaler = new Scaler(this);
         scaler.Scaling += new EventHandler(ScalingCells);
      }

      public void InitNewDesign()
      {
         width.Add((int)(CellWidth * Program.ScaleFactor));
         //realWidth.Add(CellWidth);

         AddRow();
         Size = new Size(CellWidth + 1, CRowHeight + 1);
      }

      void ScalingCells(object sender, EventArgs e)
      {
         for (int i = realWidth.Count - 1; i >= 0; i--)
            width[i] = (int)(realWidth[i] * Program.ScaleFactor);

         for (int i = realHeight.Count - 1; i >= 0; i--)
            height[i] = (int)(realHeight[i] * Program.ScaleFactor);

         UpdateMinSize();
      }

      public override ISite Site
      {
         get
         {
            return base.Site;
         }
         set
         {
            if (base.Site != null)
               scaler.UnSited();

            base.Site = value;

            if (value != null)
               scaler.Sited();
         }
      }

      [Category("—войства")]
      public string Object 
      { 
         get { return obj; }
         set { obj = value; } 
      }

      [Category("—войства")]
      [DefaultValue(-1)]
      public int VariableRow
      {
         get
         { 
            return variableRow; 
         }
         set 
         {
            if (variableRow != value)
            {
               variableRow = value;
               Invalidate();
               Update();
            }
         } 
      }

      internal int SelCell
      { 
         get 
         { 
            return selectedCell; 
         } 
         set 
         { 
            selectedCell = value;
            Invalidate();
            Update();
         }  
      }
      internal int SelRow 
      {
         get 
         { 
            return selectedRow; 
         } 
         set 
         { 
            selectedRow = value;
            Invalidate();
            Update();
         } 
      }

      internal void CellBounds(int row, Cell cell, ref Rectangle bounds)
      {
         bounds.Y = (row == 0) ? 0 : height[row - 1];
         bounds.X = (cell.Index == 0) ? 0 : width[cell.Index - 1];

         bounds.Height = height[row + cell.RowSpan - 1] - bounds.Y;
         bounds.Width = width[cell.Index + cell.ColSpan - 1] - bounds.Left;
      }

      public HitTestEnum HitTest(Point scPt, ref int row, ref int col)
      {
         Point pt = PointToClient(scPt);

         row = 0;
         foreach (TableRow tr in rows)
         {
            if (Math.Abs(pt.Y - height[row]) < SnapArea && row != rows.Count - 1)
               return HitTestEnum.OnRow;

            col = 0;
            foreach (Cell cell in tr)
            {
               Rectangle bounds = new Rectangle();
               
               CellBounds(row, cell, ref bounds);
               if (Math.Abs(pt.X - bounds.Right) < SnapArea && col != tr.Count - 1)
                  return HitTestEnum.OnCell;
               if (bounds.Contains(pt))
                  return HitTestEnum.InsideCell;

               col++;
            }
            if (height[row] > pt.Y)
               break;

            row++;
         }
         return HitTestEnum.None;
      }

      [Category("—войства")]
      [DesignerSerializationVisibility(DesignerSerializationVisibility.Content)]
      public Collection<TableRow> Rows
      {
         get { return rows; }
         set { rows = value; }
      }

      [Category("—войства")]
      [DesignerSerializationVisibility(DesignerSerializationVisibility.Content)]
      public Collection<int> TableWidth
      {
         get { return width; }
         set { width = value; }
      }

      [Category("—войства")]
      [DesignerSerializationVisibility(DesignerSerializationVisibility.Content)]
      public Collection<int> TableHeight
      {
         get { return height; }
         set { height = value; }
      }

      protected override void OnSizeChanged(System.EventArgs e)
      {
         base.OnSizeChanged(e);

         if (scaler.IsScaling)
         {
            Height = height[rows.Count - 1] + 1;
            Width = width[width.Count - 1] + 1;
            return;
         }

         height[rows.Count - 1] = Height - 1;
         width[width.Count - 1] = Width - 1;

         realHeight[rows.Count - 1] = (int)(height[rows.Count - 1] / Program.ScaleFactor);
         realWidth[width.Count - 1] = (int)(width[width.Count - 1] / Program.ScaleFactor);
      }

      protected override void OnPaint(PaintEventArgs e)
      {
         base.OnPaint(e);

         int ctr = 0;
         foreach(TableRow row in rows)
         {
            row.Draw(e.Graphics, this, ctr, fontSize);
            ctr++;
         }
      }

      internal void UpdateMinSize()
      {
         int y = (rows.Count > 1) ? height[rows.Count - 2] + 1 : 1;
         int x = (width.Count > 1) ? width[width.Count - 2] + 1 : 1;
         Size sz = new Size(x, y);
         MinimumSize = sz;
      }

      internal void AddRow()
      {
         TableRow r = new TableRow();
         //realHeight.Add(CRowHeight);
         height.Add((int)(CRowHeight * Program.ScaleFactor));

         if( rows.Count == 0 )
         {
            Cell c = new Cell(0);
            r.Add(c);
         } else
         {
            TableRow check = rows[rows.Count - 1];

            int rh = (rows.Count > 1) ? height[rows.Count - 1] - height[rows.Count - 2] : height[rows.Count - 1];

            height[height.Count-1] = height[rows.Count - 1] + rh;
            realHeight[realHeight.Count - 1] = (int)(height[height.Count - 1] / Program.ScaleFactor);

            for( int i = 0; i<width.Count; i++ )
            {
               Cell c = new Cell(i);
               r.Add(c);
            }
         }
         Rows.Add(r);

         UpdateMinSize();
         Height = height[rows.Count - 1] + 1;
      }

      internal void DragColumn(int dragCol, int dragRow, Point delta)
      {
         Cell cell = rows[dragRow][dragCol];
         int index = cell.Index + cell.ColSpan - 1;
         width[index] += delta.X;
         realWidth[index] = (int)(width[index] / Program.ScaleFactor);

         int sw = (index == 0) ? 0 : width[index - 1];

         Rectangle r = new Rectangle(sw, 0, width[index + 1] - sw, Height);
         Invalidate(r);
         Update();
      }

      internal void DragRow(int dragRow, Point delta)
      {
         height[dragRow] += delta.Y;
         realHeight[dragRow] = (int)(height[dragRow] / Program.ScaleFactor);

         int sh = (dragRow == 0) ? 0 : height[dragRow - 1];

         Rectangle r = new Rectangle(0, sh, Width, height[dragRow + 1] - sh);
         Invalidate(r);
         Update();
      }

      internal void RemoveRow()
      {
         if (selectedRow == -1 || rows.Count == 1 )
            return;

         if (selectedRow != rows.Count - 1)
         {
            int dh = height[selectedRow + 1] - height[selectedRow];
            int rh = (int)(dh / Program.ScaleFactor);
            for (int cr = selectedRow + 1; cr < rows.Count; cr++)
            {
               height[cr] -= dh;
               realHeight[cr] -= rh;
            }

            // убрать объединенные €чейки из удал€емой строки
            TableRow dest = rows[selectedRow + 1];
            foreach (Cell cell in rows[selectedRow])
            {
               if (cell.RowSpan > 1)
               {
                  int i=0;
                  for (; i < dest.Count; i++)
                  {
                     if (dest[i].Index >= cell.Index)
                        break;
                  }
                  Cell nc = new Cell();
                  nc.Index = cell.Index;
                  nc.RowSpan = cell.RowSpan - 1;
                  nc.ColSpan = cell.ColSpan;
                  nc.Text = cell.Text;

                  dest.Insert(i, nc);
               }
            }
         }

         // убрать строку из объединенных €чеек
         for (int i = 0; i < selectedRow; i++)
         {
            TableRow tr = rows[i];
            foreach (Cell cell in tr)
            {
               if (cell.RowSpan > 1 && selectedRow - i <= cell.RowSpan - 1 && selectedRow - i >= 0)
                  cell.RowSpan--;
            }
         }

         rows.RemoveAt(selectedRow);
         height.RemoveAt(selectedRow);
         //realHeight.RemoveAt(selectedRow);

         UpdateMinSize();
         Height = height[rows.Count - 1];

         selectedRow = -1;
         selectedCell = -1;
      }

      internal void AddColumn(int insertAfter)
      {
         int index = insertAfter;
         if( index > width.Count)
            index = width.Count;
         if (index <= 0)
            index = 1;

         int w = width[index - 1];
         if (index == width.Count)
         {
            w += CellWidth;
         }
         else
         {
            int w1 = width[index];
            w += (w1 - w) / 2;
         }

         width.Insert(index,w);
         //realWidth.Add((int)(w / Program.ScaleFactor));

         foreach (TableRow tr in rows)
         {
            Cell c = new Cell(index);
            Cell insCell = null;
            foreach (Cell cell in tr)
            {
               if (cell.Index >= index)
               {
                  if (insCell == null)
                     insCell = cell;
                  cell.Index = cell.Index + 1;
               }
            }
            if( insCell != null )
               tr.Insert(tr.IndexOf(insCell), c);
            else
               tr.Add(c);
         }

         Width = w + 1;
         UpdateMinSize();
      }

      internal void AddColumn()
      {
         int index = width.Count;
         int w = width[index - 1] + CellWidth;
         width.Add(w);
         //realWidth.Add((int)(w / Program.ScaleFactor));

         foreach (TableRow tr in rows)
         {
            Cell c = new Cell(index);
            tr.Add(c);
         }

         Width = w+1;
         UpdateMinSize();
      }

      internal void RemoveColumn()
      {
         if (selectedCell == -1 || width.Count == 1)
            return;

         int selCellIndex = rows[selectedRow][selectedCell].Index;
         foreach (TableRow tr in rows)
         {
            for( int i=0; i<tr.Count; i++ )
            {
               Cell c = tr[i];
               if (selCellIndex - c.Index >= 0 && selCellIndex - c.Index <= c.ColSpan - 1)
               {
                  if (c.ColSpan > 1) c.ColSpan--;
                  else
                  {
                     tr.Remove(c);
                     i--;
                  }
               }
               else if (c.Index > selCellIndex)
                  c.Index--;
            }
         }
         if (selCellIndex != width.Count - 1)
         {
            int dw = (selCellIndex == 0) ? width[selCellIndex] : width[selCellIndex] - width[selCellIndex-1];
            int rw = (int)(dw / Program.ScaleFactor);
            for (int i = selCellIndex + 1; i < width.Count; i++)
            {
               width[i] -= dw;
               realWidth[i] -= rw;
            }
         }
         width.RemoveAt(selCellIndex);
         //realWidth.RemoveAt(selCellIndex);

         Width = width[width.Count-1]+1;
         UpdateMinSize();
      }

      private int CheckColSpan(int selectedRow, int selectedCell, bool onlyCheck, int prevCS)
      {
         TableRow tr = rows[selectedRow];
         Cell cell = tr[selectedCell];

         if (prevCS == cell.ColSpan)
            return cell.ColSpan;

         if (prevCS > cell.ColSpan) // мы всегда можем убрать часть объединенной €чейки
         {
            // insert new cell;
            Cell newCell = new Cell(cell.Index + cell.ColSpan);
            newCell.ColSpan = prevCS - cell.ColSpan;
            tr.Insert(selectedCell + 1, newCell);

            return cell.ColSpan;
         }

         // мы можем вставить только если nextCell.Index = ourCell.Index + ourCell.ColSpan && nextCell.ColSpan = 1 && nextCell.RowSpan = 1
         // check colspan
         int cs = prevCS, ctr = selectedCell + 1;
         for (; cs < cell.ColSpan; )
         {
            if (ctr < tr.Count)
            {
               Cell checkCell = tr[ctr];

               //
               // if checkCell.Index > cs + cell.Index, then prevRow cell have rowSpan
               //
               if (checkCell.ColSpan > 1 || checkCell.RowSpan > 1 || checkCell.Index != cs + cell.Index)
                  break;

               if (onlyCheck)
                  ctr++;
               else
                  tr.RemoveAt(ctr);
               cs++;
            }
            else
            {
               if (cs + cell.Index < width.Count)
                  cs++;
               else
                  break;
            }
         }

         if (onlyCheck)
            return cs;

         cell.ColSpan = cs;
         return cell.ColSpan;
      }

      private void InsertCell(int row, int index, int colspan)
      {
         int i = 0;
         TableRow cr = rows[row];
         for (; i < cr.Count; i++)
         {
            Cell checkCell = cr[i];

            if (checkCell.Index == index)
               return;

            if (checkCell.Index > index)
            {
               Cell c = new Cell(index);
               c.ColSpan = Math.Min(checkCell.Index - index, colspan);
               cr.Insert(i, c);
               return;
            }

            // check overlapped cells
            if (checkCell.Index + checkCell.ColSpan > index)
               return;
         }
      }

      private int FindCell(int row, int index)
      {
         int i = 0;
         TableRow cr = rows[row];
         for (; i < cr.Count; i++)
         {
            Cell checkCell = cr[i];

            if (checkCell.Index == index)
               return i;

            // check overlapped cells
            if (checkCell.Index + checkCell.ColSpan > index)
               return -1;
         }
         return -1;
      }

      //
      // нет проверки на объединенные €чейки в текущей строке (считаетс€, что CheckColSpan, был вызван до этого)
      //
      private void CheckRowSpan(int selectedRow, int selectedCell, int prevRS, int prevCS)
      {
         TableRow tr = rows[selectedRow];
         Cell cell = tr[selectedCell];

         if (prevRS > cell.RowSpan)
         {
            int cr = cell.RowSpan;
            while (cr < prevRS)
            {
               InsertCell(selectedRow + cr, cell.Index, prevCS);
               cr++;
            }

            return;
         }

         int rowSpan = prevRS;
         while (rowSpan < cell.RowSpan && rowSpan + selectedRow < rows.Count)
         {
            // find cell
            int i = FindCell(selectedRow + rowSpan, cell.Index);
            if (i < 0)
               break;

            Cell curCell = rows[selectedRow + rowSpan][i];
            if (curCell.RowSpan > 1 || curCell.ColSpan > cell.ColSpan)
               break;

            // check can we span
            int saveSpan = curCell.ColSpan;
            curCell.ColSpan = cell.ColSpan;
            int checkSpan = CheckColSpan(selectedRow + rowSpan, i, true, saveSpan);
            if (checkSpan < cell.ColSpan)
            {
               curCell.ColSpan = saveSpan;
               break;
            }

            // span cell, and remove it
            CheckColSpan(selectedRow + rowSpan, i, false, saveSpan);
            rows[selectedRow + rowSpan].RemoveAt(i);

            rowSpan++;
         }

         cell.RowSpan = rowSpan;
      }

      internal void CheckCell(int selectedRow, int selectedCell, int prevColSpan, int prevRowSpan)
      {
         Cell cell = rows[selectedRow][selectedCell];

         if( cell.ColSpan != prevColSpan )
            CheckColSpan(selectedRow, selectedCell, false, prevColSpan);
         if( cell.RowSpan != prevRowSpan )
            CheckRowSpan(selectedRow, selectedCell, prevRowSpan, prevColSpan);

         Invalidate();
         Update();
      }

      #region IScalable Members

      public void Scaling(float factor)
      {
         scaler.DoScaling(factor);
      }

      [Browsable(false)]
      [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
      public Size RealSize
      {
         get { return scaler.Size; }
      }

      [Browsable(false)]
      [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
      public Point RealLocation
      {
         get { return scaler.Location; }
      }

      #endregion

      #region IFontSize Members

      [Category("—войства")]
      [Browsable(true)]
      public int FontSize
      {
         get
         {
            return fontSize;
         }
         set
         {
            fontSize = value;

            Invalidate();
            Update();
         }
      }

      #endregion
   }

   [Flags]
   enum Borders { None = 0, Left = 1, Right = 2, Top = 4, Bottom = 8, All = 15};
   enum HitTestEnum { None, OnCell, OnRow, InsideCell };

   [TypeConverter(typeof(CellConvertor))]
   class Cell
   {
      private int index = 0;
      private string text;
      private int colspan = 1, rowspan = 1;
      private ContentAlignment align = ContentAlignment.MiddleCenter;
      private Borders borders = Borders.All;

      public Cell()
      {
      }

      public Cell(int index)
      {
         this.index = index;
      }

      public Cell(int index, int cs, int rs, string text, string salign, Borders b)
      {
         colspan = cs;
         rowspan = rs;
         this.index = index;
         this.text = text;
         if (salign != null && salign.Length > 0)
         {
            TypeConverter tc = TypeDescriptor.GetConverter(typeof(ContentAlignment));
            align = (ContentAlignment)tc.ConvertFromString(salign);
         }
         borders = b;
      }

      [DefaultValue(Borders.All)]
      public Borders Border
      {
         get { return borders; }
         set { borders = value; }
      }

      [DefaultValue(ContentAlignment.MiddleCenter)]
      public ContentAlignment TextAlign
      {
         set { align = value;  }
         get { return align;  }
      }

      [DefaultValue(1)]
      public int ColSpan { get { return colspan; } set { colspan = value; } }

      [DefaultValue(1)]
      public int RowSpan { get { return rowspan; } set { rowspan = value; } }

      public int Index { get { return index; } set { index = value; } }

      public string Text { get { return text; } set { text = value; } }

      virtual public void Draw(Rectangle rbounds,  Graphics g, Control parent, bool selected, int fontSize)
      {
         //Point end = new Point(origin.X, origin.Y);
         //Pen p = new Pen(SystemColors.WindowText);
         //end.Offset(Width, Height);
         //g.DrawLine(p, origin, end);

         StringFormat sf = new StringFormat(StringFormat.GenericDefault);
         switch (align)
         {
            case ContentAlignment.BottomCenter:
               sf.Alignment = StringAlignment.Center;
               sf.LineAlignment = StringAlignment.Far;
               break;
            case ContentAlignment.BottomLeft:
               sf.Alignment = StringAlignment.Near;
               sf.LineAlignment = StringAlignment.Far;
               break;
            case ContentAlignment.BottomRight:
               sf.Alignment = StringAlignment.Far;
               sf.LineAlignment = StringAlignment.Far;
               break;
            case ContentAlignment.MiddleCenter:
               sf.Alignment = StringAlignment.Center;
               sf.LineAlignment = StringAlignment.Center;
               break;
            case ContentAlignment.MiddleLeft:
               sf.Alignment = StringAlignment.Near;
               sf.LineAlignment = StringAlignment.Center;
               break;
            case ContentAlignment.MiddleRight:
               sf.Alignment = StringAlignment.Far;
               sf.LineAlignment = StringAlignment.Center;
               break;
            case ContentAlignment.TopCenter:
               sf.Alignment = StringAlignment.Center;
               sf.LineAlignment = StringAlignment.Near;
               break;
            case ContentAlignment.TopLeft:
               sf.Alignment = StringAlignment.Near;
               sf.LineAlignment = StringAlignment.Near;
               break;
            case ContentAlignment.TopRight:
               sf.Alignment = StringAlignment.Far;
               sf.LineAlignment = StringAlignment.Near;
               break;
         }

         RectangleF bounds = new RectangleF(rbounds.Left+2, rbounds.Top+1, rbounds.Width-4, rbounds.Height-2);
         if (selected)
            g.FillRectangle(SystemBrushes.ControlLight, bounds);

         using (Font font = new Font("Microsoft Sans Serif", fontSize * Program.ScaleFactor * 300.0F / 96))
            g.DrawString(text, font, SystemBrushes.WindowText, bounds, sf);

      }
   }

   class CellConvertor : TypeConverter
   {
      public override bool  CanConvertFrom(ITypeDescriptorContext context, System.Type sourceType)
      {
         if( sourceType == typeof(string) )
            return true;
         return base.CanConvertFrom(context, sourceType);
      }

      public override object ConvertFrom(ITypeDescriptorContext context, System.Globalization.CultureInfo culture, object value)
      {
         if( value is string )
         {
            char[] chars = ((string)value).ToCharArray();
            Collection<string> v = new Collection<string>();

            string curVal = "";
            // states
            // 0 - норм. текст
            // 1 - был нормальный, тек.символ - кавычка
            // 2 - текст в кавычках
            // 3 - кавычка после текста (ждем следующий символ чтобы пон€ть что это
            // 4 - две кавычки пришли, ждем следующий символ чтобы пон€ть что это 
            int state = 0;
            foreach(char c in chars)
            {
               if (c == ',')
               {
                  if (state == 0 || state == 3 || state == 4)
                  {
                     v.Add(curVal);
                     curVal = "";
                     state = 0;
                  } else
                     curVal += c;
                  continue;
               }
               else if (state == 4)
               {
                  curVal += "\"";
                  state = 0;
               } 
               if (c == '"')
               {
                  if (state == 0)
                  {
                     state = 1;
                  } else if (state == 1)
                  {
                     state = 4;
                  } else if (state == 2)
                  {
                     state = 3;
                  } else if (state == 3)
                  {
                     curVal += '"';
                     state = 2;
                  }
               } else
               {
                  curVal += c;
                  if (state == 1) state = 2;
               }
            }
            v.Add(curVal);
            //string[] v = ((string)value).Split(new char[] { ',' });
            if (v.Count < 5 || v[4].Length == 0)
               return new Cell(int.Parse(v[0]), int.Parse(v[1]), int.Parse(v[2]), v[3], null, Borders.All);
            else
            {
               Borders b = Borders.All;
               string align = v[4];
               if (align.Contains("-"))
               {
                  string[] astr = align.Split(new char[] { '-' });
                  align = astr[0];
                  astr = astr[1].Split(new char[] { '|' });

                  b = Borders.None;
                  foreach (string str in astr)
                  {
                     if (str == "Left")
                        b |= Borders.Left;
                     else if (str == "Right")
                        b |= Borders.Right;
                     else if (str == "Top")
                        b |= Borders.Top;
                     else if (str == "Bottom")
                        b |= Borders.Bottom;
                  }
               }
               return new Cell(int.Parse(v[0]), int.Parse(v[1]), int.Parse(v[2]), v[3], align, b);
            }
         }
         return base.ConvertFrom(context, culture, value);
      }

      public override bool  CanConvertTo(ITypeDescriptorContext context, System.Type destinationType)
      {
         if( destinationType == typeof(string) )
            return true;
 	      return base.CanConvertTo(context, destinationType);
      }

      public override object  ConvertTo(ITypeDescriptorContext context, System.Globalization.CultureInfo culture, object value, System.Type destinationType)
      {
         if (destinationType == typeof(string))
         {
            Cell rc = value as Cell;
            string align = (rc.TextAlign == ContentAlignment.MiddleCenter) ? "" : rc.TextAlign.ToString();
            string text = rc.Text;
            if (text == null) text = "";
            if (rc.Border != Borders.All)
            {
               string brd = "";
               if ((rc.Border & Borders.Left) != 0)
                  brd += "Left";
               if ((rc.Border & Borders.Right) != 0)
               {
                  if (brd.Length > 0) brd += "|";
                  brd += "Right";
               }
               if ((rc.Border & Borders.Top) != 0)
               {
                  if (brd.Length > 0) brd += "|";
                  brd += "Top";
               }
               if ((rc.Border & Borders.Bottom) != 0)
               {
                  if (brd.Length > 0) brd += "|";
                  brd += "Bottom";
               }
               align += "-" + brd;
            }
            return rc.Index + "," + rc.ColSpan + "," + rc.RowSpan + ",\"" + text.Replace("\"", "\"\"") + "\"," + align;
         }
 	      return base.ConvertTo(context, culture, value, destinationType);
      }
   }

   class TableRow : Collection<Cell>
   {
      public TableRow()
      {
      }

      public void Draw(Graphics g, Table parent, int row, int fontSize)
      {
         Rectangle bounds = new Rectangle();
         int curCel = 0;
         foreach (Cell cell in this)
         {
            parent.CellBounds(row, cell, ref bounds);

            cell.Draw(bounds, g, parent, (row == parent.SelRow && parent.SelCell == curCel), fontSize);

            if (cell.Border == Borders.All)
               g.DrawRectangle((row == parent.VariableRow) ? SystemPens.ActiveBorder : SystemPens.WindowText, bounds);
            else
            {
               Pen cp = (row == parent.VariableRow) ? SystemPens.ActiveBorder : SystemPens.WindowText;
               if ((cell.Border & Borders.Left) != 0)
                  g.DrawLine(cp, new Point(bounds.Left, bounds.Top), new Point(bounds.Left, bounds.Bottom));
               if ((cell.Border & Borders.Right) != 0)
                  g.DrawLine(cp, new Point(bounds.Right, bounds.Top), new Point(bounds.Right, bounds.Bottom));
               if ((cell.Border & Borders.Top) != 0)
                  g.DrawLine(cp, new Point(bounds.Left, bounds.Top), new Point(bounds.Right, bounds.Top));
               if ((cell.Border & Borders.Bottom) != 0)
                  g.DrawLine(cp, new Point(bounds.Left, bounds.Bottom), new Point(bounds.Right, bounds.Bottom));
            }
            curCel++;
         }
      }
   }

   class TableDesigner : ControlDesigner
   {
      private int dragRow, dragCol;
      private Point origin = new Point();
      bool dragging = false;

      public override void InitializeNewComponent(IDictionary defaultValues)
      {
         (Component as Table).InitNewDesign();
         base.InitializeNewComponent(defaultValues);
      }

      void RaiseChangeSize(int oldValue, string propName)
      {
         Table ctrl = Component as Table;
         PropertyDescriptor pd = TypeDescriptor.GetProperties(ctrl)[propName];
         RaiseComponentChanging(pd);
         RaiseComponentChanged(pd, oldValue, pd.GetValue(ctrl));
      }

      public void AddRow(Object sender, EventArgs args)
      {
         Table ctrl = Component as Table;

         int oldVal = ctrl.Height;
         ctrl.AddRow();
         RaiseChangeSize(oldVal, "Height");
      }

      public void RemoveRow(Object sender, EventArgs args)
      {
         Table ctrl = Component as Table;

         int oldVal = ctrl.Height;
         ctrl.RemoveRow();
         RaiseChangeSize(oldVal, "Height");
      }

      public void AddColumn(Object sender, EventArgs args)
      {
         Table ctrl = Component as Table;

         int oldVal = ctrl.Width;
         //ctrl.AddColumn();
         ctrl.AddColumn(ctrl.SelCell);
         RaiseChangeSize(oldVal, "Width");
      }

      public void RemoveColumn(Object sender, EventArgs args)
      {
         Table ctrl = Component as Table;

         int oldVal = ctrl.Width;
         ctrl.RemoveColumn();
         RaiseChangeSize(oldVal, "Width");
      }

      internal void SetCellProperties(Object sender, EventArgs args)
      {
         Table ctrl = Component as Table;
         int selectedRow = ctrl.SelRow;
         int selectedCell = ctrl.SelCell;
         if (selectedCell == -1 || selectedRow == -1) return;
         TableRow tr = ctrl.Rows[selectedRow];
         Cell cell = tr[selectedCell];

         int prevCS = cell.ColSpan;
         int prevRS = cell.RowSpan;

         IUIService uiService = (IUIService)GetService(typeof(IUIService));

         CellProperties cp = new CellProperties(ref cell);

         if (uiService.ShowDialog(cp) == DialogResult.OK)
            ctrl.CheckCell(selectedRow, selectedCell, prevCS, prevRS);
      }

      internal void MarkVariable(Object sender, EventArgs args)
      {
         Table ctrl = Component as Table;
         ctrl.VariableRow = ctrl.SelRow;
      }

      public override System.ComponentModel.Design.DesignerVerbCollection Verbs
      {
         get
         {
            return new DesignerVerbCollection(
               new DesignerVerb[] 
                  {
                     new DesignerVerb("Add row", new EventHandler(AddRow)),
                     new DesignerVerb("Remove row", new EventHandler(RemoveRow)),
                     new DesignerVerb("Add column", new EventHandler(AddColumn)),
                     new DesignerVerb("Remove column", new EventHandler(RemoveColumn)),
                     new DesignerVerb("Cell properties", new EventHandler(SetCellProperties)),
                     new DesignerVerb("Mark row variable", new EventHandler(MarkVariable)),
                  } 
               );
         }
      }

      protected override void OnMouseDragEnd(bool cancel)
      {
         base.OnMouseDragEnd(cancel);
         if (dragging)
         {
            ((Table)Component).UpdateMinSize();
            dragging = false;
         }
      }

      protected override void OnSetCursor()
      {
         Table ctrl = Component as Table;
         int col = 0, row = 0;
         switch (ctrl.HitTest(Cursor.Position, ref row, ref col))
         {
            default:
               Cursor.Current = Cursors.SizeAll;
               break;
            case HitTestEnum.OnCell:
               Cursor.Current = Cursors.SizeWE;
               break;
            case HitTestEnum.OnRow:
               Cursor.Current = Cursors.SizeNS;
               break;
         }
      }

      protected override void OnMouseDragMove(int x, int y)
      {
         if (!dragging)
         {
            base.OnMouseDragMove(x, y);
            return;
         }
         Table ctrl = Component as Table;
         Point delta = new Point(-(origin.X - x), -(origin.Y - y));

         if (dragCol != -1)
         {
            ctrl.DragColumn(dragCol, dragRow, delta);
            origin.Offset(delta);
         } else if (dragRow != -1)
         {
            ctrl.DragRow(dragRow, delta);
            origin.Offset(delta);
         } else
            base.OnMouseDragMove(x, y);
      }

      protected override void OnMouseDragBegin(int x, int y)
      {
         Table ctrl = Component as Table;

         dragCol = -1;
         dragRow = -1;
         origin = new Point(x, y);

         dragging = true;
         switch (ctrl.HitTest(origin, ref dragRow, ref dragCol))
         {
            case HitTestEnum.None:
               dragCol = -1;
               dragRow = -1;
               base.OnMouseDragBegin(x, y);
               break;

            case HitTestEnum.OnCell:
               break;

            case HitTestEnum.OnRow:
               dragCol = -1;
               break;

            case HitTestEnum.InsideCell:
               ctrl.SelRow = dragRow;
               ctrl.SelCell = dragCol;
               dragCol = -1;
               dragRow = -1;
               base.OnMouseDragBegin(x, y);
               break;
         }
      }

      public override System.Collections.IList SnapLines
      {
         get
         {
            Table table = Component as Table;

            Collection<SnapLine> lines = new Collection<SnapLine>();

            lines.Add(new SnapLine(SnapLineType.Top, 0));
            lines.Add(new SnapLine(SnapLineType.Bottom, 0));
            lines.Add(new SnapLine(SnapLineType.Left, 0));
            lines.Add(new SnapLine(SnapLineType.Right, 0));

            foreach (int h in table.TableHeight)
            {
               lines.Add(new SnapLine(SnapLineType.Top, h));
               lines.Add(new SnapLine(SnapLineType.Bottom, h));
            }

            foreach (int w in table.TableWidth)
            {
               lines.Add(new SnapLine(SnapLineType.Left, w));
               lines.Add(new SnapLine(SnapLineType.Right, w));
            }
            return lines;
         }
      }

      protected override void PreFilterProperties(System.Collections.IDictionary properties)
      {
         base.PreFilterProperties(properties);

         DictionaryEntry[] entiries = new DictionaryEntry[properties.Count];
         properties.CopyTo(entiries, 0);

         foreach (DictionaryEntry de in entiries)
         {
            PropertyDescriptor pd = de.Value as PropertyDescriptor;
            if (pd.IsBrowsable == false || pd.IsReadOnly) continue;

            if (pd.Category == "—войства") continue;

            if ( pd.Name != "Text" && pd.Name != "Location" && pd.Name != "Size" && pd.Name != "TextAlign")
               properties.Remove(de.Key);
         }
      }

      //protected override void OnPaintAdornments(PaintEventArgs e)
      //{
      //   base.OnPaintAdornments(e);

      //   Pen p = new Pen(SystemColors.WindowText);

      //   Control ctrl = Component as Control;
      //   e.Graphics.DrawLine(p, ctrl.Left, ctrl.Top, ctrl.Right, ctrl.Bottom);
      //} 
   }
}