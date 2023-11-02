using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Collections;

namespace GRSoft.NapoleonManager
{
   public partial class FmUserOrder : Form
   {

      public DataSet<int, Note> dsNote;
      public DataSet<int, NoteAction> dsNoteAction;
      public Dictionary<string, NoteAction> nacash = new Dictionary<string, NoteAction>();

      public FmUserOrder()
      {
         InitializeComponent();
         dsNote = (DataSet<int, Note>)DataModule.Get(Note.OBJECT_NAME) ?? new DataSet<int, Note>(Note.OBJECT_NAME);
         dsNoteAction = (DataSet<int, NoteAction>)DataModule.Get(NoteAction.OBJECT_NAME) ?? new DataSet<int, NoteAction>(NoteAction.OBJECT_NAME);
         grid.AutoGenerateColumns = false;
      }

      void FillFilterComboBox()
      {
         cbBrigade.Items.Clear();
         cbBrigade.Items.Add("Все");
         cbBrigade.SelectedIndex = 0;
      }

     
      //Настройка кнопок для выбора периода 
      private void AdjustRangeButton(bool isToday, string toolTipText)
      {
         btnRange.Image = isToday ? miToday.Image : miRange.Image;
         miToday.Checked = isToday;
         miRange.Checked = !isToday;
         btnRange.ToolTipText = toolTipText;
         dtpEnd.Enabled = !isToday;
      }

      private void btnRange_ButtonClick(object sender, EventArgs e)
      {
         if (miToday.Checked)
         {
            miRange_Click(null, null); 
         }
         else
         {
            miToday_Click(null, null);
         }
      }

      private void miToday_Click(object sender, EventArgs e)
      {
         AdjustRangeButton(true, "За сегодня");
      }

      private void miRange_Click(object sender, EventArgs e)
      {
         AdjustRangeButton(false, "За период");
      }

      private void FmOrder_Move(object sender, EventArgs e)
      {
        
      }

      private void dtpBegin_LocationChanged(object sender, EventArgs e)
      {
        
      }

      private void dtpEnd_ValueChanged(object sender, EventArgs e)
      {
        
      }

      private void cbBrigade_SelectionChangeCommitted(object sender, EventArgs e)
      {
        
      }

      private void dgvOrder_ColumnHeaderMouseClick(object sender, DataGridViewCellMouseEventArgs e)
      {
        
      }

      private OrderGridComparer gridComparer = new OrderGridComparer();

      class OrderGridComparer : GridBoundedObjectComparer
      {
      }

      private void RefreshWithSortOrder()
      {
         
      }

      private void dgvOrder_SelectionChanged(object sender, EventArgs e)
      {
      }

      private void dgvOrder_MouseDoubleClick(object sender, MouseEventArgs e)
      {
         
      }

      private void dgvOrder_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         if(e.RowIndex >= 0 )
         {
            Note n = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as Note;
            e.CellStyle.BackColor = Color.White;
            string key = n.userid + n.created.ToString();

            if (nacash.ContainsKey(key) && nacash[key].readed == 1)
               e.CellStyle.BackColor = Color.Gray;
         }
      }

      private void dgvOrder_MouseDown(object sender, MouseEventArgs e)
      {
         DataGridView.HitTestInfo info = ((DataGridView)sender).HitTest(e.X, e.Y);

         if (info != null && e.Button == MouseButtons.Right)
         {
            ((DataGridView)sender).CurrentCell =
               ((DataGridView)sender).Rows[info.RowIndex].Cells[info.ColumnIndex];
         }
      }

      private void btnReport_Click(object sender, EventArgs e)
      {
         
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         const string COMMON_FILTER_STR = "\"{0}\" >= ToDate('{1:dd/MM/yyyy}') and \"{0}\" < ToDate('{2:dd/MM/yyyy}')";

         dsNote.Filter = string.Format(COMMON_FILTER_STR, "created", dtpBegin.Value.Date, dtpEnd.Value.Date.AddDays(1));
         dsNoteAction.Filter = string.Format(COMMON_FILTER_STR, "created", dtpBegin.Value.Date, dtpEnd.Value.Date.AddDays(1));
         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsNote);
         list.Add(dsNoteAction);

         FmWait.StdDataRefresh(this, list, DoLoadData);
      }

      private void DoLoadData()
      {
         SortBindingList<Note> data = new SortBindingList<Note>();

         foreach (Note n in dsNote.Values)
            data.Add(n);

         nacash.Clear();
         foreach (NoteAction n in dsNoteAction.Values)
            nacash[n.userid + n.created.ToString()] = n;

         grid.DataSource = data;
      }

      private void grid_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         Note n = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as Note;

         if (n != null)
            tbRemark.Text = n.remark;
      }

      private void miMarkAsread_Click(object sender, EventArgs e)
      {
         DataGridViewRow r = grid.CurrentRow;

         if(r != null)
         {
            Note n = r.DataBoundItem as Note;

            if (n != null)
            {
               NoteAction na = new NoteAction();
               na.created = n.created;
               na.userid = n.userid;
               na.readed = 1;
               List<IDataSet> list = new List<IDataSet>();
               SimpleDataSet<NoteAction> ds = new SimpleDataSet<NoteAction>(NoteAction.OBJECT_NAME);
               ds.Add(na);
               list.Add(ds);

               if (!DataModule.WriteDataSet(list, Config.GetConfig().GetConnection()))
                  DialogUtil.UpdateErrMsg(this);
               else
               {
                  grid.Refresh();
                  nacash[na.userid + na.created.ToString()] = na;
               }
            }
         }
      }
   }
   
}
