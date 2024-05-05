using System;
using System.Collections.Generic;
using System.IO;
using System.Net.NetworkInformation;
using System.Windows.Forms;
using System.Xml;
using ExcelLibrary.SpreadSheet;
using GRSoft.Network;

namespace GRSoft.NapoleonAdmin
{
   public class MainFormEx : MainForm
   {
      DataGridViewCheckBoxColumn clmnDisableSave = new DataGridViewCheckBoxColumn();
      DataGridViewCheckBoxColumn clmnAllowCopy = new DataGridViewCheckBoxColumn();
      DataGridViewCheckBoxColumn clmnAllowDelete = new DataGridViewCheckBoxColumn();
      DataGridViewCheckBoxColumn clmnAllowLookPhoto = new DataGridViewCheckBoxColumn();

      DataGridViewTextBoxColumn clmnKISId = new System.Windows.Forms.DataGridViewTextBoxColumn();
      DataGridViewCheckBoxColumn[] rightColumns;
      ToolStripButton btn;

      public MainFormEx()
      {
         dgvSyncInfo.Columns["Column2"].Visible = false;
         dgvSyncInfo.Columns["Column3"].AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         usersView.RowHeadersVisible = true;
         clmnDisableSave.DataPropertyName = "DisableSave";
         clmnDisableSave.HeaderText = "Запрет редактирования";
         clmnDisableSave.Name = "clmnCanDisableFirms";
         clmnDisableSave.Visible = false;
         clmnDisableSave.Width = 90;
         clmnDisableSave.SortMode = DataGridViewColumnSortMode.Programmatic;

         clmnAllowCopy.DataPropertyName = "AllowCopy";
         clmnAllowCopy.HeaderText = "Запрет дубль в 1С";
         clmnAllowCopy.Name = "clmnAllowCopy";
         clmnAllowCopy.Visible = false;
         clmnAllowCopy.Width = 90;
         clmnAllowCopy.SortMode = DataGridViewColumnSortMode.Programmatic;

         clmnAllowDelete.DataPropertyName = "AllowDelete";
         clmnAllowDelete.HeaderText = "Запрет удаления";
         clmnAllowDelete.Name = "clmnAllowDelete";
         clmnAllowDelete.Visible = false;
         clmnAllowDelete.Width = 90;
         clmnAllowDelete.SortMode = DataGridViewColumnSortMode.Programmatic;

         clmnAllowLookPhoto.DataPropertyName = "AllowLookPhoto";
         clmnAllowLookPhoto.HeaderText = "Запрет просмотра";
         clmnAllowLookPhoto.Name = "clmnAllowLookPhoto";
         clmnAllowLookPhoto.Visible = false;
         clmnAllowLookPhoto.Width = 90;
         clmnAllowLookPhoto.SortMode = DataGridViewColumnSortMode.Programmatic;

         // 
         // clmnId
         // 
         clmnKISId.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmnKISId.DataPropertyName = "KISID";
         clmnKISId.HeaderText = "Код 1с";
         clmnKISId.Name = "KISID";
         clmnKISId.Width = 150;

         rightColumns = new DataGridViewCheckBoxColumn[] 
         { 
               clmnDisableSave, 
               clmnAllowCopy,
               clmnAllowDelete,
               clmnAllowLookPhoto
         };
         usersView.Columns.AddRange(rightColumns);
         usersView.Columns.Insert(1, clmnKISId);

         btn = new ToolStripButton();
         btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btn.Image = global::GRSoft.NapoleonAdmin.Properties.Resources.excel;
         btn.ImageTransparentColor = System.Drawing.Color.Magenta;
         btn.Name = "excel";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "Экспорт в Excel";
         btn.Click += new System.EventHandler(Excel_Click);

         toolStrip1.Items.Add(btn);
         Size = new System.Drawing.Size(1500, 700);
      }

      private void Excel_Click(object sender, EventArgs e)
      {
         using (SaveFileDialog openFileDialog = new SaveFileDialog())
         {
            openFileDialog.Filter = "Excel files (*.xls)|*.xls|All files (*.*)|*.*";
            openFileDialog.RestoreDirectory = true;

            if (openFileDialog.ShowDialog() == DialogResult.OK)
            {
               String filePath = openFileDialog.FileName;

               try
               {
                  Workbook wb = new Workbook();
                  Worksheet ws = new Worksheet("Page 1");

                  for (int i = 0; i < 100; i++)      //ms office doesn't support less then 100 cells
                     ws.Cells[i, 0] = new Cell("");

                  ws.Cells[0, 0] = new Cell("ID");
                  ws.Cells[0, 1] = new Cell("Код 1С");
                  ws.Cells[0, 2] = new Cell("Пользователь");
                  ws.Cells[0, 3] = new Cell("Логин");
                  ws.Cells[0, 4] = new Cell("Пароль");
                  ws.Cells[0, 5] = new Cell("Последний доступ");
                  ws.Cells[0, 6] = new Cell("Версия");

                  int row = 1;

                  foreach(UserDataItem i in userData)
                  {
                     UserDataItemEx ie = (UserDataItemEx)i;

                     ws.Cells[row, 0] = new Cell(ie.Id);
                     ws.Cells[row, 1] = new Cell(ie.KISID);
                     ws.Cells[row, 2] = new Cell(ie.Name);
                     ws.Cells[row, 3] = new Cell(ie.Login);
                     ws.Cells[row, 4] = new Cell(ie.Passw);
                     ws.Cells[row, 5] = new Cell(ie.LastAccess.ToString());
                     ws.Cells[row, 6] = new Cell(ie.Version);

                     row++;
                  }

                  wb.Worksheets.Add(ws);
                  ws.Cells.ColumnWidth[0] = 5000;
                  ws.Cells.ColumnWidth[1] = 5000;
                  ws.Cells.ColumnWidth[2] = 10000;
                  ws.Cells.ColumnWidth[3] = 5000;
                  ws.Cells.ColumnWidth[4] = 5000;
                  ws.Cells.ColumnWidth[5] = 5000;
                  ws.Cells.ColumnWidth[6] = 5000;


                  wb.Save(filePath);


               }
               catch (Exception ex)
               {
                  MessageBox.Show(ex.Message);
               }
            }
         }
      }

      protected override void SetStatusText()
      {
         base.SetStatusText();

         if (btn != null) 
            btn.Visible = cbUserType.SelectedIndex == 0;
      }
      protected override void usersView_CurrentCellDirtyStateChanged(object sender, System.EventArgs e)
      {
         base.usersView_CurrentCellDirtyStateChanged(sender, e);
         foreach (DataGridViewCheckBoxColumn c in rightColumns)
            if (usersView.CurrentCell.ColumnIndex == c.DisplayIndex)
            {
               usersView.CommitEdit(DataGridViewDataErrorContexts.Commit);
               break;
            }
      }

      protected override void PrepareViewComponents(bool agentView)
      {
         base.PrepareViewComponents(agentView);

         bool visible = !agentView;
         clmnDisableSave.Visible = visible;
         clmnAllowCopy.Visible = visible;
         clmnAllowLookPhoto.Visible = visible;
         clmnAllowDelete.Visible = visible;  
         clmnKISId.Visible = agentView;
      }

   }
}