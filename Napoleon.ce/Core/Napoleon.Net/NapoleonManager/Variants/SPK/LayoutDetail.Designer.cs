namespace GRSoft.NapoleonManager
{
   partial class LayoutDetail
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
         this.btnSave = new System.Windows.Forms.Button();
         this.cbType = new System.Windows.Forms.ComboBox();
         this.cbRemark = new System.Windows.Forms.ComboBox();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnChanged = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         ((System.ComponentModel.ISupportInitialize)(this.grid)).BeginInit();
         this.SuspendLayout();
         // 
         // grid
         // 
         this.grid.AllowUserToAddRows = false;
         this.grid.AllowUserToDeleteRows = false;
         this.grid.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.grid.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.grid.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column1,
            this.Column2,
            this.clmnChanged,
            this.Column3});
         this.grid.EditMode = System.Windows.Forms.DataGridViewEditMode.EditOnEnter;
         this.grid.Location = new System.Drawing.Point(0, 32);
         this.grid.Name = "grid";
         this.grid.RowHeadersVisible = false;
         this.grid.Size = new System.Drawing.Size(658, 247);
         this.grid.TabIndex = 0;
         this.grid.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.grid_CellFormatting);
         // 
         // btnSave
         // 
         this.btnSave.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(224)))), ((int)(((byte)(224)))), ((int)(((byte)(224)))));
         this.btnSave.Location = new System.Drawing.Point(0, 4);
         this.btnSave.Name = "btnSave";
         this.btnSave.Size = new System.Drawing.Size(135, 21);
         this.btnSave.TabIndex = 1;
         this.btnSave.Text = "Записать";
         this.btnSave.UseVisualStyleBackColor = false;
         this.btnSave.Click += new System.EventHandler(this.btnSave_Click);
         // 
         // cbType
         // 
         this.cbType.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbType.FormattingEnabled = true;
         this.cbType.Items.AddRange(new object[] {
            "Утвердить (без изменений)",
            "Утвердить с комментарием",
            "Забраковано"});
         this.cbType.Location = new System.Drawing.Point(141, 4);
         this.cbType.Name = "cbType";
         this.cbType.Size = new System.Drawing.Size(173, 21);
         this.cbType.TabIndex = 2;
         this.cbType.SelectedIndexChanged += new System.EventHandler(this.cbType_SelectedIndexChanged);
         // 
         // cbRemark
         // 
         this.cbRemark.FormattingEnabled = true;
         this.cbRemark.Location = new System.Drawing.Point(320, 5);
         this.cbRemark.Name = "cbRemark";
         this.cbRemark.Size = new System.Drawing.Size(338, 21);
         this.cbRemark.TabIndex = 3;
         this.cbRemark.TextChanged += new System.EventHandler(this.cbRemark_TextChanged);
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn1.FillWeight = 80F;
         this.dataGridViewTextBoxColumn1.HeaderText = "Наименование";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn2.DataPropertyName = "Qty";
         this.dataGridViewTextBoxColumn2.FillWeight = 20F;
         this.dataGridViewTextBoxColumn2.HeaderText = "Количество";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         // 
         // dataGridViewTextBoxColumn3
         // 
         this.dataGridViewTextBoxColumn3.DataPropertyName = "Changed";
         this.dataGridViewTextBoxColumn3.HeaderText = "Изменено";
         this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
         // 
         // Column1
         // 
         this.Column1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column1.DataPropertyName = "Name";
         this.Column1.FillWeight = 80F;
         this.Column1.HeaderText = "Наименование";
         this.Column1.Name = "Column1";
         // 
         // Column2
         // 
         this.Column2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column2.DataPropertyName = "Qty";
         this.Column2.FillWeight = 20F;
         this.Column2.HeaderText = "Количество";
         this.Column2.Name = "Column2";
         // 
         // clmnChanged
         // 
         this.clmnChanged.DataPropertyName = "Changed";
         this.clmnChanged.HeaderText = "Изменено";
         this.clmnChanged.Name = "clmnChanged";
         // 
         // Column3
         // 
         this.Column3.DataPropertyName = "Cause";
         this.Column3.HeaderText = "Причина отсутствия";
         this.Column3.Name = "Column3";
         // 
         // LayoutDetail
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.Controls.Add(this.grid);
         this.Controls.Add(this.cbRemark);
         this.Controls.Add(this.cbType);
         this.Controls.Add(this.btnSave);
         this.Name = "LayoutDetail";
         this.Size = new System.Drawing.Size(658, 279);
         ((System.ComponentModel.ISupportInitialize)(this.grid)).EndInit();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.DataGridView grid;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.Button btnSave;
      private System.Windows.Forms.ComboBox cbType;
      private System.Windows.Forms.ComboBox cbRemark;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column1;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column2;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnChanged;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column3;
   }
}
