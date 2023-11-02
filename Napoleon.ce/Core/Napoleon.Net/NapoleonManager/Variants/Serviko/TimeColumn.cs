using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public class TimeColumn : DataGridViewColumn
   {
      public TimeColumn() : base(new TimeCell())
      {
      }

      public override DataGridViewCell CellTemplate 
      { 
         get => base.CellTemplate; 
         set
         {
            if(value != null && !value.GetType().IsAssignableFrom(typeof(TimeCell)))
            {
               throw new InvalidCastException("Must be TimeCell");
            }
            base.CellTemplate = value;
         }
      }
   }

   public class TimeCell : DataGridViewTextBoxCell
   {

      string defaultValue = "";

      public TimeCell() : base()
      {
      }

      public static string ToStr(int value)
      {
         return string.Format("{0:D2}:{1:D2}", value / 60, value % 60);
      }

      public static int From(string value)
      {
         if (value == null || value.Length == 0)
            return 0;
         int h, m = 0;
         string[] data = value.Split(new char[] { ':' });
         int.TryParse(data[0], out h);
         if (data.Length > 1)
            int.TryParse(data[1], out m);

         return h * 60 + m % 60;
      }

      public static DateTime From(int value)
      {
         DateTime dt = new DateTime(DateTime.Now.Year, DateTime.Now.Month, DateTime.Now.Day
            ,value /60, value % 60, 0);
         return dt;
      }

      public static int From(DateTime value)
      {
         return value.Minute + 60 * value.Hour;
      }

      public override void InitializeEditingControl(int rowIndex, object initialFormattedValue, DataGridViewCellStyle dataGridViewCellStyle)
      {
         base.InitializeEditingControl(rowIndex, initialFormattedValue, dataGridViewCellStyle);
         TimeEditingControl ctl = DataGridView.EditingControl as TimeEditingControl;
         ctl.Value = From(From((this.Value == null) ? (string)DefaultNewRowValue : (string)Value));
      }

      public override Type EditType
      {
         get { return typeof(TimeEditingControl); }
      }

      public override Type ValueType
      {
         get { return typeof(string); }
      }

      public override object DefaultNewRowValue
      {
         get { return defaultValue; }
      }
   }

   class TimeEditingControl : DateTimePicker, IDataGridViewEditingControl
   {
      DataGridView dataGridView;
      private bool valueChanged = false;
      int rowIndex;

      public TimeEditingControl()
      {
         this.Format = DateTimePickerFormat.Custom;
         CustomFormat = "HH:mm";
         ShowUpDown = true;
      }

      // Implements the IDataGridViewEditingControl.EditingControlFormattedValue
      // property.
      public object EditingControlFormattedValue
      {
         get { return this.Value.ToString("HH:mm"); }
         set
         {
            if (value is String)
            {
               try
               {
                  this.Value = TimeCell.From(TimeCell.From((String)value));
               }
               catch
               {
                  this.Value = DateTime.Now;
               }
            }
         }
      }

      public object GetEditingControlFormattedValue(DataGridViewDataErrorContexts context)
      {
         return EditingControlFormattedValue;
      }

      public void ApplyCellStyleToEditingControl(DataGridViewCellStyle dataGridViewCellStyle)
      {
         this.Font = dataGridViewCellStyle.Font;
         this.CalendarForeColor = dataGridViewCellStyle.ForeColor;
         this.CalendarMonthBackground = dataGridViewCellStyle.BackColor;
      }

      public int EditingControlRowIndex
      {
         get { return rowIndex; }
         set { rowIndex = value; }
      }

      // Implements the IDataGridViewEditingControl.EditingControlWantsInputKey
      // method.
      public bool EditingControlWantsInputKey(Keys key, bool dataGridViewWantsInputKey)
      {
         // Let the DateTimePicker handle the keys listed.
         switch (key & Keys.KeyCode)
         {
            case Keys.Left:
            case Keys.Up:
            case Keys.Down:
            case Keys.Right:
            case Keys.Home:
            case Keys.End:
            case Keys.PageDown:
            case Keys.PageUp:
               return true;
            default:
               return !dataGridViewWantsInputKey;
         }
      }

      // Implements the IDataGridViewEditingControl.PrepareEditingControlForEdit
      // method.
      public void PrepareEditingControlForEdit(bool selectAll)
      {
      }

      // Implements the IDataGridViewEditingControl
      // .RepositionEditingControlOnValueChange property.
      public bool RepositionEditingControlOnValueChange
      {
         get { return false; }
      }

      // Implements the IDataGridViewEditingControl
      // .EditingControlDataGridView property.
      public DataGridView EditingControlDataGridView
      {
         get { return dataGridView; }
         set { dataGridView = value; }
      }

      public bool EditingControlValueChanged
      {
         get { return valueChanged; }
         set { valueChanged = value; }
      }

      public Cursor EditingPanelCursor
      {
         get { return base.Cursor; }
      }

      protected override void OnValueChanged(EventArgs eventargs)
      {
         valueChanged = true;
         this.EditingControlDataGridView.NotifyCurrentCellDirty(true);
         base.OnValueChanged(eventargs);
      }
   }
}
