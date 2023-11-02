namespace GRSoft.NapoleonManager
{
   partial class RemnantsControl
   {
      /// <summary> 
      /// Required designer variable.
      /// </summary>
      private System.ComponentModel.IContainer components = null;

      /// <summary> 
      /// Clean up any resources being used.
      /// </summary>
      /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
      protected override void Dispose(bool disposing)
      {
         if (disposing && (components != null))
         {
            components.Dispose();
         }
         base.Dispose(disposing);
      }

      #region Component Designer generated code

      /// <summary> 
      /// Required method for Designer support - do not modify 
      /// the contents of this method with the code editor.
      /// </summary>
      private void InitializeComponent()
      {
         this.grid = new System.Windows.Forms.DataGridView();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn5 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn6 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn7 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column6 = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         ((System.ComponentModel.ISupportInitialize)(this.grid)).BeginInit();
         this.SuspendLayout();
         // 
         // grid
         // 
         this.grid.AllowUserToAddRows = false;
         this.grid.AllowUserToDeleteRows = false;
         this.grid.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.grid.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column1,
            this.Column6});
         this.grid.Cursor = System.Windows.Forms.Cursors.SizeNS;
         this.grid.Dock = System.Windows.Forms.DockStyle.Fill;
         this.grid.Location = new System.Drawing.Point(0, 0);
         this.grid.Margin = new System.Windows.Forms.Padding(4);
         this.grid.Name = "grid";
         this.grid.RowHeadersVisible = false;
         this.grid.RowHeadersWidth = 51;
         this.grid.Size = new System.Drawing.Size(1355, 646);
         this.grid.TabIndex = 0;
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn1.HeaderText = "Наименование";
         this.dataGridViewTextBoxColumn1.MinimumWidth = 6;
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.DataPropertyName = "Format";
         this.dataGridViewTextBoxColumn2.HeaderText = "Формат ТТ";
         this.dataGridViewTextBoxColumn2.MinimumWidth = 6;
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         this.dataGridViewTextBoxColumn2.Width = 125;
         // 
         // dataGridViewTextBoxColumn3
         // 
         this.dataGridViewTextBoxColumn3.DataPropertyName = "Qty";
         this.dataGridViewTextBoxColumn3.HeaderText = "Остатоки(кол-во)";
         this.dataGridViewTextBoxColumn3.MinimumWidth = 6;
         this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
         this.dataGridViewTextBoxColumn3.Width = 125;
         // 
         // dataGridViewTextBoxColumn4
         // 
         this.dataGridViewTextBoxColumn4.DataPropertyName = "Face";
         this.dataGridViewTextBoxColumn4.HeaderText = "Фейсы";
         this.dataGridViewTextBoxColumn4.MinimumWidth = 6;
         this.dataGridViewTextBoxColumn4.Name = "dataGridViewTextBoxColumn4";
         this.dataGridViewTextBoxColumn4.Width = 125;
         // 
         // dataGridViewTextBoxColumn5
         // 
         this.dataGridViewTextBoxColumn5.DataPropertyName = "Cost";
         this.dataGridViewTextBoxColumn5.HeaderText = "Цена на полке";
         this.dataGridViewTextBoxColumn5.MinimumWidth = 6;
         this.dataGridViewTextBoxColumn5.Name = "dataGridViewTextBoxColumn5";
         this.dataGridViewTextBoxColumn5.Width = 125;
         // 
         // dataGridViewTextBoxColumn6
         // 
         this.dataGridViewTextBoxColumn6.DataPropertyName = "Promo";
         this.dataGridViewTextBoxColumn6.HeaderText = "Отметка промо";
         this.dataGridViewTextBoxColumn6.MinimumWidth = 6;
         this.dataGridViewTextBoxColumn6.Name = "dataGridViewTextBoxColumn6";
         this.dataGridViewTextBoxColumn6.Width = 125;
         // 
         // dataGridViewTextBoxColumn7
         // 
         this.dataGridViewTextBoxColumn7.DataPropertyName = "OOS";
         this.dataGridViewTextBoxColumn7.HeaderText = "Причина ООS";
         this.dataGridViewTextBoxColumn7.MinimumWidth = 6;
         this.dataGridViewTextBoxColumn7.Name = "dataGridViewTextBoxColumn7";
         this.dataGridViewTextBoxColumn7.Width = 125;
         // 
         // Column1
         // 
         this.Column1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column1.DataPropertyName = "Name";
         this.Column1.HeaderText = "Наименование";
         this.Column1.MinimumWidth = 6;
         this.Column1.Name = "Column1";
         // 
         // Column6
         // 
         this.Column6.DataPropertyName = "Exists";
         this.Column6.FalseValue = "0";
         this.Column6.HeaderText = "Наличие";
         this.Column6.MinimumWidth = 6;
         this.Column6.Name = "Column6";
         this.Column6.TrueValue = "1";
         this.Column6.Width = 150;
         // 
         // RemnantsControl
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.Controls.Add(this.grid);
         this.Margin = new System.Windows.Forms.Padding(4);
         this.Name = "RemnantsControl";
         this.Size = new System.Drawing.Size(1355, 646);
         ((System.ComponentModel.ISupportInitialize)(this.grid)).EndInit();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.DataGridView grid;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn4;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn5;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn6;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn7;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column1;
      private System.Windows.Forms.DataGridViewCheckBoxColumn Column6;
   }
}
