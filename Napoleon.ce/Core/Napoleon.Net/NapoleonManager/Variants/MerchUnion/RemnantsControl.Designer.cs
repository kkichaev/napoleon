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
         this.Column2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column5 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column6 = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         this.Column7 = new System.Windows.Forms.DataGridViewTextBoxColumn();
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
            this.Column2,
            this.Column3,
            this.Column4,
            this.Column5,
            this.Column6,
            this.Column7});
         this.grid.Cursor = System.Windows.Forms.Cursors.SizeNS;
         this.grid.Dock = System.Windows.Forms.DockStyle.Fill;
         this.grid.Location = new System.Drawing.Point(0, 0);
         this.grid.Name = "grid";
         this.grid.RowHeadersVisible = false;
         this.grid.Size = new System.Drawing.Size(1016, 525);
         this.grid.TabIndex = 0;
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn1.HeaderText = "Наименование";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.DataPropertyName = "Format";
         this.dataGridViewTextBoxColumn2.HeaderText = "Формат ТТ";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         // 
         // dataGridViewTextBoxColumn3
         // 
         this.dataGridViewTextBoxColumn3.DataPropertyName = "Qty";
         this.dataGridViewTextBoxColumn3.HeaderText = "Остатоки(кол-во)";
         this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
         // 
         // dataGridViewTextBoxColumn4
         // 
         this.dataGridViewTextBoxColumn4.DataPropertyName = "Face";
         this.dataGridViewTextBoxColumn4.HeaderText = "Фейсы";
         this.dataGridViewTextBoxColumn4.Name = "dataGridViewTextBoxColumn4";
         // 
         // dataGridViewTextBoxColumn5
         // 
         this.dataGridViewTextBoxColumn5.DataPropertyName = "Cost";
         this.dataGridViewTextBoxColumn5.HeaderText = "Цена на полке";
         this.dataGridViewTextBoxColumn5.Name = "dataGridViewTextBoxColumn5";
         // 
         // dataGridViewTextBoxColumn6
         // 
         this.dataGridViewTextBoxColumn6.DataPropertyName = "Promo";
         this.dataGridViewTextBoxColumn6.HeaderText = "Отметка промо";
         this.dataGridViewTextBoxColumn6.Name = "dataGridViewTextBoxColumn6";
         // 
         // dataGridViewTextBoxColumn7
         // 
         this.dataGridViewTextBoxColumn7.DataPropertyName = "OOS";
         this.dataGridViewTextBoxColumn7.HeaderText = "Причина ООS";
         this.dataGridViewTextBoxColumn7.Name = "dataGridViewTextBoxColumn7";
         // 
         // Column1
         // 
         this.Column1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column1.DataPropertyName = "Item";
         this.Column1.HeaderText = "Наименование";
         this.Column1.Name = "Column1";
         // 
         // Column2
         // 
         this.Column2.DataPropertyName = "Format";
         this.Column2.HeaderText = "Формат ТТ";
         this.Column2.Name = "Column2";
         this.Column2.Width = 50;
         // 
         // Column3
         // 
         this.Column3.DataPropertyName = "Qty";
         this.Column3.HeaderText = "Остатоки(кол-во)";
         this.Column3.Name = "Column3";
         this.Column3.Width = 50;
         // 
         // Column4
         // 
         this.Column4.DataPropertyName = "Face";
         this.Column4.HeaderText = "Фейсы";
         this.Column4.Name = "Column4";
         this.Column4.Width = 50;
         // 
         // Column5
         // 
         this.Column5.DataPropertyName = "Cost";
         this.Column5.HeaderText = "Цена на полке";
         this.Column5.Name = "Column5";
         this.Column5.Width = 50;
         // 
         // Column6
         // 
         this.Column6.DataPropertyName = "Promo";
         this.Column6.FalseValue = "0";
         this.Column6.HeaderText = "Отметка промо";
         this.Column6.Name = "Column6";
         this.Column6.TrueValue = "1";
         this.Column6.Width = 50;
         // 
         // Column7
         // 
         this.Column7.DataPropertyName = "OOS";
         this.Column7.HeaderText = "Причина ООS";
         this.Column7.Name = "Column7";
         this.Column7.Width = 50;
         // 
         // RemnantsControl
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.Controls.Add(this.grid);
         this.Name = "RemnantsControl";
         this.Size = new System.Drawing.Size(1016, 525);
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
      private System.Windows.Forms.DataGridViewTextBoxColumn Column2;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column3;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column4;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column5;
      private System.Windows.Forms.DataGridViewCheckBoxColumn Column6;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column7;
   }
}
