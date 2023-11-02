namespace GRSoft.NapoleonManager
{
   partial class FmDistribView
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
         this.label1 = new System.Windows.Forms.Label();
         this.grid = new System.Windows.Forms.DataGridView();
         this.Column1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column4 = new System.Windows.Forms.DataGridViewImageColumn();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         ((System.ComponentModel.ISupportInitialize)(this.grid)).BeginInit();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.label1.Location = new System.Drawing.Point(0, 0);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(701, 378);
         this.label1.TabIndex = 0;
         // 
         // grid
         // 
         this.grid.AllowUserToAddRows = false;
         this.grid.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.grid.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column1,
            this.Column2,
            this.Column3,
            this.Column4});
         this.grid.Dock = System.Windows.Forms.DockStyle.Fill;
         this.grid.Location = new System.Drawing.Point(0, 0);
         this.grid.Name = "grid";
         this.grid.ReadOnly = true;
         this.grid.RowHeadersVisible = false;
         this.grid.Size = new System.Drawing.Size(701, 378);
         this.grid.TabIndex = 1;
         this.grid.CellContentClick += new System.Windows.Forms.DataGridViewCellEventHandler(this.grid_CellContentClick);
         this.grid.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.grid_CellFormatting);
         // 
         // Column1
         // 
         this.Column1.DataPropertyName = "Pos";
         this.Column1.HeaderText = "№";
         this.Column1.Name = "Column1";
         this.Column1.ReadOnly = true;
         this.Column1.Width = 30;
         // 
         // Column2
         // 
         this.Column2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column2.DataPropertyName = "Name";
         this.Column2.HeaderText = "Наименование";
         this.Column2.Name = "Column2";
         this.Column2.ReadOnly = true;
         // 
         // Column3
         // 
         this.Column3.DataPropertyName = "Remark";
         this.Column3.HeaderText = "Причина отсутствия";
         this.Column3.Name = "Column3";
         this.Column3.ReadOnly = true;
         // 
         // Column4
         // 
         this.Column4.DataPropertyName = "Photo";
         this.Column4.HeaderText = "Фото";
         this.Column4.ImageLayout = System.Windows.Forms.DataGridViewImageCellLayout.Zoom;
         this.Column4.Name = "Column4";
         this.Column4.ReadOnly = true;
         this.Column4.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Automatic;
         this.Column4.Width = 80;
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Pos";
         this.dataGridViewTextBoxColumn1.HeaderText = "№";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         this.dataGridViewTextBoxColumn1.ReadOnly = true;
         this.dataGridViewTextBoxColumn1.Width = 30;
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn2.HeaderText = "Наименование";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         this.dataGridViewTextBoxColumn2.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn3
         // 
         this.dataGridViewTextBoxColumn3.HeaderText = "Причина отсутствия";
         this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
         this.dataGridViewTextBoxColumn3.ReadOnly = true;
         // 
         // FmDistribView
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.Controls.Add(this.grid);
         this.Controls.Add(this.label1);
         this.Name = "FmDistribView";
         this.Size = new System.Drawing.Size(701, 378);
         ((System.ComponentModel.ISupportInitialize)(this.grid)).EndInit();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.DataGridView grid;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column1;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column2;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column3;
      private System.Windows.Forms.DataGridViewImageColumn Column4;
   }
}
