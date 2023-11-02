namespace GRSoft.NapoleonManager
{
   partial class FmMerchView
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
         this.tabControl1 = new System.Windows.Forms.TabControl();
         this.folders = new System.Windows.Forms.TabPage();
         this.grid1 = new System.Windows.Forms.DataGridView();
         this.Column1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.items = new System.Windows.Forms.TabPage();
         this.grid2 = new System.Windows.Forms.DataGridView();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column5 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.tabControl1.SuspendLayout();
         this.folders.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.grid1)).BeginInit();
         this.items.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.grid2)).BeginInit();
         this.SuspendLayout();
         // 
         // tabControl1
         // 
         this.tabControl1.Controls.Add(this.folders);
         this.tabControl1.Controls.Add(this.items);
         this.tabControl1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tabControl1.Location = new System.Drawing.Point(0, 0);
         this.tabControl1.Name = "tabControl1";
         this.tabControl1.SelectedIndex = 0;
         this.tabControl1.Size = new System.Drawing.Size(970, 509);
         this.tabControl1.TabIndex = 0;
         // 
         // folders
         // 
         this.folders.Controls.Add(this.grid1);
         this.folders.Location = new System.Drawing.Point(4, 22);
         this.folders.Name = "folders";
         this.folders.Padding = new System.Windows.Forms.Padding(3);
         this.folders.Size = new System.Drawing.Size(962, 483);
         this.folders.TabIndex = 0;
         this.folders.Text = "Папки";
         this.folders.UseVisualStyleBackColor = true;
         // 
         // grid1
         // 
         this.grid1.AllowUserToAddRows = false;
         this.grid1.AllowUserToDeleteRows = false;
         this.grid1.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.grid1.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column1,
            this.Column2,
            this.Column3,
            this.Column4});
         this.grid1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.grid1.Location = new System.Drawing.Point(3, 3);
         this.grid1.Name = "grid1";
         this.grid1.RowHeadersVisible = false;
         this.grid1.Size = new System.Drawing.Size(956, 477);
         this.grid1.TabIndex = 0;
         // 
         // Column1
         // 
         this.Column1.DataPropertyName = "Pos";
         this.Column1.HeaderText = "№";
         this.Column1.Name = "Column1";
         this.Column1.Width = 40;
         // 
         // Column2
         // 
         this.Column2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column2.DataPropertyName = "Name";
         this.Column2.HeaderText = "Наименование";
         this.Column2.Name = "Column2";
         // 
         // Column3
         // 
         this.Column3.DataPropertyName = "Mine";
         this.Column3.HeaderText = "Свои";
         this.Column3.Name = "Column3";
         // 
         // Column4
         // 
         this.Column4.DataPropertyName = "Their";
         this.Column4.HeaderText = "Чужие";
         this.Column4.Name = "Column4";
         // 
         // items
         // 
         this.items.Controls.Add(this.grid2);
         this.items.Location = new System.Drawing.Point(4, 22);
         this.items.Name = "items";
         this.items.Padding = new System.Windows.Forms.Padding(3);
         this.items.Size = new System.Drawing.Size(962, 483);
         this.items.TabIndex = 1;
         this.items.Text = "Товары";
         this.items.UseVisualStyleBackColor = true;
         // 
         // grid2
         // 
         this.grid2.AllowUserToAddRows = false;
         this.grid2.AllowUserToDeleteRows = false;
         this.grid2.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.grid2.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dataGridViewTextBoxColumn1,
            this.dataGridViewTextBoxColumn2,
            this.Column5,
            this.dataGridViewTextBoxColumn4});
         this.grid2.Dock = System.Windows.Forms.DockStyle.Fill;
         this.grid2.Location = new System.Drawing.Point(3, 3);
         this.grid2.Name = "grid2";
         this.grid2.RowHeadersVisible = false;
         this.grid2.Size = new System.Drawing.Size(956, 477);
         this.grid2.TabIndex = 1;
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Pos";
         this.dataGridViewTextBoxColumn1.HeaderText = "№";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         this.dataGridViewTextBoxColumn1.Width = 40;
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn2.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn2.HeaderText = "Наименование";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         // 
         // Column5
         // 
         this.Column5.DataPropertyName = "System";
         this.Column5.HeaderText = "В системе";
         this.Column5.Name = "Column5";
         // 
         // dataGridViewTextBoxColumn4
         // 
         this.dataGridViewTextBoxColumn4.DataPropertyName = "Qty";
         this.dataGridViewTextBoxColumn4.HeaderText = "Кол-во";
         this.dataGridViewTextBoxColumn4.Name = "dataGridViewTextBoxColumn4";
         // 
         // FmMerchView
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.Controls.Add(this.tabControl1);
         this.Name = "FmMerchView";
         this.Size = new System.Drawing.Size(970, 509);
         this.tabControl1.ResumeLayout(false);
         this.folders.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.grid1)).EndInit();
         this.items.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.grid2)).EndInit();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.TabControl tabControl1;
      private System.Windows.Forms.TabPage folders;
      private System.Windows.Forms.TabPage items;
      private System.Windows.Forms.DataGridView grid1;
      private System.Windows.Forms.DataGridView grid2;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column1;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column2;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column3;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column4;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column5;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn4;
   }
}
