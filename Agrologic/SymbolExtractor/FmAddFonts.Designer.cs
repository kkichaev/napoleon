namespace SymbolsExtractor
{
   partial class FmAddFonts
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

      #region Windows Form Designer generated code

      /// <summary>
      /// Required method for Designer support - do not modify
      /// the contents of this method with the code editor.
      /// </summary>
      private void InitializeComponent()
      {
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmAddFonts));
            this.toolStrip1 = new System.Windows.Forms.ToolStrip();
            this.toolStripButton1 = new System.Windows.Forms.ToolStripButton();
            this.toolStripButton2 = new System.Windows.Forms.ToolStripButton();
            this.toolStripButton3 = new System.Windows.Forms.ToolStripButton();
            this.toolStripButton4 = new System.Windows.Forms.ToolStripButton();
            this.label1 = new System.Windows.Forms.Label();
            this.tbCommonSymbols = new System.Windows.Forms.TextBox();
            this.dgvFonts = new System.Windows.Forms.DataGridView();
            this.Column1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.Column4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.Column2 = new System.Windows.Forms.DataGridViewCheckBoxColumn();
            this.Column3 = new System.Windows.Forms.DataGridViewCheckBoxColumn();
            this.clmnFIleName = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.tbFontSymbols = new System.Windows.Forms.TextBox();
            this.label2 = new System.Windows.Forms.Label();
            this.dgvAlcGraph = new System.Windows.Forms.DataGridView();
            this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.dataGridViewCheckBoxColumn1 = new System.Windows.Forms.DataGridViewCheckBoxColumn();
            this.dataGridViewCheckBoxColumn2 = new System.Windows.Forms.DataGridViewCheckBoxColumn();
            this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.toolStrip1.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dgvFonts)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.dgvAlcGraph)).BeginInit();
            this.SuspendLayout();
            // 
            // toolStrip1
            // 
            this.toolStrip1.ImageScalingSize = new System.Drawing.Size(32, 32);
            this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripButton1,
            this.toolStripButton2,
            this.toolStripButton3,
            this.toolStripButton4});
            this.toolStrip1.Location = new System.Drawing.Point(0, 0);
            this.toolStrip1.Name = "toolStrip1";
            this.toolStrip1.Size = new System.Drawing.Size(1009, 39);
            this.toolStrip1.TabIndex = 0;
            this.toolStrip1.Text = "toolStrip1";
            // 
            // toolStripButton1
            // 
            this.toolStripButton1.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            this.toolStripButton1.Image = ((System.Drawing.Image)(resources.GetObject("toolStripButton1.Image")));
            this.toolStripButton1.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.toolStripButton1.Name = "toolStripButton1";
            this.toolStripButton1.Size = new System.Drawing.Size(36, 36);
            this.toolStripButton1.Text = "Open";
            this.toolStripButton1.Click += new System.EventHandler(this.toolStripButton1_Click);
            // 
            // toolStripButton2
            // 
            this.toolStripButton2.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            this.toolStripButton2.Image = ((System.Drawing.Image)(resources.GetObject("toolStripButton2.Image")));
            this.toolStripButton2.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.toolStripButton2.Name = "toolStripButton2";
            this.toolStripButton2.Size = new System.Drawing.Size(36, 36);
            this.toolStripButton2.Text = "Save";
            this.toolStripButton2.Click += new System.EventHandler(this.toolStripButton2_Click);
            // 
            // toolStripButton3
            // 
            this.toolStripButton3.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            this.toolStripButton3.Image = ((System.Drawing.Image)(resources.GetObject("toolStripButton3.Image")));
            this.toolStripButton3.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.toolStripButton3.Name = "toolStripButton3";
            this.toolStripButton3.Size = new System.Drawing.Size(36, 36);
            this.toolStripButton3.Text = "Save as";
            this.toolStripButton3.Click += new System.EventHandler(this.toolStripButton3_Click);
            // 
            // toolStripButton4
            // 
            this.toolStripButton4.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
            this.toolStripButton4.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            this.toolStripButton4.Image = ((System.Drawing.Image)(resources.GetObject("toolStripButton4.Image")));
            this.toolStripButton4.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.toolStripButton4.Margin = new System.Windows.Forms.Padding(0, 1, 10, 2);
            this.toolStripButton4.Name = "toolStripButton4";
            this.toolStripButton4.Size = new System.Drawing.Size(36, 36);
            this.toolStripButton4.Text = "Cancel";
            this.toolStripButton4.Click += new System.EventHandler(this.toolStripButton4_Click);
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(19, 51);
            this.label1.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(122, 17);
            this.label1.TabIndex = 1;
            this.label1.Text = "Common symbols:";
            // 
            // tbCommonSymbols
            // 
            this.tbCommonSymbols.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.tbCommonSymbols.Location = new System.Drawing.Point(19, 71);
            this.tbCommonSymbols.Margin = new System.Windows.Forms.Padding(4);
            this.tbCommonSymbols.Multiline = true;
            this.tbCommonSymbols.Name = "tbCommonSymbols";
            this.tbCommonSymbols.Size = new System.Drawing.Size(972, 61);
            this.tbCommonSymbols.TabIndex = 2;
            this.tbCommonSymbols.TextChanged += new System.EventHandler(this.tbCommonSymbols_TextChanged);
            // 
            // dgvFonts
            // 
            this.dgvFonts.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.dgvFonts.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dgvFonts.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column1,
            this.Column4,
            this.Column2,
            this.Column3,
            this.clmnFIleName});
            this.dgvFonts.EditMode = System.Windows.Forms.DataGridViewEditMode.EditOnEnter;
            this.dgvFonts.Location = new System.Drawing.Point(20, 404);
            this.dgvFonts.Margin = new System.Windows.Forms.Padding(4);
            this.dgvFonts.Name = "dgvFonts";
            this.dgvFonts.Size = new System.Drawing.Size(973, 234);
            this.dgvFonts.TabIndex = 5;
            this.dgvFonts.CellDoubleClick += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvFonts_CellDoubleClick);
            this.dgvFonts.CellValueChanged += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvFonts_CellValueChanged);
            this.dgvFonts.CurrentCellDirtyStateChanged += new System.EventHandler(this.dgvFonts_CurrentCellDirtyStateChanged);
            // 
            // Column1
            // 
            this.Column1.DataPropertyName = "FontName";
            this.Column1.FillWeight = 200F;
            this.Column1.HeaderText = "Font";
            this.Column1.Name = "Column1";
            this.Column1.Width = 150;
            // 
            // Column4
            // 
            this.Column4.DataPropertyName = "Size";
            this.Column4.HeaderText = "Size";
            this.Column4.Name = "Column4";
            this.Column4.Width = 70;
            // 
            // Column2
            // 
            this.Column2.DataPropertyName = "Bold";
            this.Column2.HeaderText = "Bold";
            this.Column2.Name = "Column2";
            this.Column2.Width = 40;
            // 
            // Column3
            // 
            this.Column3.DataPropertyName = "Italic";
            this.Column3.HeaderText = "Italic";
            this.Column3.Name = "Column3";
            this.Column3.Width = 40;
            // 
            // clmnFIleName
            // 
            this.clmnFIleName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
            this.clmnFIleName.DataPropertyName = "FontFile";
            this.clmnFIleName.HeaderText = "File name";
            this.clmnFIleName.Name = "clmnFIleName";
            this.clmnFIleName.ReadOnly = true;
            // 
            // tbFontSymbols
            // 
            this.tbFontSymbols.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.tbFontSymbols.Location = new System.Drawing.Point(17, 164);
            this.tbFontSymbols.Margin = new System.Windows.Forms.Padding(4);
            this.tbFontSymbols.Multiline = true;
            this.tbFontSymbols.Name = "tbFontSymbols";
            this.tbFontSymbols.Size = new System.Drawing.Size(972, 61);
            this.tbFontSymbols.TabIndex = 7;
            this.tbFontSymbols.TextChanged += new System.EventHandler(this.textBox1_TextChanged);
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(17, 145);
            this.label2.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(143, 17);
            this.label2.TabIndex = 6;
            this.label2.Text = "Added fonts symbols:";
            // 
            // dgvAlcGraph
            // 
            this.dgvAlcGraph.AllowUserToAddRows = false;
            this.dgvAlcGraph.AllowUserToDeleteRows = false;
            this.dgvAlcGraph.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.dgvAlcGraph.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dgvAlcGraph.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dataGridViewTextBoxColumn1,
            this.dataGridViewTextBoxColumn2,
            this.dataGridViewCheckBoxColumn1,
            this.dataGridViewCheckBoxColumn2,
            this.dataGridViewTextBoxColumn3});
            this.dgvAlcGraph.EditMode = System.Windows.Forms.DataGridViewEditMode.EditOnEnter;
            this.dgvAlcGraph.Location = new System.Drawing.Point(16, 244);
            this.dgvAlcGraph.Margin = new System.Windows.Forms.Padding(4);
            this.dgvAlcGraph.Name = "dgvAlcGraph";
            this.dgvAlcGraph.RowHeadersVisible = false;
            this.dgvAlcGraph.Size = new System.Drawing.Size(973, 137);
            this.dgvAlcGraph.TabIndex = 8;
            this.dgvAlcGraph.CellMouseDoubleClick += new System.Windows.Forms.DataGridViewCellMouseEventHandler(this.dgvAlcGraph_CellMouseDoubleClick);
            // 
            // dataGridViewTextBoxColumn1
            // 
            this.dataGridViewTextBoxColumn1.DataPropertyName = "FontName";
            this.dataGridViewTextBoxColumn1.FillWeight = 200F;
            this.dataGridViewTextBoxColumn1.HeaderText = "AlcGraph";
            this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
            this.dataGridViewTextBoxColumn1.ReadOnly = true;
            this.dataGridViewTextBoxColumn1.Width = 150;
            // 
            // dataGridViewTextBoxColumn2
            // 
            this.dataGridViewTextBoxColumn2.DataPropertyName = "Size";
            this.dataGridViewTextBoxColumn2.HeaderText = "Size";
            this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
            this.dataGridViewTextBoxColumn2.Width = 70;
            // 
            // dataGridViewCheckBoxColumn1
            // 
            this.dataGridViewCheckBoxColumn1.DataPropertyName = "Bold";
            this.dataGridViewCheckBoxColumn1.HeaderText = "Bold";
            this.dataGridViewCheckBoxColumn1.Name = "dataGridViewCheckBoxColumn1";
            this.dataGridViewCheckBoxColumn1.Width = 40;
            // 
            // dataGridViewCheckBoxColumn2
            // 
            this.dataGridViewCheckBoxColumn2.DataPropertyName = "Italic";
            this.dataGridViewCheckBoxColumn2.HeaderText = "Italic";
            this.dataGridViewCheckBoxColumn2.Name = "dataGridViewCheckBoxColumn2";
            this.dataGridViewCheckBoxColumn2.Width = 40;
            // 
            // dataGridViewTextBoxColumn3
            // 
            this.dataGridViewTextBoxColumn3.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
            this.dataGridViewTextBoxColumn3.DataPropertyName = "FontFile";
            this.dataGridViewTextBoxColumn3.HeaderText = "File name";
            this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
            this.dataGridViewTextBoxColumn3.ReadOnly = true;
            // 
            // FmAddFonts
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1009, 658);
            this.Controls.Add(this.dgvAlcGraph);
            this.Controls.Add(this.tbFontSymbols);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.dgvFonts);
            this.Controls.Add(this.toolStrip1);
            this.Controls.Add(this.tbCommonSymbols);
            this.Controls.Add(this.label1);
            this.Margin = new System.Windows.Forms.Padding(4);
            this.Name = "FmAddFonts";
            this.Text = "Additional fonts and symbols";
            this.toolStrip1.ResumeLayout(false);
            this.toolStrip1.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dgvFonts)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.dgvAlcGraph)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton toolStripButton1;
      private System.Windows.Forms.ToolStripButton toolStripButton2;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.TextBox tbCommonSymbols;
      private System.Windows.Forms.DataGridView dgvFonts;
      private System.Windows.Forms.TextBox tbFontSymbols;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.ToolStripButton toolStripButton3;
      private System.Windows.Forms.ToolStripButton toolStripButton4;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column1;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column4;
      private System.Windows.Forms.DataGridViewCheckBoxColumn Column2;
      private System.Windows.Forms.DataGridViewCheckBoxColumn Column3;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnFIleName;
        private System.Windows.Forms.DataGridView dgvAlcGraph;
        private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
        private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
        private System.Windows.Forms.DataGridViewCheckBoxColumn dataGridViewCheckBoxColumn1;
        private System.Windows.Forms.DataGridViewCheckBoxColumn dataGridViewCheckBoxColumn2;
        private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
    }
}