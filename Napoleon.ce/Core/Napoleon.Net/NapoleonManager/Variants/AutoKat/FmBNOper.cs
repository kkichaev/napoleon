using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Diagnostics;
using System.Drawing;
using System.Text;
using System.Web;
using System.Web.UI.Design.WebControls;
using System.Windows.Forms;
using static GRSoft.NapoleonManager.Division;

namespace GRSoft.NapoleonManager
{
   public partial class FmBNOper : Form
   {
      MaskedTextBox textBox;
      SortableBindingList<GridData> data = new SortableBindingList<GridData>();
      public DataSet<string, BNOper> dsBNOper = new DataSet<string, BNOper>(BNOper.OBJECT_NAME);

      class GridData 
      { 
         public string ID {  get; set; }
         public string Agent { get; set; }
         public string Mo { get; set; }
         public string Tu { get; set; }
         public string We { get; set; }
         public string Th { get; set; }
         public string Fr { get; set; }

         public string Sa { get; set; }
         public string Su { get; set; }

         internal void Load(BNOper per)
         {
            Mo = per.mo;
            Tu = per.tu;
            We = per.we;
            Th = per.th;
            Fr = per.fr;
            Sa = per.sa;
            Su = per.su;
         }
      }


      public FmBNOper()
      {
         InitializeComponent();

         grid.AutoGenerateColumns = false;
         
         textBox = new MaskedTextBox();
         textBox.Mask = "90:90";
         textBox.Visible = false;
         textBox.KeyUp += TextBox_KeyUp;
         grid.Controls.Add(textBox);
         grid.DataSource = data;
         
      }

      private void TextBox_KeyUp(object sender, KeyEventArgs e)
      {
         if (e.KeyCode == Keys.Enter)
         {
            grid.CommitEdit(DataGridViewDataErrorContexts.Commit);
         }
      }

      private void grid_CellBeginEdit(object sender, DataGridViewCellCancelEventArgs e)
      {
         Debug.WriteLine("Begin Edit");
         Rectangle rect = ((DataGridView)sender).GetCellDisplayRectangle(e.ColumnIndex, e.RowIndex, true);
         textBox.Location = rect.Location;
         textBox.Size = rect.Size;
         textBox.Text = "";

         this.BeginInvoke(new Action(() =>
         {
            this.textBox.Visible = true;
            this.textBox.Focus();
            this.textBox.Select(0, 0);
         }));

         object value = ((DataGridView)sender)[e.ColumnIndex, e.RowIndex].Value;

         if (value != null)
         {
            textBox.Text = value.ToString();
         }

         textBox.Visible = true;
      }

      private void grid_CellEndEdit(object sender, DataGridViewCellEventArgs e)
      {
         if (e.ColumnIndex > 0)
         {
            if (textBox.Visible)
            {
               String old = ((DataGridView)sender).CurrentCell.Value?.ToString();
               String text = textBox.Text.Trim();
               GridData data = ((DataGridView)sender).CurrentRow.DataBoundItem as GridData;

               if (text.Equals(":")) text = "";

               if (text.Length > 0)
               {
                  String[] time = text.Split(':');

                  if (time.Length == 2)
                  {
                     if (Int32.TryParse(time[0], out int hour) && Int32.TryParse(time[1], out int min))
                     {
                        if (hour < 24 && min < 60)
                        {
                           text = string.Format("{0}:{1}", time[0].Trim(), time[1].Trim());
                           SetCellValue(e.RowIndex, e.ColumnIndex, text, data);
                        }
                     }
                  }
               }else
                  SetCellValue(e.RowIndex, e.ColumnIndex, text, data);

               textBox.Visible = false;

               if (!text.Equals(old))
                  btnSave.Enabled = true;
            }

            Debug.WriteLine("End Edit");
         }
      }

      private void SetCellValue(int row, int col, string text, GridData data)
      {
         grid.Rows[row].Cells[col].Value = text;

         BNOper oper = new BNOper() { id = data.ID };

         if (dsBNOper.ContainsKey(oper.id))
            oper = dsBNOper[oper.id];
         else
            dsBNOper[oper.id] = oper;

         string name = grid.Columns[col].DataPropertyName.ToLower();

         oper.GetType().GetField(name).SetValue(oper, text);
      }

      private void grid_Scroll(object sender, ScrollEventArgs e)
      {
         if (textBox.Visible)
         {
            DataGridViewCell cell = ((DataGridView)sender).CurrentCell;

            if (cell != null)
            {
               Rectangle rect = ((DataGridView)sender).GetCellDisplayRectangle(cell.ColumnIndex, cell.RowIndex,true);
               textBox.Location = rect.Location;
            }
         }
      }

      private void FmBNOper_Load(object sender, EventArgs e)
      {
         Manager mc = CurrentUser.user as Manager;
         if (mc != null)
         {
            mc.AllDivisions.ForEach(d => cbDivision.Items.Add(d));

            if (cbDivision.Items.Count > 0)
               cbDivision.SelectedIndex = 0;

            btnSync.PerformClick();
         }
      }

      private void cbDivision_SelectedIndexChanged(object sender, EventArgs e)
      {
        ReloadData();
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         if (SaveChanges(true))
            btnSave.Enabled = false;
      }

      bool CheckChanges()
      {
         if (!btnSave.Enabled)
            return true;

         DialogResult dr = MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
         if (dr == DialogResult.No)
            return true;
         if (dr == DialogResult.Cancel)
            return false;

         return SaveChanges(false);
      }

      public bool SaveChanges(bool showDialog)
      {
         bool ret = true;
         grid.CommitEdit(DataGridViewDataErrorContexts.Commit);

         if (dsBNOper.Count > 0)
         {
            List<IDataSet> wr = new List<IDataSet>();
            wr.Add(dsBNOper);

            ret = DataModule.UpdateDataSet(wr, null, null, Config.GetConfig().GetConnection());
            if (showDialog)
            {
               MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");
            }
         }

         return ret;
      }
      protected override void OnClosing(CancelEventArgs e)
      {
         base.OnClosing(e);
         if (!CheckChanges())
            e.Cancel = true;
      }

      private void btnSync_Click(object sender, EventArgs e)
      {
         dsBNOper.Filter = "'id' is null or 'id' is not null";

         List<IDataSet> sets = new List<IDataSet>();

         sets.Add(dsBNOper);

         DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), sets, null).Join();
         ReloadData();
      }

      private void ReloadData()
      {
         Division div = cbDivision.SelectedItem as Division;

         Debug.WriteLine("selectedIndexChanged: {0}", div);

         if (div != null)
         {
            data.Clear();

            div.GetAllAgents().ForEach(a =>
            {
               GridData bnd = new GridData()
               {
                  ID = a.id,
                  Agent = a.AgentName
               };

               if (dsBNOper.ContainsKey(a.id)) 
               {
                  bnd.Load(dsBNOper[a.id]);
               }

               data.Add(bnd);

            });

            grid.Sort(grid.Columns[0], ListSortDirection.Ascending);
         }
      }

      private void grid_KeyDown(object sender, KeyEventArgs e)
      {
         List<DataGridViewCell> cells = new List<DataGridViewCell>();

         foreach(DataGridViewCell cell in ((DataGridView)sender).SelectedCells)
            cells.Add(cell);

         cells.Reverse();

         if (e.KeyCode == Keys.C && e.Control)
         {
            if (cells.Count > 0)
            {
               StringBuilder sb = new StringBuilder();

               foreach (DataGridViewCell cell in cells)
               { 
                  if (sb.Length > 0)  sb.Append(";");
                  sb.Append(cell.Value != null ? cell.Value.ToString().Trim() : "");
               }

               if (sb.Length > 0)
               {
                  Clipboard.SetText(sb.ToString());
                  ((DataGridView)sender).ClearSelection();
               }
            }
         }else if (e.KeyCode == Keys.V && e.Control)
         {
            string[] array = Clipboard.GetText().Split(';');

            if (array.Length == 0) return;
            int idx = 0;

            foreach (DataGridViewCell cell in cells)
            {
               string val = idx < array.Length ? array[idx] : array[array.Length-1];
               GridData data = grid.Rows[cell.RowIndex].DataBoundItem as GridData;
               SetCellValue(cell.RowIndex, cell.ColumnIndex, val, data);
               idx++;
            }

            if (cells.Count > 0)
            {
               btnSave.Enabled = true;
               grid.Refresh();
            }
               
         }else if (e.KeyCode == Keys.Delete)
         {
            foreach (DataGridViewCell cell in cells)
            {
               GridData data = grid.Rows[cell.RowIndex].DataBoundItem as GridData;
               SetCellValue(cell.RowIndex, cell.ColumnIndex, "", data);
            }

            if (cells.Count > 0)
            {
               btnSave.Enabled = true;
               grid.Refresh();
            }
         }
      }

      private void grid_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         //if (grid.IsCurrentCellDirty)
         //{
         //   grid.CommitEdit(DataGridViewDataErrorContexts.Commit);

         //   textBox.Text = grid.CurrentCell.Value.ToString();
         //   textBox.BeginInvoke((MethodInvoker)delegate () { textBox.Select(1, 0); });
         //}
      }
   }
}
